package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Hauptklasse der Aufgabe 'Seilschaften' der 2. Runde des 33.
 * Bundeswettbewerbs Informatik.
 *
 * @author Marian Dietz
 */
public class Seilschaften {

    public static void main(String[] args) {

        new Seilschaften().start();

    }

    /**
     * Startet das Programm.
     * Liest Daten ein, generiert alle mögliche Fahrten und anschließend
     * die kürzeste Lösung und gibt diese aus.
     */
    private void start() {
        System.out.println("\nANLEITUNG FÜR DIE EINGABE DER " +
                "KOMPONENTEN:\n\n" +
                "Komponenten müssen in der Form 'Art Gewicht " +
                "Anfangsposition Endposition' eingegeben werden.\n\n" +
                "Die Art ist entweder P für Person oder S für Stein.\n\n" +
                "Für die Positionen gibt es folgende Möglichkeiten:\n" +
                "  '^F' bzw. '_F' für Positionen oben bzw. unten außerhalb " +
                "des Korbes,\n" +
                "  '^K' bzw. '_K' für Positionen oben bzw. unten im Korb,\n" +
                "  '^' bzw. '_' für Positionen oben bzw. unten, der Korb " +
                "ist dabei egal, \n" +
                "  'F' bzw. 'K' für Positionen außerhalb des Korbes bzw. im" +
                " Korb, die Seite ist dabei egal, oder \n" +
                "  'E' für Positionen, die komplett egal sind.\n\n" +
                "Für Startpositionen sind nur '^F', '_F', '^K' und '_K' " +
                "möglich (spezifische Positionen).\n" +
                "Für Endpositionen gehen alle Positionen. Wenn sie " +
                "weggelassen wird, werden die Standardpositionen verwendet " +
                "(s. Aufgabenstellung).\n\n" +
                "Bei Startpositionen kann man '^F' bzw. '_F' auch durch '^'" +
                " bzw. '_' abkürzen.\n\n" +
                "'^' ist durch 'O' oder 'Oben' austauschbar, '_' durch 'U' " +
                "oder 'Unten',\n" +
                "'F' durch 'Frei', 'K' durch 'Korb' und 'E' durch 'Egal'" +
                ".\n\n" +
                "Beispiel, bei dem ein Stein 20 Einheiten wiegt, am Anfang " +
                "oben, nicht im Korb und am Ende unten, im Korb sein " +
                "soll:\n" +
                "S 20 ^ _Korb\n\n" +
                "===============================================================\n");

        Scanner scanner = new Scanner(System.in);
        int difference = requestDifference(scanner);
        ComponentList componentList = requestComponents(scanner);
        scanner.close();

        printInputs(difference, componentList);

        // Alle möglichen Fahrten generieren:
        PossibleRidesGenerator possibleRidesGenerator =
                new PossibleRidesGenerator(componentList, difference);
        List<Ride> possibleRides = possibleRidesGenerator.generate();

        // Kürzeste Lösung finden:
        SolutionFinder solutionFinder =
                new SolutionFinder(possibleRides, componentList);
        List<Ride> order = solutionFinder.execute();

        System.out.println
                ("\n===============================================================");

        // Lösung (wenn vorhanden) ausgeben.
        if (order == null) System.out.println("\nEs gibt keine Lösung.");
        else {
            System.out.println("\nEine kürzeste Lösung wurde gefunden.");
            System.out.println("Dafür werden " + order.size() + " Fahrten " +
                    "benötigt.\n");
            for (Ride ride : order)
                System.out.println(ride);
        }

    }

