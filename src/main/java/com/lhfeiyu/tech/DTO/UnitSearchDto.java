package com.lhfeiyu.tech.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UnitSearchDto {

    private String name;
    private String shortName;

}
