package com.lhfeiyu.tech.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zom.statistics.dao.po.*;
import com.zom.statistics.domain.StaConst;
import com.zom.statistics.model.LogonOffRecord;
import com.zom.statistics.model.PairLog;
import com.zom.statistics.model.UserCache;
import com.zom.statistics.service.StaLog2ParseService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * 原始日志解析
 *
 * @author airson
 */
@Component
public class StaLog2ParseHandler {

    @Autowired
    private CacheHandler cacheHandler;
    @Autowired
    private StaLog3PairHandler staLog3PairHandler;
    @Autowired
    private StaLog2ParseService staLog2ParseService;

    private static Logger logger = LoggerFactory.getLogger(StaLog2ParseHandler.class);
    private String start_time;

    /**
     * parse logs from sta_original_log to each domain table
     */
    public void parseOriginLog() {
        // XXX 从sta_original_log中加载原始日志，通过id判断业务类型，获取对应数据，组装数据，更新对应统计表
        // sta_original_log_flag是记录上次解析到哪里
        // 根据业务类型获取数据，并查询数据库，组装出所有字段
        // 更新统计表需要更新对应记录表和用户，部门的统计表，部门包括所有的上级链部门

        // XXX STEP: get origin log list to parse
        List<StaOriginalLog> dataList = staLog2ParseService.getListByPage(StaConst.PARSE_SINGLE_PAGE_COUNT);
        if (null == dataList || dataList.size() == 0) {
            return;
        }

        // XXX STEP: parse list by domain type
        PairLog pairLog = new PairLog();

        // XXX 需要保证某条数据有异常，只影响这一条数据，把这条数据过滤掉就行了，不能影响整体的解析
        // 这里进行try catch是为主保证此次解析出错只影响这次的统计结果，然后直接往后继续解析，
        // 如果报错后不更新解析位置会导致错误的数据被重复解析，每次解析仍然会报错（原则是要保证程序主体能正常稳定运行，再追求数据准确性）
        Map<Long, LogonOffRecord> logonOffRecordMap = null;
        try {
            logonOffRecordMap = parseByDomain(dataList, pairLog);
        } catch (Exception e) {
            logger.error("parseByDomain error:{}", e.getMessage());
            logger.warn("the data of this turn will be ignore and parse ahead");
            e.printStackTrace();
        }

        // XXX STEP: update parse position
        long startPkId = dataList.get(0).getId();
        long endPkId = dataList.get(dataList.size() - 1).getId();
        long lastPkId = endPkId > startPkId ? endPkId : startPkId;
        staLog2ParseService.updateLastPkId(lastPkId, StaConst.sta_original_log);

        staLog3PairHandler.pairLog(pairLog, logonOffRecordMap);

    }

