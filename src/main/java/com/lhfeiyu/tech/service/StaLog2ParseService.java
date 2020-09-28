package com.lhfeiyu.tech.service;

import com.zom.statistics.dao.mapper.logMapper.*;
import com.zom.statistics.dao.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 30, readOnly = false, rollbackFor = Exception.class)
public class StaLog2ParseService {

    @Autowired
    private StaOriginalLogMapper staOriginalLogMapper;
    @Autowired
    private StaParsePositionMapper staParsePositionMapper;
    @Autowired
    private StaUserLogonRecordOriginMapper staUserLogonRecordOriginMapper;
    @Autowired
    private StaUserAudioRecordOriginMapper staUserAudioRecordOriginMapper;
    @Autowired
    private StaUserImRecordMapper staUserImRecordMapper;
    @Autowired
    private StaUserMileageRecordMapper staUserMileageRecordMapper;
    @Autowired
    private StaUserPhotoRecordMapper staUserPhotoRecordMapper;
    @Autowired
    private StaUserSosRecordOriginMapper staUserSosRecordOriginMapper;
    @Autowired
    private StaUserTmpgroupRecordMapper staUserTmpgroupRecordMapper;
    @Autowired
    private StaUserVideoRecordOriginMapper staUserVideoRecordOriginMapper;

    private static Logger logger = LoggerFactory.getLogger(StaLog2ParseService.class);

    @Transactional(propagation = Propagation.REQUIRED, timeout = 30, readOnly = true, rollbackFor = Exception.class)
    public List<StaOriginalLog> getListByPage(int count) {
        Long lastPkId = staParsePositionMapper.selectLastPkIdByTableName("sta_original_log");
        if (lastPkId == null) {
            logger.error("fatal error: sta_original_log parse position is null, stop parsing");
            return null;
        }
        List<StaOriginalLog> dataList = staOriginalLogMapper.selectListByLastPkId(lastPkId, count);
        logger.debug("origin load start:{},count:{}", lastPkId, count);
        return dataList;
    }

    public int updateLastPkId(long lastPkId, String tableName) {
        return staParsePositionMapper.updateLastPkIdByTableName(lastPkId, tableName);
    }

    public int insertLogonRecordOrigin(List<StaUserLogonRecordOrigin> dataList) {
        if (null != dataList && dataList.size() > 0) {
            return staUserLogonRecordOriginMapper.insertBatch(dataList);
        }
        return 0;
    }

    public int insertSosRecordOrigin(List<StaUserSosRecordOrigin> dataList) {
        if (null != dataList && dataList.size() > 0) {
            return staUserSosRecordOriginMapper.insertBatch(dataList);
        }
        return 0;
    }

    public int insertAudioRecordOrigin(List<StaUserAudioRecordOrigin> dataList) {
        if (null != dataList && dataList.size() > 0) {
            return staUserAudioRecordOriginMapper.insertBatch(dataList);
        }
        return 0;
    }

    public int insertVideoRecordOrigin(List<StaUserVideoRecordOrigin> dataList) {
        if (null != dataList && dataList.size() > 0) {
            return staUserVideoRecordOriginMapper.insertBatch(dataList);
        }
        return 0;
    }

    public int insertPhotoRecord(List<StaUserPhotoRecord> dataList) {
        if (null != dataList && dataList.size() > 0) {
            return staUserPhotoRecordMapper.insertBatch(dataList);
        }
        return 0;
    }

    public int insertTmpgroupRecord(List<StaUserTmpgroupRecord> dataList) {
        if (null != dataList && dataList.size() > 0) {
            return staUserTmpgroupRecordMapper.insertBatch(dataList);
        }
        return 0;
    }

    public int insertImRecord(List<StaUserImRecord> dataList) {
        if (null != dataList && dataList.size() > 0) {
            return staUserImRecordMapper.insertBatch(dataList);
        }
        return 0;
    }

    public int insertMileageRecord(List<StaUserMileageRecord> dataList) {
        if (null != dataList && dataList.size() > 0) {
            return staUserMileageRecordMapper.insertBatch(dataList);
        }
        return 0;
    }

}
