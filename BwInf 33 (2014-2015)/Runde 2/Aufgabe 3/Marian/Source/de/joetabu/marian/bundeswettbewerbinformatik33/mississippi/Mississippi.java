package de.joetabu.marian.bundeswettbewerbinformatik33.mississippi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

/**
 * Hauptklasse der Aufgabe 'Seilschaften' der 2. Runde des 33.
 * Bundeswettbewerbs Informatik.
 *
 * @author Marian Dietz
 */
public class Mississippi {

    public static void main(String[] args) {

        new Mississippi().start();

    }

    /**
     * Startet das Programm. Liest Daten ein, lässt die Teilzeichenketten
     * berechnen und gibt diese aus.
     */
    private void start() {
        Scanner scanner = new Scanner(System.in);
        String genome = requestGenome(scanner);
        int k = requestK(scanner);
        int l = requestL(scanner);
        scanner.close();

        System.out.println("Sequenzen werden gesucht...");

        Set<String> sequences = new SequenceFinder(genome, k, l).find();
        printSequences(sequences);

        System.out.println("");
    }

    /**
     * Fragt die Zeichenkette ab, von der die Teilzeichenketten berechnet
     * werden sollen.
     */
    private String requestGenome(Scanner scanner) {
        System.out.println("Zeichenkette oder Pfad zu einer Datei mit einer" +
                " Zeichenkette eingeben:");
        while (true) {
            String string = scanner.nextLine().trim();

            try {
                BufferedReader reader =
                        new BufferedReader(new FileReader(string));

                string = ""; // Der eingegebene Pfad wird rausgelöscht.

                String line;
                while((line = reader.readLine()) != null)
                    string += line; // Jede Zeile wird hinzugefügt

                reader.close();

            } catch (IOException e) {
                // Fehler beim Lesen der Datei. Dies passiert bspw., wenn
                // die eingegebene Datei nicht existiert. Daher wird einfach
                // versucht, den eingegebenen String als Genom zu verwenden.
            }

            // Es dürfen nur die vier Buchstaben A, C, G und T in der
            // Zeichenketten vorkommen.
            if (string.matches("[ACGT]+"))
                return string;

            System.out.println("Zeichenkette konnte nicht als Genom erkannt" +
                    " werden. Bitte erneut eingeben:");
        }
    }

    /**
     * Fragt k, die Mindestanzahl der Teilzeichenketten ab.
     */
    private int requestK(Scanner scanner) {
        System.out.println("Wie oft sollen die Teilzeichenketten mindestens" +
                " auftreten (k)?");
        while (true) {
            try {
                String numberString = scanner.nextLine();

                int number = Integer.parseInt(numberString);
                if (number >= 2)
                    return number;

            } catch (NumberFormatException ignore) {
                // Integer.parseInt() nutzt diesen Fehler, wenn die Eingabe
                // kein int ist
            }

            System.out.println("k konnte nicht als Ganzzahl (≥ 2) erkannt " +
                    "werden. Bitte erneut eingeben:");
        }
    }

    /**
     * Fragt l, die Mindestlänge der Teilzeichenketten ab.
     */
    private int requestL(Scanner scanner) {
        System.out.println("Wie lang sollen die Teilzeichenketten " +
                "mindestens sein (l)?");
        while (true) {
            try {
                String numberString = scanner.nextLine();

                int number = Integer.parseInt(numberString);
                if (number >= 1)
                    return number;
            } catch (NumberFormatException ignore) {
                // Integer.parseInt() nutzt diesen Fehler, wenn die Eingabe
                // kein int ist
            }

            System.out.println("l konnte nicht als Ganzzahl (≥ 1) erkannt " +
                    "werden. Bitte erneut eingeben:");
        }
    }

    /**
     * Gibt die übergebenen Teilzeichenketten aus.
     */
    private void printSequences(Set<String> sequences) {

        if (sequences.size() == 0) {
            System.out.println("\nEs gibt keine validen Teilzeichenketten.");
            return;
        }

        System.out.println("\nEs gibt " + sequences.size() + " valide " +
                "Teilzeichenkette(n):");

        boolean first = true; // Vor der ersten ausgegebenen
        // Teilzeichenkette darf kein Komma stehen, dazu 'first'.
        for (String sequence : sequences) {
            if (first) {
                System.out.print(sequence);
                first = false;
            } else
                System.out.print(", " + sequence);
        }
    }

}
