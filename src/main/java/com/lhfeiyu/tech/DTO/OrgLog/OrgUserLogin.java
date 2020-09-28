package com.lhfeiyu.tech.DTO.OrgLog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 用户登录
public class OrgUserLogin {

    @NonNull
    private Integer id;
    @NonNull
    private Long uid;
    @NonNull
    private String time;
    // 等于1时，为真正的登录
    private int real;
    private String session;

}
