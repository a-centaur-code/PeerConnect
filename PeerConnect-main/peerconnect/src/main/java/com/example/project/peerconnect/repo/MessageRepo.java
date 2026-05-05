package com.example.project.peerconnect.repo;

import com.example.project.peerconnect.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Integer> {

    List<Message> findByMeetupId(int meetupId);

}