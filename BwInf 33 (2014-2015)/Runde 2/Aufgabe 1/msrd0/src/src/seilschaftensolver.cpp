/*
 * seilschaftensolver.cpp
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

#include "filteredqueue.h"
#include "seilschaftensolver.h"

#include <math.h>
#include <stdio.h>

#include <QTime>

SeilschaftenSolver::SeilschaftenSolver (bool verbose)
	: _verbose(verbose)
{
}

void SeilschaftenSolver::printState (QTextStream *out) const
{
	*out << "    OBEN:\n";
	for (Element e : up)
		*out << "     - " << e.toString() << "\n";
	*out << "    UNTEN:\n";
	for (Element e : down)
		*out << "     - " << e.toString() << "\n";
	out->flush();
}

void SeilschaftenSolver::printState (QTextStream *out, const Solution &solution) const
{
	*out << "    SOLUTION:\n";
	*out << "     SCHRITTE:\n";
	for (Move m : solution)
		*out << "     - " << m.toString() << "\n";
	*out << "     OBEN:\n";
	for (Element e : solution.up)
		*out << "      - " << e.toString() << "\n";
	*out << "     UNTEN:\n";
	for (Element e : solution.down)
		*out << "      - " << e.toString() << "\n";
	out->flush();
}

/// Methode um alle Sublisten einer Liste zu finden
template <typename E>
QList<QList<E>> sublists (QList<E> list)
{
	QList<QList<E>> subsets;
	QList<E> empty;
	subsets << empty;
	
	for (int i = 0; i < list.size(); i++)
	{
		QList<QList<E>> subsetsTemp = subsets;
		for (int j = 0; j < subsetsTemp.size(); j++)
			subsetsTemp[j] << list[i];
		subsets << subsetsTemp;
	}
	
	return subsets;
}

void SeilschaftenSolver::solve ()
{	
	FilteredQueue<Solution> pathes;
	pathes << Solution(up, down);
	
	// jeden gefundenen Pfad weitergehen bis eine Lösung gefunden wurde
	for (uint itcount = 1; !pathes.isEmpty(); itcount++)
	{
		Solution solution = pathes.pop_first();
		
		if (itcount > 1)
			printf("\x1b[1A");
		printf("\x1b[2KIteration: \t%u\t\tQueue Size: \t%d\n", itcount, pathes.size());
		
		// Sublisten für die up und down Liste finden
		QList<QList<Element>> sup = sublists(solution.up);
		QList<QList<Element>> sdown = sublists(solution.down);
		
		// schauen, ob es beim Einladen bzw. Ausladen von Steinen Probleme geben könnte
		{
			bool nopersonsup = true, nopersonsdown = true;
			for (Element e : solution.up)
			{
				if (e.isPerson())
				{
					nopersonsup = false;
					break;
				}
			}
			for (Element e : solution.down)
			{
				if (e.isPerson())
				{
					nopersonsdown = false;
					break;
				}
			}
			
			if (nopersonsup)
				sup = QList<QList<Element>>() << (solution.isEmpty() ? QList<Element>() : solution.last().goingup);
			if (nopersonsdown)
				sdown = QList<QList<Element>>() << (solution.isEmpty() ? QList<Element>() : solution.last().goingdown);
		}
		
		// durch alle Sublisten-Kombinationen durchiterieren
		for (QList<Element> goingdown : sup)
		{
			// Überprüfen dass goingdown nicht zu schwer
			uint weightgoingdown = 0;
			bool stoneonlyup = true;
			for (Element e : goingdown)
			{
				weightgoingdown += e.weight;
				if (e.isPerson())
					stoneonlyup = false;
			}
			if (!stoneonlyup && (weightgoingdown > maxweight))
				continue;
			
			// clone solution
			Solution s0(solution);
			
			// Die Elemente aus goingdown nach unten verschieben
			for (Element e : goingdown)
			{
				s0.up.removeOne(e);
				s0.down << e;
			}
			
			// sdown-Sublisten durchiterieren, zu schwere direkt löschen
			for (int j = 0; j < sdown.size();)
			{
				QList<Element> goingup = sdown[j];
				if (goingdown.isEmpty() && goingup.isEmpty())
				{
					j++;
					continue;
				}
				
				// Überprüfen dass goingup nicht zu schwer bzw. d nicht überschritten ist
				uint weightdown = 0;
				bool stoneonlydown = true;
				for (Element e : goingup)
				{
					weightdown += e.weight;
					if (e.isPerson())
						stoneonlydown = false;
				}
				if ((weightgoingdown <= weightdown) // Die Körbe fahren nur wenn das Gewicht oben größer ist
						|| (!(stoneonlydown && stoneonlyup) && (weightgoingdown - weightdown > d))) // d überschritten
				{
					j++;
					continue;
				}
				else if (!stoneonlydown && (weightdown > maxweight))
				{
					sdown.removeAt(j);
					continue;
				}
				
				// clone solution
				Solution s(s0);
				s << Move{ goingdown, goingup };
				
				// Die Elemente von goingup nach oben verschieben
				for (Element e : goingup)
				{
					s.down.removeOne(e);
					s.up << e;
				}
				
				// Wenn keine Personen mehr in der oberen Liste sind abbrechen
				bool end = true;
				for (Element e : s.up)
				{
					if (e.isPerson())
					{
						end = false;
						break;
					}
				}
				if (end)
				{	
					this->solution = s;
					return;
				}
				
				// Ansonsten s zu den verfügbaren Pfaden adden
				pathes.push_back(s);
				
				// increase j
				j++;
			} // for j=0; j<sdown.size;
		} // for goingdown : sdown
		
	} // for solution : pathes
}

void SeilschaftenSolver::execute (InputData data)
{
	QTextStream sout(stdout);
	
	// Eingabedaten verarbeiten
	d = data.d;
	for (Element e : data.elements)
	{
		if (e.isUp())
			up << e;
		else
			down << e;
		weight += e.weight;
	}
	maxweight = ceil(weight / 2.0) + d;
	
	sout << " -- Eingabedaten erhalten:\n";
	sout << "  - Gewichtsschranke d: " << data.d << "\n";
	sout << "  - Status:\n";
	printState(&sout);
	if (verbose())
		sout << "  - Berechnetes Maximalgewicht eines Korbes: " << maxweight << "\n";
	
	sout << " -- Starte Algorithmus (Breitensuche)\n";
	sout.flush();
	QTime time = QTime::currentTime();
	
	// Algorithmus laufen lassen
	solve();
	
	// Lösung ausgeben
	sout << " -- Algorithmus beendet, time consumed: " << (time.elapsed() / 1000.0) << " sec\n";
	sout << " -- L\xf6""sung:\n";
	if (!solution.isEmpty())
	{
		uint count = 0;
		for (Move m : solution)
		{
			count++;
			sout << "  - " << count << "." << (count < 10 ? "    " : (count < 100 ? "   " : "  ")) << m.toString() << "\n";
			for (Element e : m.goingup)
			{
				down.removeOne(e);
				up << e;
				e.state = Element::Up;
			}
			for (Element e : m.goingdown)
			{
				up.removeOne(e);
				down << e;
				e.state = Element::Down;
			}
		}
	}
	sout << " -- Endzustand:\n";
	printState(&sout);
	sout.flush();
}
