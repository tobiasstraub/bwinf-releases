#pragma once

#include "util.h"
#include "vectors.h"

#include <GL/glew.h>

#include <SFML/Graphics.hpp>
#include <SFML/OpenGL.hpp>

#include <cstdint>
#include <vector>
#include <atomic>
#include <random>

namespace Ameisenfutter
{
	class Ameisenfutter;
	class MapViewer;
	class Map
	{
		friend class MapViewer;
	public:
		struct MapData
		{
			uint32_t numAnts;
			uint32_t numAntsWithFood;
			uint32_t numFood;
			uint32_t numPheromone;
			bool isNest;
		};

		Map(int width, int height, int numAnts, int numFoods, int foodPerStack, float evaporationChance);
		~Map();


		struct MapIterator : public std::iterator<std::forward_iterator_tag, std::pair<ivec2, MapData&>>
		{
			MapIterator(Map& m);
			MapIterator(const MapIterator& other);
			void operator=(const MapIterator& other) { p = other.p; }
			void operator++() { if(++p.x >= m.width) { p.x = 0; ++p.y; } }
			value_type operator*() { return std::pair<ivec2, MapData&>(p, get()); }
			bool operator!=(const MapIterator& other) const { return !(p == other.p); }
			bool operator==(const MapIterator& other) const { return p == other.p; }
			const MapData& get() const;
			MapData& get();
			ivec2 getPoint() { return p; };
			bool good() const;
			void reset();
			ivec2 p;
		private:
			Map& m;
		};

		//Get the map data at the specified coordinates
		const MapData& GetPoint(ivec2 p) const;
		//Get the map data at the specified coordinates
		const MapData& GetPoint(int x, int y) const;

		//Get a reference to the map data at the specified coordinates
		MapData& GetPoint(ivec2 p);
		//Get a reference to the map data at the specified coordinates
		MapData& GetPoint(int x, int y);

		std::vector<MapData>& GetMapData();
		const std::vector<MapData>& GetMapData() const;
		std::vector<MapData>& GetInactiveMapData();

		void AddNest(ivec2 position);

		MapIterator begin();
		MapIterator end();

		void update(int steps);

	private:
		int width, height;
		int numAnts;
		float evaporationChance;
		std::atomic<uint8_t> currentlyActiveData;
		std::vector<MapData> mapData0;
		std::vector<MapData> mapData1;

		std::minstd_rand rng;

		void doUpdateStep();
		void swapMapData();

		mutable std::atomic<bool> shouldUpdateGLMapData = 0;
	};
}
