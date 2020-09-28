package com.lhfeiyu.tech.handler;

import com.alibaba.fastjson.JSON;
import com.zom.statistics.vo.Message;
import com.zom.statistics.vo.UserSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 * https://www.cnblogs.com/yjmyzz/p/spring-boot-2-websocket-sample.html
 * https://juejin.im/post/5c495e85e51d45358e42a447
 *
 * @author airson
 */
//@Component
@Deprecated
public class WebSocketHandler {

    //private static RedisTemplate redisTemplate;

    private static final AtomicInteger                      onlineCount = new AtomicInteger(0);
    // concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
    //private static       CopyOnWriteArraySet<Session> sessionSet  = new CopyOnWriteArraySet<>();
    private static       ConcurrentHashMap<String, Session> sessionMap  = new ConcurrentHashMap<>();

    /*@Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }*/

    private static Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    public void onOpen(Session session, String token) {

        //TODO

    }

    private void cacheUserSession(Session session, String uid_userSession_key, UserSession userSession) {

        //TODO

        logger.info("ws open uid:{}", uid_userSession_key);
    }

    private UserSession castUserSession(Object userSessionObj) {
        return JSON.toJavaObject((JSON) JSON.toJSON(userSessionObj), UserSession.class);
    }

    public void onClose(Session session) {

        //TODO

        logger.info("ws close");
    }

    public void onMessage(String message, Session session) {
        logger.debug("onMessage:{}", message);
        if (StringUtils.isEmpty(message)) {
            // invalid
        } else {

            //TODO

        }
        //sendMessage(session, message);
    }

    public void onError(Session session, Throwable error) {
        logger.error("onError:{},session:{}", error.getMessage(), session.getId());
        error.printStackTrace();
    }


    public static void close(Session session, String message) {
        CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, message);
        try {
            session.close(closeReason);
        } catch (IOException e) {
            logger.error("ws close fail:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    private static String buildMessage(String content, Long receiver, Long sender) {
        Message message = new Message();
        message.setContent(content);
        message.setTs(new Date().getTime());
        message.setReceiver(receiver);
        message.setSender(sender);
        return JSON.toJSONString(message);
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session
     * @param message
     */
    public static void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.error("sendMessage fail:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 群发消息
     *
     * @param message
     * @throws IOException
     */
    /*public static void broadCast(String message) throws IOException {
        for (Session session : sessionSet) {
            if (session.isOpen()) {
                sendMessage(session, message);
            }
        }
    }*/

    /**
     * 指定Session发送消息
     *
     * @param sessionId
     * @param message
     * @throws IOException
     */
    /*public static void sendMessage(String message, String sessionId) throws IOException {
        Session session = null;
        for (Session s : sessionSet) {
            if (s.getId().equals(sessionId)) {
                session = s;
                break;
            }
        }
        if (session != null) {
            sendMessage(session, message);
        } else {
            logger.warn("没有找到你指定ID的会话：{}", sessionId);
        }
    }*/

}
