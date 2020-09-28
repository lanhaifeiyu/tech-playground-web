package com.lhfeiyu.tech.DTO.InterfaceDoc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DeptRequestParams extends ValueMaxAndMin {

    private String dept_id;
    private int dept_chain;
    private int type;

}
