package com.lhfeiyu.tech.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fenglin
 * @date 2020-04-04
 * 持续业务信息查询
 */
@Data
@Accessors(chain = true)
public class ContinueBusiness {

    private Integer corpId;
//    private String name;
    private Integer uid;
    private int timeYmd;
    private String uniqueId;
    private Integer targetId;
    /**
     * 用户登录此项为空
     *
     * 语音通话：
     * 1：个呼     2：组呼
     *
     * 视频通话：
     * 1：视频通话   2：视频回传  3：视频点名  4：视频会商
     */
    private Integer type;
    private String startTime;
    private String session;

}
