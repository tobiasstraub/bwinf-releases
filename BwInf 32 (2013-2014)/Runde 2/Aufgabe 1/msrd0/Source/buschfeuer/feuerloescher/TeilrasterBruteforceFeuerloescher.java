package buschfeuer.feuerloescher;

import static buschfeuer.impl.Raster.BRAND;
import static buschfeuer.impl.Raster.GELOESCHT;
import static buschfeuer.impl.Raster.WALD;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import buschfeuer.impl.Raster;
import buschfeuer.impl.RasterUtils;
import buschfeuer.impl.Teilraster;

/**
 * Dieser veränderte Bruteforce Feuerlöscher sucht nach einem kleineren Raster innerhalb
 * des Rasters, wo aber alle Brandherde eingeschlossen sind, und versucht zuerst nur
 * diesen zu löschen. Wenn das Feuer aber an den Rand gelangt, wird das Raster vergrößert.
 * @author Dominic S. Meiser
 */
public class TeilrasterBruteforceFeuerloescher extends BruteforceFeuerloescher
{
	/**
	 * Zerlegt das Raster und lässt die Superklasse einen Bruteforce darauf anwenden.
	 */
	public void init (Raster raster)
	{
		long time = System.currentTimeMillis();
		System.out.print("[FEUERLÖSCHER] teile Raster ...");
		
		List<Teilraster> teilraster = new LinkedList<>();
		
		// Zuerst alle Teilraster bilden, in denen alle Brand-Elemente vorhanden sind
		int minx=raster.getSize().width-1, miny=raster.getSize().height-1, maxx=0, maxy=0;
		for (int i = 0; i < raster.getSize().width; i++)
		{
			for (int j = 0; j < raster.getSize().height; j++)
			{
				if (raster.get(i, j) == BRAND)
				{
					if (minx > i) minx = i;
					if (miny > j) miny = j;
					if (maxx < i) maxx = i+1;
					if (maxy < j) maxy = j+1;
				}
			}
		}
		if ((minx >= maxx) || (miny >= maxy)) // negative Arraygrößen ausschließen
		{
			maxx = minx+1;
			maxy = miny+1;
		}
		// Teilraster bilden
		Teilraster r = null;
		do
		{
			r = new Teilraster (minx, maxx, miny, maxy, WALD);
			for (int i = minx; i < maxx; i++)
				for (int j = miny; j < maxy; j++)
					if (raster.get(i, j) != WALD)
						r.set(i-minx, j-miny, raster.get(i, j));
			teilraster.add(r);
			if (minx > 0) minx--;
			if (miny > 0) miny--;
			if (maxx < raster.getSize().width) maxx++;
			if (maxy < raster.getSize().height) maxy++;
		} while (!r.getSize().equals(raster.getSize()));
		
		printBashText(" done ("+(System.currentTimeMillis()-time)+" ms, "+teilraster.size()+" Teilraster)\n", "32");
		
		// Jetzt Bruteforce auf das möglichste, kleinste Teilraster anwenden. Sobald das Feuer
		// an den Rand stößt und dort nicht gelöscht wurde, muss die nächstgrößere Rasterversion
		// verwendet werden
		for (Teilraster teilr : teilraster)
		{
			super.init(teilr);
			printBashText(teilr+" -> "+super.loeschen+"\n", "1;32");
			if ((loeschen != null) && isPossible(RasterUtils.clone(teilr), raster.getSize(), loeschen))
			{
				// Koordinaten "übersetzen"
				for (int i = 0; i < super.loeschen.size(); i++)
				{
					if (loeschen.get(i) != null)
					{
						loeschen.get(i).x += teilr.minx;
						loeschen.get(i).y += teilr.miny;
					}
				}
				// Fertig
				break;
			}
			bestSolution = 0;
			printBashText("[ERGEBNIS] nicht möglich\n", "31");
		}
		
		
		// Raster speichern
		File file = new File(System.getProperty("java.io.tmpdir")+File.separator+"buschfeuer",
				"raster_teilraster-bruteforce.bfr");
		try { RasterUtils.save(raster, loeschen, file); }
		catch (IOException ioe) { ioe.printStackTrace(); }
	}
	
	/**
	 * Überprüft, ob diese Lösung möglich ist oder ob das Raster vergrößert werden muss. 
	 */
	protected boolean isPossible (Teilraster raster, Dimension size, List<Point> solution)
	{
		// Brand simulieren
		for (Point p : solution)
		{
			RasterUtils.burn(raster);
			if (p != null)
				raster.set(p.x, p.y, raster.get(p.x, p.y)+GELOESCHT);
		}
		
		// oberer Rand
		if (raster.miny != 0)
		{
			for (int i = 0; i < raster.getSize().width; i++)
				if (raster.get(i, 0) == BRAND)
					return false;
		}
		
		// rechter Rand
		if (raster.minx != 0)
		{
			for (int j = 0; j < raster.getSize().height; j++)
				if (raster.get(0, j) == BRAND)
					return false;
		}
		
		// unterer Rand
		if (raster.maxy != size.height)
		{
			for (int i = 0; i < raster.getSize().width; i++)
				if (raster.get(i, raster.getSize().height-1) == BRAND)
					return false;
		}
		
		// linker Rand
		if (raster.maxx != size.width)
		{
			for (int j = 0; j < raster.getSize().height; j++)
				if (raster.get(raster.getSize().width-1, j) == BRAND)
					return false;
		}
		
		return true;
	}
}
