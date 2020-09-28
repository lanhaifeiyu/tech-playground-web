package com.lhfeiyu.tech.config;

/**
 *
 */
//@Configuration
@Deprecated
public class WebSocketConfig {

    /**
     * javax.websocket.DeploymentException: Multiple Endpoints may not be deployed to the path
     * 注释掉这段代码后就可以了。使用@SpringBootApplication启动类进行启动时需要下面这段代码，但生成war包部署在tomcat中不需要这段
     */
    /*@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }*/

}