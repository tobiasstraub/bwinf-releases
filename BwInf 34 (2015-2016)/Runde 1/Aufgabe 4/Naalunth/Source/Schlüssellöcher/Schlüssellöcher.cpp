#include "Schlüssellöcher.h"

#include "Key.h"

#include <algorithm>
#include <cmath>
#include <limits>

Schlüssellöcher::Schlüssellöcher::Schlüssellöcher()
{
	std::random_device rd;
	rng.seed(rd());
}


Schlüssellöcher::Schlüssellöcher::~Schlüssellöcher()
{
}

double calculateKeyDifferenceFactor(const Schlüssellöcher::Key& key, const std::vector<Schlüssellöcher::Key>& otherKeys)
{
	double differenceFactor = 1.0;
	auto flipped = key.flipped();
	for (auto& otherKey : otherKeys)
	{
		double diff = key.differenceToKey(otherKey) * flipped.differenceToKey(otherKey);
		differenceFactor += (diff == 0 ? -std::numeric_limits<double>::infinity() : std::log(diff));
	}
	return differenceFactor;
}

std::vector<Schlüssellöcher::Key> Schlüssellöcher::Schlüssellöcher::CalculateNewKeys(uint32_t size, const std::vector<Key>& otherKeys, int numberOfKeys)
{
	int sizesq = size*size;
	std::vector<Key> result(numberOfKeys, Key(size));

	//add the keys and their mirrored versions to the internal vector
	std::vector<Key> keysWithMirrors(otherKeys.size() * 2);
	keysWithMirrors.reserve(otherKeys.size() * 2 + numberOfKeys * 2);
	auto& it = keysWithMirrors.begin();
	for (auto& key : otherKeys)
	{
		*it++ = key;
		*it++ = key.flipped();
	}

	//if there aren't any keys yet, create a random one
	int startPoint = 0;
	if (otherKeys.size() == 0)
	{
		result[0] = CreateRandomKey(size);
		keysWithMirrors.push_back(result[0]);
		keysWithMirrors.push_back(result[0].flipped());
		startPoint++;
	}
	for (int id = startPoint; id < numberOfKeys; id++)
	{

		//initial guess:
		//set bits to the least frequent ones
		for (int i = 0; i < sizesq; ++i)
		{
			int setCount = std::count_if(keysWithMirrors.begin(), keysWithMirrors.end(), [i](const Key& it)->bool {return it.bits[i] == true; });
			result[id].bits[i] = setCount < (keysWithMirrors.size() / 2);
		}

		//refine the guess by flipping bits and looking if the difference has increased
		double baseMeanDifference;
		bool anythingChanged = true;
		while (anythingChanged)
		{
			anythingChanged = false;
			baseMeanDifference = calculateKeyDifferenceFactor(result[id], keysWithMirrors);
			for (int i = 0; i < sizesq; ++i)
			{
				result[id].bits[i].flip();
				double newMeanDifference = calculateKeyDifferenceFactor(result[id], keysWithMirrors);
				if (newMeanDifference > baseMeanDifference)
				{
					baseMeanDifference = newMeanDifference;
					anythingChanged = true;
				}
				else
					result[id].bits[i].flip();
			}
			//randomize the key if it is still the same as some other one
			if (baseMeanDifference == -std::numeric_limits<double>::infinity())
			{
				result[id] = CreateRandomKey(size);
				anythingChanged = true;
			}
		}
		keysWithMirrors.push_back(result[id]);
		keysWithMirrors.push_back(result[id].flipped());
	}
	return result;
}

Schlüssellöcher::Key Schlüssellöcher::Schlüssellöcher::CreateRandomKey(uint32_t size)
{
	int sizesq = size*size;
	Key result(size);
	for (int_fast32_t i = 0; i / 32 <= sizesq; i++)
	{
		uint_fast32_t n = rng();
		for (int_fast32_t j = 0; j < 32 && i * 32 + j < sizesq; j++)
		{
			result.bits[i * 32 + j] = (n & (1 << j)) >> j;
		}
	}
	return result;
}
