QT = core gui

TEMPLATE = app
TARGET   = yamyams
CONFIG  += console
CONFIG  += c++11
CONFIG  -= app_bundle

# fix for use with clang
clang {
	QMAKE_CXXFLAGS_RELEASE -= -fvar-tracking-assignments -Og
}

INCLUDEPATH += include/

SOURCES += \
    src/main.cpp \
    src/world.cpp \
    src/algo.cpp

HEADERS += \
    include/world.h \
    include/algo.h

RESOURCES += \
    img/img.qrc \
	examples/examples.qrc
