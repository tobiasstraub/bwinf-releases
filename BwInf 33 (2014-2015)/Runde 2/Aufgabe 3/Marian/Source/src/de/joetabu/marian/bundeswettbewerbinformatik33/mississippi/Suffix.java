package de.joetabu.marian.bundeswettbewerbinformatik33.mississippi;

/**
 * Repräsentiert ein Suffix einer Zeichenkette. Es enthält die gesamte
 * Zeichenkette und den Startindex.
 */
public class Suffix implements Comparable<Suffix> {

    private String genome;
    private int index;

    /**
     * @param genome das gesamte Genom.
     * @param index der Startindex
     */
    public Suffix(String genome, int index) {
        this.genome = genome;
        this.index = index;
    }

    @Override
    public int compareTo(Suffix o) {
        if (this == o) return 0;

        // Die chars können nur bis zum letzten Zeichen des kürzeren
        // Suffixes verglichen werden:
        int length = Math.min(length(), o.length());

        for (int i = 0; i < length; i++) {
            if (this.charAt(i) > o.charAt(i))
                return +1;
            if (this.charAt(i) < o.charAt(i))
                return -1;
        }

        // Sind alle chars gleich, erscheint das kürzere Suffix weiter vorne.
        return this.length() - o.length();
    }

    public int length() {
        return genome.length() - index;
    }

    private char charAt(int charIndex) {
        return genome.charAt(index + charIndex);
    }

    public String genome() {
        return genome.substring(index);
    }

    public String genome(int end) {
        return genome.substring(index, index + end);
    }

}
