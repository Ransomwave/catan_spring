package org.example.catan_spring.dto;

import java.util.EnumMap;
import java.util.Map;

import org.example.catan_spring.enums.ColorJugador;
import org.example.catan_spring.enums.TipusRecurs;
import org.example.catan_spring.model.Jugador;

public record PlayerResponse(Integer id, String name, ColorJugador color, Integer turnOrder,
        Map<TipusRecurs, Integer> resources) {

    // Must be called inside a @Transactional method: the inventory is LAZY
    public static PlayerResponse from(Jugador jugador) {
        var resources = new EnumMap<TipusRecurs, Integer>(TipusRecurs.class);
        for (var recurs : jugador.getInventari()) {
            resources.put(recurs.getTipus(), recurs.getQuantitat());
        }
        return new PlayerResponse(jugador.getId(), jugador.getNom(), jugador.getColor(), jugador.getOrdreTorn(),
                resources);
    }
}
