/*
 * Divider
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

// ai-import Formatter
// ai-import LinearFunction
// ai-import Part

/**
 * Diese KI teilt das Spielfeld in Quadrate ein und berechnet daraus den vermeintlich besten Wurf.
 */
@NoArgsConstructor
@SuppressWarnings("unused")
public class Divider implements AI
{
	@SuppressWarnings("unchecked")
	@Override
	public Move move (SceneData data)
	{
		final int DEPTH = (int)Math.ceil(data.getRadius() / 2.0) + 1;
		System.out.println("[DIVIDER] cones: " + Formatter.format(data.getCones().size())
						   + "; radius: " + Formatter.format(data.getRadius())
						   + "; depth: " + Formatter.format(DEPTH));

		// die verschiedenen Parts bilden und die Kegel in die Parts "aufteilen"
		List<Part> parts[] = new List[DEPTH + 1];
		parts[0] = Arrays.asList(
				new Part(-data.getRadius(), -data.getRadius(), 2 * data.getRadius(), 2 * data.getRadius(),
						 data.getCones()));
		for (int depth = 1; depth <= DEPTH; depth++)
		{
			parts[depth] = new ArrayList<>();
			for (Part part : parts[depth - 1])
			{
				List<Part> subs = new ArrayList<>(4);
				subs.add(new Part(part.x, part.y, part.width / 2.0, part.height / 2.0));
				subs.add(new Part(part.x + part.width / 2.0, part.y, part.width / 2.0, part.height / 2.0));
				subs.add(new Part(part.x, part.y + part.height / 2.0, part.width / 2.0, part.height / 2.0));
				subs.add(new Part(part.x + part.width / 2.0, part.y + part.height / 2.0, part.width / 2.0,
								  part.height / 2.0));

				for (Cone c : part.cones)
				{
					if (c.isOverthrown())
						continue;
					for (Part sub : subs)
					{
						if ((c.getX() >= sub.x) && (c.getX() <= sub.x + sub.width)
							&& (c.getY() >= sub.y) && (c.getY() <= sub.y + sub.height))
							sub.cones.add(c);
					}
				}

				for (Part sub : subs)
				{
					if (!sub.cones.isEmpty())
						parts[depth].add(sub);
				}
			}
		}

		// die Parts sortieren
		Collections.sort(parts[DEPTH]);
//		for (int i = 0; i < parts[DEPTH].size() && i < 10; i++)
//		{
//			System.out.println("[DIVIDER] part " + Formatter.format(i) + ": " + parts[DEPTH].get(i));
//		}

		// eine Linie durch die beiden Parts mit den meisten Kegeln legen
		Part best = parts[DEPTH].get(0), second = (parts[DEPTH].size() > 1 ? parts[DEPTH].get(1) : null);
		System.out.println("[DIVIDER] best: " + best + "; second: " + second);
		double x = best.x + best.width / 2.0;
		double y = best.y + best.height / 2.0;
		LinearFunction fct = new LinearFunction(x, y, 0);
		if (second != null)
		{
			double sx = second.x + second.width / 2.0;
			double sy = second.y + second.height / 2.0;
			fct = new LinearFunction(x, y, sx, sy);
		}

		// den Zielpunkt herausfinden
		Point2D.Double destination = best.getMiddle(fct, data.getRadius());
		double angle = Math.atan(fct.getM());
		fct = new LinearFunction(destination.x, destination.y, angle);
		System.out.println("[DIVIDER] (" + Formatter.format(x) + "|" + Formatter.format(y)
						   + ") → (" + Formatter.format(destination.x) + "|" + Formatter.format(destination.y)
						   + ") → " + fct);
		x = destination.x;
		y = destination.y;

		// den Move zurückgeben
		System.out.println("[DIVIDER] result: angle=" + Formatter.formatRadians(angle) + ", x="
						   + Formatter.format(x) + ", y=" + Formatter.format(y));
		return new Move(angle, x, y);
	}
}
