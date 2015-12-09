package de.mariandietz.bundeswettbewerbinformatik2014.zahlenspiel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Diese Klasse repräsentiert einen Bruch, welcher aus x (Zähler) und y (Nenner)
 * besteht.
 */
public class Fracture {

    private int x, y;
    private List<Integer> usedMultiplications;

    /**
     * Erstellt eine neue Instanz dieser Klasse.
     *
     * @param x der Zähler
     * @param y der Nenner
     */
    public Fracture(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Diese Methode prüft, ob der Bruch aus den Zahlen x und y gekürzt werden
     * kann. Ein Bruch kann gekürzt werden, wenn beide Zahlen von mindestens einer Zahl
     * von 2 bis zu der kleineren Zahl teilbar ist. Die eins ist nicht in diesem
     * Bereich, da jede ganze Zahl durch eins teilbar ist.
     *
     * @return {@code true} wenn der Bruch gekürzt werden kann,
     * {@code false} wenn nicht
     */
    public boolean canBeShortened() {
        int min = Math.min(x, y);

        // Bereich: 2 bis zur kleineren der beiden Zahlen (Math.min())
        for (int i = 2; i <= min; i++) {
            if (x % i == 0 && y % i == 0) // beide müssen durch i teilbar sein
                return true;
        }

        return false;
    }

    /**
     * Diese Methode erstellt einen neuen Bruch, welcher eine ungegürzte 'Variante'
     * dieses Bruches ist. Hierbei wird immer, wenn ein Bruch generiert wurde, die
     * Zahl, mit der multipliziert wurde, in die Liste {@link #usedMultiplications}
     * eingefügt. Alle Zahlen dieser Liste können dann künftig nicht mehr in dieser
     * Methode verwendet werden, um neue Brüche generieren zu lassen. Sollte es
     * demnach keine Zahl mehr geben, mit der multipliziert werden kann, kommt eine
     * NoFracturesFoundException zum Einsatz.
     *
     * @param length die 'Länge' des neuen Bruches
     * @param random eine Instanz der Klasse {@link java.util.Random},
     *               welche für zufällige Brüche verwendet werden soll.
     * @return der neue, ungekürzte Bruch
     * @throws NoFracturesFoundException wenn kein ungekürzter Bruch generiert
     *                                   werden konnte.
     */
    public Fracture createBiggerFracture(int length, Random random) throws
            NoFracturesFoundException {

        if (usedMultiplications == null) // lazy initialization
            usedMultiplications = new ArrayList<Integer>();

        // Mögliche Zahlen, mit denen der ungekürzte Bruch multipliziert werden kann:
        List<Integer> possibleMultiplications = new ArrayList<Integer>();

        for (int i = 2; true; i++) { /* muss mindestens 2 sein, da '1 * x = x',
                                        und damit nicht sinnvoll ist */

            // Wenn bereits genutzt, darf die Zahl nicht erneut verwendet werden:
            if (usedMultiplications.contains(i))
                continue;

            // Länge des ungekürzten Bruches, also nach der  Multiplizierung mit i:
            int realLength = Integer.toString(x * i).length() +
                    Integer.toString(y * i).length();

            if (realLength == length) // muss mit der angegebenen Länge übereinstimmen
                possibleMultiplications.add(i);

            // wenn realLength größer als length ist, kann diese nur noch steigen,
            // da i immer größer wird; demnach kann es also keine Zahl mehr geben,
            // mit der noch multipliziert werden kann
            else if (realLength > length)
                break;
        }

        // keine geeignete Zahl zum Multiplizieren gefunden:
        if (possibleMultiplications.size() == 0)
            throw new NoFracturesFoundException();
        else {
            int multiplication = possibleMultiplications
                    .get(random.nextInt(possibleMultiplications.size()));

            // Wird nun verwendet; kann demnach nicht mehr genutzt werden:
            usedMultiplications.add(multiplication);

            return new Fracture(x * multiplication, y * multiplication);
        }
    }

    /**
     * Stellt eine Instanz dieser Klasse als {@code String} dar.
     * Der {@link java.lang.String} ist in der Form 'x/y'.
     *
     * @return die in einen {@link java.lang.String} umgewandelte Klasse.
     */
    public String toString() {
        return x + "/" + y;
    }

}
