/**
 * generated file.
 * sources: [Solution.java, DancePattern.java, Compleater.java, Escaper.java, Power2KI.java]
 * mainfile (changed to AI): Power2KI
 */

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.LinkedList;



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
abstract class AbstractKI
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
class Solution implements Runnable
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


/**
 * Diese Klasse repräsentiert ein Teil eines Tanzes. Es speichert ein Pattern und
 * die Anzahl der Auftritte im String. Mit der escape()-Methode kann man ein neues
 * DancePattern erstellen das nur Zahlen bis 9 enthält. Die alte Anzahl von Auftritten
 * wird immernoch in insgAppearment gespeichert.
 * 
 * @author Dominic S. Meiser
 */
class DancePattern
{
	/** Das Pattern. */
	public String pattern;
	/** Die Länge des Pattern, wie pattern.length(). */
	public int length;
	/** Die Anzahl der Auftritte des Pattern im Tanz. */
	public int appearment, insgAppearment;
	
	/** Initialisiert das DancePattern (macht eig nix). */
	public DancePattern ()
	{
		pattern = "";
		appearment = 1;
	}
	
	/** Initialisiert das DancePattern mit den angegebenen Werten. */
	public DancePattern (String pattern, int appearment)
	{
		this.pattern = pattern;
		this.length = pattern.length();
		this.appearment = appearment;
		this.insgAppearment = appearment;
	}
	
	/** Gibt eine kurze Beschreibung des Pattern zurück. */
	public String toString ()
	{
		return getClass().getCanonicalName()+"[pattern="+pattern+";appearment="+appearment+"]";
	}
	
	
	/**
	 * Gibt das längste Pattern aus der angegebenen Liste zurück. Die Liste wird dafü nicht sortiert.
	 * 
	 * @param list Die Liste aus der das längste Pattern herausgesucht werden soll.
	 * @return Das längste Pattern aus der Liste.
	 */
	public static DancePattern getLongest (List<? extends DancePattern> list)
	{
		int longest=-1, index=-1;
		for (int i = 0; i < list.size(); i++)
		{
			// aktuelles Pattern heraussuchen
			DancePattern p = list.get(i);
			// Pattern auf Validität prüfen
			if (!Pattern.matches("[0-9]{0,}[FBrl\\-]{1,}[\\.]{0,}", p.pattern)) continue;
			
			// wenns länger als das bis jetzt längste gefundene ist als längstes markieren
			if ((p.length > longest) && (((p.appearment > 1) && (p.length > 2)) || (p.appearment > 2)))
			{
				longest = p.length;
				index = i;
			}
		}
		
		// das längste zurückgeben
		if (index == -1) return null;
		return list.get(index);
	}
	
	
	
