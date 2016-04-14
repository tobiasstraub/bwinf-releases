/*
 * Part
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai.impl;

import bwinf33_2.aufgabe2.Cone;

import java.awt.geom.Point2D;
import java.util.*;

import lombok.*;

// ai-import Formatter
// ai-import IllegalPartException

/**
 * Diese Klasse repräsentiert einen Teil des Kreises. Dabei wird die Größe des Rechtecks und die darin enthaltenen
 * Kegel abgespeichert.
 */
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = { "cones" })
class Part implements Comparable<Part>
{
	/**
	 * Die Größe des Rechtecks.
	 */
	@Getter
	@Setter
	@NonNull
	double x, y, width, height;
	/**
	 * Die im Rechteck enthaltenen Kegel.
	 */
	@Getter
	@Setter
	Collection<Cone> cones = new ArrayList<>();

	/**
	 * Erstellt einen neuen Part an der Stelle x|y mit der Breite und Höhe 1 und keinen Kegeln.
	 */
	public Part (double x, double y)
	{
		this(x, y, 1, 1);
	}

	/**
	 * Überprüft, ob der Punkt in diesem Rechteck liegt.
	 */
	public boolean contains (@NonNull Point2D.Double point)
	{
		return ((point.x >= x) && (point.x <= x + width)
				&& (point.y >= y) && (point.y <= y + height));
	}

	/**
	 * Gibt den Punkt in Rechteck UND Kreis zurück, der dem Mittelpunkt am nächsten ist und auf dre angegebenen
	 * LinearFunction liegt.
	 */
	public Point2D.Double getMiddle (@NonNull LinearFunction fct, double r)
	{
		double mx = x + width / 2.0, my = y + height / 2.0;
		if (mx * mx + my * my <= r * r)
			return new Point2D.Double(mx, my);

		Set<Point2D.Double> intersections = fct.intersections(r);
		for (Point2D.Double intersection : intersections)
		{
			if (contains(intersection))
				return intersection;
		}

		throw new IllegalPartException(
				"Die Schnittpunkte mit dem Kreis liegen außerhalb des Rechtecks: x="
				+ Formatter.format(x) + "; y=" + Formatter.format(y) + "; r=" + Formatter.format(r)
				+ "; - " + fct + " - " + intersections);
	}

	/**
	 * Vergleicht die Anzahl der Kegel in diesem Part mit denen in other.
	 *
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo (Part other)
	{
		return (other.cones.size() - cones.size());
	}

	@Override
	public String toString ()
	{
		return "Part(x=" + Formatter.format(x) + ", y=" + Formatter.format(y) + ", width="
			   + Formatter.format(width) + ", height=" + Formatter.format(height)
			   + ", cones.size()=" + Formatter.format(cones.size()) + ")";
	}
}
