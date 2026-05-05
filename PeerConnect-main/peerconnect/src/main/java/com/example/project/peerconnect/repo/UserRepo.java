package com.example.project.peerconnect.repo;

import com.example.project.peerconnect.model.Meetup;
import com.example.project.peerconnect.model.Message;
import com.example.project.peerconnect.model.Request;
import com.example.project.peerconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}



