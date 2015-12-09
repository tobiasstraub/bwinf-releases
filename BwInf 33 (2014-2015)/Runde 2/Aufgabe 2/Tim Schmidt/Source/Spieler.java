package org.timschmidt.bwinf.a2;

import java.awt.Color;

public abstract class Spieler {

	private Color bahnColor;
	private int punkte;
	private String name;
	
	public Spieler(Color bahnColor, String name){
		this.bahnColor = bahnColor;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getBahnColor(){
		return bahnColor;
	}
	
	abstract void zug();

	public int getPunkte() {
		return punkte;
	}

	public void addPunkte(int punkte) {
		this.punkte+= punkte;
	}
}
