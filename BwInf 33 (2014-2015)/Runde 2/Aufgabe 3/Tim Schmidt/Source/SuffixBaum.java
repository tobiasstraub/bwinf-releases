package org.timschmidt.bwinf.a3;

public class SuffixBaum {

	private final Knoten wurzel = new Knoten(0, -2, null); // Die Kante über der Wurzel ist ein leerer String. Dies wird dadurch signalisiert, dass der Endindex kleiner ist als der Anfangsindex
	
	
	
	public SuffixBaum (char s0){
		wurzel.setKind(char2Pos(s0), new Knoten(0,-1, wurzel));
	}
	
	public boolean put(int anf, int end) {
		String pfadString = Utility.subS(Main.getInput(), anf, end-1);				// Um den aktuellen Suffix input[j..i+1] hinzuzufügen bzw. einen Suffix hinzuzufügen,
																			// der genau ein Zeichen (input[i+1]) mehr enthält als ein bereits vorhandener Suffix input[j..i],
																			// muss der Pfad bis zu diesem vorhandenen Suffix input[j..i] gegangen werden
		char zusatzChar = Utility.subS(Main.getInput(), end, end).charAt(0);		// Der zusätzliche Char input[i+1] ist das letzte Zeichen des neuen Suffix input[j..i+1]
		
		Object[] pfadZiel = pfadAbgehen(pfadString);
		Knoten zielKnoten = (Knoten)pfadZiel[0];
		int zielBuchstabe = (int)pfadZiel[1];
		
		/* Sollte im Laufe der Erweiterung Regel 3 Anwendung finden, bedeutet dass, dass es im Baum bereits einen String input[j...i] geben, dem der zusatzChar input[i+1] folgt.
		 * Dann müssen auch für j' >= j alle Strings[j'...i] mit dem zusatzChar input[i+1] enden, da alle Strings input[j'...i+1] Suffixe von des gleichlangen (falls j' = j) oder längeren
		 * Strings input[j...i+1] sind und somit bereits im Baum vorhanden sind.
		 * Demnach kann die Phase nach der Anwendung von Regel 3 abgebrochen werden, da alle weiteren Erweiterungen ebenfalls in Regel 3 enden würden.
		 * Beispiel:
		 * 	Phase: xyzxy + z
		 * 		Erweiterung um xy + z (Die Erweiterungen xyzxy + z, yzxy + z und zxy + z wurden bereits ausgeführt):
		 * 			Der pfadString xy läuft in die Kante unter der Wurzel beginnend mit x und endet dort nach dem zweiten Zeichen y.
		 * 			Nach diesem Pfad wird auf Regel 3 überprüft, d.h. es wird geprüft, ob das nächste Zeichen == z ist.
		 * 			Da die Kombination auf dieser Kante zu diesem Zeitpunkt xyzxyz lautet, ist dass der Fall, das dritte Zeichen ist z.
		 * 			Nun müssen laut obiger These alle folgenden Erweiterungen (y + z, "" + z) ebenfalls auf Regel 3 stoßen:
		 * 				1. y+z
		 * 				Der pfadString y läuft in die Kante unter der Wurzel beginnend y und endet auf dem ersten Zeichen y.
		 * 				Da die Kante zu diesem Zeitpunkt yzxyz lautet, ist das nächste Zeichen z => Regel 3 findet Anwendung.
		 * 				2. ""+z
		 * 				Der pfadString "" hat eine Länge von 0 und endet damit auf dem aktuellen Knoten, in diesem Falle die Wurzel.
		 * 				Die Wurzel hat bereits eine Kante, die mit z anfängt => Regel 3 findet Anwendung.
		 */
		
		if (zielBuchstabe == -1){ // Wenn der Pfad auf einem Knoten endet
			if (istBlatt(zielKnoten)){ // Und dieser ein Blatt ist [DIES SOLLTE NIEMALS EINTRETEN] [TODO: Kommentar entfernen]
				// Gemäß Regel 1 würde hier die Vorkante des Blattes um das neue Zeichen erweitert werden. Dies wird jedoch über Veränderung der e-Variable implizit durchgeführt.
				System.out.println("||| Blatt getroffen! |||");
				return false; // Es wurde nicht Regel 3 angewandt => false zurückgeben
			} else {					// Dieser aber kein Blatt ist
				Knoten kindKnotenAufZS = zielKnoten.getKind(char2Pos(zusatzChar));
				if (kindKnotenAufZS == null){ // aber trotzdem keinen unterknoten beginnend mit dem Zusatzchar besitzt
					kindKnotenAufZS = new Knoten(end, -1, zielKnoten);	// Wird ein neues Blatt mit Vorkante zusatzChar gemäß Regel 2 an den zielKnoten angehängt
					zielKnoten.setKind(char2Pos(zusatzChar), kindKnotenAufZS);
					return false; // Es wurde nicht Regel 3 angewandt => false zurückgeben
				} else	// Besitzt der aktuelle einen Unterknoten beginnend mit dem Zusatzchar, wird gemäß Regel 3 nichts unternommen.
					return true; // Es WURDE Regel 3 angewandt => true zurückgeben
			}
		} else { // Wenn der Pfad auf einer Kante endet
			if (zielKnoten.getVorKante().charAt(zielBuchstabe+1) != zusatzChar){ // Wenn der nächste Buchstabe auf der Kante nicht dem zusatzChar entspricht, d.h. wenn der String input[j...i+1] nicht bereits im Baum ist
				// Vorkante zu zielKnoten durchtrennen:
				// 1. Neuen Knoten mit ersten Teil der Kante als Vorkante erstellen, dieser hat als Elternknoten den Elternknoten des aktuellen Zielknotens
				Knoten mittelKnoten = new Knoten(zielKnoten.getVorKanteAnfang(), zielKnoten.getVorKanteAnfang() + zielBuchstabe, zielKnoten.getEltern());
				// 2. Den Mittelknoten als Kind des Elternknotens eintragen
				mittelKnoten.getEltern().setKind(char2Pos(mittelKnoten.getVorKante().charAt(0)), mittelKnoten);
				// 3. Mittelknoten als Elternknoten des Zielknotens eintragen
				zielKnoten.setEltern(mittelKnoten);
				// 4. Vorkante des zielKnotens auf den zweiten Teil der Kante verkürzen
				zielKnoten.kuerzeVorKante(zielBuchstabe + 1); // zielBuchstabe +1, da zielBuchstabe nullbasiert, kuerzeVorKante einsbasiert ist
				// 5. Zielknoten als Kind des Mittelknotens eintragen
				mittelKnoten.setKind(char2Pos(zielKnoten.getVorKante().charAt(0)), zielKnoten);
				
				
				
				 
				// Neues Blatt gemäß Regel 2 an den Mittelknoten anhängen
				Knoten neuKnoten = new Knoten(end, -1, mittelKnoten);
				mittelKnoten.setKind(char2Pos(zusatzChar), neuKnoten);
				return false; // Es wurde nicht Regel 3 angewandt => false zurückgeben
			} else // Sollte dies nicht der Fall sein und hinter dem String input[j...i] doch der zusatzChar input[i+1] stehen, ist der String input[j...i+1] bereits im Baum und gemäß Regel 3 wird nichts unternommen
				return true; // Es WURDE Regel 3 angewandt => true zurückgeben
			
		}
		
		
	}
	
