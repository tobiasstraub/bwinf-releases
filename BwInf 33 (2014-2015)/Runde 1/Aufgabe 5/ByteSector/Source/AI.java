import java.util.ArrayList;
import java.util.List;

public class AI {

	// Schläger, Ball und Zug
	Spiel.Zustand.Schlaeger me, opp;
	Spiel.Zustand.Ball ball;
	Spiel.Zug zug;
    
    // --Pfad
    double m,b;									// Steigung und Verschiebung auf der Y-Achse
    double landepunkt;							// Höhe, auf der der Ball auf eine Seite auftreffen wird
    boolean ersteMessung = true, 				// Gibt ab, ob dies die erste Erfassung eines Punktes ist
    		ersteRechnung = true, 				// Gibt an, ob dies die erste Messung (zweiter erfasster Punkt) seit dem Anfang der Flugbahn ist
    		nachUnten, vorherigesNachUnten, 	// vertikale Richtung
    		zuMir, vorherigesZuMir, unklarVorherigesUnten = true; // horizontale Richtung
    int spiegelachse = -1, verschiebung = 0;	// Welche Achse zum spiegeln verwendet wird (-1 bedeutet keine) und wie hoch die Verschiebung ist
    int vorherigesX, vorherigesY;				// Vorheriger Punkt
    List<int[]> koordsUn;						// Liste an Koordinaten. Die Y-Koordinate wird hierbei entspiegelt (also "übersetzt", siehe Dokumentation)

	public void zug(int id, Spiel.Zustand zustand, Spiel.Zug zug) {
		setup(id, zustand, zug);
		flugbahnUpdate();
		bewegung();
        return;
	}
	
	private void setup(int id, Spiel.Zustand zustand, Spiel.Zug zug){
	
		// Parameter auf Eigenschaten übertragen
		this.zug = zug;
		ball = zustand.listeBall().get(0);
		
		// Schläger identifizieren
		for (Spiel.Zustand.Schlaeger cur : zustand.listeSchlaeger())
			if (cur.identifikation() == id)
				me = cur;
			else
				opp = cur;
				
		// Liste an Koordinaten initialisieren
		if (koordsUn == null)
			koordsUn = new ArrayList<int[]>();
		
	}
    
