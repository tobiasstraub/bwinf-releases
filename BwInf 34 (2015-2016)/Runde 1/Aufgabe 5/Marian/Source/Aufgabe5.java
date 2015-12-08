import java.util.*;

public class AI {
    
    // Maximaler Unterschied zwischen der aktuellen und der anderen Situation, mit der verglichen wird:
    private static final int MAX_DIFFERENCE_OLD_CURRENT = 1;
    // Maximaler Unterschied zwischen der Vorhersage der letzten ähnlichen Situationen:
    private static final int MAX_DIFFERENCE_LAST_MOVES = 2;
    // Die Anzahl der letzten ähnlichen Situationen, deren Vorhersagen verglichen werden sollen:
    private static final int LAST_MOVES_TO_COMPARE = 4;
    // Die Anzahl der letzten Züge, die für den Zufall verwendet werden sollen:
    private static final int LAST_MOVES_FOR_RANDOM = 6;
    // Der maximale Bereich der Zahlen, die beim Zufall gesetzt werden können:
    private static final int MAX_RANDOM = 20;
    
    // Die eigenen Züge bzw. die des Gegners:
    private List<Integer> ownMoves = new ArrayList<Integer>();
    private List<Integer> opponentMoves = new ArrayList<Integer>();
    
    /**
     * Führt einen Zug aus.
     */
    public void zug(int id, Spiel.Zustand zustand, Spiel.Zug zug) {
        Spiel.Zustand.Spieler me = me(id, zustand);
        Spiel.Zustand.Spieler opponent = opponent(id, zustand);
        
        // Die letzten Züge (wenn vorhanden) hinzufügen:
        if (me.letzterZug() != 0) {
            ownMoves.add(me.letzterZug());
            opponentMoves.add(opponent.letzterZug());
        }

        makeMove(zug);
    }
    
    /**
     * @return das eigene Spieler-Objekt
     */
    private Spiel.Zustand.Spieler me(int id, Spiel.Zustand zustand) {
        if (zustand.listeSpieler().get(0).id() == id)
            return zustand.listeSpieler().get(0);
        return zustand.listeSpieler().get(1);
    }
    
    /**
     * @return das gegnerische Spieler-Objekt
     */
    private Spiel.Zustand.Spieler opponent(int id, Spiel.Zustand zustand) {
        if (zustand.listeSpieler().get(0).id() == id)
            return zustand.listeSpieler().get(1);
        return zustand.listeSpieler().get(0);
    }
    
    /**
     * Eine Situation mit der Ähnlichkeit zur aktuellen Situation.
     */
    public class Situation {

        int length;     // Länge der Situation
        int difference; // Unterschied zur aktuellen Situation
        int time;       // Gibt an, beim wievielten Zug die Situation stattfand
        int reaction;   // Gibt die danach gesetzte Zahl des Gegners an

        public Situation(int length, int difference, int time, int reaction) {
            this.length = length;
            this.difference = difference;
            this.time = time;
            this.reaction = reaction;
        }

    }

    /**
     * Sortiert Situationen nach deren Wichtigkeit. Die längste Situation
     * befindet sich ganz vorne. Bei gleicher Länge unterscheidet der
     * Unterschied zur aktullen Situation. Bei gleichem Unterschied wird die
     * neueste Situation bevorzugt.
     */
    private Comparator<Situation> situationComparator = new Comparator<Situation>() {
        public int compare(Situation left, Situation right) {
            if (left.length != right.length) {
                return right.length - left.length;
            } if (left.difference != right.difference) {
                return left.difference - right.difference;
            } else {
                return right.time - left.time;
            }
        }
    };

    /**
     * Setzt den Zug.
     */
    private void makeMove(Spiel.Zug zug) {
        Integer move = bestNumber();
        
        if (move != null) {
            // Zug konnte berechnet werden
            zug.setzen(move);
            return;
        }
        
        // Zufällig setzen
        zug.setzen(random(zug));
    }
    
    /**
     * Berechnet die beste zu setzende Zahl aus den zu der aktuellen Situation
     * möglichst ähnlichen Situationen.
     */
    private Integer bestNumber() {
        // Ähnliche Situationen zu der aktuellen:
        List<Situation> situations = similarSituations();
        
        // Die niedrigste bzw. höchste Reaktion, die der Gegner machen wird:
        Integer min = null, max = null;
        
        // Situationen sortieren, damit die wichtigsten davon ganz am Anfang
        // stehen. Danach die kleinste und die größte Reaktion des Gegners suchen.
        Collections.sort(situations, situationComparator);
        for (int i = 0; i < Math.min(LAST_MOVES_TO_COMPARE, situations.size()); i++) {
            Situation situation = situations.get(i);
	
	        if (min == null || situation.reaction < min)
	            min = situation.reaction;
	        if (max == null || situation.reaction > max)
	            max = situation.reaction;
        }
        
        if (max == null || max - min > MAX_DIFFERENCE_LAST_MOVES) {
            // Bei zu unterschiedlichen vorhergesagten Werten kann nichts genaues angegeben werden:
            return null;
        }
        
        if (situations.size() < LAST_MOVES_TO_COMPARE || max - min > 0) {
            // Nur wenige ähnliche Situationen existieren oder die
            // vorhergesagten Werte sind unterschiedlich. Daher vorschtiger setzen.
            
            if (min > 6) {
                // Sechs weniger als die kleinste vorhergesagte Zahl und zur 
                // Sicherheit noch 1-2 weniger setzen, mindestens jedoch 1.
                return Math.max(1, min - 6 - ((int) (Math.random() * 2)));
            } else {
                // 4-5 mehr als die kleinste vorgergesagte Zahl legen.
                return min + 3 + ((int) (Math.random() * 2));
            }
        } else {
            // Es gibt genug ähnliche Situationen, welche alle dasselbe voraussagen.
            
            if (min > 6)
                return min - 6; // 6 weniger als der Gegner legen
            else
                return min + 5; // 5 mehr als der Gegner legen
        }
    }
    
