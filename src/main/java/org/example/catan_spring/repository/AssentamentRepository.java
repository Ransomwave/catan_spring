package org.example.catan_spring.repository;

import java.util.List;
import java.util.Optional;

import org.example.catan_spring.model.Assentament;
import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Vertex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssentamentRepository extends JpaRepository<Assentament, Integer> {

	Optional<Assentament> findById(int id);

	List<Assentament> findByVertex_Id(Integer idVertex);

	long countByVertex(Vertex vertex);

	@Query("SELECT COUNT(a) FROM Assentament a "
			+ "WHERE a.propietari.id = :idVictima "
			+ "AND a.vertex.id IN "
			+ "(SELECT hv.vertex.id FROM HexagonVertex hv WHERE hv.hexagon.id = :idHexagon)")
	long countByPropietariAdjacentHexagon(@Param("idVictima") Integer idVictima, @Param("idHexagon") Integer idHexagon);

	@Query("""
			SELECT count(a) FROM Assentament a
			WHERE a.propietari = :jugador
			  AND (a.vertex = :vertexA OR a.vertex = :vertexB)
			""")
	int assentamentsPerJugadorVertexAB(@Param("jugador") Jugador jugador, @Param("vertexA") Vertex vertexA, @Param("vertexB") Vertex vertexB);

	@Query("""
			select count(a) from Assentament a, Aresta ar
			where (ar.vertexA = :vertex and a.vertex = ar.vertexB)
			   or (ar.vertexB = :vertex and a.vertex = ar.vertexA)
			""")
	int assentamentsPerVertex(@Param("vertex") Vertex vertex);

	@Query("""
			SELECT COUNT(a)
			FROM Assentament a
			WHERE a.propietari.id = :idJugador
			  AND TYPE(a) <> Ciutat
			""")
	long comptarAssentamentsQueNoSonCiutat(@Param("idJugador") Integer idJugador);

}
