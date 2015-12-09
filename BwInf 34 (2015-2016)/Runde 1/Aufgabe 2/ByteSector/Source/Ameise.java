package a2;

import java.util.Random;

public class Ameise {
	private Position pos;
	private boolean hatFutter;
	private int nestNearX, nestNearY;
	private static Random rnd = new Random();
	
	public Ameise(Position pos){
		hatFutter = false;
		this.pos = pos;
	}
	
	/** Markiert die Ameise als beladen und reduziert die Anzahl an Futtereinheiten auf dem aktuellen Feld um eins. **/
	private void aufheben(MapTile curTile){
		curTile.redFutter();
		hatFutter = true;
	}
	
	/** Markiert die Ameise als unbeladen und erhöht die Anzahl an gesammelten Futtereinheiten um eins. **/
	private void ablegen(){
		hatFutter = false;
		Model.incFutterGesammelt();
	}
	
	/** Erhöht die Duftintensität auf dem aktuellen Feld um eins. **/
	private void duftAbgeben(MapTile curTile){
		curTile.incDuft();
	}
	
	/** Findet das nächste Feld des 2x2 Nestes **/
	private void findNestNear(){
		// Falls Ameise links vom Nest, linke Seite ansteuern und vice versa
		if (pos.getX() < Model.getPosNest().getX())
			nestNearX = Model.getPosNest().getX();
		else
			nestNearX = Model.getPosNest().getX() + 1;
		// Falls Ameise über Nest, obere Seite ansteuern und vice versa
		if (pos.getY() < Model.getPosNest().getY())
			nestNearY = Model.getPosNest().getY();
		else
			nestNearY = Model.getPosNest().getY() + 1;
	}
	
	public void zug(){
		// Aktuelles Kartenfeld merken
		MapTile curTile = Model.getWorld()[pos.getX()][pos.getY()];
		
		// Laufrichtung notieren
		int dx = 0, dy = 0;
		
		// Nächstes Nest-Feld finden
		findNestNear();
		
		// Verhalten für Ameise ohne Futter
		if (!hatFutter){			
			// Falls Ameise bereits auf Futterplatz, Futter aufheben und Zug beenden
			if (curTile.getFutter() > 0){
				aufheben(curTile);
				return;
			}
			
			// Wenn kein Futter gefunden wurde, umliegende Felder untersuchen
			MapTile high = null;
			
			// Überprüfen, ob umliegende Felder existieren. Felder, die näher ans Nest führen, werden ignoriert.
			MapTile tileLeft = null,
					tileRight = null,
					tileUp = null,
					tileDown = null;
			// Nur links in Betracht ziehen, wenn das Nest auf gleicher x-Koordinate oder rechts davon liegt.
			if (pos.getX() > 0 && pos.getX() - nestNearX <= 0)
				tileLeft = Model.getWorld()[pos.getX() - 1][pos.getY()];
			if (pos.getX() < 499 && pos.getX() - nestNearX >= 0)
				tileRight = Model.getWorld()[pos.getX() + 1][pos.getY()];
			if (pos.getY() > 0 && pos.getY() - nestNearY <= 0)
				tileUp = Model.getWorld()[pos.getX()][pos.getY() - 1];
			if (pos.getY() < 499 && pos.getY() - nestNearY >= 0)
				tileDown = Model.getWorld()[pos.getX()][pos.getY() + 1];
			
			
			// Existierende umliegende Felder untersuchen
				// Nur untersuchen, wenn Feld überhaupt existiert. Wenn noch kein anderes passendes
				// Feld gefunden wurde, nur prüfen, ob Duftpunkt vorhanden sind.
				// Für das erste Feld (tileLeft) kann die Überprüfung verkürzt werden, da noch kein
				// anderes passendes Feld gefunden worden sein kann.
			if (tileLeft != null && tileLeft.getDuft() > 0){
				high = tileLeft;
				dx = -1;
			}
			if (tileRight != null && 
					((high == null && tileRight.getDuft() > 0) ||
					(high != null && tileRight.getDuft() > high.getDuft()))){
				high = tileRight;
				dx = +1;
			}
			if (tileUp != null &&
					((high == null && tileUp.getDuft() > 0) ||
					(high != null && tileUp.getDuft() > high.getDuft()))){
				high = tileUp;
				dx = 0; dy = -1;
			}
			if (tileDown != null &&
					((high == null && tileDown.getDuft() > 0) ||
					(high != null && tileDown.getDuft() > high.getDuft()))){
				high = tileDown;
				dx = 0; dy = +1;
			}
			
			// Falls keine Duftpunkte gefunden wurden, zufällig bewegen
			if (high == null){
				do {
				// x- oder y-Richtung
				if (rnd.nextBoolean())
					// Links oder Rechts
					if (rnd.nextBoolean())
						dx = -1;
					else
						dx = +1;
				else 
					// Oben oder Unten
					if (rnd.nextBoolean())
						dy = -1;
					else
						dy = +1;
				}
				// Falls die Zufallsbewegung über die Weltgrenzen führen würde, Bewegung neu berechnen
				while (!(0 <= pos.getX() + dx && pos.getX() + dx <= 499 &&
						0 <= pos.getY() + dy && pos.getY() + dy <= 499));
			}		
		}
		
		
		// Verhalten für Ameise mit Futter
		else {
			// Falls die Ameise bereits auf dem Nest angekommen ist, Futter ablegen und Zug beenden
			if (curTile.isNest()){
				ablegen();
				return;
			}
			
			// Sonst Duftpunkt legen
			duftAbgeben(curTile);
			
			// Da Ameisen nicht diagonal laufen können, macht es keinen Unterschied, ob ein gerader Weg zum Nest gewählt
			// oder zuerst nur auf einer Dimension in Richtung Nest gelaufen wird.
			
			// Richtung (x oder y) mit größerer Differenz zuerst korrigieren
			if (Math.abs(pos.getX() - nestNearX) > Math.abs(pos.getY() - nestNearY))
				// Ein Feld in die entsprechende Richtung laufen
				dx = (int)Math.copySign(1, nestNearX - pos.getX());
			else
				dy = (int)Math.copySign(1, nestNearY - pos.getY());
		}
		
		// Zum gewählten Feld laufen
		pos.setX(pos.getX() + dx);
		pos.setY(pos.getY() + dy);
	}
	
	public Position getPos(){
		return pos;
	}
	
	public boolean hatFutter(){
		return hatFutter;
		
	}
}
