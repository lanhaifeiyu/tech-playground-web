package com.lhfeiyu.tech.DTO.OrgLog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 里程数更新
public class OrgMileage {

    @NonNull
    private Integer id;
    @NonNull
    private Long uid;
    @NonNull
    private String time;
    @NonNull
    private Integer mileage;
    private String session;

}
