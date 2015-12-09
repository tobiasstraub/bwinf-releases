package org.timschmidt.bwinf.a3;

public class Knoten {

	/* Statt den String einer Kante explizit abzuspeichern, genügt es, die Indizes des Anfangs und des Endes (beides inklusiv) des Vorkommnisses dieses Strings im input zu notieren
	 * Beispiel:
	 * 	String: "acc"
	 * 		Kante "acc" => Kante [0,2]
	 * 		Kante "cc" => Kante [1,2]
	 * 
	 * Hierdurch ist der Speicherverbrauch für einen Knoten konstant für Alphabete konstanter Länge: 1 Int-Array[2] + 2 Pointer auf Knoten + 1 Knotenarray[alphabetlänge] + Java-spezifischer Speicher für eine Instanz
	 */
	
	private static int e;
	
	private int[] vorKante;
	private Knoten eltern, sufLink;
	private Knoten[] kinder;
	
	public Knoten(int vorkanteAnfang, int vorkanteEnde, Knoten eltern) {
		this.eltern = eltern;
		kinder = new Knoten[Main.ZEICHENSATZ.length()];
		vorKante = new int[2];
		vorKante[0] = vorkanteAnfang;
		vorKante[1] = vorkanteEnde;
	}

	// Anzahl Blätter unter diesem Knoten zählen => Vorkommen des Strings von der Wurzel bis hier
	public int calcVorkommen(String preS){
		String fullString = preS + getVorKante();
		int vorkommen = 1;
		
		for (int i = 0; i < kinder.length; i++){
			if (kinder[i] != null){
				vorkommen += kinder[i].calcVorkommen(fullString);
			}
		}
		
		if (getVorKante().length() >= Main.getL() && vorkommen >= Main.getK())
			Main.repetitionen.add(fullString);
		
		return vorkommen;
	}
	
	// Getter und Setter
	public String getVorKante() {
		if (vorKante[1] == -1)
			return Utility.subS(Main.getInput(), vorKante[0], e);
		else
			return Utility.subS(Main.getInput(), vorKante[0], vorKante[1]);
	}

	public void setVorKante(int anf, int end){
		vorKante[0] = anf;
		vorKante[1] = end;
	}
	
	public void kuerzeVorKante(int shift) {
		vorKante[0] += shift; // Anfangsindex um i erhöhen => Kürzung des Strings auf der Vorkante von input[vorkante[0]...vorkante[1]] auf input[vorkante[0]+shift...vorkante[1]]
	}
	
	public int getVorKanteAnfang() {
		return vorKante[0];
	}

	public Knoten getEltern() {
		return eltern;
	}

	public void setEltern(Knoten eltern) {
		this.eltern = eltern;
	}

	public Knoten getSufLink() {
		return sufLink;
	}

	public void setSufLink(Knoten sufLink) {
		this.sufLink = sufLink;
	}

	public Knoten[] getKinder() {
		return kinder;
	}
	
	public Knoten getKind(int i) {
		return kinder[i];
	}

	public void setKinder(Knoten[] kinder) {
		this.kinder = kinder;
	}
	
	public void setKind(int i, Knoten kind) {
		kinder[i] = kind;
	}
	
	public static void setE(int e){
		Knoten.e = e;
	}
	
	public static int getE(){
		return e;
	}
}
