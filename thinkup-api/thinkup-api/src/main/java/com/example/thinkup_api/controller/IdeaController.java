package com.example.thinkup_api.controller;

import com.example.thinkup_api.model.Idea;
import com.example.thinkup_api.repository.IdeaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideas")
@CrossOrigin
public class IdeaController {

    private final IdeaRepository repo;

    public IdeaController(IdeaRepository repo) {
        this.repo = repo;
    }

    // Obtener todas las ideas
    @GetMapping
    public List<Idea> allIdeas() {
        return repo.findAll();
    }

    // Guardar una idea
    @PostMapping
    public Idea save(@RequestBody Idea idea) {
        return repo.save(idea);
    }

    // Eliminar por id
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    // Buscar por autor
    @GetMapping("/author/{name}")
    public List<Idea> byAuthor(@PathVariable String name) {
        return repo.findByAuthor(name);
    }
}
