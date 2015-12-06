#include "Kassiopeia.h"

#include "Map.h"

#include <fstream>
#include <iostream>
#include <string>


Kassiopeia::Kassiopeia::Kassiopeia()
{
}


Kassiopeia::Kassiopeia::~Kassiopeia()
{
}

void Kassiopeia::Kassiopeia::run()
{
	using std::cin;
	using std::cout;

	currentState = MAIN;

	auto mainState = [&]()
	{
		cout << "\n"
			<< "1: Load file\n"
			<< "0: Exit\n"
			<< "> ";
		int input;
		cin >> input;
		switch (input)
		{
		case 1:
			transitionState(MAP_LOADED);
			break;
		case 0:
			transitionState(CLOSING);
			break;
		}
	};

	auto mapLoadedState = [&]()
	{
		cout << "\n"
			<< "1: Print Map\n"
			<< "2: Show number of regions\n"
			<< "3: Show filling path\n"
			<< "0: Back\n"
			<< "> ";
		int input;
		cin >> input;
		Map::path_result_type tmp_path;
		switch (input)
		{
		case 1:
			map.PrintMap();
			cout << std::endl;
			break;
		case 2:
			cout << "Number of regions: " << map.NumberOfRegions() << std::endl;
			break;
		case 3:
			tmp_path = map.FindFillingPath();
			if (tmp_path.first)
				if (tmp_path.second == "")
					cout << "No path needed." << std::endl;
				else
					cout << "Path: " << tmp_path.second << std::endl;
			else
				cout << "No path found" << std::endl;
			break;
		case 0:
			transitionState(MAIN);
			break;
		}
	};

	bool running = true;

	while (running)
	{
		switch (currentState)
		{
		case MAIN:
			mainState();
			break;
		case MAP_LOADED:
			mapLoadedState();
			break;
		case CLOSING:
			std::cout << "Bye! :D";
			running = false;
		}
	}
}

void Kassiopeia::Kassiopeia::transitionState(State next)
{
	State oldState = currentState;
	currentState = next;
	switch (next)
	{
	case CLOSING:
		break;
	case MAIN:
		std::cout << "\n";
		break;
	case MAP_LOADED:
		if (oldState == MAIN)
		{
			std::cout << "Enter the filename\n"
				<< "> ";
			std::cout.flush();
			std::string input;
			std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
			std::getline(std::cin, input);
			bool properlyLoaded = false;
			std::ifstream s{ input };
			properlyLoaded = map.LoadMap(s);
			if (!properlyLoaded) currentState = MAIN;
			std::cout << "\n";
		}
		break;
	}
}
