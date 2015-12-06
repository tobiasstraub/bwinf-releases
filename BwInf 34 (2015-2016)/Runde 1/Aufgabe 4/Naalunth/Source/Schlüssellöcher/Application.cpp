#include "Application.h"

#include "Schlüssellöcher.h"
#include "Key.h"

#include <iostream>
#include <fstream>
#include <cstdint>

double calculateMeanDifference(const Schlüssellöcher::Key& key, const std::vector<Schlüssellöcher::Key>& otherKeys)
{
	double differenceFactor = 0.0;
	for (auto& otherKey : otherKeys)
	{
		double diff = otherKey.meanDifferenceWithFlipped(key);
		differenceFactor += diff;
	}
	return differenceFactor / otherKeys.size();
}

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

	
	cout << "Schluesselloecher 1.0\n" //Windows console still messes up everything thats not english
		<< "Author: Kevin Schier\n"
		<< "Licensed under WTFPL\n\n";

	auto mainState = [&]()
	{
		cout << "\n"
			<< "1: Generate keys\n"
			<< "0: Exit\n"
			<< "> ";
		int input;
		cin >> input;
		switch (input)
		{
		case 1:
		{
			cout << "Enter number of keys\n> ";
			int numKeys;
			cin >> numKeys;
			Schlüssellöcher::Schlüssellöcher s;
			std::vector<Schlüssellöcher::Key> keys;
			keys = s.CalculateNewKeys(5, keys, numKeys);
			for (auto& key : keys)
			{
				for (int i = 0; i < 5; i++)
				{
					for (int j = 0; j < 5; j++)
					{
						cout << key.bits[i * 5 + j];
					}
					cout << "\n";
				}
				cout << "\n";
			}
			double diff = 0;
			while (keys.size() >= 2)
			{
				auto key0 = keys[0];
				keys.erase(keys.begin());
				diff += calculateMeanDifference(key0, keys) * keys.size();
			}
			cout << "mean difference (with mirrored keys):" << diff / (numKeys*(numKeys-1)*0.5) << std::endl;
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
