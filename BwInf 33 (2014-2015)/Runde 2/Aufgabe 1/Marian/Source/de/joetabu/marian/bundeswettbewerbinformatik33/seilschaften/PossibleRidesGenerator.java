package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

import java.util.*;

/**
 * Generiert alle möglichen Fahrten zu einer Liste von Komponenten.
 */
public class PossibleRidesGenerator {

    private ComponentList componentList;
    private int maxWeightDifference;

    /**
     * @param componentList       die Liste mit allen Komponenten
     * @param maxWeightDifference der maximale Unterschied zwischen den
     *                            Gewichten des oberen und des unteren Korbes
     */
    public PossibleRidesGenerator(ComponentList componentList, int
            maxWeightDifference) {
        this.componentList = componentList;
        this.maxWeightDifference = maxWeightDifference;
    }

    /**
     * Generiert eine Liste mit allen möglichen Fahrten. Dabei wird
     * zunächst die Potenzmenge der Liste mit allen Komponenten in einer
     * Map mit den Teilmengen als Schlüssel und den zugehörigen Gewichten
     * als Werte berechnet. Mit der Potenzmenge werden dann alle
     * Kombinationen für Fahrten ausprobiert (eine Teilmenge stellt dabei
     * immer die Komponenten im Korb oben bzw. unten dar) und schließlich
     * werden alle Fahrten (nachdem unmögliche davon aussortiert wurden)
     * zurückgegeben.
     */
    public List<Ride> generate() {

        // Die Potenzmenge aller Komponenten als Map erstellen und die
        // Gewichte (Werte der Map) aller Teilmengen
        // (Schlüssel der Map) berechnen.
        Map<List<Component>, Integer> powerSet =
                powerSet(componentList.getComponents());

        // Alle möglichen Fahrten erstellen und zurückgeben.
        return generatePossibleRides(powerSet, componentList.getComponents());

    }

    /**
     * Generiert die Potenzmenge (engl. "power set") der Liste mit allen
     * Komponenten als Map. Die Map beinhaltet als Schlüssel eine Teilmenge
     * und direkt als Wert das zugehörige Gewicht, damit dieses später
     * nicht unnötig mehrmals berechnet werden muss. Um die Potenzmenge zu
     * erstellen, gibt es zunächst nur die leere Menge. Immer, wenn eine
     * weitere Komponente hinzugefügt wird, wird sie jeder Teilmenge
     * angehangen. Dabei bleiben trotzdem alle alten Teilmengen bestehen.
     *
     * @param components alle Komponenten
     * @return die Potenzmenge
     */
    private Map<List<Component>, Integer> powerSet(List<Component>
                                                           components) {
        Map<List<Component>, Integer> powerSet = new HashMap<>();
        powerSet.put(new ArrayList<Component>(), 0);

        // Für jede Komponente neue Teilmengen erstellen, wobei jede
        // bisherige Teilmenge genommen und einer Kopie davon die
        // Komponente hinzugefügt wird.
        for (Component component : components) {

            // newSubList: die neu entstehenden Teilmengen. Sie können der
            // Potenzmenge nicht direkt hinzugefügt werden, da durch das
            // Hinzufügen während einer Iteration der Fehler java.util
            // .ConcurrentModificationException auftritt.
            Map<List<Component>, Integer> newSubLists = new HashMap<>();

            // Alle bisherigen Teilmengen durchlaufen
            for (List<Component> subList : powerSet.keySet()) {

                // Neue Teilmenge erstellen, wobei die aktuelle Komponente
                // einer Kopie von subList hinzugefügt wird.
                List<Component> newSubList = new ArrayList<>(subList);
                newSubList.add(component);

                // newSubList der Map newSubLists hinzufügen, wobei das
                // Gewicht der Teilmenge direkt berechnet wird.
                newSubLists.put(newSubList, weight(newSubList));
            }

            powerSet.putAll(newSubLists);
        }

        return powerSet;
    }

    /**
     * Berechnet das Gesamtgewicht der Komponenten in der angegebenen Liste.
     */
    private int weight(List<Component> components) {

        int weight = 0;

        for (Component component : components)
            weight += component.getWeight();

        return weight;
    }

