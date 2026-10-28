package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.Aresta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArestaRepository extends JpaRepository<Aresta, Integer> {

	Optional<Aresta> findById(int id);


}
