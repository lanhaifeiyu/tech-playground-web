package com.lhfeiyu.tech.dao.mapper.common;

import org.apache.ibatis.annotations.Param;

public interface GroupMapper {

    // -- old below --

    String selectNameById(@Param("id") Long id);
}
