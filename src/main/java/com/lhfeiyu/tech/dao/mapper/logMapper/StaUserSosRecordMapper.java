package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.lhfeiyu.tech.dao.mapper.common.BaseMapper;
import com.lhfeiyu.tech.dao.po.StaUserSosRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserSosRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 一键告警记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserSosRecordMapper extends BaseMapper<StaUserSosRecord> {

    List<StaUserSosRecord> selectOngoingList();

    List<StaUserSosRecord> selectListToSum(@Param("lastPkId") Long lastPkId, @Param("count") Integer count);

    int updateStateEnd(StaUserSosRecord staUserSosRecord);

    int updateStateHandle(StaUserSosRecord staUserSosRecord);

    int updateStateEndBeforeLogonOff(@Param("uid") Long uid, @Param("endTime") Date endTime);

    // -- old below --

    List<StaUserSosRecord> selectByState(@Param("state") int state);

    List<StaUserSosRecord> selectMsgByUidsAndDate(@Param("uids") String uids, @Param("date") String date);


    // select methods

    StaUserSosRecord load(long id);

    StaUserSosRecord selectByPrimaryKey(long id);

    List<StaUserSosRecord> selectListByCondition(Map<String, Object> map);

    StaUserSosRecord selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserSosRecord staUserSosRecord);

    int updateByPrimaryKeySelective(StaUserSosRecord staUserSosRecord);

    // insert methods

    int insert(StaUserSosRecord staUserSosRecord);

    int insertSelective(StaUserSosRecord staUserSosRecord);

    int insertBatch(List<StaUserSosRecord> staUserSosRecord);

    // delete methods

    int deleteByPrimaryKey(long id);

}