#include "Map.h"

#include "disjoint_set.h"

#include <algorithm>
#include <vector>
#include <string>
#include <cctype>
#include <sstream>
#include <iterator>
#include <iostream>
#include <set>
#include <stack>
#include <functional>


Kassiopeia::Map::Map()
	:isMapInitialized(false)
{
}


Kassiopeia::Map::~Map()
{
}

void Kassiopeia::Map::PrintMap()
{
	int lastline = 0;
	std::string tmpBuffer;
	std::string toPrint;
	for (auto it : *this)
	{
		if (it.first.y > lastline)
		{
			toPrint.append("\n");
			++lastline;
		}
		if (!it.second.isFree)
			tmpBuffer = "#";
		else
		{
			if (it.second.isTurtle)
				tmpBuffer = "K";
			else if (it.second.isAlreadyVisited)
				tmpBuffer = ".";
			else
				tmpBuffer = " ";
		}
		toPrint.append(tmpBuffer);
	}
	std::cout << toPrint << "\n";
}

bool Kassiopeia::Map::LoadMap(std::istream& in)
{
	using std::string;
	using std::vector;

	if (!in.good())
	{
		std::cerr << "input stream invalid (the file you entered probably does not exist)";
		return false;
	}

	try {
		//read the maps dimensions and construct a map of specified size
		{
			in >> height >> width;
			mapData.resize((size_t) height * width);
		}

		in.ignore(256, '\n');

		//read the maps data
		{
			string line;
			int lineCounter = 0;
			MapData tmp;
			while (std::getline(in, line).good())
			{
				for (int i = 0; i < width; i++)
				{
					switch (line.at(i))
					{
					case '#':
						tmp = { false, false, false };
						break;
					case ' ':
						tmp = { true, false, false };
						break;
					case 'K':
						tmp = { true, true, false };
						break;
					default:
						throw std::exception("the map contained some symbols the program could not understand");
					}
					SetMap(i, lineCounter, tmp);
				}
				lineCounter++;
				if (lineCounter >= height) break;
			}
		}

	} catch (const std::exception ex) {
		std::cerr << "Got an error while loading map: " << ex.what() << std::endl;
		return false;
	}
	isMapInitialized = true;
	return true;
}

void Kassiopeia::Map::SetMap(int x, int y, const MapData & data)
{
	mapData[y * width + x] = data;
}

void Kassiopeia::Map::SetMap(ivec2 p, const MapData & data)
{
	SetMap(p.x, p.y, data);
}

Kassiopeia::Map::MapData Kassiopeia::Map::GetMap(int x, int y) const
{
	return mapData[y * width + x];
}

Kassiopeia::Map::MapData Kassiopeia::Map::GetMap(ivec2 p) const
{
	return GetMap(p.x, p.y);
}

Kassiopeia::Map::MapData& Kassiopeia::Map::GetMap(int x, int y)
{
	return mapData[y * width + x];
}

Kassiopeia::Map::MapData& Kassiopeia::Map::GetMap(ivec2 p)
{
	return GetMap(p.x, p.y);
}

Kassiopeia::Map::MapIterator Kassiopeia::Map::begin()
{
	return MapIterator{ *this };
}

Kassiopeia::Map::MapIterator Kassiopeia::Map::end()
{
	MapIterator mi{ *this };
	mi.p.x = 0;
	mi.p.y = height;
	return mi;
}


