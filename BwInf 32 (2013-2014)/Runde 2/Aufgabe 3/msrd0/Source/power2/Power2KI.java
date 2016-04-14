package power2;

import game.AbstractKI;
import game.Spiel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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
 * Zugehörige KI: power2 von meiserdo
 * @version 27
 * @author Dominic S. Meiser
 */
public class Power2KI extends AbstractKI
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
		System.out.println(tanz+": "+s0.getRating());
		Solution s1 = new Solution("", tanz);
		System.out.println(": "+s1.getRating());
		
		
		// Versuchen, die durch einen zu langen String (>255 Zeichen, d.h. der Server hat
		// den String abgeschnitten) abgeschnittenen Zeichen zu rekonstruieren
		Solution s2 = null;
		List<Solution> solutions = new LinkedList<>();
		long time = System.currentTimeMillis();
		for (int i = 0; (i < tanz.length()) && (System.currentTimeMillis()-time < 550); i++)
		{
			String teil1 = tanz.substring(0, i);
			String teil2 = tanz.substring(i);
			System.out.print(teil1+" | "+teil2);
			String escaped1 = Escaper.escape(teil1);
			String escaped2 = Escaper.escape(teil2);
			System.out.print(" = "+escaped1+""+escaped2);
			if ((tanz.length() == 255) && (escaped1.length() < 8))
				escaped2 = Compleater.compleate(escaped2,
						((escaped1.length()<7) ? 9-escaped1.length() : 10-escaped1.length()));
			String s = Escaper.escape(escaped1+escaped2);
			System.out.println(" = "+s);
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
					System.out.print(teil1+" | "+teil2);
					String escaped1 = Escaper.escape(teil1);
					String escaped2 = Escaper.escape(teil2);
					DancePattern result = new DancePattern(escaped1+escaped2, pattern.appearment);
					result = DancePattern.escape(result);
					System.out.println(" = "+result.appearment+result.pattern+".");
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
