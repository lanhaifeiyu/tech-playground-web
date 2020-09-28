package com.lhfeiyu.tech.DTO.OrgLog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 创建临时组
public class OrgCreateTemporaryGroup {

    @NonNull
    private Integer id;
    @NonNull
    private Long uid;
    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String time;
    @NonNull
    private String name;
    private String session;

}
