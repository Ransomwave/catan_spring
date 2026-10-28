package org.example.catan_spring.repository;

import java.util.Optional;

import org.example.catan_spring.model.Aresta;
import org.example.catan_spring.model.Carretera;
import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Vertex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarreteraRepository extends JpaRepository<Carretera, Integer> {

	Optional<Carretera> findById(int id);

	long countByAresta(Aresta aresta);

	long countByPropietari(Jugador propietari);

	long countByPropietari_Id(Integer idJugador);

	@Query("""
			SELECT count(c) FROM Carretera c
			WHERE c.propietari = :jugador
			  AND (c.aresta.vertexA = :vertexA OR c.aresta.vertexB = :vertexA
			    OR c.aresta.vertexA = :vertexB OR c.aresta.vertexB = :vertexB)
			""")
	int carreteresPerJugadorVertexAB(@Param("jugador") Jugador jugador, @Param("vertexA") Vertex vertexA, @Param("vertexB") Vertex vertexB);

	@Query("""
			select count(c) from Carretera c
			where c.propietari = :jugador
			  and (c.aresta.vertexA = :vertex or c.aresta.vertexB = :vertex)
			""")
	int carreteresPerJugadorVertex(@Param("jugador") Jugador jugador, @Param("vertex") Vertex vertex);
}
