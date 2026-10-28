package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JugadorRepository extends JpaRepository<Jugador, Integer> {

	Optional<Jugador> findById(int id);


}
