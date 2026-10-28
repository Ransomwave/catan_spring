package org.example.catan_spring.repository;

import java.util.List;
import java.util.Optional;

import org.example.catan_spring.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JugadorRepository extends JpaRepository<Jugador, Integer> {
    Optional<Jugador> findById(Integer id);

    List<Jugador> findByPartida_IdOrderByOrdreTornAsc(Integer idPartida);

    List<Jugador> findByPartida_IdOrderByCavallersJugatsDesc(Integer idPartida);

    @Query("""
        SELECT COALESCE(SUM(r.quantitat), 0)
        FROM RecursJugador r
        WHERE r.jugador.id = :idJugador
        """)
    long comptarRecursosTotals(@Param("idJugador") Integer idJugador);
}
