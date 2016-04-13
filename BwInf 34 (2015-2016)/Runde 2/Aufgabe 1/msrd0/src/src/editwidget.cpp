/*
 * editwidget.cpp
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
#include "editwidget.h"

#include <algorithm>
using namespace std;

#include <QDataStream>
#include <QDebug>
#include <QFile>
#include <QFileDialog>
#include <QFileInfo>
#include <QMessageBox>
#include <QPointer>

#define file_filter "Geburtstagskuchen (*.cake);;All files (*.*)"
#define edit_point_diff 10

EditWidget::EditWidget(MainWindow *mw, QWidget *parent)
	: QWidget(parent)
	, _mw(mw)
{
	_path.setFillRule(Qt::WindingFill);
}

const QColor c_bg(0x708AA0);
const QColor c_path(0x20B830);
const QColor c_point(0xFF0000);
const QColor c_candle(0xFFFF00);

void EditWidget::paintEvent(QPaintEvent*)
{
	QPainter p(this);
	p.fillRect(geometry(), c_bg);
	
	p.setPen(QPen(c_path, 2));
	p.drawPath(_path);
	p.setBrush(c_path);
	_path.setFillRule(Qt::WindingFill);
	p.drawPolygon(_path.simplified().toFillPolygon());
	
	p.setPen(c_point);
	p.setBrush(c_point);
	for (auto a : _points)
		p.drawEllipse(QPoint(a.x, a.y), 3, 3);
	
	p.setPen(c_candle);
	p.setBrush(c_candle);
	for (auto a : _candles)
		p.drawEllipse(a, 5, 5);
}

void EditWidget::mousePressEvent(QMouseEvent *event)
{
	if (event->button() != Qt::LeftButton)
		return;
	if (_sobj == MainWindow::NONE)
	{
		if (_mw->sobj() == MainWindow::CANDLE)
		{
			_candles.append(QPoint(event->x(), event->y()));
			repaint();
			emit contentChanged();
			return;
		}
		_spos = pos2point(event->x(), event->y());
		_sobj = _mw->sobj();
		if (_sobj != MainWindow::NONE)
		{
			_points.insert(_spos);
			repaint();
		}
		return;
	}
	edit_point pos = pos2point(event->x(), event->y());
	switch (_sobj)
	{
	case MainWindow::LINE:
		if (_path.currentPosition() != QPointF(_spos.x, _spos.y))
			_path.moveTo(_spos.x, _spos.y);
		_path.lineTo(pos.x, pos.y);
		break;
	case MainWindow::CIRCLE: {
			int xd = _spos.x - pos.x;
			int yd = _spos.y - pos.y;
			double r = sqrt(xd*xd + yd*yd); // satz des pythagoras
			_path.moveTo(_spos.x, _spos.y-r);
			// https://en.wikipedia.org/wiki/Composite_B%C3%A9zier_curve#Using_four_curves
			double k = r * 4.0 * (sqrt(2.0) - 1.0) / 3.0;
			_path.cubicTo(_spos.x+k,_spos.y-r, _spos.x+r,_spos.y-k, _spos.x+r,_spos.y);
			_path.cubicTo(_spos.x+r,_spos.y+k, _spos.x+k,_spos.y+r, _spos.x,_spos.y+r);
			_path.cubicTo(_spos.x-k,_spos.y+r, _spos.x-r,_spos.y+k, _spos.x-r,_spos.y);
			_path.cubicTo(_spos.x-r,_spos.y-k, _spos.x-k,_spos.y-r, _spos.x,_spos.y-r);
		}
	}
	_points.insert(pos);
	_sobj = MainWindow::NONE;
	repaint();
	emit contentChanged();
}

edit_point EditWidget::pos2point(int x, int y) const
{
	edit_point p{x,y};
	auto it = _points.find(p);
	if (it != _points.end())
		return *it;
	return p;
}

QString EditWidget::filename() const
{
	QFileInfo info(_filename);
	return info.fileName();
}

bool EditWidget::open()
{
	_filename = QFileDialog::getOpenFileName(parentWidget(), "Öffnen", QDir::homePath(), file_filter);
	if (_filename.isEmpty())
		return false;
	QFile file(_filename);
	if (!file.open(QIODevice::ReadOnly))
	{
		QMessageBox::critical(parentWidget(), "Fehler beim Öffnen", "Fehler beim Öffnen der Datei \"" + _filename + "\": " + file.errorString(), QMessageBox::Ok);
		return false;
	}
	if (file.readLine().trimmed() != "CAKE")
	{
		QMessageBox::critical(parentWidget(), "Fehler beim Öffnen", "Fehler beim Öffnen der Datei \"" + _filename + "\": Dateiformat wird nicht unterstützt", QMessageBox::Ok);
		file.close();
		return false;
	}
	QDataStream stream(&file);
	stream.setVersion(QDataStream::Qt_5_5);
	stream.setByteOrder(QDataStream::BigEndian);
	stream >> _path;
	stream >> _candles;
	file.close();
	for (int i = 0; i < _path.elementCount(); i++)
		_points.insert({(int)_path.elementAt(i).x, (int)_path.elementAt(i).y});
	return true;
}

void EditWidget::save()
{
	if (_filename.isEmpty())
	{
		saveAs();
		return;
	}
	QFile file(_filename);
	if (!file.open(QIODevice::WriteOnly))
	{
		QMessageBox::critical(parentWidget(), "Fehler beim Speichern", "Fehler beim Öffnen der Datei \"" + _filename + "\": " + file.errorString(), QMessageBox::Ok);
		return;
	}
	file.write("CAKE\n");
	QDataStream stream(&file);
	stream.setVersion(QDataStream::Qt_5_5);
	stream.setByteOrder(QDataStream::BigEndian);
	stream << _path;
	stream << _candles;
	file.close();
}

void EditWidget::saveAs()
{
	_filename = QFileDialog::getSaveFileName(parentWidget(), "Speichern", QDir::homePath(), file_filter);
	if (_filename.isEmpty())
		return;
	_filename = QDir::home().absoluteFilePath(_filename);
	emit filenameChanged(filename());
	save();
}

void EditWidget::setCandles(const QList<QPointF> &candles)
{
	_candles = candles;
	repaint();
	emit contentChanged();
}



bool edit_point::operator== (const edit_point &other) const
{
	return (abs(other.x - x) <= edit_point_diff && abs(other.y - y) <= edit_point_diff);
}

bool edit_point::operator< (const edit_point &other) const
{
	return (!(*this==other) && (x<other.x || (x==other.x && y<other.y)));
}
