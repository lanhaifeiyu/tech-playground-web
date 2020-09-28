package com.lhfeiyu.tech.DTO.InterfaceDoc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserRequestParams extends ValueMaxAndMin {

    private String uids;
    private int type;

}