    private Map<Long, LogonOffRecord> parseByDomain(List<StaOriginalLog> dataList, PairLog pairLog) {
        int size = dataList.size();

        logger.debug("parseByDomain size:{}", size);

        //List<JSONObject> contentJsonList = new ArrayList<>(size);
        /*int uidSetInitialSize = size > 200 ? 200 : size;
        Set<Long> uidSet = new HashSet<>(uidSetInitialSize);*/
        int listSize = size > 100 ? 20 : 10;
        int listSizeHot = listSize * 2;
        List<JSONObject> list1000_1 = new ArrayList<>(listSizeHot);// 用户登录 1000 用户退出 1001
        List<JSONObject> list1002_3_4 = new ArrayList<>(listSize);// 发起一键报警 1002 取消一键报警 1003 处理一键报警 1004
        List<JSONObject> list1100 = new ArrayList<>(listSizeHot);// 语音通话 1100
        // 视频通话 id:1200-1201 视频回传 1200-1202 视频点名 1200-1203 视频会商 1200-1204
        List<JSONObject> list1201_2_3_4 = new ArrayList<>(listSizeHot);
        List<JSONObject> list1300 = new ArrayList<>(listSize);// 照片回传 1300
        List<JSONObject> list1400 = new ArrayList<>(listSize);// 创建临时组 1400
        List<JSONObject> list1500 = new ArrayList<>(listSize);// 即时消息 1500
        List<JSONObject> list1600 = new ArrayList<>(listSizeHot);// 里程数更新 1600

        for (StaOriginalLog log : dataList) {
            JSONObject contentJson;
            try {
                contentJson = (JSONObject) JSON.parse(log.getContent());
            } catch (Exception e) {
                logger.warn("parseByDomain content error:{}", log.getContent());
                continue;
            }
            Integer id = contentJson.getInteger("id");
            /*Long uid = contentJson.getLong("uid");
            uidSet.add(uid);*/
            //contentJsonList.add(contentJson);
            if (null == id) {
                continue;
            }
            switch (id) {
                case 1000:
                case 1001:
                    list1000_1.add(contentJson);
                    break;
                case 1002:
                case 1003:
                case 1004:
                    /*Long handle_uid = contentJson.getLong("handle_uid"); //处理一键报警 1004 处理人
                    if (null != handle_uid) {
                        uidSet.add(handle_uid);
                    }*/
                    list1002_3_4.add(contentJson);
                    break;
                case 1100:
                    /*Long targetId = contentJson.getLong("targetId"); //个呼：对方uid，组呼：通话组ID
                    Long type = contentJson.getLong("type"); //通话类型：1个呼，2组呼
                    if (null != type && type == 1 && null != targetId) {
                        uidSet.add(targetId);
                    }*/
                    list1100.add(contentJson);
                    break;
                case 1200:
                    /*Integer sub_id = contentJson.getInteger("sub_id");
                    Long targetId2 = contentJson.getLong("targetId");
                    if (null != sub_id && sub_id == 1201 && null != targetId2) {
                        uidSet.add(targetId2);
                    }*/
                    list1201_2_3_4.add(contentJson);
                    break;
                case 1300:
                    list1300.add(contentJson);
                    break;
                case 1400:
                    list1400.add(contentJson);
                    break;
                case 1500:
                    list1500.add(contentJson);
                    break;
                case 1600:
                    list1600.add(contentJson);
                    break;
            }

        }
        // cache user info
        //Map<Long, UserCache> userCacheMap = cacheHandler.getUserList(uidSet);
        Map<Long, UserCache> userCacheMap = null; // 不再这里统一查询到缓存，而是在用的时候再去查询到缓存，暂时没有直接去除这部分代码而是置空

        Map<Long, LogonOffRecord> logonOffRecordMap = parseDomain1000_1(list1000_1, userCacheMap, pairLog); // 先解析logon_off,要提前检查结束业务
        parseDomain1002_3_4(list1002_3_4, userCacheMap, pairLog);
        parseDomain1100(list1100, userCacheMap, pairLog);
        parseDomain1201_2_3_4(list1201_2_3_4, userCacheMap, pairLog);
        parseDomain1300(list1300, userCacheMap);
        parseDomain1400(list1400, userCacheMap);
        parseDomain1500(list1500, userCacheMap);
        parseDomain1600(list1600, userCacheMap);
        return logonOffRecordMap;
    }

