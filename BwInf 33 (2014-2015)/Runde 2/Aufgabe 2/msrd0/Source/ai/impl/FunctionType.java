/*
 * FunctionType
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai.impl;

import lombok.*;

@AllArgsConstructor
@ToString
enum FunctionType
{
	UPPER_FCT(1),
	MIDDLE_FCT(0),
	LOWER_FCT(-1);

	@Getter
	private double z;
}
