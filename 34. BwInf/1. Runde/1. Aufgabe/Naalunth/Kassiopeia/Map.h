#pragma once

#include "util.h"

#include <cstdint>
#include <vector>
#include <string>
#include <tuple>

namespace Kassiopeia
{
	class Map
	{
	public:
		struct MapData
		{
			uint8_t isFree : 1;
			uint8_t isTurtle : 1;
			uint8_t isAlreadyVisited : 1;
			uint8_t internal_isPath : 1;
			uint8_t internal_isDeadEnd : 1;

			MapData() = default;
			MapData(const MapData& other) = default;
			MapData& operator=(const MapData& other) = default;

			bool isUsable() const;
		};

		struct MapIterator : public std::iterator<std::forward_iterator_tag, std::pair<ivec2, MapData&>>
		{
			MapIterator(Map& m);
			void operator=(const MapIterator& other) { m = other.m; p = other.p; }
			void operator++() { if (++p.x >= m.width) { p.x = 0; ++p.y; } }
			value_type operator*() { return std::pair<ivec2, MapData&>(p, getR()); }
			bool operator!=(const MapIterator& other) const { return !(p == other.p); }
			bool operator==(const MapIterator& other) const { return p == other.p; }
			MapData get();
			MapData& getR();
			ivec2 getPoint() { return p; };
			bool good() const;
			void reset();
			ivec2 p;
		private:
			Map& m;
		};

		Map();
		~Map();

		//Prints a nice little representation of the map to std::cout
		void PrintMap();

		//Loads a map from the specified istream, returns false on error
		//throws exceptions
		bool LoadMap(std::istream& in);
		bool LoadMap(std::istream&& in) = delete;

		//Set the map data at the specified coordinates
		void SetMap(int x, int y, const MapData& data);
		//Set the map data at the specified coordinates
		void SetMap(ivec2 p, const MapData& data);

		//Get the map data at the specified coordinates
		MapData GetMap(int x, int y) const;
		//Get the map data at the specified coordinates
		MapData GetMap(ivec2 p) const;

		//Get a reference to the map data at the specified coordinates
		MapData& GetMap(int x, int y);
		//Get a reference to the map data at the specified coordinates
		MapData& GetMap(ivec2 p);

		MapIterator begin();
		MapIterator end();

		//Works out different partitions in the map
		std::vector<int> PartitionMap(bool considerPathsAsWalls = false, bool considerTurtleAsWall = false);

		//Counts how many continous regions the map has
		//Uses PartitionMap()
		int NumberOfRegions(bool considerPathsAsWalls = false, bool considerTurtleAsWall = false);

		//True if the map has only one continous region
		bool IsMapContinuous(bool considerPathsAsWalls = false, bool considerTurtleAsWall = false);

		typedef std::pair<bool, std::string> path_result_type;
		//Finds a route starting at Kassiopeia that fills the whole map
		path_result_type FindFillingPath();

		int width, height;

	private:
		void updateAllInternalMarkers(bool turtleIsWall = false);
		void updateAroundPoint(ivec2 p, bool turtleIsWall = false);
		void updatePoint(ivec2 p, bool turtleIsWall = false);

		std::vector<MapData> mapData;
		bool isMapInitialized;
	};
}
