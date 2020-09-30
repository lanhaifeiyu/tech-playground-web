package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.lhfeiyu.tech.dao.mapper.common.BaseMapper;
import com.lhfeiyu.tech.dao.po.StaUserVideoRecordOrigin;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserVideoRecordOrigin
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 视频通话记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserVideoRecordOriginMapper extends BaseMapper<StaUserVideoRecordOrigin> {

    // -- old below --

    List<StaUserVideoRecordOrigin> selectByLastId (@Param("id") int id);

    List<StaUserVideoRecordOrigin> selectStartByUids (@Param("ids") String ids, @Param("type") int type);

    // select methods

    StaUserVideoRecordOrigin load(long id);

    StaUserVideoRecordOrigin selectByPrimaryKey(long id);

    List<StaUserVideoRecordOrigin> selectListByCondition(Map<String, Object> map);

    StaUserVideoRecordOrigin selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserVideoRecordOrigin staUserVideoRecordOrigin);

    int updateByPrimaryKeySelective(StaUserVideoRecordOrigin staUserVideoRecordOrigin);

    // insert methods

    int insert(StaUserVideoRecordOrigin staUserVideoRecordOrigin);

    int insertSelective(StaUserVideoRecordOrigin staUserVideoRecordOrigin);

    int insertBatch(List<StaUserVideoRecordOrigin> staUserVideoRecordOrigin);

    // delete methods

    int deleteByPrimaryKey(long id);

}