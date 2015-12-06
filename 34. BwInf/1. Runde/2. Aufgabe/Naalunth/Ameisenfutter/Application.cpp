#include "Application.h"

#include "Ameisenfutter.h"

#include <iostream>


Application::Application()
{
}


Application::~Application()
{
}

void Application::run()
{
	using std::cin;
	using std::cout;

	currentState = MAIN;

	std::string inputString;

	cout << "Ameisenfutter 1.0\n"
		<< "Author: Kevin Schier\n"
		<< "Licensed under WTFPL\n\n";

	auto mainState = [&]()
	{
		cout << "\n"
			<< "1: Start simulation\n"
			<< "0: Exit\n"
			<< "> ";
		int input;
		cin >> input;
		switch (input)
		{
		case 1:
			transitionState(SIMULATION_SETUP);
			break;
		case 0:
			transitionState(CLOSING);
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
		case SIMULATION:
			ameisenfutter->run();
			transitionState(MAIN);
			break;
		case CLOSING:
			std::cout << "Bye! :D";
			running = false;
			break;
		default:
			transitionState(CLOSING);
		}
	}
}


void Application::transitionState(State next)
{
	using std::cin;
	using std::cout;
	State lastState = currentState;
	currentState = next;

	switch (currentState)
	{
	case SIMULATION_SETUP:
	{
		int numAnts;
		int numFoods;
		float evaporationRate;
		cout << "Enter number of ants:\n> ";
		cin >> numAnts;
		cout << "Enter number of food sources:\n> ";
		cin >> numFoods;
		cout << "Enter pheromone evaporation rate (chance per step for each unit to evaporate, try 0.02):\n> ";
		cin >> evaporationRate;
		cout << "Right click on the map to set the nest position and start the simulation.\n"
			<< "Left click to pan the view, mouse wheel to zoom." << std::endl;
		ameisenfutter = std::make_unique<Ameisenfutter::Ameisenfutter>(numAnts, numFoods, evaporationRate);
		transitionState(SIMULATION);
	}
	break;
	}
}
