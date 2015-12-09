package a2;

import java.util.Observable;
import java.util.Random;

public class Model extends Observable{

		private static MapTile[][] world;
		private static Position posNest;
		private static Ameise[] ameisen;
		private static int futterGesammelt = 0,
				zgeInsg = 0;
		
		public Model(int amAmeisen, Position posNest, int amFutterquelle, int tVerdunstung){
			// Alle MapTiles instanzieren
			createWorld(tVerdunstung);
			// Die MapTiles, die als Nest gewählt wurden, werden entsprechend markiert.
			createNest(posNest);
			// amFutterquellen viele zufällig ausgewählte MapTiles mit jeweils 50 Futtereinheiten belegen
			createFutter(amFutterquelle);
			// amAmeisen viele Ameisen mit Position innerhalb des Nestes erstellen
			createAmeisen(amAmeisen);
		}
		
		private void createWorld(int tVerdunstung){
			world = new MapTile[500][500];
			for (int i = 0; i < world.length; i++)
				for (int j = 0; j < world.length; j++)
					world[i][j] = new MapTile(tVerdunstung);
		}
		
		private void createNest(Position posNest){
			Model.posNest = posNest;
			// Es wird die links-obere Ecke des 2x2 Nests als Parameter angegeben
			world[posNest.getX()][posNest.getY()].setNest(true);
			world[posNest.getX()+1][posNest.getY()].setNest(true);
			world[posNest.getX()][posNest.getY()+1].setNest(true);
			world[posNest.getX()+1][posNest.getY()+1].setNest(true);
		}
		
		private void createFutter(int amFutterquelle){
			Random rnd = new Random();
			for (int i = 0; i < amFutterquelle; i++){
				// Position der Futterquelle zufällig bestimmen
				int x, y;
				do {
					x = rnd.nextInt(500);
					y = rnd.nextInt(500);
				} // Position neu berechnen, falls Futterquelle auf Nest generiert werden würde
				while ((x == posNest.getX() || x == posNest.getX() + 1) && (y == posNest.getY() || y == posNest.getY() + 1));
				
				// Futterquelle füllen
				world[x][y].setFutter(50);
			}
		}

		private void createAmeisen(int amAmeisen){
			ameisen = new Ameise[amAmeisen];
			Random rnd = new Random();
			for (int i = 0; i < ameisen.length; i++){
				// Ameisen zufällig auf das 2x2-Nest verteilen (rnd.nextInt(2) erzeugt 0 oder 1)
				ameisen[i] = new Ameise(new Position(posNest.getX() + rnd.nextInt(2), posNest.getY() + rnd.nextInt(2)));
			}
		}
		
		public void zugAusführen(){
			// Verdunstung simulieren
			for (MapTile[] i : world)
				for (MapTile j : i)
					j.decayDuft();
			
			// Ameisen agieren lassen
			for (Ameise a : ameisen)
				a.zug();
			
			// Anzahl an gemachten Zügen erhöhen
			zgeInsg++;
			
			// Controller informieren, dass Zug beendet wurde
			setChanged();
			notifyObservers();
		}
		
		public static MapTile[][] getWorld() {
			return world;
		}

		public static Position getPosNest() {
			return posNest;
		}
		
		public static Ameise[] getAmeisen(){
			return ameisen;
		}
		
		public static int getFutterGesammelt() {
			return futterGesammelt;
		}

		public static void incFutterGesammelt() {
			Model.futterGesammelt++;
		}

		public static int getZgeInsg() {
			return zgeInsg;
		}

}
