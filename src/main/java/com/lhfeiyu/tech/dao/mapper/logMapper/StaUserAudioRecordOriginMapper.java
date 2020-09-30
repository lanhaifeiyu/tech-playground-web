package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.lhfeiyu.tech.dao.mapper.common.BaseMapper;
import com.lhfeiyu.tech.dao.po.StaUserAudioRecordOrigin;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserAudioRecordOrigin
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 语音通话记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserAudioRecordOriginMapper extends BaseMapper<StaUserAudioRecordOrigin> {


    // -- old below --

    List<StaUserAudioRecordOrigin> selectByLastId (@Param("id") int lastId);

    List<StaUserAudioRecordOrigin> selectStartByUidsAndType (@Param("ids") String ids, @Param("type") Integer type);

    // select methods

    StaUserAudioRecordOrigin load(long id);

    StaUserAudioRecordOrigin selectByPrimaryKey(long id);

    List<StaUserAudioRecordOrigin> selectListByCondition(Map<String, Object> map);

    StaUserAudioRecordOrigin selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserAudioRecordOrigin staUserAudioRecordOrigin);

    int updateByPrimaryKeySelective(StaUserAudioRecordOrigin staUserAudioRecordOrigin);

    // insert methods

    int insert(StaUserAudioRecordOrigin staUserAudioRecordOrigin);

    int insertSelective(StaUserAudioRecordOrigin staUserAudioRecordOrigin);

    int insertBatch(List<StaUserAudioRecordOrigin> staUserAudioRecordOrigin);

    // delete methods

    int deleteByPrimaryKey(long id);

}