package de.joetabu.marian.bundeswettbewerbinformatik33.buffetlotterie;

import java.util.Scanner;

/**
 * Hauptklasse der Aufgabe 'Buffet-Lotterie' des Bundeswettbewerbs Informatik 2014.
 *
 * @author Marian Dietz
 * @author Johannes Heinrich
 */
public class BuffetLotterie {

    /**
     * Start des Programmes.
     *
     * @param args Argumente (werden nicht verwendet)
     */
    public static void main(String[] args) {
        new BuffetLotterie().start();
    }

    /**
     * Startet das Programm.
     * Liest Daten ein, generiert die kürzeste Lösung und gibt diese aus.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        int participants = requestParticipants(scanner);
        scanner.close();

        new BuffetLotterieGenerator(participants).generate().printSolution();
    }

    /**
     * Fragt die Anzahl der Teilnehmer ab, mit denen die "Buffet-Lotterie" gestartet
     * werden soll.
     *
     * @return die eingegebene Anzahl der Teilnehmer
     */
    private int requestParticipants(Scanner scanner) {
        System.out.println("Bitte die Anzahl von Teilnehmern eingeben:");

        while (true) {
            try {
                String numberString = scanner.nextLine();
                int number = Integer.parseInt(numberString);

                if (number >= 1)
                    return number;
            } catch (NumberFormatException ignore) {
                // Integer.parseInt() nutzt diesen Fehler,
                // wenn die Eingabe kein int ist
            }

            System.out.println("Eingabe konnte nicht als Ganzzahl (≥ 1) erkannt " +
                    "werden. Bitte erneut eingeben:");
        }
    }

}
