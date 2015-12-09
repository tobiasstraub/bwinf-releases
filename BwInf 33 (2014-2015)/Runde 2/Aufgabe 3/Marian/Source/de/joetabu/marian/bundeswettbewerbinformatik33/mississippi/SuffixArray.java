package de.joetabu.marian.bundeswettbewerbinformatik33.mississippi;

import java.util.Arrays;

/**
 * Stellt ein Suffixarray dar.
 */
public class SuffixArray {

    private Suffix[] suffixes;

    /**
     * Erstellt ein neues Suffixarray anhand der übergebenen Zeichenkette.
     */
    public SuffixArray(String genome) {

        int length = genome.length();
        this.suffixes = new Suffix[length];

        for (int i = 0; i < length; i++)
            suffixes[i] = new Suffix(genome, i);

        Arrays.sort(suffixes);
    }

    public int length() {
        return suffixes.length;
    }

    public Suffix get(int i) {
        return suffixes[i];
    }

    /**
     * Zählt die Anzahl der Suffixe, die mit dem übergebenen Präfix
     * anfangen. Dafür werden die Suffixe vor dem Suffix mit dem
     * übergebenen Index überprüft, bis ein Suffix auftaucht, wofür die
     * Bedingung nicht eintritt.
     */
    public int count(int i, String prefix) {

        // Es wird davon ausgegegangen, dass das Suffix i mit Präfix startet:
        int count = 1;

        // Die Überprüfung startet mit dem vorherigen Suffix (i--), endet
        // spätestens beim ersten Suffix, nach jedem Durchgang wird i um eins
        // runtergezählt, damit das vorherige Suffix genommen wird und der
        // Zähler wird inkrementiert.
        for (i--; i >= 0; i--, count++)
            if (!suffixes[i].genome().startsWith(prefix))
                return count;

        return count;
    }

}
