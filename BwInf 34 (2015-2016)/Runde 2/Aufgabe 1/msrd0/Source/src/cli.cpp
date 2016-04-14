#include "algo.h"

#include <QCoreApplication>
#include <QCommandLineOption>
#include <QCommandLineParser>
#include <QFile>
#include <QPainterPath>
#include <QRegularExpression>
#include <QTextStream>

#if defined __unix || defined __APPLE__
#define HAS_TTY
#include <unistd.h>
#endif

#ifdef BUILD_USING_QMAKE
int cli(int argc, char **argv)
#else
int main(int argc, char **argv)
#endif
{
	QCoreApplication app(argc, argv);
	QCoreApplication::setApplicationName("Geburtstagskuchen");
	
	QCommandLineParser parser;
	parser.addHelpOption();
	QCommandLineOption spreadOption("spread", "Spread the given number of candles", "candles");
	parser.addOption(spreadOption);
	QCommandLineOption omitCandlesOption("omit", "Don't print out the spreaded candle positions");
	parser.addOption(omitCandlesOption);
	QCommandLineOption rateOption("rate", "Rate the candles");
	parser.addOption(rateOption);
	QCommandLineOption outputOption("o", "The file to output the cake", "file");
	parser.addOption(outputOption);
	QCommandLineOption inputOption("i", "The file to read the cake from (binary format)", "file");
	parser.addOption(inputOption);
	QCommandLineOption dumpOption("dump", "Will dump the cake as text format (after spread if given)");
	parser.addOption(dumpOption);
	parser.process(app);
	
#ifdef HAS_TTY
	bool tty = isatty(STDOUT_FILENO);
	bool interactive = isatty(STDIN_FILENO);
#else
	bool tty = false, interactive = true;
#endif
	
	QPainterPath path;
	QList<QPointF> candles;
	if (parser.isSet(inputOption))
	{
		QFile file(parser.value(inputOption));
		file.open(QIODevice::ReadOnly);
		if (file.readLine() != "CAKE\n")
			return 1;
		QDataStream stream(&file);
		stream.setVersion(QDataStream::Qt_5_5);
		stream.setByteOrder(QDataStream::BigEndian);
		stream >> path;
		stream >> candles;
		file.close();
	}
	else
	{
		QTextStream in(stdin);
		QString line;
		while (true)
		{
			if (tty && interactive)
				printf("\033[1;32mcake>\033[0m ");
			else if (interactive)
				printf("cake> ");
			line = in.readLine();
			if (line.isEmpty())
				break;
			line = line.trimmed();
			if (line.isEmpty() || line.startsWith('#'))
				continue;
			if (line == "q")
				break;
			QStringList s = line.split(QRegularExpression("\\s+"));
			bool error = false;
			if (s[0] == "move")
			{
				if (s.size() == 3)
					path.moveTo(s[1].toDouble(), s[2].toDouble());
				else 
					error = true;
			}
			else if (s[0] == "line")
			{
				if (s.size() == 3)
					path.lineTo(s[1].toDouble(), s[2].toDouble());
				else
					error = true;
			}
			else if (s[0] == "quad")
			{
				if (s.size() == 5)
					path.quadTo(s[1].toDouble(), s[2].toDouble(), s[3].toDouble(), s[4].toDouble());
				else
					error = true;
			}
			else if (s[0] == "cubic")
			{
				if (s.size() == 7)
					path.cubicTo(s[1].toDouble(), s[2].toDouble(), s[3].toDouble(), s[4].toDouble(), s[5].toDouble(), s[6].toDouble());
				else
					error = true;
			}
			else if (s[0] == "candle")
			{
				if (s.size() == 3)
					candles << QPoint(s[1].toDouble(), s[2].toDouble());
				else
					error = true;
			}
			else
				error = true;
			
			if (error)
			{
				printf("Commands:\n");
				printf("  move x y               Move to the given position\n");
				printf("  line x y               Draw a line to the given position\n");
				printf("  quad cx cy x y         Draw a quadratic bezier curve to the given\n");
				printf("                         position using the control point cx,cy\n");
				printf("  cubic cx cy dx dy x y  Draw a cubic bezier curve to the given position\n");
				printf("                         using the control points cx,cy and dx,dy\n");
				printf("  candle x y             Put a candle at the given position\n");
				printf("  q                      Finish the cake\n");
			}
		}
	}
	
	
	if (parser.isSet(rateOption))
	{
		double r = rate(path, candles);
		if (tty)
			printf("\033[1;33mRATING:\033[0m %f\n", r);
		else
			printf("RATING: %f\n", r);
	}
	
	if (parser.isSet(spreadOption))
	{
		QList<QPointF> s = spread(path, parser.value(spreadOption).toInt());
		if (!parser.isSet(omitCandlesOption))
		{
			if (tty)
				printf("\033[1;33mSPREAD CANDLES:\033[0m\n");
			else
				printf("SPREAD CANDLES:\n");
			for (auto p : s)
				printf(" - %f %f\n", p.x(), p.y());
		}
		double r = rate(path, s);
		if (tty)
			printf("\033[1;33mSPREAD RATING:\033[0m %f\n", r);
		else
			printf("SPREAD RATING: %f\n", r);
	}
	
	if (parser.isSet(outputOption))
	{
		QFile file(parser.value(outputOption));
		file.open(QIODevice::WriteOnly);
		file.write("CAKE\n");
		QDataStream stream(&file);
		stream.setVersion(QDataStream::Qt_5_5);
		stream.setByteOrder(QDataStream::BigEndian);
		stream << path;
		stream << candles;
		file.close();
	}
	
	if (parser.isSet(dumpOption))
	{
		if (tty)
			printf("\033[1;33mDUMP:\033[0m\n");
		else
			printf("#DUMP:\n");
#define dump_elem(name, ...) { \
	if (tty) printf("  \033[1m" name "\033[0m " __VA_ARGS__ ); \
	else printf(name " " __VA_ARGS__ ); }
		QList<QPointF> curveData;
#define dump_curve { \
	if (curveData.size() == 2) dump_elem("quad", "%f %f %f %f\n", curveData[0].x(), curveData[0].y(), curveData[1].x(), curveData[1].y()) \
	else if (curveData.size() == 3) dump_elem("cubic", "%f %f %f %f %f %f\n", curveData[0].x(), curveData[0].y(), curveData[1].x(), curveData[1].y(), curveData[2].x(), curveData[2].y()) \
	curveData.clear(); \
	}
		for (int i = 0; i < path.elementCount(); i++)
		{
			auto e = path.elementAt(i);
			switch (e.type)
			{
			case QPainterPath::MoveToElement:
				dump_curve
						dump_elem("move", "%f %f\n", e.x, e.y)
						break;
			case QPainterPath::LineToElement:
				dump_curve
						dump_elem("line", "%f %f\n", e.x, e.y)
						break;
			case QPainterPath::CurveToElement:
				dump_curve
					case QPainterPath::CurveToDataElement:
					curveData << (QPointF)e;
				break;
			}
		}
		dump_curve
				dump_elem("q", "\n");
	}
	
	return 0;
}
