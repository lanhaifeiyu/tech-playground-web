package com.lhfeiyu.tech.DTO.OrgLog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
// 用户退出
public class OrgUserLoginOut {

    @NonNull
    private Integer id;
    @NonNull
    private Long uid;
    @NonNull
    private String time;
    // 真实登录值为1
    private int real;
    private String session;


}
