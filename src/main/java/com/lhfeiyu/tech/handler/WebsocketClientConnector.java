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
//@Component
public class WebsocketClientConnector {

    /*@Value("${custom.ws_client_url}")
    private String                   ws_client_url;
    @Autowired
    private WebSocketStompController webSocketStompController;

    private static boolean alive = false;

    private static WebsocketClientEndpoint clientEndpoint = null;

    private static Logger logger = LoggerFactory.getLogger(WebsocketClientConnector.class);

    public static boolean isAlive() {
        return alive;
    }

    public static void setAlive(boolean alive) {
        WebsocketClientConnector.alive = alive;
    }

    public void checkAlive() {
        if (isAlive()) {
            return;
        }
        // not alive
        doConnect();
    }*/

    /**
     * sta项目要做为client去连接logstash的websocket server，接收消息，然后通过自己的websocket发送给client
     * https://www.elastic.co/guide/en/logstash/current/plugins-outputs-websocket.html
     * <p>
     * You can connect to it with ws://<host\>:<port\>/
     * If no clients are connected, any messages received are ignored.
     * <p>
     * https://www.baeldung.com/websockets-api-java-spring-client
     */

    /*public void doConnect() {
//        WebSocketClient client = new StandardWebSocketClient();
//        WebSocketStompClient stompClient = new WebSocketStompClient(client);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        StompSessionHandler sessionHandler = new ClientStompSessionHandler();
//        String url = "ws://localhost:6001";
//        stompClient.connect(url, sessionHandler);

        // way 2
        try {
            logger.debug("ws_client_url:{}", ws_client_url);
            // open websocket
            WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI(ws_client_url));

            // add listener
            clientEndPoint.addMessageHandler(message -> {
                logger.debug("ws_cli_msg:{}", message);
                // 接收消息，发送消息
                WebSocketResponse response = new WebSocketResponse();
                response.setContent(message);
                try {
                    webSocketStompController.sta_notice(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // send message to websocket
            //clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");

            // wait 5 seconds for messages from websocket
            //Thread.sleep(5000);

       // } catch (InterruptedException ex) {
       //     logger.error("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            logger.error("URISyntaxException exception: " + ex.getMessage());
        }
    }
*/

}