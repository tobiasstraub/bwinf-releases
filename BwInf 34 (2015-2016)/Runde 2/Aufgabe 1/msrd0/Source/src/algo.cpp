/*
 * algo
 * 
 * Copyright (C) 2016 Dominic S. Meiser <meiserdo@web.de>
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
#include "algo.h"

#include <algorithm>
#include <iostream>
#include <limits>
#include <set>
#include <vector>
using namespace std;

#include <QDebug>
#include <QMessageBox>

const double dinf = numeric_limits<double>::max();

#define stepRate 0.01

bool operator< (const QPointF &a, const QPointF &b)
{
	return (a.x()<b.x() || (a.x()==b.x() && a.y()<b.y()));
}

//!begin doku Der Algorithmus zum Bewerten
//!doku Die \texttt{rate}-Methode nimmt den Kuchen als \texttt{QPainterPath} und die Kerzen
//!doku als \texttt{QList<QPointF>} entgegen und berechnet die Gleichmäßigkeit, so wie es
//!doku die Lösungsidee beschreibt.
double rate(const QPainterPath &path, const QList<QPointF> &candles)
{
	//!doku In diesen Variablen werden Minimum, Maximum und Durchschnitt gespeichert
	double min=dinf, max=-1, avg=0;
	//!doku In diesen Variablen werden die entsprechenden Werte des Kuchens gespeichert
	double xmin=dinf, xmax=0, ymin=dinf, ymax=0;
	//!doku Diese Variable zählt mit, wieviele Abstände ich bereits angeschaut habe. Dies
	//!doku ist wichtig, um den Durchschnitt zu bestimmen.
	int count = 0;
	
	//!doku Zuerst die Adjazenzmatrix füllen
	vector<vector<double>> adj(candles.size()+1, vector<double>(candles.size()+1, dinf));
	for (int i = 0; i < candles.size(); i++)
	{
		//!doku Der Abstand einer Kerze zu sich selbst ist $\infty$
		adj[i+1][i+1] = dinf;
		
		//!doku Den Abstand zum Rand bestimmen. Dabei werden die Punkte, die den Rand
		//!doku approximieren, durch die Methode \texttt{QPainterPath::pointAtPercent}
		//!doku bestimmt.
		double d = dinf;
		for (double r = 0; r < 1; r += stepRate)
		{
			auto p = path.pointAtPercent(r);
			double xd = p.x() - candles[i].x();
			double yd = p.y() - candles[i].y();
			if (i == 0) // only do this once
			{
				xmin = std::min(xmin, p.x());
				xmax = std::max(xmax, p.x());
				ymin = std::min(ymin, p.y());
				ymax = std::max(ymax, p.y());
			}
			d = std::min(d, xd*xd + yd*yd);
		}
		adj[i+1][0] = sqrt(d);
		adj[0][i+1] = adj[i+1][0];
		
		//!doku Den Abstand zu den anderen Kerzen bestimmen. Diesen kann ich einfach
		//!doku über den Satz des Pythagoras bestimmen.
		for (int j = i+1; j < candles.size(); j++)
		{
			int xd = candles[i].x() - candles[j].x();
			int yd = candles[i].y() - candles[j].y();
			adj[i+1][j+1] = sqrt(xd*xd + yd*yd);
			adj[j+1][i+1] = adj[i+1][j+1];
		}
	}
	
	//!doku Der in der Lösungsidee beschriebene, abgewandelte Floyd-Warshall-Algorithmus
	for (size_t k = 1; k < adj.size(); k++)
		for (size_t i = 0; i < adj.size(); i++)
			for (size_t j = 0; j < adj.size(); j++)
				if (adj[i][k] < adj[i][j] && adj[k][j] < adj[i][j])
					adj[i][j] = dinf;
	
	//!doku Minimum, Durchschnitt und Maximum berechnen
	for (size_t i = 0; i < adj.size(); i++)
		for (size_t j = i+1; j < adj.size(); j++)
			if (adj[i][j] != dinf)
			{
				min = std::min(min, adj[i][j]);
				max = std::max(max, adj[i][j]);
				avg = (count * avg + adj[i][j]);
				avg /= ++count;
			}
	
	//!doku Sollten die Werte noch auf ihren Startwerten stehen, sind keine Kerzen angegeben
	//!doku worden. In diesem Fall gebe ich -1 zurück.
	if (min==dinf || max==-1) // no points found
		return -1;
	//!doku Jetzt noch den Wert bestimmen und durch die größtmögliche Abweichung teilen,
	//!doku wie in der Lösungsidee beschrieben.
	double xd = xmax - xmin, yd = ymax - ymin;
	Q_ASSERT(xd > 0 && yd > 0);
	return (std::max(avg-min, max-avg) / std::sqrt(xd*xd + yd*yd));
}
//!end doku

//!begin doku Der Algorithmus zum Verteilen
//!doku Die \texttt{spread}-Methode nimmt den Kuchen als \texttt{QPainterPath} und die Anzahl
//!doku an Kerzen entgegen und gibt eine Liste mit den Positionen der Kerzen zurück. Mir ist
//!doku bekannt, dass dies nicht zwingend die beste Verteilung sein muss.
QList<QPointF> spread(const QPainterPath &path, int numcandles)
{
	//!doku In diesen Variablen werden die entsprechenden Werte des Kuchens gespeichert
	double xmin=dinf, xmax=0, ymin=dinf, ymax=0;
	//!doku Die Punkte auf dem Rand werden mithilfe der Methode
	//!doku \texttt{QPainterPath::pointAtPercent} bestimmt.
	set<QPointF> border;
	for (double r = 0; r < 1; r += stepRate)
	{
		auto p = path.pointAtPercent(r);
		xmin = std::min(xmin, p.x());
		xmax = std::max(xmax, p.x());
		ymin = std::min(ymin, p.y());
		ymax = std::max(ymax, p.y());
		border.insert(p);
	}
	Q_ASSERT(xmin < xmax && ymin < ymax);
	//!doku Das Grid, das den Kuchen repräsentiert
	double field[(int)(1 / stepRate)][(int)(1 / stepRate)];
	double xv[(int)(1 / stepRate)], yv[(int)(1 / stepRate)]; // real values for the field index
	//!doku Eine Queue, sortiert nach der Summe aller $F$-Werte für das Feld.
	set<pair<double, QPointF>> q;
	//!doku Zunächst das Grid füllen.
	for (int i = 0; i < 1 / stepRate; i++)
	{
		xv[i] = xmin + (xmax - xmin) * stepRate * i;
		for (int j = 0; j < 1 / stepRate; j++)
		{
			if (i == 0)
				yv[j] = ymin + (ymax - ymin) * stepRate * j;
			
			//!doku Wenn der Punkt auf dem Kuchen liegt, den Wert wie in der Lösungsidee
			//!doku beschrieben berechnen und ihn der Queue hinzufügen.
			if (path.contains(QPointF(xv[i], yv[j])))
			{
				field[i][j] = 0;
				for (auto p : border)
				{
					double xd = xv[i] - p.x();
					double yd = yv[j] - p.y();
					field[i][j] += 1 / (xd*xd + yd*yd); // F = 1/(4*PI*E0) * (q1*q2)/r²
				}
				q.insert({field[i][j], QPointF(xv[i], yv[j])});
			}
			//!doku Ansonsten den Wert auf >1, hier 2, setzen.
			else
				field[i][j] = 2;
		}
	}
	//!doku Die Kerzen verteilen
	QList<QPointF> candles;
	for (int i = 0; i < numcandles; i++)
	{
		if (q.empty())
		{
			QMessageBox::critical(0, "ERROR", "Das Objekt ist schlecht definiert, ich kann "
											  "keine Punkte darin finden!", QMessageBox::Ok);
			return QList<QPointF>();
		}
		//!doku Den ersten Punkt aus der Queue nehmen und die Kerze dort positionieren.
		auto a = * q.begin();
		candles << a.second;
		//!doku Anschließend die Queue leeren, die Werte für die Felder im Grid updaten
		//!doku und die Queue wieder füllen.
		q.clear();
		for (int i = 0; i < 1 / stepRate; i++)
		{
			for (int j = 0; j < 1 / stepRate; j++)
			{
				double xd = xv[i] - a.second.x();
				double yd = yv[j] - a.second.y();
				field[i][j] += 1 / (xd*xd + yd*yd);
				q.insert({field[i][j], QPointF(xv[i], yv[j])});
			}
		}
	}
	
	//!doku Und zum Schluss die Liste der Positionen der Kerzen zurückgeben.
	Q_ASSERT(candles.size() == numcandles);
	return candles;
}
//!end doku
