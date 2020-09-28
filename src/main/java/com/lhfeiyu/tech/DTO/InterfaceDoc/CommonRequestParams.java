package com.lhfeiyu.tech.DTO.InterfaceDoc;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonRequestParams {

    @NonNull
    private String token;
    private Integer start;
    private Integer count;
    private Integer page;
    private String req_id;
    private String key_start;
    private String key_count;
    private String key_req_id;
    private String key_data;
    private int key_total;
    private String chart_type;

}
