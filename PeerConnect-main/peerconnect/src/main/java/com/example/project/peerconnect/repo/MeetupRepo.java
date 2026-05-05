package com.example.project.peerconnect.repo;

import com.example.project.peerconnect.model.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetupRepo extends JpaRepository<Meetup,Integer> {};
