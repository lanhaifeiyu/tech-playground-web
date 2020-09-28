package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.zom.statistics.DTO.ContinueBusiness;
import com.zom.statistics.dao.mapper.common.BaseMapper;
import com.zom.statistics.dao.po.StaUserVideoRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserVideoRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 视频通话记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserVideoRecordMapper extends BaseMapper<StaUserVideoRecord> {

    List<StaUserVideoRecord> selectOngoingList();

    /** 加载开始时间超过5分钟的业务数据来刷新时长 */
    List<StaUserVideoRecord> selectOngoingListFresh();

    List<StaUserVideoRecord> selectListToSum(@Param("lastPkId") Long lastPkId, @Param("count") Integer count);

    int updateStateEnd(StaUserVideoRecord staUserVideoRecord);

    int updateFresh(StaUserVideoRecord staUserVideoRecord);

    int updateStateEndBeforeLogonOff(@Param("uid") Long uid, @Param("endTime") Date endTime);

    // -- old below --

    List<StaUserVideoRecord> selectMsgByUidsAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaUserVideoRecord> selectByState(@Param("state") int state);

    List<StaUserVideoRecord> selectByEndFlagAndLastId(@Param("endFlag") Integer endFlag, @Param("lastId") Long lastId);

    List<StaUserVideoRecord> selectByEndFlagAndLastIdLimit(@Param("endFlag") Integer endFlag, @Param("lastId") Long lastId);

    List<String> selectSessionByUidAndType(@Param("uid") Integer uid, @Param("type") int type);

    List<ContinueBusiness> selectOngoing(@Param("uid") Integer uid, @Param("uniqueIds") List<String> uniqueIds);

    List<StaUserVideoRecord> selectByIdIn(@Param("ids") String id);

    int updateEndFlagById(@Param("ids") String ids);

    // select methods

    StaUserVideoRecord load(long id);

    StaUserVideoRecord selectByPrimaryKey(long id);

    List<StaUserVideoRecord> selectListByCondition(Map<String, Object> map);

    StaUserVideoRecord selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserVideoRecord staUserVideoRecord);

    int updateByPrimaryKeySelective(StaUserVideoRecord staUserVideoRecord);

    // insert methods

    int insert(StaUserVideoRecord staUserVideoRecord);

    int insertSelective(StaUserVideoRecord staUserVideoRecord);

    int insertBatch(List<StaUserVideoRecord> staUserVideoRecord);

    // delete methods

    int deleteByPrimaryKey(long id);

}