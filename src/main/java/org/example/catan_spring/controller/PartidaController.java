package org.example.catan_spring.controller;

import java.util.List;

import org.example.catan_spring.dto.DiceRollResponse;
import org.example.catan_spring.dto.GameResponse;
import org.example.catan_spring.dto.HexagonResponse;
import org.example.catan_spring.dto.NewGameRequest;
import org.example.catan_spring.service.PartidaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partida")
public class PartidaController {

    private final PartidaService service;

    public PartidaController(PartidaService service) {
        this.service = service;
    }

    @GetMapping("/{idPartida}/tauler")
    public ResponseEntity<List<HexagonResponse>> board(@PathVariable int idPartida) {
        return service.mostrarTauler(idPartida);
    }

    @PostMapping
    public ResponseEntity<GameResponse> newGame(@RequestBody NewGameRequest request) {
        return service.iniciarPartida(request.jugadors(), request.puntsPerGuanyar());
    }

    @PostMapping("/{idPartida}/daus")
    public ResponseEntity<DiceRollResponse> rollDice(@PathVariable int idPartida) {
        return service.tirarDaus(idPartida);
    }
}
