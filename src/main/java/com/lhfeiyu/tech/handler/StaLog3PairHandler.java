package com.lhfeiyu.tech.handler;

import com.lhfeiyu.tech.dao.po.*;
import com.lhfeiyu.tech.domain.StaConst;
import com.lhfeiyu.tech.domain.StaTools;
import com.lhfeiyu.tech.model.LogonOffRecord;
import com.lhfeiyu.tech.model.PairLog;
import com.lhfeiyu.tech.model.PairLogOngoing;
import com.lhfeiyu.tech.service.StaLog3PairService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 原始日志解析
 *
 * @author airson
 */
@Component
public class StaLog3PairHandler {

    @Autowired
    private StaLog3PairService staLog3PairService;

    private static Logger logger = LoggerFactory.getLogger(StaLog3PairHandler.class);

    /**
     * pair logs from origin(video, audio, online, sos)
     */
    public void pairLog(PairLog pairLog, Map<Long, LogonOffRecord> logonOffRecordMap) {
        if (null == pairLog) {
            logger.warn("pairLogFromOrigin pairLog is null");
            return;
        }
        PairLogOngoing pairLogOngoing = new PairLogOngoing();

        staLog3PairService.loadOngoingList(pairLog, pairLogOngoing);

        // 目前下面的解析还不满足出错后只影响当前统计数据，解析和更新位置是一个事务，此处已经不是原始日志出现无法恢复的错误的概率较小，暂不解决
        // 临时错误就可以等到下轮解析时正常解析，比如数据库当时处于无法连接状态等

        pair_logon_record_origin(pairLog.getLogonRecordOriginList(), pairLogOngoing);
        pair_sos_record_origin(pairLog.getSosRecordOriginList(), pairLogOngoing);
        pair_audio_record_origin(pairLog.getAudioRecordOriginList(), pairLogOngoing);
        pair_video_record_origin(pairLog.getVideoRecordOriginList(), pairLogOngoing);

        //业务解析完后，结束logonOffRecordList的TS之前开始的持续业务
        staLog3PairService.endOngoingBeforeLogonOff(logonOffRecordMap, pairLogOngoing);

    }

    private void pair_logon_record_origin(List<StaUserLogonRecordOrigin> dataList, PairLogOngoing pairLogOngoing) {
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        //int size = dataList.size();

        Map<Long, StaUserOnlineRecord> ongoingDataMap = pairLogOngoing.getOnlineRecordMap();

        List<StaUserOnlineRecord> newDataList = new ArrayList<>(dataList.size());
        List<StaUserOnlineRecord> endDataList = new ArrayList<>(dataList.size());

        for (StaUserLogonRecordOrigin item : dataList) {
            Long uid = item.getUid();
            int flag = item.getFlag();  // 操作标识：1开始，2结束
            if (flag == 1) {
                // 业务开始 》 检查是否有未结束 》 有 》 结束上一个业务，并开始新的业务，如果时间相同则忽略
                // -------------------------  无 》 标识业务为开始状态，并定时刷新业务持续时长
                if (ongoingDataMap.containsKey(uid)) {
                    // 由于online offline发得比较频繁，遇到重复的online直接忽略
                    logger.debug("ignore repeat online,uid:{}", uid);
                    continue;
                    /*if (item.getTimeStr().equals(DateFormatUtils.format(item.getTime(), StaConst.DF_SECOND))) {
                        continue;
                    }
                    StaUserOnlineRecord data = ongoingDataMap.get(uid);
                    data.setLogoffTime(item.getTime());
                    data.setEndFlag(1);
                    int duration = StaTools.getDuration(data.getLogoffTime(), data.getLogonTime(), "online", data.getId(), false);
                    data.setOnlineDuration(duration);
                    endDataList.add(data);
                    ongoingDataMap.remove(uid); // remove from cache*/

                }
                // 标识业务为开始状态，并定时刷新业务持续时长
                StaUserOnlineRecord newData = new StaUserOnlineRecord();
                newData.setCorpid(item.getCorpid())
                        .setUid(item.getUid())
                        .setName(item.getName())
                        .setTimeYm(item.getTimeYm())
                        .setTimeYmd(item.getTimeYmd())
                        .setTimeYmdh(item.getTimeYmdh())
                        .setDeptUniqueId(item.getDeptUniqueId())
                        .setDeptName(item.getDeptName())
                        .setLogonTime(item.getTime())
                        .setIsOnduty(item.getBindDuty())
                        .setEndFlag(3)
                        .setSession(item.getSession())
                        .setReal(item.getReal())
                        .setState(item.getState());
                newDataList.add(newData);
                ongoingDataMap.put(uid, newData); // add to cache，此时newDate是没有主键ID的

            } else if (flag == 2) {
                // 业务结束 》 检查是否有未结束 》 有 》 结束业务
                // -------------------------  无 》 无效数据，丢弃
                if (ongoingDataMap.containsKey(uid)) {
                    // 结束业务
                    StaUserOnlineRecord data = ongoingDataMap.get(uid);
                    data.setLogoffTime(item.getTime());
                    data.setEndFlag(1);
                    int duration = StaTools.getDuration(data.getLogoffTime(), data.getLogonTime(), "online", data.getId(), false);
                    data.setOnlineDuration(duration);
                    if (null == data.getId()) {
                        // 此时数据在newDataList，没有主键ID，不能放到endDataList，这里修改了data，也是直接修改了newDataList中的对应数据
                    } else {
                        endDataList.add(data);
                    }
                    ongoingDataMap.remove(uid); // remove from cache

                } else {
                    // 无效数据，丢弃
                    continue;
                }
            }
        }

        // XXX: UPDATE: endDataList  INSERT: newDataList
        long startPkId = dataList.get(0).getId();
        long endPkId = dataList.get(dataList.size() - 1).getId();
        long lastPkId = endPkId > startPkId ? endPkId : startPkId;
        staLog3PairService.updateOnlineEndAndInsertNew(endDataList, newDataList, lastPkId);
    }

