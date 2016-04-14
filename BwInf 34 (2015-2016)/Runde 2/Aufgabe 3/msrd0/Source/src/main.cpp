#include "algo.h"

#include <QCoreApplication>
#include <QCommandLineOption>
#include <QCommandLineParser>
#include <QDebug>
#include <QDir>
#include <QFileInfo>
#include <QTime>

#ifdef QT_GUI_LIB
#  include <QGuiApplication>
#endif

#ifdef Q_OS_WIN
#  error Windoof wird nicht unterstützt.
#endif

#include <stdio.h>
#include <unistd.h>

inline int colorCode (World::FieldType type, World::FieldState state)
{
	switch (type)
	{
	case World::Wall:
		return 44;
	case World::Exit:
		return 46;
	default:
		switch (state)
		{
		case World::Save:
			return 42;
		case World::Unsave:
			return 43;
		case World::Failing:
			return 41;
		default:
			return 40;
		}
	}
}

int main(int argc, char *argv[])
{
#ifdef QT_GUI_LIB
	QGuiApplication app(argc, argv);
#else
	QCoreApplication app(argc, argv);
#endif
	QCoreApplication::setApplicationName("Torkelnde Yamyams");
	QCoreApplication::setOrganizationName("Dominic Meiser");
	
	QCommandLineParser parser;
	parser.setApplicationDescription("34. BwInf Aufgabe 3 - Torkelnde Yamyams");
	parser.addHelpOption();
	QCommandLineOption fileOption(QStringList() << "f" << "file", "A file containing the description of a world.", "file");
	parser.addOption(fileOption);
	QCommandLineOption exampleOption(QStringList() << "e" << "example", "Use the given example from the BwInf.", "example");
	parser.addOption(exampleOption);
	parser.process(app);
	
	QString filename;
	if (parser.isSet(exampleOption))
		filename = ":/examples/" + parser.value(exampleOption);
	else if (parser.isSet(fileOption))
		filename = parser.value(fileOption);
	else
	{
		qCritical() << "No action specified. Use --help to display help.";
		return 1;
	}
	
	World *w = World::read(filename);
	if (!w)
	{
		qCritical() << "Failed to read world";
		return 1;
	}
	if (parser.isSet(fileOption))
	{
		QFileInfo info(filename);
		w->write(info.absoluteDir().absoluteFilePath(info.baseName() + ".orig.yyw"), false);
	}
	for (quint32 i = 0; i < w->height(); i++)
	{
		for (quint32 j = 0; j < w->width(); j++)
		{
			if (isatty(STDOUT_FILENO))
				printf("\033[%dm  \033[0m", colorCode(w->field(j, i)->type, World::UnknownState));
			else
				printf("%c ", static_cast<char>(w->field(j, i)->type));
		}
		printf("\n");
	}
	if (w->hasResult())
		qDebug() << "The loaded world already contains a solution";
	else
	{
		QTime t = QTime::currentTime();
		solveWorld(w);
		qDebug() << "solved the world in" << t.elapsed() << "ms";
	}
	for (quint32 i = 0; i < w->height(); i++)
	{
		for (quint32 j = 0; j < w->width(); j++)
		{
			if (isatty(STDOUT_FILENO))
				printf("\033[%dm  \033[0m", colorCode(w->field(j, i)->type, w->field(j, i)->state));
			else
				printf("%c ", static_cast<char>(w->field(j, i)->state == World::UnknownState ? w->field(j, i)->type : w->field(j, i)->state));
		}
		printf("\n");
	}
	if (parser.isSet(fileOption))
	{
		QFileInfo info(filename);
		w->write(info.absoluteDir().absoluteFilePath(info.baseName() + ".solved.yyw"), true);
#ifdef QT_GUI_LIB
		QImage *img = w->draw();
		if (img)
		{
			img->save(info.absoluteDir().absoluteFilePath(info.baseName()) + ".png", "PNG");
			delete img;
		}
#endif
	}
	
	return 0;
}
