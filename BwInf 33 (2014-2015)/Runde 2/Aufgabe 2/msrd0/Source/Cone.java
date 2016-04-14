/*
 * Cone
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
package bwinf33_2.aufgabe2;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(exclude = { "state" })
public class Cone
{
	public static final int STANDING     = 0b00_00_00_01;
	public static final int OVERTHROWN   = 0b00_00_00_10;
	public static final int OVERTHROWING = 0b00_00_00_11;

	@Getter @Setter
	@NonNull
	public double x, y;

	@Getter @Setter
	private int state = STANDING;

	public boolean isOverthrown ()
	{
		return ((getState() & OVERTHROWN) != 0);
	}
}
