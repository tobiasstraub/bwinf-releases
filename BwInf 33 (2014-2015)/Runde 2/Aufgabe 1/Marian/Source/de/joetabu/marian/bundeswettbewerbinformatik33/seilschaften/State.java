package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert einen Status. Dieser beinhaltet die Positionen der
 * Komponenten und die Folge von Fahrten, die zu diesem
 * Status führt.
 */
public class State {

    /**
     * Die Positionen der Komponenten.
     * Die Indizes dieser Positionen entsprechen denen der Komponenten der
     * ComponentList.
     */
    protected List<Position> positions;
    /**
     * Die Folge von Fahrten, die zu diesem Status führt.
     */
    protected List<Ride> order;

    /**
     * Erstellt einen neuen Status anhand der angegebenen Positionen.
     */
    public State(List<Position> positions) {
        this.positions = positions;
        this.order = new ArrayList<>();
    }

    /**
     * Erstellt einen neuen Status, indem die Positionen und die Folge von
     * Fahrten vom angegebenen Status kopiert werden.
     */
    public State(State old) {
        positions = new ArrayList<>(old.positions);
        order = new ArrayList<>(old.order);
    }

    /**
     * Erstellt einen neuen Status anhand der angegebenen Fahrt und der
     * Liste mit allen Komponenten. Dabei werden die Positionen von allen
     * Komponenten, die nach oben fahren, auf TOP_BASKET, und die
     * Positionen von denen, die nach unten fahren, auf BOTTOM_BASKET
     * gesetzt. Alle anderen Positionen sind null. Der Status bezieht sich
     * also darauf, wie er aussehen muss, NACHDEM die Fahrt durchgeführt
     * wurde, wobei alle dafür irrelevanten Positionen auf null gesetzt
     * werden.
     */
    public State(Ride ride, ComponentList componentList) {

        positions = new ArrayList<>();

        for (int i = 0; i < componentList.getComponents().size(); i++) {
            if (ride.getBottom().contains(i))
                positions.add(Position.TOP_BASKET);
            else if (ride.getTop().contains(i))
                positions.add(Position.BOTTOM_BASKET);
            else
                positions.add(null);
        }
    }

    /**
     * Führt den angegebenen Zug rückwärts aus. D. h., Komponenten, die von
     * oben nach unten fahren, bekommen die Position TOP_BASKET und
     * Komponenten, die von unten nach oben fahren, bekommen die Position
     * BOTTOM_BASKET. Außerdem wird der Zug zu der Folge mit allen Fahrten
     * hinzugefügt.
     */
    public void back(Ride ride) {

        // Ganz vorne in order tun, da die Fahrt sozusagen vor allen zuvor
        // berechneten Fahrten ist:
        order.add(0, ride);

        // Komponenten, die nach unten fahren, bekommen die Position
        // TOP_BASKET.
        for (int top : ride.getTop())
            positions.set(top, Position.TOP_BASKET);

        // Komponenten, die nach oben fahren, bekommen die Position
        // BOTTOM_BASKET.
        for (int bottom : ride.getBottom())
            positions.set(bottom, Position.BOTTOM_BASKET);

    }

}
