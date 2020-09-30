package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.lhfeiyu.tech.dao.mapper.common.BaseMapper;
import com.lhfeiyu.tech.dao.po.StaUserMileageRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserMileageRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 用户里程数记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserMileageRecordMapper extends BaseMapper<StaUserMileageRecord> {

    List<StaUserMileageRecord> selectListToSum(@Param("lastPkId") Long lastPkId, @Param("count") Integer count);

    // -- old below --

    List<StaUserMileageRecord> selectMsgByUidsAndDate (@Param("uids") String uids, @Param("date") String date);

    // select methods

    StaUserMileageRecord load(long id);

    StaUserMileageRecord selectByPrimaryKey(long id);

    List<StaUserMileageRecord> selectListByCondition(Map<String, Object> map);

    StaUserMileageRecord selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserMileageRecord staUserMileageRecord);

    int updateByPrimaryKeySelective(StaUserMileageRecord staUserMileageRecord);

    // insert methods

    int insert(StaUserMileageRecord staUserMileageRecord);

    int insertSelective(StaUserMileageRecord staUserMileageRecord);

    int insertBatch(List<StaUserMileageRecord> staUserMileageRecord);

    // delete methods

    int deleteByPrimaryKey(long id);

}