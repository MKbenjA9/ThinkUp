package com.example.thinkup_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;
    private double lat;
    private double lng;
    private String author;
    private long createdAt = System.currentTimeMillis();
}