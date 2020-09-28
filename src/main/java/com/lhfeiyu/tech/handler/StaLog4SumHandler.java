package com.lhfeiyu.tech.handler;

import com.zom.statistics.dao.po.*;
import com.zom.statistics.domain.StaConst;
import com.zom.statistics.model.SumCommon;
import com.zom.statistics.model.SumLog;
import com.zom.statistics.service.StaLog4SumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原始日志解析
 *
 * @author airson
 */
@Component
public class StaLog4SumHandler {

    @Autowired
    private StaLog4SumService staLog4SumService;
    @Autowired
    private CacheHandler cacheHandler;

    private static Logger logger = LoggerFactory.getLogger(StaLog4SumHandler.class);

    /**
     *
     */
    public void doSumDomain() {
        SumLog sumLog = staLog4SumService.loadListToSum();
        if (!sumLog.isDataEmpty()) {
            doSumCore(sumLog);
        }

        // XXX 定时刷新未结束的持续业务,
        // XXX 这里有个场景有问题，当业务的开始时间和结束时间都是比较早的时间时，解析到开始时会自动以当前时间结束然后刷新时长，
        // 但是解析到结束时间时发现,结束时间是一个很早的时间，这时就会多算时长。
        // 解决方案：只会查询出开始时间超过5分钟的业务来刷新时长，这5分钟就是尽量让业务能够自然结束（几十秒解析一次，已经有多次机会来解析到结束标致）
        staLog4SumService.updateFreshDuration();
    }

    public void doSumCore(SumLog sumLog) {
        Date now = new Date();

        // 存放所有需要新增或修改的数据
        Map<String, StaDayCommon> staDayUserMap = new HashMap<>();
        Map<String, StaDayCommon> staDayDeptMap = new HashMap<>();

        // XXX 不用查出staDayUser和staDayDept，就按天-UID，天-DEPTID组装数据，然后直接发insert on duplicate
        // XXX staDayUser, staDayDept新增了uniqueId字段, 设置唯一索引

        // 按业务类型组装所有数据 - 持续业务
        sumOnline(sumLog, staDayUserMap, staDayDeptMap, StaConst.sta_user_online_record, now);
        sumAudio(sumLog, staDayUserMap, staDayDeptMap, StaConst.sta_user_audio_record, now);
        sumVideo(sumLog, staDayUserMap, staDayDeptMap, StaConst.sta_user_video_record, now);
        // 按业务类型组装所有数据 - 单次业务
        sumSos(sumLog, staDayUserMap, staDayDeptMap, StaConst.sta_user_sos_record, now);
        sumIm(sumLog, staDayUserMap, staDayDeptMap, StaConst.sta_user_im_record, now);
        sumPhoto(sumLog, staDayUserMap, staDayDeptMap, StaConst.sta_user_photo_record, now);
        sumTmtgroup(sumLog, staDayUserMap, staDayDeptMap, StaConst.sta_user_tmpgroup_record, now);
        sumMileage(sumLog, staDayUserMap, staDayDeptMap, StaConst.sta_user_mileage_record, now);

        staLog4SumService.insertOrUpdateSumData(sumLog, staDayUserMap, staDayDeptMap, false);

    }