	/**
	 * Berechnet die beste Kombination an Zahlen für ein Pattern, das länger als 9 ist. Das ursprüngliche
	 * Pattern bleibt komplett erhalten; sowie das allte appearment in insgAppearment.
	 */
	public static DancePattern escape (DancePattern pattern)
	{
		// muss das Pattern escaped werden?
		if ((pattern == null) || (pattern.appearment <= 9)) return pattern;
		
		if (pattern.appearment == 10)
		{
			pattern.appearment--;
			pattern.insgAppearment--;
			return pattern;
		}
		
		// neues Pattern bereitstellen
		DancePattern p = new DancePattern ();
		p.insgAppearment = pattern.insgAppearment;
		
		// die anfängliche Anzahl der Zahlen bestimmen
		int length;
		for (length = 1; Math.pow(9, length) < pattern.appearment; length++);
		int initlength = length;
		
		// Zahlen suchen
		int[] num = new int[length];
		while (!findNum(pattern.appearment, num, 0))
		{
			pattern.appearment--;
			p.insgAppearment--;
			for (length = 1; Math.pow(9, length) < pattern.appearment; length++);
			if (length != num.length) num = new int[length];
		}
		
		// Das Pattern erstellen
		for (int i = ((num[0] == 2) ? 2 : 1); i < length; i++) p.pattern += num[i]+"";
		p.pattern += pattern.pattern;
		if (num[0] == 2) p.pattern += pattern.pattern;
		p.length = pattern.length;
		for (int i = ((num[0] == 2) ? 2 : 1); i < length; i++) p.pattern += ".";
		p.appearment = num[((num[0] == 2) ? 1 : 0)];
		
		// Wenn das appearment des Patterns verkleinert werden musste, eine andere Lösung
		// bereitstellen, bei der dies (hoffentlich) nicht passieren muss. Sollte diese
		// kürzer sein, diese zurückgeben.
		if (p.insgAppearment < pattern.insgAppearment)
		{
			int[] num0 = findHardNums(pattern.insgAppearment, initlength);
			
			// Das neue Pattern erstellen
			DancePattern p0 = new DancePattern();
			p0.insgAppearment = 1;
			for (int n : num0) p0.insgAppearment *= n;
			
			for (int i = ((num0[0] == 2) ? 2 : 1); i < num0.length; i++) p0.pattern += num0[i]+"";
			p0.pattern += pattern.pattern;
			if (num0[0] == 2) p0.pattern += pattern.pattern;
			p0.length = pattern.length;
			for (int i = ((num0[0] == 2) ? 2 : 1); i < num0.length; i++) p0.pattern += ".";
			p0.appearment = num0[((num0[0] == 2) ? 1 : 0)];
			
			// Wenn p0 kürzer als p+ageschnittenesZeug, p0 zurückgeben
			if (p0.length+2 < p.length+2+
					(DancePattern.escape(
							new DancePattern(pattern.pattern, pattern.insgAppearment-p.insgAppearment)
							).length+2))
				return p0;
		}
		
		return p;
	}
	
	
	/**
	 * Berechnet die Nummern für das angegebene appearment. Sollte dies nicht gehen wird
	 * false zurückgegeben.
	 */
	private static boolean findNum (int appearment, int[] nums, int i)
	{
		if (nums.length == i-1)
		{
			nums[i] = appearment;
			for (int j = 0; j < nums.length-1; j++)
				nums[i] /= nums[j];
			i++;
		}
		
		if (nums.length == i)
		{
			int found = 1;
			for (int num : nums) found *= num;
			if (found == appearment) return true;
			else return false;
		}
		
		for (int num = 2; num <= 9; num++)
		{
			nums[i] = num;
			if (findNum(appearment, nums, i+1)) return true;
		}
		
		return false;
	}
	
	/**
	 * Berechnet die Nummern für das angegebene Appearment, vergrößert aber einfach
	 * die Anzahl der Nummern anstatt wie <code>findNum()</code> false zurückzugeben.
	 * Dabei wird jedoch abgebrochen, sobald die Anzahl der Zahlen größer als initsize+10
	 * ist; dann wird <code>findHardNums(appearment-1, ((initsize > 1) ? initsize-1 : 0))</code>
	 * zurückgegeben.
	 */
	private static int[] findHardNums (int appearment, int initsize)
	{
		for (int i = initsize; i < initsize+3; i++)
		{
			int[] nums = new int[i];
			if (findNum(appearment, nums, 0)) return nums;
		}
		return findHardNums(appearment-1, ((initsize > 1) ? initsize-1 : 0));
	}
}

/**
 * Diese Klasse stellt eine Methode zum vervolständigen eines abgeschnittenen
 * Strings zur Verfügung.
 * 
 * @author Dominic S. Meiser
 */
