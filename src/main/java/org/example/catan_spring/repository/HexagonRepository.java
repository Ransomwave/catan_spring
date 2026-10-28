
package org.example.catan_spring.repository;

import java.util.List;

import org.example.catan_spring.model.Hexagon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HexagonRepository extends JpaRepository<Hexagon, Integer> {

    List<Hexagon> findByPartida_IdOrderByCodiAsc(Integer idPartida);
}
