package org.example.catan_spring.dto;

import java.util.List;

import org.example.catan_spring.enums.TipusTerreny;
import org.example.catan_spring.model.Assentament;
import org.example.catan_spring.model.Ciutat;
import org.example.catan_spring.model.Hexagon;
import org.example.catan_spring.model.Partida;

public record HexagonResponse(Integer id, Integer code, TipusTerreny terrain, Integer number, boolean robber,
        List<Building> buildings) {

    public record Building(Integer vertexId, Integer vertexCode, String type, String owner) {

        public static Building from(Assentament assentament) {
            var type = assentament instanceof Ciutat ? "CIUTAT" : "POBLAT";
            return new Building(assentament.getVertex().getId(), assentament.getVertex().getCodi(), type,
                    assentament.getPropietari().getNom());
        }
    }

    public static HexagonResponse from(Hexagon hexagon, Partida partida, List<Building> buildings) {
        var robber = partida.getLladre() != null && partida.getLladre().getId().equals(hexagon.getId());
        return new HexagonResponse(hexagon.getId(), hexagon.getCodi(), hexagon.getTerreny(), hexagon.getNumero(),
                robber, buildings);
    }
}
