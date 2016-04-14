cmake_minimum_required(VERSION 3.0.2)
project(Torkelnde_Yamyams)

set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_AUTOMOC ON)

find_package(Qt5Core REQUIRED)
include_directories(${Qt5Core_INCLUDE_DIRS})
add_definitions(${Qt5Core_DEFINITIONS})

find_package(Qt5Gui REQUIRED)
include_directories(${Qt5Gui_INCLUDE_DIRS})
add_definitions(${Qt5Gui_DEFINITIONS})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

include_directories(include)
QT5_ADD_RESOURCES(qt_RCCS examples/examples.qrc img/img.qrc)
add_executable(yamyams
		src/algo.cpp src/main.cpp src/world.cpp
		include/algo.h include/world.h
		${qt_RCCS}
	)
target_link_libraries(yamyams Qt5::Core Qt5::Gui)
