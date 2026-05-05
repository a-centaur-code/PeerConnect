package com.example.project.peerconnect.controller;

import com.example.project.peerconnect.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage msg) {
        msg.setType("CHAT");
        msg.setTimestamp(LocalDateTime.now());
        template.convertAndSend("/topic/meetup/" + msg.getMeetupId(), msg);
    }
}