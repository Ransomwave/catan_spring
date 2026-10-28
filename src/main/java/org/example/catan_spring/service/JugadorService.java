package org.example.catan_spring.service;

import java.util.Optional;

import org.example.catan_spring.GameRulesHelper;
import org.example.catan_spring.enums.EstatPartida;
import org.example.catan_spring.enums.TipusRecurs;
import org.example.catan_spring.model.Aresta;
import org.example.catan_spring.model.Carretera;
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
import org.example.catan_spring.repository.CiutatRepository;
import org.example.catan_spring.repository.HexagonRepository;
import org.example.catan_spring.repository.JugadorRepository;
import org.example.catan_spring.repository.PoblatRepository;
import org.example.catan_spring.repository.VertexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class JugadorService {
	
	@Autowired
	private JugadorRepository jugadorRepository;
	
	@Autowired
	private ArestaRepository arestaRepository;
	
	@Autowired
	private CarreteraRepository carreteraRepository;
	
	@Autowired
	private PoblatRepository poblatRepository;
	
	@Autowired
	private AssentamentRepository assentamentRepository;
	
	@Autowired
	private VertexRepository vertexRepository;
	
	@Autowired
	private HexagonRepository hexagonRepository;
	
	@Autowired
	private CiutatRepository ciutatRepository;
	
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

		var ownRoads = carreteraRepository.countByJugador(jugador);
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
		Optional<Vertex> v = vertexRepository.findById(idJugador);
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

		PartidaService.comprovarVictoria(partida);


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
