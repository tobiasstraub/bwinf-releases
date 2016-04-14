#include "world.h"

#include <QDataStream>
#include <QDebug>
#include <QFile>
#include <QMap>
#include <QPoint>
#include <QTextStream>

#ifdef QT_GUI_LIB
#  include <QPainter>
#endif

typedef std::underlying_type<World::FieldType >::type ftt;
typedef std::underlying_type<World::FieldState>::type fst;

World* World::read(const QString &filename)
{
	QFile f(filename);
	return read(&f);
}

World* World::read(QIODevice *in)
{
	if (!in || (!in->isOpen() && !in->open(QIODevice::ReadOnly)))
		return 0;
	in->seek(0);
	if (in->readLine(6).trimmed() == "YYW") // YamYam World
	{
		QDataStream d(in);
		quint32 width, height;
		d >> width >> height;
		World *w = new World(width, height);
		QMap<ftt, QList<QPoint> > fields;
		d >> fields;
		for (auto ft : fields.keys())
			for (auto fp : fields[ft])
				w->_field[fp.x()][fp.y()].type = static_cast<FieldType>(ft);
		bool containsResult;
		d >> containsResult;
		if (containsResult)
		{
			w->setHasResult(containsResult);
			QMap<fst, QList<QPoint> > result;
			d >> result;
			for (auto fs : result.keys())
				for (auto fp : result[fs])
					w->_field[fp.x()][fp.y()].state = static_cast<FieldState>(fs);
		}
		return w;
	}
	else
	{
		in->seek(0);
		QTextStream t(in);
		QByteArray line = in->readLine();
		QStringList s = QString::fromLocal8Bit(line).split(' ', QString::SkipEmptyParts);
		quint32 width = s[0].toUInt(), height = s[1].toUInt();
		World *w = new World(width, height);
//		qDebug() << w->width() << w->height();
		for (quint32 i = 0; i < height; i++)
		{
			line = in->readLine();
//			qDebug() << line;
			for (quint32 j = 0; j < width; j++)
				w->field(j, i)->type = static_cast<FieldType>((char)line[j]);
		}
		return w;
	}
}

void World::write(const QString &filename, bool writeResult)
{
	QFile f(filename);
	write(&f, writeResult);
}

void World::write(QIODevice *out, bool writeResult)
{
	if (!out || (!out->isOpen() && !out->open(QIODevice::WriteOnly)))
		return;
	out->write("YYW\n");
	QDataStream d(out);
	d << _width << _height;
	QMap<ftt, QList<QPoint> > fields;
	for (quint32 i = 0; i < _width; i++)
	{
		for (quint32 j = 0; j < _height; j++)
		{
			if (_field[i][j].type != DefaultType)
			{
				if (!fields.contains(static_cast<ftt>(_field[i][j].type)))
					fields.insert(static_cast<ftt>(_field[i][j].type), QList<QPoint>() << QPoint(i,j));
				else
					fields[static_cast<ftt>(_field[i][j].type)] << QPoint(i,j);
			}
		}
	}
	d << fields;
	bool r = hasResult() && writeResult;
	d << r;
	if (r)
	{
		QMap<fst, QList<QPoint> > result;
		for (quint32 i = 0; i < _width; i++)
		{
			for (quint32 j = 0; j < _height; j++)
			{
				if (!result.contains(static_cast<fst>(_field[i][j].state)))
					result.insert(static_cast<fst>(_field[i][j].state), QList<QPoint>() << QPoint(i,j));
				else
					result[static_cast<fst>(_field[i][j].state)] << QPoint(i,j);
			}
		}
		d << result;
	}
}

World::World(quint32 width, quint32 height)
	: _width(width)
	, _height(height)
	, _result(false)
{
	_field = new Field*[_width];
	for (quint32 i = 0; i < _width; i++)
	{
		_field[i] = new Field[_height];
		for (quint32 j = 0; j < _height; j++)
		{
			_field[i][j].type  = DefaultType;
			_field[i][j].state = DefaultState;
		}
	}
}

World::~World()
{
	for (quint32 i = 0; i < _width; i++)
		delete[] _field[i];
	delete[] _field;
}

#ifdef QT_GUI_LIB
QImage* World::draw()
{
	QImage *img = new QImage(_width * 16, _height * 16, QImage::Format_RGB32);
	QPainter p(img);
	for (quint32 i = 0; i < _width; i++)
	{
		for (quint32 j = 0; j < _height; j++)
		{
			QImage im;
			if (_field[i][j].state != World::UnknownState)
				im.load(QString(":/img/") + static_cast<char>(_field[i][j].state));
			if (_field[i][j].type  != World::Empty)
				im.load(QString(":/img/") + static_cast<char>(_field[i][j].type ));
			if (!im.isNull())
				p.drawImage(i*16, j*16, im.scaled(16,16, Qt::IgnoreAspectRatio));
			else
			{
//				qDebug() << i << j << im;
				p.fillRect(i*16, j*16, 16, 16, Qt::white);
				p.drawText(i*16 + 5, j*16 + 12, "?");
			}
		}
	}
	return img;
}

#endif
