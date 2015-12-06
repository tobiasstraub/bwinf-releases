#pragma once

#include "Map.h"
#include "MapViewer.h"

#include <SFML/Graphics.hpp>
#include <SFML/Window.hpp>

#include <atomic>
#include <mutex>

namespace Ameisenfutter
{
	class Ameisenfutter
	{
	public:
		Ameisenfutter(int numAnts, int numFoodSources, float evaporationChance);
		~Ameisenfutter();

		void run();

		std::mutex glMutex;
		std::atomic<bool> isRunning = true;
	private:
		void createWindow(bool fullscreen);
		void recalculateView();
		void handleMouseMoveEvent(const sf::Event& event);
		void handleMouseScrollEvent(const sf::Event& event);
		void handleKeyPressEvent(const sf::Event& event);

		void renderThread(sf::RenderWindow* window);
		void updateThread(sf::RenderWindow* window);

		std::atomic<bool> shouldResize = false;
		std::atomic<bool> nestPositionSet = false;

		Ameisenfutter::Map map;
		Ameisenfutter::MapViewer mapViewer;

		sf::RenderWindow window;
		bool isFullscreen;
		sf::View view;

		sf::Vector2f cameraPosition;
		float zoomFactor;
		float targetZoomFactor;

		sf::Vector2i lastMousePosition;
	};
}