    private void flugbahnUpdate(){
    	// Aktuelle Position eintragen, Entspiegelung erfolgt später
    	int[] curKoords = {ball.xKoordinate(), ball.yKoordinate()};
    	koordsUn.add(curKoords);
        
    	// Falls dies der erste erfasste Punkt ist, kann keine Flugbahn berechnet werden, also wird übersprungen
        if (ersteMessung){
        	ersteMessung = false;
        }
        else {
        	// Für schnellere Eingaben aktuelle Koordinaten erneut speichern
    		int x = ball.xKoordinate(),
				y = ball.yKoordinate();
    		
    		// Falls keine Veränderung in der X-Koordinate zu sehen ist, kann keine horizontale Richtung bestimmt werden, also nicht geklärt werden,
    		// ob es sich noch um die selbe Flugbahn handelt. In diesem Falle wird die Messung ignoriert.
        	if (x-vorherigesX == 0){
        		koordsUn.remove(koordsUn.size()-1);
        		return;
        	}
    		
        	// Richtung erkennen
        	zuMir = vorherigesX > x;
        	nachUnten = vorherigesY < y;
    		
    		// Falls sich die horizontale Richtung geändert hat, wurde der Ball entweder neu eingeworfen, oder hat einen Schläger getroffen. In diesem Falle
    		// gibt es eine neue Flugbahn und die alten Werte können verworfen werden.
    		// Falls sich die vertikale Richtung geändert hat, bedeutet dies, dass entweder die obere oder untere Spielfeldbegrenzung oder ein Schläger an einem der äußeren
    		// Teile getroffen wurde.
    		// Wurde ein Schläger getroffen, ändert sich außerdem noch die horizontale Richtung. Dieser Fall wird also schon bearbeitet.
    		// Wurde eine Spielfeldbegrenzung getroffen, kommt es zu der in der Dokumentation beschriebenen Spiegelung der Punkte an dieser Spielfeldgrenze.
    		// In diesem Falle werden jetzt die Werte geändert, auf deren Basis die Punkte entspiegelt werden.
    		//
    		// Sollte dies jedoch die erste Erfassung der Richtung sein, d.h. es wurde eine neue Flugbahn durch Einwurf des Balles oder Kollision mit einem Schläger erstellt,
    		// ist eine Richtungsänderung nicht zu überprüfen.
    		if (ersteRechnung){
    			// Aktuelle horizontale Richtung zwecks späteren Vergleichs merken
    			vorherigesZuMir = zuMir;
    			// Ist die vertikale Richtung unklar, weil der aktuelle und der vorherige Y-Wert übereinstimmen, wird die Überprüfung der vertikalen Richtungsänderung verzögert
    			if (vorherigesY == y)
    				unklarVorherigesUnten = true;
    			vorherigesNachUnten = nachUnten;
    			ersteRechnung = false;
    		} else {
    			// horizontale Richtungsänderung überprüfen
	    		if (vorherigesZuMir != zuMir){
	    			vorherigesZuMir = zuMir;
	    			
	    			// Neue horiz. Richtung => Neue Flugbahn => Alle alten Punkte verwerfen => Nur aktueller Punkt (also nur einer) übrig => Flugbahnberechnung abbrechen
	    			int[] aktuell = {x,y};
	    			koordsUn = new ArrayList<int[]>();
	    			koordsUn.add(aktuell);
	    			
	    			// Spiegelung zurücksetzen, da es eine neue Flugbahn gibt
	    			spiegelachse = -1;
	    			verschiebung = 0;
	    			ersteRechnung = true;
	    			
	    			// Neueinwurf oder Schlägerabprall => ggf. neue Y-Richtung => Alte Y-Richtungsaufzeichnungen verwerfen
	    			unklarVorherigesUnten = true;	// Führt dazu, dass in der nächsten Messung der alte Wert als ungültig angesehen wird
	    			return;
	    		} else {
		    		// vertikale Richtungsänderung überprüfen
	    			if (vorherigesY != y){							// Falls die aktuelle Richtung unklar ist, Richtungsüberprüfung überspringen
	    				if (unklarVorherigesUnten){					// Falls die vorherige Richtung noch unklar ist (was durch obiges geschehen kann),
	    					vorherigesNachUnten = nachUnten;		// diese Richtung, aber klar,
	    															// diese Richtung als vorherige speichern und Richtungsüberprüfung überspringen
	    					unklarVorherigesUnten = false;
	    				} else {									// Falls vorherige und aktuelle Richtung klar,
	    															//Richtungsüberprüfung durchführen
				    		if (vorherigesNachUnten != nachUnten){
				    			// Ist noch keine Spiegelachse gesetzt, war die Anzahl getroffener Spielfeldbegrenzungen gerade
				    			// => Jetzt wird die Spiegelachse gesetzt
				    			if (spiegelachse == -1)
				    				// Fliegt der Ball nach dem Abprall nach unten, wurde die obere Spielfeldbegrenzung (y=0) getroffen
				    				if (nachUnten)
				    					spiegelachse = 0;
				    				else
				    					spiegelachse = 59;
				    			// Ist bereits ein Spiegelachse gesetzt, war die Anzahl getroffener Spielfeldbegrenzungen ungerade
				    			// => Die Spiegelachse wird gelöscht und die Verschiebung erhöht
				    			else {
				    				// War die Spiegelachse vorher die obere Spielfeldbegrenzung,
				    				// muss der Punkt um 2 Spielfeldhöhen nach oben verschoben werden,
				    				// um in der Punktewolke der "übersetzten" Funktion zu liegen
				    				if (spiegelachse == 0)
				    					verschiebung -= 118;
				    				else
				    					verschiebung += 118;
				    				spiegelachse = -1;
				    			}
				    			
				    		}
				    		// Aktuelle vertikale Richtung zwecks späteren Vergleichs merken
							vorherigesNachUnten = nachUnten;
	    				}
	    			}
	    		}
    		}
    		
    		// Für die Berechnung der Steigung und der Verschiebung auf der Y-Achse wird
    		// die aktuelle Y-Koordinate nun entspiegelt und neu eingetragen, wobei der wirkliche Wert überschrieben wird
    		int[] curKoordsUn = {x, ungespiegelt(y)};
    		koordsUn.remove(koordsUn.size()-1);
    		koordsUn.add(curKoordsUn);
    		
    		// Berechnung der Steigung und der Verschiebung auf der Y-Achse
    		calcMB();
        	
    		// Berechnung ders Landepunktes des Balls
        	landepunkt = calcLandepunkt();
        }
        // Aktuelle Punkte zwecks späteren Vergleichs merken
        vorherigesX = ball.xKoordinate();
        vorherigesY = ball.yKoordinate();
    }
    
