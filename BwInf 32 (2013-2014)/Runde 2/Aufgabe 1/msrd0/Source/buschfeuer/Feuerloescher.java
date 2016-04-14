package buschfeuer;

import java.awt.Point;
import java.awt.event.KeyEvent;

import buschfeuer.impl.Raster;

/**
 * Diese abstrakte Klasse mus jeder Feuerlöscher erweitern, ansonsten wird der Simulator
 * mit einer ClassCastException abbrechen.
 * @author Dominic S. Meiser
 */
public abstract class Feuerloescher
{
	/**
	 * Diese Methode wird nach dem Konstruktor aufgerufen, bevor mit der delete-Methode
	 * die Ergebnisse abgefragt werden. In dieser Methode sollten Algorithmen stehen, die
	 * ausgeführt werden müssen, bevor die delete-Methode ein Ergebnis zurückliefern kann.
	 * @param raster Das Raster das der Feuerlöscher löschen soll.
	 */
	public void init (Raster raster) {}
	/**
	 * Diese Methode gibt den Punkt zurück, der als nächstes gelöscht werden soll. Wenn
	 * null zurückgegeben wird, wird nichts gelöscht.
	 * @param raster Das Raster das der Feuerlöscher löschen soll.
	 */
	public abstract Point delete (Raster raster);
	
	/**
	 * Diese Methode gibt farbigen Text aus, der von der bash dargestellt werden kann.
	 * Der String wird dabei wie folgt ausgegeben: "\e["+options+"m"+text+"\e[0m"
	 */
	public static void printBashText (String text, String options)
	{
		System.out.print(((char)KeyEvent.VK_ESCAPE)+"["+options+"m"+text+
				((char)KeyEvent.VK_ESCAPE)+"[0m");
	}
}
