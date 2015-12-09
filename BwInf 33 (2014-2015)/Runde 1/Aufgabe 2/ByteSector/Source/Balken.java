package org.bytesector.bwinf.a2;

import java.util.List;

public class Balken extends Gewicht{
	private List<Gewicht> subelemente;
	private int[] positionen;
	
	public Balken(List<Gewicht> sub, int[] pos){
		// Summe aller Massen aller Unterelemente als Parameter für den Konstrukter der Basisklasse Gewicht
		super(massenAddieren(sub));
		
		// Konstruktorparameter auf Eigenschaten übertragen
		subelemente = sub;
		positionen = pos;
	}
	
	/**Addiert alle Masse aller Gewichte in einer Liste.*/
	static private int massenAddieren(List<Gewicht> sub){
		int masse = 0;
		for (int i = 0; i < sub.size(); i++){
			masse += sub.get(i).getMasse();
		}
		return(masse);
	}
	
	public List<Gewicht> getSubelemente() {
		return subelemente;
	}

	public int[] getPositionen() {
		return positionen;
	}
	
	
}
