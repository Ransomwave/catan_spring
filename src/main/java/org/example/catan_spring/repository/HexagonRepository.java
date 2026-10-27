package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.Hexagon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HexagonRepository extends JpaRepository<Hexagon, Integer> {
    Optional<Hexagon> findByIdAndPartidaId(Integer id, Integer partidaId);
}
