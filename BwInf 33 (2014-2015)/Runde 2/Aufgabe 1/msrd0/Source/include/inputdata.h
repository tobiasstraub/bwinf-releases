/*
 * inputdata.h
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

#ifndef INPUTDATA_H
#define INPUTDATA_H

#include <QList>
#include <QString>

// Zeugs definieren, dass gebraucht wird, um die Eingabedaten zu speichern

class Element
{
public:
	enum Type : char
	{
		Stone = 'S',
		Person = 'P'
	};
	
	enum State : char
	{
		Up = '^',
		Down = '_'
	};
	
	Type type;
	unsigned int weight;
	State state;
	
	bool isUp () const { return (state == Up); }
	bool isDown () const { return (state == Down); }
	bool isStone () const { return (type == Stone); }
	bool isPerson () const { return (type == Person); }
	
	bool operator== (const Element &other) const
	{
		return ((type == other.type)
				&& (weight == other.weight)
				&& (state == other.state));
	}
	
	QString toString () const
	{
		return QString("%1(%2, %3)")
				.arg(
					(type == Stone ? "Stein" : type == Person ? "Person" : "Unknown"),
					QString::number(weight),
					(state == Up ? "Oben" : state == Down ? "Unten" : "Unknown")
				);
	}
};

struct InputData
{
	unsigned int d;
	QList<Element> elements;
};

// hash funktion für Element
namespace std
{

template <>
struct hash<Element>
{
	size_t operator() (const Element &e) const
	{
		size_t result = e.weight;
		result <<= 1;
		result |= (e.isPerson() ? 0b1 : 0b0);
		return result;
	}
};

}

#endif // INPUTDATA_H
