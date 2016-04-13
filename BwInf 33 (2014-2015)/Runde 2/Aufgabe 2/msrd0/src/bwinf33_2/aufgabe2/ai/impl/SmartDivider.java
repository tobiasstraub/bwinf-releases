/*
 * SmartDivider
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai.impl;

import bwinf33_2.aufgabe2.Cone;
import bwinf33_2.aufgabe2.SceneData;
import bwinf33_2.aufgabe2.ai.AI;
import bwinf33_2.aufgabe2.ai.Move;

import java.awt.geom.Point2D;
import java.util.*;

import lombok.NoArgsConstructor;

// ai-import Area
// ai-import Formatter
// ai-import LinearFunction
// ai-import Part

/**
 * Diese KI teilt das Spielfeld in Quadrate ein und berechnet daraus den vermeintlich besten Wurf.
 */
@NoArgsConstructor
@SuppressWarnings("unused")
public class SmartDivider implements AI
{
	/**
	 * Diese Methode teilt die Kegel in die verschiedenen Parts auf.
	 */
	private void divide (Part parts[], SceneData data, int PARTS, int r)
	{
		for (Cone c : data.getCones())
		{
			if (c.isOverthrown())
				continue;
			parts[Math.min(PARTS - 1, (int)Math.floor(c.getX()) + r) * PARTS
				  + Math.min(PARTS - 1, (int)Math.floor(c.getY()) + r)]
					.getCones().add(c);
		}
	}

	@Override
	public Move move (SceneData data)
	{
		// den Radius aufrunden
		int r = (int)Math.ceil(data.getRadius());
		// ich benötige 2^r x 2^r Parts
		final int PARTS = (int)Math.pow(2, r);
		System.out.println("[SMARTDIVIDER] cones: " + Formatter.format(data.getCones().size())
						   + "; radius: " + Formatter.format(data.getRadius())
						   + "; parts: " + Formatter.format(PARTS));

		// die Parts erstellen
		Part parts[] = new Part[PARTS * PARTS];
		for (int x = 0; x < PARTS; x++)
		{
			for (int y = 0; y < PARTS; y++)
			{
				parts[x * PARTS + y] = new Part(x - r, y - r);
			}
		}

		// die Kegel in die Parts aufteilen
		divide(parts, data, PARTS, r);

		// die Parts sortieren
		Arrays.sort(parts);

		// die Anzahl der sinvollen Parts ermitteln
		int usefull = 0;
		for (Part part : parts)
		{
			if (!part.getCones().isEmpty() && (part.getCones().size() >= parts[0].getCones().size() * 0.7))
				usefull++;
		}

		// maximal 7 sinvolle Parts zulassen
		if (usefull > 7)
		{
			System.out.println("[SMARTDIVIDER] Reduziere sinvolle Parts von " + usefull + " auf 7.");
			usefull = 7;
		}
		for (int i = 0; i < usefull; i++)
		{
			System.out.println("[SMARTDIVIDER] part " + i + ": " + parts[i]);
		}

		// den besten Move ausprobieren. Dabei einfach den Winkel »brutforcen«

		double x = 0, y = 0, angle = 0;
		int best = -1;

		for (int i = 0; i < usefull; i++)
		{
			for (double angle0 = 0; angle0 <= Math.PI; angle0 += Math.PI / 60.0) // Abstand: 3°
			{
				Area a = new Area(parts[i].getX(), parts[i].getY(), angle0);
				int conecount = a.countCones(data);
				if (conecount > best)
				{
					x = parts[i].getX();
					y = parts[i].getY();
					angle = angle0;
					best = conecount;
				}
			}
		}

		// die Gerade korrigieren, sodass diese im Kreis liegt.
		LinearFunction fct = new LinearFunction(x, y, angle);
		Set<Point2D.Double> intersections = fct.intersections(data.getRadius());
		Point2D.Double result = null;
		for (Point2D.Double p : intersections)
		{
			System.out.println(Formatter.format(p));
			if (result == null)
				result = p;
			else
			{
				result.x = Math.min(result.x, p.x) + Math.abs(p.x - result.x) / 2.0;
				result.y = fct.y(result.x);
			}
		}

		// wenn die Gerade den Kreis nicht schneidet, einfach die vorherigen Werte zurückgeben
		if (result == null)
		{
			System.out.println("[SMARTDIVIDER] ERROR: Die Gerade " + fct + " schneidet den Kreis mit r=" + r + " nicht!");
			System.out.println("[SMARTDIVIDER]        Gebe angle="
							   + Formatter.formatRadians(angle) + ", x="
							   + Formatter.format(x) + ", y=" + Formatter.format(y) + " zurück.");
			return new Move(angle, x, y);
		}

		// den Move zurückgeben
		System.out.println("[SMARTDIVIDER] result: angle=" + Formatter.formatRadians(angle) + ", x="
						   + Formatter.format(result.x) + ", y=" + Formatter.format(result.y));
		return new Move(angle, result.x, result.y);
	}
}
