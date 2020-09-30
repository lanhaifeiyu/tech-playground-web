package com.lhfeiyu.tech.service;

import com.lhfeiyu.tech.dao.mapper.logMapper.StaOriginalLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaLog1SourceService {

    @Autowired
    private StaOriginalLogMapper staOriginalLogMapper;

    private static Logger logger = LoggerFactory.getLogger(StaLog1SourceService.class);

    @Transactional(propagation = Propagation.REQUIRED, timeout = 30, readOnly = false, rollbackFor = Exception.class)
    public int insertOriginalLog(List<String> dataList) {
        if (null == dataList || dataList.size() <= 0) {
            logger.debug("source do insert, insertOriginalLog list empty ignore");
            return 0;
        }
        int real_count = dataList.size();
        logger.debug("source do insert:{}", real_count);
        int count = staOriginalLogMapper.insertBatchByStringList(dataList);
        return count;
    }

}
