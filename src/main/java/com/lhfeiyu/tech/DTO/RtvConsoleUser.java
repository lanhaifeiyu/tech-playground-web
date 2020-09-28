package com.lhfeiyu.tech.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class RtvConsoleUser {

    private int id;
    private String userName;
    private String password;
    private String salt;
    private String phone;
    private int corpId;
    private Long adminId;
    private String displayName;
    private String departmentId;
    private Integer departmentFkId;
    private Integer auth;
    private Integer extId;

}
