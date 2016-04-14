package power2;

import java.util.Arrays;
import java.util.List;

/**
 * Diese Klasse stellt eine Lösung dar. Der Tanz kann escaped oder unescaped übergeben
 * werden, die werden immer mit der Methode <code>getTanz()</code> einen Unescapten und
 * mit <code>getEscapedTanz()</code> einen Escapten Tanz zurückbekommen. <code>getRating()</code>
 * liefert die Anzahl der Strafpunkte für diese Tanz-Vortanz-Kombination zurück. Das
 * Rating wird berechnet, sobald die Solution erstellt wird. Dies geschieht in einem
 * eigenen Thread. Wenn dieser noch nicht fertig ist und Sie getRating() aufrufen wird
 * gewartet bis dieser fertig ist.
 * 
 * @author Dominic S. Meiser
 */
public class Solution implements Runnable
{
	
	/**
	 * Dieser Konstruktor erzeugt eine neue Solution und startet einen Thread, der die
	 * Strafpunkte berechnet. Das Ergbenis können sie mit getRating() abrufen. Der Vortanz
	 * muss unescaped sein, beim Nachtanz ist dies egal.
	 */
	public Solution (String tanz, String vortanz)
	{
		this(tanz, vortanz, false);
	}
	/**
	 * Dieser Konstruktor erzeugt eine neue Solution und startet einen Thread, der die
	 * Strafpunkte berechnet. Das Ergbenis können sie mit getRating() abrufen. Der Vortanz
	 * muss unescaped sein, beim Nachtanz ist dies egal. Sollte escaped jedoch auf true
	 * stehen wird davon ausgegangen das der Nachtanz escaped vorliegt; ist escaped=false
	 * ist es egal in welchem Format der Nachtanz vorliegt.
	 */
	public Solution (String tanz, String vortanz, boolean escaped)
	{
		str = tanz; vor = vortanz;
		if (escaped) this.escaped = tanz;
		new Thread(this, "Solution").start();
	}
	
	/** Der (unescapte) Nachtanz. */
	private String str;
	/** Der (escapte) Nachtanz.*/
	private String escaped;
	/** Der Vortanz, muss unescaped sein. */
	private String vor;
	/** Das Rating, am Anfang -1. */
	private int rating = -1;
	
	
	/**
	 * Eine Klasse, die die aktuelle Position eines Spiel.Zustand.DanceRobot speichert und die Bewegungsrichtung.
	 * Außerdem steht noch eine tm-Variable namens <code>mvpos</code> zur Verfügung.
	 * 
	 * @author Dominic S. Meiser
	 */
	private class PositionDescriptor
	{
		int x, y, b;
		int mvpos = 0;
		public PositionDescriptor (int x, int y, int bewegung)
		{
			this.x = x; this.y = y;
			b = bewegung;
		}
	}
	
	
	/**
	 * Diese Methode berechet die Strafpunkte. Diese können anschließend über getRating()
	 * abgerufen werden.
	 */
	public void run ()
	{
		// Strings escapen und unescapen
		if (escaped == null) escaped = Escaper.escape(str);
		str = Escaper.unescape(escaped);
		
		if (str.length() > 255) str = str.substring(0, 255);
		if (escaped.length() > 255) escaped = escaped.substring(0, 255);
		
		// Strafpunkte des Immitats
		int strafpunkte = escaped.length()*3;
		
		// Position des Imitats
		int xi = 0, yi = 0;
		// Bewegubg des Imitats im Uhrzeigersin
		int bi = 0;
		
		// Position des Originals
		int xo = 0, yo = 0;
		// Bewegung des Originals im Uhrzeigersinn
		int bo = 0;
		
		// Positionen umformen
		PositionDescriptor imitat = new PositionDescriptor (xi, yi, bi);
		PositionDescriptor original = new PositionDescriptor (xo, yo, bo);
		
		// Positionen berechnen und dabei Strafpunkte berechnen
		boolean continuei = true;
		boolean continueo = true;
		while (continuei || continueo)
		{
			if (continuei) continuei = move(str, imitat);
			if (continueo) continueo = move(vor, original);
			
//			strafpunkte += Math.max(Math.abs(imitat.x-original.x), Math.abs(imitat.y-original.y));
			strafpunkte += Math.abs(imitat.x-original.x);
			strafpunkte += Math.abs(imitat.y-original.y);
		}
		
		// Strafpunkte speichern
		rating = strafpunkte;
	}
	
	/**
	 * Bewegt die Positionin <code>pos</code> um das Zeichen tanz.charAt(pos.mvpos) weiter.
	 * Siehe {@link Solution#move(int, int, PositionDescriptor)}
	 */
	private boolean move (String tanz, PositionDescriptor pos)
	{
		if (pos.mvpos >= tanz.length()) return false;
		
		char c = tanz.charAt(pos.mvpos);
		switch (c)
		{
			case 'F': move(0, -1, pos); break;
			case 'B': move(0, 1, pos); break;
			case 'r': pos.b--; if (pos.b < 0) pos.b = 3; break;
			case 'l': pos.b++; if (pos.b > 3) pos.b = 0; break;
		}
		
		pos.mvpos++;
		return true;
	}
	
	/**
	 * Bewegt die Position um (x|y) weiter. Dabei wird die Richtung in pos.b beachtet.
	 */
	private void move (int x, int y, PositionDescriptor pos)
	{
		int real_x = x;
		int real_y = y;
		switch (pos.b)
		{
			case 1: real_y = x; real_x = y*-1; break;
			case 2: real_y = y*-1; real_x = x*-1; break;
			case 3: real_y = x*-1; real_x = y; break;
		}
		pos.x += real_x;
		pos.y += real_y;
	}
	
	
	/**
	 * Gibt die Anzahl der Strafpunkte zurück.
	 */
	public int getRating ()
	{
		while (rating == -1) Thread.yield();
		return rating;
	}
	
	/**
	 * Gibt den unescapten Nachtanz zurück.
	 */
	public String getTanz ()
	{
		return str;
	}
	
	/**
	 * Gibt den escapten Nachtanz zurück.
	 */
	public String getEscapedTanz ()
	{
		while (escaped == null) Thread.yield();
		return escaped;
	}
	
	/**
	 * Gibt eine Beschreibung dieses Objects zurück. Beachten Sie das rating=-1 bedeutet, das
	 * der Thread, der die Strafpunkte bestimmt, noch nicht fertig ist.
	 */
	public String toString ()
	{
		return getClass().getCanonicalName()+"[tanz="+str+",vortanz="+vor+",rating="+rating+"]";
	}
	
	/**
	 * Gibt die Lösung mit den wenigsten Strafpunkten aus <code>solutions</code> zurück.
	 */
	public static String getBest (Solution ... solutions)
	{
		return getBest(Arrays.asList(solutions));
	}
	/**
	 * Gibt die Lösung mit den wenigsten Strafpunkten aus <code>solutions</code> zurück.
	 */
	public static String getBest (List<Solution> solutions)
	{
		Solution best = null;
		
		for (Solution s : solutions)
		{
			if (s == null) continue;
			if (best == null)
				best = s;
			else if (s.getRating() < best.getRating())
				best = s;
		}
		
		if (best == null)
			return null;
		return best.getEscapedTanz();
	}
}
