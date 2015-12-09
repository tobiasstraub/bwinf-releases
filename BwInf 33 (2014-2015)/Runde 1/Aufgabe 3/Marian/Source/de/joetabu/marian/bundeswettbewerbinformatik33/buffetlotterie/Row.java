package de.joetabu.marian.bundeswettbewerbinformatik33.buffetlotterie;

import java.util.ArrayList;
import java.util.List;

/**
 * Stellt eine Zeile der Lösung dar. Diese besteht aus der alten Anzahl der
 * Teilnehmer, dem Versatz mit der Länge des Versatzes und die Anzahl der
 * Teilnehmer, welche essen gehen können.
 */
public class Row {

    /**
     * alte Anzahl der Teilnehmer
     */
    private int alteAnzahl;
    /**
     * Versatz
     */
    private int versatz;
    /**
     * Länge des Versatzes
     */
    private int versatzLaenge;
    /**
     * Anzahl der Teilnehmer, die essen gehen können
     */
    private int raus;

    /**
     * Erstellt eine neue Instanz einer Zeile mit allen Daten dazu.
     *
     * @param alteAnzahl    alte Anzahl der Teilnehmer
     * @param versatz       Versatz
     * @param versatzLaenge Länge des Versatzes
     * @param raus          Anzahl der Teilnehmer, die essen gehen können.
     */
    public Row(int alteAnzahl, int versatz, int versatzLaenge, int raus) {
        this.alteAnzahl = alteAnzahl;
        this.versatz = versatz;
        this.versatzLaenge = versatzLaenge;
        this.raus = raus;
    }

    public int getAlteAnzahl() {
        return alteAnzahl;
    }

    public int getVersatz() {
        return versatz;
    }

    public int getVersatzLaenge() {
        return versatzLaenge;
    }

    public int getRaus() {
        return raus;
    }

    /**
     * Prüft, ob die Zeile eine Lösung darstellt. Dies ist nur der Fall, wenn
     * eine Zahl vom Versatz bis zum Versatz insklusive der Länge des Versatzes
     * 15, 16 oder der alten Anzahl entspricht.
     *
     * @return {@code true}, wenn diese Zeile eine Lösung ist, {@code false},
     * wenn nicht
     */
    public boolean isSolution() {
        for (int i = getVersatz(); i < getVersatz() + getVersatzLaenge(); i++)
            if (i == 15 || i == 16 || i == getAlteAnzahl())
                return true;
        return false;
    }

    /**
     * Gibt diese Lösung in die Konsole aus.
     */
    public void printSolution() {
        int syllables = 0; // die Anzahl, wie oft false in der Lösung auftauchen soll

        List<Boolean> solution = new ArrayList<Boolean>(); // die Lösung als
        // List, welche zum Schluss ausgegeben wird

        // Versatz wird durchlaufen. Wenn eine Lösung 15 oder 16 gefunden wurde,
        // muss noch true bzw. false angehangen werden, damit auch das
        // Geburtstagskind die letzte Silbe sagt. Wenn die Lösung der alten
        // Anzahl der Teilnehmer entspricht, darf kein Ende angehangen werden,
        // da die letzte Silbe bereits vom Geburtstagskind gesagt wird.
        for (int i = getVersatz(); i < getVersatz() + getVersatzLaenge(); i++) {
            if (i == getAlteAnzahl()) {
                syllables = getVersatz() + getVersatzLaenge() - getAlteAnzahl() - 1;
                break;
            } else if (i == 15) {
                solution.add(true);
                syllables = getVersatz() + getVersatzLaenge() - 16;
                break;
            } else if (i == 16) {
                solution.add(false);
                syllables = getVersatz() + getVersatzLaenge() - 17;
                break;
            }
        }

        // Silben werden der Liste angefügt. syllables gibt an, wie oft false
        // verwendet wird.
        for (int i = 1; i < getVersatzLaenge(); i++)
            if (syllables == 0)
                solution.add(true);
            else {
                syllables--;
                solution.add(false);
            }

        System.out.println(solution);
    }

    @Override
    public String toString() {
        return "Row{" +
                "alteAnzahl=" + alteAnzahl +
                ", versatz=" + versatz +
                ", versatzLaenge=" + versatzLaenge +
                ", raus=" + raus +
                '}';
    }
}
