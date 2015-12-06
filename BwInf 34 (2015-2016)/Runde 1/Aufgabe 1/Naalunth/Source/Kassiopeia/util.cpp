#include "util.h"

ivec2 ivec2::operator+(const ivec2 & other) const
{
	return ivec2{ x + other.x, y + other.y };
}

ivec2 ivec2::operator-(const ivec2 & other) const
{
	return ivec2{ x - other.x, y - other.y };
}

bool ivec2::operator==(const ivec2 & other) const
{
	return x == other.x && y == other.y;
}

bool in_range(ivec2 val, ivec2 min, ivec2 max)
{
	return !(val.x < min.x) && !(max.x < val.x) && !(val.y < min.y) && !(max.y < val.y);
}