    private Map<Long, LogonOffRecord> parseDomain1000_1(List<JSONObject> originList, Map<Long, UserCache> userCacheMap, PairLog pairLog) {
        if (originList.size() <= 0) {
            return null;
        }

        logger.debug("parseDomain1000_1 size:{}", originList.size());

        // XXX 逻辑：在收到真实的登录和登出时，结束所有持续状态的业务，online本身的业务就会检查结束，这里需要结束：SOS, AUDIO, VIDEO
        // 由于这里只是一种补救措施，在这里结束业务，说明时长已经不准确了，就没必要再去更新时长，直接更新为结束就行了
        // 对于语音视频通话，对方登录或登出时，也应该结束业务（SQL字段需要判断uid和target_id）
        // 这里有个时序问题，因为是按业务分组的数据，实际登录和通话有可能有多组，顺序随机，比如这组数据里有登出，但是登出之前是有结束通话的，由于这里会先执行，所以就直接结束了，
        // 应该保存uid和ts，在这轮业务解析完后，再结束TS之前开始的持续业务，前提是所有业务log是按时间顺序入队列的，有误差并因误差造成解析不准确的概率很小，暂不考虑
        //List<LogonOffRecord> logonOffRecordList = new ArrayList<>();
        Map<Long, LogonOffRecord> logonOffRecordMap = new HashMap<>(); // 这里用Map不用List，主要是防止重复，并且最新的覆盖旧的

        List<StaUserLogonRecordOrigin> dataList = new ArrayList<>(originList.size());

        for (JSONObject json : originList) {
            //items: id, uid, time, real, session
            Integer id = json.getInteger("id");
            Long uid = json.getLong("uid");
            String time = json.getString("time"); // 2020-06-10 15:32:16
            Integer real = json.getInteger("real"); // 真实登录值为1

            if (null == real) {
                real = 0; // 非真实登录，只是收到online,offline的网络波动
            }
            if (null == id || null == uid || null == time) {
                logger.warn("parseDomain1000_1001 content invalid:{}", json.toJSONString());
                continue;
            }
            String ym, d, h;
            try {
                ym = time.substring(0, 4) + time.substring(5, 7);
                d = time.substring(8, 10);
                h = time.substring(11, 13);
            } catch (Exception e) {
                logger.warn("parseDomain1000_1001 time invalid:{}", json.toJSONString());
                continue;
            }

            //UserCache user = userCacheMap.get(uid);
            UserCache user = cacheHandler.getUserCache(uid);
            if (user == null) {
                logger.warn("parseDomain1000_1001 user null, uid:{}", uid);
                continue;
            }
            StaUserLogonRecordOrigin item = new StaUserLogonRecordOrigin();
            int flag = id == 1000 ? 1 : 2;
            Date timeObj;
            try {
                timeObj = DateUtils.parseDate(time, StaConst.DF_SECOND);
                item.setCorpid(user.getCorpid())
                        .setUid(user.getId())
                        .setName(user.getUsername())
                        .setTimeYm(Integer.valueOf(ym))
                        .setTimeYmd(Integer.valueOf(ym + d))
                        .setTimeYmdh(Integer.valueOf(ym + d + h))
                        .setDeptUniqueId(user.getDeptUniqueId())
                        .setDeptName(user.getDeptName())
                        .setTimeStr(time)
                        .setTime(timeObj)
                        .setBindDuty(user.getRoleId() == 0 ? 0 : 1)
                        .setFlag(flag) // 操作标识：1登录，2退出登录
                        .setReal(real)
                        .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
            } catch (ParseException e) {
                logger.warn("parseDomain1000_1001 time parse error:{}", e.getMessage());
                continue;
            }

            dataList.add(item);
            if (null != real && real == 1) {
                LogonOffRecord logonOffRecord = new LogonOffRecord();
                logonOffRecord.setUid(uid);
                logonOffRecord.setTime(timeObj);
                logonOffRecord.setFlag(flag);
                logonOffRecordMap.put(uid, logonOffRecord);
            }
        }
        if (dataList.size() > 0) {
            staLog2ParseService.insertLogonRecordOrigin(dataList);
            pairLog.setLogonRecordOriginList(dataList);
        }
        return logonOffRecordMap;
    }