    private void pair_sos_record_origin(List<StaUserSosRecordOrigin> dataList, PairLogOngoing pairLogOngoing) {
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        //int size = dataList.size();

        Map<Long, StaUserSosRecord> ongoingDataMap = pairLogOngoing.getSosRecordMap();

        List<StaUserSosRecord> overwriteDataList = new ArrayList<>();
        List<StaUserSosRecord> cancelDataList = new ArrayList<>();
        List<StaUserSosRecord> newDataList = new ArrayList<>();
        List<StaUserSosRecord> endDataList = new ArrayList<>();

        for (StaUserSosRecordOrigin item : dataList) {
            Long uid = item.getUid();
            int flag = item.getFlag();  // 操作标识：1开始，2取消，3结束
            if (flag == 1) { // 开始
                // 业务开始 》 检查是否有未结束 》 有 》 结束上一个业务（已被覆盖），并开始新的业务，如果时间相同则忽略
                // -------------------------  无 》 标识业务为开始状态，并定时刷新业务持续时长
                //boolean updated_new_data = false;
                if (ongoingDataMap.containsKey(uid)) {
                    // 结束上一个业务，并开始新的业务，如果时间相同则忽略
                    if (item.getTimeStr().equals(DateFormatUtils.format(item.getTime(), StaConst.DF_SECOND))) {
                        continue;
                    }
                    StaUserSosRecord data = ongoingDataMap.get(uid);
                    data.setEndTime(item.getTime());
                    data.setSosState(4); // 告警状态：1已发起，2已取消，3已处理，4已被覆盖（发起后又发起）
                    if (null == data.getId()) {
                        // 此时数据在newDataList，没有主键ID，不能放到endDataList，这里修改了data，也是直接修改了newDataList中的对应数据
                    } else {
                        overwriteDataList.add(data);
                    }
                    ongoingDataMap.remove(uid); // remove from cache

                }
                // 标识业务为开始状态，并定时刷新业务持续时长

                StaUserSosRecord newData = new StaUserSosRecord();
                newData.setCorpid(item.getCorpid())
                        .setUid(item.getUid())
                        .setName(item.getName())
                        .setTimeYm(item.getTimeYm())
                        .setTimeYmd(item.getTimeYmd())
                        .setTimeYmdh(item.getTimeYmdh())
                        .setDeptUniqueId(item.getDeptUniqueId())
                        .setDeptName(item.getDeptName())
                        .setLocation(item.getLocation())
                        .setMarker(item.getMarker())
                        .setStartTime(item.getTime())
                        .setEndFlag(3)
                        .setSosState(1) // 告警状态：1已发起，2已取消，3已处理，4已被覆盖（发起后又发起）
                        .setSession(item.getSession())
                        .setState(item.getState());
                newDataList.add(newData);
                ongoingDataMap.put(uid, newData); // add to cache，此时newDate是没有主键ID的


            } else if (flag == 2) { //取消
                // 业务结束 》 检查是否有未结束 》 有 》 取消业务
                // -------------------------  无 》 无效数据，丢弃
                if (ongoingDataMap.containsKey(uid)) {
                    // 取消业务
                    StaUserSosRecord data = ongoingDataMap.get(uid);
                    data.setEndTime(item.getTime());
                    data.setSosState(2);
                    if (null == data.getId()) {
                        // 此时数据在newDataList，没有主键ID，不能放到endDataList，这里修改了data，也是直接修改了newDataList中的对应数据
                    } else {
                        cancelDataList.add(data);
                    }
                    ongoingDataMap.remove(uid); // remove from cache

                } else {
                    // 无效数据，丢弃
                    continue;
                }
            } else if (flag == 3) { //结束
                // 业务结束 》 检查是否有未结束 》 有 》 结束业务
                // -------------------------  无 》 无效数据，丢弃
                if (ongoingDataMap.containsKey(uid)) {
                    // 结束业务，需要更新处理人，所以不能直接通过ID串更新状态
                    StaUserSosRecord data = ongoingDataMap.get(uid);
                    data.setHandleTime(item.getTime())
                            .setHandleUid(item.getHandleUid())
                            .setHandleName(item.getHandleName())
                            .setEndTime(item.getTime())
                            .setSosState(3); // 告警状态：1已发起，2已取消，3已处理，4已被覆盖（发起后又发起）
                    if (null == data.getId()) {
                        // 此时数据在newDataList，没有主键ID，不能放到endDataList，这里修改了data，也是直接修改了newDataList中的对应数据
                    } else {
                        endDataList.add(data);
                    }
                    ongoingDataMap.remove(uid); // remove from cache

                } else {
                    // 无效数据，丢弃
                    continue;
                }
            }
        }

        // XXX: UPDATE: overwriteDataList,cancelDataList,endDataList  INSERT: newDataList
        long startPkId = dataList.get(0).getId();
        long endPkId = dataList.get(dataList.size() - 1).getId();
        long lastPkId = endPkId > startPkId ? endPkId : startPkId;
        staLog3PairService.updateSosStateAndInsertNew(overwriteDataList, cancelDataList, endDataList, newDataList, lastPkId);
    }

