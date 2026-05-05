package com.example.project.peerconnect.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String type; // CHAT, JOIN, LEAVE
    private int meetupId;
    private String sender;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
}