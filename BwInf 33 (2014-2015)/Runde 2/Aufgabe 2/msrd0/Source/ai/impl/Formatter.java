/*
 * Formatter
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bwinf33_2.aufgabe2.ai.impl;

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.*;

import lombok.NonNull;

/**
 * Diese Klasse dient zum Formatieren von Zahlen.
 */
class Formatter
{
	private static NumberFormat numberFormat = null;
	public static NumberFormat getNumberFormat ()
	{
		if (numberFormat == null)
		{
			numberFormat = NumberFormat.getInstance(Locale.GERMANY);
			numberFormat.setMaximumFractionDigits(4);
			numberFormat.setMinimumFractionDigits(0);
		}
		return numberFormat;
	}

	public static String format (double d)
	{
		return getNumberFormat().format(d);
	}

	public static String format (long l)
	{
		return getNumberFormat().format(l);
	}

	public static String format (@NonNull Point2D.Double p)
	{
		return "Point(x=" + format(p.x) + ";y=" + format(p.y) + ")";
	}

	public static String formatRadians (double radians)
	{
		return format(radians) + "(=" + format(Math.toDegrees(radians)) + "°)";
	}
}
