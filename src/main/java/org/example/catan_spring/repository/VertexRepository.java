package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Vertex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VertexRepository extends JpaRepository<Vertex, Integer> {

	Optional<Vertex> findById(int id);
	
}
