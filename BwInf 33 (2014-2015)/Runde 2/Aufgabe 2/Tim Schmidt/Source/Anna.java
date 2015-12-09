package org.timschmidt.bwinf.a2;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

public class Anna extends Spieler {

	public Anna() {
		super(new Color(255,216,0,100), "Anna");
	}

	@Override
	void zug() {
		Main.gui.enableUI();
		
		// Abbrechen, wenn Feld leer
		int nKegel = 0;
		double yKegel = 0;
		for (Kegel k : Main.kegellist){
			if (!k.umgeworfen()){
				nKegel++;
				yKegel = k.getY();
			}
		}
		if (nKegel == 1){
			setZielWinkel(new Point2D.Double(0,yKegel)), 90);
			return;
		} else if (nKegel == 0){
			setZielWinkel(new Point2D.Double(0,0), 90);
			return;
		}
		
		double[] calcedMB = Utility.calcMB(Main.kegellist);
		List<Kegel> eigenListe = Utility.cloneKegellist(Main.kegellist);
		
		double highCount = Utility.innenkegelzaehlen(eigenListe, calcedMB[0], calcedMB[1]),
				highM = calcedMB[0], highB = calcedMB[1];
		
		// Solange unerreichbare Kegel bei der Regression ignorieren, bis kein weiterer, beachteter Kegel mehr unerreichbar ist; beste Möglichkeit merken
		while(Utility.aussenkegelEntfernen(eigenListe, calcedMB[0], calcedMB[1])){
			calcedMB = Utility.calcMB(eigenListe);
			if (Utility.innenkegelzaehlen(eigenListe, calcedMB[0], calcedMB[1]) > highCount){
				highM = calcedMB[0];
				highB = calcedMB[1];
			}
		}	
		
		double winkel = Utility.m2AngleD(highM);
		Point2D.Double ziel = Utility.findZiel(highM, highB);
		
		setZielWinkel(ziel, winkel);
		// Der eigentlich Zug wird durch das GUI durchgeführt
	}
	
	private void setZielWinkel (Point2D.Double ziel, double winkel){
		Main.curZiel = ziel;
		Main.curWinkel = winkel;
		Main.gui.re //TODO: Refresh tb
	}

}