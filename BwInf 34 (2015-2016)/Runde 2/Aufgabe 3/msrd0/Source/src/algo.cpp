#include "algo.h"

#include <QDebug>

using namespace algo;

//!begin doku Der Algorithmus zur Kategorisierung der Felder
//!doku Die Methode \texttt{solveWorld} nimmt einen Pointer auf die Welt entgegen
//!doku und speichert den Status der Felder der Welt in dieser.
void solveWorld(World *w)
{
	//!doku Zunächst eine bereits vorhandene Lösung löschen
	if (w->hasResult())
	{
		qDebug() << "Warning: Removing existing solution from world";
		for (quint32 i = 0; i < w->width(); i++)
			for (quint32 j = 0; j < w->height(); j++)
				w->field(i, j)->state = World::UnknownState;
	}
	
	//!doku Den Algorithmus einmal aufrufen und alle Felder, die keinen Ausgang
	//!doku erreichen können, als \textit{Failing} markieren.
	for (quint32 i = 0; i < w->width(); i++)
	{
		for (quint32 j = 0; j < w->height(); j++)
		{
			if (w->field(i, j)->type != World::Wall && w->field(i, j)->state == World::UnknownState)
			{
				if (!findPath(w, QPoint(i,j), World::Exit))
					w->field(i, j)->state = World::Failing;
			}
		}
	}
	//!doku Den Algorithmus nochmal aufrufen und alle Felder, die ein \textit{Failing}
	//!doku Feld erreichen können als \textit{Unsicher} und die restlichen als
	//!doku \textit{Sicher} markieren.
	for (quint32 i = 0; i < w->width(); i++)
	{
		for (quint32 j = 0; j < w->height(); j++)
		{
			if (w->field(i, j)->type != World::Wall && w->field(i, j)->state == World::UnknownState)
			{
				if (findPath(w, QPoint(i,j), World::Empty, World::Failing))
					w->field(i, j)->state = World::Unsave;
				else
					w->field(i, j)->state = World::Save;
			}
		}
	}
	
	w->setHasResult(true);
}
//!end doku

//!begin doku Der Algorithmus zum Bestimmen des Nachbarn
//!doku Die Methode \texttt{neighbor} nimmt einen Pointer auf die Welt, den Startpunkt
//!doku \texttt{start} und \texttt{xinc} und \texttt{yinc} entgegen. Sie geht in jedem
//!doku Schritt um \texttt{xinc|yinc} Felder weiter und stopt bei einer Wand oder einem
//!doku Ausgang.
QPoint algo::neighbor(World *w, const QPoint &start, int xinc, int yinc)
{
	Q_ASSERT(qAbs(xinc) <= 1);
	Q_ASSERT(qAbs(yinc) <= 1);
	
	//!doku Solange weitergehen, bis das Feld nicht \textit{Empty} ist.
	int x = start.x(), y = start.y();
	while (w->field(x, y)->type == World::Empty)
	{
		x += xinc;
		y += yinc;
	}
	//!doku Wenn das Feld eine Wand ist, interesiert mich das Feld davor, da das Yamyam
	//!doku nicht in die Wand reinlaufen kann.
	if (w->field(x, y)->type == World::Wall)
	{
		x -= xinc;
		y -= yinc;
	}
	
	return QPoint(x, y);
}
//!end doku

//!begin doku Der Algorithmus zum Überprüfen, ob ich ein bestimmtes Feld erreichen kann
//!doku Die Methode \texttt{findPath} nimmt einen Pointer auf die Welt, den Startpunkt
//!doku \texttt{start}, den zu suchenden Typ bzw. den zu suchenden Status und ein Set
//!doku mit den bereits besuchten Feldern entgegen und gibt zurück, ob ein solches Feld
//!doku erreicht werden kann.
bool algo::findPath(World *w, const QPoint &start,
					World::FieldType toFind, World::FieldState state,
					QSet<QPoint> *searched)
{
	Q_ASSERT(searched);
	
	//!doku Wenn das aktuelle Feld den Kriterien entspricht, habe ich einen Pfad gefunden
	if ((state == World::UnknownState && w->field(start)->type == toFind)
			|| (state != World::UnknownState && w->field(start)->state == state))
		return true;
	//!doku Den aktuellen Punkt als besucht markieren
	searched->insert(start);
	
	//!doku Alle Nachbarn finden. Ich möchte hier bemerken, dass ich hierbei jedesmal
	//!doku den Algorithmus aufrufe, um den Nachbarn zu finden, statt dies für jedes
	//!doku Feld nur einmal zu machen und das Ergebnis zu speichern. Da alle Beispiele
	//!doku auf meinem Rechner in unter 10 Millisekunden laufen, war es mir aber den Arbeitsspeicher
	//!doku nicht wert. Die Laufzeit hier ($\mathcal{O}(n^2 + n^2 \cdot l)$) entspricht
	//!doku also nicht der aus der Lösungsidee ($\mathcal{O}(n^2 + n \cdot l)$). Dafür
	//!doku ist die Arbeitsspeicherkomplexität hier konstant, während sie bei der Lösungsidee
	//!doku $\mathcal{O}(n)$ wäre.
	QSet<QPoint> neighbors;
	neighbors << neighbor(w, start, -1,  0);
	neighbors << neighbor(w, start,  1,  0);
	neighbors << neighbor(w, start,  0, -1);
	neighbors << neighbor(w, start,  0,  1);
	//!doku Bereits besuchte Nachbarn wieder entfernen. Da in Qt ein Set als Hash Set
	//!doku implementiert ist und neighbors eine maximale Größe von 4 hat, geht dies in
	//!doku konstanter Laufzeit, wie in der Lösungsidee vorgeschrieben.
	for (auto it = neighbors.begin(); it != neighbors.end();)
	{
		if (searched->contains(*it))
			it = neighbors.erase(it);
		else
			it++;
	}
	
	//!doku Rekursiv alle verbleibenden Nachbarn durchsuchen
	for (QPoint p : neighbors)
		if (findPath(w, p, toFind, state, searched))
			return true;
	return false;
}
//!end doku

uint qHash(const QPoint &p)
{
	return (p.x() << 8) ^ p.y();
}
