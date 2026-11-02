package org.example.catan_spring.dto;

import java.util.List;
import java.util.Map;

import org.example.catan_spring.enums.TipusRecurs;

public record DiceRollResponse(int die1, int die2, int total, boolean robberPending, List<PlayerResources> players) {

    // discarded is only above 0 when a 7 is rolled
    public record PlayerResources(Integer playerId, String name, Map<TipusRecurs, Integer> received, int discarded) {
    }
}
