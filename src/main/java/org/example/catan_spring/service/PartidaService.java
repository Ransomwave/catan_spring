package org.example.catan_spring.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.example.catan_spring.GameRulesHelper;
import org.example.catan_spring.dto.DiceRollResponse;
import org.example.catan_spring.dto.GameResponse;
import org.example.catan_spring.dto.HexagonResponse;
import org.example.catan_spring.dto.PlayerResponse;
import org.example.catan_spring.enums.ColorJugador;
import org.example.catan_spring.enums.EstatPartida;
import org.example.catan_spring.enums.TipusCarta;
import org.example.catan_spring.enums.TipusRecurs;
import org.example.catan_spring.enums.TipusTerreny;

import org.example.catan_spring.model.Aresta;
import org.example.catan_spring.model.Assentament;
import org.example.catan_spring.model.CartaDesenvolupament;
import org.example.catan_spring.model.Ciutat;
import org.example.catan_spring.model.Hexagon;
import org.example.catan_spring.model.HexagonVertex;
import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.Partida;
import org.example.catan_spring.model.RecursJugador;
import org.example.catan_spring.model.Vertex;

import org.example.catan_spring.repository.ArestaRepository;
import org.example.catan_spring.repository.AssentamentRepository;
import org.example.catan_spring.repository.CarreteraRepository;
import org.example.catan_spring.repository.CartaDesenvolupamentRepository;
import org.example.catan_spring.repository.CiutatRepository;
import org.example.catan_spring.repository.HexagonRepository;
import org.example.catan_spring.repository.HexagonVertexRepository;
import org.example.catan_spring.repository.JugadorRepository;
import org.example.catan_spring.repository.PartidaRepository;
import org.example.catan_spring.repository.VertexRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidaService {
    private final PartidaRepository partidaRepository;
    private final JugadorRepository jugadorRepository;
    private final CartaDesenvolupamentRepository cartaRepository;
    private final CiutatRepository ciutatRepository;
    private final CarreteraRepository carreteraRepository;
    private final VertexRepository vertexRepository;
    private final HexagonRepository hexagonRepository;
    private final HexagonVertexRepository hexagonVertexRepository;
    private final ArestaRepository arestaRepository;
    private final AssentamentRepository assentamentRepository;

    private final Random random = new Random();

    public PartidaService(
            PartidaRepository partidaRepository,
            JugadorRepository jugadorRepository,
            CartaDesenvolupamentRepository cartaRepository,
            CiutatRepository ciutatRepository,
            CarreteraRepository carreteraRepository,
            VertexRepository vertexRepository,
            HexagonRepository hexagonRepository,
            HexagonVertexRepository hexagonVertexRepository,
            ArestaRepository arestaRepository,
            AssentamentRepository assentamentRepository) {

        this.partidaRepository = partidaRepository;
        this.jugadorRepository = jugadorRepository;
        this.cartaRepository = cartaRepository;
        this.ciutatRepository = ciutatRepository;
        this.carreteraRepository = carreteraRepository;
        this.vertexRepository = vertexRepository;
        this.hexagonRepository = hexagonRepository;
        this.hexagonVertexRepository = hexagonVertexRepository;
        this.arestaRepository = arestaRepository;
        this.assentamentRepository = assentamentRepository;
    }

    // Busca una partida per id
    private Partida buscarPartida(Integer idPartida) {

        Optional<Partida> resultat = partidaRepository.findById(idPartida);

        if (resultat.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existeix la partida amb id: " + idPartida);
        }

        return resultat.get();
    }

    private static <T> ResponseEntity<T> error(HttpStatus status, String info) {
        return ResponseEntity.status(status).header("Partida-info", info).build();
    }

    // Crea una partida, els jugadors, el tauler i les cartes
    @Transactional
    public ResponseEntity<GameResponse> iniciarPartida(List<String> playerNames, Integer pointsToWin) {

        if (playerNames == null
                || playerNames.size() < 2
                || playerNames.size() > 4) {
            return error(HttpStatus.BAD_REQUEST, "Hi ha d'haver entre 2 i 4 jugadors");
        }

        Partida partida = new Partida(EstatPartida.PREPARACIO, 1, pointsToWin == null ? 10 : pointsToWin);
        partidaRepository.save(partida);

        List<String> names = new ArrayList<>(playerNames);
        Collections.shuffle(names);

        for (int i = 0; i < names.size(); i++) {

            Jugador jugador = new Jugador(
                    names.get(i),
                    ColorJugador.values()[i],
                    i + 1,
                    0,
                    partida);

            jugadorRepository.save(jugador);
            partida.getJugadors().add(jugador);

            if (i == 0) {
                partida.setActiu(jugador);
            }
        }

        loadBoard(partida);

        // Pila de cartes: 14 cavallers, 5 punts de victòria i 2 monopolis
        for (int i = 0; i < 14; i++) {
            CartaDesenvolupament carta = new CartaDesenvolupament(
                    TipusCarta.CAVALLER, false, null, partida);
            cartaRepository.save(carta);
        }

        for (int i = 0; i < 5; i++) {
            CartaDesenvolupament carta = new CartaDesenvolupament(
                    TipusCarta.PUNT_VICTORIA, false, null, partida);
            cartaRepository.save(carta);
        }

        for (int i = 0; i < 2; i++) {
            CartaDesenvolupament carta = new CartaDesenvolupament(
                    TipusCarta.MONOPOLI, false, null, partida);
            cartaRepository.save(carta);
        }

        var players = new ArrayList<PlayerResponse>();
        for (var jugador : partida.getJugadors()) {
            players.add(PlayerResponse.from(jugador));
        }
        var hexagons = new ArrayList<HexagonResponse>();
        for (var hexagon : partida.getTauler()) {
            hexagons.add(HexagonResponse.from(hexagon, partida, List.of()));
        }
        var game = new GameResponse(partida.getId(), partida.getEstat(), partida.getRonda(),
                partida.getPuntsPerGuanyar(), players, hexagons);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Partida-info", "Partida creada ID: " + partida.getId())
                .body(game);
    }

    private void afegirTerrenys(
            List<TipusTerreny> llista, TipusTerreny tipus, int quantitat) {

        for (int i = 0; i < quantitat; i++) {
            llista.add(tipus);
        }
    }

    // Carrega els vèrtexs, hexàgons, relacions i arestes del tauler.
    private void loadBoard(Partida partida) {

        HashMap<Integer, Vertex> vertices = new HashMap<>();

        List<String[]> filesVertexs = readCsv("vertexs.csv");

        for (String[] fila : filesVertexs) {

            int codi = Integer.parseInt(fila[0]);
            Vertex vertex = new Vertex(codi, partida);

            vertexRepository.save(vertex);
            vertices.put(codi, vertex);
        }

        List<TipusTerreny> terrenys = new ArrayList<>();

        afegirTerrenys(terrenys, TipusTerreny.BOSC, 4);
        afegirTerrenys(terrenys, TipusTerreny.TURONS, 3);
        afegirTerrenys(terrenys, TipusTerreny.PASTURES, 4);
        afegirTerrenys(terrenys, TipusTerreny.CAMPS, 4);
        afegirTerrenys(terrenys, TipusTerreny.MUNTANYES, 3);
        afegirTerrenys(terrenys, TipusTerreny.DESERT, 1);

        Collections.shuffle(terrenys);

        int[] valorsNumeros = {
                2, 3, 3, 4, 4, 5, 5, 6, 6,
                8, 8, 9, 9, 10, 10, 11, 11, 12
        };

        List<Integer> numeros = new ArrayList<>();

        for (int valor : valorsNumeros) {
            numeros.add(valor);
        }

        Collections.shuffle(numeros);

        HashMap<Integer, Hexagon> hexagons = new HashMap<>();

        List<String[]> filesHexagons = readCsv("hexagons.csv");

        for (String[] fila : filesHexagons) {

            int codi = Integer.parseInt(fila[0]);

            TipusTerreny terreny = terrenys.remove(terrenys.size() - 1);

            Integer numero = null;

            if (terreny != TipusTerreny.DESERT) {
                numero = numeros.remove(numeros.size() - 1);
            }

            Hexagon hexagon = new Hexagon(
                    codi,
                    terreny,
                    numero,
                    Integer.parseInt(fila[1]),
                    Integer.parseInt(fila[2]),
                    partida);

            hexagonRepository.save(hexagon);
            hexagons.put(codi, hexagon);
            partida.getTauler().add(hexagon);

            if (terreny == TipusTerreny.DESERT) {
                partida.setLladre(hexagon);
            }
        }

        List<String[]> filesHexagonVertex = readCsv("hexagon_vertex.csv");

        for (String[] fila : filesHexagonVertex) {

            Hexagon hexagon = hexagons.get(Integer.parseInt(fila[0]));
            Vertex vertex = vertices.get(Integer.parseInt(fila[1]));

            HexagonVertex relacio = new HexagonVertex(
                    hexagon,
                    vertex,
                    Integer.parseInt(fila[2]));

            hexagonVertexRepository.save(relacio);
        }

        List<String[]> filesArestes = readCsv("arestes.csv");

        for (String[] fila : filesArestes) {

            Vertex vertexA = vertices.get(Integer.parseInt(fila[1]));
            Vertex vertexB = vertices.get(Integer.parseInt(fila[2]));

            Aresta aresta = new Aresta(
                    Integer.parseInt(fila[0]),
                    partida,
                    vertexA,
                    vertexB);

            arestaRepository.save(aresta);
        }
    }

    private List<String[]> readCsv(String nomFitxer) {

        try (BufferedReader lector = new BufferedReader(
                obrirFitxer(nomFitxer))) {

            List<String[]> files = new ArrayList<>();

            lector.readLine();

            String linia = lector.readLine();

            while (linia != null) {

                if (!linia.isBlank()) {
                    files.add(linia.split(","));
                }

                linia = lector.readLine();
            }

            return files;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private InputStreamReader obrirFitxer(String nomFitxer) {

        InputStream stream = getClass().getResourceAsStream("/board/" + nomFitxer);

        if (stream == null) {
            throw new IllegalStateException(
                    "No s'ha trobat el fitxer: /board/" + nomFitxer);
        }

        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<HexagonResponse>> mostrarTauler(Integer idPartida) {

        var partida = partidaRepository.findById(idPartida).orElse(null);
        if (partida == null)
            return error(HttpStatus.NOT_FOUND, "No existeix la partida amb id: " + idPartida);

        // Every building on the board in a single query, grouped by hexagon id
        var buildings = new HashMap<Integer, List<HexagonResponse.Building>>();
        for (Object[] row : hexagonVertexRepository.findAssentamentsByPartidaId(idPartida)) {
            var hexagonId = (Integer) row[0];
            var assentament = (Assentament) row[1];
            buildings.computeIfAbsent(hexagonId, k -> new ArrayList<>())
                    .add(HexagonResponse.Building.from(assentament));
        }

        var board = new ArrayList<HexagonResponse>();
        for (Hexagon hexagon : hexagonRepository.findByPartida_IdOrderByCodiAsc(idPartida)) {
            board.add(HexagonResponse.from(hexagon, partida, buildings.getOrDefault(hexagon.getId(), List.of())));
        }
        return ResponseEntity.ok(board);
    }

    @Transactional(readOnly = true)
    public void mostrarPartida(Integer idPartida) {

        Partida partida = buscarPartida(idPartida);

        List<Jugador> jugadors = jugadorRepository.findByPartida_IdOrderByOrdreTornAsc(
                idPartida);

        String nomActiu;

        if (partida.getActiu() == null) {
            nomActiu = "Cap";
        } else {
            nomActiu = partida.getActiu().getNom();
        }

        String textTirada;

        if (partida.getUltimaTirada() == null) {
            textTirada = "Sense tirada";
        } else {
            textTirada = String.valueOf(partida.getUltimaTirada());
        }

        System.out.println("===== PARTIDA " + partida.getId() + " =====");
        System.out.println("Estat: " + partida.getEstat());
        System.out.println("Ronda: " + partida.getRonda());
        System.out.println("Jugador actiu: " + nomActiu);
        System.out.println("Última tirada: " + textTirada);

        for (Jugador jugador : jugadors) {

            Integer idJugador = jugador.getId();

            long poblats = assentamentRepository
                    .comptarAssentamentsQueNoSonCiutat(idJugador);

            long ciutats = ciutatRepository.countByPropietari_Id(idJugador);

            long carreteres = carreteraRepository.countByPropietari_Id(idJugador);

            long recursos = jugadorRepository.comptarRecursosTotals(idJugador);

            int punts = calcularPunts(jugador);

            System.out.println();
            System.out.println("Jugador: " + jugador.getNom());
            System.out.println("Punts: " + punts);
            System.out.println("Poblats: " + poblats);
            System.out.println("Ciutats: " + ciutats);
            System.out.println("Carreteres: " + carreteres);
            System.out.println("Recursos totals: " + recursos);
        }

        System.out.println();
        System.out.println("===== FI DE LA PARTIDA =====");
    }

    @Transactional
    public ResponseEntity<DiceRollResponse> tirarDaus(int idPartida) {

        var partida = partidaRepository.findById(idPartida).orElse(null);
        if (partida == null)
            return error(HttpStatus.NOT_FOUND, "No existeix la partida amb id: " + idPartida);

        if (partida.getEstat() != EstatPartida.EN_CURS)
            return error(HttpStatus.CONFLICT, "Només es poden tirar els daus amb la partida en curs");

        if (partida.isDausTirats())
            return error(HttpStatus.CONFLICT, "Els daus ja s'han tirat aquest torn");

        int dau1 = random.nextInt(6) + 1;
        int dau2 = random.nextInt(6) + 1;
        int tirada = dau1 + dau2;

        partida.setUltimaTirada(tirada);
        partida.setDausTirats(true);

        // Keyed by player id
        var received = new HashMap<Integer, EnumMap<TipusRecurs, Integer>>();
        var discarded = new HashMap<Integer, Integer>();

        if (tirada == 7) {
            gestionarSet(partida, discarded);
        } else {
            repartirRecursos(partida, tirada, received);
        }

        var players = new ArrayList<DiceRollResponse.PlayerResources>();
        for (Jugador jugador : jugadorRepository.findByPartida_IdOrderByOrdreTornAsc(idPartida)) {
            players.add(new DiceRollResponse.PlayerResources(jugador.getId(), jugador.getNom(),
                    received.getOrDefault(jugador.getId(), new EnumMap<>(TipusRecurs.class)),
                    discarded.getOrDefault(jugador.getId(), 0)));
        }
        return ResponseEntity.ok(new DiceRollResponse(dau1, dau2, tirada, partida.isLladrePendent(), players));
    }

    private void gestionarSet(Partida partida, HashMap<Integer, Integer> discarded) {

        for (Jugador jugador : partida.getJugadors()) {

            int total = GameRulesHelper.totalResources(jugador);

            if (total <= 7) {
                continue;
            }

            List<TipusRecurs> cartes = new ArrayList<>();

            for (RecursJugador recurs : jugador.getInventari()) {
                for (int i = 0; i < recurs.getQuantitat(); i++) {
                    cartes.add(recurs.getTipus());
                }
            }

            Collections.shuffle(cartes);

            int aDescartar = total / 2;

            for (int i = 0; i < aDescartar; i++) {
                GameRulesHelper.addResource(jugador, cartes.get(i), -1);
            }

            discarded.put(jugador.getId(), aDescartar);
        }

        partida.setLladrePendent(true);
    }

    private void repartirRecursos(
            Partida partida, int tirada, HashMap<Integer, EnumMap<TipusRecurs, Integer>> received) {

        Integer idLladre = partida.getLladre().getId();

        List<Object[]> files = hexagonVertexRepository.trobarProduccioPerTirada(
                partida.getId(), tirada, idLladre);

        for (Object[] fila : files) {

            TipusTerreny terreny = (TipusTerreny) fila[0];
            Assentament assentament = (Assentament) fila[1];

            Jugador jugador = assentament.getPropietari();

            int quantitat = 1;

            if (assentament instanceof Ciutat) {
                quantitat = 2;
            }

            GameRulesHelper.addResource(
                    jugador, terreny.getResource(), quantitat);

            received.computeIfAbsent(jugador.getId(), k -> new EnumMap<>(TipusRecurs.class))
                    .merge(terreny.getResource(), quantitat, Integer::sum);
        }
    }

    // Punts: poblats (1), ciutats (2), cartes de punt de victòria (1)
    // i exèrcit més gran (2)
    @Transactional(readOnly = true)
    public int calcularPunts(Jugador jugador) {

        Integer idJugador = jugador.getId();

        long poblats = assentamentRepository
                .comptarAssentamentsQueNoSonCiutat(idJugador);

        long ciutats = ciutatRepository.countByPropietari_Id(idJugador);

        long cartes = cartaRepository.comptarCartesPerTipus(
                idJugador, TipusCarta.PUNT_VICTORIA);

        int punts = (int) poblats + (int) ciutats * 2 + (int) cartes;

        Jugador exercit = exercitMesGran(jugador.getPartida().getId());

        if (exercit != null) {
            if (exercit.getId().equals(jugador.getId())) {
                punts = punts + 2;
            }
        }

        return punts;
    }

    // Retorna el jugador amb l'exèrcit més gran
    @Transactional(readOnly = true)
    public Jugador exercitMesGran(Integer idPartida) {

        List<Jugador> jugadors = jugadorRepository.findByPartida_IdOrderByCavallersJugatsDesc(
                idPartida);

        if (jugadors.isEmpty()) {
            return null;
        }

        Jugador primer = jugadors.get(0);

        if (primer.getCavallersJugats() < 3) {
            return null;
        }

        if (jugadors.size() > 1) {

            Jugador segon = jugadors.get(1);

            if (primer.getCavallersJugats()
                    .equals(segon.getCavallersJugats())) {
                return null;
            }
        }

        return primer;
    }

    @Transactional
    public void comprovarVictoria(Integer idPartida) {

        Partida partida = buscarPartida(idPartida);

        List<Jugador> jugadors = jugadorRepository.findByPartida_IdOrderByOrdreTornAsc(
                idPartida);

        for (Jugador jugador : jugadors) {

            int punts = calcularPunts(jugador);

            if (punts >= partida.getPuntsPerGuanyar()) {
                partida.setGuanyador(jugador);
                partida.setEstat(EstatPartida.FINALITZADA);
                return;
            }
        }
    }
}