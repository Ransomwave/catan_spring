package org.example.catan_spring.repository;

import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarreteraRepository extends JpaRepository<Partida, Integer> {
    long countByPropietari(Jugador propietari);
}
