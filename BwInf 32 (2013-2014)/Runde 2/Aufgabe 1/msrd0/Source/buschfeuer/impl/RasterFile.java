package buschfeuer.impl;

import java.awt.Point;
import java.util.List;

import buschfeuer.impl.Raster;

/**
 * Diese Klasse speichert den Inhalt einer geladenen Datei, die ein Raster und evtl. eine
 * Lösung enthät.
 * @author Dominic S. Meiser
 */
public class RasterFile
{
	public Raster raster;
	public List<Point> solution;
	public RasterFile (Raster r)
	{
		raster = r;
	}
	public RasterFile (Raster r, List<Point> s)
	{
		raster = r;
		solution = s;
	}
}
