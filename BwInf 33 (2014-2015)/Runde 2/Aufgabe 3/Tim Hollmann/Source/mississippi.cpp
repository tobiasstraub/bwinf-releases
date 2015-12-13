/*
============================================
| Aufgabe 3 - Mississippi                  |
| Tim Hollmann @ 33.BwInf 2014/'15 2.Runde |
============================================
*/

#include <iostream>		// std::cout
#include <string>		// std::string
#include <vector>		// std::vector
#include <fstream>		// std::fstream
#include <ctime>		// std::clock()
#include <algorithm>	// std::sort()
#include <sstream>		// std::sstream_basic
#include <map>			// std::map

using namespace std;

// --- Globale Variablen ----

string line;
unsigned int length;

unsigned int l, k;
string filename;
char escapeSign = '$';

// ---------- Suffix-Klasse ---------- //
class suffix{
public:
	int relPointer; // relativeer Zeiger auf die Stelle, an der er sich am Mutter-Suffix andockt
	int absPointer; // absoluter Zeiger auf die Stelle am original-String

	vector<suffix> subSuffixes; // Stack mit Sub-Suffixen

	suffix(int relStart, int absStart){
		this->relPointer = relStart;
		this->absPointer = absStart;
	}

	unsigned int getFrequency(void){
		unsigned int counter = 1;
		for (auto &suffixes : this->subSuffixes){
			counter += suffixes.getFrequency();
		}
		return counter;
	}

	bool takeUp(int startPointer){ 
		for (unsigned int deep = 1; deep < (length - startPointer); deep++){
			if (line[this->absPointer + deep] != line[startPointer + deep]){
				bool found = false;
				for (unsigned int i = 0; i < this->subSuffixes.size(); i++){
					if (this->subSuffixes[i].relPointer == deep && line[this->subSuffixes[i].absPointer] == line[startPointer + deep]){
						this->subSuffixes[i].takeUp(startPointer + deep);
						found = true;
						break;
					}
				}
				if (!found){
					this->subSuffixes.push_back(*new suffix(deep, startPointer + deep));
				}
				break;
			}
		}
		return true;
	}

};

// Lösungs-Klasse
class loesung{
public:
	string str;
	unsigned int haeufigkeit;
	loesung(string a, int b){
		this->str = a;
		this->haeufigkeit = b;
	}
	loesung(void){}
};

vector<loesung> loesungen;

// Vergleichsfunktion, um die Lösungen nach Häufigkeit und Länge zu sortieren
bool sortLoesungen(const loesung& x, const loesung& y){
	if (x.haeufigkeit > y.haeufigkeit) return false;
	if (x.haeufigkeit < y.haeufigkeit) return true;
	if (x.str.length() < y.str.length()) return false;
	if (x.str.length() > y.str.length()) return true;
	return false;
}

// Process: Suffix-Baum nach l und k auswerten
int process(suffix suf, string suffixSoFar){

	if (k <= 1){
		//Loesung gefunden: Aktueller String: suf.absPointer -> Stirng-Ende
		if (suf.absPointer != length){ //Pointer darf nicht auf letzte Stelle zeigen (-> $)
			string currentSuffix = suffixSoFar + line.substr(suf.absPointer, length - suf.absPointer);
			if (currentSuffix.length() >= l){
				loesungen.push_back(*new loesung(currentSuffix, 1));
			}
		}
	}

	for (auto &suffix : suf.subSuffixes){

		// Suffix: suffixSoFar + line.substr(suf.absPointer, suffix.relPointer)
		// Suffix zwischen suf und suffix

		string currentSuffix = suffixSoFar + line.substr(suf.absPointer, suffix.relPointer);

		// Stimmt die Längendifferenz + suffixSoFar.size() >= l?
		if (currentSuffix.length() >= l){
			//Häufigkeit ermitteln

			unsigned int counter = 1;

			// Häufigkeiten gleicher und darunterliegender Suffixe addieren
			for (auto &s : suf.subSuffixes){
				if (s.relPointer >= suffix.relPointer){
					counter += s.getFrequency();
				}
			}

			if (counter >= k){ loesungen.push_back(*new loesung(currentSuffix, counter)); }

		}
		process(suffix, suffixSoFar + line.substr(suf.absPointer, suffix.relPointer));

	}

	return 1;

}

/* Funktion für Dateneingabe */
bool input(int argc, char *argv[]){

	if (argc == 4){

		stringstream sstream;
		sstream.clear();

		/* Datei öffnen und auslesen */

		sstream << argv[1];

		filename = "";
		sstream >> filename;

		fstream fin(filename);
		getline(fin, line);
		fin.close();

		/* l und k*/

		sstream.clear();
		sstream << argv[2];
		sstream >> l;

		sstream.clear();
		sstream << argv[3];
		sstream >> k;

		/* Überprüfung */

		if (line.size() == 0 || l == 0 || k == 0){ 
			cout << "\nFehler - Fehlerhafte Dateneingabe. (" << argc << " Parameter).\nExistiert die Quell-Datei? k und l dürfen nicht 0 sein. \nMuster: mississippi.exe <DNS-Quelldatei> <l> <k>\n\n"; 
			return false; 
		}

		return true;

	}else{
		cout << "\nFehler - Nicht ausreichende Dateieingabe (" << argc << " Argumente). \nMuster: mississippi.exe <DNS-Quelldatei> <l> <k>\n\n";
		return false;
	}

}

