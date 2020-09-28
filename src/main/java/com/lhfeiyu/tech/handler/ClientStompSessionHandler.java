package com.lhfeiyu.tech.handler;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

/**
 * stomp-websocket client, 连接logstash websocket server
 *
 * https://www.baeldung.com/websockets-api-java-spring-client
 *
 * @author airson
 */
@Deprecated
//@Component
public class ClientStompSessionHandler implements StompSessionHandler {


    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/messages", this);
        //session.send("/app/chat", getSampleMessage());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return null;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        //Message msg = (Message) payload;
        //logger.info("Received : " + msg.getText()+ " from : " + msg.getFrom());

    }
}
