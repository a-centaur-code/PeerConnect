package com.example.project.peerconnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;     // requester
    private int meetupId;   // which meetup

    private String status;   // PENDING / ACCEPTED / REJECTED
}
