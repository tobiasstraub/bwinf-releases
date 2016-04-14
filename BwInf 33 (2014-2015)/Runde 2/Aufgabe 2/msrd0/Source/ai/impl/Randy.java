/*
 * Randy
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai.impl;

import bwinf33_2.aufgabe2.SceneData;
import bwinf33_2.aufgabe2.ai.AI;
import bwinf33_2.aufgabe2.ai.Move;
import lombok.NoArgsConstructor;

// ai-import Formatter

@NoArgsConstructor
@SuppressWarnings("unused")
public class Randy implements AI
{
	@Override
	public Move move (SceneData data)
	{
		double x, y;
		do
		{
			x = Math.random() * 2 * data.getRadius() - data.getRadius();
			y = Math.random() * 2 * data.getRadius() - data.getRadius();
		}
		while (x * x + y * y > data.getRadius() * data.getRadius());

		double angle = Math.random() * Math.PI;

		System.out.println("[RANDY] result: angle=" + Formatter.formatRadians(angle)
						   + ", x=" + Formatter.format(x) + ", y=" + Formatter.format(y));
		return new Move(angle, x, y);
	}
}
