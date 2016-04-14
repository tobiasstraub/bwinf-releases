package buschfeuer.feuerloescher;

import static buschfeuer.impl.Raster.BRAND;
import static buschfeuer.impl.Raster.GELOESCHT;
import static buschfeuer.impl.Raster.WALD;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import buschfeuer.Feuerloescher;
import buschfeuer.impl.Raster;
import buschfeuer.impl.RasterUtils;
import buschfeuer.impl.RasterValue;

/**
 * Dieser Feuerlöscher löscht das Raster mithilfe von Bruteforce. 
 * @author Dominic S. Meiser
 */
public class BruteforceFeuerloescher extends Feuerloescher
{
	protected List<Point> loeschen;
	protected int bestSolution = 0;
	public void init (Raster raster)
	{
		// Lösung rekursiv bestimmen
		long time = System.currentTimeMillis();
		System.out.println();
		loeschen = test(raster, new LinkedList<Point>());
		time = System.currentTimeMillis() - time;
		System.out.println("[ERGEBNIS] "+loeschen);
		System.out.println("[ERGEBNIS] "+bestSolution);
		System.out.print("[ERGEBNIS] "); printBashText("done ("+time+" ms)\n", "32");
		
		// Raster speichern
		File file = new File(System.getProperty("java.io.tmpdir")+File.separator+"buschfeuer",
				"raster_bruteforce.bfr");
		try { RasterUtils.save(raster, loeschen, file); }
		catch (IOException ioe) { ioe.printStackTrace(); }
	}
	
	/**
	 * Durchsucht das Raster rekursiv mittels Bruteforce. vorhanden gibt die bereits
	 * in der Liste vorhandenen Brandstücke an.
	 */
	protected List<Point> test (Raster raster, List<Point> vorhanden)
	{
		int wald = raster.count(WALD);
		if ((wald > bestSolution) && RasterUtils.istGeloescht(raster))
		{
			System.out.println("[GELÖSCHT] "+wald+": "+vorhanden);
			bestSolution = wald;
			return vorhanden;
		}
		
		// Abbruch, wenn Anzahl der Waldstücke kleiner als bestSolution. Wenn die
		// Anzahl der Waldstücke gleich bestSolution ist, dann ist die Lösung
		// uninteressant, da bereits eine gleichwertige oder bessere Lösung gefunden
		// wurde.
		if (wald <= bestSolution)
			return null;
		
		// Das Feuer einmal sich weiterverbreiten lassen
		Raster neuesRaster = RasterUtils.clone(raster);
		RasterUtils.burn(neuesRaster);
		
		// Zuerst die Brandherde mit der Anzahl der in der Umgebung liegenden
		// Waldstücken ermitteln
		List<RasterValue> braende = new LinkedList<>();
		for (int i = 0; i < neuesRaster.getSize().width; i++)
		{
			for (int j = 0; j < neuesRaster.getSize().height; j++)
			{
				// Brennt das Waldstück überhaupt?
				if (neuesRaster.get(i, j) == BRAND)
				{
					// nicht brenndenden Wald in Umgebung suchen
					int wald_in_umgebung = 0;
					if (i > 0)
						if (neuesRaster.get(i-1, j) == WALD)
							wald_in_umgebung++;
					if (j > 0)
						if (neuesRaster.get(i, j-1) == WALD)
							wald_in_umgebung++;
					if (i < neuesRaster.getSize().width-1)
						if (neuesRaster.get(i+1, j) == WALD)
							wald_in_umgebung++;
					if (j < neuesRaster.getSize().height-1)
						if (neuesRaster.get(i, j+1) == WALD)
							wald_in_umgebung++;
					// Brandstück hinzufügen, wenn mindestens 1 Waldstück in der
					// Umgebung liegt
					if (wald_in_umgebung != 0)
						braende.add(new RasterValue(i, j, BRAND, wald_in_umgebung));
				}
			}
		}
		
		if (braende.size() == 0)
		{
			vorhanden.add(null);
			List<Point> erg = test(neuesRaster, vorhanden);
			vorhanden.remove(vorhanden.size()-1);
			return erg;
		}
		
		// Jetzt alle Brandstücke durchgehen und rekursiv weiterarbeiten.
		List<Point> best = null;
		for (RasterValue elem : braende)
		{
			vorhanden.add(elem.pos);
			neuesRaster.set(elem.pos.x, elem.pos.y, neuesRaster.get(elem.pos.x, elem.pos.y)+GELOESCHT);
			int bestBefore = bestSolution;
			List<Point> ergebnis = test(neuesRaster, vorhanden);
			if ((ergebnis != null) && (bestSolution > bestBefore))
				best = clone(ergebnis);
			neuesRaster.set(elem.pos.x, elem.pos.y, neuesRaster.get(elem.pos.x, elem.pos.y)-GELOESCHT);
			vorhanden.remove(elem.pos);
		}
		return best;
	}
	
	
	int i = -1;
	public Point delete (Raster raster)
	{
		if (i >= loeschen.size()-1) return null;
		return loeschen.get(++i);
	}
	
	
	/** Klont die angegebene Liste. */
	protected static List<Point> clone (List<Point> list0)
	{
		List<Point> list1 = new LinkedList<>();
		for (Point e : list0)
			list1.add(e);
		return list1;
	}
}
