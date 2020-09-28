package com.lhfeiyu.tech.DTO.InterfaceDoc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CommonResponseParams<T> {

    @NonNull
    private Integer code;
    @NonNull
    private boolean success;
    private String err_msg;
    @NonNull
    private Integer start;
    @NonNull
    private Integer count;
    private String req_id;
    private Integer total;
    private Integer total_page;
    private Integer page;
    private T data;
    private String key_start;
    private String key_count;
    private String key_req_id;
    private String key_data;
    private Integer key_total;

}
