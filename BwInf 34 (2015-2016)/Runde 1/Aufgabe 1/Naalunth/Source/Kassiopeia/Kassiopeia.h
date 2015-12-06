#pragma once

#include "Map.h"

#include <vector>

namespace Kassiopeia
{

	class Kassiopeia
	{
	public:
		Kassiopeia();
		~Kassiopeia();

		void run();

		enum State
		{
			MAIN, MAP_LOADED, CLOSING
		} currentState;

		void transitionState(State next);


		Map map;

	};
}

