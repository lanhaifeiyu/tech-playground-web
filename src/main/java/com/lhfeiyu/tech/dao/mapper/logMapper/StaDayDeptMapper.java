package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.zom.statistics.dao.po.StaDayCommon;
import com.zom.statistics.dao.po.StaDayDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaDayDept
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 部门按天汇总统计表
 * @time 2020-02-27 09:57:44
 */
public interface StaDayDeptMapper {

    int insertOrUpdateList(List<StaDayCommon> list);

    // -- old below --

    StaDayDept selectByUniqueIdAndTimeYmd(@Param("uniqueId") String uniqueId, @Param("date") String timeYmd);

    List<StaDayDept> selectAllByCondition(Map<String, Object> map);

    int selectAllByConditionCount(Map<String, Object> map);

    List<StaDayDept> selectByTimeYmd(@Param("timeYmd") String timeYmd);

    List<StaDayDept> selectAll(StaDayDept staDayDept);

    List<StaDayDept> selectByUniqueIdAndTime(@Param("uniqueId") String uniqueId, @Param("startTime") Integer startTime, @Param("endTime") Integer endTime);

    List<StaDayDept> selectAudioCountByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectAudioDurationByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectVideoCountByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectVideoDurationByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectPhotoCountByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectSosCountByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectImCountByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectOnlineDurationByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectOnpostDurationByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectMileageCountByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectLogonCountByUniqueIdAndDate(Map<String, Object> map);

    List<StaDayDept> selectTmpGroupCountByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String date);

    List<StaDayDept> selectCurrentOnlineByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId);

    int selectUnitOnlineAll(List<String> list);

    List<StaDayDept> selectByUniqueId(@Param("uniqueId") String uniqueId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 通过uniqueId和时间，模糊查询出统计后的数据
     *
     * @param uniqueId
     * @param month
     * @return
     */
    List<StaDayDept> selectByUniqueIdAndDate(@Param("uniqueIdIn") String uniqueId, @Param("date") String month);

    /**
     * 通过uniqueId和时间，模糊查询出一条统计后的数据
     *
     * @param uniqueId
     * @param month
     * @return
     */
    StaDayDept selectByDateAndUniqueId(@Param("uniqueId") String uniqueId, @Param("date") String month);

    List<StaDayDept> selectByUniqueIdInAndDates(@Param("uniqueIds") List<String> uniqueIds, @Param("dates") List<String> dates);

    List<StaDayDept> selectUseOfCount(@Param("uniqueIds") List<String> uniqueIds, @Param("dates") List<String> dates);

    // select methods

    StaDayDept load(long id);

    StaDayDept selectByPrimaryKey(long id);

    List<StaDayDept> selectListByCondition(Map<String, Object> map);

    StaDayDept selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaDayDept staDayDept);

    int updateByPrimaryKeySelective(StaDayDept staDayDept);

    // insert methods

    int insert(StaDayDept staDayDept);

    int insertSelective(StaDayDept staDayDept);

    int insertBatch(List<StaDayDept> staDayDept);

    // delete methods

    int deleteByPrimaryKey(long id);

}