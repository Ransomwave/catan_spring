package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.Ciutat;
import org.example.catan_spring.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiutatRepository extends JpaRepository<Ciutat, Integer>  {
	
	Optional<Ciutat> findById(int id);
	
	long countByPropietari(Jugador jugador);

	long countByPropietari_Id(Integer idJugador);

}
