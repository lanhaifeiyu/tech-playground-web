package com.lhfeiyu.tech.dao.mapper.logMapper;

import com.zom.statistics.dao.po.StaOriginalLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yuronghua-airson
 * @description Mybatis Mapper: StaOriginalLog
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 原始业务日志记录表（定期清理）
 * @time 2020-02-27 09:57:44
 */
public interface StaOriginalLogMapper {

    // https://blog.csdn.net/u014687389/article/details/72778664
    // https://blog.csdn.net/weixin_38553453/article/details/75139613
    int insertBatchByStringList(List<String> dataList);

    List<StaOriginalLog> selectListByLastPkId(@Param("lastPkId") long lastPkId, @Param("count") int count);

    // -- old below --

    @Deprecated
    List<StaOriginalLog> selectByStartId(long startId);
    @Deprecated
    int insertList (@Param("list") List<StaOriginalLog> staUserLogonRecord);


    // select methods

    StaOriginalLog load(long id);

    StaOriginalLog selectByPrimaryKey(long id);

    List<StaOriginalLog> selectListByCondition(Map<String, Object> map);

    StaOriginalLog selectByCondition(Map<String, Object> map);

    Long selectCountByCondition(Map<String, Object> map);


    // update methods

    int updateByPrimaryKey(StaOriginalLog staOriginalLog);

    int updateByPrimaryKeySelective(StaOriginalLog staOriginalLog);

    // insert methods

    int insert(StaOriginalLog staOriginalLog);

    int insertSelective(StaOriginalLog staOriginalLog);

    int insertBatch(List<StaOriginalLog> staOriginalLog);

    // delete methods

    int deleteByPrimaryKey(long id);

}