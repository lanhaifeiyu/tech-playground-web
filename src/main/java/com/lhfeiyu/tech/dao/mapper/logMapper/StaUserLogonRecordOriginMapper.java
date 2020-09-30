package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.lhfeiyu.tech.dao.mapper.common.BaseMapper;
import com.lhfeiyu.tech.dao.po.StaUserLogonRecordOrigin;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaUserLogonRecordOrigin
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 用户登录记录表
 * @time 2020-02-27 09:57:44
 */
public interface StaUserLogonRecordOriginMapper extends BaseMapper<StaUserLogonRecordOrigin> {

    // -- old below --

    List<StaUserLogonRecordOrigin> selectLogonOutByLastId (@Param("id") int laseId);

    List<StaUserLogonRecordOrigin> selectLogonByUids (@Param("uids") String uids);

    // select methods

    StaUserLogonRecordOrigin load(long id);

    StaUserLogonRecordOrigin selectByPrimaryKey(long id);

    List<StaUserLogonRecordOrigin> selectListByCondition(Map<String, Object> map);

    StaUserLogonRecordOrigin selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);

    // update methods

    int updateByPrimaryKey(StaUserLogonRecordOrigin staUserLogonRecordOrigin);

    int updateByPrimaryKeySelective(StaUserLogonRecordOrigin staUserLogonRecordOrigin);

    // insert methods

    int insert(StaUserLogonRecordOrigin staUserLogonRecordOrigin);

    int insertSelective(StaUserLogonRecordOrigin staUserLogonRecordOrigin);

    int insertBatch(List<StaUserLogonRecordOrigin> staUserLogonRecordOrigin);

    // delete methods

    int deleteByPrimaryKey(long id);

}