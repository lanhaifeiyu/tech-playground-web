package com.lhfeiyu.tech.config;

/**
 * TODO
 *
 * @author airson
 */
public class Const {

    public Const() {
        // forbid new instance
    }

    //MQ
    public static final String MQ_INSTANCE  = "kafka";
    public static final String MQ_GROUP_STA = "g-sta";
    public static final String MQ_TOPIC_STA = "t-sta";

    // WEBSOCKET
    public static final String WS_STA_MSG_TOPIC        = "/topic/greetings";
    public static final String WS_STA_MSG_TOPIC_PREFIX = "/topic";
    public static final String WS_STA_MSG_ENDPOINT     = "/chat";

    // SESSION
    public static final String WS_SESSION_COUNT_KEY = "ws_session_count";

    // TOKEN
    public static final String SESSION_PRE_KEY     = "s_";
    public static final String SESSION_UID_PRE_KEY = "su_";
    public static final String TOKEN_PRE_KEY       = "t_";

    public static String build_uid_userSession_key(Long uid) {
        return SESSION_PRE_KEY + uid;
    }

    public static String build_sid_uid_key(String sessionId) {
        return SESSION_UID_PRE_KEY + sessionId;
    }

    public static String build_token_uid_key(String token) {
        return TOKEN_PRE_KEY + token;
    }

}
