/*
 * closedlist.cpp
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

#include "closedlist.h"

#include <QtGlobal>

template <typename T>
T ClosedList<T>::first () const
{
	// überprüfen, dass die Liste nicht leer ist
	Q_ASSERT(!isEmpty());
	
	// das erste Element in der Liste suchen, dass noch nicht angeschaut wurde, und
	// anschließend speichern dass das Element angeschaut wurde
	T elem;
	ClosedListData<T> *data;
	do
	{
		elem = data->data;
		data = data->next;
	} while (_viewed.contains(elem) && data);
	_viewed << elem;
	return elem;
}

template <typename T>
T ClosedList<T>::pop_first ()
{
	// überprüfen, dass die Liste nicht leer ist
	Q_ASSERT(!isEmpty());
	
	// das erste Element in der Liste suchen, dass noch nicht angeschaut wurde, und
	// anschließend speichern dass das Element angeschaut wurde. Alle bereits angeschauten
	// Elemente inklusive dem gefundenen entfernen.
	T elem;
	do
	{
		elem = _first->data;
		_first = _first->next;
		delete _first->prev;
		_first->prev = 0;
		_size--;
	} while (_viewed.contains(elem) && _first);
	_viewed << elem;
	return elem;
}

template <typename T>
void ClosedList<T>::push_back (const T &value)
{
	// wenn value schon angeschaut wurde, nichts machen
	if (_viewed.contains(value))
		return;
	
	// wenn die Liste leer ist, _last und _first einfach mit den richtigen Daten füttern
	if (!_first || !_last)
	{
		_first = new ClosedListData<T>(value);
		_last = _first;
	}
	
	// ansonsten das Element hinten an die Liste anfügen
	else
	{
		ClosedListData<T> *elem = new ClosedListData<T>(value);
		elem->prev = _last;
		_last->next = elem;
		_last = elem;
	}
	
	// size inkrementieren
	_size++;
}
