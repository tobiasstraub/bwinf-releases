package de.mariandietz.bundeswettbewerbinformatik2014.zahlenspiel;

import java.util.Scanner;
import java.util.Set;

/**
 * Hauptklasse der Aufgabe 'Zahlenspiel' des Bundeswettbewerbs Informatik 2014.
 *
 * @author Marian Dietz
 * @author Johannes Heinrich
 */
public class Zahlenspiel {

    /**
     * Start des Programmes.
     *
     * @param args Argumente (werden nicht verwendet)
     */
    public static void main(String[] args) {
        new Zahlenspiel().start();
    }

    /**
     * Startet das Programm.
     * Liest Stufe und Anzahl der Brüche ein und lässt dazu Brüche generieren.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        Stage stage = requestStage(scanner);
        int number = requestNumber(scanner);
        scanner.close();

        generateFractures(stage, number);
    }

    /**
     * Liest den Schwierigkeitsgrad ein, der verwendet werden soll.
     * Mögliche Eingaben sind 'leicht' / '1', 'mittel' / '2' oder 'schwer' / '3'.
     *
     * @param scanner der Scanner, welcher verwendet werden soll
     * @return die eingelesene Stufe
     */
    private Stage requestStage(Scanner scanner) {
        System.out.println("Stufe (leicht / mittel / schwer) eingeben:");

        while (true) {
            String stage = scanner.next().trim();
            if (stage.equalsIgnoreCase("leicht") || stage.equalsIgnoreCase("1"))
                return new Stage((byte) 1);
            else if (stage.equalsIgnoreCase("mittel") || stage.equalsIgnoreCase
                    ("2"))
                return new Stage((byte) 2);
            else if (stage.equalsIgnoreCase("schwer") || stage.equalsIgnoreCase
                    ("3"))
                return new Stage((byte) 3);

            System.out.println("Stufe konnte nicht erkannt werden. Bitte erneut " +
                    "eingeben:");
        }
    }

    /**
     * Liest die Anzahl an Aufgaben ein, die generiert werden sollen.
     * Die Anzahl muss ≥ 1 sein.
     *
     * @param scanner der Scanner, welcher verwendet werden soll
     * @return die eingelesene Anzahl an Aufgaben
     */
    private int requestNumber(Scanner scanner) {
        System.out.println("Anzahl an Aufgaben (≥ 1) eingeben:");

        while (true) {
            try {
                String numberString = scanner.next();
                int number = Integer.parseInt(numberString);

                // Nur Zahlen ≥ 1 akzeptieren, ansonsten Schleife erneut durchlaufen:
                if (number >= 1)
                    return number;
            } catch (NumberFormatException ignore) {
                // Integer.parseInt() nutzt diesen Fehler,
                // wenn die Eingabe kein int ist
            }

            System.out.println("Anzahl konnte nicht als Ganzzahl (≥ 1) erkannt " +
                    "werden. Bitte erneut eingeben:");
        }
    }

    /**
     * Lässt Brüche generieren und gibt diese aus.
     * Hierbei werden der angegebene Schwierigkeitsgrad und die angegebene Anzahl an
     * Brüchen verwendet.
     *
     * @param stage  die Stufe, für die die Brüche generiert werden soll
     * @param number die Anzahl an Brüchen, die generiert werden sollen
     */
    private void generateFractures(Stage stage, int number) {
        Set<ReduceFractureExercise> exercises =
                new FractureGenerator(stage, number).generate();

        for (ReduceFractureExercise exercise : exercises)
            System.out.println(exercise);
    }

}
