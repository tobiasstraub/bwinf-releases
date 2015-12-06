#include "Map.h"
#include "Ameisenfutter.h"

#include "vectors.h"
#include "util.h"

#include <GL/glew.h>

#include <SFML/Graphics.hpp>
#include <SFML/OpenGL.hpp>

#include <mutex>
#include <future>
#include <iostream>
#include <utility>
#include <array>
#include <algorithm>
#include <chrono>
#include <random>
#include <cassert>
#include <cstdlib>
#include <functional>
#include <numeric>


Ameisenfutter::Map::Map(int width, int height, int numAnts, int numFoods, int foodPerStack, float evaporationChance)
	: width{ width },
	height{ height },
	evaporationChance{ evaporationChance },
	currentlyActiveData{ 0 },
	mapData0(width*height),
	mapData1(width*height),
	rng{},
	numAnts{numAnts}
{
	std::random_device rd;
	rng.seed(rd());
	for (int i = 0; i < numFoods; i++)
	{
		std::uniform_int_distribution<int> dist{ 0, 499 };
		int x = dist(rng), y = dist(rng);
		if (GetPoint(x, y).numFood > 0)
			i--;
		else
			GetPoint(x, y).numFood = foodPerStack;
	}
}


Ameisenfutter::Map::~Map()
{
}

const Ameisenfutter::Map::MapData & Ameisenfutter::Map::GetPoint(ivec2 p) const
{
	return GetPoint(p.x, p.y);
}

const Ameisenfutter::Map::MapData & Ameisenfutter::Map::GetPoint(int x, int y) const
{
	return GetMapData()[y * width + x];
}

Ameisenfutter::Map::MapData & Ameisenfutter::Map::GetPoint(ivec2 p)
{
	return GetPoint(p.x, p.y);
}

Ameisenfutter::Map::MapData & Ameisenfutter::Map::GetPoint(int x, int y)
{
	return GetMapData()[y * width + x];
}

std::vector<Ameisenfutter::Map::MapData>& Ameisenfutter::Map::GetMapData()
{
	return currentlyActiveData ? mapData1 : mapData0;
}

const std::vector<Ameisenfutter::Map::MapData>& Ameisenfutter::Map::GetMapData() const
{
	return currentlyActiveData ? mapData1 : mapData0;
}

std::vector<Ameisenfutter::Map::MapData>& Ameisenfutter::Map::GetInactiveMapData()
{
	return !currentlyActiveData ? mapData1 : mapData0;
}

void Ameisenfutter::Map::AddNest(ivec2 position)
{
	auto& middle = GetPoint(position.x, position.y);
	middle.isNest = true;
	middle.numAnts = numAnts;
}

Ameisenfutter::Map::MapIterator Ameisenfutter::Map::begin()
{
	return MapIterator{ *this };
}

Ameisenfutter::Map::MapIterator Ameisenfutter::Map::end()
{
	MapIterator mi{ *this };
	mi.p.x = 0;
	mi.p.y = height;
	return mi;
}


void Ameisenfutter::Map::update(int steps)
{
	if (steps > 0)
	{
		while (steps--)
			doUpdateStep();
		shouldUpdateGLMapData = true;
	}
}

