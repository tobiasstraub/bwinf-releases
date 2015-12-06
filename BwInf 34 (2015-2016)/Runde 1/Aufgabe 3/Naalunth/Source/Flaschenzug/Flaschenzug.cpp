#include "Flaschenzug.h"
#include "util.h"

#include "bigint/BigIntegerLibrary.hh"

#include <array>
#include <numeric>
#include <algorithm>
#include <cstdint>

Flaschenzug::Flaschenzug::Flaschenzug(const std::vector<uint32_t>& containers)
	:containers{ containers }
{
	std::sort(this->containers.begin(), this->containers.end());
}

Flaschenzug::Flaschenzug::~Flaschenzug()
{
}

BigUnsigned Flaschenzug::Flaschenzug::GetNumberOfPermutations(uint32_t items)
{
	return GetNumberOfPermutationsInternalIterative(items);
}


/*
Old Implementations:

First one: obvious recursive solution
- horrendous runtime
- Computational time: at least O(N^k) (I don't really bother analyzing too much, most examples never finished)
- Memory: O(k) (function stack)
- easy to check validity

BigUnsigned Flaschenzug::Flaschenzug::GetNumberOfPermutationsInternalNaiveRecursive(uint_fast32_t items, uint_fast32_t containerIndex)
{
	int combinedRestContainerCapacity = std::accumulate(containers.begin() + containerIndex, containers.end(), 0ul);
	if (combinedRestContainerCapacity < items) return 0;
	else if (combinedRestContainerCapacity == items) return 1;
	else switch (containers.size() - containerIndex)
	{
	case 1: //last container
		return 1; break;
	default: //any other number of containers
		BigUnsigned result = 0;
		for (int i = 0; i <= containers[containerIndex] && i <= items; i++)
			result += GetNumberOfPermutationsInternalNaiveRecursive(items - i, containerIndex + 1);
		return result;
	}
}

//----------------------------------------------------------------------------------------------------------------------

Second one: memoized recursive
- way better runtime
- Computational time: O(k*N^2) ( + some huge factor for all the map lookups)
- Memory: O(k*N) ( * some huge factor because of std::map)
- as easy to check validity of solutions (in fact my reference to check the other algorithms)

BigUnsigned Flaschenzug::Flaschenzug::GetNumberOfPermutationsInternalRecursive(uint_fast32_t items, uint_fast32_t containerIndex)
{
	auto key = std::make_pair(items, containerIndex);
	auto& memoEntry = memoMap.find(key);
	if (memoEntry == memoMap.end())
	{ //if we haven't calculated these values yet
		BigUnsigned result = 0;
		switch (containers.size() - containerIndex)
		{
		case 1: //last container
			result = 1; break;
		case 2: //two containers left
		{
			uint_fast32_t a = containers[containerIndex], b = containers[containerIndex + 1];
			uint_fast32_t options[] = { a, b, items, a + b - items };
			result = BigUnsigned(*std::min_element(options, options + 4) + 1U);
		} break;
		default: //any other number of containers
			int combinedRestContainerCapacity = std::accumulate(containers.begin() + containerIndex + 1, containers.end(), 0ul);
			for (int i = std::max(0, (int) items - combinedRestContainerCapacity); i <= containers[containerIndex] && i <= items; i++)
				result += GetNumberOfPermutationsInternalRecursive(items - i, containerIndex + 1);
		}
		memoMap[key] = result;
		return result;
	}
	return memoEntry->second;
}

//----------------------------------------------------------------------------------------------------------------------

Third one: flattened out iterative approach
- even faster, because of less function call and memory overhead
- Computational time: O(k*N^2) (loop -> loop -> std::accumulate)
- Memory: O(k*N) (flat vector)
- code gets a bit arbitrary
- data independent stuff can be calculated in parallel

//Qpar doesn't like calling the std in this case
#define MYMIN(A,B) (((A)<(B))?(A):(B))

BigUnsigned Flaschenzug::Flaschenzug::GetNumberOfPermutationsInternalOldIterative(uint_fast32_t items)
{
	uint_fast32_t numContainers = containers.size();
	std::vector<uint_fast32_t> combinedRestContainerCapacities(numContainers);
	std::partial_sum(containers.rbegin(), containers.rend(), combinedRestContainerCapacities.begin());
	std::array<std::vector<BigUnsigned>, 2> resultsVectors{};
	resultsVectors.fill(std::vector<BigUnsigned>(items + 1, 1U));
	std::vector<BigUnsigned> *lastColumn = &resultsVectors[0], *currentColumn = &resultsVectors[1];

	//Last container gets implicitly calculated in the construction of the vectors
	if (numContainers <= 1) return 1U;

	//Calculate the last two containers
	uint_fast32_t a = containers[numContainers - 1], b = containers[numContainers - 2];
	uint_fast32_t smallerContainer = std::min(a, b);
	uint_fast32_t containerSum = a + b;
#pragma loop(hint_parallel(0))
#pragma loop(ivdep)
	for (int currentItems = 0; currentItems <= (int) items; ++currentItems)
		(*currentColumn)[currentItems] = std::min(smallerContainer, std::min((uint_fast32_t) currentItems, containerSum - currentItems)) + 1U;
	std::swap(currentColumn, lastColumn);


	for (int numContainersLeft = 3; numContainersLeft <= numContainers; ++numContainersLeft)
	{
		int containerIndex = numContainers - numContainersLeft;
		int combinedRestContainerCapacity = combinedRestContainerCapacities[numContainersLeft - 2];
#pragma loop(hint_parallel(0))
#pragma loop(ivdep)
		for (int currentItems = 0; currentItems <= (int) items; ++currentItems)
		{
			int lowerBound = currentItems - MYMIN((int) containers[containerIndex], currentItems);
			int upperBound = std::max(lowerBound, currentItems - std::max(0, (int) currentItems - combinedRestContainerCapacity) + 1);
			(*currentColumn)[currentItems] = std::accumulate(lastColumn->begin() + lowerBound, lastColumn->begin() + upperBound, BigUnsigned(0U));
		}
		std::swap(currentColumn, lastColumn);
	}
	return (*lastColumn)[items];
}

#undef MYMIN
*/

