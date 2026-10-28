package org.example.catan_spring.repository;

import java.util.List;
import java.util.Optional;

import org.example.catan_spring.model.Hexagon;
import org.example.catan_spring.model.Vertex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HexagonRepository extends JpaRepository<Hexagon, Integer> {

	Optional<Hexagon> findById(int id);

	Optional<Hexagon> findByIdAndPartidaId(Integer id, Integer partidaId);

	List<Hexagon> findByPartida_IdOrderByCodiAsc(Integer idPartida);

	@Query("""
			select hv.hexagon
			from HexagonVertex hv
			where hv.vertex = :vertex
			""")
	List<Hexagon> hexagonsPerVertex(@Param("vertex") Vertex vertex);

}
