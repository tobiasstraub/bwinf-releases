#-------------------------------------------------
#
# Project created by QtCreator 2016-02-18T19:08:48
#
#-------------------------------------------------

QT       += core gui
greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TEMPLATE = app
TARGET   = cake
CONFIG  += c++11

# fix for use with clang
clang {
	QMAKE_CXXFLAGS_RELEASE -= -fvar-tracking-assignments -Og
}

DEFINES += BUILD_USING_QMAKE

INCLUDEPATH += include/

SOURCES += \
    src/mainwindow.cpp \
    src/main.cpp \
    src/editwidget.cpp \
    src/algo.cpp \
    src/cli.cpp

HEADERS  += \
    include/mainwindow.h \
    include/editwidget.h \
    include/algo.h

FORMS    += \
    form/mainwindow.ui
