package de.mariandietz.bundeswettbewerbinformatik2014.zahlenspiel;

/**
 * Diese Klasse repräsentiert eine Aufgabe, bei der ein Bruch gekürzt werden muss.
 * Demzufolge besteht sie aus genau zwei Brüchen.
 */
public class ReduceFractureExercise {

    /**
     * {@code normal} ist der ungekürzte Bruch, {@code reduced} der gekürzte.
     */
    private Fracture normal, reduced;

    /**
     * Erstellt eine neue Instanz dieser Klasse.
     *
     * @param normal  der ungekürzte Bruch
     * @param reduced der gekürzte Bruch
     */
    public ReduceFractureExercise(Fracture normal, Fracture reduced) {
        this.normal = normal;
        this.reduced = reduced;
    }

    /**
     * Stellt eine Instanz dieser Klasse als {@code String} dar.
     * Der {@code String} ist in der Form "ungekürzt = gekürzt".
     *
     * @return die in einen String umgewandelte Klasse
     */
    @Override
    public String toString() {
        return normal + " = " + reduced;
    }
}
