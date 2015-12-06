#pragma once

#include "Map.h"

#include <GL/glew.h>

#include <SFML/Graphics.hpp>
#include <SFML/OpenGL.hpp>

#include <array>
#include <vector>
#include <future>

namespace Ameisenfutter
{
	class Ameisenfutter;
	class MapViewer : public sf::Drawable
	{
	public:
		MapViewer(Map& map);
		~MapViewer();

		//Overriden from sf::Drawable
		void draw(sf::RenderTarget& target, sf::RenderStates states) const;

		void createGLStuff(sf::RenderTarget& target);
		void cleanupGLStuff(sf::RenderTarget& target);

		void setTileScreenSize(float factor);

	private:
		int width, height;
		Map& map;

		struct Vertex
		{
			std::array<GLfloat, 2> position;
			std::array<GLfloat, 2> middle;
			std::array<GLfloat, 4> color;
			GLuint mapDataID;
		};
		struct GLMapData
		{
			GLuint flags;
		};

		void updateInternalGLMapVertices() const;
		void updateInternalGLMapData() const;
		void retrieveInternalGLMapData() const;

		void internalMapDataUpdateThread() const;

		mutable std::future<void> copyThreadFuture;

		mutable std::vector<GLMapData> internalMapDataVertices;
		mutable GLMapData* internalMapDataVerticesPointer;

		mutable float tileScreenSize;
		mutable GLuint vao;
		mutable GLuint vbo;
		mutable GLuint ssbo;
		mutable sf::Shader shader;
		mutable sf::Texture texture;
		mutable sf::VertexArray internalMapArray;
		mutable bool wasGLInitialized = false;
	};
}

