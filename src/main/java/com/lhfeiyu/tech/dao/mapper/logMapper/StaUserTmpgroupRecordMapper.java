package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.zom.statistics.dao.mapper.common.BaseMapper;
import com.zom.statistics.dao.po.StaUserTmpgroupRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserTmpgroupRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 临时组创建记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserTmpgroupRecordMapper extends BaseMapper<StaUserTmpgroupRecord> {

    List<StaUserTmpgroupRecord> selectListToSum(@Param("lastPkId") Long lastPkId, @Param("count") Integer count);

    // -- old below --

    List<StaUserTmpgroupRecord> selectMsgByUidsAndDate (@Param("uids") String uids, @Param("date") String date);


    // select methods

    StaUserTmpgroupRecord load(long id);

    StaUserTmpgroupRecord selectByPrimaryKey(long id);

    List<StaUserTmpgroupRecord> selectListByCondition(Map<String, Object> map);

    StaUserTmpgroupRecord selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);


    // update methods

    int updateByPrimaryKey(StaUserTmpgroupRecord staUserTmpgroupRecord);

    int updateByPrimaryKeySelective(StaUserTmpgroupRecord staUserTmpgroupRecord);

    // insert methods

    int insert(StaUserTmpgroupRecord staUserTmpgroupRecord);

    int insertSelective(StaUserTmpgroupRecord staUserTmpgroupRecord);

    int insertBatch(List<StaUserTmpgroupRecord> staUserTmpgroupRecord);

    // delete methods

    int deleteByPrimaryKey(long id);

}