    private void parseDomain1002_3_4(List<JSONObject> originList, Map<Long, UserCache> userCacheMap, PairLog pairLog) {
        if (originList.size() <= 0) {
            return;
        }

        logger.debug("parseDomain1002_3_4 size:{}", originList.size());

        List<StaUserSosRecordOrigin> dataList = new ArrayList<>(originList.size());

        for (JSONObject json : originList) {
            //items: id, uid, time, location, marker, session, handle_uid(处理一键告警)
            Integer id = json.getInteger("id");
            Long uid = json.getLong("uid");
            String time = json.getString("time");// 2020-06-10 15:32:16
            // flag是通过id来判断的，没有直接的flag字段

            if (null == id || null == uid || null == time) {
                logger.warn("parseDomain1002_3_4 content invalid:{}", json.toJSONString());
                continue;
            }
            String ym, d, h;
            try {
                ym = time.substring(0, 4) + time.substring(5, 7);
                d = time.substring(8, 10);
                h = time.substring(11, 13);
            } catch (Exception e) {
                logger.warn("parseDomain1002_3_4 time invalid:{}", json.toJSONString());
                continue;
            }

            //UserCache user = userCacheMap.get(uid);
            UserCache user = cacheHandler.getUserCache(uid);
            if (user == null) {
                logger.warn("parseDomain1002_3_4 user null, uid:{}", uid);
                continue;
            }
            StaUserSosRecordOrigin item = new StaUserSosRecordOrigin();
            try {
                item.setCorpid(user.getCorpid())
                        .setUid(user.getId())
                        .setName(user.getUsername())
                        .setTimeYm(Integer.valueOf(ym))
                        .setTimeYmd(Integer.valueOf(ym + d))
                        .setTimeYmdh(Integer.valueOf(ym + d + h))
                        .setDeptUniqueId(user.getDeptUniqueId())
                        .setDeptName(user.getDeptName())
                        .setTimeStr(time)
                        .setTime(DateUtils.parseDate(time, StaConst.DF_SECOND))
                        .setFlag(id == 1002 ? 1 : (id == 1003 ? 2 : 3)) // 操作标识：1发起，2取消，3处理
                        .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
            } catch (ParseException e) {
                logger.warn("parseDomain1002_3_4 time parse error:{}", e.getMessage());
            }
            if (id == 1004) {
                Long handle_uid = json.getLong("handle_uid");
                Long handleUid = null;
                String handleName = null;
                if (handle_uid == null) {
                    logger.warn("parseDomain1002_3_4 handle_uid null, uid:{}", uid);
                } else {
                    //UserCache handleUser = userCacheMap.get(handle_uid);
                    UserCache handleUser = cacheHandler.getUserCache(uid);
                    if (handleUser == null) {
                        logger.warn("parseDomain1002_3_4 handle user null, uid:{}", uid);
                    } else {
                        handleUid = handleUser.getId();
                        handleName = handleUser.getUsername();
                    }
                }
                item.setHandleUid(handleUid);
                item.setHandleName(handleName);
            } else {
                item.setLocation(json.getString("location"));
                item.setMarker(json.getString("marker"));
            }

            dataList.add(item);
        }
        if (dataList.size() > 0) {
            staLog2ParseService.insertSosRecordOrigin(dataList);
            pairLog.setSosRecordOriginList(dataList);
        }

    }

