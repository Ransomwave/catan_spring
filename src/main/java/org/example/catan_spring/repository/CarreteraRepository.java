package org.example.catan_spring.repository;

import org.example.catan_spring.model.Carretera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarreteraRepository extends JpaRepository<Carretera, Integer> {

    long countByPropietari_Id(Integer idJugador);
}