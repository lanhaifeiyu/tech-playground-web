package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.zom.statistics.DTO.ContinueBusiness;
import com.zom.statistics.dao.mapper.common.BaseMapper;
import com.zom.statistics.dao.po.StaUserOnlineRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserOnlineRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 用户在线时长计算表(base on logon_record)
 * @time 2020-02-27 09:57:44
 */
public interface StaUserOnlineRecordMapper extends BaseMapper<StaUserOnlineRecord> {

    List<StaUserOnlineRecord> selectOngoingList();

    /** 加载开始时间超过5分钟的业务数据来刷新时长 */
    List<StaUserOnlineRecord> selectOngoingListFresh();

    List<StaUserOnlineRecord> selectListToSum(@Param("lastPkId") Long lastPkId, @Param("count") Integer count);

    int updateStateEnd(StaUserOnlineRecord staUserOnlineRecord);

    int updateFresh(StaUserOnlineRecord staUserOnlineRecord);

    // -- old below --

    List<StaUserOnlineRecord> selectOnlineTimeByUidAndDate(@Param("uid") String uid, @Param("date") String date);

    List<StaUserOnlineRecord> selectByDate(@Param("date") String date);

    List<StaUserOnlineRecord> select(StaUserOnlineRecord staUserOnlineRecord);

    List<StaUserOnlineRecord> selectByEndFlagAndLastId(@Param("endFlag") Integer endFlag, @Param("lastId") Long lastId);

    List<StaUserOnlineRecord> selectByEndFlagAndLastIdLimit(@Param("endFlag") Integer endFlag, @Param("lastId") Long lastId);

    List<ContinueBusiness> selectOngoing(@Param("uid") Integer uid, @Param("uniqueIds") List<String> uniqueIds);

    int updateEndFlagById(@Param("ids") String ids);

    // select methods

    StaUserOnlineRecord load(long id);

    StaUserOnlineRecord selectByPrimaryKey(long id);

    List<StaUserOnlineRecord> selectListByCondition(Map<String, Object> map);

    StaUserOnlineRecord selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserOnlineRecord staUserOnlineRecord);

    int updateByPrimaryKeySelective(StaUserOnlineRecord staUserOnlineRecord);

    // insert methods

    int insert(StaUserOnlineRecord staUserOnlineRecord);

    int insertSelective(StaUserOnlineRecord staUserOnlineRecord);

    int insertBatch(List<StaUserOnlineRecord> staUserOnlineRecord);

    // delete methods

    int deleteByPrimaryKey(long id);

}