    private void parseDomain1100(List<JSONObject> originList, Map<Long, UserCache> userCacheMap, PairLog pairLog) {
        if (originList.size() <= 0) {
            return;
        }

        logger.debug("parseDomain1100 size:{}", originList.size());

        List<StaUserAudioRecordOrigin> dataList = new ArrayList<>(originList.size());

        for (JSONObject json : originList) {
            //items: id, uid, target, type, start_time or end_time, flag(1开始，2结束), session
            Integer id = json.getInteger("id");
            Long uid = json.getLong("uid");
            String start_time = json.getString("start_time");// 2020-06-10 15:32:16
            String end_time = json.getString("end_time");// 2020-06-10 15:32:16
            Integer flag = json.getInteger("flag");
            Long target = json.getLong("target");
            Integer type = json.getInteger("type"); //通话类型：1个呼，2组呼

            if (null == id || null == uid || (null == start_time && null == end_time)
                    || null == target || null == flag || flag < 1 || flag > 2) {
                logger.warn("parseDomain1100 content invalid:{}", json.toJSONString());
                continue;
            }
            String time = flag == 1 ? start_time : end_time;
            String ym, d, h;
            try {
                ym = time.substring(0, 4) + time.substring(5, 7);
                d = time.substring(8, 10);
                h = time.substring(11, 13);
            } catch (Exception e) {
                logger.warn("parseDomain1100 time invalid:{}", json.toJSONString());
                continue;
            }

            //UserCache user = userCacheMap.get(uid);
            UserCache user = cacheHandler.getUserCache(uid);
            if (user == null) {
                logger.warn("parseDomain1100 user null, uid:{}", uid);
                continue;
            }
            //UserCache targetUser = userCacheMap.get(target);
            // XXX 这里需要判断个呼和组呼
            String targetName = "";
            if (type == 1) {
                UserCache targetUser = cacheHandler.getUserCache(target);
                if (null != targetUser) {
                    targetName = targetUser.getUsername();
                }
            } else if (type == 2) {
                targetName = cacheHandler.getGroupName(target);
            }

            StaUserAudioRecordOrigin item = new StaUserAudioRecordOrigin();
            try {
                item.setCorpid(user.getCorpid())
                        .setUid(user.getId())
                        .setName(user.getUsername())
                        .setTimeYm(Integer.valueOf(ym))
                        .setTimeYmd(Integer.valueOf(ym + d))
                        .setTimeYmdh(Integer.valueOf(ym + d + h))
                        .setDeptUniqueId(user.getDeptUniqueId())
                        .setDeptName(user.getDeptName())
                        .setTimeStr(time)
                        .setTime(DateUtils.parseDate(time, StaConst.DF_SECOND))
                        .setFlag(flag) // 操作标识：1开始，2结束
                        .setTargetId(target)
                        .setTargetName(targetName)
                        .setType(type)
                        .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
            } catch (ParseException e) {
                logger.warn("parseDomain1100 time parse error:{}", e.getMessage());
            }
            dataList.add(item);
        }
        if (dataList.size() > 0) {
            staLog2ParseService.insertAudioRecordOrigin(dataList);
            pairLog.setAudioRecordOriginList(dataList);
        }
    }