/* Hauptfunktion */
int main(int argc, char *argv[]){

	cout << "\n+-----------------------------------------------+ ";
	cout << "\n| Mississippi v3 - DNS-Sequenzierungs-Programm  | ";
	cout << "\n| Tim Hollmann @ 33.BwInf 2.Runde 3.Aufgabe     | ";
	cout << "\n+-----------------------------------------------+ \n";

	// Eingabe
	if (!input(argc, argv)) return 0;

	//Terminalwort anfügen
	line += escapeSign;

	//String-Länge extra speichern; ist effizienter
	length = line.length();

	/* Übersicht über eingelesene Daten */
	cout << "\n" << "Eingelesene Zeichenketten-L[ae]nge:" << length-1 << " (" << filename << ")\n";
	cout << "\n" << "l:\t" << l;
	cout << "\n" << "k:\t" << k;
	
	// ========== Verarbeitung 1 : Suffix-Baum erstellen ========== //

	vector<suffix> root; //Suffixbaum-Wurzel "pflanzen"

	cout << "\n\n=== Verarbeitungsschritt 1 : Erstellen des Suffix-Baumes ===\nBitte warten...";

	//Zeit nehmen: vorher
	clock_t start = clock();

	// Suffixe von Anfang an abarbeiten
	for (unsigned int i = 0; i < length; i++){
		//Neues Suffix: i bis String-Ende (line[length-1])

		//Passenden Root-Knoten ermitteln
		bool found = false; //Knoten gefunden?
		for (unsigned int n = 0; n < root.size(); n++){
			//Gleicher Anfangsbuchstabe?
			if (line[root[n].absPointer] == line[i]){
				root[n].takeUp(i);
				found = true;
				break;
			}
		}

		//Wenn Urknoten noch nicht existiert, hinzufügen, wenn nicht Escape-Zeichen
		if (!found && line[i] != escapeSign){
			root.push_back(*new suffix(0, i));
		}

	}

	//Zeit nehmen: nachher
	cout << "fertig.\nBen[oe]tigte Zeit: " << ((clock() - start) / (double) CLOCKS_PER_SEC) << " Sekunde(n).";

	// ========== Verarbeitung 2 : Suffix-Baum auswerten ========== //
	cout << "\n\n=== Verarbeitungsschritt 2 : Auswerten des erstellten Suffix-Baumes ===\nBitte warten...";

	//Zeit nehmen: vorher
	start = clock();

	for (auto &r : root) { process(r, ""); }

	cout << "fertig.\nBen[oe]tigte Zeit: " << ((clock() - start) / (double)CLOCKS_PER_SEC) << " Sekunde(n).";

	cout << "\n" << loesungen.size() << " vorl[ae]ufige L[oe]sungen gefunden.";

	// ========== Verarbeitung 3 : Lösungen filtern -> maximal ========== //
	
	cout << "\n\n=== Verarbeitungsschritt 3 : Filtern der L[oe]sungen ===\nBitte warten...";

	//Zeit nehmen: vorher
	start = clock();

	// Lösungen nach Häufigkeit und Länge sortieren
	sort(loesungen.begin(), loesungen.end(), sortLoesungen);

	vector<loesung> loesungenGefiltert;

	for (unsigned int i = 0; i < loesungen.size(); i++){

		unsigned int myFrequency = loesungen[i].haeufigkeit;

		//Range ermitteln
		unsigned int e; // End-Zeiger

		for (e = i + 1; e < loesungen.size(); e++){
			if (loesungen[e].haeufigkeit != myFrequency){ break; }
		}

		// Range vergleichen
		for (unsigned int a = i; a < e; a++){
			unsigned int myFreq = loesungen[a].haeufigkeit;
			string myStr = loesungen[a].str;

			if (myFreq != 0){
				for (unsigned int b = i; b < e; b++){
					if (a != b && loesungen[b].haeufigkeit != 0 && myFreq == loesungen[b].haeufigkeit){

						if (myStr.size() == loesungen[b].str.size()){
							if (myStr[0] == loesungen[b].str[0] && myStr == loesungen[b].str){
								loesungen[b].haeufigkeit = 0;
							}
						}
						else if (myStr.size() > loesungen[b].str.size()){
							if (myStr.find(loesungen[b].str) != string::npos){
								loesungen[b].haeufigkeit = 0;
							}
						}

					}
				}
			}
		}

		i = e-1; // Zur nächsten Range springen

	}

	for (auto &l : loesungen){
		if (l.haeufigkeit != 0) loesungenGefiltert.push_back(l);
	}

	cout << "fertig.\nBen[oe]tigte Zeit: " << ((clock() - start) / (double)CLOCKS_PER_SEC) << " Sekunde(n).";
	
	// Ausgabe der Lösungen
	cout << "\n\n\n=== L[oe]sungen (" << loesungenGefiltert.size() << ") :";
	cout << "\nNr.\tH[ae]ufigkeit\tL[ae]nge\tRepetition";
	cout << "\n---------------------------------------------------";
	
	for (unsigned int i = 0; i < loesungenGefiltert.size(); i++){

		stringstream sstream;
		sstream.clear();

		string number = "";
		sstream << i+1;
		sstream >> number;

		while (number.size() < 3){ number = "0" + number; }

		cout << "\n" << number << "\t" << loesungenGefiltert[i].haeufigkeit << "\t\t" << loesungenGefiltert[i].str.size() << "\t\t" << loesungenGefiltert[i].str;

	}

	return 0;
}