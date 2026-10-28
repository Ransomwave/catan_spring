package org.example.catan_spring;

import java.util.Map;

import org.example.catan_spring.enums.EstatPartida;
import org.example.catan_spring.enums.TipusRecurs;
import org.example.catan_spring.model.Jugador;
import org.example.catan_spring.model.RecursJugador;


// Aquesta classe l'hem afegit nosaltres per tenir les regles del joc en un sol lloc i no repetir codi a cadascuna de les nostres implementacions

/**
 * Game rules shared by the DAOs. Every method works on entities loaded in the
 * current session, so changes are saved when the caller commits. Rule
 * violations throw IllegalStateException with a message explaining why.
 */
public final class GameRulesHelper {

    public static final Map<TipusRecurs, Integer> COST_CARRETERA = Map.of(
            TipusRecurs.FUSTA, 1, TipusRecurs.MAO, 1);
    public static final Map<TipusRecurs, Integer> COST_POBLAT = Map.of(
            TipusRecurs.FUSTA, 1, TipusRecurs.MAO, 1, TipusRecurs.LLANA, 1, TipusRecurs.BLAT, 1);
    public static final Map<TipusRecurs, Integer> COST_CIUTAT = Map.of(
            TipusRecurs.BLAT, 2, TipusRecurs.MINERAL, 3);
    public static final Map<TipusRecurs, Integer> COST_CARTA = Map.of(
            TipusRecurs.BLAT, 1, TipusRecurs.LLANA, 1, TipusRecurs.MINERAL, 1);

    private GameRulesHelper() {
    }

    /**
     * Checks that the player can perform an action right now: the game isn't
     * finished, it's their turn and, during EN_CURS, the dice have been rolled
     * and the robber isn't pending.
     *
     * @param allowedInPreparacio true only for ConstruirPoblat and
     *                            ConstruirCarretera
     */
    public static void checkAction(Jugador jugador, boolean allowedInPreparacio) {
        var partida = jugador.getPartida();

        if (partida.getEstat() == EstatPartida.FINALITZADA)
            throw new IllegalStateException("The game has already finished");
        if (partida.getEstat() == EstatPartida.PREPARACIO && !allowedInPreparacio)
            throw new IllegalStateException("This action isn't allowed during the preparation phase");

        var actiu = partida.getActiu();
        if (actiu == null || !actiu.getId().equals(jugador.getId()))
            throw new IllegalStateException("It's not " + jugador.getNom() + "'s turn");

        if (partida.getEstat() == EstatPartida.EN_CURS) {
            if (!partida.isDausTirats())
                throw new IllegalStateException("The dice must be rolled first");
            if (partida.isLladrePendent())
                throw new IllegalStateException("The robber must be moved first");
        }
    }

    public static RecursJugador getResource(Jugador jugador, TipusRecurs tipus) {
        for (var recurs : jugador.getInventari()) {
            if (recurs.getTipus() == tipus)
                return recurs;
        }
        throw new IllegalStateException(jugador.getNom() + " has no " + tipus + " in their inventory");
    }

    public static boolean hasResources(Jugador jugador, Map<TipusRecurs, Integer> cost) {
        for (var entry : cost.entrySet()) {
            if (getResource(jugador, entry.getKey()).getQuantitat() < entry.getValue())
                return false;
        }
        return true;
    }

    /**
     * Subtracts the cost from the player's resources. Throws without changing
     * anything if they can't afford it.
     */
    public static void pay(Jugador jugador, Map<TipusRecurs, Integer> cost) {
        if (!hasResources(jugador, cost))
            throw new IllegalStateException(jugador.getNom() + " doesn't have enough resources");
        for (var entry : cost.entrySet())
            addResource(jugador, entry.getKey(), -entry.getValue());
    }

    /**
     * Adds (or, with a negative amount, removes) resources of one type.
     */
    public static void addResource(Jugador jugador, TipusRecurs tipus, int amount) {
        var recurs = getResource(jugador, tipus);
        recurs.setQuantitat(recurs.getQuantitat() + amount);
    }

    public static int totalResources(Jugador jugador) {
        var total = 0;
        for (var recurs : jugador.getInventari())
            total += recurs.getQuantitat();
        return total;
    }
}
