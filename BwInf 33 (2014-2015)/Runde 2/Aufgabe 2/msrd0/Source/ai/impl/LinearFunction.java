/*
 * LinearFunction
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai.impl;

import java.awt.geom.Point2D;
import java.util.*;

import lombok.*;

// ai-import Formatter
// ai-import FunctionType

/**
 * Diese Klasse repräsentiert eine Grade mithilfe der Geradengleichung y=mx+b. Die beiden Werte m und b werden bei
 * Bedarf aus zwei Punkten oder einem Punkt und einem Winkel berechnet.
 */
@AllArgsConstructor
@EqualsAndHashCode
class LinearFunction
{
	protected static double correctAngle (double angle)
	{
		while (angle > Math.PI / 2.0)
		{
			angle -= Math.PI;
		}
		while (angle < Math.PI / -2.0)
		{
			angle += Math.PI;
		}
		return angle;
	}

	/**
	 * Die Steigung der Funktion.
	 */
	@Getter
	private double m;

	/**
	 * Der y-Achsen-Abschnitt der Funktion.
	 */
	@Getter
	private double b;

	/**
	 * Berechnet die Geradengleichung aus dem angegebenen Punkt und dem Winkel. Der Winkel sollte dabei zwischen
	 * -PI/2 und PI/2 liegen.
	 */
	public LinearFunction (double x, double y, double angle)
	{
		m = Math.tan(correctAngle(angle));
		b = y - m * x;
	}

	/**
	 * Berechnet die Geradengleichung aus dem angegebenen Punkt und dem Winkel. Der Winkel sollte dabei zwischen
	 * -PI/2 und PI/2 liegen.
	 */
	public LinearFunction (double x, double y, double angle, FunctionType z)
	{
		angle = correctAngle(angle);
		m = Math.tan(angle);
		b = y + z.getZ() / Math.cos(angle) - m * x;
		//System.out.println("LinearFunction::LinearFunction(" + x + ", " + y + ", " + angle + ", " + z.getZ() + "): f(x)=" + m + "x+" + b);
	}

	/**
	 * Berechnet die Geradengleichung aus den angegebenen beiden Punkten.
	 */
	public LinearFunction (double x0, double y0, double x1, double y1)
	{
		m = (y1 - y0) / (x1 - x0);
		b = y0 - m * x0;
	}

	/**
	 * Berechnet die Geradengleichung aus den angegebenen beiden Punkten.
	 */
	public LinearFunction (@NonNull Point2D.Double p0, @NonNull Point2D.Double p1)
	{
		this(p0.x, p0.y, p1.x, p1.y);
	}

	/**
	 * Berechnet den y-Wert der Funktion an der Stelle x.
	 */
	public double y (double x)
	{
		return (m * x + b);
	}

	/**
	 * Berechnet die Schnittpunkte der Gerade mit dem Kreis, angenommen der Mittelpunkt des Kreises liegt bei 0|0.
	 */
	public Set<Point2D.Double> intersections (double r)
	{
		Set<Point2D.Double> intersections = new HashSet<>();

		// Diskriminante der ABC-Formel ausrechnen
		double discriminant = m * m * r * r - b * b + r * r;

		// wenn die Diskriminante positiv ist gibt es mindestens eine Lösung
		if (discriminant >= 0)
		{
			discriminant = Math.sqrt(discriminant);
			double x = (discriminant - m * b) / (m * m + 1);
			intersections.add(new Point2D.Double(x, y(x)));

			// wenn die Diskriminante größer als 0 ist gibt es zwei Lösungen
			if (discriminant > 0)
			{
				x = (-discriminant - m * b) / (m * m + 1);
				intersections.add(new Point2D.Double(x, y(x)));
			}
		}

		return intersections;
	}

	/**
	 * Gibt die Funktionsgleichung zurück.
	 */
	@Override
	public String toString ()
	{
		return "f(x)=" + Formatter.format(getM()) + "*x+" + Formatter.format(getB());
	}
}
