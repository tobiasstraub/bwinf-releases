template<class T, class CompT>
inline bool in_range(T val, T min, T max, CompT cmp)
{
	return !(cmp(val, min) || cmp(max, val));
}

template<class T, class Interpolator>
inline T lerp(T a, T b, Interpolator f)
{
	return (T) f*b + (a - f*a);
}

template<class T, class Ta, class Tb>
inline T clamp(T x, Ta lower, Tb upper)
{
	return (T) std::max(lower, std::min(x, upper));
}