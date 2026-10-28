package org.example.catan_spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.example.catan_spring.GameRulesHelper;
import org.example.catan_spring.enums.EstatPartida;
import org.example.catan_spring.enums.TipusCarta;
import org.example.catan_spring.enums.TipusRecurs;
import org.example.catan_spring.model.Aresta;
import org.example.catan_spring.model.Assentament;
import org.example.catan_spring.model.Carretera;
import org.example.catan_spring.model.CartaDesenvolupament;
import org.example.catan_spring.model.Ciutat;
import org.example.catan_spring.model.Hexagon;
import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Partida;
import org.example.catan_spring.model.Poblat;
import org.example.catan_spring.model.RecursJugador;
import org.example.catan_spring.model.Vertex;
import org.example.catan_spring.repository.ArestaRepository;
import org.example.catan_spring.repository.AssentamentRepository;
import org.example.catan_spring.repository.CarreteraRepository;
import org.example.catan_spring.repository.CartaDesenvolupamentRepository;
import org.example.catan_spring.repository.CiutatRepository;
import org.example.catan_spring.repository.HexagonRepository;
import org.example.catan_spring.repository.JugadorRepository;
import org.example.catan_spring.repository.PartidaRepository;
import org.example.catan_spring.repository.PoblatRepository;
import org.example.catan_spring.repository.VertexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JugadorService {

    private final Random random = new Random();

    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private CarreteraRepository carreteraRepository;
    @Autowired
    private PoblatRepository poblatRepository;
    @Autowired
    private HexagonRepository hexagonRepository;
    @Autowired
    private AssentamentRepository assentamentRepository;
    @Autowired
    private CartaDesenvolupamentRepository cartaDesenvolupamentRepository;
    @Autowired
    private VertexRepository vertexRepository;
    @Autowired
    private ArestaRepository arestaRepository;
    @Autowired
    private CiutatRepository ciutatRepository;

    @Transactional
    public void passarTorn(int idJugador) {

        var jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new IllegalArgumentException("No existeix el jugador " + idJugador));

        var partida = jugador.getPartida();
        if (partida.getActiu() == null || !partida.getActiu().getId().equals(idJugador)) {
            throw new IllegalStateException("No és el torn d'aquest jugador.");
        }
        if (partida.isLladrePendent()) {
            throw new IllegalStateException(
                    "No es pot passar torn: cal moure el lladre.");
        }
        if (partida.getEstat() == EstatPartida.EN_CURS) {
            if (!partida.isDausTirats()) {
                throw new IllegalStateException(
                        "Has de tirar els daus abans de passar torn.");
            }
            passarTornEnCurs(partida);
        } else if (partida.getEstat() == EstatPartida.PREPARACIO) {
            comprovarPecesPreparacio(jugador, partida);
            passarTornPreparacio(partida);
        } else {
            throw new IllegalStateException(
                    "No es pot passar torn en aquest estat de partida.");
        }
        partida.setDausTirats(false);
    }

    private void passarTornEnCurs(Partida partida) {
        var jugadors = partida.getJugadors();
        int ordreActual = partida.getActiu().getOrdreTorn();
        int ordreSeguent = ordreActual + 1;
        if (ordreSeguent > jugadors.size()) {
            ordreSeguent = 1;
            partida.setRonda(partida.getRonda() + 1);
        }
        for (var jugador : jugadors) {
            if (jugador.getOrdreTorn() == ordreSeguent) {
                partida.setActiu(jugador);
                return;
            }
        }

        throw new IllegalStateException(
                "No s'ha trobat el jugador següent.");
    }

    private void passarTornPreparacio(Partida partida) {
        var jugadors = partida.getJugadors();
        int ordreActual = partida.getActiu().getOrdreTorn();
        int maxJugadors = jugadors.size();
        int ronda = partida.getRonda();
        int ordreSeguent;
        if (ronda == 1) {
            ordreSeguent = ordreActual + 1;
            if (ordreSeguent > maxJugadors) {
                partida.setRonda(2);
                ordreSeguent = maxJugadors;
            }
        } else {
            ordreSeguent = ordreActual - 1;
            if (ordreSeguent < 1) {
                for (var jugador : jugadors) {
                    var poblats = poblatRepository.countByPropietari(jugador);
                    var carreteres = carreteraRepository.countByPropietari(jugador);

                    if (poblats < 2 || carreteres < 2) {
                        throw new IllegalStateException(
                                "El jugador " + jugador.getNom() + " no té dos poblats i dues carreteres.");
                    }
                }
                partida.setEstat(EstatPartida.EN_CURS);
                partida.setRonda(1);
                for (var jugador : jugadors) {
                    if (jugador.getOrdreTorn() == 1) {
                        partida.setActiu(jugador);
                        return;
                    }
                }

                throw new IllegalStateException(
                        "No s'ha trobat el jugador d'ordre 1.");
            }
        }
        for (var jugador : jugadors) {
            if (jugador.getOrdreTorn() == ordreSeguent) {
                partida.setActiu(jugador);
                return;
            }
        }

        throw new IllegalStateException(
                "No s'ha trobat el jugador següent.");
    }

    private void comprovarPecesPreparacio(Jugador jugador, Partida partida) {
        int ronda = partida.getRonda();
        var poblats = poblatRepository.countByPropietari(jugador);
        var carreteres = carreteraRepository.countByPropietari(jugador);
        if (poblats < ronda || carreteres < ronda) {
            if (ronda == 1) {
                throw new IllegalStateException(
                        "Has de tenir " + ronda + " poblat i " + ronda + " carretera abans de passar torn.");
            } else {
                throw new IllegalStateException(
                        "Has de tenir " + ronda + " poblats i " + ronda + " carreteres abans de passar torn.");
            }
        }
    }

    @Transactional
    public void moureLladre(int idJugador, int idHexagon, Integer idJugadorVictima) {
        var jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new IllegalArgumentException("No existeix el jugador " + idJugador));

        var partida = jugador.getPartida();

        if (partida.getActiu() == null
                || !partida.getActiu().getId().equals(idJugador)) {
            throw new IllegalStateException("No és el torn d'aquest jugador.");
        }

        if (!partida.isLladrePendent()) {
            throw new IllegalStateException("No hi ha cap moviment del lladre pendent.");
        }

        var newHexagon = hexagonRepository.findByIdAndPartidaId(idHexagon, partida.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "L'hexàgon no existeix en aquesta partida."));

        var currentHexagon = partida.getLladre();
        if (currentHexagon != null
                && currentHexagon.getId().equals(newHexagon.getId())) {
            throw new IllegalArgumentException(
                    "El lladre s'ha de moure a un hexàgon diferent.");
        }

        partida.setLladre(newHexagon);

        if (idJugadorVictima != null) {
            var victima = jugadorRepository.findById(idJugadorVictima)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "El jugador víctima no existeix."));

            if (victima.getId().equals(jugador.getId())) {
                throw new IllegalArgumentException(
                        "No et pots robar recursos a tu mateix.");
            }

            if (!victima.getPartida().getId().equals(partida.getId())) {
                throw new IllegalArgumentException(
                        "La víctima no pertany a aquesta partida.");
            }

            var assentaments = assentamentRepository.countByPropietariAdjacentHexagon(idJugadorVictima, idHexagon);

            if (assentaments == 0) {
                throw new IllegalArgumentException(
                        "La víctima no té cap assentament en aquest hexàgon.");
            }

            var recursosDisponibles = new ArrayList<RecursJugador>();

            for (var recurs : victima.getInventari()) {
                if (recurs.getQuantitat() > 0) {
                    recursosDisponibles.add(recurs);
                }
            }
            if (!recursosDisponibles.isEmpty()) {
                int index = random.nextInt(
                        recursosDisponibles.size());

                var tipus = recursosDisponibles.get(index).getTipus();
                GameRulesHelper.addResource(victima, tipus, -1);
                GameRulesHelper.addResource(jugador, tipus, 1);
            }
        }
        partida.setLladrePendent(false);
    }

    @Transactional
    public void comprarCarta(int idJugador) {

        var jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new IllegalArgumentException("No existeix el jugador " + idJugador));

        GameRulesHelper.checkAction(jugador, false);
        var partida = jugador.getPartida();
        var pila = new ArrayList<CartaDesenvolupament>();
        for (var card : partida.getPila()) {
            if (card.getPropietari() == null) {
                pila.add(card);
            }
        }
        if (pila.isEmpty()) {
            throw new IllegalStateException(
                    "La pila de cartes de desenvolupament és buida.");
        }
        GameRulesHelper.pay(jugador, GameRulesHelper.COST_CARTA);
        int index = random.nextInt(pila.size());
        var carta = pila.get(index);
        carta.setPropietari(jugador);
        carta.setRondaCompra(partida.getRonda());
        // PartidaDao.comprovarVictoria(session, partida);
        // TODO: wait for PartidaService

    }

    @Transactional
    public void jugarCarta(int idJugador, int idCarta, TipusRecurs recurs) {
        var jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new IllegalArgumentException("No existeix el jugador " + idJugador));

        GameRulesHelper.checkAction(jugador, false);
        var partida = jugador.getPartida();
        var carta = cartaDesenvolupamentRepository.findById(idCarta)
                .orElseThrow(() -> new IllegalArgumentException("No existeix la carta amb id: " + idCarta));

        if (carta.getPropietari() == null
                || !carta.getPropietari().getId().equals(idJugador)) {
            throw new IllegalStateException(
                    "Aquesta carta no pertany al jugador.");
        }
        if (carta.getUsada()) {
            throw new IllegalStateException(
                    "Aquesta carta ja s'ha utilitzat.");
        }
        if (carta.getRondaCompra() == null
                || carta.getRondaCompra() >= partida.getRonda()) {
            throw new IllegalStateException(
                    "No pots jugar una carta durant la mateixa ronda en què l'has comprat.");
        }
        if (carta.getTipus() == TipusCarta.PUNT_VICTORIA) {
            throw new IllegalStateException(
                    "Les cartes de punt de victòria es compten directament a CalcularPunts.");
        }
        if (carta.getTipus() == TipusCarta.MONOPOLI && recurs == null) {
            throw new IllegalArgumentException(
                    "Has d'indicar el recurs del monopoli.");
        }
        if (carta.getTipus() == TipusCarta.CAVALLER) {
            jugador.setCavallersJugats(
                    jugador.getCavallersJugats() + 1);
            partida.setLladrePendent(true);
            // PartidaDao.comprovarVictoria(session, partida);
            // TODO: wait for PartidaService
        } else if (carta.getTipus() == TipusCarta.MONOPOLI) {
            var currentPlrRecurs = GameRulesHelper.getResource(jugador, recurs);
            for (var otherPlr : partida.getJugadors()) {
                if (otherPlr.getId().equals(idJugador)) {
                    continue;
                }
                var otherPlrRecurs = GameRulesHelper.getResource(otherPlr, recurs);
                currentPlrRecurs.setQuantitat(currentPlrRecurs.getQuantitat() + otherPlrRecurs.getQuantitat());
                otherPlrRecurs.setQuantitat(0);
            }
        }
        carta.setUsada(true);

    }

    @Transactional(readOnly = true)
    public List<Vertex> vertexsDisponibles(int idJugador) {
        var jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new IllegalArgumentException("No existeix el jugador amb id: " + idJugador));
        var partida = jugador.getPartida();
        return vertexRepository.findDisponibles(partida, jugador, partida.getEstat() == EstatPartida.PREPARACIO);
    }

    @Transactional
    public void mostrarRecursos(int idJugador) {
        Jugador jugador = null;
        Optional<Jugador> j = jugadorRepository.findById(idJugador);
        if (j.isPresent()) {
            jugador = j.get();
        }
        else {
            throw new IllegalArgumentException(
                "No existeix el jugador " + idJugador
            );
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Inventari de ").append(jugador.getNom()).append(" (").append(jugador.getColor()).append(")\n");
        for (RecursJugador r : jugador.getInventari()) {
            sb.append("\t").append(r.getTipus()).append(": ").append(r.getQuantitat()).append("\n");
        }

        String text = sb.toString();
        System.out.println(text);
    }

    @Transactional
    public void construirCarretera(int idJugador, int idAresta) {
        Jugador jugador = null;
        Optional<Jugador> j = jugadorRepository.findById(idJugador);
        if (j.isPresent()) {
            jugador = j.get();
        }else {
            throw new IllegalArgumentException("Jugador not found with id: " + idJugador);
        }

        GameRulesHelper.checkAction(jugador, true);
        var partida = jugador.getPartida();
        Aresta aresta = null;
        var a = arestaRepository.findById(idAresta);
        if (a.isPresent()) {
            aresta = a.get();
        }
        if (aresta == null || !aresta.getPartida().getId().equals(partida.getId()))
            throw new IllegalArgumentException("Aresta " + idAresta + " isn't part of this game");

        var roadsOnAresta = carreteraRepository.countByAresta(aresta);
        if (roadsOnAresta > 0)
            throw new IllegalStateException("There's already a road on this aresta");

        var ownRoads = carreteraRepository.countByPropietari(jugador);
        if (ownRoads >= 15)
            throw new IllegalStateException("You've already built 15 roads");

        var vertexA = aresta.getVertexA();
        var vertexB = aresta.getVertexB();

        if (partida.getEstat() == EstatPartida.PREPARACIO) {
            // the road must touch the poblat placed this turn, which is the player's
            // only poblat that doesn't have one of their roads next to it yet
            var newPoblats = poblatRepository.poblatsPerJugadorVertexAB(jugador, vertexA, vertexB);
            if (newPoblats == 0)
                throw new IllegalStateException("The road must touch the poblat you just placed");
        } else {
            // the road must touch one of the player's poblats/ciutats or another of
            // their roads
            var ownAssentaments = assentamentRepository.assentamentsPerJugadorVertexAB(jugador, vertexA, vertexB);
            var ownConnectedRoads = carreteraRepository.carreteresPerJugadorVertexAB(jugador, vertexA, vertexB);
            if (ownAssentaments == 0 && ownConnectedRoads == 0)
                throw new IllegalStateException("The road must be connected to one of your buildings or roads");

            GameRulesHelper.pay(jugador, GameRulesHelper.COST_CARRETERA);
        }

        var carretera = new Carretera();
        carretera.setPropietari(jugador);
        carretera.setAresta(aresta);
        carreteraRepository.save(carretera);
    }

    @Transactional
    public void construirPoblat(int idJugador, int idVertex) {
        Jugador jugador = null;
        Optional<Jugador> j = jugadorRepository.findById(idJugador);
        if (j.isPresent()) {
            jugador = j.get();
        } else {
            throw new IllegalArgumentException("Jugador not found with id: " + idJugador);
        }
        
        GameRulesHelper.checkAction(jugador, true);
        var partida = jugador.getPartida();
        var preparacio = partida.getEstat() == EstatPartida.PREPARACIO;
        Vertex vertex = null;
        Optional<Vertex> v = vertexRepository.findById(idVertex);
        if (v.isPresent()) {
            vertex = v.get();
        }
        if (vertex == null || !vertex.getPartida().getId().equals(partida.getId()))
            throw new IllegalArgumentException("Vertex " + idVertex + " isn't part of this game");

        var buildingsOnVertex = assentamentRepository.countByVertex(vertex);
        if (buildingsOnVertex > 0)
            throw new IllegalStateException("There's already a building on this vertex");

        // don't allow building if there's another building on an adjacent vertex
        // (distance rule)
        var buildingsNextTo = assentamentRepository.assentamentsPerVertex(vertex);
        if (buildingsNextTo > 0)
            throw new IllegalStateException("Too close to another building (distance rule)");

        var ownPoblats = poblatRepository.countByPropietari(jugador);
        if (ownPoblats >= 5)
            throw new IllegalStateException("You've already built 5 poblats");

        if (preparacio) {
            if (ownPoblats >= partida.getRonda()) // the player can only place 1 poblat per turn during the
                                                    // preparation phase
                throw new IllegalStateException("You've already placed your poblat this turn");
        } else {
            var ownRoadsHere = carreteraRepository.carreteresPerJugadorVertex(jugador, vertex);
            if (ownRoadsHere == 0)
                throw new IllegalStateException("The poblat must be connected to one of your roads");

            GameRulesHelper.pay(jugador, GameRulesHelper.COST_POBLAT);
        }

        var poblat = new Poblat();
        poblat.setPropietari(jugador);
        poblat.setVertex(vertex);
        poblatRepository.save(poblat);

        // the second poblat of the preparation phase gives 1 resource for each
        // hexagon it touches (desert gives nothing)
        if (preparacio && ownPoblats == 1) {
            var hexagons = hexagonRepository.hexagonsPerVertex(vertex);
            for (var hexagon : hexagons) {
                var resource = hexagon.getTerreny().getResource();
                if (resource != null)
                    GameRulesHelper.addResource(jugador, resource, 1);
            }
        }

        // TODO: wait for PartidaService


    }

    @Transactional
    public void millorarACiutat(int idJugador, int idVertex) {
        Jugador jugador = null;
        Optional<Jugador> j = jugadorRepository.findById(idJugador);
        if (j.isPresent()) {
            jugador = j.get();
        } else {
            throw new IllegalArgumentException(
                "No existeix el jugador."
            );
        }
        Partida partida = jugador.getPartida();
        if (partida.getActiu() == null
                || !partida.getActiu().getId().equals(idJugador)) {
            throw new IllegalStateException(
                "No és el torn d'aquest jugador."
            );
        }
        Vertex vertex = null;
        Optional<Vertex> v = vertexRepository.findById(idVertex);
        if (v.isPresent()) {
            vertex = v.get();
        } else {
            throw new IllegalArgumentException(
                "No existeix el vertex."
            );
        }
        if (!vertex.getPartida().getId().equals(partida.getId())) {
            throw new IllegalArgumentException(
                "El vertex no pertany a aquesta partida."
            );
        }
        Poblat poblat = poblatRepository.poblatPerVertex(vertex);
        if (poblat == null) {
            throw new IllegalStateException(
                "No hi ha cap poblat en aquest vertex."
            );
        }
        if (!poblat.getPropietari().getId().equals(idJugador)) {
            throw new IllegalStateException(
                "El poblat no pertany a aquest jugador."
            );
        }
        Long nombreCiutats = ciutatRepository.countByPropietari(jugador);
        if (nombreCiutats >= 4) {
            throw new IllegalStateException(
                "Ja tens el maxim de 4 ciutats."
            );
        }
        RecursJugador blat = null;
        RecursJugador mineral = null;
        for (RecursJugador recurs : jugador.getInventari()) {
            if (recurs.getTipus() == TipusRecurs.BLAT) {
                blat = recurs;
            }

            if (recurs.getTipus() == TipusRecurs.MINERAL) {
                mineral = recurs;
            }
        }
        if (blat == null || mineral == null
                || blat.getQuantitat() < 2
                || mineral.getQuantitat() < 3) {
            throw new IllegalStateException(
                "No tens prou recursos per construir una ciutat."
            );
        }
        blat.setQuantitat(blat.getQuantitat() - 2);
        mineral.setQuantitat(mineral.getQuantitat() - 3);
        Ciutat ciutat = new Ciutat();
        ciutat.setVertex(vertex);
        ciutat.setPropietari(jugador);
        poblatRepository.delete(poblat);
        ciutatRepository.save(ciutat);
    }

    public void comerciarBanca(int idJugador, TipusRecurs dona, TipusRecurs rep) {
        Jugador jugador = null;
        Optional<Jugador> j = jugadorRepository.findById(idJugador);
        if (j.isPresent()) {
            jugador = j.get();
        } else {
            throw new IllegalArgumentException(
                    "El jugador no existeix."
            );
        }
        Partida partida = jugador.getPartida();
        if (partida.getActiu() == null
                || !partida.getActiu().getId().equals(idJugador)) {
            throw new IllegalStateException(
                    "No és el torn d'aquest jugador."
            );
        }
        if (dona == null || rep == null || dona == rep) {
            throw new IllegalArgumentException(
                    "Els tipus de recursos han de ser diferents."
            );
        }
        RecursJugador recursDonat = null;
        RecursJugador recursRebut = null;
        for (RecursJugador recurs : jugador.getInventari()) {
            if (recurs.getTipus() == dona) {
                recursDonat = recurs;
            }

            if (recurs.getTipus() == rep) {
                recursRebut = recurs;
            }
        }
        if (recursDonat == null || recursRebut == null) {
            throw new IllegalStateException(
                    "No s'han trobat els recursos necessaris."
            );
        }
        if (recursDonat.getQuantitat() < 4) {
            throw new IllegalStateException(
                    "No tens prou recursos per fer l'intercanvi."
            );
        }
        recursDonat.setQuantitat(recursDonat.getQuantitat() - 4);
        recursRebut.setQuantitat(recursRebut.getQuantitat() + 1);
    }

    public int calcularPunts(int idJugador) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
