package buschfeuer.impl;

import java.awt.Point;

/**
 * Diese Klasse speichert Daten über ein Element im Raster.
 * @author Dominic S. Meiser
 */
public class RasterValue
{
	public int value;
	public int wald_in_umgebung;
	public Point pos;
	
	/**
	 * Diese Klasse ist im Prinzip nichts anderes als Point, hat aber einen einfacheren
	 * und kürzeren Beschreibungsstring.
	 * @author Dominic S. Meiser
	 */
	static class SysoutPoint extends Point
	{
		private static final long serialVersionUID = -8731898263866279560L;

		// Konstruktoren der Superklasse "kopieren"
		public SysoutPoint () { super(); }
		public SysoutPoint (int x, int y) { super(x, y); }
		
		public String toString ()
		{
			return "["+x+", "+y+"]";
		}
	}
	
	public RasterValue (int x, int y, int value, int waldUmgebung)
	{
		this(new SysoutPoint(x, y), value, waldUmgebung);
	}
	public RasterValue (int x, int y, int value)
	{
		this(new SysoutPoint(x, y), value);
	}
	public RasterValue (int x, int y)
	{
		this(new SysoutPoint(x, y));
	}
	public RasterValue (Point p, int value, int waldUmgebung)
	{
		pos = p;
		this.value = value;
		this.wald_in_umgebung = waldUmgebung;
	}
	public RasterValue (Point p, int value)
	{
		this(p, value, -1);
	}
	public RasterValue (Point p)
	{
		this(p, -1);
	}
	
	public String toString ()
	{
		return "["+pos+"(="+value+"),"+wald_in_umgebung+"]";
	}
}
