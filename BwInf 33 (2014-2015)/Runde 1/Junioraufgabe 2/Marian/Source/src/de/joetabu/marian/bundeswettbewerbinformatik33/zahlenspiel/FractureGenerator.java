package de.mariandietz.bundeswettbewerbinformatik2014.zahlenspiel;

import java.util.*;

/**
 * Diese Klasse wird verwendet, um eine Auswahl an ReduceFractureExercises zu
 * generieren.
 */
public class FractureGenerator {

    private Stage stage;
    private int number;

    /**
     * Erstellt eine neue Instanz dieser Klasse.
     *
     * @param stage  die Stufe, für die die Brüche generiert werden soll
     * @param number die Anzahl an Brüchen, die generiert werden sollen
     */
    public FractureGenerator(Stage stage, int number) {
        this.stage = stage;
        this.number = number;
    }

    /**
     * Generiert ein Set mit ReduceFractureExercises. Dazu werden der
     * Schwierigkeitsgrad und die Anzahl an Brüchen verwendet.
     *
     * @return die generierten Aufgaben
     */
    public Set<ReduceFractureExercise> generate() {
        List<Fracture> possibleFractures = generatePossibleReducedFractures();
        return generateExercises(possibleFractures);
    }

    /**
     * Generiert alle möglichen gekürzten Brüche für den zuvor definierten
     * Schwierigkeitsgrad.
     *
     * @return eine Liste mit allen möglichen passenden Brüchen
     */
    private List<Fracture> generatePossibleReducedFractures() {
        List<Fracture> fractures = new ArrayList<Fracture>();

        for (int x = 1; x < stage.getForEnd(); x++) {
            for (int y = 1; y < stage.getForEnd(); y++) {
                // Bedingung a ≠ b und Einschränkung der Summe von p/q:
                if (x != y && x + y >= stage.getMinSum()
                        && x + y <= stage.getMaxSum()) {

                    Fracture fracture = new Fracture(x, y);

                    // p/q muss immer voll gekürzt sein:
                    if (!fracture.canBeShortened())
                        fractures.add(fracture);
                }
            }
        }

        return fractures;
    }

    /**
     * Generiert ein Set mit ReduceFractureExercises. Dazu wird die angegebene Liste
     * mit den bereits gekürzten Brüchen verwendet.
     *
     * @param possibleFractures die möglichen gekürzten Brüche
     * @return die generierten Aufgaben
     */
    private Set<ReduceFractureExercise> generateExercises(List<Fracture>
                                                                  possibleFractures) {
        //bereits genutzte Brüche:
        List<Fracture> usedFractures = new ArrayList<Fracture>();

        Random random = new Random();
        Set<ReduceFractureExercise> exercises = new
                HashSet<ReduceFractureExercise>();

        for (int i = 0; i < number; i++) {
            ReduceFractureExercise exercise =
                    generateExercise(possibleFractures, usedFractures, random);

            if (exercise == null) // alle möglichen Aufgaben wurden schon generiert
                break;
            else
                exercises.add(exercise);
        }

        return exercises;
    }

    /**
     * Generiert eine ReduceFractureExercise, wobei die angegebenen möglichen
     * gekürzten Brüche und die angegebenen bereits zum Generieren benutzten Brüche,
     * welche verwendet werden, sollten nicht mehr genug noch nicht verwendete
     * Brüche existieren, verwendet werden.
     *
     * @param possibleFractures die möglichen gekürzten Brüche
     * @param usedFractures     die bereits genutzten Brüche
     * @param random            die Instanz von Random, welches für zufällige
     *                          Operationen verwendet werden soll
     * @return eine Aufgabe zum Kürzen von Brüchen
     */
    private ReduceFractureExercise generateExercise(
            List<Fracture> possibleFractures, List<Fracture> usedFractures,
            Random random) {

        // Der ungekürzte Bruch:
        Fracture fracture;
        // Der größere Bruch, der auf den oben genannten gekürzt werden soll:
        Fracture biggerFracture = null;

        do {
            // Es gibt gekürzte Brüche, welche noch nicht verwendet wurden:
            if (possibleFractures.size() > 0) {
                fracture = possibleFractures
                        .get(random.nextInt(possibleFractures.size()));

                // Wird jetzt verwendet und deshalb aus der Liste mit den möglichen
                // Brüchen entfernt und kommt zu den genutzten Brüchen hinzu:
                possibleFractures.remove(fracture);
                usedFractures.add(fracture);
            }

            // Alternativ weitere Brüche generieren, deren gekürzte Brüche schon
            // verwendet wurden:
            else if (usedFractures.size() > 0)
                fracture = usedFractures.get(random.nextInt(usedFractures.size()));
            else
                return null; // beide Listen sind leer, keine Brüche mehr möglich

            try {
                biggerFracture = fracture.createBiggerFracture(
                        stage.getFractureLength(), random);
            } catch (NoFracturesFoundException exception) {
                // Der gekürzte Bruch hat keinen weiteren größeren Bruch, der alle
                // Bedingungen einhält. Er wird also aus allen Listen entfernt:
                possibleFractures.remove(fracture);
                usedFractures.remove(fracture);
            }
        }
        // Sollte noch keiner generiert worden sein können, wird es erneut ausgeführt:
        while (biggerFracture == null);

        return new ReduceFractureExercise(biggerFracture, fracture);
    }

}
