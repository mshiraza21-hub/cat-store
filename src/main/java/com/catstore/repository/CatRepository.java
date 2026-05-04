package com.catstore.repository;

import com.catstore.model.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long> {
    List<Cat> findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase(String name, String breed);
    List<Cat> findByBreedIgnoreCase(String breed);
}