class Compleater
{
	/**
	 * Diese Methode vervolständigt den abgeschnittenen String <code>escaped</code>
	 * zu einem String mit der maximalen Länge <code>maxlength</code>. Wenn da nur
	 * Stuß rauskommt wird evtl. ein ein wenig längerer String zurückgegebene.
	 * Außerdem ist nicht garantiert das das Ergebnis der Eingebe entspricht.
	 */
	public static String compleate (String escaped, int maxlength)
	{
		// Zuerst einen String mit maximal maxlength Zeichen "abschneiden"
		String str = escaped.substring(0, ((escaped.length() > maxlength) ? maxlength : escaped.length()));
		
		// Diesen, falls noch nicht vorhanden, mit einer "Schleife" versehen
		if (!Character.isDigit(str.charAt(0)) || (str.charAt(str.length()-1) != '.'))
			str = "8"+str.substring(0, ((str.length() < maxlength-2) ? str.length() : maxlength-2))+".";
		// Die Anzahl der Punkte und Zahl vorrübergehend in Ordnung bringen
		for (int i = 0; i <= str.length()-2; i++)
		{
			if (Character.isDigit(str.charAt(i)) && (str.charAt(i+1) == '.'))
			{
//				System.out.println("\tremove: "+str.substring(0, i)+" |- "+str.substring(i, i+2)+" -| "+str.substring(i+2));
				str = str.substring(0, i)+str.substring(i+2);
			}
		}
		
		// Den String durch eine "Schleife" verlängern
		if (Integer.parseInt(str.substring(0, 1)) < 9)
			str = (Integer.parseInt(str.substring(0, 1))+1)+str.substring(1);
		else if (Character.isDigit(str.charAt(1)) && (Integer.parseInt(str.substring(1, 2)) < 9) &&
				(str.charAt(str.length()-2) == '.'))
			str = str.substring(0, 1)+(Integer.parseInt(str.substring(1, 2))+1)+str.substring(2);
		else
			str = "99"+str.substring(1, ((str.length()-2 < 6) ? str.length()-2 : 6))+"..";
		
		// Die Anzahl der Punkte und Nummern letztendlich in Ordnung bringen
		while (countDots(str) > countNums(str))
			str = str.substring(0, str.lastIndexOf('.')) + str.substring(str.lastIndexOf('.')+1);
		for (int i = str.length()-1; (countNums(str) > countDots(str)) && (i > 0); i--)
		{
			if (str.length() < maxlength)
			{
				str += ".";
				i = str.length();
			}
			else
				str = str.substring(0, i)+"."+((i != str.length()) ? str.substring(i+1) : "");
		}
		for (int i = 0; i <= str.length()-2; i++)
		{
			if (Character.isDigit(str.charAt(i)) && (str.charAt(i+1) == '.'))
			{
//				System.out.println("\tremove: "+str.substring(0, i)+" |- "+str.substring(i, i+2)+" -| "+str.substring(i+2));
				str = str.substring(0, i)+str.substring(i+2);
			}
		}
		
		// Wenn der String viel zu lang ist maxlength evtl. etwas vergrößern
		if (((str.length() > 13) || !Escaper.unescape(str).startsWith(Escaper.unescape(escaped)))
				&& (maxlength < 13))
		{
//			System.out.println(" -> compleater: increaing maxlength");
			String str0 = compleate(escaped, maxlength+1);
//			System.out.println("  -> \""+str+"\" -> \""+str0+"\"");
			return str0;
		}
		
//		System.out.println(" -> compleater returns \""+str+"\" for input \""+escaped+"\"");
		return str;
	}
	
	/** Zählt die Anzahl der Zahlen in <code>escapedTanz</code>. */
	private static int countNums (String escapedTanz)
	{
		int count = 0;
		for (char c : escapedTanz.toCharArray())
			if (Character.isDigit(c))
				count++;
		return count;
	}
	
	/** Zählt die Anzahl der Punkte in <code>escapedTanz</code>. */
	private static int countDots (String escapedTanz)
	{
		int count = 0;
		for (char c : escapedTanz.toCharArray())
			if (c == '.')
				count++;
		return count;
	}
}


/**
 * Diese Klasse stellt Methoden zum escapen und unescapen von Tänzen bereit.
 * 
 * @author Dominic S. Meiser
 */
