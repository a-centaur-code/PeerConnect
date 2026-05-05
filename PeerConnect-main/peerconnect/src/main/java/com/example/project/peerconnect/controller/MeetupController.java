package com.example.project.peerconnect.controller;

import com.example.project.peerconnect.model.ChatMessage;
import com.example.project.peerconnect.model.Meetup;
import com.example.project.peerconnect.model.User;
import com.example.project.peerconnect.repo.MeetupRepo;
import com.example.project.peerconnect.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/meetups")
@CrossOrigin(origins = "http://localhost:3000")
public class MeetupController {

    @Autowired
    MeetupRepo repo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate template;

    // CREATE MEETUP
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Meetup meetup) {
        // Initialize attendees with creator
        if (meetup.getAttendees() == null) {
            meetup.setAttendees(new ArrayList<>());
        }
        meetup.getAttendees().add(meetup.getCreatedBy()); // Creator is first attendee

        Meetup savedMeetup = repo.save(meetup);

        User creator = userRepo.findById(savedMeetup.getCreatedBy()).orElse(null);
        if (creator != null) {
            savedMeetup.setCreatorName(creator.getName());
            savedMeetup.setCreatorEmail(creator.getEmail());
        }

        return ResponseEntity.ok(savedMeetup);
    }

    // GET ALL MEETUPS
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllMeetups() {
        List<Meetup> meetups = repo.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Meetup meetup : meetups) {
            Map<String, Object> meetupData = new HashMap<>();
            meetupData.put("id", meetup.getId());
            meetupData.put("activity", meetup.getActivity());
            meetupData.put("lat", meetup.getLat());
            meetupData.put("lng", meetup.getLng());
            meetupData.put("genderPreference", meetup.getGenderPreference());
            meetupData.put("createdBy", meetup.getCreatedBy());
            meetupData.put("attendees", meetup.getAttendees());
            meetupData.put("attendeeCount", meetup.getAttendees() != null ? meetup.getAttendees().size() : 0);
            meetupData.put("isFull", (meetup.getAttendees() != null && meetup.getAttendees().size() >= 2));

            // Get creator name
            if (meetup.getCreatedBy() != null) {
                User creator = userRepo.findById(meetup.getCreatedBy()).orElse(null);
                if (creator != null) {
                    meetupData.put("creatorName", creator.getName());
                    meetupData.put("creatorEmail", creator.getEmail());
                } else {
                    meetupData.put("creatorName", "Unknown User");
                }
            } else {
                meetupData.put("creatorName", "Unknown User");
            }

            response.add(meetupData);
        }

        return ResponseEntity.ok(response);
    }

    // JOIN MEETUP - Only allows if less than 2 attendees
    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinMeetup(@PathVariable Integer id, @RequestBody Integer userId) {
        Optional<Meetup> meetupOpt = repo.findById(id);
        Optional<User> userOpt = userRepo.findById(userId);

        if (meetupOpt.isPresent() && userOpt.isPresent()) {
            Meetup meetup = meetupOpt.get();
            User user = userOpt.get();

            // Check if meetup is full (max 2 people: creator + 1 joiner)
            if (meetup.getAttendees() != null && meetup.getAttendees().size() >= 2) {
                return ResponseEntity.badRequest().body(Map.of("error", "Meetup is full! Maximum 2 people per meetup."));
            }

            // Check if user already joined
            if (meetup.getAttendees() != null && meetup.getAttendees().contains(userId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "You already joined this meetup"));
            }

            // Check if user is the creator
            if (meetup.getCreatedBy().equals(userId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "You cannot join your own meetup"));
            }

            // Add user to attendees
            if (meetup.getAttendees() == null) {
                meetup.setAttendees(new ArrayList<>());
            }
            meetup.getAttendees().add(userId);
            repo.save(meetup);

            // Send notification
            ChatMessage notification = new ChatMessage();
            notification.setType("JOIN");
            notification.setMeetupId(id);
            notification.setSender(user.getEmail());
            notification.setSenderName(user.getName());
            notification.setContent(user.getName() + " joined the meetup! 🎉");
            notification.setTimestamp(LocalDateTime.now());

            template.convertAndSend("/topic/meetup/" + id, notification);

            return ResponseEntity.ok(Map.of("message", "Joined successfully"));
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Failed to join"));
    }

    // DELETE MEETUP
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeetup(@PathVariable Integer id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Meetup deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // TERMINATE MEETUP
    @DeleteMapping("/{id}/terminate")
    public ResponseEntity<?> terminateMeetup(@PathVariable Integer id, @RequestBody Integer userId) {
        Optional<Meetup> meetupOpt = repo.findById(id);

        if (meetupOpt.isPresent()) {
            Meetup meetup = meetupOpt.get();
            if (meetup.getCreatedBy().equals(userId)) {
                ChatMessage terminationMsg = new ChatMessage();
                terminationMsg.setType("TERMINATE");
                terminationMsg.setMeetupId(id);
                terminationMsg.setContent("⚠️ Meetup has been terminated by the creator! ⚠️");
                terminationMsg.setTimestamp(LocalDateTime.now());

                template.convertAndSend("/topic/meetup/" + id, terminationMsg);
                repo.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Meetup terminated"));
            }
            return ResponseEntity.status(403).body(Map.of("error", "Only creator can terminate"));
        }
        return ResponseEntity.notFound().build();
    }
}