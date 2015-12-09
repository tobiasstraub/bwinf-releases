package org.bytesector.bwinf.a1v2;

public class Container {
	
	// Die Eigenschaften Volumen (=size) und Füllmenge (=fill), sowie eine numerische ID, um die Gefäßen in der Ausgabe unterscheiden zu können
	private int size, fill, id;
	// Der Besitzer wird als boolean gespeichert, wobei gilt: True = Person A; False = Person B
	private boolean owner;
	
	public Container(int size, int fill, boolean owner, int id) throws Exception{
		// Gib' Fehler aus, falls der Behälter nach der Erstellung überfüllt sein würde
		if (fill > size){
			throw new Exception("Behälter überfüllt, breche ab.");
		}
		
		// Konstruktorparameter auf Eigenschaten übertragen
		this.size = size;
		this.fill = fill;
		this.owner = owner;
		this.id = id;
	}
	
	// Methode zum Umfüllen in einen anderen Behälter
	public void transfer(Container con){
		// Füllstand wird auf den Rückgabewert der 'add'-Methode gesetzt (siehe unten)
		fill = con.add(fill);
	}
	
	// Fügt dem Behälter möglichst viel von der angegebenen Menge an Flüssigkeit hinzu und gibt das übrige Volumen zurück
	public int add(int am){
		// Das letztendlich aufgenommene Volumen an Flüssigkeit (=actual) ist maximal der verbleibende Platz (Differenz aus Volumen und Füllmenge)
		int actual = Math.min(size-fill, am);
		// Füllstand erhöhen. Ist der Behälter voll, ist actual 0
		fill += actual;
		// Übrig gebliebene Flüssigkeit zurückgeben
		return am - actual;
	}
	
	
	// Getter-Methoden
	public int getSize(){
		return size;
	}
	
	public int getFill(){
		return fill;
	}
	
	public boolean getOwner() {
		return owner;
	}
	
	public int getID(){
		return id;
	}
	
	
	// Methode zum Klonen des Objektes, welche beim Erstellen neuer Gefäßsammlungen für Unterknoten verwendet wird
	public Container clone(){
		Container conN = null;
		try {
			conN = new Container(size, fill, owner, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conN;
	}

}
