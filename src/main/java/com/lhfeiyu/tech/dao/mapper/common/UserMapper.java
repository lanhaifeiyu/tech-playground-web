package com.lhfeiyu.tech.dao.mapper.common;

import com.lhfeiyu.tech.DTO.User;
import com.lhfeiyu.tech.DTO.UserSearchDto;
import com.lhfeiyu.tech.model.UserCache;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    UserCache loadToCache(@Param("id") Long id);

    // -- old below --

    List<User> selectByIdIn(@Param("ids") String ids);

    User selectById(@Param("id") Long id);

    String selectNameById(@Param("id") Long id);

    List<User> selectAllByIdIn(@Param("ids") String ids);

    List<User> selectByUniqueIdIn(@Param("uniqueIdIn") String uniqueIdIn, @Param("corpId") Integer corpId);

    List<User> select(UserSearchDto userSearchDto);
}
