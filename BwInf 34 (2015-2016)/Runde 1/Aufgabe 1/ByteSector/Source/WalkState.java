package a1;

import java.util.ArrayList;
import java.util.List;

public class WalkState {
	boolean[][] map;
	int posX, posY;
	String cmd;
	
	static private String solution = "";
	
	List<WalkState> subStates;
	
	public WalkState(boolean[][] map, int posX, int posY, String cmd){
		map[posX][posY] = true;	// Aktuelle Position als unbegehbar einfärben.
		
		this.map = map;
		this.posX = posX;
		this.posY = posY;
		this.cmd = cmd;
		subStates = new ArrayList<WalkState>();
	}
	
	public int goDown(){
		// Integer zum Notieren von Fortschritt (0 = kein Fortschritt; 1 = Fortschritt; 2 = Lösung gefunden)
		int progress = 0;
		
		// Wenn es noch keine Unterzustände gibt, wird versucht, welche zu erschaffen.
		if (subStates.size() == 0){
			// Nord
			// Nur weitergehen, wenn das nördliche Feld weiß ist.
			if (!map[posX][posY-1]){
				// Neue Anweisung notieren
				String newCmd = "N";
				// Neuen Zustand mit eigener Kopie der Karte etc. erstellen
				subStates.add(new WalkState(cloneMap(map), posX, posY-1, newCmd));
				// Notieren, dass Fortschritt gemacht wurde
				progress = 1;
			}
			// Osten
			if (!map[posX+1][posY]){
				String newCmd = "O";
				subStates.add(new WalkState(cloneMap(map), posX+1, posY, newCmd));
				progress = 1;
			}
			// Süden
			if (!map[posX][posY+1]){
				String newCmd = "S";
				subStates.add(new WalkState(cloneMap(map), posX, posY+1, newCmd));
				progress = 1;
			}
			// Westen
			if (!map[posX-1][posY]){
				String newCmd = "W";
				subStates.add(new WalkState(cloneMap(map), posX-1, posY, newCmd));
				progress = 1;
			}
			
			// Nach der Erstellung der neuen Zustände wird überprüft, ob mindestens einer von ihnen zum Lösungszustand führt.
			// Wenn keine neuen Zustände erstellt werden konnten, ist subStates leer und dieser Test wird übersprungen.
			for (WalkState curState : subStates){
				if (curState.done()){			// Erfüllt einer der Unterzustände die Bedingung
					solution = cmd + solution;	// wird der Lösungsweg um die eigene Anweisung erweitert
					return 2;					// und zurückgegeben, dass eine Lösung gefunden wurde
				}
					
			}
			
		} else {	// Falls es bereits Unterzustände gibt, wird das Kommando weitergeleitet
			
			// Eine Abschussliste wird verwendet, da während der Iteration über die Liste an
			// Unterzuständen keine Zustände aus ihr gelöscht werden dürfen.
			List<WalkState> deadStates = new ArrayList<WalkState>();
			for (WalkState curState : subStates){
				int curProgress = curState.goDown();
				switch (curProgress){
				case 0: // Wenn der Unterzustand keinen Fortschritt meldet...
					deadStates.add(curState);	// wird er auf die Abschussliste geschrieben
					break;
				case 1:	// Wenn der Unterzustand Fortschritt meldet...
					progress = 1;				// wird dieser auch hier verzeichnet
					break;
				case 2: // Wenn der Unterzustand eine Lösung gefunden hat...
					solution = cmd + solution;	// wird der Lösungsweg um die eigene Anweisung erweitert
					return 2;					// und zurückgegeben, dass eine Lösung gefunden wurde
				}
			
			}
			
			// Zustände auf der Abschussliste werden dem GC überlassen
			for (WalkState curState : deadStates)
				subStates.remove(curState);
			
		}
		
		// Sollte bis zu diesem Zeitpunkt kein neuer Zustand erfolgreich erstellt worden sein
		// bzw. kein Unterzustand Fortschritt verzeichnet haben, ist es von diesem Zustand aus
		// unmöglich, eine Lösung zu erreichen. Es wird der Standardwert 'kein Fortschritt'
		// (progress = 0) an den Oberzustand zurückgegeben.
		
		return progress;
	}
	
	/** Überprüft, ob es keine weißen Felder mehr gibt und somit eine Lösung gefunden wurde. **/
	public boolean done(){
		boolean ret = false;
		loopX: // Äußere Schleife wird benannt, um sie später abbrechen zu können.
		for (int i = 0;i < map.length; i++)
			for (int j = 0; j < map[0].length; j++)
				if (!map[i][j]){
					solution = cmd;
					ret = true;
					break loopX; // Sobald ein weißes Feld gefunden wurde, kann die Suche abgebrochen werden.
				}
		return !ret;
	}
	
	/** Statische Funktion zum Klonen einer Karte **/
	static public boolean[][] cloneMap(boolean[][] map){
		boolean[][] ret = new boolean[map.length][map[0].length];
		
		for (int i = 0; i < map.length; i++)
			ret[i] = map[i].clone();
		
		return ret;
	}
	
	static public String getSolution(){
		return solution;
	}
}
