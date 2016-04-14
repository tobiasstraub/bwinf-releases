/*
 * main.cpp
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

#include "inputdataparser.h"
#include "seilschaftensolver.h"

#include <QCoreApplication>
#include <QCommandLineParser>

int main(int argc, char *argv[])
{
	QCoreApplication app(argc, argv);
	QCoreApplication::setApplicationName("Seilschaften");
	
	// Argumente parsen
	QCommandLineParser parser;
	parser.setApplicationDescription("Aufgabe 1 - 33. BwInf");
	parser.addHelpOption();
	parser.addPositionalArgument("file", "Die Datei, aus der die Eingabedaten gelesen werden sollen.", "[<file>]");
	QCommandLineOption verboseOption(QStringList() << "v" << "verbose", "Gibt genauere Details zum aktuell laufenden Vorgang aus.");
	parser.addOption(verboseOption);
	QCommandLineOption exampleOption(QStringList() << "e" << "example", "Beispiel Nummer n vom BwInf benutzen.", "n");
	parser.addOption(exampleOption);
	parser.process(app);
	
	bool verbose = parser.isSet(verboseOption);
	
	// Algorithmus instantiieren
	SeilschaftenSolver *solver = new SeilschaftenSolver(verbose);
	
	// Eingabedaten einlesen und weiterleiten
	InputDataParser data(solver, verbose);
	
	// Eingabequelle wählen
	QStringList args = parser.positionalArguments();
	if (args.length() > 0)
		data.parse(args[0]);
	else if (parser.isSet(exampleOption))
		data.parse(":/examples/seilschaften" + parser.value(exampleOption) + ".txt");
	else
		data.query();
}
