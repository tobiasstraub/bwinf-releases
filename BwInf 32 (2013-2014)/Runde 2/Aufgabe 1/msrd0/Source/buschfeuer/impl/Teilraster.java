package buschfeuer.impl;

/**
 * Speichert ein normales Raster und zusätzliche Variablen wie dieses Teilraster
 * gebildet wurde.
 * @author Dominic S. Meiser
 */
public class Teilraster extends Raster
{
	public Teilraster(int minx, int maxx, int miny, int maxy, int startValue)
	{
		super(maxx-minx, maxy-miny, startValue);
		this.minx = minx; this.maxx = maxx;
		this.miny = miny; this.maxy = maxy;
	}
	public int minx, maxx, miny, maxy;
}
