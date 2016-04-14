package power2;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Diese Klasse repräsentiert ein Teil eines Tanzes. Es speichert ein Pattern und
 * die Anzahl der Auftritte im String. Mit der escape()-Methode kann man ein neues
 * DancePattern erstellen das nur Zahlen bis 9 enthält. Die alte Anzahl von Auftritten
 * wird immernoch in insgAppearment gespeichert.
 * 
 * @author Dominic S. Meiser
 */
public class DancePattern
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