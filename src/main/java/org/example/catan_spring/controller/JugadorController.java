package org.example.catan_spring.controller;

import org.example.catan_spring.dto.PlayerResponse;
import org.example.catan_spring.service.JugadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jugador")
public class JugadorController {

    private final JugadorService service;

    public JugadorController(JugadorService service) {
        this.service = service;
    }

    @GetMapping("/{idJugador}/recursos")
    public ResponseEntity<PlayerResponse> resources(@PathVariable int idJugador) {
        return service.mostrarRecursos(idJugador);
    }

    // posicio is the database id of the aresta (CARRETERA) or the vertex (POBLAT,
    // CIUTAT)
    @PostMapping("/{idJugador}/construir")
    public ResponseEntity<String> build(@PathVariable int idJugador, @RequestParam String tipus,
            @RequestParam int posicio) {
        return switch (tipus) {
            case "CARRETERA" -> service.construirCarretera(idJugador, posicio);
            case "POBLAT" -> service.construirPoblat(idJugador, posicio);
            case "CIUTAT" -> service.millorarACiutat(idJugador, posicio);
            default -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Jugador-info", "Tipus desconegut: ha de ser CARRETERA, POBLAT o CIUTAT")
                    .build();
        };
    }

    @PostMapping("/{idJugador}/passar")
    public ResponseEntity<String> endTurn(@PathVariable int idJugador) {
        return service.passarTorn(idJugador);
    }
}
