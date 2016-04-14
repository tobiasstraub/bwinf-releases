/*
 * AI
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai;

import bwinf33_2.aufgabe2.SceneData;

/**
 * Dieses Interface muss von jeder KI implementiert werden.
 */
public interface AI
{
	/**
	 * Wird aufgerufen, wenn die KI einen Zug machen soll. Das SceneData-Objekt enthält Radius und Kegel. Achtung: Es
	 * sind auch bereits umgeworfene Kegel enthalten!
	 */
	public Move move (SceneData data);

	/**
	 * Gibt den Namen der KI zurück. By default ist das der Klassenname.
	 */
	public default String getName ()
	{
		return getClass().getSimpleName();
	}
}
