package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

import java.util.*;

/**
 * Findet eine kürzeste Lösung anhand der angegebenen Komponenten und allen
 * möglichen Fahrten.
 */
public class SolutionFinder {

    /**
     * Die Warteschlange der Breitensuche.
     */
    private Queue<State> queue;
    /**
     * Schon besuchte Status. Kommt die Breitensuche bei einem Status
     * an, der schon besucht wurde, wird dort nicht weitergesucht.
     */
    private List<List<Position>> found = new ArrayList<>();

    /**
     * Alle möglichen Fahrten.
     */
    private List<Ride> possibleRides;
    /**
     * Die Liste mit allen Komponenten.
     */
    private ComponentList componentList;

    /**
     * Der "Startstatus". Kommt die Breitensuche hier an, so wurde eine
     * Lösung gefunden.
     */
    private State startState;

    public SolutionFinder(List<Ride> possibleRides, ComponentList
            componentList) {
        this.possibleRides = possibleRides;
        this.componentList = componentList;

        // Startstatus anhand der Startpositionen erstellen
        startState = new State(componentList.onStart());

        // Warteschlange erstellen und den Endstatus einfügen, bei dem die
        // Breitensuche anfängt zu suchen.
        queue = new LinkedList<>();
        queue.add(new State(componentList.onEnd()));
    }

    /**
     * Führt die Breitensuche aus. Sie fängt an beim letzten Status, der
     * die Endpositionen beinhaltet. Bei jedem Schritt werden alle
     * möglichen Fahrten durchprobiert. Die Fahrten, die mit dem aktuellen
     * Status möglich sind, werden "rückwärts" durchgeführt. Die
     * Breitensuche ist beendet, wenn der "Startstatus", der die
     * Startpositionen beinhaltet, gefundet wurde.
     *
     * @return {@code null}, wenn keine Lösung gefunden wurde, ansonsten
     * eine kürzeste Lösung
     */
    public List<Ride> execute() {

        while (!queue.isEmpty()) {

            // Erstes Element der Warteschlange entnehmen:
            State state = queue.poll();

            // Wenn der Status schon "besucht" wurde, muss hier nicht
            // weitergemacht werden.
            if (!found.contains(state.positions)) {

                // Es wurde eine Lösung gefunden, wenn der Übergang vom
                // aktuellen Status zu den Startpositionen möglich ist.
                if (new Transition(startState, state, componentList)
                        .execute().size() > 0)
                    return state.order;

                // Status wird jetzt "besucht".
                found.add(state.positions);

                // Alle möglichen Fahrten durchprobieren:
                for (Ride ride : possibleRides) {

                    // rideState: Der Status, der gebraucht wird, damit die
                    // Fahrt durchgeführt werden kann.
                    State rideState = new State(ride, componentList);

                    // Bei allen möglichen Status, die durch den Übergang
                    // entstehen können, die Fahrt rückwärts durchführen
                    // und das Resultat der Warteschlange hinzufügen.
                    for (State newState : new Transition(rideState, state,
                            componentList).execute()) {
                        newState.back(ride);
                        queue.add(newState);
                    }

                }
            }
        }

        // Die Breitensuche hat keine Lösung gefunden, also null zurückgeben.
        return null;
    }

}
