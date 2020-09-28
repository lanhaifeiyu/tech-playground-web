package com.lhfeiyu.tech.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class User implements Serializable {

    private Long id;
    private String displayName;
    private int corpId;
    private String departmentId;
    private String deptName;
    // 用户角色
    private int userRoleId;
    // 是否为领导 1：是    2：否
    private int roleType;
    // 角色id
    private int roleId;

}
