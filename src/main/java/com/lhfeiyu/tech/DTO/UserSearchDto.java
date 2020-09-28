package com.lhfeiyu.tech.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserSearchDto {

    private String displayName;
    private String phone;
    private Integer corpId;

}
