package com.agentgroups.realchatapplication.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String content;

    private String sender;

    private String recipient;

    private String color;

    @Column

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        PRIVATE_MESSAGE,
        TYPING
    }


}