    private void parseDomain1201_2_3_4(List<JSONObject> originList, Map<Long, UserCache> userCacheMap, PairLog pairLog) {
        if (originList.size() <= 0) {
            return;
        }

        logger.debug("parseDomain1201_2_3_4 size:{}", originList.size());

        List<StaUserVideoRecordOrigin> dataList = new ArrayList<>(originList.size());

        for (JSONObject json : originList) {
            //items: id, uid, target(通话), start_time or end_time, flag(1开始，2结束), conference_id（会商）, session
            Integer id = json.getInteger("id");
            Integer sub_id = json.getInteger("sub_id");
            Long uid = json.getLong("uid");
            String start_time = json.getString("start_time");// 2020-06-10 15:32:16
            String end_time = json.getString("end_time");// 2020-06-10 15:32:16
            Integer flag = json.getInteger("flag"); // 标识：1开始，2结束

            if (null == id || null == uid || (null == start_time && null == end_time)
                    || null == flag || flag < 1 || flag > 2) {
                logger.warn("parseDomain1201_2_3_4 content invalid:{}", json.toJSONString());
                continue;
            }
            String time = flag == 1 ? start_time : end_time;
            String ym, d, h;
            try {
                ym = time.substring(0, 4) + time.substring(5, 7);
                d = time.substring(8, 10);
                h = time.substring(11, 13);
            } catch (Exception e) {
                logger.warn("parseDomain1201_2_3_4 time invalid:{}", json.toJSONString());
                continue;
            }

            //UserCache user = userCacheMap.get(uid);
            UserCache user = cacheHandler.getUserCache(uid);
            if (user == null) {
                logger.warn("parseDomain1201_2_3_4 user null, uid:{}", uid);
                continue;
            }
            StaUserVideoRecordOrigin item = new StaUserVideoRecordOrigin();
            try {
                item.setCorpid(user.getCorpid())
                        .setUid(user.getId())
                        .setName(user.getUsername())
                        .setTimeYm(Integer.valueOf(ym))
                        .setTimeYmd(Integer.valueOf(ym + d))
                        .setTimeYmdh(Integer.valueOf(ym + d + h))
                        .setDeptUniqueId(user.getDeptUniqueId())
                        .setDeptName(user.getDeptName())
                        .setTimeStr(time)
                        .setTime(DateUtils.parseDate(time, StaConst.DF_SECOND))
                        .setFlag(flag) // 操作标识：1开始，2结束
                        .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
            } catch (ParseException e) {
                logger.warn("parseDomain1201_2_3_4 time parse error:{}", e.getMessage());
            }
            if (sub_id == 1201) {
                item.setType(1);
                Long target = json.getLong("target");
                //UserCache targetUser = userCacheMap.get(target);
                UserCache targetUser = cacheHandler.getUserCache(target);
                if (targetUser == null) {
                    logger.warn("parseDomain1201_2_3_4 target user null, uid:{}", uid);
                    continue;
                }
                item.setTargetId(targetUser.getId()).setTargetName(targetUser.getUsername());

            } else if (sub_id == 1202) {
                item.setType(2);
            } else if (sub_id == 1203) {
                item.setType(3);
            } else {
                item.setType(4);
                item.setConferenceId(json.getString("conference_id"));
            }
            dataList.add(item);
        }
        if (dataList.size() > 0) {
            staLog2ParseService.insertVideoRecordOrigin(dataList);
            pairLog.setVideoRecordOriginList(dataList);
        }
    }

    private void parseDomain1300(List<JSONObject> originList, Map<Long, UserCache> userCacheMap) {
        if (originList.size() <= 0) {
            return;
        }

        logger.debug("parseDomain1300 size:{}", originList.size());

        List<StaUserPhotoRecord> dataList = new ArrayList<>(originList.size());

        for (JSONObject json : originList) {
            //items: id, uid, time, session
            Integer id = json.getInteger("id");
            Long uid = json.getLong("uid");
            String time = json.getString("time");// 2020-06-10 15:32:16

            if (null == id || null == uid || null == time) {
                logger.warn("parseDomain1300 content invalid:{}", json.toJSONString());
                continue;
            }
            String ym, d, h;
            try {
                ym = time.substring(0, 4) + time.substring(5, 7);
                d = time.substring(8, 10);
                h = time.substring(11, 13);
            } catch (Exception e) {
                logger.warn("parseDomain1300 time invalid:{}", json.toJSONString());
                continue;
            }

            //UserCache user = userCacheMap.get(uid);
            UserCache user = cacheHandler.getUserCache(uid);
            if (user == null) {
                logger.warn("parseDomain1300 user null, uid:{}", uid);
                continue;
            }

            StaUserPhotoRecord item = new StaUserPhotoRecord();
            try {
                item.setCorpid(user.getCorpid())
                        .setUid(user.getId())
                        .setName(user.getUsername())
                        .setTimeYm(Integer.valueOf(ym))
                        .setTimeYmd(Integer.valueOf(ym + d))
                        .setTimeYmdh(Integer.valueOf(ym + d + h))
                        .setDeptUniqueId(user.getDeptUniqueId())
                        .setDeptName(user.getDeptName())
                        .setTimeStr(time)
                        .setStartTime(DateUtils.parseDate(time, StaConst.DF_SECOND))
                        .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
            } catch (ParseException e) {
                logger.warn("parseDomain1300 time parse error:{}", e.getMessage());
            }
            dataList.add(item);
        }
        if (dataList.size() > 0) {
            staLog2ParseService.insertPhotoRecord(dataList);
        }
    }

