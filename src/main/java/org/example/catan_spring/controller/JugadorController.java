package org.example.catan_spring.controller;

import org.example.catan_spring.dto.PlayerResponse;
import org.example.catan_spring.service.JugadorService;
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
        try {
            return ResponseEntity.ok(service.mostrarRecursos(idJugador));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // posicio is the database id of the aresta (CARRETERA) or the vertex (POBLAT,
    // CIUTAT)
    @PostMapping("/{idJugador}/construir")
    public String build(@PathVariable int idJugador, @RequestParam String tipus, @RequestParam int posicio) {
        try {
            switch (tipus) {
                case "CARRETERA" -> service.construirCarretera(idJugador, posicio);
                case "POBLAT" -> service.construirPoblat(idJugador, posicio);
                case "CIUTAT" -> service.millorarACiutat(idJugador, posicio);
                default -> {
                    return "Tipus desconegut: ha de ser CARRETERA, POBLAT o CIUTAT";
                }
            }
            return "Construit";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/{idJugador}/passar")
    public String endTurn(@PathVariable int idJugador) {
        try {
            return "Torn passat: ara és el torn de " + service.passarTorn(idJugador);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
}
