package com.example.project.peerconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String to, String otp) {
        // Print to console for testing (so you can see OTP without email config)
        System.out.println("========================================");
        System.out.println("EMAIL: " + to);
        System.out.println("OTP CODE: " + otp);
        System.out.println("========================================");

        // Try to send email (won't work without proper config, but console will show OTP)
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("PeerConnect OTP Verification");
            message.setText("Your OTP is: " + otp);
            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.out.println("Email failed to send, but OTP is printed above for testing");
        }
    }
}