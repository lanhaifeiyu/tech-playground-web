package com.lhfeiyu.tech.handler;

import com.lhfeiyu.tech.DTO.JwtParams;
import com.lhfeiyu.tech.tools.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;

/**
 * https://juejin.im/post/5c495e85e51d45358e42a447
 * 服务端和客户端在进行握手挥手时会被执行
 */
@Component
public class WebSocketDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private static Logger logger = LoggerFactory.getLogger(WebSocketDecoratorFactory.class);

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                logger.info("ws_open:{}", session.getId());
                Principal principal = session.getPrincipal();
                boolean valid = false;
                if (principal != null) {
                    String token = principal.getName();
                    logger.info("ws_open_token:{}", token);
                    // TODO 身份校验
                    try {
                        JwtTokenUtil.parseJWT(token, JwtParams.SECRET);
                        // 身份校验成功，缓存socket连接
                        WebSocketManager.add(principal.getName(), session);
                        valid = true;
                    } catch (Exception e) {
                        valid = false;
                    }
                }
                if (!valid) {
                    logger.info("ws_open_invalid close");
                    session.close(CloseStatus.BAD_DATA);
                    session.close();
                }
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                logger.info("ws_close:{}", session.getId());
                Principal principal = session.getPrincipal();
                if (principal != null) {
                    // 身份校验成功，移除socket连接
                    WebSocketManager.remove(principal.getName());
                }
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }

}



