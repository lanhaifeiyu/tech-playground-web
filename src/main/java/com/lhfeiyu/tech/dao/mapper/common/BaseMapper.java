package com.lhfeiyu.tech.dao.mapper.common;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<T> {

    // -- old below --

    List<T> selectByStartId(long startId);

    int insertList(@Param("list") List<T> staUserLogonRecord);

    List<T> selectByLastId(@Param("id") Long id);

}