    /**
     * Sucht zur aktuellen Situation ähnliche Situationen.
     */
    private List<Situation> similarSituations() {
        List<Situation> similar = new ArrayList<Situation>();
        
        // i ist der Index des Zuges mit der Reaktion auf die vorangegangene Situation. Die Schleife fängt
        // mit 1 an, denn die vorangegangene Situation muss mindestens ein Zug lang sein.
        for (int i = 1; i < ownMoves.size(); i++) {
            int totalDifference = 0; // Der Unterschied zwischen der Situation und der aktuellen Situation
            int signedDifference = 0; // Der Unterschied, wobei Vorzeichen beachtet werden
            int maxLength = -1; // Länge der längsten ähnlichen gefundenen Situation
            
            // j ist die Länge der ähnlichen Situation. Bei jedem Durchgang wird die nächstgrößere Situation
            // überprüft, wobei der Gesamtunterschied jedes Mal größer werden kann.
            for (int j = 1; j <= i; j++) {
                // difference1 und difference2 sind die Unterschiede zwischen den gesetzten Zahlen der aktuellen
                // Situation und den gesetzten Zahlen der (möglicherweise) ähnlichen Situation.
                int difference1 = ownMoves.get(i - j) - ownMoves.get(ownMoves.size() - j);
                int difference2 = opponentMoves.get(i - j) - opponentMoves.get(opponentMoves.size() - j);
                
                if (Math.abs(difference1) <= MAX_DIFFERENCE_OLD_CURRENT && Math.abs(difference2) <= MAX_DIFFERENCE_OLD_CURRENT) {
                    // Unterschiede sind klein genug, sodass die Situation ähnlich ist
                    totalDifference += Math.abs(difference1) + Math.abs(difference2);
                    signedDifference += difference1 + difference2;
                    
                    maxLength = j;
                } else {
                    // Unterschiede zu groß, daher ist auch die nächstgrößere Situation nicht ähnlich genug
                    break;
                }
            }
            
            if (maxLength != -1) {
                // Es wurde eine ähnliche Situation gefunden
                
                // Die durchschnittliche Abweichung:
                int avgSignedDifference = (int) Math.round(((double) signedDifference) / (maxLength * 2));
                // Die Reaktion des Gegners, wobei die Abweichung einberechnet wird:
                int reaction = Math.max(1, opponentMoves.get(i) - avgSignedDifference);
                
            	similar.add(new Situation(maxLength, totalDifference, i, reaction));
            }
        }
        
        return similar;
    }

    /**
     * Wählt zufällig eine Zahl aus dem Bereich der vom Gegner zuletzt gesetzten Zahlen aus.
     */
    private int random(Spiel.Zug zug) {
        if (opponentMoves.isEmpty())
            return 6;
        
        // Letzte Züge des Gegners raussuchen, sortieren und Ausreißer entfernen:
        int subListStart = Math.max(0, opponentMoves.size() - LAST_MOVES_FOR_RANDOM);
        List<Integer> moves = new ArrayList<Integer>(opponentMoves.subList(subListStart, opponentMoves.size()));
        Collections.sort(moves);
        removeOutlier(moves);

        // Min und Max der Züge raussuchen:
        int min = moves.get(0);
        int max = moves.get(moves.size() - 1);

        if (min > 6) {
            // 6 weniger als das Minimum setzen (jedoch maximal MAX_RANDOM):
            return Math.min(MAX_RANDOM, min - 6);
        }
        if (moves.size() < LAST_MOVES_FOR_RANDOM && max <= 6) {
            // Kein "sicherer" Zufall, da es nicht mehr genug Züge gibt.
            // min und max sind kleiner als 7, also 6 verwenden:
            return 6;
        }
        
        // Der kleinste mögliche Zufallswert ist fünf mehr als das Minimum, der Bereich geht
        // bis zum Maximum der gegnerischen Züge, kann jedoch höchstens MAX_RANDOM lang sein:
        int startValue = min + 5;
        int endValue = Math.min(MAX_RANDOM, Math.max(startValue, max));
        return ((int) (Math.random() * (endValue - startValue + 1))) + startValue;
    }

    /**
     * Entfernt maximal einen Ausreißer aus der angegebenen sortierten Liste.
     */
    private void removeOutlier(List<Integer> list) {
        if (list.size() < 3) return; // Nur bei min. 3 Elementen möglich

        // Durchschnittlicher Abstand zwischen den jeweiligen Elementen berechnen:
        double avgDifference = 0;
        for (int j = 0; j < list.size() - 1; j++)
            avgDifference += list.get(j + 1) - list.get(j);
        avgDifference /= list.size() - 1;

        if (list.get(1) - list.get(0) > avgDifference * 2) {
            // Kleinstes Element hat den doppelten durchschnittlichen Abstand
            list.remove(0);
        } else if (list.get(list.size() - 1) - list.get(list.size() - 2) > avgDifference * 2) {
            // Größtes Element hat den doppelten durchschnittlichen Abstand
            list.remove(list.size() - 1);
        }
    }

}