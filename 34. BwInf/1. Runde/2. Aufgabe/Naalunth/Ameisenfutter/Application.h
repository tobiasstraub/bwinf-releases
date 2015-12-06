#pragma once
#include "Ameisenfutter.h"

#include <memory>

class Application
{
public:
	Application();
	~Application();

	enum State
	{
		MAIN, CLOSING, SIMULATION_SETUP, SIMULATION
	} currentState;

	void run();
	void transitionState(State next);

private:
	std::unique_ptr<Ameisenfutter::Ameisenfutter> ameisenfutter;
};

