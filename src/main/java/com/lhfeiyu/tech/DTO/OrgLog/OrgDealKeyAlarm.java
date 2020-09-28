package com.lhfeiyu.tech.DTO.OrgLog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 处理一键告警
public class OrgDealKeyAlarm {

    @NonNull
    private Integer id;
    @NonNull
    private Long uid;
    @NonNull
    private Long handle_uid;
    @NonNull
    private String time;
    private String location;
    private String marker;
    private String session;

}
