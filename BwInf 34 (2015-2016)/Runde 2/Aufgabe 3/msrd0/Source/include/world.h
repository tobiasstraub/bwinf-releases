#ifndef WORLD_H
#define WORLD_H

#include <QIODevice>
#include <QString>

#ifdef QT_GUI_LIB
#  include <QImage>
#endif

class World : public QObject
{
	Q_OBJECT
	Q_DISABLE_COPY(World)
	
public:
	/// Die Typen, die ein Feld haben kann.
	enum FieldType : signed char
	{
		Wall  = '#',
		Exit  = 'E',
		Empty = ' ',
		// default / initial
		DefaultType = Empty
	};
	Q_ENUM(FieldType);
	
	/// Den Status, den ein Feld haben kann.
	enum FieldState : signed char
	{
		UnknownState = '?',
		Save         = '+',
		Unsave       = '%',
		Failing      = '-',
		// default / initial
		DefaultState = UnknownState
	};
	Q_ENUM(FieldState);
	
	/// Ein Feld.
	struct Field
	{
		FieldType  type;
		FieldState state;
	};
	
	/// Erstellt eine neue Welt mit der angegebenen Größe.
	World(quint32 width, quint32 height);
	
	static World* read(const QString &filename);
	static World* read(QIODevice *in);
	
	void write(const QString &filename, bool writeResult = true);
	void write(QIODevice *out, bool writeResult = true);
	
	~World();
	
	quint32 width() const { return _width; }
	quint32 height() const { return _height; }
	
	Field* field(const QPoint &pos) { return &_field[pos.x()][pos.y()]; }
	Field* field(int x, int y) { return &_field[x][y]; }
	
	void setHasResult(bool result) { _result = result; }
	bool hasResult() const { return _result; }
	
#ifdef QT_GUI_LIB
	QImage* draw();
#endif
	
private:
	/// Die Breite der Welt.
	quint32 _width;
	/// Die Höhe der Welt.
	quint32 _height;
	/// Das Feld der Welt.
	Field **_field;
	/// Gibt an, ob die Welt beurteilt wurde.
	bool _result;
	
};

#endif // WORLD_H
