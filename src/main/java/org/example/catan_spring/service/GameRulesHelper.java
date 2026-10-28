package org.example.catan_spring.service;

import org.example.catan_spring.model.Jugador;
import org.springframework.stereotype.Service;
import org.example.catan_spring.enums.TipusRecurs;

@Service
public class GameRulesHelper {

    public static int totalResources(Jugador jugador) {
        int total = 0;

        for (var recurs : jugador.getInventari()) {
            total += recurs.getQuantitat();
        }

        return total;
    }

    public static void addResource(
            Jugador jugador, TipusRecurs tipus, int quantitat) {

        for (var recurs : jugador.getInventari()) {
            if (recurs.getTipus() == tipus) {
                recurs.setQuantitat(
                        recurs.getQuantitat() + quantitat);
                return;
            }
        }
    }
}
