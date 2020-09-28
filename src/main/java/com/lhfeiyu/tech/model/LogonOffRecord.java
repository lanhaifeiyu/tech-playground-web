package com.lhfeiyu.tech.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class LogonOffRecord implements Serializable {

    private Long uid;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    //  操作标识：1登录，2退出登录
    private Integer flag;

}
