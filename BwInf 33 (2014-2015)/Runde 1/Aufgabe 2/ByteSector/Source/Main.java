package org.bytesector.bwinf.a2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main(){
		// Gewichte anfragen
		System.out.println("Massen der Gewichte mit Komma getrennt eingeben (bsp. 1,2,3,44):");
		Scanner sc = new Scanner(System.in);
		
		// Benutzerinput splitten und konvertieren
		int[] input = null;
		try {
			String read[] = sc.nextLine().split(",");
			input = new int[read.length];
			for (int i = 0; i < input.length; i++)
				input[i] = Integer.parseInt(read[i]);
			
			sc.close();
		} catch (Exception e){
			System.out.println("ERROR: Eingabe ungültig!");
			System.exit(0);
		}
		
		// Input auslesen
		sortArrayInt(input);
		List<Gewicht> gewichte = new ArrayList<Gewicht>();
		for (int i = 0; i < input.length; i++){
			gewichte.add(new Gewicht(input[i]));
		}
		
		List<Gewicht> balken = createBalken(gewichte);
		
		// Mobile erstellen: So lange Gewichte bzw. Balken an neue Balken hängen, bis ein einziger übrig bleibt
		while (balken.size() > 1){
			balken = createBalken(balken);
		}
		
		// Ergebnis ausgeben
		System.out.println(System.lineSeparator() + "Mobile fertiggestellt:");
		System.out.println(genMasse(balken.get(0)));
	}

	/** Verteilt alle eingegebenen Gewichte so auf neue erstellte Balken, dass diese balanciert sind, und gibt dann die Liste an Balken zurück.*/
	private List<Gewicht> createBalken (List<Gewicht> gewichte){
		// Berechnen, wie viele Balken für die Unterbringung aller Gewichte erstellt werden müssen (=need)
		int anzahlBalken = (int) Math.ceil((double)gewichte.size() / 4);
		List<Gewicht> balken = new ArrayList<Gewicht>();
		
		// Alle erforderlichen Balken durchgehen
		for (int i = 0; i < anzahlBalken; i++){
			
			// Reihung an Gewichten erstellen, die später dem aktuellen Balken zugeteilt werden
			List<Gewicht> curGewichte = new ArrayList<Gewicht>();
			// Jedes 'Anzahl an Balken'-te Gewicht wird dem aktuellen Balken zugewiesen (=> Balken erhalten abwechselnd die Gewichte), damit die Gewichtsverteilung auf die einzelnen Balken ähnlich ist.
			// Hierbei wird darauf geachtet, dass kein Balken mehr als vier Gewichte erhält
			// 		Beispiel: Insgesamt 3 Balken, dies ist der 2
			//					=> Jedes dritte Gewicht (+ Offset von 1) wird diesem Balken zugeordnet
			for (int j = i; j < gewichte.size() && curGewichte.size() < 4; j += anzahlBalken){
				curGewichte.add(gewichte.get(j));
			}
			
			// Balken mit den aktuellen Gewichten auf Positionen, mit denen der Balken balanciert wäre, erstellen
			balken.add(new Balken(curGewichte, findPos(curGewichte)));
		}
		return balken;
	}
	
	/** Sortiert einen int-Array in absteigender Reihenfolge der Werte.*/
	private void sortArrayInt(int[] input){
		
		// Jedes Element durchgehen
		for (int i = 0; i < input.length; i++){ 
			// Größtes Element im verbleibenden Teil des unsortiertes Array finden...
			int curMax = i;
			for (int j = i+1; j < input.length; j++){ 
				if (input[curMax] < input[j])
					// Index merken
					curMax = j;
			}
			// ...und tauschen
			int _ = input[curMax];
			input[curMax] = input[i];
			input[i] = _;
		}
	}
	
	/** Findet Positionen für eine Reihung an Gewichten, mit denen der haltende Balken balanciert wäre.*/
	private int[] findPos(List<Gewicht> curGewichte){
		int curPos[] = new int[curGewichte.size()];
		
		// Jeweils zwei Gewichten Positionen, die auf verschiedenen Seiten liegen und deren Wert der Masse des anderen Gewichtes entspricht, zuordnen
		// => 	Balken bleibt balanciert
		// 		Beispiel: 2 auf Pos 3 + 3 auf Pos -2 
		//				  => 2*3 + 3*-2 = 0
		// Liegt eine ungerade Anzahl an Gewichten vor, wird eines der Gewichte auf Position 0 platziert
		int cut = 0;
		if (curGewichte.size() % 2 == 1){
			curPos[curGewichte.size()-1] = 0;
			cut = 1;
		}
		
		for(int j = 0; j < curGewichte.size() - cut; j += 2){
			curPos[j] = curGewichte.get(j+1).getMasse();
			curPos[j+1] = -curGewichte.get(j).getMasse();
		}
		
		// falls die Positionen, die dem 3. und 4. Gewichte zugeordnet wurden, schon belegt sind, beide um den selben Faktor erhöhen
		if (curGewichte.size() == 4){
			int korrektur = 1;
			while (curPos[0] == curPos[2] * korrektur || curPos[1] == curPos[3] * korrektur){
				korrektur++;
			}
			curPos[2] *= korrektur;
			curPos[3] *= korrektur;
		}
		
		return curPos;
		
	}
	
	/**Gibt die Masse eines Gewichtes aus. Ist das Gewicht ein Balken, wird zusätzlich noch die textuelle Darstellung
	 * des Balkens in der Form '[position1_masse1|position2_masse2|...]" ausgegeben.*/
	private String genMasse(Gewicht gewicht){
		// Rückgabestring
		String masse = "";
		
		// Masse des Gewichtes dem Rückgabestring hinzufügen
		masse += gewicht.getMasse();
		
		// Ist das Gewicht ein Balken, Subelementeklammer setzen
		if ((gewicht instanceof Balken)){
			// Klammer öffnen
			masse+="[";
			
			// Alle Subelemente durchgehen
			for(int i = 0; i < ((Balken)gewicht).getSubelemente().size(); i++){
				// Für jedes Subelement die Position gefolgt von _ gefolgt vom Rückgabestring dieser Methode auf das Subelement dem Rückgabestring hinzufügen
				masse += ((Balken)gewicht).getPositionen()[i] + "_" + genMasse(((Balken)gewicht).getSubelemente().get(i)) + "|";
			}
			
			// Letzten Trenntstrich entfernen
			masse = masse.substring(0, masse.length()-1);
			
			// Klammer schließen
			masse += "]";
		}

		// String zurückgeben
		return masse;
	}
}
