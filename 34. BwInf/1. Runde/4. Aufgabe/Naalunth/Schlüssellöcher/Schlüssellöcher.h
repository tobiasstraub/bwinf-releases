#pragma once

#include <bitset>
#include <vector>
#include <random>

namespace Schlüssellöcher
{
	class Key;
	class Schlüssellöcher
	{
	public:
		Schlüssellöcher();
		~Schlüssellöcher();

		//all the keys should be of same size
		std::vector<Key> CalculateNewKeys(uint32_t size, const std::vector<Key>& otherKeys, int numberOfKeys);
		Key CreateRandomKey(uint32_t size);
	private:
		std::mt19937 rng;
	};


}
