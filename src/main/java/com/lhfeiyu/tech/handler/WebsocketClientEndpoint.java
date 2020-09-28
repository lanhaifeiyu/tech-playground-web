package com.lhfeiyu.tech.handler;

/**
 * https://cloud.tencent.com/developer/ask/66587
 *
 * @author airson
 */

/**
 * 使用http，暂不使用websocket
 */
@Deprecated
//@ClientEndpoint
public class WebsocketClientEndpoint {

    /*Session userSession = null;
    private MessageHandler messageHandler;

    private static Logger logger = LoggerFactory.getLogger(WebsocketClientEndpoint.class);

    public WebsocketClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            logger.error("ws_cli connect fail:{}", e.getMessage());
            e.printStackTrace();
        }
    }*/

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
   /* @OnOpen
    public void onOpen(Session userSession) {
        logger.debug("opening websocket");
        WebsocketClientConnector.setAlive(true);
        this.userSession = userSession;
    }*/

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason      the reason for connection close
     */
    /*@OnClose
    public void onClose(Session userSession, CloseReason reason) {
        logger.debug("closing websocket");
        WebsocketClientConnector.setAlive(false);
        this.userSession = null;
    }*/

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    /*@OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }*/

    /**
     * register message handler
     *
     * @param msgHandler
     */
    /*public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }*/

    /**
     * Send a message.
     *
     * @param message
     */
    /*public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }*/

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    /*public interface MessageHandler {
        void handleMessage(String message);
    }*/
}