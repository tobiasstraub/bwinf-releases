#pragma once

#include <algorithm>

struct ivec2
{
	int x;
	int y;

	ivec2 operator+(const ivec2& other) const;
	ivec2 operator-(const ivec2& other) const;
	bool operator==(const ivec2& other) const;
};

template<class T, class CompT = std::less<T> >
bool in_range(T val, T min, T max, CompT cmp = std::less<T>{})
{
	return !(cmp(val, min) || cmp(max, val));
}

bool in_range(ivec2 val, ivec2 min, ivec2 max);

enum Direction
{
	NORTH = 0,
	EAST = 1,
	SOUTH = 2,
	WEST = 3,
	NONE = 4
};

const Direction Directions[] = { NORTH, EAST, SOUTH, WEST };
const ivec2 DirectionVectorsWithNone[] = { { 0,-1 },{ 1,0 },{ 0,1 },{ -1,0 },{ 0,0 } };
const ivec2 DirectionVectors[] = { { 0,-1 },{ 1,0 },{ 0,1 },{ -1,0 } };
const char DirectionLetters[] = { 'N', 'E', 'S', 'W' };
