/*
 * mainwindow.cpp
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
#include "mainwindow.h"
#include "ui_mainwindow.h"

#include <QDebug>

MainWindow::MainWindow(QWidget *parent) :
	QMainWindow(parent),
	ui(new Ui::MainWindow)
{
	ui->setupUi(this);
}

MainWindow::~MainWindow()
{
	delete ui;
}

MainWindow::SelectedObject MainWindow::sobj() const
{
	if (ui->a_line->isChecked())
		return LINE;
	if (ui->a_curve->isChecked())
		return CURVE;
	if (ui->a_circle->isChecked())
		return CIRCLE;
	if (ui->a_candle->isChecked())
		return CANDLE;
	return NONE;
}

void MainWindow::on_a_quit_triggered()
{
    QApplication::exit();
}

void MainWindow::on_a_new_triggered()
{
	EditWidget *ew = new EditWidget(this);
	Q_CHECK_PTR(ew);
    ui->tabWidget->addTab(ew, "unbenannt");
	ui->a_close->setEnabled(true);
	ui->widget->setEnabled(true);
	connect(ew, SIGNAL(filenameChanged(QString)), this, SLOT(filename_changed(QString)));
	connect(ew, SIGNAL(contentChanged()), this, SLOT(content_changed()));
}

void MainWindow::on_tabWidget_tabCloseRequested(int index)
{
	QWidget *w = ui->tabWidget->widget(index);
    ui->tabWidget->removeTab(index);
	delete w;
	
	if (ui->tabWidget->count() == 0)
	{
		ui->a_close->setEnabled(false);
		ui->widget->setEnabled(false);
	}
}

void MainWindow::on_a_close_triggered()
{
    on_tabWidget_tabCloseRequested(ui->tabWidget->currentIndex());
}

void MainWindow::on_a_line_toggled(bool checked)
{
	if (!checked)
		return;
    ui->a_curve->setChecked(false);
	ui->a_circle->setChecked(false);
	ui->a_candle->setChecked(false);
}

void MainWindow::on_a_curve_toggled(bool checked)
{
	if (!checked)
		return;
    ui->a_line->setChecked(false);
	ui->a_circle->setChecked(false);
	ui->a_candle->setChecked(false);
}

void MainWindow::on_a_circle_toggled(bool checked)
{
	if (!checked)
		return;
	ui->a_line->setChecked(false);
    ui->a_curve->setChecked(false);
	ui->a_candle->setChecked(false);
}

void MainWindow::on_a_candle_toggled(bool checked)
{
	if (!checked)
		return;
	ui->a_line->setChecked(false);
    ui->a_curve->setChecked(false);
	ui->a_circle->setChecked(false);
}

void MainWindow::on_a_open_triggered()
{
    EditWidget *ew = new EditWidget(this);
	Q_CHECK_PTR(ew);
	if (ew->open())
	{
		ui->tabWidget->addTab(ew, ew->filename());
		ui->a_close->setEnabled(true);
		ui->widget->setEnabled(true);
		connect(ew, SIGNAL(filenameChanged(QString)), this, SLOT(filename_changed(QString)));
		connect(ew, SIGNAL(contentChanged()), this, SLOT(content_changed()));
		ui->rating->setText(QString::number(rate(ew->path(), ew->candles())));
	}
	else
		delete ew;
}

void MainWindow::on_a_save_triggered()
{
    EditWidget *ew = (EditWidget*) ui->tabWidget->currentWidget();
	if (ew)
		ew->save();
}

void MainWindow::on_a_save_as_triggered()
{
    EditWidget *ew = (EditWidget*) ui->tabWidget->currentWidget();
	if (ew)
		ew->saveAs();
}

void MainWindow::filename_changed(const QString &filename)
{
	ui->tabWidget->setTabText(ui->tabWidget->indexOf((QWidget*) sender()), filename);
}

void MainWindow::content_changed()
{
	EditWidget *ew = (EditWidget*) sender();
	ui->rating->setText(QString::number(rate(ew->path(), ew->candles())));
}

void MainWindow::on_exec_clicked()
{
    EditWidget *ew = (EditWidget*) ui->tabWidget->currentWidget();
	if (ew)
		ew->setCandles(spread(ew->path(), ui->numCandles->value()));
}
