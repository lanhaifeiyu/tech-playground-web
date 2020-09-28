package com.lhfeiyu.tech.service;

import com.zom.statistics.dao.mapper.logMapper.*;
import com.zom.statistics.dao.po.*;
import com.zom.statistics.domain.StaConst;
import com.zom.statistics.domain.StaTools;
import com.zom.statistics.handler.CacheHandler;
import com.zom.statistics.model.SumLog;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 30, readOnly = false, rollbackFor = Exception.class)
public class StaLog4SumService {

    @Autowired
    private CacheHandler cacheHandler;
    @Autowired
    private StaDayUserMapper staDayUserMapper;
    @Autowired
    private StaDayDeptMapper staDayDeptMapper;
    @Autowired
    private StaParsePositionMapper staParsePositionMapper;
    @Autowired
    private StaUserOnlineRecordMapper staUserOnlineRecordMapper;
    @Autowired
    private StaUserSosRecordMapper staUserSosRecordMapper;
    @Autowired
    private StaUserAudioRecordMapper staUserAudioRecordMapper;
    @Autowired
    private StaUserVideoRecordMapper staUserVideoRecordMapper;
    @Autowired
    private StaUserImRecordMapper staUserImRecordMapper;
    @Autowired
    private StaUserPhotoRecordMapper staUserPhotoRecordMapper;
    @Autowired
    private StaUserTmpgroupRecordMapper staUserTmpgroupRecordMapper;
    @Autowired
    private StaUserMileageRecordMapper staUserMileageRecordMapper;

    private static Logger logger = LoggerFactory.getLogger(StaLog4SumService.class);

    @Transactional(propagation = Propagation.REQUIRED, timeout = 30, readOnly = true, rollbackFor = Exception.class)
    public SumLog loadListToSum() {
        List<StaParsePosition> positionList = staParsePositionMapper.selectAll();
        Long last_online_id = null;
        Long last_sos_id = null;
        Long last_audio_id = null;
        Long last_video_id = null;
        Long last_im_id = null;
        Long last_photo_id = null;
        Long last_tmpgroup_id = null;
        Long last_mileage_id = null;
        for (StaParsePosition position : positionList) {
            String name = position.getTableName();
            switch (name) {
                case StaConst.sta_user_online_record:
                    last_online_id = position.getLastPkId();
                    break;
                case StaConst.sta_user_sos_record:
                    last_sos_id = position.getLastPkId();
                    break;
                case StaConst.sta_user_audio_record:
                    last_audio_id = position.getLastPkId();
                    break;
                case StaConst.sta_user_video_record:
                    last_video_id = position.getLastPkId();
                    break;
                case StaConst.sta_user_im_record:
                    last_im_id = position.getLastPkId();
                    break;
                case StaConst.sta_user_photo_record:
                    last_photo_id = position.getLastPkId();
                    break;
                case StaConst.sta_user_tmpgroup_record:
                    last_tmpgroup_id = position.getLastPkId();
                    break;
                case StaConst.sta_user_mileage_record:
                    last_mileage_id = position.getLastPkId();
                    break;
            }
        }

        List<StaUserOnlineRecord> onlineRecordList = null;
        List<StaUserSosRecord> sosRecordList = null;
        List<StaUserAudioRecord> audioRecordList = null;
        List<StaUserVideoRecord> videoRecordList = null;
        List<StaUserImRecord> imRecordList = null;
        List<StaUserPhotoRecord> photoRecordList = null;
        List<StaUserTmpgroupRecord> tmpgroupRecordList = null;
        List<StaUserMileageRecord> mileageRecordList = null;

        if (null != last_online_id) {
            onlineRecordList = staUserOnlineRecordMapper.selectListToSum(last_online_id, StaConst.SUM_SINGLE_PAGE_COUNT);
        }
        if (null != last_sos_id) {
            sosRecordList = staUserSosRecordMapper.selectListToSum(last_sos_id, StaConst.SUM_SINGLE_PAGE_COUNT);
        }
        if (null != last_audio_id) {
            audioRecordList = staUserAudioRecordMapper.selectListToSum(last_audio_id, StaConst.SUM_SINGLE_PAGE_COUNT);
        }
        if (null != last_video_id) {
            videoRecordList = staUserVideoRecordMapper.selectListToSum(last_video_id, StaConst.SUM_SINGLE_PAGE_COUNT);
        }
        if (null != last_im_id) {
            imRecordList = staUserImRecordMapper.selectListToSum(last_im_id, StaConst.SUM_SINGLE_PAGE_COUNT);
        }
        if (null != last_photo_id) {
            photoRecordList = staUserPhotoRecordMapper.selectListToSum(last_photo_id, StaConst.SUM_SINGLE_PAGE_COUNT);
        }
        if (null != last_tmpgroup_id) {
            tmpgroupRecordList = staUserTmpgroupRecordMapper.selectListToSum(last_tmpgroup_id, StaConst.SUM_SINGLE_PAGE_COUNT);
        }
        if (null != last_mileage_id) {
            mileageRecordList = staUserMileageRecordMapper.selectListToSum(last_mileage_id, StaConst.SUM_SINGLE_PAGE_COUNT);
        }

        SumLog sumLog = new SumLog();
        sumLog.setOnlineRecordList(onlineRecordList)
                .setSosRecordList(sosRecordList)
                .setAudioRecordList(audioRecordList)
                .setVideoRecordList(videoRecordList)
                .setImRecordList(imRecordList)
                .setPhotoRecordList(photoRecordList)
                .setTmpgroupRecordList(tmpgroupRecordList)
                .setMileageRecordList(mileageRecordList)
                .buildAllLastPkId();

        return sumLog;

    }

