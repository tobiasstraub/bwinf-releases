#pragma once

#include <string>
#include <vector>

struct Application
{

	struct Coord
	{
		unsigned int x, y;
		char action;
	};

	auto main(const std::vector<std::string>& arguments) -> int;
	auto loadFromFile(void) -> bool;

	auto Application::tiefensuche(std::vector<Coord> path, int deep) -> bool;
	auto Application::warSchonDa(std::vector<Application::Coord> path, Application::Coord f) -> bool;
	auto Application::goTo(Application::Coord pos, std::vector<Coord> path, const int deep) -> bool;

	std::string _filename;

	bool _justOneWay = false;

	std::vector<std::vector<bool> > field;
	unsigned int startX, startY = -1;
	int anzahlWeisseFelder;

	std::vector< std::vector<Application::Coord> > endzustaende;
};