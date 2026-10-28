package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Poblat;
import org.example.catan_spring.model.Vertex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface PoblatRepository extends JpaRepository<Poblat, Integer>  {
	
	Optional<Poblat> findById(int id);
	
	long countByPropietari(Jugador jugador);
	
	@Query("""
			FROM Poblat p WHERE p.vertex.id = :idVertex
			""")
	Poblat poblatPerVertex(@Param("vertex") Vertex vertex);
	
	@Query("""
			SELECT count(p) FROM Poblat p
			WHERE p.propietari = :jugador
			  AND (p.vertex = :vertexA or p.vertex = :vertexB)
			  AND not exists (
			      SELECT c FROM Carretera c
			      WHERE c.propietari = :jugador
			        AND (c.aresta.vertexA = p.vertex or c.aresta.vertexB = p.vertex))
			""")
	int poblatsPerJugadorVertexAB(@Param("jugador") Jugador jugador, @Param("vertexA") Vertex vertexA, @Param("vertexB") Vertex vertexB);
	
	
}
