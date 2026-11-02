package org.example.catan_spring.dto;

import java.util.List;

import org.example.catan_spring.enums.EstatPartida;

public record GameResponse(Integer id, EstatPartida state, Integer round, Integer pointsToWin,
        List<PlayerResponse> players, List<HexagonResponse> hexagons) {
}