//Two-pass variation stolen from https://en.wikipedia.org/wiki/Connected-component_labeling
std::vector<int> Kassiopeia::Map::PartitionMap(bool considerPathsAsRegions, bool considerTurtleAsWall)
{
	std::vector<int> regionlabels(width * height, -1);
	auto getLabel = [this, &regionlabels](int x, int y)->int& {return regionlabels[y * width + x]; };
	disjoint_set<int> labelset{};
	labelset.insert(-1);

	//1st pass
	{
		auto isEqualColor = [this, &considerPathsAsRegions, &considerTurtleAsWall](int x, int y, const MapData& self)
		{
			return GetMap(x, y).isUsable() 
				&& (considerPathsAsRegions ? (self.internal_isPath == GetMap(x, y).internal_isPath) : true) 
				&& (considerTurtleAsWall ? !GetMap(x, y).isTurtle : true);
		};
		int labelcounter = 0;
		for (auto cell : *this)
		{
			if (!GetMap(cell.first.x, cell.first.y).isUsable() || (considerTurtleAsWall&&cell.second.isTurtle)) continue;
			if (cell.first.x > 0 && isEqualColor(cell.first.x - 1, cell.first.y, cell.second))
			{
				if (cell.first.y > 0 && isEqualColor(cell.first.x, cell.first.y - 1, cell.second))
				{
					getLabel(cell.first.x, cell.first.y) = std::min(getLabel(cell.first.x - 1, cell.first.y), getLabel(cell.first.x, cell.first.y - 1));
					labelset.merge(getLabel(cell.first.x - 1, cell.first.y), getLabel(cell.first.x, cell.first.y - 1));
				}
				else
					getLabel(cell.first.x, cell.first.y) = getLabel(cell.first.x - 1, cell.first.y);
			}
			else if (cell.first.y > 0 && isEqualColor(cell.first.x, cell.first.y - 1, cell.second))
			{
				getLabel(cell.first.x, cell.first.y) = getLabel(cell.first.x, cell.first.y - 1);
			}
			else
			{
				getLabel(cell.first.x, cell.first.y) = labelcounter;
				labelset.insert(labelcounter++);
			}
		}
	}

	//2nd pass
	for (auto cell : *this)
	{
		int& label = getLabel(cell.first.x, cell.first.y);
		label = labelset.find(label);
	}

	return regionlabels;
}

int Kassiopeia::Map::NumberOfRegions(bool considerPathsAsRegions, bool considerTurtleAsWall)
{
	std::vector<int> regionlabels = PartitionMap(considerPathsAsRegions, considerTurtleAsWall);
	std::sort(regionlabels.begin(), regionlabels.end());
	return std::distance(regionlabels.begin(), std::unique(regionlabels.begin(), regionlabels.end())) - 1;
}

bool Kassiopeia::Map::IsMapContinuous(bool considerPathsAsRegions, bool considerTurtleAsWall)
{
	return NumberOfRegions(considerPathsAsRegions, considerTurtleAsWall) == 1;
}

