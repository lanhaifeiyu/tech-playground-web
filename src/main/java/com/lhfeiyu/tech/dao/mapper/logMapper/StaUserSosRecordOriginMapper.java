package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.lhfeiyu.tech.dao.mapper.common.BaseMapper;
import com.lhfeiyu.tech.dao.po.StaUserSosRecordOrigin;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserSosRecordOrigin
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 一键告警记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserSosRecordOriginMapper extends BaseMapper<StaUserSosRecordOrigin> {

    // -- old below --

    List<StaUserSosRecordOrigin> selectDealMsg (@Param("id") Integer id, @Param("flag") Integer flag);

    List<StaUserSosRecordOrigin> selectByUidsAndFlag (@Param("uids") String uids, @Param("flag") Integer flag);


    // select methods

    StaUserSosRecordOrigin load(long id);

    StaUserSosRecordOrigin selectByPrimaryKey(long id);

    List<StaUserSosRecordOrigin> selectListByCondition(Map<String, Object> map);

    StaUserSosRecordOrigin selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserSosRecordOrigin staUserSosRecordOrigin);

    int updateByPrimaryKeySelective(StaUserSosRecordOrigin staUserSosRecordOrigin);

    // insert methods

    int insert(StaUserSosRecordOrigin staUserSosRecordOrigin);

    int insertSelective(StaUserSosRecordOrigin staUserSosRecordOrigin);

    int insertBatch(List<StaUserSosRecordOrigin> staUserSosRecordOrigin);

    // delete methods

    int deleteByPrimaryKey(long id);

}