    private void parseDomain1400(List<JSONObject> originList, Map<Long, UserCache> userCacheMap) {
        if (originList.size() <= 0) {
            return;
        }

        logger.debug("parseDomain1400 size:{}", originList.size());

        List<StaUserTmpgroupRecord> dataList = new ArrayList<>(originList.size());

        for (JSONObject json : originList) {
            //items: id, uid, time, session
            Integer id = json.getInteger("id");
            Long uid = json.getLong("uid");
            String time = json.getString("time");// 2020-06-10 15:32:16
            String name = json.getString("name");

            if (null == id || null == uid || null == time || null == name) {
                logger.warn("parseDomain1400 content invalid:{}", json.toJSONString());
                continue;
            }
            String ym, d, h;
            try {
                ym = time.substring(0, 4) + time.substring(5, 7);
                d = time.substring(8, 10);
                h = time.substring(11, 13);
            } catch (Exception e) {
                logger.warn("parseDomain1400 time invalid:{}", json.toJSONString());
                continue;
            }

            //UserCache user = userCacheMap.get(uid);
            UserCache user = cacheHandler.getUserCache(uid);
            if (user == null) {
                logger.warn("parseDomain1400 user null, uid:{}", uid);
                continue;
            }

            StaUserTmpgroupRecord item = new StaUserTmpgroupRecord();
            try {
                item.setCorpid(user.getCorpid())
                        .setUid(user.getId())
                        .setName(user.getUsername())
                        .setTimeYm(Integer.valueOf(ym))
                        .setTimeYmd(Integer.valueOf(ym + d))
                        .setTimeYmdh(Integer.valueOf(ym + d + h))
                        .setDeptUniqueId(user.getDeptUniqueId())
                        .setDeptName(user.getDeptName())
                        .setTimeStr(time)
                        .setCreateTime(DateUtils.parseDate(time, StaConst.DF_SECOND))
                        .setName(name)
                        .setGroupName(name) // DB实际存的是此字段
                        .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
            } catch (ParseException e) {
                logger.warn("parseDomain1400 time parse error:{}", e.getMessage());
            }
            dataList.add(item);
        }
        if (dataList.size() > 0) {
            staLog2ParseService.insertTmpgroupRecord(dataList);
        }
    }

