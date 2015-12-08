package de.joetabu.marian.bundeswettbewerbinformatik33.buffetlotterie;

/**
 * Generiert die beste Lösung fuer eine "Buffet-Lotterie".
 */
public class BuffetLotterieGenerator {

    /**
     * Die Anzahl der Silben im Abzählvers.
     */
    private final static int SYLLABLES = 16;

    /**
     * Die Anzahl der Teilnehmer beim Start.
     * Diese Variable wird nie geändert.
     */
    private int participants;

    /**
     * Erstellt eine neue Instanz dieser Klasse
     *
     * @param participants die Anzahl der Teilnehmer beim Start
     */
    public BuffetLotterieGenerator(int participants) {
        this.participants = participants;
    }

    /**
     * Generiert die Lösung.
     *
     * @return die letzte Zeile der Lösung
     */
    public Row generate() {
        Row row = null;

        // Wird fortgesetzt beim ersten Versuch (row == null) und wenn die letzte
        // Zeile noch keine Lösung enthält.
        while (row == null || !row.isSolution()) {

            int a = row == null ? participants : (row.getAlteAnzahl() - row.getRaus());
            // Alte Anzahl der Teilnehmer. In der ersten Zeile ist dies die
            // Gesamtzahl, ansonsten wird die letzte Anzahl genommen und davon
            // subtrahiert, wie viele Teilnehmer essen gehen.

            int e = e(row, a); // e: Anzahl der Teilnehmer, die rausgehen
            if (e == 0) // mindestens ein Teilnehmer muss rausgehen
                e = 1;

            int versatz = versatz(row, a, e); // kleinster Versatz
            int letzterVersatz = letzterVersatz(row, a, e); // größter Versatz

            int versatzLaenge = row == null ? 2 : row.getVersatzLaenge() + 1;
            // versatzLaenge ist beim ersten Durchgang zwei, danach steigt diese
            // immer um 1

            // Geburtstagskind wurde ein weiteres Mal "überrundet":
            while (versatz <= 0) {

                // Wenn auch der letzte Versatz das Geburtstagskind überrundet wurde:
                if (letzterVersatz <= 0) {
                    // Geburtstagskind kann sich 1 Mal mehr entscheiden:
                    versatzLaenge++;
                    // weitere Runde gestartet:
                    letzterVersatz += a;
                }

                versatz += a;
                // zum Versatz wird die Anzahl der Teilnehmer hinzugefügt, da eine
                // weitere Runde gestartet wird
            }

            row = new Row(a, versatz, versatzLaenge, e);
        }

        return row; // Zeile enthält Lösung, kann zurückgegeben werden
    }

    /**
     * Berechnet die Anzahl der Teilnehmer, die essen gehen.
     *
     * @param row die letzte Zeile
     * @param a   alte Anzahl der Teilnehmer
     * @return die Anzahl der Teilnehmer, die essen gehen
     */
    private int e(Row row, int a) {
        // Am Anfang sind noch alle Teilnehmer drin:
        if (row == null)
            return participants / SYLLABLES;

        // Ansonsten wird die neue Anzahl der Teilnehmer berechnet, wobei der
        // Versatz hnzugefügt wird, das Ergebnis wird durch die Silben geteilt:
        return (row.getVersatz() + a) / SYLLABLES;
    }

    /**
     * Berechnet den neuen Versatz.
     *
     * @param row die letzte Zeile
     * @param a   alte Anzahl der Teilnehmer
     * @param e   Anzahl der Teilnehmer, die essen gehen
     * @return den neuen Versatz
     */
    private int versatz(Row row, int a, int e) {
        return a - ((SYLLABLES * e) - (row == null ? 1 : row.getVersatz()));
        // Formel: v' = a - (16e - v)
        // Wenn die erste Zeile berechnet wird, ist v = 1
    }

    /**
     * Berechnet den letzten Versatz.
     *
     * @param row die letzte Zeile
     * @param a   alte Anzahl der Teilnehmer
     * @param e   Anzahl der Teilnehmer, die essen gehen
     * @return den letzten Versatz
     */
    private int letzterVersatz(Row row, int a, int e) {
        return a - ((SYLLABLES * e) - (row == null ? 1 :
                row.getVersatz() + row.getVersatzLaenge() - 1));
        // Formel: v' = a - (16e - v)
        // v ist dabei der letzte Versatz, nicht der kleinste
    }

}