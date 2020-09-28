package com.lhfeiyu.tech.dao.mapper.common;

import com.zom.statistics.DTO.RtvConsoleUser;
import org.apache.ibatis.annotations.Param;

public interface ConsoleUserMapper {

    // -- old below --

    RtvConsoleUser selectById(@Param("id") int id);

    RtvConsoleUser selectByNameAndPsw(@Param("name") String name, @Param("psw") String psw);

}