    /**
     * Generiert alle möglichen Fahrten mithilfe der angegebenen
     * Potenzmenge der Komponenten und deren Gewichten.
     *
     * @param powerSet   die Potenzmenge aller Komponenten
     * @param components alle Komponenten
     * @return alle möglichen Fahrten
     */
    private List<Ride> generatePossibleRides(Map<List<Component>,
            Integer> powerSet, List<Component> components) {

        // possibleRides: Liste mit allen möglichen Fahrten
        List<Ride> possibleRides = new ArrayList<>();

        // Alle Teilmengen als top bzw. bottom miteinander kombinieren.
        // Sollten diese so passen, dass eine Fahrt mit ihnen erstellt
        // werden kann, die alle Bedingungen einhält, wird sie der Liste
        // possibleRides hinzugefügt.
        for (Map.Entry<List<Component>, Integer> top : powerSet.entrySet()) {
            for (Map.Entry<List<Component>, Integer> bottom :
                    powerSet.entrySet()) {

                // Teilmengen aus top bzw. bottom auslesen (sie sind als
                // Schlüssel gespeichert).
                List<Component> topComponents = top.getKey();
                List<Component> bottomComponents = bottom.getKey();

                // Eine Komponente darf nicht gleichzeitig unten und oben sein
                if (containSameComponents(topComponents, bottomComponents))
                    continue;

                // Die Differenz zwischen den Gesamtgewichten (den Werten
                // von top bzw. bottom) berechnen.
                int weightDifference = top.getValue() - bottom.getValue();

                // Die Fahrt kann nicht stattfinden, wenn die Differenz <= 0
                // ist, da der obere Korb schwerer belastet werden muss
                // (das ist nur dann der Fall, wenn weightDifference
                // positiv ist). Außerdem ist die Fahrt nicht möglich, wenn
                // die Differenz die Maximaldifferenz überschreitet und min.
                // eine Person in einer der Körben sitzt, da Personen
                // "sicher" fahren müssen.
                if (weightDifference <= 0 ||
                        (weightDifference > maxWeightDifference &&
                                !onlyStones(topComponents, bottomComponents)))
                    continue;

                // Alle Bedingungen wurden eingehalten, daher kann die
                // Fahrt erstellt und der Liste hinzugefügt werden.
                possibleRides.add(createRide(topComponents,
                        bottomComponents, components));
            }
        }

        return possibleRides;
    }

    /**
     * Überprüft, ob es eine Komponente gibt, die in beiden Listen
     * enthalten ist.
     */
    private boolean containSameComponents(List<Component> top,
                                          List<Component> bottom) {

        // Mithilfe von verschachtelten Schleifen true zurückgeben, wenn
        // eine Komponente in beiden Liste enthalten ist.
        for (Component componentTop : top)
            for (Component componentBottom : bottom)
                if (componentTop == componentBottom)
                    return true;

        return false;
    }

    /**
     * Überprüft, ob in beiden Listen nur Steine enthalten sind.
     */
    private boolean onlyStones(List<Component> top, List<Component> bottom) {

        // Wenn irgendeine Komponente eine Person ist, wird false
        // zurückgegeben.
        for (Component component : top)
            if (component instanceof Person)
                return false;
        for (Component component : bottom)
            if (component instanceof Person)
                return false;

        return true;
    }

    /**
     * Erstellt eine Fahrt anhand der angegebenen Komponenten, die nach
     * oben bzw. nach unten fahren.
     *
     * @return die Fahrt
     */
    private Ride createRide(List<Component> top, List<Component> bottom,
                            List<Component> components) {

        // Liste mit den Indizes von den Komponenten erstellen, die oben
        // bzw. unten sind.
        List<Integer> topIDs = new ArrayList<>();
        for (Component component : top)
            topIDs.add(components.indexOf(component));

        List<Integer> bottomIDs = new ArrayList<>();
        for (Component component : bottom)
            bottomIDs.add(components.indexOf(component));

        return new Ride(topIDs, bottomIDs);
    }

}
