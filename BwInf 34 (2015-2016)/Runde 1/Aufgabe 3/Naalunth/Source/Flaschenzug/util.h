#pragma once

#include <algorithm>

template<class T, class CompT = std::less<T> >
bool in_range(T val, T min, T max, CompT cmp = std::less<T>{});

template<class T, class Interpolator>
T lerp(T a, T b, Interpolator f);

template<class T, class Ta, class Tb>
T clamp(T x, Ta lower, Tb upper);


#include "util.inl"
