package com.lhfeiyu.tech.DTO.OrgLog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 即时消息
public class OrgInsMessage {

    @NonNull
    private Integer id;
    @NonNull
    private Long uid;
    @NonNull
    private Long target;
    @NonNull
    private Integer type;
    @NonNull
    private Integer im_type;
    @NonNull
    private String time;
    private String session;

}
