/*
 * move.cpp
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

#include "move.h"

bool Move::operator== (const Move &other) const
{
	return ((goingdown == other.goingdown) && (goingup == other.goingup));
}

QString Move::toString () const
{
	QString str;
	
	if (!goingdown.isEmpty())
	{
		for (int i = goingdown.size()-1; i >= 0; i--)
			str.append(goingdown[i].toString()).append(i!=0 ? ", " : QByteArray());
		str.append(" fahren runter und ");
	}
	else
		str.append("Niemand fährt runter und ");
	
	if (!goingup.isEmpty())
	{
		for (int i = goingup.size()-1; i >= 0; i--)
			str.append(goingup[i].toString()).append(i!=0 ? ", " : QByteArray());
		str.append(" hoch.");
	}
	else
		str.append("niemand hoch.");
	
	return str;
}
