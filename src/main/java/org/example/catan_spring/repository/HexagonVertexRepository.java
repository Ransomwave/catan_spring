package org.example.catan_spring.repository;

import java.util.List;

import org.example.catan_spring.model.HexagonVertex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HexagonVertexRepository
        extends JpaRepository<HexagonVertex, Integer> {

    // Settlements and cities on the vertices of each hexagon of the game: [hexagon id, assentament]
    @Query("""
        SELECT hv.hexagon.id, a
        FROM HexagonVertex hv
        JOIN Assentament a ON a.vertex = hv.vertex
        WHERE hv.hexagon.partida.id = :idPartida
        ORDER BY hv.posicio
        """)
    List<Object[]> findAssentamentsByPartidaId(@Param("idPartida") Integer idPartida);

    @Query("""
        SELECT hv.hexagon.terreny, a
        FROM HexagonVertex hv
        JOIN Assentament a ON a.vertex = hv.vertex
        WHERE hv.hexagon.partida.id = :idPartida
          AND hv.hexagon.numero = :numero
          AND hv.hexagon.id <> :idLladre
        """)
    List<Object[]> trobarProduccioPerTirada(
            @Param("idPartida") Integer idPartida,
            @Param("numero") Integer numero,
            @Param("idLladre") Integer idLladre);
}