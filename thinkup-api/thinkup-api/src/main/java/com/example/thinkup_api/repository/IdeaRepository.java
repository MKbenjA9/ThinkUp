package com.example.thinkup_api.repository;


import com.example.thinkup_api.model.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdeaRepository extends JpaRepository<Idea, Long> {
    List<Idea> findByAuthor(String author);
    List<Idea> findByCategory(String category);
}
