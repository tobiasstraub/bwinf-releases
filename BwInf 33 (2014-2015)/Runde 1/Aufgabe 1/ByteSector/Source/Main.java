package org.bytesector.bwinf.a1v2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private List<Step> solution;
	private List<Container> col;
	private Knot mainKnot;
	private BufferedReader reader;
	
	// Boolean, welcher anzeigt, ob während der letzten Iteration Fortschritt gemacht wurde
	static private boolean progress;
	
	public static void main(String[] args) {
		new Main(args);
	}
	
	public Main(String[] args){
		solution = new ArrayList<Step>();
		col = new ArrayList<Container>();
		String[] input = new String[3];
		progress = false;
		
		try {
			// Textdatei einlesen
			// Leser mit in den Startparametern angegebenem Pfad (args[0]) erstellen
			reader = new BufferedReader(new FileReader(args[0]));
			
			// Textdatei zeilenweise auslesen
			input[0] = reader.readLine();
			input[1] = reader.readLine();
			input[2] = reader.readLine();
			
			// Anzahl an Behältern im Besitz von Person A auslesen
			int numA = Integer.parseInt(input[0]);
			
			// Größen und Füllmengen trennen
			String[] sizes = input[1].split(" ");
			String[] fills = input[2].split(" ");
			
			// Behälter erstellen
			// Hierbei beachten, dass die Strings in Integer konvertiert werden
			for (int i = 0; i < sizes.length; i++){
				col.add(new Container(Integer.parseInt(sizes[i]), Integer.parseInt(fills[i]), (i < numA), i));
			}
			
		} catch (Exception e) {
			System.out.println("--- ERROR: DATEI KONNTE NICHT GELESEN WERDEN ---");
			System.exit(0);
		}
		
		System.out.println("-=- Suche nach Lösung... -=-");
		if (findSol()){
			System.out.println("Lösung gefunden! Öffne output.html...");
			Output out = new Output(solution, col);
			out.open();
			
			
		} else
			System.out.println("Es wurde keine Lösung gefunden!");
	}
	
	// Übermethode zur Lösungsfindung
	// Weißt den Hauptknoten so lange dazu an, Unterknoten zu erstellen, bis eine Lösung gefunden wurde oder das Problem als unlösbar befunden wird
	private boolean findSol(){
		// Erstellen des Hauptknotens mit der Anfangssammlung
		// Hierbei enthält der damit verbundene Schritt weder Quell- noch Zielgefäß
		mainKnot = new Knot(col, new Step(null, null, col));
		
		// Urpsrungszustand der programmweiten Liste an Füllstandkombinationen hinzufügen
		Knot.foundKnots.add(mainKnot.summarizeCol());
		
		// Schleife zur Lösungsfindung
		while (true)
			// Hauptknoten anweisen, Unterknoten zu erstellen
			// Gibt der Hauptknoten true zurück, wurde eine Lösung gefunden, ...
			if (mainKnot.goDown()){
				// ... die Schritte bis zum Lösungsknoten werden gesammelt und ...
				mainKnot.getSteps();
				// ... in 'solution' gespeichert
				solution = Knot.solSteps;
				// Letztendlich wird der Lösungsfindungsprozess mit positivem Ergebnis beendet
				return true;
			} 
			// Falls in dieser Iteration keine Lösung gefunden wurde, ...
			else {
				// ... aber auch kein Fortschritt gemacht wurde, gibt es keine Lösung und der Lösungsfindungsprozess wird mit negativem Ergebnis beendet
				if (!progress){
					return false;
				} 
				// ... aber Fortschritt gemacht wurde, progress für die nächste Iteration auf false setzen
				else
					progress = false;
			}
		
	}
	
	static public void setProgress(boolean p){
		progress = p;
	}
}
