
package org.example.catan_spring.repository;

import org.example.catan_spring.enums.TipusCarta;
import org.example.catan_spring.model.CartaDesenvolupament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartaDesenvolupamentRepository
        extends JpaRepository<CartaDesenvolupament, Integer> {

    @Query("""
        SELECT COUNT(c)
        FROM CartaDesenvolupament c
        WHERE c.propietari.id = :idJugador
          AND c.tipus = :tipus
        """)
    long comptarCartesPerTipus(
            @Param("idJugador") Integer idJugador,
            @Param("tipus") TipusCarta tipus);
}
