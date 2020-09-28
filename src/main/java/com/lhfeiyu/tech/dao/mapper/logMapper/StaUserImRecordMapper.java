package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.zom.statistics.dao.mapper.common.BaseMapper;
import com.zom.statistics.dao.po.StaUserImRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserImRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 即时消息记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserImRecordMapper extends BaseMapper<StaUserImRecord> {

    List<StaUserImRecord> selectListToSum(@Param("lastPkId") Long lastPkId, @Param("count") Integer count);

    // select methods

    StaUserImRecord load(long id);

    StaUserImRecord selectByPrimaryKey(long id);

    List<StaUserImRecord> selectListByCondition(Map<String, Object> map);

    StaUserImRecord selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    List<StaUserImRecord> selectMsgByUidsAndDate (@Param("uids") String uids, @Param("date") String date);

    // update methods

    int updateByPrimaryKey(StaUserImRecord staUserImRecord);

    int updateByPrimaryKeySelective(StaUserImRecord staUserImRecord);

    // insert methods

    int insert(StaUserImRecord staUserImRecord);

    int insertSelective(StaUserImRecord staUserImRecord);

    int insertBatch(List<StaUserImRecord> staUserImRecord);

    // delete methods

    int deleteByPrimaryKey(long id);

}