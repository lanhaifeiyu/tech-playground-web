package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.lhfeiyu.tech.DTO.ContinueBusiness;
import com.lhfeiyu.tech.dao.mapper.common.BaseMapper;
import com.lhfeiyu.tech.dao.po.StaUserAudioRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserAudioRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 语音通话记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserAudioRecordMapper extends BaseMapper<StaUserAudioRecord> {

    List<StaUserAudioRecord> selectOngoingList();

    /** 加载开始时间超过5分钟的业务数据来刷新时长 */
    List<StaUserAudioRecord> selectOngoingListFresh();

    List<StaUserAudioRecord> selectListToSum(@Param("lastPkId") Long lastPkId, @Param("count") Integer count);

    int updateStateEnd(StaUserAudioRecord staUserAudioRecord);

    int updateFresh(StaUserAudioRecord staUserAudioRecord);

    int updateStateEndBeforeLogonOff(@Param("uid") Long uid, @Param("endTime") Date endTime);

    // -- old below --

    List<StaUserAudioRecord> selectMsgByUidsAndDate (@Param("uids") String uids, @Param("date") String date);

    List<StaUserAudioRecord> selectByState (@Param("state") int state);

    List<StaUserAudioRecord> selectByEndFlagAndLastId (@Param("endFlag") Integer endFlag, @Param("lastId") Long lastId);

    List<StaUserAudioRecord> selectByEndFlagAndLastIdLimit (@Param("endFlag") Integer endFlag, @Param("lastId") Long lastId);

    List<ContinueBusiness> selectOngoing (@Param("uid") Integer uid, @Param("uniqueIds") List<String> uniqueIds);

    /**
     * 查询未结束的任务
     * @param ids
     * @return
     */
    List<StaUserAudioRecord> selectByIdIn (@Param("ids") String ids);

    int updateEndFlagById (@Param("ids") String ids);

    // select methods

    StaUserAudioRecord load(long id);

    StaUserAudioRecord selectByPrimaryKey(long id);

    List<StaUserAudioRecord> selectListByCondition(Map<String, Object> map);

    StaUserAudioRecord selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserAudioRecord staUserAudioRecord);

    int updateByPrimaryKeySelective(StaUserAudioRecord staUserAudioRecord);

    // insert methods

    int insert(StaUserAudioRecord staUserAudioRecord);

    int insertSelective(StaUserAudioRecord staUserAudioRecord);

    int insertBatch(List<StaUserAudioRecord> staUserAudioRecord);

    // delete methods

    int deleteByPrimaryKey(long id);

}