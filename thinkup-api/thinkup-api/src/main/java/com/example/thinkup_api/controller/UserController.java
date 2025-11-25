package com.example.thinkup_api.controller;

import com.example.thinkup_api.model.User;
import com.example.thinkup_api.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/register")
    public boolean register(@RequestBody User user) {
        if (repo.existsById(user.getEmail())) return false;
        repo.save(user);
        return true;
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return repo.findByEmailAndPassword(user.getEmail(), user.getPassword());
    }
}
