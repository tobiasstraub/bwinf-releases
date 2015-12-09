package de.joetabu.marian.bundeswettbewerbinformatik33.mobile;

/**
 * Stellt eine Figur eines Mobiles dar. Diese hat lediglich ein Gewicht.
 */
public class Figure implements Component {

    /**
     * Das Gewicht der Figur.
     */
    private int weight;

    /**
     * Erstellt eine neue Instanz einer Figur mit dem angegebenen Gewicht.
     *
     * @param weight das Gewicht der Figur.
     */
    public Figure(int weight) {
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return Integer.toString(weight);
    }

}
