package org.example.catan_spring.dto;

import java.util.List;

// { "jugadors": ["Anna", "Pau", "Marta"], "puntsPerGuanyar": 10 }, puntsPerGuanyar is optional
public record NewGameRequest(List<String> jugadors, Integer puntsPerGuanyar) {
}
