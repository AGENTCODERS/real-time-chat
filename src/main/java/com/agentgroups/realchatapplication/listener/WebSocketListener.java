package com.agentgroups.realchatapplication.listener;

import com.agentgroups.realchatapplication.model.ChatMessage;
import com.agentgroups.realchatapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.logging.Logger;

@Component
public class WebSocketListener {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private static final Logger logger = Logger.getLogger(WebSocketListener.class.getName());

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        logger.config("connected to websocket");
        //System.out.println("WebSocket connected");
    }

    public void handleWebSocketDisconnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getSessionAttributes().get("username").toString();
        // Handle disconnection event if needed

        System.out.println("User Disconnected : " + username);

        userService.setUserOnlineStatus(username, false);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setSender(username);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);




    }


}
