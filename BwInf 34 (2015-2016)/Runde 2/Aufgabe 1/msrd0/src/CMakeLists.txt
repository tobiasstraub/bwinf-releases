cmake_minimum_required(VERSION 3.0.2)
project(Geburtstagskuchen)

set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_AUTOMOC ON)

find_package(Qt5Core REQUIRED)
include_directories(${Qt5Core_INCLUDE_DIRS})
add_definitions(${Qt5Core_DEFINITIONS})

find_package(Qt5Gui REQUIRED)
include_directories(${Qt5Gui_INCLUDE_DIRS})
add_definitions(${Qt5Gui_DEFINITIONS})

find_package(Qt5Widgets REQUIRED)
include_directories(${Qt5Widgets_INCLUDE_DIRS})
add_definitions(${Qt5Widgets_DEFINITIONS})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

include_directories(include)
QT5_WRAP_UI(qt_UI_H form/mainwindow.ui)
add_executable(cake-qt
		src/algo.cpp src/editwidget.cpp src/main.cpp src/mainwindow.cpp
		include/algo.h include/editwidget.h include/mainwindow.h
		${qt_UI_H}
	)
target_link_libraries(cake-qt Qt5::Core Qt5::Gui Qt5::Widgets)

add_executable(cake-cli
		src/algo.cpp src/cli.cpp
		include/algo.h
	)
target_link_libraries(cake-cli Qt5::Core Qt5::Gui Qt5::Widgets)
