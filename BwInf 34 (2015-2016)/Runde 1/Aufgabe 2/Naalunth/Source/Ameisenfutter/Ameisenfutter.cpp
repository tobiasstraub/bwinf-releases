#include "Ameisenfutter.h"
#include "util.h"

#include <GL/glew.h>

#include <SFML/Graphics.hpp>

#include <iostream>
#include <cmath>
#include <thread>

//multiplier applied to zooming
static const float scrollSpeed = 1.2F;

//dampening factor on zooming
static const float zoomDampening = 0.0001F;

static const int mapWidth = 500;
static const int mapHeight = 500;


Ameisenfutter::Ameisenfutter::Ameisenfutter(int numAnts, int numFoodSources, float evaporationChance)
	:view{ sf::Vector2f{ 0.0, 0.0 }, sf::Vector2f{ 1.0, 1.0 } },
	cameraPosition{ 250.0, 250.0 },
	zoomFactor{ 0.0019f },
	targetZoomFactor{ zoomFactor },
	lastMousePosition{ 0,0 },
	map{ mapWidth, mapHeight, numAnts, numFoodSources, 50, evaporationChance },
	mapViewer{ map },
	isFullscreen{ false }
{
	createWindow(false);
	recalculateView();
}

Ameisenfutter::Ameisenfutter::~Ameisenfutter()
{
}

void Ameisenfutter::Ameisenfutter::renderThread(sf::RenderWindow* w)
{
	sf::RenderWindow& window{ *w };
	window.setActive(true);
	sf::Clock deltaClock;
	while (isRunning)
	{
		sf::Time dt = deltaClock.restart();
		{
			std::unique_lock<std::mutex> lck{ glMutex };
			window.setActive(true);
			window.clear(sf::Color::Black);

			zoomFactor = lerp(targetZoomFactor, zoomFactor, std::pow(zoomDampening, dt.asSeconds()));
			recalculateView();

			window.setView(view);

			mapViewer.setTileScreenSize(zoomFactor*window.getSize().y);
			window.draw(mapViewer, view.getTransform());
			window.resetGLStates();

			window.display();
			window.setActive(false);
		}
	}
}

void Ameisenfutter::Ameisenfutter::updateThread(sf::RenderWindow * window)
{
	sf::Clock deltaClock;
	sf::Time updateTimer;
	while (isRunning)
	{
		sf::Time dt = deltaClock.restart();
		updateTimer += dt;
		float updateRate{ 300.0f };
		int steps = std::floor(updateTimer.asSeconds() * updateRate);
		updateTimer -= sf::seconds(steps / updateRate);
		if (nestPositionSet)
			map.update(clamp(steps, 0, 1));
	}
}

void Ameisenfutter::Ameisenfutter::run()
{
	std::thread renderThread{ &Ameisenfutter::renderThread, this, &window };
	std::thread updateThread{ &Ameisenfutter::updateThread, this, &window };

	while (isRunning)
	{
		sf::Event event;
		while (window.pollEvent(event))
		{
			switch (event.type)
			{
			case sf::Event::Closed:
				isRunning = false;
				break;

			case sf::Event::Resized:
			{
				std::unique_lock<std::mutex> lck{ glMutex };
				window.setActive(true);
				glViewport(0, 0, event.size.width, event.size.height);
				window.setActive(false);
				recalculateView();
			}
			break;

			case sf::Event::MouseButtonPressed:
				lastMousePosition = sf::Mouse::getPosition(window);
				if (!nestPositionSet && event.mouseButton.button == sf::Mouse::Button::Right)
				{
					sf::Vector2i sfp{ window.mapPixelToCoords(lastMousePosition) };
					ivec2 pos{ sfp.x, sfp.y };
					if (!in_area(pos, { 0,0 }, { mapWidth - 1, mapHeight - 1 })) break;
					map.AddNest(pos);
					nestPositionSet = true;
				}
				break;

			case sf::Event::MouseMoved:
				handleMouseMoveEvent(event);
				break;

			case sf::Event::MouseWheelScrolled:
				handleMouseScrollEvent(event);
				break;

			case sf::Event::KeyPressed:
				handleKeyPressEvent(event);
				break;
			}
		}
	}

	renderThread.join();
	updateThread.join();
	window.close();
}

void Ameisenfutter::Ameisenfutter::createWindow(bool fullscreen)
{
	std::unique_lock<std::mutex> lck{ glMutex };
	window.setActive(true);
	mapViewer.cleanupGLStuff(window);

	sf::String title = "Ameisenfutter";
	sf::ContextSettings settings{ 0,0,8,4,3 };
	if (fullscreen)
		window.create(sf::VideoMode::getFullscreenModes()[0], title, sf::Style::Fullscreen, settings);
	else
		window.create(sf::VideoMode{ 800, 600 }, title, sf::Style::Default, settings);
	window.setVerticalSyncEnabled(false);
	window.setActive(true);
	glewExperimental = GL_TRUE;
	glewInit();

	mapViewer.createGLStuff(window);
	window.setActive(false);
}

void Ameisenfutter::Ameisenfutter::recalculateView()
{
	sf::Vector2f vec{ window.getSize() };
	view.setSize(sf::Vector2f{ vec.x / vec.y, 1.0 } / zoomFactor);
	view.setCenter(cameraPosition);
}


void Ameisenfutter::Ameisenfutter::handleMouseMoveEvent(const sf::Event & event)
{
	sf::Vector2i newMousePosition = sf::Mouse::getPosition(window);
	if (sf::Mouse::isButtonPressed(sf::Mouse::Button::Left))
	{
		cameraPosition += sf::Vector2f{ lastMousePosition - newMousePosition } *(1.0F / window.getSize().y) * (1.0F / zoomFactor);
	}
	lastMousePosition = newMousePosition;
}

void Ameisenfutter::Ameisenfutter::handleMouseScrollEvent(const sf::Event & event)
{
	targetZoomFactor *= std::pow(scrollSpeed, (float) event.mouseWheelScroll.delta);
}

void Ameisenfutter::Ameisenfutter::handleKeyPressEvent(const sf::Event & event)
{
	using sf::Keyboard;
	switch (event.key.code)
	{
	case Keyboard::Key::Escape:
		isRunning = false;
		break;
	case Keyboard::Key::F11:
		createWindow(isFullscreen = !isFullscreen);
		break;
	}
}
