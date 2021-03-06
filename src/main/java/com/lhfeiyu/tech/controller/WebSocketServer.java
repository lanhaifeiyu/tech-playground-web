package com.lhfeiyu.tech.controller;

import com.lhfeiyu.tech.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;

/**
 * TODO
 * https://www.jianshu.com/p/2c9be4641d43
 * https://www.jianshu.com/p/840fded397be
 * <p>
 * XXX spring springboot websocket 不能注入( @Autowired ) service bean 报 null 错误
 * XXX https://blog.csdn.net/m0_37202351/article/details/86255132
 * XXX 必须要用setRedisTemplate方法，autowired和constructor都会为null或报错
 *
 * @author airson
 */
//@ServerEndpoint(value = "/ws/v1/endpoint")
//@Component
@Deprecated
public class WebSocketServer {

    private static WebSocketHandler handler;

    //@Autowired
    public void setWebSocketHandler(WebSocketHandler handler) {
        this.handler = handler;
    }

    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    @PostConstruct
    public void init() {
        logger.debug("websocket init");
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        handler.onOpen(session, token);
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        handler.onClose(session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.debug("message:{}", message);
        handler.onMessage(message, session);
    }

    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        handler.onError(session, error);
    }

}