class Escaper
{
	
	/**
	 * Diese Methode escaped einen Tanz.<br><br>Beispiel:
	 * Aus <code>FrFrFrFrFrFrFr</code> wird <code>8Fr.</code>
	 */
	public static String escape (String str)
	{
		if (str == null)
		{
			System.err.println("CANT ESCAPE NULL");
			return "";
		}
		
		if (str.length() <= 3) return str;
		if (!Pattern.matches("[rlFB\\-]{0,}", str))
		{
			int pos0=-1, pos1=-1;
			for (int i = 0; i < str.length(); i++) if (Character.isDigit(str.charAt(i))) pos0 = i+1;
			for (int i = str.length()-1; i >= 0; i--) if (str.charAt(i) == '.') pos1 = i;
			if ((pos0 != -1) && (pos1 != -1) && (pos0 < pos1))
				return str.substring(0, pos0)+escape(str.substring(pos0, pos1))+str.substring(pos1);
			return str;
		}
		
		// Enthält alle gefundenen DancePattern
		List<DancePattern> pattern = new LinkedList<>();
		
		// Nach DancePattern suchen
		for (int i = 1; i <= str.length()/2; i++)
		{
			DancePattern p = new DancePattern();
			p.length = i;
			p.pattern = str.substring(0, i);
			
			// Die Anzahl der Auftritte des DancePattern suchen
			for (int j = i; j+i-1 < str.length(); j+=i)
			{
				if (str.substring(j, j+i).equals(p.pattern))
					p.appearment++;
				else break;
			}
			p.insgAppearment = p.appearment;
			
			// überprüfen, ob das DancePattern nicht ein anderes ersetzt, wie etwa bei
			// einem bereits gefundenen DancePattern "Fr" wäre "FrFr" überflüssig
			boolean add = true;
			for (DancePattern p0 : pattern)
			{
				boolean add0 = false;
				if (p.length/p0.length != 1.0*p.length/p0.length) continue;
				for (int j = 0; j+p0.length <= p.length; j+=p0.length)
				{
					if (!p0.pattern.equals(p.pattern.substring(j, j+p0.length)))
						add0 = true;
				}
				if (!add0) { add = false; break; }
			}
			
			if (add) pattern.add(p);
		}
		
		// Das beste Pattern herausfinden und den nicht durch das Pattern abgedeckten Teil
		// des String (rekursiv) nochmal escapen.
		DancePattern bestPattern = DancePattern.getLongest(pattern);
		DancePattern best = DancePattern.escape(bestPattern);
		if (best == null) return str.substring(0, 1)+((str.length() > 1) ? escape(str.substring(1)) : "");
		else
		{
			String str0 = best.appearment+escape(best.pattern)+"."+
					((str.length() > 1) ? escape(str.substring(best.insgAppearment*best.length)) : "");
			if (str0 != str) str0 = escape(str0);
			return str0;
		}
	}
	
	
	
	/**
	 * Diese Methode unescaped einen Tanz.<br><br>Beispiel:
	 * Aus <code>8Fr.</code> wird <code>FrFrFrFrFrFrFr</code>
	 */
	public static String unescape (String escaped)
	{
		if (Pattern.matches("[FBrl\\-]{0,}", escaped))
		{
			return escaped;
		}
		String str = unescape(escaped, 0);
		return str;
	}
	private static String unescape (String escaped, int pos)
	{
		if (Pattern.matches("[FBrl\\-]{0,}", escaped)) return escaped;
		
		escaped = escaped.substring(pos);
		StringBuilder unescaped = new StringBuilder();
		
		int pos0=-1, pos1=-1, inside=0; DancePattern pattern = new DancePattern();
		for (int i = 0; i < escaped.length(); i++)
		{
			char c = escaped.charAt(i);
			if (Character.isDigit(c))
			{
				if (pos0 != -1)
					inside++;
				else
				{
					pos0 = i;
					pattern.appearment = Integer.parseInt(escaped.substring(i, i+1));
					pattern.insgAppearment = pattern.appearment;
				}
			}
			if (c == '.')
			{
				if (inside != 0)
					inside--;
				else
				{
					pos1 = i;
					pattern.pattern = unescape(escaped.substring(pos0+1, pos1), 0);
					pattern.length = pattern.pattern.length();
				}
			}
		}
		
		if ((pos0 != -1) && (pos1 != -1))
		{
			unescaped.append(escaped.substring(0, pos0));
			for (int i = 0; i < pattern.appearment; i++)
				unescaped.append(pattern.pattern);
			unescaped.append(unescape(escaped, pos1+1));
		}
		else unescaped.append(escaped);
		
		return unescaped.toString();
	}
}



