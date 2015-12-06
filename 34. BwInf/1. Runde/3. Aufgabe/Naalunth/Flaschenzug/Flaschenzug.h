#pragma once

#include "bigint/BigIntegerLibrary.hh"

#include <vector>
#include <map>
#include <cstdint>

namespace Flaschenzug
{
	class Flaschenzug
	{
	public:
		Flaschenzug(const std::vector<uint32_t>& containers);
		~Flaschenzug();
		BigUnsigned GetNumberOfPermutations(uint32_t items);
	private:
		std::vector<uint_fast32_t> containers;
		BigUnsigned GetNumberOfPermutationsInternalIterative(uint_fast32_t items);
		std::map<std::pair<uint_fast32_t, uint_fast32_t>, BigUnsigned> memoMap;
	};
}
