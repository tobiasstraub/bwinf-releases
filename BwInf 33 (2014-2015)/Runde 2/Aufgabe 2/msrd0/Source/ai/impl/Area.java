/*
 * Area
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai.impl;

import bwinf33_2.aufgabe2.Cone;
import bwinf33_2.aufgabe2.SceneData;

import java.awt.geom.Point2D;

import lombok.*;

// ai-import Formatter
// ai-import LinearFunction

@RequiredArgsConstructor
@EqualsAndHashCode(of = { "x", "y", "angle" })
class Area
{
	/**
	 * Die Koordinaten des Punktes, mit der diese Area erstellt wurde.
	 */
	@Getter
	@NonNull
	private double x, y;

	/**
	 * Der Winkel, mit dem diese Area erstellt wurde.
	 */
	@Getter
	@NonNull
	private double angle;

	/**
	 * Die Funktionen für diese Area.
	 */
	private LinearFunction upper, middle, lower;

	/**
	 * Berechnet die Area mit der Mittel-Geraden durch die beiden Punkte.
	 */
	public Area (double x0, double y0, double x1, double y1)
	{
		this(x0, y0, Math.atan((y0 - y1) / (x0 - x1)));
	}

	/**
	 * Berechnet die Area mit der Mittel-Geraden durch die beiden Punkte.
	 */
	public Area (@NonNull Point2D.Double p0, @NonNull Point2D.Double p1)
	{
		this(p0.x, p0.y, p1.x, p1.y);
	}

	/**
	 * Gibt die upper-Funktion für diese Area zurück.
	 */
	public LinearFunction getUpper ()
	{
		if (upper == null)
			upper = new LinearFunction(getX(), getY(), getAngle(), FunctionType.UPPER_FCT);
		return upper;
	}

	/**
	 * Gibt die middle-Funktion für diese Area zurück.
	 */
	public LinearFunction getMiddle ()
	{
		if (middle == null)
			middle = new LinearFunction(getX(), getY(), getAngle());
		return middle;
	}

	/**
	 * Gibt die lower-Funktion für diese Area zurück.
	 */
	public LinearFunction getLower ()
	{
		if (lower == null)
			lower = new LinearFunction(getX(), getY(), getAngle(), FunctionType.LOWER_FCT);
		return lower;
	}

	/**
	 * Gibt die Anzahl der übergebenen, nicht umgeschmissenen Cones zurück, die in dieser Area liegen.
	 */
	public int countCones (@NonNull SceneData data)
	{
		int count = 0;
		for (Cone c : data.getCones())
		{
			if (!c.isOverthrown()
				&& (c.getY() <= getUpper().y(c.getX()))
				&& (c.getY() >= getLower().y(c.getX())))
				count++;
		}
		return count;
	}

	public String toString ()
	{
		return "Area(x=" + Formatter.format(getX()) + ";y=" + Formatter.format(getY())
			   + ";angle=" + Formatter.formatRadians(getAngle()) + ")";
	}
}
