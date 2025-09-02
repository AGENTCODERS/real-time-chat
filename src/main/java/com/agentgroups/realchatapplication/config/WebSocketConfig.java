package com.agentgroups.realchatapplication.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public void configureMessageBroker(MessageBrokerRegistry config) {

        //enable a simple in-memory message broker to carry the messages back to the client on destinations prefixed with /topic and /queue
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //register the /ws endpoint, enabling SockJS fallback options so that alternate transports can be used if WebSocket is not available
        registry.addEndpoint("/ws").
                setAllowedOriginPatterns("*").withSockJS();
    }


}
