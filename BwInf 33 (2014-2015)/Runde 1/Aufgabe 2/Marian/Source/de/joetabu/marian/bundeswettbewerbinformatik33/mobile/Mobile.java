package de.joetabu.marian.bundeswettbewerbinformatik33.mobile;

import java.util.Scanner;

/**
 * Hauptklasse der Aufgabe 'Mobile' des 33. Bundeswettbewerbs Informatik.
 *
 * @author Marian Dietz
 * @author Johannes Heinrich
 */
public class Mobile {

    /**
     * Start des Programmes.
     *
     * @param args Argumente (werden nicht verwendet)
     */
    public static void main(String[] args) {
        new Mobile().start();
    }

    /**
     * Startet das Programm.
     * Liest Daten ein, erstellt ein Mobile und gibt dieses aus.
     */
    public void start() {
        Figure[] figures = requestFigures();

        System.out.println(new Bar(figures));
    }

    /**
     * Fragt die Figuren ab, aus denen ein Mobile erstellt werden soll.
     *
     * @return die eingegebenen Figuren in einem {@code Array}
     */
    private Figure[] requestFigures() {
        System.out.println("Figuren durch Leerzeichen getrennt eingeben:");
        Scanner scanner = new Scanner(System.in);

        Figure[] containers;

        // do-while-Schleife wird solange wiederholt,
        // bis valide Figuren eingegeben wurden
        do {
            // Figuren werden durch ein Leerzeichen (' ') getrennt
            String[] stringContainers = scanner.nextLine().split(" ");
            containers = new Figure[stringContainers.length];

            // nacheinander alle Figuren in int's konvertieren
            for (int i = 0; i < containers.length; i++) {
                try {
                    containers[i] =
                            new Figure(Integer.parseInt(stringContainers[i]));
                } catch (NumberFormatException e) {
                    // wird aufgerufen, wenn Integer.parseInt() kein int erkennt
                    containers = null; // noch einmal versuchen...
                    System.out.println("Figuren konnten nicht erkannt werden. " +
                            "Bitte erneut eingeben:");
                    break;
                }
            }
        } while (containers == null);
        scanner.close();

        return containers;
    }

}