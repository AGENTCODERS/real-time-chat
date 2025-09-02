package com.agentgroups.realchatapplication.controller;

import com.agentgroups.realchatapplication.model.ChatMessage;
import com.agentgroups.realchatapplication.repository.ChatMessageRepository;
import com.agentgroups.realchatapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

//@RestController("/chat")
@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        if(userService.userExists(chatMessage.getSender())) {

            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            userService.setUserOnlineStatus(chatMessage.getSender(), true);

            System.out.println("User added Successfully: " + chatMessage.getSender()+ "with session id: "+headerAccessor.getSessionId());

            chatMessage.setTimestamp(LocalDateTime.now());
            if(chatMessage.getContent()==null || chatMessage.getContent().isEmpty()) {
                chatMessage.setContent("Joined the chat");
            }
            return chatMessageRepository.save(chatMessage);
//            chatMessage.setType(ChatMessage.MessageType.LEAVE);
//            chatMessage.setContent("Username already taken. Please choose another one.");
//            return chatMessage;
        }
        return null;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if(userService.userExists(chatMessage.getSender())) {
            if(chatMessage.getTimestamp()==null){
                chatMessage.setTimestamp(LocalDateTime.now());
            }
           if (chatMessage.getContent() == null || chatMessage.getContent().isEmpty()) {
               chatMessage.setContent("No message content");
              }
            return chatMessageRepository.save(chatMessage);
        }
        return null;
    }


    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        if(userService.userExists(chatMessage.getSender()) && userService.userExists(chatMessage.getRecipient())) {
            if(chatMessage.getTimestamp()==null){
                chatMessage.setTimestamp(LocalDateTime.now());
            }
            if (chatMessage.getContent() == null || chatMessage.getContent().isEmpty()) {
                chatMessage.setContent("No message content");
            }
            chatMessage.setType(ChatMessage.MessageType.PRIVATE_MESSAGE);
            ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
            System.out.println("Private message from " + chatMessage.getSender() + " to " + chatMessage.getRecipient() + ": " + chatMessage.getContent() + " with id: "+savedChatMessage.getId());

            try {

            String receipientDestination = "/user/" + chatMessage.getRecipient() + "/queue/private";
            System.out.println("Sending to receipient destination: " + receipientDestination);
            messagingTemplate.convertAndSend(receipientDestination, savedChatMessage);

            String senderDestination = "/user/" + chatMessage.getSender() + "/queue/private";
            System.out.println("Sending to sender destination: " + senderDestination);
            messagingTemplate.convertAndSend(senderDestination, savedChatMessage);

            } catch (Exception e) {
                System.out.println("Error Occured while sending private message: " + e.getMessage());
                e.printStackTrace();
            }
            chatMessageRepository.save(chatMessage);
        }
        else
        {
            System.out.println("Error: Sender "+ chatMessage.getSender() + " or Receipient " + chatMessage.getRecipient() + " does not exist.");
        }
    }
}
