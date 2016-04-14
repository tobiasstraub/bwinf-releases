package power2;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Diese Klasse stellt Methoden zum escapen und unescapen von Tänzen bereit.
 * 
 * @author Dominic S. Meiser
 */
public class Escaper
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
