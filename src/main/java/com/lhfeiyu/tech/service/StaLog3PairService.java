package com.lhfeiyu.tech.service;

import com.zom.statistics.dao.mapper.logMapper.*;
import com.zom.statistics.dao.po.StaUserAudioRecord;
import com.zom.statistics.dao.po.StaUserOnlineRecord;
import com.zom.statistics.dao.po.StaUserSosRecord;
import com.zom.statistics.dao.po.StaUserVideoRecord;
import com.zom.statistics.domain.StaConst;
import com.zom.statistics.model.LogonOffRecord;
import com.zom.statistics.model.PairLog;
import com.zom.statistics.model.PairLogOngoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 30, readOnly = false, rollbackFor = Exception.class)
public class StaLog3PairService {

    @Autowired
    private StaParsePositionMapper staParsePositionMapper;
    @Autowired
    private StaUserOnlineRecordMapper staUserOnlineRecordMapper;
    @Autowired
    private StaUserAudioRecordMapper staUserAudioRecordMapper;
    @Autowired
    private StaUserVideoRecordMapper staUserVideoRecordMapper;
    @Autowired
    private StaUserSosRecordMapper staUserSosRecordMapper;

    private static Logger logger = LoggerFactory.getLogger(StaLog3PairService.class);

    @Transactional(propagation = Propagation.REQUIRED, timeout = 30, readOnly = true, rollbackFor = Exception.class)
    public void loadOngoingList(PairLog pairLog, PairLogOngoing pairLogOngoing) {
        // init
        pairLogOngoing.setOnlineRecordMap(new HashMap<>());
        pairLogOngoing.setAudioRecordMap(new HashMap<>());
        pairLogOngoing.setVideoRecordMap(new HashMap<>());
        pairLogOngoing.setSosRecordMap(new HashMap<>());

        if (null != pairLog.getAudioRecordOriginList() && pairLog.getAudioRecordOriginList().size() > 0) {
            List<StaUserAudioRecord> dataList = staUserAudioRecordMapper.selectOngoingList();
            if (null != dataList && dataList.size() > 0) {
                Map<Long, StaUserAudioRecord> dataMap = new HashMap<>();
                for (StaUserAudioRecord item : dataList) {
                    dataMap.put(item.getUid(), item);
                }
                pairLogOngoing.setAudioRecordMap(dataMap);
            }
        }
        if (null != pairLog.getVideoRecordOriginList() && pairLog.getVideoRecordOriginList().size() > 0) {
            List<StaUserVideoRecord> dataList = staUserVideoRecordMapper.selectOngoingList();
            if (null != dataList && dataList.size() > 0) {
                Map<Long, StaUserVideoRecord> dataMap = new HashMap<>();
                for (StaUserVideoRecord item : dataList) {
                    dataMap.put(item.getUid(), item);
                }
                pairLogOngoing.setVideoRecordMap(dataMap);
            }
        }
        if (null != pairLog.getSosRecordOriginList() && pairLog.getSosRecordOriginList().size() > 0) {
            List<StaUserSosRecord> dataList = staUserSosRecordMapper.selectOngoingList();
            if (null != dataList && dataList.size() > 0) {
                Map<Long, StaUserSosRecord> dataMap = new HashMap<>();
                for (StaUserSosRecord item : dataList) {
                    dataMap.put(item.getUid(), item);
                }
                pairLogOngoing.setSosRecordMap(dataMap);
            }
        }
        if (null != pairLog.getLogonRecordOriginList() && pairLog.getLogonRecordOriginList().size() > 0) {
            List<StaUserOnlineRecord> dataList = staUserOnlineRecordMapper.selectOngoingList();
            if (null != dataList && dataList.size() > 0) {
                Map<Long, StaUserOnlineRecord> dataMap = new HashMap<>();
                for (StaUserOnlineRecord item : dataList) {
                    dataMap.put(item.getUid(), item);
                }
                pairLogOngoing.setOnlineRecordMap(dataMap);
            }
        }
    }

