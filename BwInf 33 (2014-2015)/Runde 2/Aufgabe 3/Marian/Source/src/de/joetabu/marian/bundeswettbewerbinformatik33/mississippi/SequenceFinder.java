package de.joetabu.marian.bundeswettbewerbinformatik33.mississippi;

import java.util.*;

/**
 * Sucht Teilzeichenketten einer Zeichenkette, die die Bedingungen k und l
 * einhalten.
 */
public class SequenceFinder {

    private SuffixArray array;
    private int k, l;

    public SequenceFinder(String genome, int k, int l) {
        this.array = new SuffixArray(genome);
        this.k = k;
        this.l = l;
    }

    /**
     * Sucht nach Teilzeichenketten, wofür zuersten alle Teilzeichenketten
     * berechnet werden. Danach werden diejenigen Teilzeichenketten
     * gelöscht, die nicht maximal sind.
     */
    public Set<String> find() {
        Map<String, Integer> sequences = sequences();
        removeUnnecessarySequences(sequences);
        return sequences.keySet();
    }

    /**
     * Berechnet alle Teilzeichenketten, die k und l einhalten.
     */
    private Map<String, Integer> sequences() {
        // Teilzeichenketten mit der Anzahl des Auftretens:
        Map<String, Integer> sequences = new HashMap<>();
        Set<String> checked = new HashSet<>(); // bereits überprüfte Präfixe

        int length = array.length();

        // Bei dem letzten Suffix beginnen und zu den vordersten Elementen
        // des Arrays alle durcharbeiten. Suffixe mit i < k - 1 müssen
        // nicht abgearbeitet werden, da sie nicht genug Suffixe vor sich
        // im Array haben können, um Präfixe zu haben, die oft genug
        // auftauchen.
        for (int i = length - 1; i >= k - 1; i--) {
            Suffix suffix = array.get(i);

            // Das Präfix des aktuellen Suffixes muss mindestens l Zeichen
            // lang sein und darf nicht länger als das eigentliche Suffix
            // sein.
            for (int prefixLength = l; prefixLength <= suffix.length();
                 prefixLength++) {

                // Präfix des Suffixes abfragen:
                String prefix = suffix.genome(prefixLength);

                // bereits bearbeitete Präfixe werden übersprungen:
                if (checked.contains(prefix))
                    continue;

                checked.add(prefix);

                // die Teilzeichenkette muss mindestens k-mal auftreten:
                int count = array.count(i, prefix);
                if (count < k) break;

                sequences.put(prefix, count);

            }
        }

        return sequences;
    }

    /**
     * Löscht alle nicht maximalen Zeichenketten aus der übergebenen Map.
     */
    private void removeUnnecessarySequences(Map<String, Integer> sequences) {

        // die zu löschenden Zeichenketten:
        Set<String> remove = new HashSet<>();

        for (Map.Entry<String, Integer> entry : sequences.entrySet()) {

            // Wenn diese Zeichenkette schon gelöscht werden soll, muss sie
            // nicht überprüft werden:
            if (!remove.contains(entry.getKey())) {
                String string = entry.getKey();

                // Alle Teilzeichenketten von string bilden:
                for (int i = 0; i < string.length(); i++) {
                    for (int j = i; j < string.length(); j++) {

                        // j+1, da der Endindex von substring exklusiv ist.
                        String sub = string.substring(i, j + 1);

                        // sub.length() < string.length(), damit die
                        // aktuelle Zeichenkette nicht mit sich selbst
                        // verglichen wird. Objects.equals(), da sequences
                        // .get() auch null zurückgibt, ist die
                        // Teilzeichenkette nicht enthalten (das kann nur
                        // sein, wenn die Teilzeichenkette kürzer als l ist
                        // und daher nicht in die Map gekommen ist).
                        if (sub.length() < string.length() && Objects
                                .equals(sequences.get(sub), entry.getValue()))
                            remove.add(sub);
                    }
                }
            }
        }

        // nicht maximale Zeichenketten rauslöschen:
        for (String string : remove)
            sequences.remove(string);
    }

}
