package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidaRepository extends JpaRepository<Partida, Integer> {
    Optional<Partida> findById(Integer id);
}
