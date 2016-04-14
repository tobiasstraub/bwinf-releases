/*
 * seilschaftensolver.h
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

#ifndef SEILSCHAFTENSOLVER_H
#define SEILSCHAFTENSOLVER_H

#include "inputdata.h"
#include "move.h"
#include "solution.h"

#include <sys/types.h>

#include <QTextStream>

class SeilschaftenSolver
{
public:
	explicit SeilschaftenSolver (bool verbose = false);
	
	/** Gibt zurück ob die verbose-Option aktiviert ist. */
	bool verbose () const { return _verbose; }
	
	/** Führt den Algorithmus für die übergebenen Eingabedaten aus. */
	void execute (InputData data);
	
private:
	
	void solve();
	
	void printState (QTextStream *out) const;
	void printState (QTextStream *out, const Solution &solution) const;
	
	QList<Element> up, down;
	uint d;
	uint weight, maxweight;
	
	QList<Move> solution;
	
	bool _verbose;
	
};

#endif // SEILSCHAFTENSOLVER_H
