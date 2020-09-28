package com.lhfeiyu.tech.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class UserCache implements Serializable {

    private Long id;
    private Integer corpid;
    private String  username;
    private String  deptUniqueId;
    private String  deptName;
    private Integer roleId;// for bind_duty: setBindDuty(user.getRoleId() == 0 ? 0 : 1)
    private Integer roleType;// 统计状态：1需要统计，2不需统计(领导数据)
}