Kassiopeia::Map::path_result_type Kassiopeia::Map::FindFillingPath()
{
	std::string result;
	result.reserve(width*height);
	for (auto d : *this) d.second.isAlreadyVisited = false;
	ivec2 currentposition = std::find_if(begin(), end(), [this](const MapIterator::value_type& d) {return d.second.isTurtle; }).p;
	ivec2 turtleposition = currentposition;
	ivec2 startingturtleposition = turtleposition;

	int printCounter = 0;
	bool printStuff = true;

	auto moveTurtle = [&](ivec2 newPosition)
	{
		GetMap(turtleposition).isTurtle = false;
		turtleposition = newPosition;
		GetMap(turtleposition).isTurtle = true;
	};

	updateAllInternalMarkers();

	std::function<bool(Direction)> recursiveSearch = [&](Direction dir) -> bool
	{
		{ //move
			if (dir != Direction::NONE) GetMap(currentposition).isAlreadyVisited = true;
			updateAroundPoint(currentposition);
			currentposition = currentposition + DirectionVectorsWithNone[dir];
			moveTurtle(currentposition);
		}
		auto rewind = [&]() {
			currentposition = currentposition - DirectionVectorsWithNone[dir];
			GetMap(currentposition).isAlreadyVisited = false;
			updateAroundPoint(currentposition);
			moveTurtle(currentposition);
		};

		if (printCounter++ == 100)
		{
			std::cout << "This is taking longer than expected - hold on..." << std::endl;
			printStuff = false;
		}
		if (printCounter == 50000)
			std::cout << "The chances of there being a path seem slim..." << std::endl;
		if (printCounter == 1000000)
			std::cout << "Seriously, just terminate this program already..." << std::endl;
		if (printCounter == 10000000)
			std::cout << "OK, it seems you enjoy wasting your time watching me while I test all the possible permutations of paths on this one. I am sure you have got something better to do." << std::endl;
		if(printStuff)
		{
			std::cout << "Testing:\n";
			PrintMap();
		}

		//if there is no cell left to be visited, return true
		if (std::find_if(begin(), end(), [](const MapIterator::value_type& d)->bool {
			return d.second.isUsable() && !d.second.isTurtle;
		}) == end())
		{
			rewind();
			return true;
		}

		//skip the search if there are too many dead ends
		std::vector<MapIterator::value_type> deadEnds;
		std::copy_if(begin(), end(), std::back_inserter(deadEnds), [](const MapIterator::value_type& d)->bool {return d.second.internal_isDeadEnd; });
		if (deadEnds.size() > 2) goto label_skipSearch;
		else if (deadEnds.size() == 2 && !(deadEnds[0].first == currentposition || deadEnds[1].first == currentposition)) goto label_skipSearch;

		//skip search if there is more than one cohesive region
		if (!IsMapContinuous(false, true)) goto label_skipSearch;

		//try to move K in every direction
		for (auto d : Directions)
		{
			ivec2 v = currentposition + DirectionVectorsWithNone[d];
			if (in_range(v, { 0,0 }, { width - 1, height - 1 }) && GetMap(v).isUsable())
				if (recursiveSearch(d))
				{
					result.push_back(DirectionLetters[d]);
					rewind();
					return true;
				}
		}

	label_skipSearch:
		if(printStuff)
			std::cout << "Backtracking...\n";
		rewind();
		return false;
	};

	if (!recursiveSearch(Direction::NONE))
		return path_result_type{ false, std::string{} };

	std::reverse(result.begin(), result.end());

	updateAllInternalMarkers();

	return path_result_type{ true, result };
}

void Kassiopeia::Map::updateAllInternalMarkers(bool turtleIsWall)
{
	for (auto it : *this)
	{
		updatePoint({ it.first.x, it.first.y }, turtleIsWall);
	}
}

void Kassiopeia::Map::updateAroundPoint(ivec2 p, bool turtleIsWall)
{
	updatePoint(p);
	for (Direction d : Directions) updatePoint(p + DirectionVectors[d], turtleIsWall);
}

void Kassiopeia::Map::updatePoint(ivec2 point, bool turtleIsWall)
{
	if (!in_range(point, { 0,0 }, { width - 1, height - 1 })) return;
	if (!GetMap(point).isUsable())
	{
		MapData& d = GetMap(point);
		d.internal_isDeadEnd = false;
		d.internal_isPath = false;
		return;
	}
	bool isVisited = GetMap(point).isAlreadyVisited;

	int walls = 0;
	for (Direction d : Directions)
		if (!in_range(point + DirectionVectors[d], { 0,0 }, { width - 1, height - 1 }) 
			|| !GetMap(point + DirectionVectors[d]).isUsable() 
			|| (turtleIsWall && GetMap(point + DirectionVectors[d]).isTurtle) )
			++walls;

	GetMap(point).internal_isPath = (walls >= 2);
	GetMap(point).internal_isDeadEnd = (walls == 3) && !(isVisited);
}


inline Kassiopeia::Map::MapIterator::MapIterator(Map & m) : m(m) { reset(); }

inline Kassiopeia::Map::MapData Kassiopeia::Map::MapIterator::get() { return m.GetMap(p); }

inline Kassiopeia::Map::MapData & Kassiopeia::Map::MapIterator::getR() { return m.GetMap(p); }

inline bool Kassiopeia::Map::MapIterator::good() const { return p.y < m.height && p.x < m.width; }

inline void Kassiopeia::Map::MapIterator::reset() { p = { 0,0 }; }

bool Kassiopeia::Map::MapData::isUsable() const
{
	return isFree && !isAlreadyVisited;
}