//----------------------------------------------------------------------------------------------------------------------

/*
Current implemenetation (v4):
Based on version 3:
- combines the equal calculations between parameters into a single calculation and therefore has to calculate a lot less
- Computational time: O(k*N) (loop -> ( std::partial_sum, loop ) ) ( + sort O(k*log(k)), k is usually a lot smaller than N so that doesn't matter)
- Memory: O(N+k) (a lot of data can be ignored while going on)
- boils down to adding a lot of numbers repeatedly together
- code becomes totally unclear and unintuitive, but works extremely quickly
- slightly optimized for sorted input (see constructor)
- two container optimization removed, it wasn't doing anything anymore, other than adding complexity
- there is nearly no parallelization potential anymore, but that is because all the things that could be calculated in parallel before are now done at once
*/
BigUnsigned Flaschenzug::Flaschenzug::GetNumberOfPermutationsInternalIterative(uint_fast32_t items)
{
	uint_fast32_t numContainers = containers.size();
	//these are the combined capacities of the containers beginning at every index
	std::vector<uint_fast32_t> combinedRestContainerCapacities(numContainers);
	std::partial_sum(containers.rbegin(), containers.rend(), combinedRestContainerCapacities.begin());
	//only two rows of item parameters are needed because the dependencies are always exactly one container down
	std::array<std::vector<BigUnsigned>, 2> resultsVectors{};
	resultsVectors.fill(std::vector<BigUnsigned>(items + 1, 1U));
	std::vector<BigUnsigned> *lastColumn = &resultsVectors[0], *currentColumn = &resultsVectors[1];

	//Last container gets implicitly calculated in the construction of the vectors
	//there is always only one way to arrange any amount of equal items in a single container
	if (numContainers <= 1) return 1U;

	for (int numContainersLeft = 2; numContainersLeft <= numContainers; ++numContainersLeft)
	{
		//for (auto& i : *lastColumn) std::cout << i << " ";
		//this is the index of the container currently examined
		uint_fast32_t containerIndex = numContainers - numContainersLeft;
		//this stores the capacity of all containers after the current one
		uint_fast32_t combinedRestContainerCapacity = combinedRestContainerCapacities[numContainersLeft - 2];
		//this calculates the highest items parameter needed from the last row (lower bound is always 0)
		uint_fast32_t highestNeededSum = std::min(items, std::max(combinedRestContainerCapacity, items - containers[containerIndex]));
		//this preemptively sums up all the entries from the last row
		std::partial_sum(lastColumn->begin(), lastColumn->begin() + highestNeededSum + 1, lastColumn->begin());
		for (int currentItems = 0; currentItems <= (int) items; ++currentItems)
		{
			//every entry in a row is the sum over a range of entries in the row before
			//the partial sums up to the upper bound - the sums to the lower bound give the sum over the desired range
			//it's like using the antiderivative to get the integral on a range
			uint_fast32_t lowerBound = currentItems - std::min((int) containers[containerIndex], currentItems);
			uint_fast32_t upperBound = clamp(combinedRestContainerCapacity, lowerBound, (uint_fast32_t) currentItems);
			(*currentColumn)[currentItems] = (*lastColumn)[upperBound] - (lowerBound == 0 ? 0 : (*lastColumn)[lowerBound - 1]);
		}
		//the newly calculated row is then used as the source for the next one
		std::swap(currentColumn, lastColumn);
	}
	//for (auto& i : *lastColumn) std::cout << i << " ";
	//the last value in the row that just got calculated is the desired result
	return (*lastColumn)[items];
}
