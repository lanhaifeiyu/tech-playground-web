package com.lhfeiyu.tech.DTO.InterfaceDoc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ValueMaxAndMin extends BaseReqDto {

    private Integer min;
    private Integer max;

}