void Ameisenfutter::Map::doUpdateStep()
{
	std::uniform_int_distribution<int> dist;
	std::binomial_distribution<int> p;
	std::vector<MapData>& newMap{ GetInactiveMapData() };
	std::binomial_distribution<int> pheromone_dist;
	ivec2 nest = { 0,0 };

	//this function makes sure there are no out of bounds errors
	MapData dummy;
	auto getNewPoint = [&](ivec2 p) -> MapData& {return in_area(p, ivec2{ 0,0 }, ivec2{ width - 1, height - 1 }) ? newMap[p.y * width + p.x] : dummy; };

	//This function returns the direction priorities for a given direction vector
	auto getDirectionOrder = [&](ivec2 dir)->std::array<int, 4>
	{
		std::array<int, 4> P{ NORTH, EAST, SOUTH, WEST };
		std::array<int, 2> nsP = (0 < dir.y) ? std::array<int, 2>{ SOUTH, NORTH } : std::array<int, 2>{ NORTH, SOUTH };
		std::array<int, 2> ewP = (0 < dir.x) ? std::array<int, 2>{ EAST, WEST } : std::array<int, 2>{ WEST, EAST };
		P = (std::abs(dir.y) < std::abs(dir.x)) ?
			std::array<int, 4>{ ewP[0], nsP[0], nsP[1], ewP[1] } :
			std::array<int, 4>{ nsP[0], ewP[0], ewP[1], nsP[1] };
		return P;
	};

	//This function calculates where ants on a tile should go
	auto getDistribution = [&](int max, std::array<int, 4> markers, ivec2 toNest)->std::array<int, 5>
	{
		std::array<int, 5> res = { 0,0,0,0,0 };
		if (max > 0)
		{
			bool markersNearby = false;
			for (bool b : markers)
				if (b) markersNearby = true;

			if (markersNearby)
			{
				//if there are markers nearby, send the ants to the tile farthest away from the nest according to the getDirectionOrder() function
				ivec2 dir = -toNest;
				std::array<int, 4> P = getDirectionOrder(dir);
				int mCount = std::count_if(P.begin(), P.end(), [&markers](int i) {return markers[i]; });
				if (mCount <= 2 && dir != ivec2{ 0, 0 })
				{
					for (auto i : P)
						if (markers[i])
						{
							res[i] = max;
							break;
						}
				}
				else
				{
					//if there are multiple choices, distribute them randomly
					for (auto i : P)
						if (markers[i])
						{
							if (mCount > 1)
							{
								p = std::binomial_distribution<int>{ max, mCount / (double) (mCount * 2 - 1) };
								float amount = clamp(p(rng), 0, max);
								res[i] = amount;
								max -= amount;
								--mCount;
							}
							else
							{
								res[i] = max;
								break;
							}
						}
				}
			}
			else //!markersNearby
			{
				if (max < 6) //avoid the binomial_distribution call, if there only a few ants in the cell
				{
					const auto smalldist = std::uniform_int_distribution<int>{ 0,4 };
					while (max--) ++res[smalldist(rng)];
				}
				else
				{
					p = std::binomial_distribution<int>{ max, 0.2 };
					res[4] = p(rng);
					max -= res[4];
					dist = std::uniform_int_distribution<int>{ 0, max };
					int ns = dist(rng);
					int we = max - ns;
					dist = std::uniform_int_distribution<int>{ 0, ns };
					res[0] = dist(rng);
					res[1] = ns - res[0];
					dist = std::uniform_int_distribution<int>{ 0, we };
					res[2] = dist(rng);
					res[3] = we - res[2];
				}
			}
		}
		return res;
	};

	//Clear the map
	for (int i = 0; i < width*height; i++)
	{
		auto& d = GetInactiveMapData()[i];
		d.numAnts = 0;
		d.numAntsWithFood = 0;
		nest = d.isNest ? ivec2{ i % width, i / width } : nest;
	}

	//Main loop iterating over every source cell
	for (auto it : *this)
	{
		auto& data = it.second;
		auto& ndata = getNewPoint(it.first);

		ndata.isNest = data.isNest;
		ndata.numFood = data.numFood;

		//if there is food on this cell, convert ants on it
		if (data.numFood > 0)
		{
			if (data.numFood < data.numAnts)
			{
				data.numAnts -= data.numFood;
				ndata.numAntsWithFood += data.numFood;
				ndata.numFood = 0;
			}
			else
			{
				ndata.numFood -= data.numAnts;
				ndata.numAntsWithFood += data.numAnts;
				data.numAnts = 0;
			}
		}

		//update pheromone
		if (data.numPheromone > 0 || data.numAntsWithFood > 0)
		{
			if (data.numPheromone <= 1)
				data.numPheromone = 0;
			pheromone_dist = std::binomial_distribution<int>{ (int) data.numPheromone , 1.0 - evaporationChance };
			ndata.numPheromone = std::floor(pheromone_dist(rng)) + (data.numAntsWithFood << 16); //every ant leaves 2^16 pheromones
		}

		//deliver food to nest
		if (data.isNest)
		{
			ndata.numAnts += data.numAntsWithFood;
			data.numAntsWithFood = 0;
		}

		//update positions of ants without food
		if (data.numAnts > 0)
		{
			std::array<int, 4> markers;
			for (int dir : Directions)
			{
				ivec2 p(it.first + DirectionVectorsWithNone[dir]);
				markers[dir] = (in_area(p, ivec2{ 0,0 }, ivec2{ width - 1, height - 1 })) ? (GetPoint(p).numPheromone) : 0;
			}

			auto antDistribution = getDistribution(data.numAnts, markers, nest - it.first);
			//all these calculations make sure that ants don't go out of bounds
			if (it.first.x == 0)
				antDistribution[EAST] += antDistribution[WEST];
			if (it.first.x == width - 1)
				antDistribution[WEST] += antDistribution[EAST];
			if (it.first.y == 0)
				antDistribution[SOUTH] += antDistribution[NORTH];
			if (it.first.y == height - 1)
				antDistribution[NORTH] += antDistribution[SOUTH];

			getNewPoint(it.first + DirectionVectors[NORTH]).numAnts += antDistribution[NORTH];
			getNewPoint(it.first + DirectionVectors[EAST]).numAnts += antDistribution[EAST];
			getNewPoint(it.first + DirectionVectors[SOUTH]).numAnts += antDistribution[SOUTH];
			getNewPoint(it.first + DirectionVectors[WEST]).numAnts += antDistribution[WEST];
			ndata.numAnts += antDistribution[NONE];
		}

		//update positions of ants with food
		if (data.numAntsWithFood > 0)
		{
			ivec2 toNest(nest - it.first);
			int nestDirection = std::abs(toNest.x) > std::abs(toNest.y) ? (toNest.x > 0 ? EAST : WEST) : (toNest.y > 0 ? SOUTH : NORTH);
			getNewPoint(it.first + DirectionVectors[nestDirection]).numAntsWithFood += data.numAntsWithFood;
		}
	}
	swapMapData();
}

void Ameisenfutter::Map::swapMapData()
{
	currentlyActiveData = currentlyActiveData == 0 ? 1 : 0;
}


///////////////////////
// MapIterator stuff //
///////////////////////

Ameisenfutter::Map::MapIterator::MapIterator(Map & m)
	:m{ m }
{
	p = { 0,0 };
}

Ameisenfutter::Map::MapIterator::MapIterator(const MapIterator & other)
	: m{ other.m }
{
	p = other.p;
}

inline const Ameisenfutter::Map::MapData & Ameisenfutter::Map::MapIterator::get() const
{
	return m.GetPoint(p);
}

inline Ameisenfutter::Map::MapData & Ameisenfutter::Map::MapIterator::get()
{
	return m.GetPoint(p);
}

inline bool Ameisenfutter::Map::MapIterator::good() const
{
	return p.y < m.height && p.x < m.width;
}

inline void Ameisenfutter::Map::MapIterator::reset()
{
	p = { 0,0 };
}
