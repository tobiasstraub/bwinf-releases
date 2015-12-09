package de.joetabu.marian.bundeswettbewerbinformatik33.mobile;

/**
 * Stellt eine Komponente dar.
 * Dies kann ein Balken oder eine Figur sein.
 * Eine Komponente besitzt lediglich ein Gewicht.
 */
public interface Component {

    /**
     * Gibt zurück, wie schwer die Komponente ist.
     *
     * @return wie schwer die Komponente ist
     */
    public int getWeight();

}
