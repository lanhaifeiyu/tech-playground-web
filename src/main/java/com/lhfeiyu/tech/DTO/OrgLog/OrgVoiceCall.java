package com.lhfeiyu.tech.DTO.OrgLog;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 语音通话
public class OrgVoiceCall {

    @NonNull
    private Integer id;
    @NonNull
    private Long uid;
    @NonNull
    private Long target;
    @NonNull
    private Integer type;
    private String start_time;
    private String end_time;
    @NonNull
    private Integer flag;
    private String session;

}
