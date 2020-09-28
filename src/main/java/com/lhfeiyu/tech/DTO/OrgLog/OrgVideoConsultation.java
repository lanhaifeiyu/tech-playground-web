package com.lhfeiyu.tech.DTO.OrgLog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 视频会商
public class OrgVideoConsultation {

    @NonNull
    private Integer id;
    @NonNull
    private Integer sub_id;
    @NonNull
    private Long uid;
    private String start_time;
    private String end_time;
    @NonNull
    private Integer flag;
    private String session;

}
