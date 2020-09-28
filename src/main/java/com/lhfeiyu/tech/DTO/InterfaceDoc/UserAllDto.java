package com.lhfeiyu.tech.DTO.InterfaceDoc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserAllDto extends BaseReqDto {

    private String dept_id;
    private String uids;
}
