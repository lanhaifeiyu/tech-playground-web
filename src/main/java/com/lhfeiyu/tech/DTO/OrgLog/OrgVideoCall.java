package com.lhfeiyu.tech.DTO.OrgLog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 视频通话
public class OrgVideoCall {

    @NotNull
    private Integer id;
    @NonNull
    private Integer sub_id;
    @NotNull
    private Long uid;
    @NotNull
    private Long target;
    private String start_time;
    private String end_time;
    @NotNull
    private Integer flag;
    private String session;


}
