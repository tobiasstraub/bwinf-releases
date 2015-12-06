#include "Application.h"

#include "Flaschenzug.h"

#include "bigint/BigIntegerLibrary.hh"

#include <iostream>
#include <fstream>
#include <cstdint>


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

	cout << "Flaschenzug 1.3\n"
		<< "Author: Kevin Schier\n"
		<< "Licensed under WTFPL\n\n";

	auto mainState = [&]()
	{
		cout << "\n"
			<< "1: Load file\n"
			<< "2: All given demos\n"
			<< "0: Exit\n"
			<< "> ";
		int input;
		cin >> input;
		switch (input)
		{
		case 1:
		{
			cout << "Enter filename:\n> ";
			std::string filename;
			cin >> filename;
			std::ifstream file{ filename };
			int items, numContainers;
			file >> items >> numContainers;
			std::vector<uint32_t> containers(numContainers);
			for (int i = 0; i < numContainers; i++)
				file >> containers[i];

			Flaschenzug::Flaschenzug f{ containers };
			cout << "There are " << f.GetNumberOfPermutations(items) << " ways to arrange the bottles." << std::endl;
		}
		break;
		case 2:
		{
			for (int fileid = 0; fileid <= 5; fileid++)
			{
				std::string filename = std::string("flaschenzug").append(std::to_string(fileid)).append(".txt");
				std::ifstream file{ filename };
				int items, numContainers;
				file >> items >> numContainers;
				std::vector<uint32_t> containers(numContainers);
				for (int i = 0; i < numContainers; i++)
					file >> containers[i];

				Flaschenzug::Flaschenzug f{ containers };
				cout << filename << ": " << f.GetNumberOfPermutations(items) << " permutations" << std::endl;
			}
		}
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
	}
}
