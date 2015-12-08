package de.joetabu.marian.bundeswettbewerbinformatik33.mobile;

import java.util.*;

/**
 * Stellt einen Balken eines Mobiles dar.
 * Ein Balken hat 2 bis 4 Komponente, welche an ihm hängen.
 */
public class Bar implements Component {

    /**
     * Die Komponente, welche an diesem Balken hängen.
     * Schlüssel: die Position der Komponente, positiv oder negativ
     * Wert: Die Komponenten, welche an der Position (Schlüssel) hängt
     */
    private Map<Integer, Component> components;

    /**
     * Erstellt eine neue Instanz eines Balkens. Dafür wird aus den gegebenen
     * Figuren ein ausbalancierter Balken erstellt.
     *
     * @param figures die Figuren, welche an diesem Balken hängen sollen
     * @throws java.lang.IllegalArgumentException wenn weniger als 2 Komponenten
     *                                            übergeben wurden
     */
    public Bar(Figure[] figures) {
        if (figures.length < 2)
            throw new IllegalArgumentException("length of figures must be at " +
                    "least 2");
        components = generateBar(figures);
    }

    /**
     * Generiert einen balancierten Balken anhand der angegebenen Figuren.
     *
     * @param figures die Figuren, welche am Balken hängen sollen
     * @return eine {@link java.util.Map} mit den Positionen als Schlüssel und
     * den Komponenten als Werte
     */
    private Map<Integer, Component> generateBar(Figure[] figures) {
        // Unbalancierte Komponente generieren. Wenn die Anzahl der Figuren
        // kleiner oder gleich 4 ist, koennen diese alle am Balken hängen.
        // Ansonsten koennen nur 3 Figuren am Balken haengen und die restlichen
        // werden an einen anderen Balken gehängt, welcher dann die vierte
        // Komponente ist.
        List<Component> unbalanced = generateComponents(figures,
                figures.length <= 4 ? 4 : 3);

        return balance(unbalanced);
    }

    /**
     * Generiert eine unbalancierte Liste mit allen Komponenten, welche am Balken
     * hängen sollen.
     *
     * @param figures    die dazu zu verwendenen Figuren
     * @param maxFigures die maximale Anzahl von Figuren, die am obersten Balken
     *                   hängen (sollten weitere Figuren übrig bleiben, werden
     *                   diese an einen weiteren Balken gehängt)
     * @return eine unbalancierte Liste mit den am Balken hängenden Komponenten
     */
    private List<Component> generateComponents(Figure[] figures, int maxFigures) {
        if (figures.length <= maxFigures) // alle koennen an EINEN Balken
            return Arrays.asList((Component[]) figures);

        else { // ein weiterer Balken muss erstellt werden

            // components: fertige Komponente
            List<Component> components = new ArrayList<Component>();

            // newMobileFigures: Figuren, die an einen anderen Balken müssen
            Figure[] newMobileFigures = new Figure[figures.length - maxFigures];

            for (int i = 0; i < figures.length; i++)
                if (i < maxFigures) // kann einfach an den Balken
                    components.add(figures[i]);
                else // muss an den anderen Balken
                    newMobileFigures[i - maxFigures] = figures[i];

            // Den fertigen Komponenten wird der neue Balken mit allen
            // restlichen Figuren hinzugefuegt.
            components.add(new Bar(newMobileFigures));

            return components;
        }
    }

    /**
     * Balanciert die Liste mit unbalancierten Komponenten aus.
     * Dabei wird unterschieden, ob es zwei, drei oder vier auszubalancierende
     * Komponenten gibt.
     *
     * @param unbalanced die unbalancierten Komponenten
     * @return die balancierten Komponenten
     */
    private Map<Integer, Component> balance(List<Component> unbalanced) {
        Map<Integer, Component> balanced = new HashMap<Integer, Component>();

        switch (unbalanced.size()) {
            case 2:
                // lediglich beide Komponente zusammen ausbalancieren
                balance(balanced, unbalanced.get(0), unbalanced.get(1));
                break;
            case 3:
                // alle drei Komponente zusammen ausbalancieren
                balance(balanced, unbalanced.get(0), unbalanced.get(1),
                        unbalanced.get(2));
                break;
            case 4:
                // erst die beiden ersten Komponenten ausbalancieren,
                // danach die beiden anderen
                balance(balanced, unbalanced.get(0), unbalanced.get(1));
                balance(balanced, unbalanced.get(2), unbalanced.get(3));
                break;
        }

        // wenn möglich, werden alle Positionen durch den ggT geteilt, um das
        // Mobile kleiner zu machen
        int ggT = ggT(balanced);
        Map<Integer, Component> divided = new HashMap<Integer, Component>();
        for (Map.Entry<Integer, Component> entry : balanced.entrySet())
            divided.put(entry.getKey() / ggT, entry.getValue());

        return divided;
    }

