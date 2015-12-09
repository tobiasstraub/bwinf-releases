package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

import java.util.ArrayList;
import java.util.List;

/**
 * Prüft den Übergang zwischen zwei Status, ohne, dass eine weitere Fahrt
 * durchgeführt wird. Ein Übergang ist logischerweise nur dann möglich,
 * wenn keine Komponente von oben nach unten oder umgekehrt "gehen" muss.
 * Außerdem können Steine nur in einen Korb rein oder aus einem Korb
 * rausgehen, wenn eine Person anwesend ist.
 */
public class Transition {

    /**
     * Der Status, zu dem dieser Übergang führen soll.
     */
    private State neededState;
    /**
     * Der Status, wie er aktuell ist.
     */
    private State currentState;
    /**
     * Die Liste mit allen Komponenten.
     */
    private ComponentList componentList;

    /**
     * Diese Variablen besagen, ob Personen oben bzw. unten benötigt werden.
     */
    private boolean personNeededTop = false, personNeededBottom = false;
    /**
     * Diese Variablen besagen, ob min. eine Person oben bzw. unten ist.
     */
    private boolean personIsTop = false, personIsBottom = false;

    public Transition(State neededState, State currentState,
                      ComponentList componentList) {
        this.neededState = neededState;
        this.currentState = currentState;
        this.componentList = componentList;
    }

    /**
     * Überprüft den Übergang.
     *
     * @return alle Status, die nach dem Übergang möglich sind,
     * möglicherweise auch gar keine
     */
    public List<State> execute() {

        // Alle Positionen überprüfen
        for (int i = 0; i < neededState.positions.size(); i++) {

            // aktuelle und benötigte Positionen:
            Position needed = neededState.positions.get(i);
            Position current = currentState.positions.get(i);

            if (needed == null) {
                // Wenn die benötigte Position null ist, dann nimmt die
                // zugehörige Komponente nicht an der Fahrt teil. Daher ist
                // die benötigte Position außerhalb des Korbes, aber auf
                // der Seite der aktuellen Position:
                needed = current.free();

                // Auch der Status muss die neue Position bekommen:
                this.neededState.positions.set(i, needed);
            }

            if (!changePosition(current, needed))
                return new ArrayList<>(); // Eine Komponente müsste die
            // Seite ändern, also gibt es keine Lösung.

            // Wenn die Komponente eine Person ist, wird personIsTop bzw.
            // personIsBottom auf true gesetzt.
            if (componentList.isPerson(i) &&
                    needed.simpleSide() == Position.TOP)
                personIsTop = true;

            else if (componentList.isPerson(i) &&
                    needed.simpleSide() == Position.BOTTOM)
                personIsBottom = true;

        }

        // Die bisherige Folge von Fahrten kopieren und dem benötigten
        // Status hinzufügen, da dieser die Folge noch nirgends bekommen
        // hat, aber dieselbe Folge von Fahrten dorthin führt.
        this.neededState.order = new ArrayList<>(this.currentState.order);

        if ((personNeededTop && !personIsTop) &&
                (personNeededBottom && !personIsBottom)) {
            // Es werden Personen unten und oben benötigt, es ist aber
            // keine Person oben und es ist keine Person unten.
            return personsNeeded();

        } else if (personNeededTop && !personIsTop) {
            // Eine Person wird oben benötigt, es ist aber keine oben.
            return personNeeded(Position.TOP_FREE);

        } else if (personNeededBottom && !personIsBottom) {
            // Eine Person wird unten benötigt, es ist aber keine unten.
            return personNeeded(Position.BOTTOM_FREE);

        } else {
            // Der Übergang funktioniert reibungslos, also wird der Status
            // zurückgegeben.
            List<State> states = new ArrayList<>();
            states.add(this.neededState);
            return states;
        }
    }

    /**
     * Überprüft, ob eine Komponente von der Position current zu needed
     * wechseln kann.
     */
    private boolean changePosition(Position current, Position needed) {
        if (current.simpleSide() == needed.simpleSide() ||
                current.simpleSide() == Position.UNKNOWN) {
            // Auf derselben Seite oder die aktuelle Seite ist egal.

            if (current.simpleBasket() == needed.simpleBasket() ||
                    current.simpleBasket() == Position.UNKNOWN) {
                // Beide Positionen sind frei bzw. im Korb oder es ist
                // egal, ob die Komponente aktuell im Korb ist. Der Wechsel
                // für diese Komponente ist auf jeden Fall möglich.
                return true;

            } else {
                // Die Positionen sind nicht beide frei bzw. im Korb, also
                // wird auf dieser Seite eine Person benötigt.
                if (needed.simpleSide() == Position.TOP)
                    personNeededTop = true;
                else personNeededBottom = true;
                return true;
            }

        } else // Auf unterschiedlichen Seiten -> nicht möglich
            return false;
    }

    /**
     * Es wird eine Person an der angegebenen Position benötigt. Diese
     * Methode sucht nach Personen, deren benötigte Seiten unbekannt (also
     * letztendlich egal) sind. Wird eine solche Person gefunden, dann wird
     * sie an die entsprechende Position gesetzt. Alle dabei entstehenden
     * Varianten werden danach zurückgegeben.
     */
    private List<State> personNeeded(Position position) {

        List<State> states = new ArrayList<>();

        for (int i = 0; i < neededState.positions.size(); i++) {
            if (componentList.isPerson(i) && neededState.positions.get(i)
                    .simpleSide() == Position.UNKNOWN) {

                // Status erstellen, die Position der Person festlegen und
                // den Status der Liste hinzufügen.
                State state = new State(neededState);
                state.positions.set(i, position);
                states.add(state);
            }
        }

        return states;
    }

    /**
     * Es werden Personen oben und unten benötigt. Diese Methode sucht nach
     * Personen, deren benötigte Seiten unbekannt (also letztendlich egal)
     * sind. Werden zwei solche Person gefunden, dann werden sie an die
     * entsprechenden Positionen TOP_FREE und BOTTOM_FREE gesetzt. Alle
     * dabei entstehenden Varianten werden danach zurückgegeben.
     */
    private List<State> personsNeeded() {

        List<State> states = new ArrayList<>();

        for (int i = 0; i < neededState.positions.size(); i++) {
            if (componentList.isPerson(i) &&
                    neededState.positions.get(i) == Position.UNKNOWN) {

                // Komponente wird mit allen anderen Komponenten kombiniert:
                for (int j = 0; j < neededState.positions.size(); j++) {

                    if (componentList.isPerson(j) && neededState.positions
                            .get(j).simpleSide() == Position.UNKNOWN
                            && i != j) {

                        // Status erstellen, die Positionen der Personen
                        // festlegen und den Status der Liste hinzufügen.
                        State state = new State(neededState);
                        state.positions.set(i, Position.TOP_FREE);
                        state.positions.set(j, Position.BOTTOM_FREE);
                        states.add(state);
                    }
                }
            }
        }

        return states;
    }

}
