
/*
======================================
| Aufgabe 1                          |
| Der Script-Tim @ 33.BwInf 2014/'15 |
|                                    |
======================================
*/

/* Benötigte Header einbinden */
#include <iostream>
#include <string>
#include <vector>

using namespace std;

//Klasse Behaelter; erleichtert die verarbeitung der Behälter
class Behaelter{
public:
	unsigned int besitzer;
	unsigned int kapazitaet;
	unsigned int inhalt;


	bool voll(void){ //voll?
		return (inhalt >= kapazitaet) ? true: false;
	}

	bool leer(void){ //leer?
		return (inhalt <= 0) ? true : false;
	}

	unsigned int frei(void){ //Freier Platz
		return (kapazitaet - inhalt);
	}

	Behaelter(unsigned int b, unsigned int k, unsigned int i){
		besitzer = b;
		kapazitaet = k;
		inhalt = i;
	}
};

//Pfad-Struktur
struct StructPfad{
	unsigned int source;
	unsigned int target;
};

//Struktur eines Zustandes
struct StructZustaende{
	vector<StructPfad> pfad;
	vector<Behaelter> behaelter;
};

vector<StructZustaende> zustaende;
vector<vector<StructPfad>> loesungen; //Hier werden die Lösungen gesammelt
unsigned int SummeInhalt = 0; //Summe des Inhaltes insgesamt

/* In dieser Funktion wird die eigentliche Umfüllung vorgenommen: von behaelter[sourceID] => behaelter[targetID] */
vector<Behaelter> umfuellen(vector<Behaelter> behaelter, unsigned int sourceID, unsigned int targetID){

	//Umfüllen
	if (behaelter[sourceID].inhalt == behaelter[targetID].frei()){ //Passt genau
		behaelter[sourceID].inhalt = 0;
		behaelter[targetID].inhalt = behaelter[targetID].kapazitaet;
	}else if (behaelter[sourceID].inhalt > behaelter[targetID].frei()){ //Zu viel Inhalt in source für target
		behaelter[sourceID].inhalt -= behaelter[targetID].frei();
		behaelter[targetID].inhalt = behaelter[targetID].kapazitaet;
	}else{ //Zu wenig Inhalt in source für target
		behaelter[targetID].inhalt += behaelter[sourceID].inhalt;
		behaelter[sourceID].inhalt = 0;
	}
	return behaelter;
}

/* Ermittelt, ob ein Zustand 'zustand' bereits vorhanden ist */
bool zustandVorhanden(vector<Behaelter> zustand){
	
	for(unsigned int x = 0; x < zustaende.size(); x++){
		bool gleich = true;

		for(unsigned int y = 0; y < zustand.size(); y++){
			if (zustaende[x].behaelter[y].inhalt != zustand[y].inhalt) gleich = false;
		}

		if (gleich == true) return true;
	}
	return false;
}

/* Diese Funktion ermittelt zunächst, ob ein Zustand 'zustand' bereits vorhanden (in zustaende) inst.
Falls nein, wird der Zustand gesetzt.
Falls ja, wird die Anzahl der benötigten Umfüllungen verglichen; ist die aktuelle geringer, überschreibt sie die alte */
bool setZustand(vector<Behaelter> zustand, vector<StructPfad> pfad){
	//Zustand vorhanden?
	
	if (zustandVorhanden(zustand)){

		unsigned int id = 0;
		for(unsigned int x = 0; x < zustaende.size(); x++){
			bool gleich = true;
			for (unsigned int y = 0; y < zustaende[x].behaelter.size(); y++){
				if (zustand[y].inhalt != zustaende[x].behaelter[y].inhalt ) gleich = false;
			}
			if (gleich == true) id = x;
		}

		if (zustaende[id].pfad.size() > pfad.size()){
			//Zustand überschreiben
			StructZustaende temp;
			temp.pfad = pfad;
			temp.behaelter = zustand;
			zustaende[id] = temp;
		}else{
			return false;
		}
		
	}else{
		//Zustand belegen
		StructZustaende temp;
		temp.pfad = pfad;
		temp.behaelter = zustand;
		zustaende.push_back(temp);
		return true;
	}
}

/* Überprüft, ob der Inhalt gleich auf die Personen verteilt ist */
bool checkGleichVerteilt(vector<Behaelter> behaelter){
	int BesitzPerson[2] = {0, 0};
	for (unsigned int x = 0; x < behaelter.size(); x++){
		if (behaelter[x].besitzer == 1){
			BesitzPerson[0] += behaelter[x].inhalt;
		}else{
			BesitzPerson[1] += behaelter[x].inhalt;
		}
	}
	return (BesitzPerson[0] == (SummeInhalt/2) && BesitzPerson[0] == BesitzPerson[1]) ? true : false;
}

/* Schleifen- Funktion */
void loop (vector<Behaelter> behaelter, vector<StructPfad> pfad ){

	//Gleich verteilt?
	if (checkGleichVerteilt(behaelter)){
		loesungen.push_back (pfad);//Lösungen hinzufügen
		return; //und Ende
	}
	//Sonst wird die Schleife weiter vertieft
	for(unsigned int sourceID = 0; sourceID < behaelter.size(); sourceID++){
		for(unsigned int targetID = 0; targetID < behaelter.size(); targetID++){
			if (sourceID != targetID && !behaelter[sourceID].leer() && !behaelter[targetID].voll()){
				//Umfüllung simulieren
				vector<Behaelter> tempBehaelter = umfuellen(behaelter, sourceID, targetID);
				//Pfad vervollständigen
				vector<StructPfad> tempPfad = pfad;
				StructPfad temp;
				temp.source = sourceID;
				temp.target = targetID;
				tempPfad.push_back (temp);
				//Wenn der Zustand der nächsten Umfüllung noch nicht erreicht war oder den alten überschrieben hat, wird die Schleife fortgesetzt
				if (setZustand(tempBehaelter, tempPfad)){
					loop(tempBehaelter, tempPfad);
					
				}

			}
		}
	}

	return;
}

