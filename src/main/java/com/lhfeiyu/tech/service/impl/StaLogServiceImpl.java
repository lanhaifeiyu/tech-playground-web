package com.lhfeiyu.tech.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zom.statistics.DTO.*;
import com.zom.statistics.DTO.OrgLog.*;
import com.zom.statistics.dao.mapper.common.GroupMapper;
import com.zom.statistics.dao.mapper.common.RtvUnitMapper;
import com.zom.statistics.dao.mapper.common.UserMapper;
import com.zom.statistics.dao.mapper.logMapper.*;
import com.zom.statistics.dao.po.*;
import com.zom.statistics.service.IStaLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Deprecated
@Service
public class StaLogServiceImpl implements IStaLogService {

    @Autowired
    private StaOriginalLogMapper staOriginalLogMapper;
    @Autowired
    private StaParsePositionMapper staParsePositionMapper;
    @Autowired
    private StaDayDeptMapper staDayDeptMapper;
    @Autowired
    private StaUserLogonRecordOriginMapper staUserLogonRecordOriginMapper;
    @Autowired
    private StaUserAudioRecordOriginMapper staUserAudioRecordOriginMapper;
    @Autowired
    private StaUserImRecordMapper staUserImRecordMapper;
    @Autowired
    private StaUserMileageRecordMapper staUserMileageRecordMapper;
    @Autowired
    private StaUserOnlineRecordMapper staUserOnlineRecordMapper;
    @Autowired
    private StaUserPhotoRecordMapper staUserPhotoRecordMapper;
    @Autowired
    private StaUserSosRecordOriginMapper staUserSosRecordOriginMapper;
    @Autowired
    private StaUserTmpgroupRecordMapper staUserTmpgroupRecordMapper;
    @Autowired
    private StaUserVideoRecordOriginMapper staUserVideoRecordOriginMapper;
    @Autowired
    private StaUserAudioRecordMapper staUserAudioRecordMapper;
    @Autowired
    private StaUserVideoRecordMapper staUserVideoRecordMapper;
    @Autowired
    private StaUserSosRecordMapper staUserSosRecordMapper;
    @Autowired
    private StaDayUserMapper staDayUserMapper;
    @Autowired
    private RtvUnitMapper rtvUnitMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GroupMapper groupMapper;