    public void insertOrUpdateSumData(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap, Map<String, StaDayCommon> staDayDeptMap, boolean onlyFreshDuration) {

        // update parse position
        updatePosition(sumLog);

        boolean userEmpty = false;
        boolean deptEmpty = false;
        if (null == staDayUserMap || staDayUserMap.isEmpty()) {
            userEmpty = true;
        }
        if (null == staDayDeptMap || staDayDeptMap.isEmpty()) {
            deptEmpty = true;
        }
        if (userEmpty && deptEmpty) {
            return;
        }
        if (!userEmpty) {
            // 先排序再转list
            staDayUserMap = MapUtils.orderedMap(staDayUserMap);
            List<StaDayCommon> staDayUserList = new ArrayList<>(staDayUserMap.size());
            staDayUserMap.forEach((k, v) -> {
                staDayUserList.add(v);
            });
            int size = staDayUserList.size();
            logger.debug("staDayUser insert-update count:{}", size);
            if (onlyFreshDuration) {
                staDayUserMapper.insertOrUpdateList(staDayUserList);
            } else {
                staDayUserMapper.insertOrUpdateList(staDayUserList);
            }
        }
        if (!deptEmpty) {
            // 先排序再转list
            staDayDeptMap = MapUtils.orderedMap(staDayDeptMap);
            List<StaDayCommon> staDayDeptList = new ArrayList<>(staDayDeptMap.size());
            staDayDeptMap.forEach((k, v) -> {
                staDayDeptList.add(v);
            });
            int size = staDayDeptList.size();
            logger.debug("staDayDept insert-update count:{}", size);
            if (onlyFreshDuration) {
                staDayDeptMapper.insertOrUpdateList(staDayDeptList);
            } else {
                staDayDeptMapper.insertOrUpdateList(staDayDeptList);
            }
        }
    }