/**
 * Diese Klasse ist die KI. Sie produziert beim Vortanzen einen mehr oder wenig
 * zufälligen String, der einem zufällig bestimmten Format folgt. Beim Nachtanzen
 * wird zuerst der String automatisch escaped. Anschließend versucht die KI, das
 * Pattern zu ergänzen, da der Server String, die länger als 255 Zeichen sind,
 * abschneidet. Dadurch stehen verschiedene Solutions bereit, von denen anschließend
 * die beste ausgewählt wird. Von der besten Solution werden dann noch unnötige Zeichen
 * am Ende abgeschnitten, die durch ihnre Anwesenheit mehr Strafpunkte erzeugen als
 * verhindern.<br>
 * Beachten Sie, dass die zug()-Methode automatisch von der Superklasse in die
 * beiden Methoden vor() und nach() aufgeteilt wird. Siehe {@link AbstractKI}
 * <p>
 * Spiel.Zugehörige KI: power2 von meiserdo
 * @version 27
 * @author Dominic S. Meiser
 */
class AI extends AbstractKI
{
	/**
	 * Diese Methode generiert einen zufälligen Vortanz. Der Tanz folgt dabei
	 * einem der folgenden Muster:
	 * <ul>
	 *   <li>999F.r.-r.
	 *   <li>9Fr.99BB..
	 *   <li>FF489F..r.
	 *   <li>9rFr3rF.B.
	 *   <li>9F9Fl7F...
	 *   <li>B5F5l7B...
	 * </ul>
	 * Das Muster wird zufällig bestimmt, wobei nicht jedesm Muster dieselbe
	 * Warscheinlichkeit hat. F kann durch B, r durch l (und -) ersetzt werden.
	 * 
	 * @param id Die Identifikationsnummer der KI.
	 * @param zustand Der aktuelle Spiel.Zustand des Spiels.
	 * @param zug Das Objekt, mit dem man den Spiel.Zug durchführen kann.
	 */
	public void vor (int id, Spiel.Zustand zustand, Spiel.Zug zug)
	{
		String str = "";
		
		if (Math.random() < 0.03)
		{
			char c0 = ((Math.random() < 0.5) ? 'F' : 'B');
			char c1 = ((Math.random() < 4f/6f) ? 'r' : ((Math.random() < 0.75) ? 'l' : '-'));
			// Format: 999F.r.-r.
			str = "999"+c0+"."+c1+".-"+c1+".";
		}
		
		else if (Math.random() < 0.03)
		{
			char c0 = ((Math.random() < 0.5) ? 'F' : 'B');
			char c1 = ((Math.random() < 0.5) ? 'F' : 'B');
			char c2 = ((Math.random() < 4f/6f) ? 'r' : ((Math.random() < 0.75) ? 'l' : '-'));
			// Format: 9Fr.99BB..
			str = "9"+c0+""+c2+".99"+c1+""+c1+"..";
		}
		
		else if (Math.random() < 0.25)
		{
			char c0 = ((Math.random() < 0.5) ? 'F' : 'B');
			char c1 = ((c0 == 'F') ? 'B' : 'F');
			char c2 = ((Math.random() < 0.5) ? 'r' : 'l');
			int c3 = (int)(Math.random()*4+4);
			// Format: 9rFr3rF.B.
			str = c3+""+c2+""+c0+""+c2+"3"+c2+""+c0+"."+c1+".";
		}
		
		else if (Math.random() < 0.4)
		{
			char c0 = ((Math.random() < 0.5) ? 'F' : 'B');
			char c1 = ((Math.random() < 0.5) ? 'r' : 'l');
			// Format: FF489F..r.
			str = c0+""+c0+"489"+c0+".."+c1+".";
		}
		
		else if (Math.random() < 0.4)
		{
			char c0 = ((Math.random() < 0.5) ? 'F' : 'B');
			char c1 = ((Math.random() < 0.5) ? 'r' : 'l');
			// Format: 9F9Fl7F...
			str = "9"+c0+"9"+c0+""+c1+"7"+c0+"...";
		}
		
		else
		{
			char c0 = ((Math.random() < 0.5) ? 'F' : 'B');
			char c1 = ((c0 == 'F') ? 'B' : 'F');
			char c2 = ((Math.random() < 0.5) ? 'r' : 'l');
			// Format: B5F5l7B...
			str = c0+"5"+c1+"5"+c2+"7"+c0+"...";
		}
		
		zug.ausgabe("<vor><ergebnis>"+str+"</ergebnis></vor>");
		zug.tanzen(str);
	}
	
	
	
