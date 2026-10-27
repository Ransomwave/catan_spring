package org.example.catan_spring.repository;

import java.util.List;

import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Partida;
import org.example.catan_spring.model.Vertex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VertexRepository extends JpaRepository<Vertex, Integer> {
    // free vertexs with no adjacent settlement; outside preparation they must also
    // touch one of the player's roads
    @Query("""
            select v from Vertex v
            where v.partida = :partida
              and not exists (select a from Assentament a where a.vertex = v)
              and not exists (
                  select a from Assentament a, Aresta ar
                  where (ar.vertexA = v and a.vertex = ar.vertexB)
                     or (ar.vertexB = v and a.vertex = ar.vertexA))
              and (:preparacio = true or exists (
                  select c from Carretera c
                  where c.propietari = :jugador
                    and (c.aresta.vertexA = v or c.aresta.vertexB = v)))
            order by v.codi
            """)
    List<Vertex> findDisponibles(@Param("partida") Partida partida, @Param("jugador") Jugador jugador,
            @Param("preparacio") boolean preparacio);
}
