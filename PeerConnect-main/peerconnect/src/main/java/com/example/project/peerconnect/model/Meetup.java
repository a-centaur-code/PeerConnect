package com.example.project.peerconnect.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String activity;

    private Double lat;
    private Double lng;

    private String genderPreference;

    private Integer createdBy;

    @ElementCollection
    private List<Integer> attendees = new ArrayList<>(); // ✅ Store user IDs who joined

    @Transient
    private String creatorName;

    @Transient
    private String creatorEmail;
}