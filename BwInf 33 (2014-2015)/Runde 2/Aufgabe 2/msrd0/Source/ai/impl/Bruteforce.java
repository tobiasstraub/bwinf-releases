/*
 * Bruteforce
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai.impl;

import bwinf33_2.aufgabe2.SceneData;
import bwinf33_2.aufgabe2.ai.AI;
import bwinf33_2.aufgabe2.ai.Move;

import java.awt.geom.Point2D;
import java.util.*;

import lombok.NoArgsConstructor;

// ai-import Area
// ai-import LinearFunction

/**
 * Diese KI "brutforct" nach dem besten Wurf.
 */
@NoArgsConstructor
@SuppressWarnings("unused")
public class Bruteforce implements AI
{
	@Override
	public Move move (SceneData data)
	{
		double move_x = 0, move_y = 0, move_angle = 0;
		int cones = -1;

		// Schrittweite bestimmen
		double step = 1.0 / 3.0;
		double angle_step = 180.0 / 2.0;

		// mögliche Werte ausprobieren
		for (double x = -data.getRadius(); x <= data.getRadius(); x += step)
		{
			for (double y = -data.getRadius(); y <= data.getRadius(); y += step)
			{
				for (double angle = 0; angle <= Math.PI; angle += Math.PI / angle_step)
				{
					int cones0 = new Area(x, y, angle).countCones(data);

					// evtl. diesen Wurf als besten speichern
					if (cones0 > cones)
					{
						move_x = x;
						move_y = y;
						move_angle = angle;
						cones = cones0;
					}
				}
			}
		}

		// den Wurf gültig machen
		LinearFunction fct = new LinearFunction(move_x, move_y, move_angle);
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

		// den besten gefundenen Wurf zurückgeben
		return new Move(move_angle, result.x, result.y);
	}
}
