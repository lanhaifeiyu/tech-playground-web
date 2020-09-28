package com.lhfeiyu.tech.vo;

import lombok.Data;

@Data
public class WebSocketRequest {

    // TODO 需要根据统计接口的格式重新定义消息字段

    private Long    ts;
    private Long    from;
    private Long    to;
    private String  content;
    private Integer module;
    private Integer state;

    //重要注意事项：收发的消息类，必须存在"无参的默认构造函数"，否则topic订阅会出问题，而且代码不报错！
    /*public WebSocketRequest() {
    }*/

}
