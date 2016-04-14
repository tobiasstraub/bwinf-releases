package game;

/**
 * Diese Klasse ist eine einfache Implementation der KI. Sie kann als
 * Grundlage für jede KI dienen. Jede Unterklasse muss die folgenden
 * Methoden implementieren:
 * <ul>
 *   <li>vor(): Der Zug, der als Vortänzer erwünscht ist
 *   <li>nach(): Der Zug der KI als Nachtänzer
 * </ul>
 * @author Dominic S. Meiser
 */
public abstract class AbstractKI
{
	/** Einfache Implementation. Die Methode wird in vor() und nach() aufgesplittet. */
	public void zug (int id, Spiel.Zustand zustand, Spiel.Zug zug)
	{
	    if (ichBinVortaenzer(zustand, id))
	        vor(id, zustand, zug);  
	    else
	        nach(id, zustand, zug, this.tanzZumNachtanzen(zustand));
	}
	
	/** Der Zug als Vortänzer. */
	public abstract void vor (int id, Spiel.Zustand zustand, Spiel.Zug zug);
	/** Der Zug als Nachtänzer. */
	public abstract void nach (int id, Spiel.Zustand zustand, Spiel.Zug zug, String tanz);
	   
	/** Gibt das eigene Spielobject zurück. */
	public Spiel.Zustand.DanceRobot getMe (int id, Spiel.Zustand zustand)
	{
	    for (Spiel.Zustand.DanceRobot dr : zustand.listeDanceRobot())
	        if (dr.identifikation() == id)
	            return dr;
	       return null;
	}
	   
	/** Gibt an, ob ich Vortänzer bin. */
	public boolean ichBinVortaenzer (Spiel.Zustand zustand, int id)
	{
	    return getMe(id, zustand).istVortaenzer();
	}
	   
	/** Gibt den letzten Tanz des Vortänzers zurück. */
	public String tanzZumNachtanzen (Spiel.Zustand zustand)
	{
	    for (Spiel.Zustand.DanceRobot dr : zustand.listeDanceRobot())
	        if (dr.istVortaenzer())
	            return dr.letzterTanz();
	    return null;
	}
}