    /**
     * Fragt die Maximaldifferenz der beiden Körbe ab.
     */
    private int requestDifference(Scanner scanner) {
        System.out.println("Gewichtsschranke d eingeben:");

        // Immer wieder durchführen, bis es eine korrekte Eingabe gibt.
        while (true) {
            try {
                String numberString = scanner.nextLine();

                int number = Integer.parseInt(numberString);

                // Differenz muss mindestens 0 sein, da eine negative
                // Differenz sinnlos ist. Auch bei einer Differenz von 0
                // können Personen nicht fahren, das wird hier aber
                // akzeptiert.
                if (number >= 0)
                    return number;

            } catch (NumberFormatException ignore) {
                // Integer.parseInt() nutzt diesen Fehler, wenn die Eingabe
                // kein int ist
            }

            System.out.println("Eingabe konnte nicht als Ganzzahl (≥ 1) " +
                    "erkannt werden. Bitte erneut eingeben:");
        }
    }

    /**
     * Fragt die Komponenten ab.
     */
    private ComponentList requestComponents(Scanner scanner) {
        System.out.println("Komponenten eingeben (wenn fertig, nichts " +
                "eingeben und Enter drücken):");

        List<Component> components = new ArrayList<>();

        // Immer wieder durchführen, bis die Schleife abgebrochen wird
        // (wenn nichts eingegeben wird).
        while (true) {

            String line = scanner.nextLine();
            if (line.equals(""))
                break; // Wenn nichts eingegeben wurde, sind alle
            // Komponenten fertig und die Schleife wird beendet.

            String[] input = line.split(" "); // Die Eigenschaften werden
            // durch Leerzeichen getrennt.

            if (input.length < 3) {
                System.out.println("Komponente konnte nicht erkannt werden." +
                        " Bitte erneut eingeben:");
                continue;
            }

            boolean person; // true, wenn die Komponente eine Person ist,
            // false, wenn sie ein Stein ist.
            if (input[0].equalsIgnoreCase("P")) person = true;
            else if (input[0].equalsIgnoreCase("S")) person = false;
            else {
                System.out.println("Art konnte nicht als Person (P) oder " +
                        "Stein (S) erkannt werden. Bitte erneut eingeben:");
                continue;
            }

            int weight; // Gewicht der Komponente
            try {
                weight = Integer.parseInt(input[1]);
            } catch (NumberFormatException e) {
                System.out.println("Gewicht konnte nicht als int erkannt " +
                        "werden. Bitte erneut eingeben:");
                continue;
            }

            Position startPosition =
                    Position.startPositionFromString(input[2]);
            if (startPosition == null) {
                System.out.println("Startposition konnte nicht erkannt " +
                        "werden. Bitte erneut eingeben:");
                continue;
            }

            Position endPosition;
            if (input.length == 3) {
                // Endposition wurde nicht angegeben, also
                // Standard-Endpositionen benutzen.
                if (person) endPosition = Position.BOTTOM;
                else endPosition = Position.UNKNOWN;
            } else // Endposition wurde angegeben
                endPosition = Position.endPositionFromString(input[3]);

            if (endPosition == null) {
                System.out.println("Endposition konnte nicht erkannt werden" +
                        ". Bitte erneut eingeben:");
                continue;
            }

            // Eigenschaften fertig ausgelesen, Komponente kann der Liste
            // hinzugefügt werden.
            if (person)
                components.add(
                        new Person(weight, startPosition, endPosition));
            else
                components.add(new Stone(weight, startPosition, endPosition));
        }

        return new ComponentList(components);
    }

    private void printInputs(int difference, ComponentList componentList) {

        System.out.println("===============================================================");

        if (difference == -1)
            System.out.println("\nEs wird keine Maximaldifferenz verwendet" +
                    ".\n");
        else
            System.out.println("\nEs wird die Maximaldifferenz " +
                    difference + " verwendet.\n");

        System.out.println("Für folgende Komponenten wird eine Lösung " +
                "gesucht:");

        for (Component component : componentList.getComponents())
            System.out.println(componentList.getComponents().indexOf
                    (component) +
                    " - " + (component instanceof Person ? "Person" :
                    "Stein") +
                    " - Gewicht: " + component.getWeight() +
                    " - Startposition: " + component.startPosition() +
                    " - Endposition: " + component.endPosition());

        System.out.println("\n===============================================================");

        System.out.println("\nLösung suchen...");
    }

}
