#ifndef ALGO_H
#define ALGO_H

#include "world.h"

#include <QPoint>
#include <QSet>

extern void solveWorld(World *w);

namespace algo
{
extern QPoint neighbor(World *w, const QPoint &start, int x, int y);
extern bool findPath(World *w, const QPoint &start, World::FieldType toFind, World::FieldState state = World::UnknownState, QSet<QPoint> *searched = new QSet<QPoint>);
}

// thanks to qt for not providing this
uint qHash(const QPoint &p);

#endif // ALGO_H