	private Object[] pfadAbgehen (String pfadString){
		Knoten curKnoten = wurzel;
		
		// Hier muss nie überprüft werden, ob der Pfad überhaupt existiert, da er der Pfad zu einem vorher eingefügten String ist und somit existieren MUSS.
		while (true){
			if (pfadString.length() == 0){ // Wenn der restliche Pfadstring leer ist, endspricht der aktuelle Knoten genau dem Ende des Pfades
				Object[] ret = {curKnoten, -1}; // Zielbuchstabe von -1 signalisiert, dass der Pfad bis zum curKnoten reicht
				return ret;
			}
			
			Knoten unterKnoten = curKnoten.getKind(char2Pos(pfadString.charAt(0))); // Kante am ersten Buchstaben identifizieren
			if (unterKnoten.getVorKante().length() <= pfadString.length()){ // Falls die Kantenbeschriftung nicht länger ist als der restliche Teilstring, muss die Kante komplett abgegangen werden
				curKnoten = unterKnoten; // Pfad runtergehen
				pfadString = Utility.subS(pfadString, curKnoten.getVorKante().length(), pfadString.length()-1); // Pfadstring um die bereits abgelaufenen Zeichen verkürzen
			} else { // Falls die Kantenbeschriftung länger ist als der restliche Teilstring, ist der Pfad bei length(pfadString)-Buchstaben in der aktuellen Kante zuende
				curKnoten = unterKnoten; // Pfad runtergehen
				Object[] ret = {curKnoten, pfadString.length() - 1}; // Zurückgeben, an welchem Buchstabe der Kante vor curKnoten der Pfad zuende ist
				return ret;
			}
		}
	}
	
	private byte char2Pos(char c){
		return (byte) Main.ZEICHENSATZ.indexOf(c);
	}
	
	private boolean istBlatt(Knoten k){
		for (int i = 0; i < k.getKinder().length; i++) // Alle Unterknoten durchgucken
			if (k.getKind(i) != null)	// Wenn ein Unterknoten existiert...
				return false;			// ... ist der gegebene Knoten kein Blatt
		
		return true;
	}
	
	public Knoten getWurzel(){
		return wurzel;
	}
}
