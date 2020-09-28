package com.lhfeiyu.tech.vo;

import lombok.Data;

@Data
public class Message {

    // TODO 需要根据统计接口的格式重新定义消息字段

    private String  content;
    private Long    ts;
    private Integer state;
    private Integer type;
    private Long    sender;
    private Long    receiver;

}