int main(void){ //PROGRAMMSTART

	cout << "\n";
	cout << "**************************************************\n";
	cout << "* Aufgabe 1                                      *\n";
	cout << "* Der Script-Tim @ 33.BwInf 2014/'15             *\n";
	cout << "**************************************************\n";
	
	/* Eingabe der Behälter durch den Benutzer */
	vector<Behaelter> behaelter;
	char c = 'n';
	do{
		//Wenn c != '', eingabe
		if (c == 'j'){
			unsigned int besitzer = 0, kapazitaet = 0, inhalt = 0;
			cout << "\n\n***** Neuen Behaelter erstellen *****";
			do{
				cout << "\nBesitzer des neuen Behaelters: [0/1]:";
				cin >> besitzer;
			}while(besitzer < 0 || besitzer > 1);
			
			do{
				cout << "\nKapazitaet des Behaelters [Ma(ss)e Wein]:";
				cin >> kapazitaet;
			}while(kapazitaet < 0);

			do{
				cout << "\nWein im Behaelter (Inhalt):";
				cin >> inhalt;
			}while(inhalt < 0 || inhalt > kapazitaet);

			behaelter.push_back( * new Behaelter( besitzer, kapazitaet, inhalt));
		}
		//Liste der Behälter anzeigen
		cout << "\n====== Behaelter: " << behaelter.size() << " ========";
		for (unsigned int x = 0; x < behaelter.size(); x++){
			cout << "\n [" << x << "] ( Besitzer: " << behaelter[x].besitzer << ", Kapazitaet: " << behaelter[x].kapazitaet << ", Inhalt: " << behaelter[x].inhalt << ")";;
		}

		cout << "\n\nEinen neuen Behaelter hinzufuegen?\n";
		cout << " +----- Optionen: -----------+\n";
		cout << " | j: Behaelter hinzufuegen  |\n";
		cout << " | n: Fortfahren             |\n";
		cout << " +---------------------------+\n";
		cin >> c;

	}while(c != 'n');

	/* Summe der Inhalte insgesamt ermitteln */
	for (unsigned int x = 0; x < behaelter.size(); x++){ SummeInhalt += behaelter[x].inhalt; }

	cout << "\n\n === Vorgang gestartet, bitte warten... ===\n";
	/* Schleifen initiieren */
	for (unsigned int sourceID = 0; sourceID < behaelter.size(); sourceID++){
		for(unsigned int targetID = 0; targetID < behaelter.size(); targetID++){
			if (sourceID != targetID && !behaelter[sourceID].leer() && !behaelter[targetID].voll()){ //Umfüllung formal/logisch korrekt?
				//Umfüllung simulieren
				vector<Behaelter> tempBehaelter = umfuellen(behaelter, sourceID, targetID);
				//Pfad erweitern
				vector<StructPfad> tempPfad;
				StructPfad temp;
				temp.source = sourceID;
				temp.target = targetID;
				tempPfad.push_back (temp);

				if (setZustand (tempBehaelter, tempPfad)){ //Wenn Zustand nicht gesetzt oder überschrieben, Schleife starten
					loop(tempBehaelter, tempPfad);
				}
			}
		}
		//cout << "\n" << 100/behaelter.size()*sourceID << "%";
	}

	/* Lösungsweg(e) ausgeben */
	cout << "\n\n=== Loesungswege ===";
	if (loesungen.size() ==  0){
		cout << "\nEs wurde kein Loesungsweg gefunden.";
	}else{
		cout << "\nGefundene Loesungswege: " << loesungen.size();
		//Kürzesten Lösungsweg ermitteln
		int kuerzester = 1000000;
		int kuerzesterID = 0;
		for (unsigned int x = 0; x < loesungen.size(); x++){
			if (loesungen[x].size() < kuerzester) { kuerzester = loesungen[x].size(); kuerzesterID = x; }
		}

		//Kürzesten Lösungsweg ausgeben
		cout << "\nKuerzester Loesungsweg: " << loesungen[kuerzesterID].size() << " Umfuellungen:";
		for (unsigned int x = 0; x < loesungen[kuerzesterID].size(); x++){
			cout << "\n[" << loesungen[kuerzesterID][x].source << "] => [" << loesungen[kuerzesterID][x].target << "]";
		}

		int eingabe = 0;
		cout << "\n\n==== Optionen =================";
		cout << "\n= 1: Alle Loesungswege anzeigen =";
		cout << "\n= Alles andere: Beenden        =";
		cout << "\n================================\n:";
		cin >> eingabe;

		if (eingabe == 1){
			//Alle Lösungswege ausgeben
			cout << "\n----------------- Alle Loesungswege:";
			for (unsigned int x = 0; x < loesungen.size(); x++){
				cout << "\n\nLoesungsweg Nr. " << x+1 << ": " << loesungen[x].size() << " Umfuellungen";
				for(unsigned int y = 0; y < loesungen[x].size(); y++){
					cout << "\n[" << loesungen[x][y].source << "] => [" << loesungen[x][y].target << "]";
				}
			}
		}else{
			return 0;
		}
	}

	cin.sync();
	cout << "\n\n<ENTER> zum Beenden:";
	cin.get();
	cout << "\n\n";

	return 0;
}
