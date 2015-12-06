#pragma once

template <class data_type>
struct Vector2
{
	data_type x;
	data_type y;

	template <typename result_type = data_type, class other_type>
	Vector2<result_type> operator+(const Vector2<other_type>& other) const
	{
		return Vector2<result_type>{ x + other.x, y + other.y };
	}

	template <typename result_type = data_type, class other_type>
	Vector2<result_type> operator-(const Vector2<other_type>& other) const
	{
		return Vector2<result_type>{ x - other.x, y - other.y };
	}

	template <class other_type>
	bool operator==(const Vector2<other_type>& other) const
	{
		return x == other.x && y == other.y;
	}

	template <class other_type>
	bool operator!=(const Vector2<other_type>& other) const
	{
		return x != other.x || y != other.y;
	}

	Vector2<data_type> operator-() const
	{
		return Vector2<data_type>{ -x, -y };
	}

};

template <typename T>
bool in_area(Vector2<T> val, Vector2<T> min, Vector2<T> max)
{
	return !(val.x < min.x) && !(max.x < val.x) && !(val.y < min.y) && !(max.y < val.y);
}

typedef Vector2<int> ivec2;
