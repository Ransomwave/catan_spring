package org.example.catan_spring.repository;

import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Poblat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoblatRepository extends JpaRepository<Poblat, Integer> {
    long countByPropietari(Jugador propietari);
}
