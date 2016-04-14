/*
 * inputdataparser.h
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

#ifndef INPUTDATAPARSER_H
#define INPUTDATAPARSER_H

#include "inputdata.h"

class SeilschaftenSolver;

/**
 * Diese Klasse liest Eingabedaten ein und gibt sie per signals an den verarbeitenden Algorithmus
 * weiter. Dabei können die Eingabedaten aus einer Datei oder von der Komandozeile eingelesen werden.
 */
class InputDataParser
{
	
public:
	explicit InputDataParser(SeilschaftenSolver *solver, bool verbose = false);
	~InputDataParser();
	
	/** Parst die angegebene Datei. */
	void parse (const QString &file);
	
	/** Fragt den Benutzer nach den Eingabedaten. */
	void query ();
	
	/** Gibt zurück ob die verbose-Option aktiviert ist. */
	bool verbose () const { return _verbose; }
	
private:
	SeilschaftenSolver *_solver;
	bool _verbose;
	
};

#endif // INPUTDATAPARSER_H