    /**
     * Balanciert zwei Komponente aus und steckt diese in die Map balanced.
     *
     * @param balanced   die {@code Map}, in welche die balancierten Komponenten kommen
     * @param component1 die erste Komponente
     * @param component2 die zweite Komponente
     */
    private void balance(Map<Integer, Component> balanced, Component component1,
                         Component component2) {

        // Bei 4 Komponenten wird diese Methode 2 Mal aufgerufen. Daher können beim
        // zweiten Aufruf Positionen bereits belegt sein. Aus diesem Grund wird
        // hier zunächst ein Faktor generiert, mit dem die Positionen des zweiten
        // Aufrufs multipliziert werden, ohne die Positionen des ersten Aufrufs
        // erneut zu belegen. Dieser Faktor kann höchstens 3 sein, da nur zwei
        // Positionen vorher besetzt wurden. Im Normalfall ist er aber nicht
        // notwendig ist und daher gleich 1.
        int multiplication = 1;
        while (balanced.containsKey(component1.getWeight() * multiplication) ||
                balanced.containsKey(-component2.getWeight() * multiplication))
            multiplication++;

        // Komponente 2 kommt an die Stelle von Gewicht von Komponente 2
        balanced.put(component1.getWeight() * multiplication, component2);

        // Komponente 1 kommt an die Stelle von -Gewicht von Komponente 1
        balanced.put(-component2.getWeight() * multiplication, component1);
    }

    /**
     * Balanciert drei Komponenten aus und steckt diese in die Map balanced.
     * Hier ist es nicht noetig, einen Faktor zu generieren,
     * mit dem die Positionen multipliziert werden, da diese Methode nur ein
     * Mal aufgerufen werden kann (s. {@link #balance(java.util.List)})
     *
     * @param balanced   die {@code Map}, in welche die balancierten Komponente kommen
     * @param component1 die erste Komponente
     * @param component2 die zweite Komponente
     * @param component3 die dritte Komponente
     */
    private void balance(Map<Integer, Component> balanced, Component component1,
                         Component component2, Component component3) {

        // Komponente 1 kommt an die Stelle von Gewicht von Komponente 3
        balanced.put(component3.getWeight(), component1);

        // Komponente 2 kommt an die Stelle von Gewicht von Komponente 3 mal 2
        balanced.put(component3.getWeight() * 2, component2);

        // Komponente 3 kommt an die Stelle auf der anderen Seite des Balkens,
        // um das Gewicht auszugleichen
        balanced.put(-component1.getWeight() - component2.getWeight() * 2,
                component3);
    }

    /**
     * Berechnet die größte Zahl (ggT), durch die alle Positionen dividiert
     * werden können, sodass immer noch ein valides Mobile besteht.
     *
     * @param balanced die ausbalancierten Komponenten
     * @return der ggT, durch den alle Positionen geteilt werden können
     */
    private int ggT(Map<Integer, Component> balanced) {
        Integer[] positions = balanced.keySet().toArray(new Integer[balanced.size()]);

        // ggT von allen Zahlen in positions berechnen
        int ggT = ggT(positions[0], positions[1]);
        if (positions.length > 2)
            ggT = ggT(ggT, positions[2]);
        if (positions.length > 3)
            ggT = ggT(ggT, positions[3]);

        return ggT;
    }

    /**
     * Berechnet den ggT der zwei gegeben Zahlen rekursiv anhand des "euklidischen
     * Algorithmus'".
     *
     * @param a Zahl 1
     * @param b Zahl 2
     * @return den ggT der zwei gegebenen Zahlen
     */
    private int ggT(int a, int b) {
        if (b == 0)
            return a;
        else
            return ggT(b, a % b);
    }

    /**
     * {@inheritDoc}
     * Dabei werden alle Gewichte der Komponenten zusammengezählt und
     * zurückgegeben.
     */
    @Override
    public int getWeight() {
        int weight = 0;

        for (Component component : components.values())
            weight += component.getWeight();

        return weight;
    }

    @Override
    public String toString() {
        return components.toString();
    }

}