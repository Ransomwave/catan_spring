
package org.example.catan_spring.repository;

import org.example.catan_spring.model.Ciutat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiutatRepository extends JpaRepository<Ciutat, Integer> {

    long countByPropietari_Id(Integer idJugador);
}
