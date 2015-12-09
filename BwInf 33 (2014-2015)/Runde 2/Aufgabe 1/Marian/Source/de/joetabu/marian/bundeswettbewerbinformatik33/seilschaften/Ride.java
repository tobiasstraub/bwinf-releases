package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

import java.util.List;

/**
 * Repräsentiert eine Fahrt. Dafür gibt es eine Liste mit den Indizes der
 * Komponenten, die nach unten fahren und eine Liste mit den Indexen der
 * Komponenten, die nach oben fahren. Die Indizes beziehen sich auf die in
 * der ComponentList verwendeten Liste mit allen Komponenten.
 */
public class Ride {

    private List<Integer> top, bottom;

    /**
     * @param top die Komponenten, die anfangs oben sind und nach unten fahren
     * @param bottom die Komponenten, die anfangs unten sind und nach oben
     *               fahren
     */
    public Ride(List<Integer> top, List<Integer> bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public List<Integer> getTop() {
        return top;
    }

    public List<Integer> getBottom() {
        return bottom;
    }

    @Override
    public String toString() {
        return top + " | " + bottom;
    }

}
