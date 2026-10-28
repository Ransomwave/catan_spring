
package org.example.catan_spring.repository;

import java.util.List;

import org.example.catan_spring.model.Assentament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssentamentRepository
        extends JpaRepository<Assentament, Integer> {

    List<Assentament> findByVertex_Id(Integer idVertex);

    @Query("""
        SELECT COUNT(a)
        FROM Assentament a
        WHERE a.propietari.id = :idJugador
          AND TYPE(a) <> Ciutat
        """)
    long comptarAssentamentsQueNoSonCiutat(
            @Param("idJugador") Integer idJugador);
}