    private void pair_audio_record_origin(List<StaUserAudioRecordOrigin> dataList, PairLogOngoing pairLogOngoing) {
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        //int size = dataList.size();

        Map<Long, StaUserAudioRecord> ongoingDataMap = pairLogOngoing.getAudioRecordMap();

        List<StaUserAudioRecord> newDataList = new ArrayList<>(dataList.size());
        List<StaUserAudioRecord> endDataList = new ArrayList<>(dataList.size());

        for (StaUserAudioRecordOrigin item : dataList) {
            Long uid = item.getUid();
            Long targetId = item.getTargetId();
            int flag = item.getFlag();  // 操作标识：1开始，2结束
            if (flag == 1) {
                /**
                 * XXX：有一种场景是u1发起通话，u2结束通话，这时开始日志和结束日志产生的uid不一致，所以针对语音通话，不能只通过uid判断匹配，
                 * 若uid不匹配，还要判断uid与target是否相等，若相等，则视为一对操作。
                 */
                // 业务开始 》 检查是否有未结束 》 有 》 结束上一个业务，并开始新的业务，如果时间相同则忽略
                // -------------------------  无 》 标识业务为开始状态，并定时刷新业务持续时长
                //boolean updated_new_data = false;
                if (ongoingDataMap.containsKey(uid) || ongoingDataMap.containsKey(targetId)) {
                    // 结束上一个业务，并开始新的业务，如果时间相同则忽略
                    if (item.getTimeStr().equals(DateFormatUtils.format(item.getTime(), StaConst.DF_SECOND))) {
                        continue;
                    }
                    StaUserAudioRecord data = ongoingDataMap.get(uid);
                    data.setEndTime(item.getTime());
                    data.setEndFlag(1);
                    int duration = StaTools.getDuration(data.getEndTime(), data.getStartTime(), "audio", data.getId(), false);
                    data.setDuration(duration);
                    if (null == data.getId()) {
                        // 此时数据在newDataList，没有主键ID，不能放到endDataList，这里修改了data，也是直接修改了newDataList中的对应数据

                    } else {
                        endDataList.add(data);
                    }
                    ongoingDataMap.remove(uid); // remove from cache

                }
                // 标识业务为开始状态，并定时刷新业务持续时长

                StaUserAudioRecord newData = new StaUserAudioRecord();
                newData.setCorpid(item.getCorpid())
                        .setUid(item.getUid())
                        .setName(item.getName())
                        .setTimeYm(item.getTimeYm())
                        .setTimeYmd(item.getTimeYmd())
                        .setTimeYmdh(item.getTimeYmdh())
                        .setDeptUniqueId(item.getDeptUniqueId())
                        .setDeptName(item.getDeptName())
                        .setStartTime(item.getTime())
                        .setTargetId(item.getTargetId())
                        .setTargetName(item.getTargetName())
                        .setType(item.getType())
                        .setEndFlag(3)
                        .setSession(item.getSession())
                        .setState(item.getState());
                newDataList.add(newData);
                ongoingDataMap.put(uid, newData); // add to cache，此时newDate是没有主键ID的


            } else if (flag == 2) {
                // 业务结束 》 检查是否有未结束 》 有 》 结束业务
                // -------------------------  无 》 无效数据，丢弃
                if (ongoingDataMap.containsKey(uid) || ongoingDataMap.containsKey(targetId)) {
                    // 结束业务
                    StaUserAudioRecord data = ongoingDataMap.get(uid);
                    data.setEndTime(item.getTime());
                    data.setEndFlag(1);
                    int duration = StaTools.getDuration(data.getEndTime(), data.getStartTime(), "audio", data.getId(), false);
                    data.setDuration(duration);
                    if (null == data.getId()) {
                        // 此时数据在newDataList，没有主键ID，不能放到endDataList，这里修改了data，也是直接修改了newDataList中的对应数据
                    } else {
                        endDataList.add(data);
                    }
                    ongoingDataMap.remove(uid); // remove from cache

                } else {
                    // 无效数据，丢弃
                    continue;
                }
            }
        }

        // XXX: UPDATE: endDataList  INSERT: newDataList
        long startPkId = dataList.get(0).getId();
        long endPkId = dataList.get(dataList.size() - 1).getId();
        long lastPkId = endPkId > startPkId ? endPkId : startPkId;
        staLog3PairService.updateAudioEndAndInsertNew(endDataList, newDataList, lastPkId);

    }

