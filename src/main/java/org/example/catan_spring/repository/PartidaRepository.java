
package org.example.catan_spring.repository;

import org.example.catan_spring.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidaRepository extends JpaRepository<Partida, Integer> {
}
