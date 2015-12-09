package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

/**
 * Repräsentiert eine Komponente.
 * Eine Komponente ist entweder eine Person oder ein Stein.
 */
public abstract class Component {

    private int weight;
    private Position startPosition;
    private Position endPosition;

    /**
     * @param weight        das Gewicht der Komponente
     * @param startPosition die Startposition
     * @param endPosition   die Endposition
     */
    public Component(int weight, Position startPosition, Position
            endPosition) {
        this.weight = weight;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public int getWeight() {
        return weight;
    }

    public Position startPosition() {
        return startPosition;
    }

    public Position endPosition() {
        return endPosition;
    }

}
