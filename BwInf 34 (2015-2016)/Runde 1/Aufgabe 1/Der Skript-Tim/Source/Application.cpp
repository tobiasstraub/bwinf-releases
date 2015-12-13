#include <string>
#include <vector>
#include <iostream>
#include <cstdlib>
#include <fstream>

#include "./../inc/Application.h"

auto Application::main(const std::vector<std::string>& arguments) -> int
{

	// Standard-Werte für Flags setzen
	Application::_justOneWay = false;

	std::cout << "\n +===========================================+";
	std::cout << "\n | Kassiopeia v.2                            |";
	std::cout << "\n +-------------------------------------------+";
	std::cout << "\n | Tim Hollmann                              |";
	std::cout << "\n | @ 34.Bundeswettbewerb Informatik 2015/'16 |";
	std::cout << "\n +===========================================+\n\n";

	// Übergebene Kommandozeilenparameter auswerten
	for (unsigned int i = 0; i < arguments.size(); ++i) {

		if (i == 0 && (arguments[i] == "-h" || arguments[i] == "--help" || arguments[i] == "-?"))
		{
			// Hilfe-Ausgabe
			std::cout << "Hilfe-Ausgabe: Kommandizeilenargumente\n";
			std::cout << "\n-f --file <Dateiname>\tLiest die Datendatei ein. Format von bundeswettbewerb-informatik.de";
			std::cout << "\n-o --justOneSolution\tGibt nur eine mögliche Lösung aus, für den Fall, dass zu viele Lösungen entstehen.";

			return EXIT_SUCCESS;
		}

		if (arguments[i] == "-f" || arguments[i] == "--file")
		{
			// Übergeben des Dateinamens
			if (i + 1 < arguments.size()) {
				_filename = arguments[i + 1];
				i++;
			}
			else {
				// Fehler - Kein Dateiname übergeben
				std::cout << "Fehler - Kein Dateiname übergeben.";
				return EXIT_FAILURE;
			}
		}

		if (arguments[i] == "-o" || arguments[i] == "--justOneSolution") { _justOneWay = true; }

	}

	if (_filename.size() == 0)
	{
		std::cout << "Fehler - Keine Datendatei angegeben (-f <Datei>). --help für mehr Informationen.";
		return EXIT_FAILURE;
	}

	if (!Application::loadFromFile()) return EXIT_FAILURE;
	
	std::cout << "\nGeladenes Spielfeld ( " << field.size() << " x " << field[0].size() << "): \n\n";

	for (auto zeile : Application::field) {
		for (auto spalte : zeile) {
			std::cout << ((spalte) ? " " : "#");
		}
		std::cout << std::endl;
	}

	std::cout << "\nStartposition: (" << startX + 1 << " | " << startY + 1 << ")";

	Application::Coord startPosition;
	startPosition.x = startX;
	startPosition.y = startY;
	startPosition.action = '+';

	std::cout << "\nAnzahl der Weißen Felder: " << anzahlWeisseFelder;

	std::cout << "\n\nStarte Ermittlung der möglichen Wege...";

	std::vector<Application::Coord> path;
	path.push_back(startPosition);

	tiefensuche(path, 1);

	// Endzustände ausgeben

	std::cout << "\n\nErmittelte Wege: (" << endzustaende.size() << ")\n";

	if (endzustaende.size() != 0) {
		int counter = 0;
		for (auto zust : endzustaende) {
			std::cout << "\n" << ++counter << "\t: ";
			for (auto p : zust) {
				std::cout << p.action;
			}
		}
	}
	else {
		std::cout << "\n - Kein Weg gefunden. - ";
		std::cout << "\n Kassiopeia stirbt! :(";
	}

	return EXIT_SUCCESS;
}

auto Application::loadFromFile(void) -> bool // Laden des Spielfeldes aus einer Datei
{

	std::fstream datei(_filename.c_str(), std::ios::in);

	if (!datei.good())
	{
		std::cout << "Fehler beim Lesen aus Datei " << _filename;
		return false;
	}

	std::vector<std::string> file;

	std::string zeile;
	while (std::getline(datei, zeile, '\n'))
		file.push_back(zeile);

	datei.close();
	std::vector<std::vector<bool> > fieldTemp;

	bool foundStartPosition = false;
	int weiss = 0;

	for (int zeile = 1; zeile < file.size(); ++zeile) // erste Zeile nicht mitnehmen (Maße)
	{ 
		std::vector<bool> z;

		for (int spalte = 0; spalte < file[zeile].length(); ++spalte)
		{

			// In Bool-Entsprechung überführen
			if (file[zeile].substr(spalte, 1) == "#") {
				z.push_back(false);
			}
			else if (file[zeile].substr(spalte, 1) == " ") {
				z.push_back(true);
				weiss++;
			}
			else if (file[zeile].substr(spalte, 1) == "K") {
				foundStartPosition = true;
				startY = zeile - 1;
				startX = spalte;
				z.push_back(true);
				weiss++;
			}
			else {
				std::cout << "\n\nFehler - Undefiniertes Zeichen '" << file[zeile].substr(spalte, 1) << "' in Zeile " << zeile + 1 << " Zeichen " << spalte + 1 <<" gefunden.";
				return false;
			}

		}
		fieldTemp.push_back(z);
	}

	if (!foundStartPosition)
	{
		std::cout << "\nFehler - Keine Startposition gefunden.";
		return false;
	}

	Application::field = fieldTemp;
	Application::anzahlWeisseFelder = weiss;
	return true;
}

auto Application::warSchonDa(std::vector<Application::Coord> path, Application::Coord f) -> bool
{
	for (auto p : path)
		if (p.x == f.x && p.y == f.y) { return true; }
	return false;
}

auto Application::goTo(Application::Coord pos, std::vector<Coord> path, const int deep) -> bool
{
	std::vector<Application::Coord> t = path;
	t.push_back(pos);
	return tiefensuche(t, deep + 1);
}

auto Application::tiefensuche(std::vector<Coord> path, const int deep) -> bool
{

	if (deep == anzahlWeisseFelder)
	{
		endzustaende.push_back(path);
		return true;
	}

	Coord currentField = path.back();

	// nach oben
	if (currentField.y > 0 && field[currentField.y-1][currentField.x])
	{
		Coord temp = currentField;
		temp.y--;
		temp.action = 'N';

		if (!warSchonDa(path, temp))
			if (goTo(temp, path, deep) && _justOneWay) return true;
	}

	// nach unten
	if (currentField.y + 1 < field.size() && field[currentField.y + 1][currentField.x])
	{
		Coord temp = currentField;
		temp.y++;
		temp.action = 'S';

		if (!warSchonDa(path, temp))
			if (goTo(temp, path, deep) && _justOneWay) return true;
	}

	// nach links
	if (currentField.x > 0 && field[currentField.y][currentField.x - 1])
	{
		Coord temp = currentField;
		temp.x--;
		temp.action = 'W';

		if (!warSchonDa(path, temp))
			if (goTo(temp, path, deep) && _justOneWay) return true;
	}

	// nach rechts
	if (currentField.x + 1 < field[0].size() && field[currentField.y][currentField.x + 1])
	{
		Coord temp = currentField;
		temp.x++;
		temp.action = 'O';

		if (!warSchonDa(path, temp))
			if (goTo(temp, path, deep) && _justOneWay) return true;
	}

	return false;

}