    private void bewegung(){
    	// Ist der Ball ankommend und ist die Flugbahn berechnet worden, bewege die Schlägermitte in Richtung des Landepunktes
    	if (zuMir && !ersteRechnung){
    		if (me.yKoordinate() + 3 < (int) Math.round(landepunkt)){
    			zug.nachUnten();
    			return;
    		}
    		else if (me.yKoordinate() + 3 > (int) Math.round(landepunkt)){
    			zug.nachOben();
    			return;
    		}
    	// Ist die Flugbahn nicht berechnet worden oder ist der ball nicht ankommen, bewege die Schlägermitte auf mittlere Höhe
    	} else if (me.yKoordinate() + 3 < 32){
    		zug.nachUnten();
    		return;
    	}
    	else if (me.yKoordinate() + 3 > 33) {
    		zug.nachOben();
    		return;
    	}
    }
    
    // Entspiegelung
    private int ungespiegelt(int y){
    	if (spiegelachse != -1){
			// Abstand zur Spiegelachse
	    	int aZS = y - spiegelachse;
			y += aZS * -1 * 2;
		}
    	y += verschiebung;
    	return y;
    }

    private double calcLandepunkt(){
    	// Ziel-X-Koordinate aussuchen, dann Y-Koordinate an dieser Stelle ausrechnen
    	int ziel;
    	if (zuMir)
    		ziel = 0;
    	else
    		ziel = 64;
    	
    	// Landepunkt erstmals berechnen
    	double landepunkt = m * ziel + b;
    	
    	// Falls ausgrerechnete Y-Koordinate außerhalb des Spielfeldes (0-59) liegt, bedeutet dies, dass der Ball vor dem Erreichen der Ziel-X-Koordinate
    	// noch mindestens einmal an einer horizontalen Spielfeldbegrenzung abprallt
    	double tempM = m, tempB = b;
    	// Solange der Landepunkt nicht innerhalb des Spielfeldes liegt
    	while (landepunkt < 0 || landepunkt > 59){
    		
    		// Bestimmen, an welcher Spielfeldbegrenzung der Ball abprallen wird
    		double limit;
    		if (landepunkt < 0)
    			limit = 0;
    		else
    			limit = 59;
    		
    		// Funktion spiegeln: Steigung mit -1 multiplizieren und Verschiebung auf der Y-Achse an entsprechender Spiegelachse spiegeln
    		tempM = tempM*-1;
    		double abstand = tempB - limit;
    		tempB += abstand * -1 * 2;
    		
    		// Landepunkt mit gespiegelter Funktion erneut bestimmen
    		landepunkt = tempM * ziel + tempB;
    	}
    	// Endgültigen Landepunkt zurückgeben
    	return landepunkt;
    }

    private void calcMB(){
    	// Erster Wert wird als Nullpunkt betrachtet
    	// => Alle Werte werden verschoben
    	
    	// Summe Xi berechnen
    	int summeX = 0;
    	for (int[] curKoords : koordsUn)
    		summeX += curKoords[0];
    	
    	// Summe Yi berechnen
    	int summeY = 0;
    	for (int[] curKoords : koordsUn)
    		summeY += curKoords[1];
    	
    	// Summe XiYi berechnen
    	int summeXY = 0;
    	for (int[] curKoords : koordsUn)
    		summeXY += curKoords[0] * curKoords[1];
    	
    	// Summe Xi² berechnen
    	int summeXQuadrat = 0;
    	for (int[] curKoords : koordsUn)
    		summeXQuadrat += Math.pow(curKoords[0],2);
    	
    	
    	// Erklärung siehe Dokumentation
    	double c = koordsUn.size() * summeXQuadrat - Math.pow(summeX, 2);
    	m = (koordsUn.size() * summeXY - summeX * summeY) / c;
    	b = (summeXQuadrat * summeY - summeX * summeXY) / c;
    }

}

