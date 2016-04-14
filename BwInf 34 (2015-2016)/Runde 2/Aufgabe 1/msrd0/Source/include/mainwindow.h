/*
 * mainwindow.h
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
#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
	Q_OBJECT
	
public:
	enum SelectedObject : char
	{
		NONE   = ' ',
		LINE   = '-',
		CURVE  = 'c',
		CIRCLE = 'o',
		CANDLE = 'i'
	};
	Q_ENUM(SelectedObject);
	
	explicit MainWindow(QWidget *parent = 0);
	~MainWindow();
	
	SelectedObject sobj() const;
	
private slots:
	void on_a_quit_triggered();
	void on_a_new_triggered();
	void on_tabWidget_tabCloseRequested(int index);
	void on_a_close_triggered();
	void on_a_line_toggled(bool arg1);
	void on_a_curve_toggled(bool arg1);
	void on_a_circle_toggled(bool arg1);	
	void on_a_candle_toggled(bool arg1);
	void on_a_open_triggered();
	void on_a_save_triggered();
	void on_a_save_as_triggered();
	
	void filename_changed(const QString &filename);
	void content_changed();
	
	void on_exec_clicked();
	
private:
	Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
