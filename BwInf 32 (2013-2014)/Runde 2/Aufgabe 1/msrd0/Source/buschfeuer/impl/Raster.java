package buschfeuer.impl;

import java.awt.Dimension;
import java.util.Arrays;

/**
 * Diese Klasse speichert ein Raster.
 * @author Dominic S. Meiser
 */
public class Raster
{
	public static final int WALD=1, SCHNEISE=2, BRAND=3, GELOESCHT=4;
	
	/**
	 * Erstellt ein Raster mit der angegebenen Dimension. Alle Felder haben am Anfang
	 * startValue.
	 */
	public Raster (int width, int height, int startValue)
	{
		super();
		if ((width < 1) || (height < 1))
			throw new IllegalArgumentException("Das Raster ist zu klein!");
		raster = new int[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				set(i, j, startValue);
	}
	private int[][] raster;
	
	/**
	 * Gibt das raster zurück.
	 */
	public int[][] getRaster ()
	{
		return raster;
	}
	/**
	 * Gibt den Wert des Rasters an der angegebenen Position zurück.
	 */
	public int get (int i0, int i1)
	{
		return getRaster()[i0][i1];
	}
	/**
	 * Setzt den Wert des Rasters an der angegebenen Position auf value.
	 */
	public void set (int i0, int i1, int value)
	{
		if (!((value == WALD) || (value == SCHNEISE) || (value == BRAND) ||
				(value == WALD+GELOESCHT) || (value == BRAND+GELOESCHT)))
			throw new IllegalArgumentException("Kein Rasterwert: "+value);
		raster[i0][i1] = value;
	}
	/**
	 * Gibt die Größe des Rasters an.
	 */
	public Dimension getSize ()
	{
		return new Dimension(getRaster().length, getRaster()[0].length);
	}
	
	/**
	 * Zählt, wie oft value im Raster vorkommt.
	 */
	public int count (int value)
	{
		int count = 0;
		for (int i = 0; i < raster.length; i++)
			for (int j = 0; j < raster[i].length; j++)
				if (get(i,j) == value)
					count++;
		return count;
	}
	
	
	/** Gibt eine Beschreibung des Rasters an. */
	public String toString ()
	{
		StringBuilder str = new StringBuilder(getClass().getCanonicalName());
		str.append("[");
		
		for (int i = 0; i < raster.length; i++)
		{
			str.append(Arrays.toString(raster[i]));
			if (i != raster.length-1)
				str.append(", ");
		}
		
		str.append("]");
		return str.toString();
	}
}