    private void increaseDayUserCount(StaDayCommon data, SumCommon sumCommon, String domainType) {
        int count = 1;
        switch (domainType) {
            case StaConst.sta_user_online_record:
                data.setOnlineDuration(data.getOnlineDuration() + sumCommon.getDuration());
                if (null != sumCommon.getIsOnduty() && sumCommon.getIsOnduty() == 1) {
                    data.setOnpostDuration(data.getOnpostDuration() + sumCommon.getDuration());
                }
                break;
            case StaConst.sta_user_sos_record:
                data.setSosCount(data.getSosCount() + count);
                break;
            case StaConst.sta_user_audio_record:
                data.setTalkCount(data.getTalkCount() + count);
                data.setTalkDuration(data.getTalkDuration() + sumCommon.getDuration());
                if (sumCommon.getType() == 1) { // 语音类型：1个呼，2组呼
                    data.setIndividualTalkCount(data.getIndividualTalkCount() + count);
                    data.setIndividualTalkDuration(data.getIndividualTalkDuration() + sumCommon.getDuration());
                } else if (sumCommon.getType() == 2) {
                    data.setGroupTalkCount(data.getGroupTalkCount() + count);
                    data.setGroupTalkDuration(data.getGroupTalkDuration() + sumCommon.getDuration());
                }
                break;
            case StaConst.sta_user_video_record:
                data.setVideoAllCount(data.getVideoAllCount() + count);
                data.setVideoDuration(data.getVideoDuration() + sumCommon.getDuration());
                if (sumCommon.getType() == 1) { // 视频类型：1视频通话，2视频回传，3视频点名，4视频会商
                    data.setVideoCallCount(data.getVideoCallCount() + count);
                } else if (sumCommon.getType() == 2) {
                    data.setVideoUploadCount(data.getVideoUploadCount() + count);
                } else if (sumCommon.getType() == 3) {
                    data.setVideoRollcallCount(data.getVideoRollcallCount() + count);
                } else if (sumCommon.getType() == 4) {
                    data.setVideoConfCount(data.getVideoConfCount() + count);
                }
                break;
            case StaConst.sta_user_im_record:
                data.setImCount(data.getImCount() + count);
                if (sumCommon.getType() == 1) { // IM类型： 1个人，2群组
                    data.setIndividualImCount(data.getIndividualImCount() + count);
                } else if (sumCommon.getType() == 2) {
                    data.setGroupImCount(data.getGroupImCount() + count);
                }
                break;
            case StaConst.sta_user_photo_record:
                data.setPhotoUploadCount(data.getPhotoUploadCount() + count);
                break;
            case StaConst.sta_user_tmpgroup_record:
                data.setTmpGroupCount(data.getTmpGroupCount() + count);
                break;
            case StaConst.sta_user_mileage_record:
                data.setMileage(data.getMileage() + sumCommon.getMileage());
                break;
        }
    }

    private void sumDayUserCore(Map<String, StaDayCommon> staDayUserMap, String domainType, SumCommon sumCommon, Date now) {
        long uid = sumCommon.getUid();
        int ymd = sumCommon.getTimeYmd();
        String dayUserKey = uid + "" + ymd;
        StaDayCommon data;
        if (staDayUserMap.containsKey(dayUserKey)) {
            data = staDayUserMap.get(dayUserKey);
        } else {
            data = new StaDayCommon();
            data.setUniqueId(dayUserKey)
                    .setCorpid(sumCommon.getCorpid())
                    .setUid(uid)
                    .setName(sumCommon.getName())
                    .setTimeYmd(ymd)
                    .setDeptUniqueId(sumCommon.getDeptUniqueId())
                    .setDeptName(sumCommon.getDeptName())
                    .setState(1)
                    .setUpdateTime(now);
            staDayUserMap.put(dayUserKey, data);
        }
        increaseDayUserCount(data, sumCommon, domainType);
    }

    private void sumDayDeptCore(Map<String, StaDayCommon> staDayDeptMap, String domainType, SumCommon sumCommon, Date now) {
        Map<String, String> deptMap = cacheHandler.getDeptParentUniqueIds(sumCommon.getDeptUniqueId());
        int ymd = sumCommon.getTimeYmd();
        Integer corpid = sumCommon.getCorpid();
        deptMap.forEach((uniqueId, deptName) -> {
            String dayDeptKey = ymd + uniqueId; // 需要确保uniqueId为String
            StaDayCommon data;
            if (staDayDeptMap.containsKey(dayDeptKey)) {
                data = staDayDeptMap.get(dayDeptKey);
            } else {
                data = new StaDayCommon();
                data.setUniqueId(dayDeptKey)
                        .setCorpid(corpid)
                        .setTimeYmd(ymd)
                        .setDeptUniqueId(uniqueId)
                        .setDeptName(deptName)
                        .setState(1)
                        .setUpdateTime(now);
                staDayDeptMap.put(dayDeptKey, data);
            }
            increaseDayUserCount(data, sumCommon, domainType);
        });
    }

