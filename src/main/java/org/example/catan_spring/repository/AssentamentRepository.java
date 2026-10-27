package org.example.catan_spring.repository;

import org.example.catan_spring.model.Assentament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssentamentRepository extends JpaRepository<Assentament, Integer> {
    @Query("SELECT COUNT(a) FROM Assentament a "
            + "WHERE a.propietari.id = :idVictima "
            + "AND a.vertex.id IN "
            + "(SELECT hv.vertex.id FROM HexagonVertex hv WHERE hv.hexagon.id = :idHexagon)")
    long countByPropietariAdjacentHexagon(@Param("idVictima") Integer idVictima, @Param("idHexagon") Integer idHexagon);
}
