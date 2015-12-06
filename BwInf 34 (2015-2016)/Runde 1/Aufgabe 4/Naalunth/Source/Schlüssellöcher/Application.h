#pragma once

#include "Schlüssellöcher.h"

#include <memory>

class Application
{
public:
	Application();
	~Application();

	enum State
	{
		MAIN, CLOSING
	} currentState;

	void run();
	void transitionState(State next);

private:
	std::unique_ptr<Schlüssellöcher::Schlüssellöcher> schlüssellöcher;
};

