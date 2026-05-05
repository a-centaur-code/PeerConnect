package com.example.project.peerconnect.controller;

import com.example.project.peerconnect.model.User;
import com.example.project.peerconnect.repo.UserRepo;
import com.example.project.peerconnect.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    // REGISTER (SEND OTP)
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        user.setOtp(otp);
        user.setVerified(false);

        userRepo.save(user);

        emailService.sendOtp(user.getEmail(), otp);

        return "OTP sent to email";
    }

    // VERIFY OTP
    @PostMapping("/verify")
    public String verify(@RequestParam String email,
                         @RequestParam String otp) {

        User user = userRepo.findByEmail(email);

        if (user == null) return "User not found";

        if (user.getOtp().equals(otp)) {
            user.setVerified(true);
            user.setOtp(null);
            userRepo.save(user);
            return "Verified Successfully";
        }

        return "Invalid OTP";
    }

    // LOGIN (IMPORTANT FIX)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User req) {

        System.out.println("LOGIN API HIT");

        if (req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("Missing fields");
        }

        User user = userRepo.findByEmail(req.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getVerified()) {
            return ResponseEntity.badRequest().body("Email not verified");
        }

        // ✅ SAFE PASSWORD CHECK
        if (user.getPassword() == null || req.getPassword() == null ||
                !user.getPassword().equals(req.getPassword())) {
            return ResponseEntity.badRequest().body("Wrong password");
        }

        return ResponseEntity.ok(user);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(e.getMessage());
    }
    // Add this to AuthController.java
    // Add this to AuthController.java
    @GetMapping("/user")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        System.out.println("=== GET USER BY EMAIL ===");
        System.out.println("Email: " + email);

        User user = userRepo.findByEmail(email);

        if (user != null) {
            System.out.println("User found:");
            System.out.println("  ID: " + user.getId());
            System.out.println("  Name: " + user.getName());
            System.out.println("  Email: " + user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);
        }

        System.out.println("User NOT found for email: " + email);
        return ResponseEntity.notFound().build();
    }
}