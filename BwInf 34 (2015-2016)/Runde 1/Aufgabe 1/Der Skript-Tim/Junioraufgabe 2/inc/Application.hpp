#pragma once

#include <string>
#include <vector>

struct Application
{

	struct Coord
	{
		unsigned int x, y;
	};

	auto main(const std::vector<std::string>& arguments) -> int;
	auto loadFromFile(void) -> bool;

	auto Application::tiefensuche(Coord path, const int deep) -> void;
	auto Application::warSchonDa(Coord f) -> bool;

	std::string _filename;

	std::vector<std::vector<bool> > field;
	unsigned int startX, startY = -1;
	int anzahlWeisseFelder;

	std::vector<Application::Coord> erreichteZustaende;

	std::vector< std::vector<Application::Coord> > endzustaende;
};