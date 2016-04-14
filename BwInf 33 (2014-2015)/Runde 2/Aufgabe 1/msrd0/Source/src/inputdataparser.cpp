/*
 * inputdataparser.cpp
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

#include <stdio.h>
#include <iostream>

#include <QCoreApplication>
#include <QFile>
#include <QRegExp>
#include <QTextStream>

// Macro zum Ausgeben von Fehlern und anschließendem beenden
#define ERROR_IF(condition, message) \
	if (condition) \
	{ \
		fprintf(stderr, "ERROR: %s\n", QString(message).toStdString().data()); \
		return; \
	}

// Macro zum Lesen einer Zeile aus cin
#define CIN_LINE(variable) \
	{ \
		char data[100]; \
		std::cin.getline(data, 100); \
		variable = data; \
	} \
	variable = variable.trimmed();

InputDataParser::InputDataParser(SeilschaftenSolver *solver, bool verbose)
	: _solver(solver)
	, _verbose(verbose)
{
}

InputDataParser::~InputDataParser ()
{
	delete _solver;
}

void InputDataParser::parse (const QString &file)
{
	// Datei öffnen
	QFile f(file);
	ERROR_IF(!f.open(QIODevice::ReadOnly), "Die Datei " + file + " kann entweder nicht gefunden oder nicht gelesen werden.")
			
	InputData inputdata;
	
	// Zeile 1: Schranke für Gewichtsunterschied d
	QString line = f.readLine().trimmed();
	bool ok = false;
	inputdata.d = line.toInt(&ok, 10);
	ERROR_IF(!ok, file + ":1: Schranke d kann nicht als Dezimalzahl eingelesen werden.")
			
	// Weitere Zeilen: jeweils Angabe zu Gewicht und Ausgangsposition einer Person bzw. eines Steins
	// NOTE: Leerzeilen werden wie Zeilenende betrachtet
	QRegExp regex("([PS])\\s*(\\d+)\\s*([_^])");
	for (int i = 2; !(line = f.readLine().trimmed()).isEmpty(); i++)
	{
		ERROR_IF(!regex.exactMatch(line), file + ":" + i + ": Die Zeile passt nicht auf das vorgegebene Muster '" + regex.pattern() + "'.")
		inputdata.elements.append({ (regex.cap(1) == "P" ? Element::Person : Element::Stone),
									regex.cap(2).toUInt(0, 10),
									(regex.cap(3) == "_" ? Element::Down : Element::Up) });
	}
	
	// Datei schließen
	printf("Eingabedaten erfolgreich aus der Datei %s gelesen.\n", file.toStdString().data());
	f.close();
	
	// Eingabedaten weiterleiten
	_solver->execute(inputdata);
}

void InputDataParser::query ()
{
	InputData inputdata;
	QString line;
	
	// Benutzer nach der Schranke für den Gewichtsunterschied d fragen
	bool ok = false;
	while (!ok)
	{
		printf("Schranke für den Gewichtsunterschied (d): ");
		CIN_LINE(line)
		inputdata.d = line.trimmed().toInt(&ok, 10);
	}
	
	// Benutzer nach den Elementen (Personen & Steine) fragen
	QRegExp regex("([PS])\\s*(\\d+)\\s*([_^])");
	printf("Personen und Steine (Format: '%s'). Beenden mit einer Zeile die nur einen Punkt enthält.\n", regex.pattern().toStdString().data());
	while (true)
	{
		printf("Seilschaften:%d > ", inputdata.elements.size());
		CIN_LINE(line)
		
		if (line == ".")
			break;
		
		if (regex.exactMatch(line))
			inputdata.elements.append({ (regex.cap(1) == "P" ? Element::Person : Element::Stone),
										regex.cap(2).toUInt(0, 10),
										(regex.cap(3) == "_" ? Element::Down : Element::Up) });
	}
	
	// Eingabedaten weiterleiten
	_solver->execute(inputdata);
}
