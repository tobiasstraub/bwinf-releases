package org.bytesector.bwinf.a1v2;

import java.util.ArrayList;
import java.util.List;

public class Knot {
	private List<Container> col;		// Behälter des Knotens
	private Step step;					// Schritt, mit dem der Knoten erreicht wurde
	
	private List<Knot> subKnots;		// Liste an Unterknoten
	private Knot solKnot;				// Nächster Knoten in der Kette, die zur Lösung kam
	
	public static List<String> foundKnots = new ArrayList<String>();	// Programmweite Liste aller gefundenen Kombinationen
	public static List<Step> solSteps = new ArrayList<Step>();			// Lösungsschritte
	
	
	public Knot(List<Container> col, Step step){
		// Konstruktorparameter auf Eigenschaten übertragen
		this.col = col;
		this.step = step;
		
		// Liste an Unterknoten und Unterknoten, der zur Lösung führt, initialisieren
		subKnots = new ArrayList<Knot>();
		solKnot = null;
	}
	
	// Methode zum Erstellen von Unterknoten
	// Gibt true zurück, falls eine Lösung gefunden wurde, sonst false
	public boolean goDown(){
		// Falls dieser Knotenpunkt noch keine Unterknoten hat, werden neue erstellt
		if (subKnots.isEmpty()){
			// Es werden alle Umfüllprozesse aller Gefäße untereinander bearbeitet
			for (Container con : col)
				for (Container con2 : col){
					// Nicht ausführen, falls Quell- und Zielgefäß der selbe sind, der Quellbehälter leer oder der Zielbehälter voll ist
					if (con != con2 && con.getFill() > 0 && con2.getFill() != con2.getSize()){
						
						// Sammlung an Gefäßen klonen, damit Änderungen sich nicht auf diesen Knoten auswirken
						List<Container> _col = new ArrayList<Container>();
						for (Container con3 : col)
							_col.add(con3.clone());
						
						// Führe die Umfüllung mit den neuen Gefäßen durch, erreiche sie über den selben Index wie die Originialgefäße
						_col.get(col.indexOf(con)).transfer(_col.get(col.indexOf(con2))); 
						
						// Überprüfe, ob Kombination an Füllständen bereits vorkam
						boolean testPassed = true;
						String sumCol = summarizeCol(_col);
						for (String str : foundKnots){
							if (str.equals(sumCol))
								testPassed = false;
						}
						
						// Falls Kombination neu:
						if (testPassed){
							// Festhalten, dass Fortschritt gemacht wurde
							Main.setProgress(true);
							
							// Neue Kombination eintragen
							foundKnots.add(sumCol);
							
							// Neuen Knoten mit geklonten Gefäßen erstellen
							Knot _knot = new Knot(_col, new Step(con, con2, _col));
							subKnots.add(_knot); 
							
							// Wenn erstellter Knoten den Zielzustand darstellt, als solcher speichern und die Kette hochfahren
							if (_knot.checkState()){
								solKnot = _knot;
								return true;
							}
						}
					}
				}
		} 
		// Falls dieser Knotenpunkt bereits Unterknoten besitzt, wird der Befehl weitergeleitet
		else {
			// Alle Unterknoten durchgehen
			for (Knot knot : subKnots)
				// Befehl weitergeben und falls einer der Unterknoten auf eine Lösung gestoßen ist, diesen als solcher speichern und die Kette weiter hochfahren
				if (knot.goDown()){
					solKnot = knot;
					return true;
				}
		}
		return false;
	}
	
	// Überprüfen, ob faire Verteilung bereits gegeben
	public boolean checkState(){
		int wineA = 0, wineB = 0;
		for (Container con : col)
			if (con.getOwner())
				wineA += con.getFill();
			else
				wineB += con.getFill();
		
		return wineA == wineB;
	}
	
	// Eigenen und unterliegende Schritte hochgeben
	public void getSteps(){
		solSteps.add(step);
		
		if (solKnot != null)
			solKnot.getSteps();
	}
	
	// Fasst die Füllungen der aktuellen Behälter in einem String zusammen
	public String summarizeCol(){
		String _ = "";
		for (Container con : col)
			_ += con.getFill() + " ";
		return _;
	}
	
	// Fasst die Füllungen einer gegebenen Sammlung an Behältern in einem String zusammen
	static public String summarizeCol(List<Container> col){
		String _ = "";
		for (Container con : col)
			_ += con.getFill() + " ";
		return _;
	}
	
}
