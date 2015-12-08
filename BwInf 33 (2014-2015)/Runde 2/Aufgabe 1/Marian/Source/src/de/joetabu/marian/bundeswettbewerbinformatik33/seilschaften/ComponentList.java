package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

import java.util.ArrayList;
import java.util.List;

/**
 * Beinhaltet eine Liste mit allen Komponenten. Außerdem werden die Status
 * mit den Start- bzw. Endpositionen der Komponenten gespeichert.
 */
public class ComponentList {

    private List<Component> components;
    private List<Position> onStart;
    private List<Position> onEnd;

    /**
     * Erstellt eine neue Instanz von ComponentList. Dabei werden direkt
     * die Start- und Endpositionen der angegebenen Komponenten den
     * zugehörigen Listen onStart bzw. onEnd hinzugefügt. Die Indizes der
     * Listen onStart und onEnd sind dieselben wie bei der Liste components.
     * Eine Komponente, die bspw. in components den Index 3 hat, steht auch
     * in onStart und onEnd auf dem Index 3.
     *
     * @param components alle Komponenten
     */
    public ComponentList(List<Component> components) {
        this.components = components;

        onStart = new ArrayList<>();
        onEnd = new ArrayList<>();

        for (Component component : components) {
            onStart.add(component.startPosition());
            onEnd.add(component.endPosition());
        }
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<Position> onStart() {
        return onStart;
    }

    public List<Position> onEnd() {
        return onEnd;
    }

    /**
     * Überprüft, ob die Komponente des angegebenen Index eine Person ist.
     */
    public boolean isPerson(int index) {
        return components.get(index) instanceof Person;
    }

}