    private void pair_video_record_origin(List<StaUserVideoRecordOrigin> dataList, PairLogOngoing pairLogOngoing) {
        if (null == dataList || dataList.size() == 0) {
            return;
        }
        //int size = dataList.size();

        Map<Long, StaUserVideoRecord> ongoingDataMap = pairLogOngoing.getVideoRecordMap();

        List<StaUserVideoRecord> newDataList = new ArrayList<>(dataList.size());
        List<StaUserVideoRecord> endDataList = new ArrayList<>(dataList.size());

        // 1201为视频通话，其他3个业务一样是持续业务，只是没有target，有相同的uid和session

        for (StaUserVideoRecordOrigin item : dataList) {
            Long uid = item.getUid();
            Long targetId = item.getTargetId();
            int flag = item.getFlag();  // 操作标识：1开始，2结束
            if (flag == 1) {
                /**
                 * XXX：有一种场景是u1发起通话，u2结束通话，这时开始日志和结束日志产生的uid不一致，所以针对语音通话，不能只通过uid判断匹配，
                 * 若uid不匹配，还要判断uid与target是否相等，若相等，则视为一对操作。
                 */
                // 业务开始 》 检查是否有未结束 》 有 》 结束上一个业务，并开始新的业务，如果时间相同则忽略
                // -------------------------  无 》 标识业务为开始状态，并定时刷新业务持续时长
                //boolean updated_new_data = false;
                if (ongoingDataMap.containsKey(uid) || ongoingDataMap.containsKey(targetId)) {
                    // 结束上一个业务，并开始新的业务，如果时间相同则忽略
                    if (item.getTimeStr().equals(DateFormatUtils.format(item.getTime(), StaConst.DF_SECOND))) {
                        continue;
                    }
                    StaUserVideoRecord data = ongoingDataMap.get(uid);
                    data.setEndTime(item.getTime());
                    data.setEndFlag(1);
                    int duration = StaTools.getDuration(data.getEndTime(), data.getStartTime(), "video", data.getId(), false);
                    data.setDuration(duration);
                    if (null == data.getId()) {
                        // 此时数据在newDataList，没有主键ID，不能放到endDataList，这里修改了data，也是直接修改了newDataList中的对应数据
                    } else {
                        endDataList.add(data);
                    }
                    ongoingDataMap.remove(uid); // remove from cache

                }
                // 标识业务为开始状态，并定时刷新业务持续时长
                StaUserVideoRecord newData = new StaUserVideoRecord();
                newData.setCorpid(item.getCorpid())
                        .setUid(item.getUid())
                        .setName(item.getName())
                        .setTimeYm(item.getTimeYm())
                        .setTimeYmd(item.getTimeYmd())
                        .setTimeYmdh(item.getTimeYmdh())
                        .setDeptUniqueId(item.getDeptUniqueId())
                        .setDeptName(item.getDeptName())
                        .setStartTime(item.getTime())
                        .setTargetId(item.getTargetId())
                        .setTargetName(item.getTargetName())
                        .setType(item.getType())
                        .setEndFlag(3)
                        .setSession(item.getSession())
                        .setState(item.getState());
                newDataList.add(newData);
                ongoingDataMap.put(uid, newData); // add to cache，此时newDate是没有主键ID的


            } else if (flag == 2) {
                /**
                 * XXX：有一种场景是u1发起通话，u2结束通话，这时开始日志和结束日志产生的uid不一致，所以针对语音通话，不能只通过uid判断匹配，
                 * 若uid不匹配，还要判断uid与target是否相等，若相等，则视为一对操作。
                 */
                // 业务结束 》 检查是否有未结束 》 有 》 结束业务
                // -------------------------  无 》 无效数据，丢弃
                if (ongoingDataMap.containsKey(uid) || ongoingDataMap.containsKey(targetId)) {
                    // 结束业务
                    StaUserVideoRecord data = ongoingDataMap.get(uid);
                    data.setEndTime(item.getTime());
                    data.setEndFlag(1);
                    int duration = StaTools.getDuration(data.getEndTime(), data.getStartTime(), "video", data.getId(), false);
                    data.setDuration(duration);
                    if (null == data.getId()) {
                        // 此时数据在newDataList，没有主键ID，不能放到endDataList，这里修改了data，也是直接修改了newDataList中的对应数据
                    } else {
                        endDataList.add(data);
                    }
                    ongoingDataMap.remove(uid); // remove from cache

                } else {
                    // 无效数据，丢弃
                    continue;
                }
            }
        }

        // XXX: UPDATE: endDataList  INSERT: newDataList
        long startPkId = dataList.get(0).getId();
        long endPkId = dataList.get(dataList.size() - 1).getId();
        long lastPkId = endPkId > startPkId ? endPkId : startPkId;
        staLog3PairService.updateVideoEndAndInsertNew(endDataList, newDataList, lastPkId);
    }

}
