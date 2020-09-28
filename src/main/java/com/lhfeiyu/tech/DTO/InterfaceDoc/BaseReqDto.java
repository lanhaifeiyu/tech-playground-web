package com.lhfeiyu.tech.DTO.InterfaceDoc;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseReqDto {

    private Integer start_time;
    private Integer end_time;
    private String time_level;

}