    private void updatePosition(SumLog sumLog) {
        if (null == sumLog) { // null when fresh duration
            return;
        }

        // DONE 有个问题，如果parse_position表的业务ID的起始值与对应业务表的ID起始值不一致，那就会不匹配，比如里程表的ID起始值是100，而位置表里面却是从0开始，这就会有大量的重复解析
        // DONE 我觉得只检查一下初始状态就行了，如果位置表的起始ID为0，则与更新为业务表的起始ID-1，
        // DONE 但是还有如果ID不连续也有问题，不应该用增加量来更新，应该取业务数据中的最大ID来更新,这就会解决起始位置不致和不连续的问题

        //数据在查询出来时就已经调用，这里不再重复调用
        //sumLog.buildAllLastPkId();

        if (null != sumLog.getOnlineRecordList()) {
            int size = sumLog.getOnlineRecordList().size();
            Long lastPkId = sumLog.getOnlineRecordLastPkId();
            if (size > 0 && null != lastPkId) {
                staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_online_record);
            }
        }
        if (null != sumLog.getSosRecordList()) {
            int size = sumLog.getSosRecordList().size();
            Long lastPkId = sumLog.getSosRecordLastPkId();
            if (size > 0 && null != lastPkId) {
                staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_sos_record);
            }
        }
        if (null != sumLog.getAudioRecordList()) {
            int size = sumLog.getAudioRecordList().size();
            Long lastPkId = sumLog.getAudioRecordLastPkId();
            if (size > 0 && null != lastPkId) {
                staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_audio_record);
            }
        }
        if (null != sumLog.getVideoRecordList()) {
            int size = sumLog.getVideoRecordList().size();
            Long lastPkId = sumLog.getVideoRecordLastPkId();
            if (size > 0 && null != lastPkId) {
                staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_video_record);
            }
        }
        if (null != sumLog.getPhotoRecordList()) {
            int size = sumLog.getPhotoRecordList().size();
            Long lastPkId = sumLog.getPhotoRecordLastPkId();
            if (size > 0 && null != lastPkId) {
                staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_photo_record);
            }
        }
        if (null != sumLog.getImRecordList()) {
            int size = sumLog.getImRecordList().size();
            Long lastPkId = sumLog.getImRecordLastPkId();
            if (size > 0 && null != lastPkId) {
                staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_im_record);
            }
        }
        if (null != sumLog.getTmpgroupRecordList()) {
            int size = sumLog.getTmpgroupRecordList().size();
            Long lastPkId = sumLog.getTmpgroupRecordLastPkId();
            if (size > 0 && null != lastPkId) {
                staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_tmpgroup_record);
            }
        }
        if (null != sumLog.getMileageRecordList()) {
            int size = sumLog.getMileageRecordList().size();
            Long lastPkId = sumLog.getMileageRecordLastPkId();
            if (size > 0 && null != lastPkId) {
                staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_mileage_record);
            }
        }
    }

    public void updateFreshDuration() {
        Date now = null;
        int curYmd = 0;

        Map<String, StaDayCommon> staDayUserMap = new HashMap<>();
        Map<String, StaDayCommon> staDayDeptMap = new HashMap<>();

        freshDurationAudio(staDayUserMap, staDayDeptMap, now, curYmd);
        freshDurationVideo(staDayUserMap, staDayDeptMap, now, curYmd);
        freshDurationOnline(staDayUserMap, staDayDeptMap, now, curYmd);

        insertOrUpdateSumData(null, staDayUserMap, staDayDeptMap, true);
    }

    private int getCurYmd() {
        LocalDate startDate = LocalDateTime.now().toLocalDate(); // 拆分YMD
        int y = startDate.getYear();
        int m = startDate.getMonthValue();
        int d = startDate.getDayOfMonth();
        String ymdStr = y + "";
        ymdStr += (m < 10) ? ("0" + m) : m;
        ymdStr += (d < 10) ? ("0" + d) : d;
        int curYmd = Integer.valueOf(ymdStr);
        return curYmd;
    }

    private void addStaDayDeptMap(StaDayCommon staDayUser, String domainType, Map<String, StaDayCommon> staDayDeptMap, String deptUniqueId, Date now, int curYmd, int sub_type) {
        Map<String, String> deptMap = cacheHandler.getDeptParentUniqueIds(deptUniqueId);
        deptMap.forEach((uniqueId, deptName) -> {
            String dayDeptKey = curYmd + uniqueId; // 需要确保uniqueId为String
            StaDayCommon data;
            if (staDayDeptMap.containsKey(dayDeptKey)) {
                data = staDayDeptMap.get(dayDeptKey);
            } else {
                data = new StaDayCommon();
                data.setUniqueId(dayDeptKey)
                        .setCorpid(staDayUser.getCorpid())
                        .setTimeYmd(curYmd)
                        .setDeptUniqueId(uniqueId)
                        .setDeptName(deptName)
                        .setState(1)
                        .setUpdateTime(now);
                // 同一部门同一业务可能会有多个持续业务，所以这里的时长要做叠加
                if (domainType.equals("audio")) {
                    data.setTalkDuration(data.getTalkDuration() + staDayUser.getTalkDuration());
                    // 语音类型：1个呼，2组呼
                    if (sub_type == 1) {
                        data.setIndividualTalkDuration(data.getIndividualTalkDuration() + staDayUser.getIndividualTalkDuration());
                    } else if (sub_type == 2) {
                        data.setGroupTalkDuration(data.getGroupTalkDuration() + staDayUser.getGroupTalkDuration());
                    }

                } else if (domainType.equals("video")) {
                    data.setVideoDuration(data.getVideoDuration() + staDayUser.getVideoDuration());

                } else if (domainType.equals("online")) {
                    data.setOnlineDuration(data.getOnlineDuration() + staDayUser.getOnlineDuration());
                    if (sub_type == 2) {
                        data.setOnpostDuration(data.getOnpostDuration() + staDayUser.getOnpostDuration());
                    }
                }
                staDayDeptMap.put(dayDeptKey, data);
            }
        });
    }

    private void freshDurationAudio(Map<String, StaDayCommon> staDayUserMap, Map<String, StaDayCommon> staDayDeptMap, Date now, int curYmd) {
        List<StaUserAudioRecord> dataList = staUserAudioRecordMapper.selectOngoingListFresh();
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        if (null == now) {
            now = new Date();
            curYmd = getCurYmd();
        }
        int size = dataList.size();
        if (size < 20) {
            logger.debug("need fresh audio:{}", size);
        } else if (size < 40) {
            logger.info("need fresh audio:{}", size);
        } else {
            logger.warn("need fresh audio large:{}", size); // more than 40
        }

        for (StaUserAudioRecord item : dataList) {
            Date startTime = null != item.getLastTime() ? item.getLastTime() : item.getStartTime();
            Date endTime = now;
            int duration = StaTools.getDuration(endTime, startTime, "audio", item.getId(), true);
            if (duration == 0) {
                continue; // no need to update duration
            }
            if (duration == -1) {
                item.setEndFlag(1); // duration too large, need end

            } else {
                long uid = item.getUid();
                String dayUserKey = uid + "" + curYmd;
                StaDayCommon data;
                if (staDayUserMap.containsKey(dayUserKey)) {
                    data = staDayUserMap.get(dayUserKey);
                } else {
                    data = new StaDayCommon();
                }
                // 假定同一用户同一业务不会有多个持续业务，所以这里的时长不做叠加
                data.setUniqueId(dayUserKey)
                        .setCorpid(item.getCorpid())
                        .setUid(uid)
                        .setName(item.getName())
                        .setTimeYmd(curYmd)
                        .setDeptUniqueId(item.getDeptUniqueId())
                        .setDeptName(item.getDeptName())
                        .setState(1)
                        .setUpdateTime(now)
                        .setTalkDuration(duration);
                // 语音类型：1个呼，2组呼
                if (item.getType() == 1) {
                    data.setIndividualTalkDuration(duration);
                } else if (item.getType() == 2) {
                    data.setGroupTalkDuration(duration);
                }
                staDayUserMap.put(dayUserKey, data);
                addStaDayDeptMap(data, "audio", staDayDeptMap, item.getDeptUniqueId(), now, curYmd, item.getType());
            }

            item.setLastTime(now);
            int endFlag = item.getEndFlag();
            if (endFlag == 3) { // update: last_time
                item.setEndFlag(null); // 设置为NULL则不更新此字段
                if (null == item.getEndTime()) { // 这里判断endTime是为了当endTime为空时能够更新为当前时间
                    item.setEndTime(now);
                } else {
                    item.setEndTime(null); // 已经有值，设置为NULL则不更新此字段
                }
            } else if (endFlag == 1) {
                item.setEndTime(now);

            } else {  // update: end_flag, end_time, last_time
                item.setEndFlag(4);
                item.setEndTime(now);
            }
            int pair_duration = StaTools.getDuration(item.getEndTime(), item.getStartTime(), "audio", item.getId(), false);
            if (pair_duration > 0) {
                item.setDuration(pair_duration); // 刷新持续业务表的时长
            }
            staUserAudioRecordMapper.updateFresh(item);
        }
    }

    private void freshDurationVideo(Map<String, StaDayCommon> staDayUserMap, Map<String, StaDayCommon> staDayDeptMap, Date now, int curYmd) {
        List<StaUserVideoRecord> dataList = staUserVideoRecordMapper.selectOngoingListFresh();
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        if (null == now) {
            now = new Date();
            curYmd = getCurYmd();
        }
        int size = dataList.size();
        if (size < 20) {
            logger.debug("need fresh video:{}", size);
        } else if (size < 40) {
            logger.info("need fresh video:{}", size);
        } else {
            logger.warn("need fresh video large:{}", size); // more than 40
        }

        for (StaUserVideoRecord item : dataList) {
            Date startTime = null != item.getLastTime() ? item.getLastTime() : item.getStartTime();
            Date endTime = now;
            int duration = StaTools.getDuration(endTime, startTime, "video", item.getId(), true);
            if (duration == 0) {
                continue; // no need to update duration
            }
            if (duration == -1) {
                item.setEndFlag(1); // duration too large, need end

            } else {
                long uid = item.getUid();
                String dayUserKey = uid + "" + curYmd;
                StaDayCommon data;
                if (staDayUserMap.containsKey(dayUserKey)) {
                    data = staDayUserMap.get(dayUserKey);
                } else {
                    data = new StaDayCommon();
                }
                // 假定同一用户同一业务不会有多个持续业务，所以这里的时长不做叠加
                data.setUniqueId(dayUserKey)
                        .setCorpid(item.getCorpid())
                        .setUid(uid)
                        .setName(item.getName())
                        .setTimeYmd(curYmd)
                        .setDeptUniqueId(item.getDeptUniqueId())
                        .setDeptName(item.getDeptName())
                        .setState(1)
                        .setUpdateTime(now)
                        .setVideoDuration(duration); // 视频只有一个总的duration
                staDayUserMap.put(dayUserKey, data);
                addStaDayDeptMap(data, "video", staDayDeptMap, item.getDeptUniqueId(), now, curYmd, 1);
            }

            item.setLastTime(now);
            int endFlag = item.getEndFlag();
            if (endFlag == 3) { // update: last_time
                item.setEndFlag(null); // 设置为NULL则不更新此字段
                if (null == item.getEndTime()) { // 这里判断endTime是为了当endTime为空时能够更新为当前时间
                    item.setEndTime(now);
                } else {
                    item.setEndTime(null); // 已经有值，设置为NULL则不更新此字段
                }
            } else if (endFlag == 1) {
                item.setEndTime(now);

            } else {  // update: end_flag, end_time, last_time
                item.setEndFlag(4);
                item.setEndTime(now);
            }
            int pair_duration = StaTools.getDuration(item.getEndTime(), item.getStartTime(), "video", item.getId(), false);
            if (pair_duration > 0) {
                item.setDuration(pair_duration); // 刷新持续业务表的时长
            }
            staUserVideoRecordMapper.updateFresh(item);
        }
    }

    private void freshDurationOnline(Map<String, StaDayCommon> staDayUserMap, Map<String, StaDayCommon> staDayDeptMap, Date now, int curYmd) {
        List<StaUserOnlineRecord> dataList = staUserOnlineRecordMapper.selectOngoingListFresh();
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        if (null == now) {
            now = new Date();
            curYmd = getCurYmd();
        }
        int size = dataList.size();
        if (size < 20) {
            logger.debug("need fresh online:{}", size);
        } else if (size < 40) {
            logger.info("need fresh online:{}", size);
        } else {
            logger.warn("need fresh online large:{}", size); // more than 40
        }

        for (StaUserOnlineRecord item : dataList) {
            Date startTime = null != item.getLastTime() ? item.getLastTime() : item.getLogonTime();
            Date endTime = now;
            int duration = StaTools.getDuration(endTime, startTime, "online", item.getId(), true);
            if (duration == 0) {
                continue; // no need to update duration
            }
            if (duration == -1) {
                item.setEndFlag(1); // duration too large, need end

            } else {
                long uid = item.getUid();
                String dayUserKey = uid + "" + curYmd;
                StaDayCommon data;
                if (staDayUserMap.containsKey(dayUserKey)) {
                    data = staDayUserMap.get(dayUserKey);
                } else {
                    data = new StaDayCommon();
                }
                // 假定同一用户同一业务不会有多个持续业务，所以这里的时长不做叠加
                data.setUniqueId(dayUserKey)
                        .setCorpid(item.getCorpid())
                        .setUid(uid)
                        .setName(item.getName())
                        .setTimeYmd(curYmd)
                        .setDeptUniqueId(item.getDeptUniqueId())
                        .setDeptName(item.getDeptName())
                        .setState(1)
                        .setUpdateTime(now)
                        .setOnlineDuration(duration);
                int sub_type = 1;
                if (null != item.getIsOnduty() && item.getIsOnduty() == 1) {
                    data.setOnpostDuration(duration);
                    sub_type = 2;
                }
                staDayUserMap.put(dayUserKey, data);
                addStaDayDeptMap(data, "online", staDayDeptMap, item.getDeptUniqueId(), now, curYmd, sub_type);
            }

            item.setLastTime(now);
            int endFlag = item.getEndFlag();
            if (endFlag == 3) { // update: last_time
                item.setEndFlag(null); // 设置为NULL则不更新此字段
                if (null == item.getLogoffTime()) { // 这里判断endTime是为了当endTime为空时能够更新为当前时间
                    item.setLogoffTime(now);
                } else {
                    item.setLogoffTime(null); // 已经有值，设置为NULL则不更新此字段
                }
            } else if (endFlag == 1) {
                item.setLogoffTime(now);

            } else {  // update: end_flag, end_time, last_time
                item.setEndFlag(4);
                item.setLogoffTime(now);
            }
            int pair_duration = StaTools.getDuration(item.getLogoffTime(), item.getLogonTime(), "online", item.getId(), false);
            if (pair_duration > 0) {
                item.setOnlineDuration(pair_duration); // 刷新持续业务表的时长
            }
            staUserOnlineRecordMapper.updateFresh(item);
        }
    }

}