    private static final ThreadLocal<DateFormat> ym = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMM");
        }
    };

    private static final ThreadLocal<DateFormat> ymd = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> ymdh = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHH");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> y_m_d = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private static Logger logger = LoggerFactory.getLogger(StaLogServiceImpl.class);

    @Override
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public List<StaUserLogonRecordOrigin> parseLog() {
        List<StaUserLogonRecordOrigin> userLogoff = new ArrayList<>();
        // TODO 从sta_original_log中加载原始日志，通过id判断业务类型，获取对应数据，组装数据，更新对应统计表
        // sta_original_log_flag是记录上次解析到哪里
        // 根据业务类型获取数据，并查询数据库，组装出所有字段
        // 更新统计表需要更新对应记录表和用户，部门的统计表，部门包括所有的上级链部门
        StaParsePosition orgPosition = staParsePositionMapper.selectByTableName("sta_original_log");
        if (orgPosition == null) {
//            return;
        }
        List<StaOriginalLog> contentList = staOriginalLogMapper.selectByStartId(orgPosition.getLastPkId());
        if (contentList.size() == 0) {
            return null;
        }
        List<Long> allIds = new ArrayList<>();// 存放所有的用户id

        List<OrgUserLogin> list1 = new ArrayList<>();// 用户登录
        List<OrgUserLoginOut> list2 = new ArrayList<>();// 用户退出
        List<OrgIniKeyAlarm> list3 = new ArrayList<>();// 发起一键报警
        List<OrgCancelKeyAlarm> list4 = new ArrayList<>();// 取消一键报警
        List<OrgDealKeyAlarm> list5 = new ArrayList<>();// 处理一键报警
        List<OrgVoiceCall> list6 = new ArrayList<>();// 语音通话
        List<OrgVideoCall> list7 = new ArrayList<>();// 视频通话
        List<OrgVideoBack> list8 = new ArrayList<>();// 视频回传
        List<OrgVideoCallTheRoll> list9 = new ArrayList<>();// OrgDealKeyAlarm
        List<OrgVideoConsultation> list10 = new ArrayList<>();// 视频会商
        List<OrgPhotoBack> list11 = new ArrayList<>();// 照片回传
        List<OrgCreateTemporaryGroup> list12 = new ArrayList<>();// 创建临时组
        List<OrgInsMessage> list13 = new ArrayList<>();// 即时消息
        List<OrgMileage> list14 = new ArrayList<>();// 里程数更新
        List<Long> finalAllIds = allIds;
        contentList.forEach(v -> {
            JSONObject json = JSONObject.parseObject(v.getContent());
            switch ((Integer) json.get("id")) {
                case 1000:
                    OrgUserLogin u1 = JSON.toJavaObject(json, OrgUserLogin.class);
                    finalAllIds.add(u1.getUid());
                    list1.add(u1);
                    break;
                case 1001:
                    OrgUserLoginOut u2 = JSON.toJavaObject(json, OrgUserLoginOut.class);
                    finalAllIds.add(u2.getUid());
                    list2.add(u2);
                    break;
                case 1002:
                    OrgIniKeyAlarm u3 = JSON.toJavaObject(json, OrgIniKeyAlarm.class);
                    finalAllIds.add(u3.getUid());
                    list3.add(u3);
                    break;
                case 1003:
                    OrgCancelKeyAlarm u4 = JSON.toJavaObject(json, OrgCancelKeyAlarm.class);
                    finalAllIds.add(u4.getUid());
                    list4.add(u4);
                    break;
                case 1004:
                    OrgDealKeyAlarm u5 = JSON.toJavaObject(json, OrgDealKeyAlarm.class);
                    finalAllIds.add(u5.getUid());
                    list5.add(u5);
                    break;
                case 1100:
                    OrgVoiceCall u6 = JSON.toJavaObject(json, OrgVoiceCall.class);
                    finalAllIds.add(u6.getUid());
                    list6.add(u6);
                    break;
                case 1200:
                    if (json.get("sub_id") == null) {
                        break;
                    }
                    switch ((Integer) json.get("sub_id")) {
                        case 1201:
                            OrgVideoCall u7 = JSON.toJavaObject(json, OrgVideoCall.class);
                            finalAllIds.add(u7.getUid());
                            list7.add(u7);
                            break;
                        case 1202:
                            OrgVideoBack u8 = JSON.toJavaObject(json, OrgVideoBack.class);
                            finalAllIds.add(u8.getUid());
                            list8.add(u8);
                            break;
                        case 1203:
                            OrgVideoCallTheRoll u9 = JSON.toJavaObject(json, OrgVideoCallTheRoll.class);
                            finalAllIds.add(u9.getUid());
                            list9.add(u9);
                            break;
                        case 1204:
                            OrgVideoConsultation u10 = JSON.toJavaObject(json, OrgVideoConsultation.class);
                            finalAllIds.add(u10.getUid());
                            list10.add(u10);
                            break;
                    }
                    break;
                case 1300:
                    OrgPhotoBack u11 = JSON.toJavaObject(json, OrgPhotoBack.class);
                    finalAllIds.add(u11.getUid());
                    list11.add(u11);
                    break;
                case 1400:
                    OrgCreateTemporaryGroup u12 = JSON.toJavaObject(json, OrgCreateTemporaryGroup.class);
                    finalAllIds.add(u12.getUid());
                    list12.add(u12);
                    break;
                case 1500:
                    OrgInsMessage u13 = JSON.toJavaObject(json, OrgInsMessage.class);
                    finalAllIds.add(u13.getUid());
                    list13.add(u13);
                    break;
                case 1600:
                    OrgMileage u14 = JSON.toJavaObject(json, OrgMileage.class);
                    finalAllIds.add(u14.getUid());
                    list14.add(u14);
                    break;
            }
        });
        allIds = finalAllIds.stream().distinct().collect(Collectors.toList());// 去除重复ID
        String idsString = allIds.stream().distinct().map(v -> v.toString()).collect(Collectors.joining(","));
        // 获取日志所有用户数据
        List<User> userList = userMapper.selectByIdIn(idsString);
        // 将用户数据和用户所在部门数据做成map来使用
        Map<Integer, User> userMap = userList.stream().filter(v -> StringUtils.isNotEmpty(v.getDepartmentId())).collect(Collectors.toMap(v -> v.getId().intValue(), v -> v));

        //  ------------------------- 所有日志的用户信息，以及需要用到的本级和上级部门信息全部都查询出来了--------------------------------

        try {
            // 一级数据处理
            userLogoff = this.insertIntoUserLoginRecordOrigin(list1, list2, userMap);
            this.insertIntoStaUserSosRecordOrigin(list3, list4, list5, userMap);
            this.insertIntoStaUserAudioRecordOrigin(list6, userMap);
            this.insertIntoStaUserVideoRecord(list7, list8, list9, list10, userMap);
            this.insertIntoStaUserPhotoRecord(list11, userMap);
            this.insertIntoStaUserTmpGroupRecord(list12, userMap);
            this.insertIntoStaUserImRecord(list13, userMap);
            this.insertIntoStaUserMileageRecord(list14, userMap);

            if (contentList.size() > 0) {
                orgPosition.setLastPkId(contentList.get(contentList.size() - 1).getId());
                staParsePositionMapper.updateByPrimaryKeySelective(orgPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("处理原始日志数据发生异常");
        }
        return userLogoff;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void parseLog2() {
        try {
            // 二级数据处理

            this.insertIntoStaUserOnlineRecord();
            this.insertIntoStaUserAudioRecord();
            this.insertIntoStaUserSosRecord();
            this.insertIntoStaUserVideoRecord();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("处理间断性数据中发生异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void parseLog3() {
        try {
            this.insertIntoStaDayUser();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("处理用户/部门每天数据发生异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void parseLog4() {
        List<StaUserLogonRecordOrigin> userLogoff = new ArrayList<>();
        try {
            userLogoff = this.parseLog();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("处理原始日志数据中发生异常");
        }
        this.parseLog2();
        this.parseLog3();

        List<Long> ids = new ArrayList<>();//用来临时存储uid
        if (userLogoff == null) {
            return;
        }
        userLogoff = userLogoff.stream().filter(v -> {
            boolean flag = !ids.contains(v.getUid());
            ids.add(v.getUid());
            return flag;
        }).collect(Collectors.toList());
        Map<Long, StaUserLogonRecordOrigin> map = userLogoff.stream().collect(Collectors.toMap(v -> v.getUid(), v -> v));
        if (userLogoff != null && userLogoff.size() > 0) {
            List<StaOriginalLog> originalLogs = new ArrayList<>();
            String uids = userLogoff.stream().map(v -> v.getUid().toString()).collect(Collectors.joining(","));
            List<StaUserAudioRecord> audioRecordsOngoing = staUserAudioRecordMapper.selectByIdIn(uids);
            List<StaUserVideoRecord> videoRecordsOngoing = staUserVideoRecordMapper.selectByIdIn(uids);

            audioRecordsOngoing.forEach(v -> {
                StaUserLogonRecordOrigin params = map.get(v.getUid());
                if (v.getType() == 1) {
                    // 生成语音和视频退出的log
                    JSONObject audioLogType1 = new JSONObject();// 个人
                    audioLogType1.put("id", 1100);
                    audioLogType1.put("uid", v.getUid());
                    audioLogType1.put("type", 1);
                    audioLogType1.put("target", v.getTargetId());
                    audioLogType1.put("end_time", sdf.get().format(params.getTime()));
                    audioLogType1.put("flag", 2);
                    audioLogType1.put("system_interrupt", 1);// 表明是用户退出时，自动生成的日志信息
                    StaOriginalLog audio1 = new StaOriginalLog();
                    audio1.setContent(audioLogType1.toString());
                    originalLogs.add(audio1);
                } else if (v.getType() == 2) {
                    JSONObject audioLogType2 = new JSONObject();// 群组
                    audioLogType2.put("id", 1100);
                    audioLogType2.put("uid", v.getUid());
                    audioLogType2.put("type", 2);
                    audioLogType2.put("target", v.getTargetId());
                    audioLogType2.put("end_time", sdf.get().format(params.getTime()));
                    audioLogType2.put("flag", 2);
                    audioLogType2.put("system_interrupt", 1);// 表明是用户退出时，自动生成的日志信息
                    StaOriginalLog audio2 = new StaOriginalLog();
                    audio2.setContent(audioLogType2.toString());
                    originalLogs.add(audio2);
                }
            });

            videoRecordsOngoing.forEach(v -> {
                StaUserLogonRecordOrigin params = map.get(v.getUid());
                if (v.getType() == 1) {
                    JSONObject videoLogType1 = new JSONObject();// 视频通话
                    videoLogType1.put("id", 1200);
                    videoLogType1.put("sub_id", 1201);
                    videoLogType1.put("uid", v.getUid());
                    videoLogType1.put("target", v.getTargetId());
                    videoLogType1.put("end_time", sdf.get().format(params.getTime()));
                    videoLogType1.put("flag", 2);
                    videoLogType1.put("system_interrupt", 1);// 表明是用户退出时，自动生成的日志信息
                    StaOriginalLog video1 = new StaOriginalLog();
                    video1.setContent(videoLogType1.toString());
                    originalLogs.add(video1);
                } else if (v.getType() == 2) {
                    List<String> videoUploadSession = staUserVideoRecordMapper.selectSessionByUidAndType(v.getUid().intValue(), 2);
                    for (String s : videoUploadSession) {
                        JSONObject videoLogType2 = new JSONObject();// 视频回传
                        videoLogType2.put("id", 1200);
                        videoLogType2.put("sub_id", 1202);
                        videoLogType2.put("uid", v.getUid());
                        videoLogType2.put("end_time", sdf.get().format(params.getTime()));
                        videoLogType2.put("flag", 2);
                        videoLogType2.put("session", s);
                        videoLogType2.put("system_interrupt", 1);// 表明是用户退出时，自动生成的日志信息
                        StaOriginalLog video2 = new StaOriginalLog();
                        video2.setContent(videoLogType2.toString());
                        originalLogs.add(video2);
                    }
                } else if (v.getType() == 3) {
                    List<String> videoCallTheRollSession = staUserVideoRecordMapper.selectSessionByUidAndType(v.getUid().intValue(), 3);
                    for (String s : videoCallTheRollSession) {
                        JSONObject videoLogType3 = new JSONObject();// 视频点名
                        videoLogType3.put("id", 1200);
                        videoLogType3.put("sub_id", 1203);
                        videoLogType3.put("uid", v.getUid());
                        videoLogType3.put("end_time", sdf.get().format(params.getTime()));
                        videoLogType3.put("flag", 2);
                        videoLogType3.put("session", s);
                        videoLogType3.put("system_interrupt", 1);// 表明是用户退出时，自动生成的日志信息
                        StaOriginalLog video3 = new StaOriginalLog();
                        video3.setContent(videoLogType3.toString());
                        originalLogs.add(video3);
                    }
                } else if (v.getType() == 4) {
                    List<String> videoConsultationSession = staUserVideoRecordMapper.selectSessionByUidAndType(v.getUid().intValue(), 4);
                    for (String s : videoConsultationSession) {
                        JSONObject videoLogType4 = new JSONObject();// 视频会商
                        videoLogType4.put("id", 1200);
                        videoLogType4.put("sub_id", 1204);
                        videoLogType4.put("uid", v.getUid());
                        videoLogType4.put("end_time", sdf.get().format(params.getTime()));
                        videoLogType4.put("flag", 2);
                        videoLogType4.put("session", s);
                        videoLogType4.put("system_interrupt", 1);// 表明是用户退出时，自动生成的日志信息
                        StaOriginalLog video4 = new StaOriginalLog();
                        video4.setContent(videoLogType4.toString());
                        originalLogs.add(video4);
                    }
                }
            });
            if (originalLogs.size() > 0) {
                logger.debug("用户退出后，生成的结束日志：" + JSON.toJSONString(originalLogs));
                staOriginalLogMapper.insertList(originalLogs);
            }
        }
    }


    /**
     * 用户登录记录表
     * @param list1
     * @param list2
     * @param userMap
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public List<StaUserLogonRecordOrigin> insertIntoUserLoginRecordOrigin (List<OrgUserLogin> list1,
                                                 List<OrgUserLoginOut> list2,
                                                 Map<Integer, User> userMap) throws ParseException {
        List<StaUserLogonRecordOrigin> userLogonRecords = new ArrayList<>();
        List<StaUserLogonRecordOrigin> userLogoff = new ArrayList<>();
        List<Long> uidFilter = new ArrayList<>();// 过滤多余的用户uid
        for (OrgUserLogin orgUserLogin : list1) {
            try {
                StaUserLogonRecordOrigin login = new StaUserLogonRecordOrigin();
                User user = userMap.get(orgUserLogin.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，orgUserLogin：" + orgUserLogin.toString() );
                } else {
                    login.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(orgUserLogin.getTime()))))
                            .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(orgUserLogin.getTime()))))
                            .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(orgUserLogin.getTime()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setTime(sdf.get().parse(orgUserLogin.getTime()))
                            .setBindDuty(user.getRoleId() == 0 ? 0 : 1)
                            .setFlag(1)
                            .setReal(orgUserLogin.getReal())
                            .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
                    userLogonRecords.add(login);
                }
            } catch (Exception e) {
                logger.error("用户登录记录表发生异常，参数信息:" + orgUserLogin.toString());
            }

        }
        for (OrgUserLoginOut orgUserLoginOut : list2) {
            try {
                StaUserLogonRecordOrigin loginOut = new StaUserLogonRecordOrigin();
                User user = userMap.get(orgUserLoginOut.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，orgUserLoginOut：" + orgUserLoginOut.toString() );
                } else {
                    loginOut.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(orgUserLoginOut.getTime()))))
                            .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(orgUserLoginOut.getTime()))))
                            .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(orgUserLoginOut.getTime()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setTime(sdf.get().parse(orgUserLoginOut.getTime()))
                            .setBindDuty(user.getRoleId() == 0 ? 0 : 1)
                            .setFlag(2)
                            .setReal(orgUserLoginOut.getReal())
                            .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
                    userLogonRecords.add(loginOut);
                    if (loginOut.getReal() == 1) {
                    } else if (uidFilter.indexOf(loginOut.getUid()) == -1) {
                        uidFilter.add(loginOut.getUid());
                        // 非真实退出需要新增退出log
                        userLogoff.add(loginOut);
                    }
                }
            } catch (Exception e) {
                logger.error("用户登出记录表发生异常，参数信息:" + orgUserLoginOut.toString());
            }

        }

        userLogonRecords = userLogonRecords.stream().sorted((o1, o2) -> o1.getTime().compareTo(o2.getTime())).collect(Collectors.toList());
        if (userLogonRecords.size() > 0) {
            staUserLogonRecordOriginMapper.insertList(userLogonRecords);
        }
        return userLogoff;
    }


    /**
     * 语音通话origin
     * @param list6
     * @param userMap
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserAudioRecordOrigin (List<OrgVoiceCall> list6,
                                                    Map<Integer, User> userMap) throws ParseException {
        List<StaUserAudioRecordOrigin> list = new ArrayList<>();
        for (OrgVoiceCall voice : list6) {
            try {
                String targetName = "";
                if (voice.getType() == 1) {// 个呼
                    targetName = userMapper.selectNameById(voice.getTarget());
                } else if (voice.getType() == 2) {// 组呼
                    targetName = groupMapper.selectNameById(voice.getTarget());
                }
                StaUserAudioRecordOrigin origin = new StaUserAudioRecordOrigin();
                User user = userMap.get(voice.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，voice：" + voice.toString() );
                } else {
                    origin.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(voice.getFlag() == 1
                                    ? Integer.parseInt(ym.get().format(sdf.get().parse(voice.getStart_time())))
                                    : Integer.parseInt(ym.get().format(sdf.get().parse(voice.getEnd_time()))))
                            .setTimeYmd(voice.getFlag() == 1
                                    ? Integer.parseInt(ymd.get().format(sdf.get().parse(voice.getStart_time())))
                                    : Integer.parseInt(ymd.get().format(sdf.get().parse(voice.getEnd_time()))))
                            .setTimeYmdh(voice.getFlag() == 1
                                    ? Integer.parseInt(ymdh.get().format(sdf.get().parse(voice.getStart_time())))
                                    : Integer.parseInt(ymdh.get().format(sdf.get().parse(voice.getEnd_time()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setTargetId(voice.getTarget() == null ? null : voice.getTarget().longValue())
                            .setTargetName(targetName)
                            .setType(voice.getType())
                            .setTime(voice.getFlag() == 1 ? sdf.get().parse(voice.getStart_time()) : sdf.get().parse(voice.getEnd_time()))
                            .setFlag(voice.getFlag())
                            .setSession(voice.getSession());
                    if (voice.getType() == 1 && user.getRoleType() == 1) {
                        origin.setState(2);
                    } else {
                        origin.setState(1);
                    }
                    list.add(origin);
                }
            } catch (Exception e) {
                logger.error("语音通话记录表发生异常，参数信息：" + voice.toString());
            }
        }
        list = list.stream().sorted((o1, o2) -> o1.getTime().compareTo(o2.getTime())).collect(Collectors.toList());
        if (list.size() > 0) {
            staUserAudioRecordOriginMapper.insertList(list);
        }
    }

    /**
     * 即时消息记录
     * @param list13
     * @param userMap
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserImRecord (List<OrgInsMessage> list13,
                                           Map<Integer, User> userMap) throws ParseException {
        List<StaUserImRecord> list = new ArrayList<>();
        for (OrgInsMessage value : list13) {
            logger.info("测试验证信息，IM：" + value.toString());
            try {
                String targetName = "";
                if (value.getType() == 1) {// 个呼
                    targetName = userMapper.selectNameById(value.getTarget());
                } else if (value.getType() == 2) {// 组呼
                    targetName = groupMapper.selectNameById(value.getTarget());
                }
                StaUserImRecord record = new StaUserImRecord();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString() );
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getTime()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setTargetId(value.getTarget().longValue())
                            .setTargetName(targetName)
                            .setType(value.getType())
                            .setImType(value.getIm_type())
                            .setStartTime(sdf.get().parse(value.getTime()))
                            .setEndFlag(1);// 无意义的字段，默认为结束
                    if (value.getType() == 1 && user.getRoleType() == 1) {
                        record.setState(2);
                    } else {
                        record.setState(1);
                    }
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("即时消息记录表发生异常，参数信息：" + value.toString());
            }
        }
        list = list.stream().sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime())).collect(Collectors.toList());
        if (list.size() > 0) {
            staUserImRecordMapper.insertList(list);
        }
    }


    /**
     * 用户里程
     * @param list14
     * @param userMap
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserMileageRecord (List<OrgMileage> list14,
                                                Map<Integer, User> userMap) throws ParseException {

        List<StaUserMileageRecord> list = new ArrayList<>();
        for (OrgMileage value : list14) {
            try {
                StaUserMileageRecord record = new StaUserMileageRecord();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString() );
                }
                record.setCorpid(user.getCorpId())
                        .setUid(user.getId())
                        .setName(user.getDisplayName())
                        .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(value.getTime()))))
                        .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(value.getTime()))))
                        .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getTime()))))
                        .setDeptUniqueId(user.getDepartmentId())
                        .setDeptName(user.getDeptName())
                        .setStartTime(sdf.get().parse(value.getTime()))
                        .setMileage(value.getMileage())
                        .setSession(value.getSession())
                        .setState(user.getRoleType() == 1 ? 2 : 1);
                list.add(record);
            } catch (Exception e) {
                logger.error("用户里程记录表发生异常，参数信息：" + value.toString());
            }
        }
        list = list.stream().sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime())).collect(Collectors.toList());
        if (list.size() > 0) {
            staUserMileageRecordMapper.insertList(list);
        }

    }


    /**
     * 照片回传
     * @param list11
     * @param userMap
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserPhotoRecord (List<OrgPhotoBack> list11,
                                              Map<Integer, User> userMap) throws ParseException {
        List<StaUserPhotoRecord> list = new ArrayList<>();
        for (OrgPhotoBack value : list11) {
            try {
                StaUserPhotoRecord record = new StaUserPhotoRecord();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString() );
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getTime()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setStartTime(sdf.get().parse(value.getTime()))
                            .setSession(value.getSession())
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("照片回传记录表发生错误，参数信息：" + value.toString());
            }
        }
        list = list.stream().sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime())).collect(Collectors.toList());
        if (list.size() > 0) {
            staUserPhotoRecordMapper.insertList(list);
        }
    }


    /**
     * 发起一键告警，取消一键告警，处理一键告警
     * @param list3
     * @param list4
     * @param list5
     * @param userMap
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void  insertIntoStaUserSosRecordOrigin (List<OrgIniKeyAlarm> list3,
                                                   List<OrgCancelKeyAlarm> list4,
                                                   List<OrgDealKeyAlarm> list5,
                                                   Map<Integer, User> userMap) throws ParseException {
        List<StaUserSosRecordOrigin> list = new ArrayList<>();
        for (OrgIniKeyAlarm value : list3) {
            try {
                StaUserSosRecordOrigin record = new StaUserSosRecordOrigin();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value);
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getTime()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setTime(sdf.get().parse(value.getTime()))
                            .setLocation(value.getLocation())
                            .setMarker(value.getMarker())
                            .setFlag(1)
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("发起告警发生异常，参数信息:" + value.toString());
            }
        }
        for (OrgCancelKeyAlarm value : list4) {
            try {
                StaUserSosRecordOrigin record = new StaUserSosRecordOrigin();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString());
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getTime()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setTime(sdf.get().parse(value.getTime()))
                            .setLocation(value.getLocation())
                            .setMarker(value.getMarker())
                            .setFlag(2)
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("取消告警发生异常，参数信息:" + value.toString());
            }
        }
        for (OrgDealKeyAlarm value : list5) {
            try {
                StaUserSosRecordOrigin record = new StaUserSosRecordOrigin();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString());
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getTime()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setTime(sdf.get().parse(value.getTime()))
                            .setHandleUid(value.getHandle_uid().longValue())
                            .setLocation(value.getLocation())
                            .setMarker(value.getMarker())
                            .setFlag(3)
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("处理告警发生异常，参数信息:" + value.toString());
            }
        }
        list = list.stream().sorted((o1, o2) -> o1.getTime().compareTo(o2.getTime())).collect(Collectors.toList());
        if (list.size() > 0) {
            staUserSosRecordOriginMapper.insertList(list);
        }
    }

    /**
     * 创建临时组
     * @param list12
     * @param userMap
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserTmpGroupRecord (List<OrgCreateTemporaryGroup> list12,
                                                 Map<Integer, User> userMap) throws ParseException {
        List<StaUserTmpgroupRecord> list = new ArrayList<>();
        for (OrgCreateTemporaryGroup value : list12) {
            try {
                StaUserTmpgroupRecord record = new StaUserTmpgroupRecord();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString());
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(Integer.parseInt(ym.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmd(Integer.parseInt(ymd.get().format(sdf.get().parse(value.getTime()))))
                            .setTimeYmdh(Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getTime()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setCreateTime(sdf.get().parse(value.getTime()))
                            .setGroupName(value.getName())
                            .setSession(value.getSession())
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("创建临时组发生异常，参数信息:" + value.toString());
            }
        }
        list = list.stream().sorted((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime())).collect(Collectors.toList());
        if (list.size() > 0) {
            staUserTmpgroupRecordMapper.insertList(list);
        }
    }


    /**
     * 视频通话
     * @param list7
     * @param list8
     * @param list9
     * @param list10
     * @param userMap
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserVideoRecord (List<OrgVideoCall> list7,
                                              List<OrgVideoBack> list8,
                                              List<OrgVideoCallTheRoll> list9,
                                              List<OrgVideoConsultation> list10,
                                              Map<Integer, User> userMap) throws ParseException {
        List<StaUserVideoRecordOrigin> list = new ArrayList<>();
        // 视频通话
        for (OrgVideoCall value : list7) {
            try {
                StaUserVideoRecordOrigin record = new StaUserVideoRecordOrigin();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString() );
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(value.getFlag() == 1
                                    ? Integer.parseInt(ym.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ym.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setTimeYmd(value.getFlag() == 1
                                    ? Integer.parseInt(ymd.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ymd.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setTimeYmdh(value.getFlag() == 1
                                    ? Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setTargetId(value.getTarget() == null ? null : value.getTarget().longValue())
//                    .setTargetName()
                            .setType(1)
                            .setTime(value.getFlag() == 1
                                    ? sdf.get().parse(value.getStart_time())
                                    : sdf.get().parse(value.getEnd_time()))
                            .setFlag(value.getFlag())
                            .setSession(value.getSession())
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("视频通话发生异常，参数信息:" + value.toString());
            }
        }
        // 视频回传
        for (OrgVideoBack value : list8) {
            try {
                StaUserVideoRecordOrigin record = new StaUserVideoRecordOrigin();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString() );
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(value.getFlag() == 1
                                    ? Integer.parseInt(ym.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ym.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setTimeYmd(value.getFlag() == 1
                                    ? Integer.parseInt(ymd.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ymd.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setTimeYmdh(value.getFlag() == 1
                                    ? Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setType(2)
                            .setTime(value.getFlag() == 1
                                    ? sdf.get().parse(value.getStart_time())
                                    : sdf.get().parse(value.getEnd_time()))
                            .setFlag(value.getFlag())
                            .setSession(value.getSession())
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("视频回传发生异常，参数信息:" + value.toString());
            }
        }
        // 视频点名
        for (OrgVideoCallTheRoll value : list9) {
            try {
                StaUserVideoRecordOrigin record = new StaUserVideoRecordOrigin();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString() );
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(value.getFlag() == 1
                                    ? Integer.parseInt(ym.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ym.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setTimeYmd(value.getFlag() == 1
                                    ? Integer.parseInt(ymd.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ymd.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setTimeYmdh(value.getFlag() == 1
                                    ? Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setType(3)
                            .setTime(value.getFlag() == 1
                                    ? sdf.get().parse(value.getStart_time())
                                    : sdf.get().parse(value.getEnd_time()))
                            .setFlag(value.getFlag())
                            .setSession(value.getSession())
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("视频点名发生异常，参数信息:" + value.toString());
            }
        }
        // 视频会商
        for (OrgVideoConsultation value : list10) {
            try {
                StaUserVideoRecordOrigin record = new StaUserVideoRecordOrigin();
                User user = userMap.get(value.getUid());
                if (user == null) {
                    logger.warn("用户不存在，请检查数据是否正确，value：" + value.toString() );
                } else {
                    record.setCorpid(user.getCorpId())
                            .setUid(user.getId())
                            .setName(user.getDisplayName())
                            .setTimeYm(value.getFlag() == 1
                                    ? Integer.parseInt(ym.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ym.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setTimeYmd(value.getFlag() == 1
                                    ? Integer.parseInt(ymd.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ymd.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setTimeYmdh(value.getFlag() == 1
                                    ? Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getStart_time())))
                                    : Integer.parseInt(ymdh.get().format(sdf.get().parse(value.getEnd_time()))))
                            .setDeptUniqueId(user.getDepartmentId())
                            .setDeptName(user.getDeptName())
                            .setType(4)
                            .setTime(value.getFlag() == 1
                                    ? sdf.get().parse(value.getStart_time())
                                    : sdf.get().parse(value.getEnd_time()))
                            .setFlag(value.getFlag())
                            .setSession(value.getSession())
                            .setState(user.getRoleType() == 1 ? 2 : 1);
                    list.add(record);
                }
            } catch (Exception e) {
                logger.error("视频会商发生异常，参数信息:" + value.toString());
            }
        }
        list = list.stream().sorted((o1, o2) -> o1.getTime().compareTo(o2.getTime())).collect(Collectors.toList());
        if (list.size() > 0) {
            staUserVideoRecordOriginMapper.insertList(list);
        }
    }


    /**
     * 用户登录时长计算
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserOnlineRecord () throws ParseException {

        StaParsePosition orgPosition = staParsePositionMapper.selectByTableName("sta_user_logon_record_origin");// 获取上次用户登录origin表的结点
        List<StaUserLogonRecordOrigin> recordOrigins = staUserLogonRecordOriginMapper.selectLogonOutByLastId(orgPosition.getLastPkId().intValue());// 通过上次查询的id 获取到新增的用户

        if (recordOrigins.size() > 0) {
            StaUserOnlineRecord param1 = new StaUserOnlineRecord();
            // 该数据表明有开始时间没有结束时间
            List<StaUserOnlineRecord> onlineState1 = staUserOnlineRecordMapper.select(param1.setEndFlag(3));
            // 真实登录：1
            Map<String, StaUserOnlineRecord> onlineState1Map = onlineState1.stream().collect(Collectors.toMap(v -> v.getUid() + (v.getReal() == 1 ? "1" : "2"), v -> v));

            for (StaUserLogonRecordOrigin recordOrigin : recordOrigins) {
                if (recordOrigin.getFlag() == 1) {// 登陆
                    // 如果用户没有退出操作，然后又登录了，要覆盖之前的登录信息，设置为新的登录信息
                    StaUserOnlineRecord staUserOnlineRecord = onlineState1Map.get(recordOrigin.getUid() + (recordOrigin.getReal() == 1 ? "1" : "2"));
                    if (staUserOnlineRecord == null) {
                        staUserOnlineRecord = new StaUserOnlineRecord();
                        Long sec = (System.currentTimeMillis() - recordOrigin.getTime().getTime()) / 1000;
                        staUserOnlineRecord.setCorpid(recordOrigin.getCorpid())
                                .setUid(recordOrigin.getUid())
                                .setName(recordOrigin.getName())
                                .setTimeYm(recordOrigin.getTimeYm())
                                .setTimeYmd(recordOrigin.getTimeYmd())
                                .setTimeYmdh(recordOrigin.getTimeYmdh())
                                .setDeptUniqueId(recordOrigin.getDeptUniqueId())
                                .setDeptName(recordOrigin.getDeptName())
                                .setLogonTime(recordOrigin.getTime())
                                .setIsOnduty(recordOrigin.getBindDuty())
                                .setEndFlag(3)
                                .setOnlineDuration(sec.intValue())
                                .setSession(recordOrigin.getSession())
                                .setReal(recordOrigin.getReal())
                                .setState(recordOrigin.getState());
                        if (onlineState1Map.get(recordOrigin.getUid() + (recordOrigin.getReal() == 1 ? "1" : "2")) == null) {
                            onlineState1.add(staUserOnlineRecord);// 新增到在线状态为3的集合中
                            onlineState1Map.put(staUserOnlineRecord.getUid() + (staUserOnlineRecord.getReal() == 1 ? "1" : "2"), staUserOnlineRecord);// 新增到状态为3的map中
                        }
                    } else {
                        if (staUserOnlineRecord.getLogonTime().getTime() <= recordOrigin.getTime().getTime()) {
                            if (staUserOnlineRecord.getEndFlag() == 3) {
                                // 结算多次登录
                                staUserOnlineRecord.setLogoffTime(recordOrigin.getTime())
                                        .setEndFlag(staUserOnlineRecord.getLastTime() == null ? 1 : 4);
                                Long sec = (staUserOnlineRecord.getLogoffTime().getTime() - staUserOnlineRecord.getLogonTime().getTime()) / 1000;
                                if (sec < 0) {
                                    logger.warn("在线时长为负数，DO:" + staUserOnlineRecord.toString());
                                    staUserOnlineRecord.setOnlineDuration(0);
                                } else {
                                    staUserOnlineRecord.setOnlineDuration(sec.intValue());
                                }
                                onlineState1Map.remove(staUserOnlineRecord.getUid() + (staUserOnlineRecord.getReal() == 1 ? "1" : "2"));
                            }
                            StaUserOnlineRecord onlineRecord = new StaUserOnlineRecord();
                            onlineRecord.setCorpid(recordOrigin.getCorpid())
                                    .setUid(recordOrigin.getUid())
                                    .setName(recordOrigin.getName())
                                    .setTimeYm(recordOrigin.getTimeYm())
                                    .setTimeYmd(recordOrigin.getTimeYmd())
                                    .setTimeYmdh(recordOrigin.getTimeYmdh())
                                    .setDeptUniqueId(recordOrigin.getDeptUniqueId())
                                    .setDeptName(recordOrigin.getDeptName())
                                    .setLogonTime(recordOrigin.getTime())
                                    .setIsOnduty(recordOrigin.getBindDuty())
                                    .setEndFlag(3)
                                    .setSession(recordOrigin.getSession())
                                    .setReal(recordOrigin.getReal())
                                    .setState(recordOrigin.getState());
                            onlineState1.add(onlineRecord);// 新增到在线状态为3的集合中
                            onlineState1Map.put(onlineRecord.getUid() + (onlineRecord.getReal() == 1 ? "1" : "2"), onlineRecord);// 新增到状态为3的map中
                        } else {
                            logger.error("用户登录时长计算时，登录时间大于登出时间，错误的数据信息，DO：" + recordOrigin.toString());
                        }
                    }
                } else if (recordOrigin.getFlag() == 2) {
                    StaUserOnlineRecord staUserOnlineRecord = onlineState1Map.get(recordOrigin.getUid() + (recordOrigin.getReal() == 1 ? "1" : "2"));
                    if (staUserOnlineRecord != null) {
                        staUserOnlineRecord.setLogoffTime(recordOrigin.getTime())
                                .setEndFlag(staUserOnlineRecord.getLastTime() == null ? 1 : 4);
                        Long sec = (staUserOnlineRecord.getLogoffTime().getTime() - staUserOnlineRecord.getLogonTime().getTime()) / 1000;
                        if (sec < 0) {
                            logger.warn("在线时长为负数，DO:" + staUserOnlineRecord.toString());
                            staUserOnlineRecord.setOnlineDuration(0);
                        } else {
                            staUserOnlineRecord.setOnlineDuration(sec.intValue());
                        }
                        onlineState1Map.remove(staUserOnlineRecord.getUid() + (staUserOnlineRecord.getReal() == 1 ? "1" : "2"));
                    }else {
                        logger.warn("用户登录时长计算时，没有找到用户的开始时间，产生一条无用数据信息, MSG：" + recordOrigin.toString());
                    }
                }
            }

            // 新增
            List<StaUserOnlineRecord> insertList = onlineState1.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
            if (insertList.size() > 0) {
                staUserOnlineRecordMapper.insertList(insertList);
            }
            // 存在id时就修改
            List<StaUserOnlineRecord> updateList = onlineState1.stream().filter(v -> v.getId() != null).collect(Collectors.toList());
            updateList.forEach(v -> staUserOnlineRecordMapper.updateByPrimaryKeySelective(v));

            // 更新sta_user_logon_record_origin读取的主键id
            if (recordOrigins.size() > 0) {
                orgPosition.setLastPkId(recordOrigins.get(recordOrigins.size() - 1).getId());
                staParsePositionMapper.updateByPrimaryKeySelective(orgPosition);
            }
        }
    }

    /**
     * 语音通话时长计算
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserAudioRecord () throws ParseException {
        StaParsePosition orgPosition = staParsePositionMapper.selectByTableName("sta_user_audio_record_origin");// 获取上次用户登录origin表的结点
        List<StaUserAudioRecordOrigin> recordOrigins = staUserAudioRecordOriginMapper.selectByLastId(orgPosition.getLastPkId().intValue());// 获取用户结束状态
        if (recordOrigins.size() > 0) {
            // TODO 需要改成endFlag 查询出语音通话的状态为3的数据
            List<StaUserAudioRecord> audioRecordsState3List = staUserAudioRecordMapper.selectByState(3);
            Map<String, StaUserAudioRecord> audioRecordState3Map = audioRecordsState3List.stream().collect(Collectors.toMap(v -> v.getUid() + v.getType().toString(), v-> v));

            for (StaUserAudioRecordOrigin recordOrigin : recordOrigins) {
                // 个呼或者组呼时，是否为开始时间
                if (recordOrigin.getFlag() == 1) {
                    StaUserAudioRecord audioRecord = audioRecordState3Map.get(recordOrigin.getUid() + recordOrigin.getType().toString());
                    if (audioRecord == null) {
                        audioRecord = new StaUserAudioRecord();
                        audioRecord.setCorpid(recordOrigin.getCorpid())
                                .setUid(recordOrigin.getUid())
                                .setName(recordOrigin.getName())
                                .setTimeYm(recordOrigin.getTimeYm())
                                .setTimeYmd(recordOrigin.getTimeYmd())
                                .setTimeYmdh(recordOrigin.getTimeYmdh())
                                .setDeptUniqueId(recordOrigin.getDeptUniqueId())
                                .setDeptName(recordOrigin.getDeptName())
                                .setTargetId(recordOrigin.getTargetId())
                                .setTargetName(recordOrigin.getTargetName())
                                .setType(recordOrigin.getType())
                                .setStartTime(recordOrigin.getTime())
                                .setSession(recordOrigin.getSession())
                                .setEndFlag(3)
                                .setState(recordOrigin.getState());
                        if (audioRecordState3Map.get(audioRecord.getUid() + audioRecord.getType().toString()) == null) {
                            audioRecordsState3List.add(audioRecord);
                            audioRecordState3Map.put(audioRecord.getUid() + audioRecord.getType().toString(), audioRecord);
                        }
                    } else {
                        if (audioRecord.getStartTime().getTime() <= recordOrigin.getTime().getTime()) {
                            if (audioRecord.getEndFlag() == 3) {
                                // 结算之前的数据
                                audioRecord.setEndTime(recordOrigin.getTime())
                                        .setEndFlag(audioRecord.getLastTime() == null ? 1 : 4);
                                Long sec = (audioRecord.getEndTime().getTime() - audioRecord.getStartTime().getTime()) / 1000;
                                if (sec < 0) {
                                    logger.warn("语音通话时间为负数，DO：" + audioRecord.toString());
                                    audioRecord.setDuration(0);
                                } else {
                                    audioRecord.setDuration(sec.intValue());
                                }
                                audioRecordState3Map.remove(audioRecord.getUid() + audioRecord.getType().toString());
                            }
                            StaUserAudioRecord audioRecord1 = new StaUserAudioRecord();
                            audioRecord1.setCorpid(recordOrigin.getCorpid())
                                    .setUid(recordOrigin.getUid())
                                    .setName(recordOrigin.getName())
                                    .setTimeYm(recordOrigin.getTimeYm())
                                    .setTimeYmd(recordOrigin.getTimeYmd())
                                    .setTimeYmdh(recordOrigin.getTimeYmdh())
                                    .setDeptUniqueId(recordOrigin.getDeptUniqueId())
                                    .setDeptName(recordOrigin.getDeptName())
                                    .setTargetId(recordOrigin.getTargetId())
                                    .setTargetName(recordOrigin.getTargetName())
                                    .setType(recordOrigin.getType())
                                    .setStartTime(recordOrigin.getTime())
                                    .setSession(recordOrigin.getSession())
                                    .setEndFlag(3)
                                    .setState(recordOrigin.getState());
                            if (audioRecordState3Map.get(audioRecord1.getUid() + audioRecord1.getType().toString()) == null) {
                                audioRecordsState3List.add(audioRecord1);
                                audioRecordState3Map.put(audioRecord1.getUid() + audioRecord1.getType().toString(), audioRecord1);
                            }
                        } else {
                            logger.error("语音通话的结束时间小于开始时间，这是错误的数据信息，DO：" + recordOrigin.toString());
                        }
                    }
                } else if (recordOrigin.getFlag() == 2) {
                    StaUserAudioRecord audioRecord = audioRecordState3Map.get(recordOrigin.getUid() + recordOrigin.getType().toString());
                    if (audioRecord != null) {
                        audioRecord.setEndTime(recordOrigin.getTime())
                                .setEndFlag(audioRecord.getLastTime() == null ? 1 : 4);
                        Long sec = (audioRecord.getEndTime().getTime() - audioRecord.getStartTime().getTime()) / 1000;
                        if (sec < 0) {
                            logger.warn("语音通话时间为负数，DO：" + audioRecord.toString());
                            audioRecord.setDuration(0);
                        } else {
                            audioRecord.setDuration(sec.intValue());
                        }
                        audioRecordState3Map.remove(audioRecord.getUid() + audioRecord.getType().toString());
                    } else {
                        // 存在通话时，对方挂断的情况
                        StaUserAudioRecord targetRecord = audioRecordState3Map.get(recordOrigin.getTargetId() + recordOrigin.getType().toString());
                        if (targetRecord != null) {
                            targetRecord.setEndTime(recordOrigin.getTime())
                                    .setEndFlag(targetRecord.getLastTime() == null ? 1 : 4);
                            Long sec = (targetRecord.getEndTime().getTime() - targetRecord.getStartTime().getTime()) / 1000;
                            if (sec < 0) {
                                logger.warn("语音通话时间为负数，DO：" + audioRecord.toString());
                                targetRecord.setDuration(0);
                            } else {
                                targetRecord.setDuration(sec.intValue());
                            }
                            audioRecordState3Map.remove(targetRecord.getUid() + targetRecord.getType().toString());
                        } else {
                            logger.warn("语音通话时没有找到对应的发起人信息，或者发起人信息已经被处理了，这条多余数据信息, MSG：" + recordOrigin.toString());
                        }
                    }
                }
            }

            // 新增
            List<StaUserAudioRecord> insertList = audioRecordsState3List.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
            if (insertList.size() > 0) {
                staUserAudioRecordMapper.insertList(insertList);
            }
            // 修改
            List<StaUserAudioRecord> updateList = audioRecordsState3List.stream().filter(v -> v.getId() != null).collect(Collectors.toList());
            updateList.forEach(v -> staUserAudioRecordMapper.updateByPrimaryKeySelective(v));

            // 修改last_id
            if (recordOrigins.size() > 0) {
                orgPosition.setLastPkId(recordOrigins.get(recordOrigins.size() - 1).getId());
                staParsePositionMapper.updateByPrimaryKeySelective(orgPosition);
            }

        }
    }


    /**
     * 报警
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserSosRecord () throws ParseException {
        StaParsePosition orgPosition = staParsePositionMapper.selectByTableName("sta_user_sos_record_origin");// 获取上次用户登录origin表的结点
        List<StaUserSosRecordOrigin> recordOrigins = staUserSosRecordOriginMapper.selectDealMsg(orgPosition.getLastPkId().intValue(), null);// 获取所有新增的数据

        if (recordOrigins.size() > 0) {
            // 查询出sos的状态为1的数据
            List<StaUserSosRecord> sosRecordsState1List = staUserSosRecordMapper.selectByState(1);
            Map<Long, StaUserSosRecord> staUserSosRecordMap = sosRecordsState1List.stream().collect(Collectors.toMap(v -> v.getUid(), v -> v));

            for (StaUserSosRecordOrigin recordOrigin : recordOrigins) {
                // 报警要么取消，要么处理
                if (recordOrigin.getFlag() == 1) {
                    StaUserSosRecord sosRecord = staUserSosRecordMap.get(recordOrigin.getUid());
                    if (sosRecord == null) {
                        sosRecord = new StaUserSosRecord();
                        sosRecord.setCorpid(recordOrigin.getCorpid())
                                .setUid(recordOrigin.getUid())
                                .setName(recordOrigin.getName())
                                .setTimeYm(recordOrigin.getTimeYm())
                                .setTimeYmd(recordOrigin.getTimeYmd())
                                .setTimeYmdh(recordOrigin.getTimeYmdh())
                                .setDeptUniqueId(recordOrigin.getDeptUniqueId())
                                .setDeptName(recordOrigin.getDeptName())
                                .setStartTime(recordOrigin.getTime())
                                .setLocation(recordOrigin.getLocation())
                                .setMarker(recordOrigin.getMarker())
                                .setSosState(1)
                                .setState(recordOrigin.getState());
                        sosRecordsState1List.add(sosRecord);
                        staUserSosRecordMap.put(sosRecord.getUid(), sosRecord);
                    }
                } else if (recordOrigin.getFlag() == 2) {// 取消
                    StaUserSosRecord sosRecord = staUserSosRecordMap.get(recordOrigin.getUid());
                    if (sosRecord != null) {
                        sosRecord.setEndTime(recordOrigin.getTime())
                                .setSosState(2);
                        staUserSosRecordMap.remove(sosRecord.getUid());// 取消后要在集合中删除对应的用户id，防止被修改为处理状态
                    } else {
                        logger.warn("产生一条无用数据信息, MSG：" + recordOrigin.toString());
                    }
                } else if (recordOrigin.getFlag() == 3) {// 处理
                    StaUserSosRecord sosRecord = staUserSosRecordMap.get(recordOrigin.getUid());
                    if (sosRecord != null) {
                        sosRecord.setHandleTime(recordOrigin.getTime())
                                .setHandleUid(recordOrigin.getHandleUid())
                                .setHandleName(recordOrigin.getHandleName())
                                .setSosState(3);
                        staUserSosRecordMap.remove(sosRecord.getUid());// 处理后要在集合中删除对应的用户id，防止被修改为取消状态
                    } else {
                        logger.warn("产生一条无用数据信息, MSG：" + recordOrigin.toString());
                    }
                }
            }
            List<StaUserSosRecord> insertList = sosRecordsState1List.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
            if (insertList.size() > 0) {
                staUserSosRecordMapper.insertList(insertList);
            }
            List<StaUserSosRecord> updateList = sosRecordsState1List.stream().filter(v -> v.getId() != null).collect(Collectors.toList());
            for (StaUserSosRecord staUserSosRecord : updateList) {
                staUserSosRecordMapper.updateByPrimaryKey(staUserSosRecord);
            }
            if (recordOrigins.size() > 0) {
                orgPosition.setLastPkId(recordOrigins.get(recordOrigins.size() - 1).getId());
                staParsePositionMapper.updateByPrimaryKeySelective(orgPosition);
            }
        }


    }


    /**
     * 视频通话
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaUserVideoRecord () throws ParseException {
        StaParsePosition orgPosition = staParsePositionMapper.selectByTableName("sta_user_video_record_origin");// 获取上次用户登录origin表的结点
        List<StaUserVideoRecordOrigin> recordOrigins = staUserVideoRecordOriginMapper.selectByLastId(orgPosition.getLastPkId().intValue());// 获取新增用户视屏通话信息

        if (recordOrigins.size() > 0) {
            List<StaUserVideoRecord> staUserVideoRecordState3List = staUserVideoRecordMapper.selectByState(3);
            Map<String, StaUserVideoRecord> staUserVideoRecordState3Map = staUserVideoRecordState3List.stream()
                    .filter(v -> v.getType() == 1)
                    .collect(Collectors.toMap(v -> v.getUid() + v.getType().toString(), v -> v));

            Map<String, StaUserVideoRecord> sessionMap = staUserVideoRecordState3List.stream()
                    .filter(v -> StringUtils.isNotEmpty(v.getSession()))
                    .filter(v -> v.getType() != 1)
                    .collect(Collectors.toMap(v -> v.getSession() + v.getType(), v -> v));

            for (StaUserVideoRecordOrigin recordOrigin : recordOrigins) {
                if (recordOrigin.getFlag() == 1) {
                    StaUserVideoRecord videoRecord = recordOrigin.getType() == 1 ?
                            staUserVideoRecordState3Map.get(recordOrigin.getUid() + recordOrigin.getType().toString()) :
                            sessionMap.get(recordOrigin.getSession() + recordOrigin.getType());
                    if (videoRecord == null) {
                        videoRecord = new StaUserVideoRecord();
                        videoRecord.setCorpid(recordOrigin.getCorpid())
                                .setUid(recordOrigin.getUid())
                                .setName(recordOrigin.getName())
                                .setTimeYm(recordOrigin.getTimeYm())
                                .setTimeYmd(recordOrigin.getTimeYmd())
                                .setTimeYmdh(recordOrigin.getTimeYmdh())
                                .setDeptUniqueId(recordOrigin.getDeptUniqueId())
                                .setDeptName(recordOrigin.getDeptName())
                                .setSession(recordOrigin.getSession())
                                .setType(recordOrigin.getType());
                        if (videoRecord.getType() == 1) {
                            videoRecord.setTargetId(recordOrigin.getTargetId())
                                    .setTargetName(recordOrigin.getTargetName());
                        }
                        videoRecord.setStartTime(recordOrigin.getTime())
                                .setEndFlag(3);
                        videoRecord.setState(recordOrigin.getState());
                        staUserVideoRecordState3List.add(videoRecord);
                        if (recordOrigin.getType() == 1) {
                            staUserVideoRecordState3Map.put(videoRecord.getUid() + videoRecord.getType().toString(), videoRecord);
                        } else {
                            sessionMap.put(recordOrigin.getSession() + recordOrigin.getType(), videoRecord);
                        }
                    } else {
                        if (videoRecord.getStartTime().getTime() <= recordOrigin.getTime().getTime()) {
                            if (videoRecord.getEndFlag() == 3) {
                                videoRecord.setEndTime(recordOrigin.getTime())
                                        .setEndFlag(videoRecord.getId() == null ? 1 : 4);
                                Long sec = (videoRecord.getEndTime().getTime() - videoRecord.getStartTime().getTime()) / 1000;
                                if (sec < 0) {
                                    logger.warn("视频通话时间为负数，DO：" + videoRecord.toString());
                                    videoRecord.setDuration(0);
                                } else {
                                    videoRecord.setDuration(sec.intValue());
                                }
                                if (recordOrigin.getType() == 1) {
                                    staUserVideoRecordState3Map.remove(videoRecord.getUid() + videoRecord.getType().toString());
                                } else {
                                    sessionMap.remove(recordOrigin.getSession() + recordOrigin.getType());
                                }
                            }
                            StaUserVideoRecord video = new StaUserVideoRecord();
                            video.setCorpid(recordOrigin.getCorpid())
                                    .setUid(recordOrigin.getUid())
                                    .setName(recordOrigin.getName())
                                    .setTimeYm(recordOrigin.getTimeYm())
                                    .setTimeYmd(recordOrigin.getTimeYmd())
                                    .setTimeYmdh(recordOrigin.getTimeYmdh())
                                    .setDeptUniqueId(recordOrigin.getDeptUniqueId())
                                    .setDeptName(recordOrigin.getDeptName())
                                    .setSession(recordOrigin.getSession())
                                    .setType(recordOrigin.getType());
                            if (video.getType() == 1) {
                                video.setTargetId(recordOrigin.getTargetId())
                                        .setTargetName(recordOrigin.getTargetName());
                            }
                            video.setStartTime(recordOrigin.getTime())
                                    .setEndFlag(3);
                            video.setState(recordOrigin.getState());
                            staUserVideoRecordState3List.add(video);
                            if (recordOrigin.getType() == 1) {
                                staUserVideoRecordState3Map.put(video.getUid() + video.getType().toString(), video);
                            } else {
                                sessionMap.put(video.getSession() + video.getType(), video);
                            }
                        } else {
                            logger.error("错误的数据信息，DO：" + recordOrigin.toString());
                        }
                    }
                } else if (recordOrigin.getFlag() == 2) {
                    StaUserVideoRecord staUserVideoRecord = recordOrigin.getType() == 1 ?
                            staUserVideoRecordState3Map.get(recordOrigin.getUid() + recordOrigin.getType().toString()) :
                            sessionMap.get(recordOrigin.getSession() + recordOrigin.getType());
                    if (staUserVideoRecord != null) {
                        staUserVideoRecord.setEndTime(recordOrigin.getTime())
                                .setEndFlag(staUserVideoRecord.getId() == null ? 1 : 4);
                        Long sec = (staUserVideoRecord.getEndTime().getTime() - staUserVideoRecord.getStartTime().getTime()) / 1000;
                        if (sec < 0) {
                            logger.warn("视频通话时间为负数，DO：" + staUserVideoRecord.toString());
                            staUserVideoRecord.setDuration(0);
                        } else {
                            staUserVideoRecord.setDuration(sec.intValue());
                        }
                        if (recordOrigin.getType() == 1) {
                            staUserVideoRecordState3Map.remove(staUserVideoRecord.getUid() + staUserVideoRecord.getType().toString());
                        } else {
                            sessionMap.remove(staUserVideoRecord.getSession() + staUserVideoRecord.getType());
                        }
                    } else {
                        StaUserVideoRecord targetRecord = staUserVideoRecordState3Map.get(recordOrigin.getTargetId() + recordOrigin.getType().toString());
                        if (targetRecord != null) {
                            targetRecord.setEndTime(recordOrigin.getTime())
                                    .setEndFlag(targetRecord.getId() == null ? 1 : 4);// TODO 如果以后统计视频通话时长，此处需要修改成videoRecord.getLastTime()
                            Long sec = (targetRecord.getEndTime().getTime() - targetRecord.getStartTime().getTime()) / 1000;
                            if (sec < 0) {
                                logger.warn("视频通话时间为负数，DO：" + targetRecord.toString());
                                targetRecord.setDuration(0);
                            } else {
                                targetRecord.setDuration(sec.intValue());
                            }
                            staUserVideoRecordState3Map.remove(targetRecord.getUid() + targetRecord.getType().toString());
                        } else {
                            if (recordOrigin.getType() == 1) {
                                logger.warn("视频通话产生一条无用数据信息, MSG：" + recordOrigin.toString());
                            }
                        }
                    }
                }

            }


            List<StaUserVideoRecord> insertList = staUserVideoRecordState3List.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
            if (insertList.size() > 0) {
                staUserVideoRecordMapper.insertList(insertList);
            }
            List<StaUserVideoRecord> updateList = staUserVideoRecordState3List.stream().filter(v -> v.getId() != null).collect(Collectors.toList());
            updateList.forEach(v -> staUserVideoRecordMapper.updateByPrimaryKeySelective(v));
            // 更新last_id
            if (recordOrigins.size() > 0) {
                orgPosition.setLastPkId(recordOrigins.get(recordOrigins.size() - 1).getId());
                staParsePositionMapper.updateByPrimaryKeySelective(orgPosition);
            }
        }

    }


    /**
     * 用户每天数据处理
     * @throws ParseException
     */
    // rollbackFor:回滚异常  propagation：传播特性（有事务就加入，没有就创建事务） isolation：隔离级别为幻读
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void insertIntoStaDayUser () throws ParseException {
        List<Long> uids = new ArrayList<>();
        List<Integer> dates = new ArrayList<>();

        List<StaParsePosition> positions = staParsePositionMapper.select(new StaParsePosition());
        logger.info("----------------1:" + JSON.toJSONString(positions));
        Map<String, StaParsePosition> positionMap = positions.stream().collect(Collectors.toMap(v -> v.getTableName(), v -> v));
        logger.info("----------------2:" + JSON.toJSONString(positionMap));
        if (true) {
            // 用户在线
            StaParsePosition onlinePosition = positionMap.get("sta_user_online_record");// 获取上次获得的部门用户在线时长last_id
            List<StaUserOnlineRecord> onlineState1 = staUserOnlineRecordMapper.selectByEndFlagAndLastIdLimit(null, onlinePosition.getLastPkId());// end_flag = 1
            onlineState1 = onlineState1.stream().filter(v -> v.getReal() != 1).collect(Collectors.toList());// 在线时长只计算real不等于1的数据
            onlineState1 = onlineState1.stream().filter(v -> v.getEndFlag() == 1).collect(Collectors.toList());
            uids.addAll(onlineState1.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(onlineState1.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            List<StaUserOnlineRecord> onlineState3 = staUserOnlineRecordMapper.selectByEndFlagAndLastId(3, null);// end_flag = 3
            onlineState3 = onlineState3.stream().filter(v -> v.getReal() != 1).collect(Collectors.toList());// 在线时长只计算real不等于1的数据
            uids.addAll(onlineState3.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(onlineState3.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            List<StaUserOnlineRecord> onlineState4 = staUserOnlineRecordMapper.selectByEndFlagAndLastId(4, null);// end_flag = 4
            onlineState4 = onlineState4.stream().filter(v -> v.getReal() != 1).collect(Collectors.toList());// 在线时长只计算real不等于1的数据
            uids.addAll(onlineState4.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(onlineState4.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            // 语音通话
            StaParsePosition audioPosition = positionMap.get("sta_user_audio_record");// 获取上次获得的部门用户在线时长last_id
            List<StaUserAudioRecord> audioState1 = staUserAudioRecordMapper.selectByEndFlagAndLastIdLimit(null, audioPosition.getLastPkId());// end_flag = 1
            audioState1 = audioState1.stream().filter(v -> v.getEndFlag() == 1).collect(Collectors.toList());
            uids.addAll(audioState1.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(audioState1.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            List<StaUserAudioRecord> audioState3 = staUserAudioRecordMapper.selectByEndFlagAndLastId(3, null);// end_flag = 3
            uids.addAll(audioState3.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(audioState3.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            List<StaUserAudioRecord> audioState4 = staUserAudioRecordMapper.selectByEndFlagAndLastId(4, null);// end_flag = 4
            uids.addAll(audioState4.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(audioState4.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            // 视频通话
            StaParsePosition videoPosition = positionMap.get("sta_user_video_record");// 获取上次获得的部门用户在线时长last_id
            List<StaUserVideoRecord> videoState1 = staUserVideoRecordMapper.selectByEndFlagAndLastIdLimit(null, videoPosition.getLastPkId());// end_flag = 1
            videoState1 = videoState1.stream().filter(v -> v.getEndFlag() == 1).collect(Collectors.toList());
            uids.addAll(videoState1.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(videoState1.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            List<StaUserVideoRecord> videoState3 = staUserVideoRecordMapper.selectByEndFlagAndLastId(3, null);// end_flag = 3
            uids.addAll(videoState3.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(videoState3.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            List<StaUserVideoRecord> videoState4 = staUserVideoRecordMapper.selectByEndFlagAndLastId(4, null);// end_flag = 4
            uids.addAll(videoState4.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(videoState4.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            // 报警
            StaParsePosition sosPosition = positionMap.get("sta_user_sos_record");
            List<StaUserSosRecord> sosRecords = staUserSosRecordMapper.selectByLastId(sosPosition.getLastPkId());
            uids.addAll(sosRecords.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(sosRecords.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            // 照片回传
            StaParsePosition photoBackPosition = positionMap.get("sta_user_photo_record");
            List<StaUserPhotoRecord> photoRecords = staUserPhotoRecordMapper.selectByLastId(photoBackPosition.getLastPkId());
            uids.addAll(photoRecords.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(photoRecords.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            // 即时消息
            StaParsePosition imPosition = positionMap.get("sta_user_im_record");
            List<StaUserImRecord> imRecords = staUserImRecordMapper.selectByLastId(imPosition.getLastPkId());
            uids.addAll(imRecords.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(imRecords.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            // 里程数
            StaParsePosition mileagePosition = positionMap.get("sta_user_mileage_record");
            List<StaUserMileageRecord> mileageRecords = staUserMileageRecordMapper.selectByLastId(mileagePosition.getLastPkId());
            uids.addAll(mileageRecords.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(mileageRecords.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));

            // 临时组
            StaParsePosition tmpgroupPosition = positionMap.get("sta_user_tmpgroup_record");
            List<StaUserTmpgroupRecord> tmpgroupRecords = staUserTmpgroupRecordMapper.selectByLastId(tmpgroupPosition.getLastPkId());
            uids.addAll(tmpgroupRecords.stream().map(v -> v.getUid()).collect(Collectors.toList()));
            dates.addAll(tmpgroupRecords.stream().map(v -> v.getTimeYmd()).collect(Collectors.toList()));
        }
        String uidIn = uids.stream().distinct().map(v -> v.toString()).collect(Collectors.joining(","));
        logger.info("总计的用户uid为：" + uidIn);
        dates = dates.stream().distinct().sorted().collect(Collectors.toList());
        logger.info("总计的日期为：" + JSON.toJSONString(dates));
        if (StringUtils.isEmpty(uidIn)) {
            return;
        }
        Integer max = 0;
        if (dates.size() > 0) {
            max = dates.get(0);
            /*long v1 = ymd.get().parse(dates.get(0).toString()).getTime();
            long sys = System.currentTimeMillis();
            if (sys - v1 > (1000 * 60 * 60 * 24 * 7)) {// 最多七天数据
                int m = Integer.parseInt(ymd.get().format(new Date(sys)));
                max = Integer.parseInt(this.timeAddDate(ymd.get(), m, -6));
            }*/
        }
        List<Long> updateIds = new ArrayList<>();
        // 查询出数据库中所有用户每天的信息
        logger.info("查询信息为：uidIn={}，max={}", uidIn, max);
        List<StaDayUser> listAllByDay = staDayUserMapper.selectByUidIn(uidIn, max);// 如果有用户uid 肯定会存在dates
        Map<String, StaDayUser> allUserMap = listAllByDay.stream().collect(Collectors.toMap(v -> v.getUid() + v.getTimeYmd().toString(), v -> v));

        List<StaDayUser> listAllByDayClone = new ArrayList<>();
        Map<String, StaDayUser> userAllCloneMap = new HashMap<>();
        try {
            listAllByDayClone = this.deepCopyForUser(listAllByDay);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("clone 用户时发生异常：" + e.getMessage());
            listAllByDayClone = staDayUserMapper.selectByUidIn(uidIn, max);// 查询两次的作用是用来做比较
        }
        userAllCloneMap = listAllByDayClone.stream().collect(Collectors.toMap(v -> v.getUid() + v.getTimeYmd().toString(), Function.identity()));

        // 查询出数据库中所有部门信息
        List<StaDayDept> deptAll = staDayDeptMapper.selectByTimeYmd(max.toString());
        Map<String, StaDayDept> allDeptMap = deptAll.stream().collect(Collectors.toMap(v -> v.getDeptUniqueId() + v.getTimeYmd(), v -> v));

        List<StaDayDept> deptAllClone = new ArrayList<>();
        Map<String, StaDayDept> deptAllCloneMap = new HashMap<>();
        try {
            deptAllClone = this.deepCopyForDept(deptAll);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("clone 部门时发生异常：" + e.getMessage());
            deptAllClone = staDayDeptMapper.selectByTimeYmd(max.toString());// 查询两次的作用是用来做比较
        }
        deptAllCloneMap = deptAllClone.stream().collect(Collectors.toMap(v -> v.getDeptUniqueId() + v.getTimeYmd(), Function.identity()));

        // 用户在线
        StaParsePosition onlinePosition = positionMap.get("sta_user_online_record");// 获取上次获得的部门用户在线时长last_id
        List<StaUserOnlineRecord> onlineState1 = staUserOnlineRecordMapper.selectByEndFlagAndLastIdLimit(null, onlinePosition.getLastPkId());// end_flag = 1
        onlineState1 = onlineState1.stream().filter(v -> v.getReal() != 1).collect(Collectors.toList());// 在线时长只计算real不等于1的数据
        if (onlineState1.size() > 0) {
            onlinePosition.setLastPkId(onlineState1.get(onlineState1.size() - 1).getId());
        }
        onlineState1 = onlineState1.stream().filter(v -> v.getEndFlag() == 1).collect(Collectors.toList());

        List<StaUserOnlineRecord> onlineState3 = staUserOnlineRecordMapper.selectByEndFlagAndLastId(3, null);// end_flag = 3
        onlineState3 = onlineState3.stream().filter(v -> v.getReal() != 1).collect(Collectors.toList());// 在线时长只计算real不等于1的数据

        List<StaUserOnlineRecord> onlineState4 = staUserOnlineRecordMapper.selectByEndFlagAndLastId(4, null);// end_flag = 4
        onlineState4 = onlineState4.stream().filter(v -> v.getReal() != 1).collect(Collectors.toList());// 在线时长只计算real不等于1的数据

        this.dealOnline(onlineState1, listAllByDay, allUserMap, deptAll, allDeptMap,1, updateIds);
        List<StaUserOnlineRecord> needUpdateUserLastTime = this.dealOnline(onlineState3, listAllByDay, allUserMap, deptAll, allDeptMap,3, updateIds);
        List<StaUserOnlineRecord> needUpdateUserOnline = this.dealOnline(onlineState4, listAllByDay, allUserMap, deptAll, allDeptMap,4, updateIds);
        for (StaUserOnlineRecord staUserOnlineRecord : needUpdateUserLastTime) {
            staUserOnlineRecordMapper.updateByPrimaryKeySelective(staUserOnlineRecord.setLastTime(new Date()));
        }
        if (needUpdateUserOnline.size() > 0) {
            String ids = needUpdateUserOnline.stream().map(v -> v.getId().toString()).collect(Collectors.joining(","));
            if (StringUtils.isNotEmpty(ids)) {
                staUserOnlineRecordMapper.updateEndFlagById(ids);
            }
        }

        // 语音通话
        StaParsePosition audioPosition = positionMap.get("sta_user_audio_record");// 获取上次获得的部门用户在线时长last_id
        List<StaUserAudioRecord> audioState1 = staUserAudioRecordMapper.selectByEndFlagAndLastIdLimit(null, audioPosition.getLastPkId());// end_flag = 1
        if (audioState1.size() > 0) {
            audioPosition.setLastPkId(audioState1.get(audioState1.size() - 1).getId());
        }
        audioState1 = audioState1.stream().filter(v -> v.getEndFlag() == 1).collect(Collectors.toList());
        List<StaUserAudioRecord> audioState3 = staUserAudioRecordMapper.selectByEndFlagAndLastId(3, null);// end_flag = 3
        List<StaUserAudioRecord> audioState4 = staUserAudioRecordMapper.selectByEndFlagAndLastId(4, null);// end_flag = 4

        this.dealAudio(audioState1, listAllByDay, allUserMap, deptAll, allDeptMap,1, updateIds);
        List<StaUserAudioRecord> needUpdateAudioLastTime = this.dealAudio(audioState3, listAllByDay, allUserMap, deptAll, allDeptMap,3, updateIds);
        List<StaUserAudioRecord> needUpdateAudioOnline = this.dealAudio(audioState4, listAllByDay, allUserMap, deptAll, allDeptMap,4, updateIds);

        for (StaUserAudioRecord audioRecord : needUpdateAudioLastTime) {
            staUserAudioRecordMapper.updateByPrimaryKeySelective(audioRecord.setLastTime(new Date()));
        }
        if (needUpdateAudioOnline.size() > 0) {
            String ids = needUpdateAudioOnline.stream().map(v -> v.getId().toString()).collect(Collectors.joining(","));
            if (StringUtils.isNotEmpty(ids)) {
                staUserAudioRecordMapper.updateEndFlagById(ids);
            }
        }


        // 视频通话
        StaParsePosition videoPosition = positionMap.get("sta_user_video_record");// 获取上次获得的部门用户在线时长last_id
        List<StaUserVideoRecord> videoState1 = staUserVideoRecordMapper.selectByEndFlagAndLastIdLimit(null, videoPosition.getLastPkId());// end_flag = 1
        if (videoState1.size() > 0) {
            videoPosition.setLastPkId(videoState1.get(videoState1.size() - 1).getId());
        }
        videoState1 = videoState1.stream().filter(v -> v.getEndFlag() == 1).collect(Collectors.toList());
        List<StaUserVideoRecord> videoState3 = staUserVideoRecordMapper.selectByEndFlagAndLastId(3, null);// end_flag = 3
        List<StaUserVideoRecord> videoState4 = staUserVideoRecordMapper.selectByEndFlagAndLastId(4, null);// end_flag = 4

        this.dealVideo(videoState1, listAllByDay, allUserMap, deptAll, allDeptMap, 1, updateIds);
        List<StaUserVideoRecord> needUpdateVideoLastTime = this.dealVideo(videoState3, listAllByDay, allUserMap, deptAll, allDeptMap, 3, updateIds);
        List<StaUserVideoRecord> needUpdateVideoOnline = this.dealVideo(videoState4, listAllByDay, allUserMap, deptAll, allDeptMap, 4, updateIds);

        for (StaUserVideoRecord videoRecord : needUpdateVideoLastTime) {
            staUserVideoRecordMapper.updateByPrimaryKeySelective(videoRecord.setLastTime(new Date()));
        }
        if (needUpdateVideoOnline.size() > 0) {
            String ids = needUpdateVideoOnline.stream().map(v -> v.getId().toString()).collect(Collectors.joining(","));
            if (StringUtils.isNotEmpty(ids)) {
                staUserVideoRecordMapper.updateEndFlagById(ids);
            }
        }

        // 报警
        StaParsePosition sosPosition = positionMap.get("sta_user_sos_record");
        List<StaUserSosRecord> sosRecords = staUserSosRecordMapper.selectByLastId(sosPosition.getLastPkId());
        for (StaUserSosRecord sosRecord : sosRecords) {
            StaDayUser user = allUserMap.get(sosRecord.getUid() + sosRecord.getTimeYmd().toString());
            String uniqueIds;
            if (user == null) {
                user = new StaDayUser();
                user.setCorpid(sosRecord.getCorpid())
                        .setUid(sosRecord.getUid())
                        .setName(sosRecord.getName())
                        .setTimeYmd(sosRecord.getTimeYmd())
                        .setDeptUniqueId(sosRecord.getDeptUniqueId())
                        .setDeptName(sosRecord.getDeptName())
                        .setSosCount(1)
                        .setState(sosRecord.getState());
                listAllByDay.add(user);
                allUserMap.put(user.getUid() + user.getTimeYmd().toString(), user);
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
            } else {
                updateIds.add(user.getId());
                user.setSosCount(user.getSosCount() + 1);
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
            }
            if (StringUtils.isNotEmpty(uniqueIds)) {
                List<String> list = Arrays.asList(uniqueIds.split(",")).stream().distinct().collect(Collectors.toList());
                for (String s : list) {
                    StaDayDept dayDept = allDeptMap.get(s + user.getTimeYmd());
                    if (dayDept == null) {
                        dayDept = new StaDayDept();
                        dayDept.setCorpid(user.getCorpid())
                                .setTimeYmd(sosRecord.getTimeYmd())
                                .setDeptUniqueId(s)
                                .setDeptName(rtvUnitMapper.selectNameByUniqueId(s))
                                .setSosCount(1);
                        deptAll.add(dayDept);
                        allDeptMap.put(dayDept.getDeptUniqueId() + dayDept.getTimeYmd(), dayDept);
                    } else {
                        dayDept.setSosCount(dayDept.getSosCount() + 1);
                    }
                }
            }
        }
        if (sosRecords.size() > 0) {
            sosPosition.setLastPkId(sosRecords.get(sosRecords.size() - 1).getId());
        }

        // 照片回传
        StaParsePosition photoBackPosition = positionMap.get("sta_user_photo_record");
        List<StaUserPhotoRecord> photoRecords = staUserPhotoRecordMapper.selectByLastId(photoBackPosition.getLastPkId());
        for (StaUserPhotoRecord photoRecord : photoRecords) {
            StaDayUser user = allUserMap.get(photoRecord.getUid() + photoRecord.getTimeYmd().toString());
            String uniqueIds;
            if (user == null) {
                user = new StaDayUser();
                user.setCorpid(photoRecord.getCorpid())
                        .setUid(photoRecord.getUid())
                        .setName(photoRecord.getName())
                        .setTimeYmd(photoRecord.getTimeYmd())
                        .setDeptUniqueId(photoRecord.getDeptUniqueId())
                        .setDeptName(photoRecord.getDeptName())
                        .setPhotoUploadCount(1)
                        .setState(photoRecord.getState());
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
                listAllByDay.add(user);
                allUserMap.put(photoRecord.getUid() + photoRecord.getTimeYmd().toString(), user);
            } else {
                updateIds.add(user.getId());
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
                user.setPhotoUploadCount(user.getPhotoUploadCount() + 1);
            }
            if (StringUtils.isNotEmpty(uniqueIds)) {
                List<String> list = Arrays.asList(uniqueIds.split(",")).stream().distinct().collect(Collectors.toList());
                for (String s : list) {
                    StaDayDept dayDept = allDeptMap.get(s + user.getTimeYmd());
                    if (dayDept == null) {
                        dayDept = new StaDayDept();
                        dayDept.setCorpid(user.getCorpid())
                                .setTimeYmd(photoRecord.getTimeYmd())
                                .setDeptUniqueId(s)
                                .setDeptName(rtvUnitMapper.selectNameByUniqueId(s))
                                .setPhotoUploadCount(1);
                        deptAll.add(dayDept);
                        allDeptMap.put(dayDept.getDeptUniqueId() + dayDept.getTimeYmd(), dayDept);
                    } else {
                        dayDept.setPhotoUploadCount(dayDept.getPhotoUploadCount() + 1);
                    }
                }
            }
        }
        if (photoRecords.size() > 0) {
            photoBackPosition.setLastPkId(photoRecords.get(photoRecords.size() - 1).getId());
        }

        // 即时消息
        StaParsePosition imPosition = positionMap.get("sta_user_im_record");
        List<StaUserImRecord> imRecords = staUserImRecordMapper.selectByLastId(imPosition.getLastPkId());
        for (StaUserImRecord imRecord : imRecords) {
            StaDayUser user = allUserMap.get(imRecord.getUid() + imRecord.getTimeYmd().toString());
            String uniqueIds;
            if (user == null) {
                user = new StaDayUser();
                user.setCorpid(imRecord.getCorpid())
                        .setUid(imRecord.getUid())
                        .setName(imRecord.getName())
                        .setTimeYmd(imRecord.getTimeYmd())
                        .setDeptUniqueId(imRecord.getDeptUniqueId())
                        .setDeptName(imRecord.getDeptName())
                        .setState(imRecord.getState());
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
                if (imRecord.getType() == 1) {
                    user.setIndividualImCount(1);
                } else {
                    user.setGroupImCount(1);
                }
                user.setImCount(user.getIndividualImCount() + user.getGroupImCount());
                listAllByDay.add(user);
                allUserMap.put(imRecord.getUid() + imRecord.getTimeYmd().toString(), user);
            } else {
                updateIds.add(user.getId());
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
                if (imRecord.getType() == 1) {
                    user.setIndividualImCount(user.getIndividualImCount() + 1);
                } else {
                    user.setGroupImCount(user.getGroupImCount() + 1);
                }
                user.setImCount(user.getIndividualImCount() + user.getGroupImCount());
            }
            if (StringUtils.isNotEmpty(uniqueIds)) {
                List<String> list = Arrays.asList(uniqueIds.split(",")).stream().distinct().collect(Collectors.toList());
                for (String s : list) {
                    StaDayDept dayDept = allDeptMap.get(s + user.getTimeYmd());
                    if (dayDept == null) {
                        dayDept = new StaDayDept();
                        dayDept.setCorpid(user.getCorpid())
                                .setTimeYmd(imRecord.getTimeYmd())
                                .setDeptUniqueId(s)
                                .setDeptName(rtvUnitMapper.selectNameByUniqueId(s));
                        if (imRecord.getType() == 1) {
                            dayDept.setIndividualImCount(1);
                        } else {
                            dayDept.setGroupImCount(1);
                        }
                        dayDept.setImCount(dayDept.getIndividualImCount() + dayDept.getGroupImCount());
                        deptAll.add(dayDept);
                        allDeptMap.put(dayDept.getDeptUniqueId() + dayDept.getTimeYmd(), dayDept);
                    } else {
                        if (imRecord.getType() == 1) {
                            dayDept.setIndividualImCount(dayDept.getIndividualImCount() + 1);
                        } else {
                            dayDept.setGroupImCount(dayDept.getGroupImCount() + 1);
                        }
                        dayDept.setImCount(dayDept.getIndividualImCount() + dayDept.getGroupImCount());
                    }
                }
            }
        }
        if (imRecords.size() > 0) {
            imPosition.setLastPkId(imRecords.get(imRecords.size() - 1).getId());
        }

        // 里程数
        StaParsePosition mileagePosition = positionMap.get("sta_user_mileage_record");
        List<StaUserMileageRecord> mileageRecords = staUserMileageRecordMapper.selectByLastId(mileagePosition.getLastPkId());
        for (StaUserMileageRecord mileageRecord : mileageRecords) {
            StaDayUser user = allUserMap.get(mileageRecord.getUid() + mileageRecord.getTimeYmd().toString());
            String uniqueIds;
            if (user == null) {
                user = new StaDayUser();
                user.setCorpid(mileageRecord.getCorpid())
                        .setUid(mileageRecord.getUid())
                        .setName(mileageRecord.getName())
                        .setTimeYmd(mileageRecord.getTimeYmd())
                        .setDeptUniqueId(mileageRecord.getDeptUniqueId())
                        .setDeptName(mileageRecord.getDeptName())
                        .setMileage(mileageRecord.getMileage())
                        .setState(mileageRecord.getState());
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
                listAllByDay.add(user);
                allUserMap.put(user.getUid() + user.getTimeYmd().toString(), user);
            } else {
                updateIds.add(user.getId());
                user.setMileage(user.getMileage() + mileageRecord.getMileage());
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
            }
            if (StringUtils.isNotEmpty(uniqueIds)) {
                List<String> list = Arrays.asList(uniqueIds.split(",")).stream().distinct().collect(Collectors.toList());
                for (String s : list) {
                    StaDayDept dayDept = allDeptMap.get(s + user.getTimeYmd());
                    if (dayDept == null) {
                        dayDept = new StaDayDept();
                        dayDept.setCorpid(user.getCorpid())
                                .setTimeYmd(mileageRecord.getTimeYmd())
                                .setDeptUniqueId(s)
                                .setDeptName(rtvUnitMapper.selectNameByUniqueId(s))
                                .setMileage(mileageRecord.getMileage());
                        deptAll.add(dayDept);
                        allDeptMap.put(dayDept.getDeptUniqueId() + dayDept.getTimeYmd(), dayDept);
                    } else {
                        dayDept.setMileage(dayDept.getMileage() + mileageRecord.getMileage());
                    }
                }
            }
        }
        if (mileageRecords.size() > 0) {
            mileagePosition.setLastPkId(mileageRecords.get(mileageRecords.size() - 1).getId());
        }

        // 临时组
        StaParsePosition tmpgroupPosition = positionMap.get("sta_user_tmpgroup_record");
        List<StaUserTmpgroupRecord> tmpgroupRecords = staUserTmpgroupRecordMapper.selectByLastId(tmpgroupPosition.getLastPkId());
        for (StaUserTmpgroupRecord tmpgroupRecord : tmpgroupRecords) {
            StaDayUser user = allUserMap.get(tmpgroupRecord.getUid() + tmpgroupRecord.getTimeYmd().toString());
            String uniqueIds;
            if (user == null) {
                user = new StaDayUser();
                user.setCorpid(tmpgroupRecord.getCorpid())
                        .setUid(tmpgroupRecord.getUid())
                        .setName(tmpgroupRecord.getName())
                        .setTimeYmd(tmpgroupRecord.getTimeYmd())
                        .setDeptUniqueId(tmpgroupRecord.getDeptUniqueId())
                        .setDeptName(tmpgroupRecord.getDeptName())
                        .setTmpGroupCount(1)
                        .setState(tmpgroupRecord.getState());
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
                listAllByDay.add(user);
                allUserMap.put(user.getUid() + user.getTimeYmd().toString(), user);
            } else {
                updateIds.add(user.getId());
                user.setTmpGroupCount(user.getTmpGroupCount() + 1);
                uniqueIds = rtvUnitMapper.selectCommonUniqueUp(user.getDeptUniqueId());
            }
            if (StringUtils.isNotEmpty(uniqueIds)) {
                List<String> list = Arrays.asList(uniqueIds.split(",")).stream().distinct().collect(Collectors.toList());
                for (String s : list) {
                    StaDayDept dayDept = allDeptMap.get(s + user.getTimeYmd());
                    if (dayDept == null) {
                        dayDept = new StaDayDept();
                        dayDept.setCorpid(user.getCorpid())
                                .setTimeYmd(tmpgroupRecord.getTimeYmd())
                                .setDeptUniqueId(s)
                                .setDeptName(rtvUnitMapper.selectNameByUniqueId(s))
                                .setTmpGroupCount(1);
                        deptAll.add(dayDept);
                        allDeptMap.put(dayDept.getDeptUniqueId() + dayDept.getTimeYmd(), dayDept);
                    } else {
                        dayDept.setTmpGroupCount(dayDept.getTmpGroupCount() + 1);
                    }
                }
            }
        }

        if (tmpgroupRecords.size() > 0) {
            tmpgroupPosition.setLastPkId(tmpgroupRecords.get(tmpgroupRecords.size() - 1).getId());
        }

        List<StaDayUser> insertList = listAllByDay.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
        logger.info("------- start user -------");
        if (insertList.size() > 0) {
            int insertSize = 500;
            int maxSize = insertList.size() - 1;
            List<StaDayUser> newList = new ArrayList<>();
            for (int i = 0; i < insertList.size(); i++) {
                newList.add(insertList.get(i));
                if (insertSize == newList.size() || i == maxSize) {
                    staDayUserMapper.insertList(newList);
                    newList.clear();// 批量插入完成后，清理之前的数据
                }
            }
        }
        Map<String, StaDayUser> finalUserAllCloneMap = userAllCloneMap;
        List<StaDayUser> updateList = listAllByDay.stream().filter(v -> {
            StaDayUser staDayUserClone = finalUserAllCloneMap.get(v.getUid() + v.getTimeYmd().toString());
            if (staDayUserClone != null) {
                boolean flag = false;
                if (!staDayUserClone.getCorpid().equals(v.getCorpid())) {
                    flag = true;
                } else {
                    staDayUserClone.setCorpid(0);
                }
                if (staDayUserClone.getOnlineDuration() != v.getOnlineDuration()) {
                    flag = true;
                } else {
                    staDayUserClone.setOnlineDuration(0);
                }
                if (staDayUserClone.getTalkCount() != v.getTalkCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setTalkCount(0);
                }
                if (staDayUserClone.getIndividualTalkCount() != v.getIndividualTalkCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setIndividualTalkCount(0);
                }
                if (staDayUserClone.getGroupTalkCount() != v.getGroupTalkCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setGroupTalkCount(0);
                }
                if (staDayUserClone.getTalkDuration() != v.getTalkDuration()) {
                    flag = true;
                } else {
                    staDayUserClone.setTalkDuration(0);
                }
                if (staDayUserClone.getIndividualTalkDuration() != v.getIndividualTalkDuration()) {
                    flag = true;
                } else {
                    staDayUserClone.setIndividualTalkDuration(0);
                }
                if (staDayUserClone.getGroupTalkDuration() != v.getGroupTalkDuration()) {
                    flag = true;
                } else {
                    staDayUserClone.setGroupTalkDuration(0);
                }
                if (staDayUserClone.getVideoAllCount() != v.getVideoAllCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setVideoAllCount(0);
                }
                if (staDayUserClone.getVideoCallCount() != v.getVideoCallCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setVideoCallCount(0);
                }
                if (staDayUserClone.getVideoUploadCount() != v.getVideoUploadCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setVideoUploadCount(0);
                }
                if (staDayUserClone.getVideoRollcallCount() != v.getVideoRollcallCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setVideoRollcallCount(0);
                }
                if (staDayUserClone.getVideoConfCount() != v.getVideoConfCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setVideoConfCount(0);
                }
                if (staDayUserClone.getVideoDuration() != v.getVideoDuration()) {
                    flag = true;
                } else {
                    staDayUserClone.setVideoDuration(0);
                }
                if (staDayUserClone.getSosCount() != v.getSosCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setSosCount(0);
                }
                if (staDayUserClone.getPhotoUploadCount() != v.getPhotoUploadCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setPhotoUploadCount(0);
                }
                if (staDayUserClone.getImCount() != v.getImCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setImCount(0);
                }
                if (staDayUserClone.getIndividualImCount() != v.getIndividualImCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setIndividualImCount(0);
                }
                if (staDayUserClone.getGroupImCount() != v.getGroupImCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setGroupImCount(0);
                }
                if (staDayUserClone.getOnpostDuration() != v.getOnpostDuration()) {
                    flag = true;
                } else {
                    staDayUserClone.setOnlineDuration(0);
                }
                if (staDayUserClone.getMileage() != v.getMileage()) {
                    flag = true;
                } else {
                    staDayUserClone.setMileage(0);
                }
                if (staDayUserClone.getTmpGroupCount() != v.getTmpGroupCount()) {
                    flag = true;
                } else {
                    staDayUserClone.setTmpGroupCount(0);
                }
                if (staDayUserClone.getTmpGroupFileCount() != v.getTmpGroupFileCount()) {
                    flag = true;
                } else  {
                    staDayUserClone.setTmpGroupFileCount(0);
                }
                if (staDayUserClone.getState() != v.getState()) {
                    flag = true;
                } else {
                    staDayUserClone.setState(0);
                }
                return flag;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        logger.info("需要更新的用户信息总数量为：" + updateList.size());
        updateList.forEach(v -> {
            staDayUserMapper.updateByPrimaryKeySelective(v);
        });
        logger.info("------- end user -------");


        logger.info("------- start dept -------");
        // 新增
        List<StaDayDept> insertDeptList = deptAll.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
        if (insertDeptList.size() > 0) {
            int insertSize = 500;
            int maxSize = insertDeptList.size() - 1;
            List<StaDayDept> newList = new ArrayList<>();
            for (int i = 0; i < insertDeptList.size(); i++) {
                newList.add(insertDeptList.get(i));
                if (insertSize == newList.size() || i == maxSize) {
                    staDayDeptMapper.insertBatch(newList);
                    newList.clear();// 批量插入完成后，清理之前的数据
                }
            }
        }
        // 修改
        Map<String, StaDayDept> finalDeptAllCloneMap = deptAllCloneMap;

        List<StaDayDept> updateDeptList = deptAll.stream().filter(v -> {
            StaDayDept staDayDept = finalDeptAllCloneMap.get(v.getDeptUniqueId() + v.getTimeYmd());
            if (staDayDept != null) {
                boolean flag = false;
                if (!staDayDept.getCorpid().equals(v.getCorpid())) {
                    flag = true;
                } else {
                    staDayDept.setCorpid(0);
                }
                if (staDayDept.getOnlineDuration() != v.getOnlineDuration()) {
                    flag = true;
                } else {
                    staDayDept.setOnlineDuration(0);
                }
                if (staDayDept.getTalkCount() != v.getTalkCount()) {
                    flag = true;
                } else {
                    staDayDept.setTalkCount(0);
                }
                if (staDayDept.getIndividualTalkCount() != v.getIndividualTalkCount()) {
                    flag = true;
                } else {
                    staDayDept.setIndividualTalkCount(0);
                }
                if (staDayDept.getGroupTalkCount() != v.getGroupTalkCount()) {
                    flag = true;
                } else {
                    staDayDept.setGroupTalkCount(0);
                }
                if (staDayDept.getTalkDuration() != v.getTalkDuration()) {
                    flag = true;
                } else {
                    staDayDept.setTalkDuration(0);
                }
                if (staDayDept.getIndividualTalkDuration() != v.getIndividualTalkDuration()) {
                    flag = true;
                } else {
                    staDayDept.setIndividualTalkDuration(0);
                }
                if (staDayDept.getGroupTalkDuration() != v.getGroupTalkDuration()) {
                    flag = true;
                } else {
                    staDayDept.setGroupTalkDuration(0);
                }
                if (staDayDept.getVideoAllCount() != v.getVideoAllCount()) {
                    flag = true;
                } else {
                    staDayDept.setVideoAllCount(0);
                }
                if (staDayDept.getVideoCallCount() != v.getVideoCallCount()) {
                    flag = true;
                } else {
                    staDayDept.setVideoCallCount(0);
                }
                if (staDayDept.getVideoUploadCount() != v.getVideoUploadCount()) {
                    flag = true;
                } else {
                    staDayDept.setVideoUploadCount(0);
                }
                if (staDayDept.getVideoRollcallCount() != v.getVideoRollcallCount()) {
                    flag = true;
                } else {
                    staDayDept.setVideoRollcallCount(0);
                }
                if (staDayDept.getVideoConfCount() != v.getVideoConfCount()) {
                    flag = true;
                } else {
                    staDayDept.setVideoConfCount(0);
                }
                if (staDayDept.getVideoDuration() != v.getVideoDuration()) {
                    flag = true;
                } else {
                    staDayDept.setVideoDuration(0);
                }
                if (staDayDept.getSosCount() != v.getSosCount()) {
                    flag = true;
                } else {
                    staDayDept.setSosCount(0);
                }
                if (staDayDept.getPhotoUploadCount() != v.getPhotoUploadCount()) {
                    flag = true;
                } else {
                    staDayDept.setPhotoUploadCount(0);
                }
                if (staDayDept.getImCount() != v.getImCount()) {
                    flag = true;
                } else {
                    staDayDept.setImCount(0);
                }
                if (staDayDept.getIndividualImCount() != v.getIndividualImCount()) {
                    flag = true;
                } else {
                    staDayDept.setIndividualImCount(0);
                }
                if (staDayDept.getGroupImCount() != v.getGroupImCount()) {
                    flag = true;
                } else {
                    staDayDept.setGroupImCount(0);
                }
                if (staDayDept.getOnpostDuration() != v.getOnpostDuration()) {
                    flag = true;
                } else {
                    staDayDept.setOnlineDuration(0);
                }
                if (staDayDept.getMileage() != v.getMileage()) {
                    flag = true;
                } else {
                    staDayDept.setMileage(0);
                }
                if (staDayDept.getTmpGroupCount() != v.getTmpGroupCount()) {
                    flag = true;
                } else {
                    staDayDept.setTmpGroupCount(0);
                }
                if (staDayDept.getTmpGroupFileCount() != v.getTmpGroupFileCount()) {
                    flag = true;
                } else  {
                    staDayDept.setTmpGroupFileCount(0);
                }
                if (staDayDept.getState() != v.getState()) {
                    flag = true;
                } else {
                    staDayDept.setState(0);
                }
                return flag;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        logger.info("需要更新的部门信息总数量为：" + updateDeptList.size());
        updateDeptList.forEach(v -> staDayDeptMapper.updateByPrimaryKeySelective(v));
        logger.info("------- end dept -------");

        // 修改position位置
        positions.forEach(v -> staParsePositionMapper.updateByPrimaryKeySelective(v));

    }



    /**
     * 处理用户在线统计信息
     * @param onlineState   所有endFlag状态为1、3、4的数据
     * @param listAllByDay  所有用户每天的数据
     * @param allUserMap    所有用户每天数据的map集合
     * @param deptAll       所有部门每天的数据
     * @param allDeptMap    所有部门每天数据的map集合
     * @param endFlag       表示所处理数据的状态  1：代表正常数据，有开始和结束，没有被统计过
     *                                            3：代表又开始没有结束的数据，可以被统计过
     *                                            4：代表可能被统计过得，又开始和结束
     * @return  当状态为4时，才有返回信息，返回的数据为需要修改的用户在线状态信息 endFlag设置为1
     */
    public List<StaUserOnlineRecord> dealOnline (List<StaUserOnlineRecord> onlineState,
                                                 List<StaDayUser> listAllByDay,
                                                 Map<String, StaDayUser> allUserMap,
                                                 List<StaDayDept> deptAll,
                                                 Map<String, StaDayDept> allDeptMap,
                                                 int endFlag,
                                                 List<Long> updateIds) {
        List<StaUserOnlineRecord> needUpdateState = new ArrayList<>();
        onlineState.forEach(v -> {
            String start = ymd.get().format(v.getLogonTime());
            String end = (endFlag == 3) ? start : ymd.get().format(v.getLogoffTime());// endFlag 为3时，没有退出时间，直接以当前时间作为结束时间
            LocalDate stDate = LocalDate.of(Integer.parseInt(start.substring(0,4)),
                    Integer.parseInt(start.substring(4, 6)),
                    Integer.parseInt(start.substring(6, 8)));

            LocalDate etDate = LocalDate.of(Integer.parseInt(end.substring(0,4)),
                    Integer.parseInt(end.substring(4, 6)),
                    Integer.parseInt(end.substring(6, 8)));
            long f = etDate.toEpochDay() - stDate.toEpochDay() + 1;
            if (f > 0) {
                LocalTime localTime = LocalTime.of(23, 59, 59);
                LocalDateTime localDateTime = LocalDateTime.of(etDate, localTime);
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
                Date date = Date.from(zonedDateTime.toInstant());
                Date startTime;
                Date endTime;
                if (endFlag != 1) {
                    needUpdateState.add(v);
                }
                for (int i = 0; i < f; i++) {
                    try {
                        if (endFlag == 1) {
                            startTime = (i == 0) ? v.getLogonTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            endTime = (i == (f - 1)) ? v.getLogoffTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        } else if (endFlag == 3) {
                            if (v.getLastTime() == null) {
                                startTime = (i == 0) ? v.getLogonTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            } else {
                                startTime = (i == (f - 1)) ? v.getLastTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            }
                            endTime = (i == (f - 1)) ? v.getLogonTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        } else {
                            if (v.getLastTime() == null) {
                                startTime = (i == 0) ? v.getLogonTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            } else {
                                startTime = (i == (f - 1) &&
                                        ymd.get().format(v.getLogonTime()).equals(ymd.get().format(v.getLogoffTime())))
                                        ? v.getLastTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            }
                            endTime = (i == (f - 1)) ? v.getLogoffTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        }
                        Long sec = (endTime.getTime() - startTime.getTime()) / 1000;
                        logger.info("用户ID：{}, sec: {}, startTime: {}, endTime: {}, endFlag: {}", v.getUid(), sec, sdf.get().format(startTime), sdf.get().format(endTime), endFlag);
                        while (sec > 86400) {// 如果时间超过86400秒，说明是最后一次获取到跨天的退出
                            sec -= 86400;
                        }
                        if (sec < 0 && endFlag == 1) {
                            sec = 0l;
                        }
                        StaDayUser staDayUser = allUserMap.get(v.getUid() + timeAddDate(ymd.get(), v.getTimeYmd(), i));
                        if (staDayUser != null) {
                            updateIds.add(staDayUser.getId());
                            if (v.getLastTime() != null) {
                                if (i == (f - 1)) {// 增量计算
                                    staDayUser.setOnlineDuration((staDayUser.getOnlineDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayUser.getOnlineDuration() + sec.intValue());
                                    if (v.getIsOnduty() == 1) {
                                        staDayUser.setOnpostDuration((staDayUser.getOnpostDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayUser.getOnpostDuration() + sec.intValue());
                                    }
                                }
                            } else {
                                staDayUser.setOnlineDuration((staDayUser.getOnlineDuration() + sec.intValue()) < 0 ? 0 :
                                        staDayUser.getOnlineDuration() + sec.intValue());
                                if (v.getIsOnduty() == 1) {
                                    staDayUser.setOnpostDuration((staDayUser.getOnpostDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayUser.getOnpostDuration() + sec.intValue());
                                }
                            }
                        } else {
                            staDayUser = new StaDayUser();
                            staDayUser.setCorpid(v.getCorpid())
                                    .setUid(v.getUid())
                                    .setName(v.getName())
                                    .setTimeYmd(Integer.parseInt(timeAddDate(ymd.get(), v.getTimeYmd(), i)))
                                    .setDeptUniqueId(v.getDeptUniqueId())
                                    .setDeptName(v.getDeptName())
                                    .setOnlineDuration(sec.intValue() > 0 ? sec.intValue() : 0)
                                    .setOnpostDuration(v.getIsOnduty() == 1 ? sec.intValue() : 0)
                                    .setOnpostDuration(v.getIsOnduty() == 1 ? sec.intValue() : 0)
                                    .setState(v.getState());
                            if (staDayUser.getOnpostDuration() < 0) {
                                staDayUser.setOnpostDuration(0);
                            }
                            // 新增对象后放置到集合中存储
                            listAllByDay.add(staDayUser);
                            allUserMap.put(staDayUser.getUid() + staDayUser.getTimeYmd().toString(), staDayUser);
                        }
                        String parent = rtvUnitMapper.selectCommonUniqueUp(staDayUser.getDeptUniqueId());// 查询上级部门
                        List<String> listParent = Arrays.asList(parent.split(",")).stream().distinct().collect(Collectors.toList());
                        for (String s : listParent) {
                            StaDayDept staDayDept = allDeptMap.get(s + timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            if (staDayDept != null) {
                                if (v.getLastTime() != null) {
                                    if (i == (f - 1)) {// 增量计算
                                        staDayDept.setOnlineDuration((staDayDept.getOnlineDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayDept.getOnlineDuration() + sec.intValue());
                                        if (v.getIsOnduty() == 1) {
                                            staDayDept.setOnpostDuration((staDayDept.getOnpostDuration() + sec.intValue()) < 0 ? 0 :
                                                    staDayDept.getOnpostDuration() + sec.intValue());
                                        }
                                    }
                                } else {
                                    staDayDept.setOnlineDuration((staDayDept.getOnlineDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayDept.getOnlineDuration() + sec.intValue());
                                    if (v.getIsOnduty() == 1) {
                                        staDayDept.setOnpostDuration((staDayDept.getOnpostDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayDept.getOnpostDuration() + sec.intValue());
                                    }
                                }
                            } else {
                                staDayDept = new StaDayDept();
                                String deptName = rtvUnitMapper.selectNameByUniqueId(s);
                                staDayDept.setCorpid(v.getCorpid())
                                        .setTimeYmd(Integer.parseInt(timeAddDate(ymd.get(), v.getTimeYmd(), i)))
                                        .setDeptUniqueId(s)
                                        .setDeptName(deptName)
                                        .setOnlineDuration(sec.intValue() > 0 ? sec.intValue() : 0)
                                        .setOnpostDuration(v.getIsOnduty() == 1 ? sec.intValue() : 0);
                                if (staDayDept.getOnpostDuration() < 0) {
                                    staDayDept.setOnpostDuration(0);
                                }
                                // 新增加的部门需要存放到集合中
                                deptAll.add(staDayDept);
                                allDeptMap.put(staDayDept.getDeptUniqueId() + staDayDept.getTimeYmd().toString(), staDayDept);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        return needUpdateState;
    }


    /**
     * 处理用户语音统计信息
     * @param onlineState   所有endFlag状态为1、3、4的数据
     * @param listAllByDay  所有用户每天的数据
     * @param allUserMap    所有用户每天数据的map集合
     * @param deptAll       所有部门每天的数据
     * @param allDeptMap    所有部门每天数据的map集合
     * @param endFlag       表示所处理数据的状态  1：代表正常数据，有开始和结束，没有被统计过
     *                                            3：代表又开始没有结束的数据，可以被统计过
     *                                            4：代表可能被统计过得，又开始和结束
     * @return  当状态为4时，才有返回信息，返回的数据为需要修改的用户在线状态信息 endFlag设置为1
     */
    public List<StaUserAudioRecord> dealAudio (List<StaUserAudioRecord> onlineState,
                                               List<StaDayUser> listAllByDay,
                                               Map<String, StaDayUser> allUserMap,
                                               List<StaDayDept> deptAll,
                                               Map<String, StaDayDept> allDeptMap,
                                               int endFlag,
                                               List<Long> updateIds) {
        List<StaUserAudioRecord> needUpdateState = new ArrayList<>();
        onlineState.forEach(v -> {
            String start = ymd.get().format(v.getStartTime());
            String end = (endFlag == 3) ? start : ymd.get().format(v.getEndTime());// endFlag 为3时，没有退出时间，直接以当前时间作为结束时间
            LocalDate stDate = LocalDate.of(Integer.parseInt(start.substring(0,4)),
                    Integer.parseInt(start.substring(4, 6)),
                    Integer.parseInt(start.substring(6, 8)));

            LocalDate etDate = LocalDate.of(Integer.parseInt(end.substring(0,4)),
                    Integer.parseInt(end.substring(4, 6)),
                    Integer.parseInt(end.substring(6, 8)));
            long f = etDate.toEpochDay() - stDate.toEpochDay() + 1;
            if (f > 0) {
                LocalTime localTime = LocalTime.of(23, 59, 59);
                LocalDateTime localDateTime = LocalDateTime.of(etDate, localTime);
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
                Date date = Date.from(zonedDateTime.toInstant());
                Date startTime;
                Date endTime;
                if (endFlag != 1) {
                    needUpdateState.add(v);
                }
                for (int i = 0; i < f; i++) {
                    try {
                        if (endFlag == 1) {
                            startTime = (i == 0) ? v.getStartTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            endTime = (i == (f - 1)) ? v.getEndTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        } else if (endFlag == 3) {
                            if (v.getLastTime() == null) {
                                startTime = (i == 0) ? v.getStartTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            } else {
                                startTime = (i == (f - 1)) ? v.getLastTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            }
                            endTime = (i == (f - 1)) ? date : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        } else {
                            if (v.getLastTime() == null) {
                                startTime = (i == 0) ? v.getStartTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            } else {
                                startTime = (i == (f - 1) &&
                                        ymd.get().format(v.getStartTime()).equals(ymd.get().format(v.getEndTime())))
                                        ? v.getLastTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            }
                            endTime = (i == (f - 1)) ? v.getEndTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        }
                        Long sec = (endTime.getTime() - startTime.getTime()) / 1000;
                        while (sec > 86400) {// 如果时间超过86400秒，说明是最后一次获取到跨天的退出
                            sec -= 86400;
                        }
                        StaDayUser staDayUser = allUserMap.get(v.getUid() + timeAddDate(ymd.get(), v.getTimeYmd(), i));
                        if (staDayUser != null) {
                            updateIds.add(staDayUser.getId());
                            if (endFlag == 1 || endFlag == 4) {
                                if (v.getType() == 1) {
                                    staDayUser.setIndividualTalkCount(staDayUser.getIndividualTalkCount() + 1);
                                } else {
                                    staDayUser.setGroupTalkCount(staDayUser.getGroupTalkCount() + 1);
                                }
                                staDayUser.setTalkCount(staDayUser.getGroupTalkCount() + staDayUser.getIndividualTalkCount());
                            }
                            if (v.getLastTime() != null) {
                                if (i == (f - 1)) {// 增量计算
                                    if (v.getType() == 1) {
                                        staDayUser.setIndividualTalkDuration((staDayUser.getIndividualTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayUser.getIndividualTalkDuration() + sec.intValue());
                                    } else {
                                        staDayUser.setGroupTalkDuration((staDayUser.getGroupTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayUser.getGroupTalkDuration() + sec.intValue());
                                    }
                                }
                            } else {
                                if (v.getType() == 1) {
                                    staDayUser.setIndividualTalkDuration((staDayUser.getIndividualTalkDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayUser.getIndividualTalkDuration() + sec.intValue());
                                } else {
                                    staDayUser.setGroupTalkDuration((staDayUser.getGroupTalkDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayUser.getGroupTalkDuration() + sec.intValue());
                                }
                            }
                            staDayUser.setTalkDuration(staDayUser.getIndividualTalkDuration()  + staDayUser.getGroupTalkDuration());
                        } else {
                            staDayUser = new StaDayUser();
                            staDayUser.setCorpid(v.getCorpid())
                                    .setUid(v.getUid())
                                    .setName(v.getName())
                                    .setTimeYmd(Integer.parseInt(timeAddDate(ymd.get(), v.getTimeYmd(), i)))
                                    .setDeptUniqueId(v.getDeptUniqueId())
                                    .setDeptName(v.getDeptName())
                                    .setState(v.getState());
                            if (endFlag == 1 || endFlag == 4) {
                                if (v.getType() == 1) {
                                    staDayUser.setIndividualTalkCount(staDayUser.getIndividualTalkCount() + 1);
                                } else {
                                    staDayUser.setGroupTalkCount(staDayUser.getGroupTalkCount() + 1);
                                }
                                staDayUser.setTalkCount(staDayUser.getGroupTalkCount() + staDayUser.getIndividualTalkCount());
                            }
                            if (v.getLastTime() != null) {
                                if (i == (f - 1)) {// 增量计算
                                    if (v.getType() == 1) {
                                        staDayUser.setIndividualTalkDuration((staDayUser.getIndividualTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayUser.getIndividualTalkDuration() + sec.intValue());
                                    } else {
                                        staDayUser.setGroupTalkDuration((staDayUser.getGroupTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayUser.getGroupTalkDuration() + sec.intValue());
                                    }
                                }
                            } else {
                                if (v.getType() == 1) {
                                    staDayUser.setIndividualTalkDuration((staDayUser.getIndividualTalkDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayUser.getIndividualTalkDuration() + sec.intValue());
                                } else {
                                    staDayUser.setGroupTalkDuration((staDayUser.getGroupTalkDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayUser.getGroupTalkDuration() + sec.intValue());
                                }
                            }
                            staDayUser.setTalkDuration(staDayUser.getIndividualTalkDuration()  + staDayUser.getGroupTalkDuration());
                            // 新增对象后放置到集合中存储
                            listAllByDay.add(staDayUser);
                            allUserMap.put(staDayUser.getUid() + staDayUser.getTimeYmd().toString(), staDayUser);
                        }
                        String parent = rtvUnitMapper.selectCommonUniqueUp(staDayUser.getDeptUniqueId());// 查询上级部门
                        List<String> listParent = Arrays.asList(parent.split(",")).stream().distinct().collect(Collectors.toList());
                        for (String s : listParent) {
                            StaDayDept staDayDept = allDeptMap.get(s + timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            if (staDayDept != null) {
                                if (endFlag == 1 || endFlag == 4) {
                                    if (v.getType() == 1) {
                                        staDayDept.setIndividualTalkCount(staDayDept.getIndividualTalkCount() + 1);
                                    } else {
                                        staDayDept.setGroupTalkCount(staDayDept.getGroupTalkCount() + 1);
                                    }
                                    staDayDept.setTalkCount(staDayDept.getGroupTalkCount() + staDayDept.getIndividualTalkCount());
                                }
                                // 当状态为3或者4时，需要判断是否已经被统计过，如果被统计过，只需要统计最后一次信息，如果没有统计过，需要把每天的数据加进去
                                if (v.getLastTime() != null) {
                                    if (i == (f - 1)) {// 增量计算
                                        if (v.getType() == 1) {
                                            staDayDept.setIndividualTalkDuration((staDayDept.getIndividualTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                    staDayDept.getIndividualTalkDuration() + sec.intValue());
                                        } else {
                                            staDayDept.setGroupTalkDuration((staDayDept.getGroupTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                    staDayDept.getGroupTalkDuration() + sec.intValue());
                                        }
                                    }
                                } else {
                                    if (v.getType() == 1) {
                                        staDayDept.setIndividualTalkDuration((staDayDept.getIndividualTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayDept.getIndividualTalkDuration() + sec.intValue());
                                    } else {
                                        staDayDept.setGroupTalkDuration((staDayDept.getGroupTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayDept.getGroupTalkDuration() + sec.intValue());
                                    }
                                }
                                staDayDept.setTalkDuration(staDayDept.getIndividualTalkDuration()  + staDayDept.getGroupTalkDuration());
                            } else {
                                staDayDept = new StaDayDept();
                                String deptName = rtvUnitMapper.selectNameByUniqueId(s);
                                staDayDept.setCorpid(v.getCorpid())
                                        .setTimeYmd(Integer.parseInt(timeAddDate(ymd.get(), v.getTimeYmd(), i)))
                                        .setDeptUniqueId(s)
                                        .setDeptName(deptName);
                                if (endFlag == 1 || endFlag == 4) {
                                    if (v.getType() == 1) {
                                        staDayDept.setIndividualTalkCount(staDayDept.getIndividualTalkCount() + 1);
                                    } else {
                                        staDayDept.setGroupTalkCount(staDayDept.getGroupTalkCount() + 1);
                                    }
                                    staDayDept.setTalkCount(staDayDept.getGroupTalkCount() + staDayDept.getIndividualTalkCount());
                                }
                                // 当状态为3或者4时，需要判断是否已经被统计过，如果被统计过，只需要统计最后一次信息，如果没有统计过，需要把每天的数据加进去
                                if (v.getLastTime() != null) {
                                    if (i == (f - 1)) {// 增量计算
                                        if (v.getType() == 1) {
                                            staDayDept.setIndividualTalkDuration((staDayDept.getIndividualTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                    staDayDept.getIndividualTalkDuration() + sec.intValue());
                                        } else {
                                            staDayDept.setGroupTalkDuration((staDayDept.getGroupTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                    staDayDept.getGroupTalkDuration() + sec.intValue());
                                        }
                                    }
                                } else {
                                    if (v.getType() == 1) {
                                        staDayDept.setIndividualTalkDuration((staDayDept.getIndividualTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayDept.getIndividualTalkDuration() + sec.intValue());
                                    } else {
                                        staDayDept.setGroupTalkDuration((staDayDept.getGroupTalkDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayDept.getGroupTalkDuration() + sec.intValue());
                                    }
                                }
                                staDayDept.setTalkDuration(staDayDept.getIndividualTalkDuration()  + staDayDept.getGroupTalkDuration());
                                // 新增加的部门需要存放到集合中
                                deptAll.add(staDayDept);
                                allDeptMap.put(staDayDept.getDeptUniqueId() + staDayDept.getTimeYmd().toString(), staDayDept);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        return needUpdateState;
    }



    /**
     * 处理用户语音统计信息
     * @param onlineState   所有endFlag状态为1、3、4的数据
     * @param listAllByDay  所有用户每天的数据
     * @param allUserMap    所有用户每天数据的map集合
     * @param deptAll       所有部门每天的数据
     * @param allDeptMap    所有部门每天数据的map集合
     * @param endFlag       表示所处理数据的状态  1：代表正常数据，有开始和结束，没有被统计过
     *                                            3：代表又开始没有结束的数据，可以被统计过
     *                                            4：代表可能被统计过得，又开始和结束
     * @return  当状态为4时，才有返回信息，返回的数据为需要修改的用户在线状态信息 endFlag设置为1
     */
    public List<StaUserVideoRecord> dealVideo (List<StaUserVideoRecord> onlineState,
                                               List<StaDayUser> listAllByDay,
                                               Map<String, StaDayUser> allUserMap,
                                               List<StaDayDept> deptAll,
                                               Map<String, StaDayDept> allDeptMap,
                                               int endFlag,
                                               List<Long> updateIds) {
        List<StaUserVideoRecord> needUpdateState = new ArrayList<>();
        onlineState.forEach(v -> {
            String start = ymd.get().format(v.getStartTime());
            String end = (endFlag == 3) ? start : ymd.get().format(v.getEndTime());// endFlag 为3时，没有退出时间，直接以当前时间作为结束时间
            LocalDate stDate = LocalDate.of(Integer.parseInt(start.substring(0,4)),
                    Integer.parseInt(start.substring(4, 6)),
                    Integer.parseInt(start.substring(6, 8)));

            LocalDate etDate = LocalDate.of(Integer.parseInt(end.substring(0,4)),
                    Integer.parseInt(end.substring(4, 6)),
                    Integer.parseInt(end.substring(6, 8)));
            long f = etDate.toEpochDay() - stDate.toEpochDay() + 1;
            if (f > 0) {
                LocalTime localTime = LocalTime.of(23, 59, 59);
                LocalDateTime localDateTime = LocalDateTime.of(etDate, localTime);
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
                Date date = Date.from(zonedDateTime.toInstant());
                Date startTime, endTime;
                if (endFlag != 1) {
                    needUpdateState.add(v);
                }
                for (int i = 0; i < f; i++) {// 划分天数
                    try {
                        if (endFlag == 1) {// 表示没有被计算过的数据，开始和结束时间都是正确的
                            startTime = (i == 0) ? v.getStartTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            endTime = (i == (f - 1)) ? v.getEndTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        } else if (endFlag == 3) {// 表示数据已经被被处理过，没有结束时间用当前时间表示结束
                            if (v.getLastTime() == null) {
                                startTime = (i == 0) ? v.getStartTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            } else {
                                startTime = (i == (f - 1)) ? v.getLastTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            }
                            endTime = (i == (f - 1)) ? date : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        } else {// 表示数据已经被处理过，有结束时间
                            if (v.getLastTime() == null) {
                                startTime = (i == 0) ? v.getStartTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            } else {
                                startTime = (i == (f - 1)) ? v.getLastTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            }
                            endTime = (i == (f - 1) &&
                                    ymd.get().format(v.getStartTime()).equals(ymd.get().format(v.getEndTime())))
                                    ? v.getEndTime() : ymd.get().parse(timeAddDate(ymd.get(), v.getTimeYmd(), i + 1));
                        }
                        Long sec = (endTime.getTime() - startTime.getTime()) / 1000;
                        while (sec > 86400) {// 如果时间超过86400秒，说明是最后一次获取到跨天的退出
                            sec -= 86400;
                        }
                        StaDayUser staDayUser = allUserMap.get(v.getUid() + timeAddDate(ymd.get(), v.getTimeYmd(), i));
                        if (staDayUser != null) {
                            updateIds.add(staDayUser.getId());
                            if (endFlag == 1 || endFlag == 4) {
                                if (v.getType() == 1) {// 视频通话
                                    staDayUser.setVideoCallCount(staDayUser.getVideoCallCount() + 1);
                                } else if (v.getType() == 2) {
                                    staDayUser.setVideoUploadCount(staDayUser.getVideoUploadCount() + 1);
                                } else if (v.getType() == 3) {
                                    staDayUser.setVideoRollcallCount(staDayUser.getVideoRollcallCount() + 1);
                                } else if (v.getType() == 4) {
                                    staDayUser.setVideoConfCount(staDayUser.getVideoConfCount() + 1);
                                }
                                staDayUser.setVideoAllCount(staDayUser.getVideoCallCount() + staDayUser.getVideoUploadCount() + staDayUser.getVideoRollcallCount() + staDayUser.getVideoConfCount());
                            }
                            if (v.getLastTime() != null) {
                                if (i == (f - 1)) {
                                    if (v.getType() == 1) {
                                        staDayUser.setVideoDuration((staDayUser.getVideoDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayUser.getVideoDuration() + sec.intValue());
                                    }
                                }
                            } else {
                                if (v.getType() == 1) {
                                    staDayUser.setVideoDuration((staDayUser.getVideoDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayUser.getVideoDuration() + sec.intValue());
                                }
                            }
                        } else {
                            staDayUser = new StaDayUser();
                            staDayUser.setCorpid(v.getCorpid())
                                    .setUid(v.getUid())
                                    .setName(v.getName())
                                    .setTimeYmd(Integer.parseInt(timeAddDate(ymd.get(), v.getTimeYmd(), i)))
                                    .setDeptUniqueId(v.getDeptUniqueId())
                                    .setDeptName(v.getDeptName())
                                    .setState(v.getState());
                            if (endFlag == 1 || endFlag == 4) {
                                if (v.getType() == 1) {// 视频通话
                                    staDayUser.setVideoCallCount(staDayUser.getVideoCallCount() + 1);
                                } else if (v.getType() == 2) {
                                    staDayUser.setVideoUploadCount(staDayUser.getVideoUploadCount() + 1);
                                } else if (v.getType() == 3) {
                                    staDayUser.setVideoRollcallCount(staDayUser.getVideoRollcallCount() + 1);
                                } else if (v.getType() == 4) {
                                    staDayUser.setVideoConfCount(staDayUser.getVideoConfCount() + 1);
                                }
                                staDayUser.setVideoAllCount(staDayUser.getVideoCallCount() + staDayUser.getVideoUploadCount() + staDayUser.getVideoRollcallCount() + staDayUser.getVideoConfCount());
                            }
                            if (v.getLastTime() != null) {
                                if (i == (f - 1)) {
                                    if (v.getType() == 1) {
                                        staDayUser.setVideoDuration((staDayUser.getVideoDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayUser.getVideoDuration() + sec.intValue());
                                    }
                                }
                            } else {
                                if (v.getType() == 1) {
                                    staDayUser.setVideoDuration((staDayUser.getVideoDuration() + sec.intValue()) < 0 ? 0 :
                                            staDayUser.getVideoDuration() + sec.intValue());
                                }
                            }
                            // 新增对象后放置到集合中存储
                            listAllByDay.add(staDayUser);
                            allUserMap.put(staDayUser.getUid() + staDayUser.getTimeYmd().toString(), staDayUser);
                        }
                        String parent = rtvUnitMapper.selectCommonUniqueUp(staDayUser.getDeptUniqueId());// 查询上级部门
                        List<String> listParent = Arrays.asList(parent.split(",")).stream().distinct().collect(Collectors.toList());
                        for (String s : listParent) {
                            StaDayDept staDayDept = allDeptMap.get(s + timeAddDate(ymd.get(), v.getTimeYmd(), i));
                            if (staDayDept != null) {
                                if (endFlag == 1 || endFlag == 4) {
                                    if (v.getType() == 1) {// 视频通话
                                        staDayDept.setVideoCallCount(staDayDept.getVideoCallCount() + 1);
                                    } else if (v.getType() == 2) {
                                        staDayDept.setVideoUploadCount(staDayDept.getVideoUploadCount() + 1);
                                    } else if (v.getType() == 3) {
                                        staDayDept.setVideoRollcallCount(staDayDept.getVideoRollcallCount() + 1);
                                    } else if (v.getType() == 4) {
                                        staDayDept.setVideoConfCount(staDayDept.getVideoConfCount() + 1);
                                    }
                                    staDayDept.setVideoAllCount(staDayDept.getVideoCallCount() + staDayDept.getVideoUploadCount() + staDayDept.getVideoRollcallCount() + staDayDept.getVideoConfCount());
                                }
                                // 当状态为3或者4时，需要判断是否已经被统计过，如果被统计过，只需要统计最后一次信息，如果没有统计过，需要把每天的数据加进去
                                if (v.getLastTime() != null) {
                                    if (i == (f - 1)) {// 增量计算
                                        if (v.getType() == 1) {
                                            staDayDept.setVideoDuration((staDayDept.getVideoDuration() + sec.intValue()) < 0 ? 0 :
                                                    staDayDept.getVideoDuration() + sec.intValue());
                                        }
                                    }
                                } else {
                                    if (v.getType() == 1) {
                                        staDayDept.setVideoDuration((staDayDept.getVideoDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayDept.getVideoDuration() + sec.intValue());
                                    }
                                }
                            } else {
                                staDayDept = new StaDayDept();
                                String deptName = rtvUnitMapper.selectNameByUniqueId(s);
                                staDayDept.setCorpid(v.getCorpid())
                                        .setTimeYmd(Integer.parseInt(timeAddDate(ymd.get(), v.getTimeYmd(), i)))
                                        .setDeptUniqueId(s)
                                        .setDeptName(deptName);
                                if (endFlag == 1 || endFlag == 4) {
                                    if (v.getType() == 1) {// 视频通话
                                        staDayDept.setVideoCallCount(staDayDept.getVideoCallCount() + 1);
                                    } else if (v.getType() == 2) {
                                        staDayDept.setVideoUploadCount(staDayDept.getVideoUploadCount() + 1);
                                    } else if (v.getType() == 3) {
                                        staDayDept.setVideoRollcallCount(staDayDept.getVideoRollcallCount() + 1);
                                    } else if (v.getType() == 4) {
                                        staDayDept.setVideoConfCount(staDayDept.getVideoConfCount() + 1);
                                    }
                                    staDayDept.setVideoAllCount(staDayDept.getVideoCallCount() + staDayDept.getVideoUploadCount() + staDayDept.getVideoRollcallCount() + staDayDept.getVideoConfCount());
                                }
                                if (v.getLastTime() != null) {
                                    if (i == (f - 1)) {// 增量计算
                                        if (v.getType() == 1) {
                                            staDayDept.setVideoDuration((staDayDept.getVideoDuration() + sec.intValue()) < 0 ? 0 :
                                                    staDayDept.getVideoDuration() + sec.intValue());
                                        }
                                    }
                                } else {
                                    if (v.getType() == 1) {
                                        staDayDept.setVideoDuration((staDayDept.getVideoDuration() + sec.intValue()) < 0 ? 0 :
                                                staDayDept.getVideoDuration() + sec.intValue());
                                    }
                                }
                                // 新增加的部门需要存放到集合中
                                deptAll.add(staDayDept);
                                allDeptMap.put(staDayDept.getDeptUniqueId() + staDayDept.getTimeYmd().toString(), staDayDept);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        return needUpdateState;
    }

    /**
     * 对yyyyMMdd格式的日期进行加多天操作
     * @param simpleDateFormat
     * @param value
     * @param day
     * @return
     */
    private static String timeAddDate (DateFormat simpleDateFormat, Integer value, int day) throws ParseException {
        Calendar c = Calendar.getInstance();
        Date date = simpleDateFormat.parse(value.toString());
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return simpleDateFormat.format(c.getTime());
    }

    @Override
    public List<ContinueBusiness> getOngoing(int type, Integer uid, String uniqueId) {
        List<String> uniqueIds = null;
        if (StringUtils.isNotEmpty(uniqueId)) {
            String ids = rtvUnitMapper.selectCommonUniqueDown(uniqueId);
            uniqueIds = Arrays.asList(ids.split(","));
        }
        List<ContinueBusiness> list = new ArrayList<>();
        if (type == 1) {// 用户未退出查询
            list = staUserOnlineRecordMapper.selectOngoing(uid, uniqueIds);
        } else if (type == 2) {// 用户语音通话未结束查询
            list = staUserAudioRecordMapper.selectOngoing(uid, uniqueIds);
        } else if (type == 3) {// 用户视频通话未结束查询
            list = staUserVideoRecordMapper.selectOngoing(uid, uniqueIds);
        }
        return list;
    }

    @Override
    public JSONObject interruputBusiness(int type, Integer flag, Integer uid) {
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> list = new ArrayList<>();
        if (type != 1 && flag == null) {
            jsonObject.put("ret", 1);
            jsonObject.put("error", "参数flag不能为空");
            return jsonObject;
        }
        if (type == 1) {// 中断用户在线
            JSONObject json = new JSONObject();
            json.put("id", 1001);
            json.put("uid", uid);
            json.put("time", sdf.get().format(new Date()));
            json.put("real", 1);
            json.put("session", "");
            list.add(json);
            JSONObject json1 = new JSONObject();
            json1.put("id", 1001);
            json1.put("uid", uid);
            json1.put("time", sdf.get().format(new Date()));
            json1.put("real", 0);
            json1.put("session", "");
            json.put("system_interrupt", 2);// 表明是用户手动结束时，自动生成的日志信息
            list.add(json1);
        } else if (type == 2) {// 中断用户语音
            if (flag == 1 || flag == null) {// 个呼
                JSONObject json = new JSONObject();
                json.put("id", 1100);
                json.put("uid", uid);
                json.put("target", 0);
                json.put("type", 1);
                json.put("end_time", sdf.get().format(new Date()));
                json.put("flag", 2);
                json.put("session", "");
                json.put("system_interrupt", 2);// 表明是用户手动结束时，自动生成的日志信息
                list.add(json);
            }
            if (flag == 2 || flag == null) {// 组呼
                JSONObject json = new JSONObject();
                json.put("id", 1100);
                json.put("uid", uid);
                json.put("target", 0);
                json.put("type", 2);
                json.put("end_time", sdf.get().format(new Date()));
                json.put("flag", 2);
                json.put("session", "");
                json.put("system_interrupt", 2);// 表明是用户手动结束时，自动生成的日志信息
                list.add(json);
            }
        } else if (type == 3) {// 中断用户视频
            if (flag == 1 || flag == null) {
                JSONObject json = new JSONObject();
                json.put("id", 1200);
                json.put("sub_id", 1201);
                json.put("uid", uid);
                json.put("target", 0);
                json.put("end_time", sdf.get().format(new Date()));
                json.put("flag", 2);
                json.put("session", "");
                json.put("system_interrupt", 2);// 表明是用户手动结束时，自动生成的日志信息
                list.add(json);
            }
            if (flag == 2 || flag == null) {
                List<String> videoUploadSession = staUserVideoRecordMapper.selectSessionByUidAndType(uid, 2);
                for (String s : videoUploadSession) {
                    JSONObject json = new JSONObject();
                    json.put("id", 1200);
                    json.put("sub_id", 1202);
                    json.put("uid", uid);
                    json.put("end_time", sdf.get().format(new Date()));
                    json.put("flag", 2);
                    json.put("session", s);
                    json.put("system_interrupt", 2);// 表明是用户手动结束时，自动生成的日志信息
                    list.add(json);
                }
            }
            if (flag == 3 || flag == null) {
                List<String> session = staUserVideoRecordMapper.selectSessionByUidAndType(uid, 3);
                for (String s : session) {
                    JSONObject json = new JSONObject();
                    json.put("id", 1200);
                    json.put("sub_id", 1203);
                    json.put("uid", uid);
                    json.put("end_time", sdf.get().format(new Date()));
                    json.put("flag", 2);
                    json.put("session", s);
                    json.put("system_interrupt", 2);// 表明是用户手动结束时，自动生成的日志信息
                    list.add(json);
                }
            }
            if (flag == 4 || flag == null) {
                List<String> session = staUserVideoRecordMapper.selectSessionByUidAndType(uid, 4);
                for (String s : session) {
                    JSONObject json = new JSONObject();
                    json.put("id", 1200);
                    json.put("sub_id", 1204);
                    json.put("uid", uid);
                    json.put("end_time", sdf.get().format(new Date()));
                    json.put("flag", 2);
                    json.put("session", s);
                    json.put("system_interrupt", 2);// 表明是用户手动结束时，自动生成的日志信息
                    list.add(json);
                }
            }
        }
        List<StaOriginalLog> originalLogs = new ArrayList<>();
        list.forEach(v -> {
            StaOriginalLog log = new StaOriginalLog();
            log.setContent(v.toString());
            originalLogs.add(log);
        });
        if (originalLogs.size() > 0) {
            staOriginalLogMapper.insertList(originalLogs);
        }
        jsonObject.put("ret", 0);
        return jsonObject;
    }

    public static List<StaDayDept> deepCopyForDept(List<StaDayDept> src) throws IOException, ClassNotFoundException{
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in =new ObjectInputStream(byteIn);
        List<StaDayDept> dest = (List<StaDayDept>) in.readObject();
        return dest;
    }

    public static List<StaDayUser> deepCopyForUser(List<StaDayUser> src) throws IOException, ClassNotFoundException{
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in =new ObjectInputStream(byteIn);
        List<StaDayUser> dest = (List<StaDayUser>) in.readObject();
        return dest;
    }
}