	/**
	 * Diese Methode erstellt den Nachtanz des angegebenen Vortanzes. Dabei werden zuerst
	 * zwei sehr einfache Lösungen erstellt, die einmal den Vortanz (der automatisch escaped
	 * wird) und einmal einen leeren String enthält. Anschließend werden noch 2 Lösungen
	 * erstellt, die versuchen, einen abgeschnittenen String (der Server schneidet Strings,
	 * die länger als 255 Zeichen sind, ab) "wiederherzustellen", d.h. sich den abgeschnittenen
	 * Teil zu erschließen. Dies kann durch escapen zu einem kürzeren String führen.
	 * 
	 * @param id Die Identifikationsnummer der KI.
	 * @param zustand Der aktuelle Spiel.Zustand des Spiels.
	 * @param zug Das Objekt, mit dem man den Spiel.Zug durchführen kann.
	 * @param tanz Der Vortanz des Vortänzers.
	 */
	public void nach (int id, Spiel.Zustand zustand, Spiel.Zug zug, String tanz)
	{
		long insgtime = System.currentTimeMillis();
		
		// Wenn der Tanz nix enthält ist es am besten nix zurückzugeben
		if (tanz.equals(""))
		{
			zug.ausgabe("<nach><vortanz></vortanz><ergebnis></ergebnis></nach>");
			zug.tanzen("");
			return;
		}
		
		
		
		// 2 ganz einfache Lösungen hinzufügen: einmal mit nix und einmal einfach den
		// übergebenen Tanz, der sowieso automatisch escaped wird. Dazu noch eine Lösung
		// erstellen, bei der der Tanz rückwärts escaped wird, wenn der Tanz nicht abgeschnitten
		// wurde.
		Solution s0 = new Solution(tanz, tanz);
//		System.out.println(tanz+": "+s0.getRating());
		Solution s1 = new Solution("", tanz);
//		System.out.println(": "+s1.getRating());
		
		
		// Versuchen, die durch einen zu langen String (>255 Zeichen, d.h. der Server hat
		// den String abgeschnitten) abgeschnittenen Zeichen zu rekonstruieren
		Solution s2 = null;
		List<Solution> solutions = new LinkedList<>();
		long time = System.currentTimeMillis();
		for (int i = 0; (i < tanz.length()) && (System.currentTimeMillis()-time < 550); i++)
		{
			String teil1 = tanz.substring(0, i);
			String teil2 = tanz.substring(i);
//			System.out.print(teil1+" | "+teil2);
			String escaped1 = Escaper.escape(teil1);
			String escaped2 = Escaper.escape(teil2);
//			System.out.print(" = "+escaped1+""+escaped2);
			if ((tanz.length() == 255) && (escaped1.length() < 8))
				escaped2 = Compleater.compleate(escaped2,
						((escaped1.length()<7) ? 9-escaped1.length() : 10-escaped1.length()));
			String s = Escaper.escape(escaped1+escaped2);
//			System.out.println(" = "+s);
			solutions.add(new Solution(s, tanz));
//			if ((s.length() <= 10) && (Escaper.unescape(s).startsWith(tanz))) break;
		}
		s2 = new Solution(Solution.getBest(solutions), tanz);
		
		
		// Versuchen, einen nicht abgeschnittenen String durch Finden eines immer wiederkehrenden
		// Teils, darin 2 Teile zu finden und dies zu escapen, wenn vorher noch keine Lösung mit
		// <= 10 zeichen gefunden wurde
		Solution s3 = null;
		if ((s0.getEscapedTanz().length() > 10) && (s2.getEscapedTanz().length() > 10))
		{
			solutions = new LinkedList<>();
			DancePattern pattern = new DancePattern();
			{
				int startnums = 0;
				for (char c : s0.getEscapedTanz().toCharArray())
				{
					if (Character.isDigit(c)) startnums++;
					else break;
				}
				int enddots = 0;
				for (int i = s0.getEscapedTanz().length()-1; i > 0; i--)
				{
					if (s0.getEscapedTanz().charAt(i) == '.') enddots++;
					else break;
				}
				if ((enddots == 0) || (startnums == 0)) pattern = null;
				else
				{
					pattern.pattern = Escaper.unescape(s0.getEscapedTanz().substring(
							Math.min(startnums, enddots),
							s0.getEscapedTanz().length()-Math.min(startnums, enddots)));
					for (int i = 0; i < Math.min(startnums, enddots); i++)
						pattern.appearment *= Integer.parseInt(s0.getEscapedTanz().substring(i, i+1));
					pattern.length = pattern.pattern.length();
				}
			}
			if ((pattern != null) && Pattern.matches("[FBrl\\-]{1,}", pattern.pattern))
			{
				for (int i = 0; (i < pattern.length) && (System.currentTimeMillis()-time < 800); i++)
				{
					String teil1 = pattern.pattern.substring(0, i);
					String teil2 = pattern.pattern.substring(i);
//					System.out.print(teil1+" | "+teil2);
					String escaped1 = Escaper.escape(teil1);
					String escaped2 = Escaper.escape(teil2);
					DancePattern result = new DancePattern(escaped1+escaped2, pattern.appearment);
					result = DancePattern.escape(result);
//					System.out.println(" = "+result.appearment+result.pattern+".");
					solutions.add(new Solution(result.appearment+result.pattern+".", tanz));
				}
				s3 = new Solution(Solution.getBest(solutions), tanz);
			}
		}
		
		
		String best = Solution.getBest(Arrays.asList(s0, s2, s3));
		
		// alle möglichen Zeichen abschneiden und schaun welches davon die beste Lösung ist
		List<Solution> bests = new LinkedList<>();
		bests.add(s1); // diese Lösung erst jetzt beachten
		for (int i = 0; i < best.length()-1; i++)
			bests.add(new Solution(best.substring(0, best.length()-i), tanz, true));
		
		best = Solution.getBest(bests);
		if (best == null) best = "";
		
		// Die beste Lösung abgeben und eine Debugausgabe machen
		zug.ausgabe("<nach><vortanz>"+tanz+"</vortanz><ergebnis>"+best+"</ergebnis><time>"+
				(System.currentTimeMillis()-insgtime)+"</time></nach>");
		zug.tanzen(best);
	}
}

