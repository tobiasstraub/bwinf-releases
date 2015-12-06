#pragma once

#include "vectors.h"
#include <algorithm>


enum Direction
{
	NORTH = 0,
	EAST = 1,
	SOUTH = 2,
	WEST = 3,
	NONE = 4
};

const Direction Directions[] = { NORTH, EAST, SOUTH, WEST };
const Direction DirectionsWithNone[] = { NORTH, EAST, SOUTH, WEST, NONE };
const ivec2 DirectionVectorsWithNone[] = { { 0,-1 },{ 1,0 },{ 0,1 },{ -1,0 },{ 0,0 } };
const ivec2 DirectionVectors[] = { { 0,-1 },{ 1,0 },{ 0,1 },{ -1,0 } };
const char DirectionLetters[] = { 'N', 'E', 'S', 'W' };


template<class T, class CompT = std::less<T> >
bool in_range(T val, T min, T max, CompT cmp = std::less<T>{});

template<class T, class Interpolator>
T lerp(T a, T b, Interpolator f);

template<class T, class Ta, class Tb>
T clamp(T x, Ta a, Tb b);


#include "util.inl"