    private void parseDomain1500(List<JSONObject> originList, Map<Long, UserCache> userCacheMap) {
        if (originList.size() <= 0) {
            return;
        }

        logger.debug("parseDomain1500 size:{}", originList.size());

        List<StaUserImRecord> dataList = new ArrayList<>(originList.size());

        for (JSONObject json : originList) {
            //items: id, uid, target, type, im_type, time, session
            Integer id = json.getInteger("id");
            Long uid = json.getLong("uid");
            String time = json.getString("time");// 2020-06-10 15:32:16
            Long target = json.getLong("target");
            Integer type = json.getInteger("type"); // 通话类型：1个人，2群组
            Integer im_type = json.getInteger("im_type"); // 消息类型：1文字，2文件，3离线语音

            if (null == id || null == uid || null == time || null == target || null == type || null == im_type
                    || type < 1 || type > 2 || im_type < 1 || im_type > 3) {
                logger.warn("parseDomain1500 content invalid:{}", json.toJSONString());
                continue;
            }
            String ym, d, h;
            try {
                ym = time.substring(0, 4) + time.substring(5, 7);
                d = time.substring(8, 10);
                h = time.substring(11, 13);
            } catch (Exception e) {
                logger.warn("parseDomain1500 time invalid:{}", json.toJSONString());
                continue;
            }

            //UserCache user = userCacheMap.get(uid);
            UserCache user = cacheHandler.getUserCache(uid);
            if (user == null) {
                logger.warn("parseDomain1500 user null, uid:{}", uid);
                continue;
            }
            //UserCache targetUser = userCacheMap.get(target);
            // XXX 这里要判断个人群组
            String targetName = "";
            if (type == 1) {
                UserCache targetUser = cacheHandler.getUserCache(target);
                if (null != targetUser) {
                    targetName = targetUser.getUsername();
                }
            } else if (type == 2) {
                targetName = cacheHandler.getGroupName(target);
            }

            StaUserImRecord item = new StaUserImRecord();
            try {
                item.setCorpid(user.getCorpid())
                        .setUid(user.getId())
                        .setName(user.getUsername())
                        .setTimeYm(Integer.valueOf(ym))
                        .setTimeYmd(Integer.valueOf(ym + d))
                        .setTimeYmdh(Integer.valueOf(ym + d + h))
                        .setDeptUniqueId(user.getDeptUniqueId())
                        .setDeptName(user.getDeptName())
                        .setTimeStr(time)
                        .setStartTime(DateUtils.parseDate(time, StaConst.DF_SECOND))
                        .setTargetId(target)
                        .setTargetName(targetName)
                        .setType(type)
                        .setImType(im_type)
                        .setEndFlag(1) // 预留字段，目前固定为1（即定义为持续业务，目前实现为单次业务）
                        .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
            } catch (ParseException e) {
                logger.warn("parseDomain1500 time parse error:{}", e.getMessage());
            }
            dataList.add(item);
        }
        if (dataList.size() > 0) {
            staLog2ParseService.insertImRecord(dataList);
        }
    }

    private void parseDomain1600(List<JSONObject> originList, Map<Long, UserCache> userCacheMap) {
        if (originList.size() <= 0) {
            return;
        }

        logger.debug("parseDomain1600 size:{}", originList.size());

        List<StaUserMileageRecord> dataList = new ArrayList<>(originList.size());

        for (JSONObject json : originList) {
            //items: id, uid, time, session
            Integer id = json.getInteger("id");
            Long uid = json.getLong("uid");
            String time = json.getString("time");// 2020-06-10 15:32:16
            Integer mileage = json.getInteger("mileage");

            if (null == id || null == uid || null == time || null == mileage || mileage < 0 || mileage > 100000) {
                logger.warn("parseDomain1600 content invalid:{}", json.toJSONString());
                continue;
            }
            String ym, d, h;
            try {
                ym = time.substring(0, 4) + time.substring(5, 7);
                d = time.substring(8, 10);
                h = time.substring(11, 13);
            } catch (Exception e) {
                logger.warn("parseDomain1600 time invalid:{}", json.toJSONString());
                continue;
            }

            //UserCache user = userCacheMap.get(uid);
            UserCache user = cacheHandler.getUserCache(uid);
            if (user == null) {
                logger.warn("parseDomain1600 user null, uid:{}", uid);
                continue;
            }

            StaUserMileageRecord item = new StaUserMileageRecord();
            try {
                item.setCorpid(user.getCorpid())
                        .setUid(user.getId())
                        .setName(user.getUsername())
                        .setTimeYm(Integer.valueOf(ym))
                        .setTimeYmd(Integer.valueOf(ym + d))
                        .setTimeYmdh(Integer.valueOf(ym + d + h))
                        .setDeptUniqueId(user.getDeptUniqueId())
                        .setDeptName(user.getDeptName())
                        .setTimeStr(time)
                        .setStartTime(DateUtils.parseDate(time, StaConst.DF_SECOND))
                        .setMileage(mileage)
                        .setState(user.getRoleType() != 1 ? 1 : 2);//统计状态：1需要统计，2不需统计(领导数据)
            } catch (ParseException e) {
                logger.warn("parseDomain1600 time parse error:{}", e.getMessage());
            }
            dataList.add(item);
        }
        if (dataList.size() > 0) {
            staLog2ParseService.insertMileageRecord(dataList);
        }
    }

}
