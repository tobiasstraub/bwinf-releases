package de.mariandietz.bundeswettbewerbinformatik2014.zahlenspiel;

/**
 * Diese Klasse repräsentiert einen Schwierigkeitsgrad des Zahlenspiels.
 */
public class Stage {

    private int forEnd,
            minSum, maxSum,
            fractureLength;

    /**
     * Ersellt eine neue Instanz dieser Klasse.
     * Der Schwierigkeitsgrad kann eine 1 (leicht), 2 (mittel) oder 3 (schwer) sein.
     *
     * @param stage der Schwierigkeitsgrad
     * @throws java.lang.IllegalArgumentException wenn {@code stage} nicht 1,
     * 2 oder 3 beträgt
     */
    public Stage(byte stage) {
        switch (stage) {
            case 1:
                forEnd = 9;
                minSum = 0;
                maxSum = 10;
                fractureLength = 4;
                break;
            case 2:
                forEnd = 19;
                minSum = 11;
                maxSum = 20;
                fractureLength = 5;
                break;
            case 3:
                forEnd = 29;
                minSum = 21;
                maxSum = 30;
                fractureLength = 5;
                break;
            default:
                throw new IllegalArgumentException("stage must be 1, 2 or 3");
        }
    }

    /**
     * Gibt zurück, bei welcher Zahl eine Schleife stoppen soll, welche Brüche
     * generiert und die Zahl dazu verwendet, den Nenner oder den Zähler zu
     * bestimmen. Diese Zahl stellt also die höchste annehmbare Zahl für den Nenner
     * oder den Zähler dar, damit alle Bedingungen der Stufe eingehalten werden können.
     * <p/>
     * Leicht: 9
     * Mittel: 19
     * Schwer: 29
     *
     * @return die höchste Zahl für den Nenner oder den Zähler
     */
    public int getForEnd() {
        return forEnd;
    }

    /**
     * Gibt zurück, wie groß die Summe von p (Zähler) und q (Nenner) des gekürzten
     * Bruches minimal lauten muss, damit alle Bedingungen der Stufe eingehalten
     * werden.
     * <p/>
     * Leicht: 0
     * Mittel: 11
     * Schwer: 21
     *
     * @return die minimale Summe des gekürzten Bruches
     */
    public int getMinSum() {
        return minSum;
    }

    /**
     * Gibt zurück, wie groß die Summe von p (Zähler) und q (Nenner) des gekürzten
     * Bruches maximal lauten darf, damit alle Bedingungen der Stufe eingehalten
     * werden.
     * <p/>
     * Leicht: 10
     * Mittel: 20
     * Schwer: 30
     *
     * @return die maximale Summe des gekürzten Bruches
     */
    public int getMaxSum() {
        return maxSum;
    }

    /**
     * Gibt die 'Länge' des ungekürzten Bruches zurück, die für diese Stufe
     * verwendet werden muss.
     * <p/>
     * Leicht: 4
     * Mittel: 5
     * Schwer: 5
     *
     * @return die 'Länge' des ungekürzten Bruches
     */
    public int getFractureLength() {
        return fractureLength;
    }

}