    public void updateOnlineEndAndInsertNew(List<StaUserOnlineRecord> endDataList, List<StaUserOnlineRecord> newDataList, Long lastPkId) {
        if (null != endDataList && endDataList.size() > 0) {
            for (StaUserOnlineRecord item : endDataList) {
                staUserOnlineRecordMapper.updateStateEnd(item);
            }
        }
        if (null != newDataList && newDataList.size() > 0) {
            staUserOnlineRecordMapper.insertBatch(newDataList);
        }
        // XXX STEP: update parse position
        if (null != lastPkId && lastPkId > 0) {
            staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_logon_record_origin);
        }
    }

    public void updateAudioEndAndInsertNew(List<StaUserAudioRecord> endDataList, List<StaUserAudioRecord> newDataList, Long lastPkId) {
        if (null != endDataList && endDataList.size() > 0) {
            for (StaUserAudioRecord item : endDataList) {
                staUserAudioRecordMapper.updateStateEnd(item);
            }
        }
        if (null != newDataList && newDataList.size() > 0) {
            staUserAudioRecordMapper.insertBatch(newDataList);
        }
        // XXX STEP: update parse position
        if (null != lastPkId && lastPkId > 0) {
            staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_audio_record_origin);
        }
    }

    public void updateVideoEndAndInsertNew(List<StaUserVideoRecord> endDataList, List<StaUserVideoRecord> newDataList, Long lastPkId) {
        if (null != endDataList && endDataList.size() > 0) {
            for (StaUserVideoRecord item : endDataList) {
                staUserVideoRecordMapper.updateStateEnd(item);
            }
        }
        if (null != newDataList && newDataList.size() > 0) {
            staUserVideoRecordMapper.insertBatch(newDataList);
        }
        // XXX STEP: update parse position
        if (null != lastPkId && lastPkId > 0) {
            staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_video_record_origin);
        }
    }

    // sos_state: 1已发起，2已取消，3已处理，4已被覆盖（发起后又发起）
    public void updateSosStateAndInsertNew(List<StaUserSosRecord> overwriteDataList, List<StaUserSosRecord> cancelDataList,
                                           List<StaUserSosRecord> endDataList, List<StaUserSosRecord> newDataList,
                                           Long lastPkId) {
        if (null != overwriteDataList && overwriteDataList.size() > 0) {
            for (StaUserSosRecord item : overwriteDataList) {
                staUserSosRecordMapper.updateStateEnd(item);
            }
        }
        if (null != cancelDataList && cancelDataList.size() > 0) {
            for (StaUserSosRecord item : cancelDataList) {
                staUserSosRecordMapper.updateStateEnd(item);
            }
        }
        if (null != endDataList && endDataList.size() > 0) {
            for (StaUserSosRecord item : endDataList) {
                staUserSosRecordMapper.updateStateHandle(item);
            }
        }
        if (null != newDataList && newDataList.size() > 0) {
            staUserSosRecordMapper.insertBatch(newDataList);
        }
        // XXX STEP: update parse position
        if (null != lastPkId && lastPkId > 0) {
            staParsePositionMapper.updateLastPkIdByTableName(lastPkId, StaConst.sta_user_sos_record_origin);
        }
    }

    public void endOngoingBeforeLogonOff(Map<Long, LogonOffRecord> logonOffRecordMap, PairLogOngoing pairLogOngoing) {
        if (null == logonOffRecordMap || logonOffRecordMap.size() <= 0) {
            return;
        }
        //
        Map<Long, StaUserSosRecord> sosOngoingDataMap = pairLogOngoing.getSosRecordMap();
        Map<Long, StaUserAudioRecord> audioOngoingDataMap = pairLogOngoing.getAudioRecordMap();
        Map<Long, StaUserVideoRecord> videoOngoingDataMap = pairLogOngoing.getVideoRecordMap();

        logonOffRecordMap.forEach((uid, data) -> {
            if (audioOngoingDataMap.containsKey(uid)) {
                logger.info("endOngoingBeforeLogonOff audio uid:{}, time:{}", uid, data.getTime());
                staUserAudioRecordMapper.updateStateEndBeforeLogonOff(uid, data.getTime());
            } else if (videoOngoingDataMap.containsKey(uid)) {
                logger.info("endOngoingBeforeLogonOff video uid:{}, time:{}", uid, data.getTime());
                staUserVideoRecordMapper.updateStateEndBeforeLogonOff(uid, data.getTime());
            } else if (sosOngoingDataMap.containsKey(uid)) {
                logger.info("endOngoingBeforeLogonOff sos uid:{}, time:{}", uid, data.getTime());
                staUserSosRecordMapper.updateStateEndBeforeLogonOff(uid, data.getTime());
            }
        });
    }

}
