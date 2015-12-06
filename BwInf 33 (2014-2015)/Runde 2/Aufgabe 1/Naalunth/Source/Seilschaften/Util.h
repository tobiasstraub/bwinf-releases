#pragma once

#define SAFE_DELETE(x) if(x){delete (x);(x)=0;};


template<class ContainerType, class ValueType>
bool contains(const ContainerType& c, const ValueType& v)
{
	return std::find(c.begin(), c.end(), v) != c.end();
}
