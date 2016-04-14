package power2;

/**
 * Diese Klasse stellt eine Methode zum vervolständigen eines abgeschnittenen
 * Strings zur Verfügung.
 * 
 * @author Dominic S. Meiser
 */
public class Compleater
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
				System.out.println("\tremove: "+str.substring(0, i)+" |- "+str.substring(i, i+2)+" -| "+str.substring(i+2));
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
				System.out.println("\tremove: "+str.substring(0, i)+" |- "+str.substring(i, i+2)+" -| "+str.substring(i+2));
				str = str.substring(0, i)+str.substring(i+2);
			}
		}
		
		// Wenn der String viel zu lang ist maxlength evtl. etwas vergrößern
		if (((str.length() > 13) || !Escaper.unescape(str).startsWith(Escaper.unescape(escaped)))
				&& (maxlength < 13))
		{
			System.out.println(" -> compleater: increaing maxlength");
			String str0 = compleate(escaped, maxlength+1);
			System.out.println("  -> \""+str+"\" -> \""+str0+"\"");
			return str0;
		}
		
		System.out.println(" -> compleater returns \""+str+"\" for input \""+escaped+"\"");
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
