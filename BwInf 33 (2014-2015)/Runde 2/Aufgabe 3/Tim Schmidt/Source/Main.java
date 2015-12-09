package org.timschmidt.bwinf.a3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	static final String ZEICHENSATZ = "ACGT$";
	
	private static int k, l;
	private static String input;
	
	static List<String> repetitionen;

	public static void main(String[] args) {
		new Main(args);
	}
	
	public Main(String[] args){
		repetitionen = new ArrayList<String>();
		
		// Textdatei einlesen
		/*BufferedReader reader;
		input = "";
		try {
			reader = new BufferedReader(new FileReader(args[0]));
			input = reader.readLine();
		} catch (IOException e) {
			System.out.println("Datei nicht gefunden!");
			System.exit(0);
		}*/
		input = "CAGGAGGATTA";
		
		
		/*Scanner sc = new Scanner(System.in);
		input = sc.nextLine();
		sc.close();*/
		
		// Delimiter hinzufügen
		input += "$";
		
		SuffixBaum sf = sufTreeKonstruieren(input);
		System.out.println("Done building!");
		
		k = 3;
		l = 2;
		
		sf.getWurzel().calcVorkommen("");
		
		for (int i = 0; i < repetitionen.size(); i++){
			for (int j = 0; j < repetitionen.size(); j++){
				if (!repetitionen.get(i).equals(repetitionen.get(j)) && repetitionen.get(i).contains(repetitionen.get(j))){
					repetitionen.remove(j);
					if (i > j)
						i--;
					j--;
				}
			}
		}
		
		System.out.println("--- Gültige Repetitionen ---");
		for (String s : repetitionen){
			System.out.println(s);
		}
	}

	private SuffixBaum sufTreeKonstruieren (String input){
		// Länge m des Input-Textes
		int m = input.length();
		
		// Suffixbaum I[1] erstellen
		SuffixBaum sB = new SuffixBaum(input.charAt(0));
		int jImp = 0; // Erweiterung 0 kann ab sofort implizit geschehen
		Knoten.setE(0);

		for (int i = 0; i < m - 1; i++){ // Phase i+1
			Knoten.setE(i+1);
			for (int j = jImp + 1; j <= i + 1; j++){ // Erweiterung j
				jImp = j; // Notieren, dass diese Erweiterung von nun an implizit geschehen kann
				if (sB.put(j, i+1)){ // Falls R3 Anwendung gefunden hat
					jImp--; // Da Erweiterung gemäß R3 nicht durchgeführt wurde, kann sie in der nächsten Phase nicht implizit geschehen
					break;	// Da R3 Anwendung gefunden hat, wird die Phase frühzeitig abgebrochen
				} else {
					
				}
			}
		}
		
		return sB;
	}
	
	
	
	/* Keine Mechanik zum Entfernen eines Blatt, nur zum Teilen von darüberliegenden Kanten
	 * => Jedes Blatt bleibt ein Blatt
	 * => Die Kante jedes Blattes wird bei Zugabe eines neues Buchstabens gemäß Regel 1 erweitert, sollte Regel 2 später auf diese Kante angewendet werden und sie durchgetrennt werden,
	 * 		ändert sich nur der Startindex des Strings der Kante über dem Blatt
	 * 
	 * Da Regel 3 die Phase beendet, werden in jeder Phase eine Anzahl, genannt j, Erweiterungen ausgeführt, die Regel 1 oder 2 benutzen. Da Bei jeder Anwendung von Regel 2 die Anzahl an Blättern
	 * erhöht wird, Regel 2 aber nicht zwangsweise Anwendung finden muss, ist die Anzahl an Erweiterungen nach R1 oder R2 in der nächsten Phase i+1 entweder gleich oder größer:
	 *  j(i+1) >= j(i)
	 *  Da jedes bereits existierende Blatt gemäß Regel 1 erweitert wird und die Anzahl an Blättern der Anzahl an Erweiterungen der vorherigen Phase entspricht, weil die vorherige Phase diese Blätter
	 *  entweder gemäßt R2 erstellt oder ebenfalls gemäß R1 erweitert hat, werden in Phase i+1 die ersten j(i) Erweiterungen auf Blätter treffen und diese gemäß R1 um das neue Zeichen erweitern.
	 *  
	 *  Die Beobachtung, dass j(i)-Anzahl Blätter um das gleiche Zeichen erweitert werden müssen, lässt nach einer Mechanik suchen, all diese Erweiterungen in einem Schritt implizit auszuführen:
	 *  Da die Strings auf den Kanten nicht explizit ausgeschrieben werden, sondern lediglich Verweise auf die Indizes, die die Anfangs- und Endposition im kompletten bisher eingegebenen Text gleichen,
	 *  sind, wird bei Anwendung der R1 nur der Index, der die Endposition markiert, an das neue Ende des Textes verschoben.
	 *  Da dieser Endindex selbstverständlich für jede Kante gleich ist, da sich alle auf den gleichen Text beziehen, kann anstatt des Endindexes auch ein Verweis auf eine Variable e, die den Endindex
	 *  darstellt, in den Kanten gespeichert werden. Wird nun diese Variable e bei Zugabe eines neuen Zeichens zum Gesamttext um eine Position verschoben, werden auch alle Kanten aktualisiert, da ihr Index
	 *  keine eigene Zahl, sondern nur der Verweis auf e ist.
	 *  
	 *  Auf diese Art und Weise können die ersten j(i)-Erweiterungen implizit durch die Veränderung von e erledigt werden. Die ersten j(i) Erweiterungen erfolgen damit in konstanter Zeit.
	 *  
	 *  Für jede Phase i+1 bleiben damit nur noch die den ersten j(i)-Erweiterungen folgenden expliziten Erweiterungen.
	 *  Da die Anzahl j(i) der in konstanter Zeit erledigten Erweiterungen von einer Phase i+1 auf die nächste niemals sinkt, sondern höchstens im Falle der Anwendung der R3 direkt nach den ersten
	 *  j(i)-Erweiterungen gleich bleibt, UND es nur (Länge m des input-Textes)-Anzahl Phasen gibt UND die Anzahl an maximalen Erweiterungen einschließlich den impliziten pro Phase durch m begrenzt ist,
	 *  werden in allen Phasen zusammen maximal m explizite Erweiterungen plus eine weitere pro Phase zur Durchführung aller impliziten Erweiterungen einer Phase durchgeführt.
	 *  
	 *  Der Algorithmus führt mit diesen Optimierungen also insgesamt nur noch 2*m explizite Erweiterungen durch.
	 * 
	 */
	

	public static int countSubstrings(String sub){
		return 0; // TODO: Count method
	}
	
	public static int getK() {
		return k;
	}

	public static int getL() {
		return l;
	}
	
	public static String getInput() {
		return input;
	}
	
}
