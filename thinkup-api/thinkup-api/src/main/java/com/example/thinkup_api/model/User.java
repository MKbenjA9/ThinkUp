package com.example.thinkup_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    private String email;
    private String name;
    private String password;
    private long createdAt = System.currentTimeMillis();
}