    /**
     * @param sumLog
     * @param staDayUserMap
     * @param staDayDeptMap
     * @param domainType    使用对应业务的表名来做为类型判断
     * @param now
     */
    private void sumOnline(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap,
                           Map<String, StaDayCommon> staDayDeptMap, String domainType, Date now) {
        List<StaUserOnlineRecord> dataList = sumLog.getOnlineRecordList();
        if (null != dataList && dataList.size() > 0) {
            logger.debug("sumOnline size:{}", dataList.size());
            // 按uid-ymd组装数据，然后直接发insert on duplicate达到新增或修改的效果
            // XXX 需要新判断时间是否为空，结束时间是有可能为空的，表示未结束同时又没有定时刷新过, 需要考虑跨天的情况
            for (StaUserOnlineRecord item : dataList) {
                Date startTime = item.getLogonTime();
                Date endTime = item.getLogoffTime();
                if (null == startTime) {
                    logger.warn("sumOnline logonTime is null ignore,id:{}", item.getId());
                    continue;
                }
                if (null == endTime) {
                    logger.debug("sumOnline logoffTime is null, set time to now and refresh interval,id:{}", item.getId());
                    endTime = now;
                    item.setEndFlag(4);
                }
                if (endTime.before(startTime)) {
                    logger.warn("sumOnline endTime before startTime ignore,id:{}", item.getId());
                    continue;
                }

                // https://blog.csdn.net/wo541075754/article/details/102545627
                // https://blog.csdn.net/hspingcc/article/details/73332380
                LocalDateTime startDateTime = startTime.toInstant().atZone(StaConst.TIME_ZONE_ID).toLocalDateTime();
                LocalDateTime endDateTime = endTime.toInstant().atZone(StaConst.TIME_ZONE_ID).toLocalDateTime();
                LocalDate startDate = startDateTime.toLocalDate();
                LocalDate endDate = endDateTime.toLocalDate();
                if (startDate.isEqual(endDate)) {
                    SumCommon sumCommon = new SumCommon(item); // item已经设置了duration
                    /*Duration durationTime = Duration.between(startDateTime, endDateTime);
                    long duration = durationTime.getSeconds();
                    sumCommon.setDuration((int) duration);*/
                    sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                    sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);

                } else {   // 跨天，按天拆分数据
                    Duration durationTime = Duration.between(startDateTime, endDateTime);
                    long days = durationTime.toDays();
                    for (long i = 0; i <= days; i++) {
                        // 拆分时间段
                        int duration;
                        if (i == 0) {
                            duration = 86400 - startDateTime.toLocalTime().toSecondOfDay();
                        } else if (i == days) {
                            duration = endDateTime.toLocalTime().toSecondOfDay();
                        } else {
                            duration = 86400;
                        }

                        // 拆分YMD
                        startDate.plusDays(i); // 对应每天的YMD
                        int y = startDate.getYear();
                        int m = startDate.getMonthValue();
                        int d = startDate.getDayOfMonth();
                        String ymdStr = y + "";
                        ymdStr += (m < 10) ? ("0" + m) : m;
                        ymdStr += (d < 10) ? ("0" + d) : d;
                        int ymd = Integer.valueOf(ymdStr);

                        SumCommon sumCommon = new SumCommon(item);
                        sumCommon.setTimeYmd(ymd);
                        sumCommon.setDuration(duration);
                        sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                        sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);
                    }
                }
            }
        }
    }

    private void sumAudio(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap,
                          Map<String, StaDayCommon> staDayDeptMap, String domainType, Date now) {
        List<StaUserAudioRecord> dataList = sumLog.getAudioRecordList();
        if (null != dataList && dataList.size() > 0) {
            logger.debug("sumAudio size:{}", dataList.size());
            // 按uid-ymd组装数据，然后直接发insert on duplicate达到新增或修改的效果
            // XXX 需要新判断时间是否为空，结束时间是有可能为空的，表示未结束同时又没有定时刷新过, 需要考虑跨天的情况
            for (StaUserAudioRecord item : dataList) {
                Date startTime = item.getStartTime();
                Date endTime = item.getEndTime();
                if (null == startTime) {
                    logger.warn("sumAudio startTime is null ignore,id:{}", item.getId());
                    continue;
                }
                if (null == endTime) {
                    logger.debug("sumAudio endTime is null, set time to now and refresh interval,id:{}", item.getId());
                    endTime = now;
                    item.setEndFlag(4);
                }
                if (endTime.before(startTime)) {
                    logger.warn("sumAudio endTime before startTime ignore,id:{}", item.getId());
                    continue;
                }

                // https://blog.csdn.net/wo541075754/article/details/102545627
                // https://blog.csdn.net/hspingcc/article/details/73332380
                LocalDateTime startDateTime = startTime.toInstant().atZone(StaConst.TIME_ZONE_ID).toLocalDateTime();
                LocalDateTime endDateTime = endTime.toInstant().atZone(StaConst.TIME_ZONE_ID).toLocalDateTime();
                LocalDate startDate = startDateTime.toLocalDate();
                LocalDate endDate = endDateTime.toLocalDate();
                if (startDate.isEqual(endDate)) {
                    SumCommon sumCommon = new SumCommon(item);
                    sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                    sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);

                } else {   // 跨天，按天拆分数据
                    Duration durationTime = Duration.between(startDateTime, endDateTime);
                    long days = durationTime.toDays();
                    for (long i = 0; i <= days; i++) {
                        // 拆分时间段
                        int duration;
                        if (i == 0) {
                            duration = 86400 - startDateTime.toLocalTime().toSecondOfDay();
                        } else if (i == days) {
                            duration = endDateTime.toLocalTime().toSecondOfDay();
                        } else {
                            duration = 86400;
                        }

                        // 拆分YMD
                        startDate.plusDays(i); // 对应每天的YMD
                        int y = startDate.getYear();
                        int m = startDate.getMonthValue();
                        int d = startDate.getDayOfMonth();
                        String ymdStr = y + "";
                        ymdStr += (m < 10) ? ("0" + m) : m;
                        ymdStr += (d < 10) ? ("0" + d) : d;
                        int ymd = Integer.valueOf(ymdStr);

                        SumCommon sumCommon = new SumCommon(item);
                        sumCommon.setTimeYmd(ymd);
                        sumCommon.setDuration(duration);
                        sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                        sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);
                    }
                }
            }
        }
    }

    private void sumVideo(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap,
                          Map<String, StaDayCommon> staDayDeptMap, String domainType, Date now) {
        List<StaUserVideoRecord> dataList = sumLog.getVideoRecordList();
        if (null != dataList && dataList.size() > 0) {
            logger.debug("sumVideo size:{}", dataList.size());
            // 按uid-ymd组装数据，然后直接发insert on duplicate达到新增或修改的效果
            // XXX 需要新判断时间是否为空，结束时间是有可能为空的，表示未结束同时又没有定时刷新过, 需要考虑跨天的情况
            for (StaUserVideoRecord item : dataList) {
                Date startTime = item.getStartTime();
                Date endTime = item.getEndTime();
                if (null == startTime) {
                    logger.warn("sumVideo startTime is null ignore,id:{}", item.getId());
                    continue;
                }
                if (null == endTime) {
                    logger.debug("sumVideo endTime is null, set time to now and refresh interval,id:{}", item.getId());
                    endTime = now;
                    item.setEndFlag(4);
                }
                if (endTime.before(startTime)) {
                    logger.warn("sumVideo endTime before startTime ignore,id:{}", item.getId());
                    continue;
                }

                // https://blog.csdn.net/wo541075754/article/details/102545627
                // https://blog.csdn.net/hspingcc/article/details/73332380
                LocalDateTime startDateTime = startTime.toInstant().atZone(StaConst.TIME_ZONE_ID).toLocalDateTime();
                LocalDateTime endDateTime = endTime.toInstant().atZone(StaConst.TIME_ZONE_ID).toLocalDateTime();
                LocalDate startDate = startDateTime.toLocalDate();
                LocalDate endDate = endDateTime.toLocalDate();
                if (startDate.isEqual(endDate)) {
                    SumCommon sumCommon = new SumCommon(item);
                    sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                    sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);

                } else {   // 跨天，按天拆分数据
                    Duration durationTime = Duration.between(startDateTime, endDateTime);
                    long days = durationTime.toDays();
                    for (long i = 0; i <= days; i++) {
                        // 拆分时间段
                        int duration;
                        if (i == 0) {
                            duration = 86400 - startDateTime.toLocalTime().toSecondOfDay();
                        } else if (i == days) {
                            duration = endDateTime.toLocalTime().toSecondOfDay();
                        } else {
                            duration = 86400;
                        }

                        // 拆分YMD
                        startDate.plusDays(i); // 对应每天的YMD
                        int y = startDate.getYear();
                        int m = startDate.getMonthValue();
                        int d = startDate.getDayOfMonth();
                        String ymdStr = y + "";
                        ymdStr += (m < 10) ? ("0" + m) : m;
                        ymdStr += (d < 10) ? ("0" + d) : d;
                        int ymd = Integer.valueOf(ymdStr);

                        SumCommon sumCommon = new SumCommon(item);
                        sumCommon.setTimeYmd(ymd);
                        sumCommon.setDuration(duration);
                        sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                        sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);
                    }
                }
            }
        }
    }

    private void sumSos(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap,
                        Map<String, StaDayCommon> staDayDeptMap, String domainType, Date now) {
        List<StaUserSosRecord> dataList = sumLog.getSosRecordList();
        if (null != dataList && dataList.size() > 0) {
            logger.debug("sumSos size:{}", dataList.size());
            // 按uid-ymd组装数据，然后直接发insert on duplicate达到新增或修改的效果
            for (StaUserSosRecord item : dataList) {
                SumCommon sumCommon = new SumCommon(item);
                sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);
            }
        }
    }

    private void sumIm(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap,
                       Map<String, StaDayCommon> staDayDeptMap, String domainType, Date now) {
        List<StaUserImRecord> dataList = sumLog.getImRecordList();
        if (null != dataList && dataList.size() > 0) {
            logger.debug("sumIm size:{}", dataList.size());
            // 按uid-ymd组装数据，然后直接发insert on duplicate达到新增或修改的效果
            for (StaUserImRecord item : dataList) {
                SumCommon sumCommon = new SumCommon(item);
                sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);
            }
        }
    }

    private void sumPhoto(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap,
                          Map<String, StaDayCommon> staDayDeptMap, String domainType, Date now) {
        List<StaUserPhotoRecord> dataList = sumLog.getPhotoRecordList();
        if (null != dataList && dataList.size() > 0) {
            logger.debug("sumPhoto size:{}", dataList.size());
            // 按uid-ymd组装数据，然后直接发insert on duplicate达到新增或修改的效果
            for (StaUserPhotoRecord item : dataList) {
                SumCommon sumCommon = new SumCommon(item);
                sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);
            }
        }
    }

    private void sumTmtgroup(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap,
                             Map<String, StaDayCommon> staDayDeptMap, String domainType, Date now) {
        List<StaUserTmpgroupRecord> dataList = sumLog.getTmpgroupRecordList();
        if (null != dataList && dataList.size() > 0) {
            logger.debug("sumTmtgroup size:{}", dataList.size());
            // 按uid-ymd组装数据，然后直接发insert on duplicate达到新增或修改的效果
            for (StaUserTmpgroupRecord item : dataList) {
                SumCommon sumCommon = new SumCommon(item);
                sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);
            }
        }
    }

    private void sumMileage(SumLog sumLog, Map<String, StaDayCommon> staDayUserMap,
                            Map<String, StaDayCommon> staDayDeptMap, String domainType, Date now) {
        List<StaUserMileageRecord> dataList = sumLog.getMileageRecordList();
        if (null != dataList && dataList.size() > 0) {
            logger.debug("sumMileage size:{}", dataList.size());
            // 按uid-ymd组装数据，然后直接发insert on duplicate达到新增或修改的效果
            for (StaUserMileageRecord item : dataList) {
                SumCommon sumCommon = new SumCommon(item);
                sumDayUserCore(staDayUserMap, domainType, sumCommon, now);
                sumDayDeptCore(staDayDeptMap, domainType, sumCommon, now);
            }
        }
    }


}
