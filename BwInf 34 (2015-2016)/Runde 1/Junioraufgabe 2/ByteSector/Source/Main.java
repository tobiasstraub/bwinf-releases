package ja2;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Main {

	// Die Karte wird in Form eines zweidimensionalen Boolean-Arrays gespeichert.
	// Schwarze Felder werden mit 'true', weiße mit 'false' wiedergegeben.
	protected boolean[][] map;
	// Kassiopeias Startposition wird in zwei Integern für ihre Abszisse (X) und Ordinate (Y) aufgeteilt.
	protected int kassX, kassY;
	
	public static void main(String[] args) {
		Main main = new Main();
		
		main.parseMapfile(main.getMapfile());
		
		String result;
		if (main.checkAccess())
			result = "Kassiopeia kann alle Felder erreichen.";
		else
			result = "Kassiopeia kann NICHT alle Felder erreichen.";
		
		JOptionPane.showMessageDialog(null, result, "Ergebnis", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/** Überprüft, ob Kassiopeia alle Felder erreichen kann **/
	protected boolean checkAccess(){
		// Alle Felder, die Kassiopeia erreichen kann, sind auch für den Floodfill erreichbar.
		floodFill(kassX, kassY);
		
		// Sind nach dem Floodfill noch Felder weiß, sind diese nicht durch einfache horizontale und vertikale Bewegungen
		// erreichbar und es ist Kassiopeia unmöglich, alle Felder zu betreten.
		boolean whiteLeft = false;
		loopX: // Äußere Schleife wird benannt, um sie später abbrechen zu können.
		for (int i = 0;i < map.length; i++)
			for (int j = 0; j < map[0].length; j++)
				if (!map[i][j]){
					whiteLeft = true;
					break loopX; // Sobald ein weißes Feld gefunden wurde, kann die Suche abgebrochen werden.
				}
		
		return !whiteLeft;
	}
	
	/** Färbt alle aus gegebener Position direkt erreichbaren Felder schwarz. **/
	protected void floodFill(int x, int y){
		// Falls aktuelles Feld weiß ist...
		if (!map[x][y]){
			// ... schwarz färben und Füll-Kommando an direkt angrenzende Felder weitergeben.
			map[x][y] = true;
			floodFill(x + 1, y);
			floodFill(x - 1, y);
			floodFill(x, y + 1);
			floodFill(x, y - 1);
		}
	}
	
	/** Erfragt die einzulesende Datei **/
	protected File getMapfile(){
		// Das Objekt zum Anzeigen einer Dateiabfrage wird erstellt.
		JFileChooser fileChooser = new JFileChooser();
		// Als Standardordner wird das Benutzerverzeichnis festgelegt.
		// Aus diesem kann aber selbstverständlich frei navigiert werden.
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		// Schließlich wird der Dialog geöffnet...
		int result = fileChooser.showOpenDialog(null);
		// und die Eingabe - sofern vorhanden - weitergegeben.
		if (result == JFileChooser.APPROVE_OPTION)
			return(fileChooser.getSelectedFile());
		else
			// Sollte keine Eingabe getätigt worden sein, wird das Programm beendet.
			System.exit(0);
		
		// Rein formale Wertrückgabe, wird nie erreicht.
		return null;
	}

	/** Liest die eingegebene Datei aus und speichert die Informationen in die entsprechenden Variablen **/
	protected void parseMapfile(File mapfile){
		// Die Beispieldatei wird zeilenweise eingelesen und die Zeilen in einer Liste aus Strings zwischengespeichert.
		List<String> lines = null;
		try {
			lines = Files.readAllLines(mapfile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Es wird davon ausgegangen, dass gültige Beispieldateien eingegeben werden, weshalb außer des erforderlichen
		// Abfangens einer IO-Ausnahme keine Sicherung eingebaut wird.
		
		// Die Breite und Höhe werden ausgelesen, indem die erste Zeile am Leerzeichen
		// geteilt und die beiden Zahlen konvertiert werden.
		int width = Integer.parseInt(lines.get(0).split(" ")[1]),
				height = Integer.parseInt(lines.get(0).split(" ")[0]);
		
		// Der Karten-Array wird mit den nun bekannten Werten für Breite und Höhe initialisiert.
		map = new boolean[width][height];
		
		// Es wird durch alle Zeichen ab der zweiten Zeile iteriert.
		for (int i = 0;i < width; i++)
			for (int j = 0; j < height; j++){
				// Es wird beim Auslesen der Zeichen der Index der Zeile um eins korrigiert, um die erste Zeile zu überspringen
				char curChar = lines.get(j+1).charAt(i);
				
				// Überprüfen, ob es sich bei dem aktuellen Zeichen um Kassiopeias Position handelt und diese ggf. notieren
				if (curChar == 'K'){
					kassX = i;
					kassY = j;
				}
				// Schwarze Felder, gekennzeichnet durch eine Raute, werden als Boolean
				// mit Wert 'true' gespeichert, weiße Felder mit 'false'.
				map[i][j] = (lines.get(j+1).charAt(i) == '#');
			}
			
		
	}
}
