#pragma once

#include <vector>

namespace Schlüssellöcher
{
	class Key
	{
	public:
		Key();
		Key(size_t edgeLength);
		~Key();
		size_t size() const;
		Key flipped() const;
		double differenceToKey(const Key& other) const;
		double meanDifferenceWithFlipped(const Key& other) const;
		std::vector<bool> bits;
	private:
		size_t _size;
	};
}
