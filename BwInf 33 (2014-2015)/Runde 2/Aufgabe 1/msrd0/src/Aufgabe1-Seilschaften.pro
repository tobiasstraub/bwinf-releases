#-------------------------------------------------
#
# Project created by QtCreator 2015-01-11T18:30:18
#
#-------------------------------------------------

# brauche nur core Bibliothek von Qt
QT       += core
QT       -= gui

# der Code benutzt C++11 features
QMAKE_CXXFLAGS += -std=c++11

# bei release den code komplett optimieren
QMAKE_CXXFLAGS_RELEASE -= -O1
QMAKE_CXXFLAGS_RELEASE -= -O2
QMAKE_CXXFLAGS_RELEASE *= -O3
QMAKE_LFLAGS_RELEASE   -= -O1
QMAKE_LFLAGS_RELEASE   -= -Wl,-O1
QMAKE_LFLAGS_RELEASE   -= -O2
QMAKE_LFLAGS_RELEASE   -= -Wl,-O2
QMAKE_LFLAGS_RELEASE   *= -O3
QMAKE_LFLAGS_RELEASE   *= -Wl,-O3

# Commandozeilen-Anwendung
TEMPLATE = app
TARGET = Aufgabe1-Seilschaften
CONFIG += console
CONFIG -= app_bundle

# Ausgabeverzeichnis übersichtlicher gestalten
OBJECTS_DIR = obj/
MOC_DIR = gen/moc/
UI_DIR = gen/ui/
RCC_DIR = gen/rc/

# Q_ASSERT soll immer ausgeführt werden
DEFINES += QT_FORCE_ASSERTS

# die Header-Dateien sind im include/ Verzeichnis und dessen Unterverzeichnis util/
INCLUDEPATH += include/ include/util/

# die Dateien, die zu diesem Projekt gehören

SOURCES += \
    src/main.cpp \
    src/inputdataparser.cpp \
    src/seilschaftensolver.cpp \
    src/move.cpp

HEADERS += \
    include/inputdata.h \
    include/inputdataparser.h \
    include/seilschaftensolver.h \
    include/move.h \
    include/solution.h \
    include/util/filteredqueue.h

RESOURCES += \
    res/examples.qrc

OTHER_FILES += \
    res/seilschaften0.txt \
    res/seilschaften1.txt \
    res/seilschaften2.txt \
    res/seilschaften3.txt \
    res/seilschaften4.txt \
    res/seilschaften5.txt \
    res/seilschaften6.txt
