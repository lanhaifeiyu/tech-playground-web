package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.zom.statistics.dao.mapper.common.BaseMapper;
import com.zom.statistics.dao.po.StaDayCommon;
import com.zom.statistics.dao.po.StaDayUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaDayUser
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 用户按天汇总统计表
 * @time 2020-02-27 09:57:44
 */
public interface StaDayUserMapper extends BaseMapper<StaDayUser> {

    int insertOrUpdateList(List<StaDayCommon> list);

    // -- old below --

    List<StaDayUser> selectByDay(@Param("day") String day);

    List<StaDayUser> selectByLastId(@Param("id") Long id);

    List<StaDayUser> selectAllByCondition(@Param("deptIdList") List<Integer> deptIdList,
                                          @Param("deptIdList") List<Long> uidList,
                                          @Param("time_level") String time_level,
                                          @Param("start_time") Integer start_time,
                                          @Param("end_time") Integer end_time,
                                          @Param("start") Integer start,
                                          @Param("count") Integer count);

    List<StaDayUser> selectAllByCondition(Map<String, Object> map);

    int selectAllByConditionCount(Map<String, Object> map);

    /**
     * 通过部门uniqueId或者用户id或者日期来查询
     *
     * @param uniqueId
     * @param uids
     * @param month
     * @return
     */
    List<StaDayUser> selectByUniqueIdAndUidsAndDate(@Param("uniqueId") String uniqueId,
                                                    @Param("uids") String uids,
                                                    @Param("date") String month);

    List<StaDayUser> selectByUidAndUniqueIdsAndUidsAndDate(@Param("uniqueIdList") List<String> uniqueIdList,
                                                           @Param("uids") List<Integer> uidList,
                                                           @Param("date") String month);

    StaDayUser selectByUidAndDate(@Param("uid") String uid, @Param("date") String month);

    List<StaDayUser> selectUserByDateAndUid(@Param("uids") List<Integer> uids, @Param("dates") List dates);

    List<StaDayUser> selectAudioCountByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectAudioDurationByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectVideoCountByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectVideoDurationByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectPhotoCountByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectSosCountByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectImCountByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectOnlineDurationByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectOnpostDurationByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectMileageCountByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectLogonCountByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectLogonCountByUniqueIdAndDate(Map<String, Object> map);

    List<StaDayUser> selectTmpGroupCountByUniqueIdAndDate(@Param("uids") String uids, @Param("date") String date);

    List<StaDayUser> selectByUniqueId(@Param("uniqueId") String uniqueId,
                                      @Param("startTime") String startTime,
                                      @Param("endTime") String endTime);

    List<StaDayUser> selectByUidIn(@Param("uidIn") String uidIn, @Param("timeYmd") Integer timeYmd);

    List<Integer> selectUidByUniqueId(@Param("uniqueId") String uniqueId);

    List<Integer> selectUidByUniqueIdIn(@Param("uniqueIdIn") String uniqueId);

    // select methods

    StaDayUser load(long id);

    StaDayUser selectByPrimaryKey(long id);

    List<StaDayUser> selectListByCondition(Map<String, Object> map);

    StaDayUser selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaDayUser staDayUser);

    int updateByPrimaryKeySelective(StaDayUser staDayUser);

    // insert methods

    int insert(StaDayUser staDayUser);

    int insertSelective(StaDayUser staDayUser);

    int insertBatch(List<StaDayUser> staDayUser);

    // delete methods

    int deleteByPrimaryKey(long id);

}