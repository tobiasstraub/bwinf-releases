/*
 * IllegalPartException
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai.impl;

import lombok.NoArgsConstructor;

@NoArgsConstructor
class IllegalPartException extends RuntimeException
{
	public IllegalPartException (String msg)
	{
		super(msg);
	}
}
