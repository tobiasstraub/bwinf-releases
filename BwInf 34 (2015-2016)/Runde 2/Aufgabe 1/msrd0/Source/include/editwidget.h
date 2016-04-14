/*
 * editwidget.h
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
#ifndef EDITWIDGET_H
#define EDITWIDGET_H

#include "mainwindow.h"

#include <set>

#include <QList>
#include <QPainter>
#include <QPainterPath>
#include <QPaintEvent>
#include <QPoint>
#include <QWidget>

struct edit_point
{
	int x, y;
	
	bool operator== (const edit_point &other) const;
	bool operator< (const edit_point &other) const;
};

class EditWidget : public QWidget
{
	Q_OBJECT
	
public:
	explicit EditWidget(MainWindow *mw, QWidget *parent = 0);
	
	QString filename() const;
	QPainterPath path() const { return _path; }
	QPolygonF polygon() const { return _path.toFillPolygon(); }
	QList<QPointF> candles() const { return _candles; }
	void setCandles(const QList<QPointF> &candles);
	
public slots:
	bool open();
	void save();
	void saveAs();
	
signals:
	void filenameChanged(const QString &filename);
	void contentChanged();
	
protected:
	void paintEvent(QPaintEvent *event);
	void mousePressEvent(QMouseEvent *event);
	
private:
	MainWindow *_mw;
	// last used filename or an empty string
	QString _filename;
	// gui stuff
	QPainterPath _path;
	std::set<edit_point> _points;
	QList<QPointF> _candles;
	
	// backup from mouse events
	edit_point _spos;
	MainWindow::SelectedObject _sobj = MainWindow::NONE;
	
	edit_point pos2point(int x, int y) const;
	
};

#endif // EDITWIDGET_H
