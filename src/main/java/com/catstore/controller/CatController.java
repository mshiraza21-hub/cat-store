package com.catstore.controller;

import com.catstore.model.Cat;
import com.catstore.repository.CatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cats")
public class CatController {

    @Autowired
    private CatRepository catRepository;

    // GET all cats
    @GetMapping
    public List<Cat> getAllCats() {
        return catRepository.findAll();
    }

    // GET search cats
    @GetMapping("/search")
    public List<Cat> searchCats(@RequestParam String query) {
        return catRepository.findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase(query, query);
    }

    // GET filter by breed
    @GetMapping("/breed/{breed}")
    public List<Cat> filterByBreed(@PathVariable String breed) {
        return catRepository.findByBreedIgnoreCase(breed);
    }

    // GET all unique breeds
    @GetMapping("/breeds")
    public List<String> getAllBreeds() {
        return catRepository.findAll()
                .stream()
                .map(Cat::getBreed)
                .distinct()
                .sorted()
                .toList();
    }

    // POST add a cat
    @PostMapping
    public Cat addCat(@RequestBody Cat cat) {
        // If breed already has a photo, reuse it
        List<Cat> existing = catRepository.findByBreedIgnoreCase(cat.getBreed());
        if (!existing.isEmpty() && (cat.getPhotoUrl() == null || cat.getPhotoUrl().isEmpty())) {
            cat.setPhotoUrl(existing.get(0).getPhotoUrl());
        }
        return catRepository.save(cat);
    }

    // POST bulk add cats
    @PostMapping("/bulk")
    public ResponseEntity<String> addBulkCats(@RequestBody List<Cat> cats) {
        for (Cat cat : cats) {
            // Reuse existing breed photo if no photo provided
            List<Cat> existing = catRepository.findByBreedIgnoreCase(cat.getBreed());
            if (!existing.isEmpty() && (cat.getPhotoUrl() == null || cat.getPhotoUrl().isEmpty())) {
                cat.setPhotoUrl(existing.get(0).getPhotoUrl());
            }
        }
        catRepository.saveAll(cats);
        return ResponseEntity.ok("Added " + cats.size() + " cats successfully");
    }

    // DELETE a cat
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCat(@PathVariable Long id) {
        if (catRepository.existsById(id)) {
            catRepository.deleteById(id);
            return ResponseEntity.ok("Cat deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
