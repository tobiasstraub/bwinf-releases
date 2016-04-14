/*
 * solution.h
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

#ifndef SOLUTION_H
#define SOLUTION_H

#include "inputdata.h"
#include "move.h"

#include <QList>

/**
 * Diese Klasse speichert eine Lösung inklusive des aktuellen Standes.
 */
class Solution : public QList<Move>
{
public:
	Solution (const QList<Element> &upList, const QList<Element> &downList)
		: QList()
		, up(upList)
		, down(downList)
	{
	}
	
	Solution (const QList<Move> moves, const QList<Element> &upList, const QList<Element> &downList)
		: QList(moves)
		, up(upList)
		, down(downList)
	{
	}
	
	Solution (const Solution &other)
		: QList(other)
		, up(other.up)
		, down(other.down)
	{
	}
	
	Solution ()
	{
	}
	
	QList<Element> up;
	QList<Element> down;
	
	/** Überprüft, ob diese Lösung null ist. */
	bool isNull () const
	{
		return (isEmpty() && up.isEmpty() && down.isEmpty());
	}
	
	/**
	 * Überprüft, ob die up und down Liste von this und other identisch sind, NICHT ob die Lösung
	 * bzw. der Lösungsweg der gleiche ist.
	 */
	bool operator == (const Solution &other) const
	{
		return ((up == other.up) && (down == other.down));
	}
};

// hash function für die FilteredQueue
namespace std
{

template <>
struct hash<Solution>
{
	// für einen besseren hash müsste ich sortieren
	size_t operator() (const Solution &s) const
	{
		return s.up.size();
	}
};

}

#endif // SOLUTION_H
