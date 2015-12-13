#include <iostream>
#include <vector>
#include <map>
#include <string>
#include <cstdlib>
#include <fstream>
#include <sstream>

#include "./../inc/Application.hpp"

auto Application::main(const std::vector<std::string>& arguments) -> int
{

	std::cout << "\n +===========================================+";
	std::cout << "\n | Flaschenzug - Aufgabe 3                   |";
	std::cout << "\n +-------------------------------------------+";
	std::cout << "\n | Tim Hollmann (0003)                       |";
	std::cout << "\n | @ 34.Bundeswettbewerb Informatik 2015/'16 |";
	std::cout << "\n +===========================================+\n\n";

	// Kommandozeilenargumente auswerten
	for (int i = 0; i < arguments.size(); ++i)
	{

		if (arguments[i] == "-h" || arguments[i] == "--help" || arguments[i] == "-?")
		{
			// Hilfe-Ausgabe
			std::cout << "Hilfe-Ausgabe: Kommandizeilenargumente\n";
			std::cout << "\n-f --file <Dateiname>\tLiest die Datendatei ein. Format von bundeswettbewerb-informatik.de";
			return EXIT_SUCCESS;
		}
		else if (arguments[i] == "-f" || arguments[i] == "--file")
		{
			// Dateiname übergeben
			if (i + 1 <= arguments.size() - 1)
			{
				_filename = arguments[i + 1];
				i++;
			}
			else { // Dateiname nicht in übergeben
				std::cout << "Fehler - Trotz Kommandozeilenparameter Dateiname nicht übergeben. Ignoriere. ";
			}
		}
		else {
			// Unbekanntes Kommandozeilenargument
			std::cout << "Warnung - Unbekannter Kommandozeilenparameter '" << arguments[i] << "'. Ignoriere. ";
		}

	}

	// Dateiname übergeben?
	if (_filename.size() == 0)
	{
		// Keine Datei übergeben
		std::cout << " \nFehler - Keine Datendatei übergeben. Gehe zu manueller Dateneingabe über.";
		getFromUser();
	}
	else if (!loadFromFile())
	{
		// Fehler beim Laden / lesen aus der Datei
		std::cout << "\nFehler beim Öffnen/Auslesen der Datendatei. Gehe zu manueller Dateneingabe über.";
		getFromUser();
	}

	std::cout << "\n\n-----------------------------------------\nÜbersicht: \nFlaschen: \t\t" << _N << "\n\nBehälter:";
	
	for (int i = 0; i < _behaelter.size(); i++) {
		std::cout << "\n[" << i + 1 << "]\t:" << _behaelter[i];
	}

	std::cout << "\n\nBerechne Kombinationen. Bitte warten... ";

	int summe_kapazitaeten = 0;
	for (int i = 0; i < _behaelter.size(); i++) { summe_kapazitaeten += _behaelter[i]; }

	bigInt s = anzahl(_behaelter, summe_kapazitaeten, _N);

	std::cout << "fertig.\n\nKombinationen: " << s << "\n\nDrücken Sie [ENTER] zum Beenden ... ";

	std::cin.get();

	return EXIT_SUCCESS;

}

auto Application::loadFromFile(void) -> bool
{

	std::fstream datei(_filename.c_str(), std::ios::in);

	if (!datei.good()) {
		std::cout << "\nFehler beim Öffnen der Datei " << _filename << "\nStellen Sie sicher, dass diese am angegebenen Speicherort existiert.";
		return false;
	}

	std::vector<std::string> file;

	std::string zeile;
	while (std::getline(datei, zeile, '\n'))
		file.push_back(zeile);

	datei.close();

	try {
		int n = std::stoi(file[0]);

		int k = std::stoi(file[1]);

		std::string behaelter_string = file[2];

		std::stringstream data(behaelter_string);

		std::vector<int> b;

		std::string line;
		while (std::getline(data, line, ' ')) {
			b.push_back(std::stoi(line));
		}

		if (b.size() != k) {
			std::cout << "Fehler - Die Anzahl der Behälter entspricht nicht der angegebenen Anzahl in Zeile 2. Ignoriere Zeile 2 (" << k << ") und benutze " << b.size() << " Behälter.";
		}

		_N = n;

		for (int i = 0; i < b.size(); i++) {
			_behaelter.push_back(b[i]);
		}

	}
	catch(...){
		std::cout << "\nWarnung - Fehler beim Einlesen der Datendatei aufgetreten. Stellen Sie sicher, dass deren Syntax der von www.bundeswettbwerb-informatik.de entspricht. Gehe zu manueller Dateneingabe über.";
		return false;
	}

	return true;
}

auto Application::getFromUser(void) -> void
{

	// Abfrage des Benutzers
	std::cout << "\n\n===================================\nManuelle Eingabe:\n\n";
	
	int n = 0;
	do {
		std::cout << "Flaschenanzahl N:";
		std::cin >> n;
	} while (n <= 0);

	_N = n;

	std::cout << "\n";

	int k = 0;
	do {
		std::cout << "Anzahl der Behälter K:";
		std::cin >> k;
	} while (k <= 0);

	std::cout << "\n\nEingabe der Behälterkapazitäten:";
	
	for (int i = 1; i <= k; ++i) 
	{
		std::cout << "\n";
		int t = 0;
		do {
			std::cout << "Behälter " << i << " [ von " << k << "]\t:";
			std::cin >> t;
		} while (t <= 0);
		_behaelter.push_back(t);
	}

}

auto Application::anzahl(const std::vector<behaelter>& z, uint z_summe, uint n) -> bigInt
{

	// Suche im Memo
	std::tuple<std::vector<behaelter>, int> t(z, n);

	if (memo.find(t) != memo.end())
		return memo.at(t);

	// Nur ein Behälter
	if (z.size() <= 1)
	{
		int i = ((z[0] >= n) ? 1 : 0);
		memo[t] = i;
		return i;
	}

	// Bei mehreren Behältern den ersten herausnehmen (und den rest in temporärem Vektor speichern) 
	std::vector<behaelter> temp_b;
	for (int i = 1; i < z.size(); i++) { temp_b.push_back(z[i]); }

	// Neue Kapazitätensumme
	int temp_zustand_summe = z_summe - z[0];

	bigInt s = 0;
	// Ersten Behälter durchiterieren und rekursiv für übrige Behälter aufrufen
	for (int i = 0; i <= z[0] && n - i >= 0; i++) {
		if (temp_zustand_summe < n - i) continue;
		s += anzahl(temp_b, temp_zustand_summe, n - i);
	}

	memo[t] = s; // Ergebnis speichern

	return s;
}