
//////////////////////////////////////////////////////////////////////////////////
//Songtext-Writer                                                               //
//Zum 32.Bundeswettbewerb Informatik                                            //
//Junioraufgabe 1                                                               //
//                                                                              //
//Copyright (c) by Tim Hollmann,2013                                            //
//tim-hollmann@gmx.de                                                           //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

//Einbinden benötigter Header
#include <iostream> //Ein- und Ausgabe über die Konsole
#include <string> //String ist in diesem Programm sehr wichtig
#include <time.h> //Zeit(wird für den Zufallszahlen-generator benötigt)
#include <stdlib.h> //Zufallszahlen-Generator

//sonst müsste man immer statt string std::string schreiben
using namespace std;

 // Maximale Strophenanzahl pro Songtext
int max_strophenanzahl = 5;


//Funktionen definieren
int main(void);
string songwriter(void);
string strophe(int zeilenanzahl,int silbenzahl);
string zeile(int silbenzahl,string menge_konsonanten[2], string menge_vokale[2]);
string silbe(string menge_konsonanten[2], string menge_vokale[2]);

//Haupt-Funktion
int main(){
	setlocale(LC_ALL, "german");//Deutsche Umlaute in der Konsole ermöglichen(Ö,Ü und Ä sind Vokale vgl. http://de.wikipedia.org/wiki/Vokal#Vokalbuchstaben)

	while (true) {//Endlos-schleife, die verlassen wird, wenn der Anweder keinen neuen Sontext möchte

	cout << songwriter(); //Aufruf der songwriter-Funktion und Ausgabe auf der Konsole
	
	int eingabe;//Eingabevariable
	cin >> eingabe;//Eingabe einer Zahl

	if (eingabe != 1){//1= Neuer Songtext; alles Andere= Ende
		return 0;//Ende
	}

	}
	
}

string songwriter(void){
	

	srand( (unsigned) time(NULL) ) ; //Starten des Zufallszahlen-Generators mit Hilfe der Uhrzeit (deshalb wird time.h benötigt)

	string songtext= ""; //Songtext wird in dieser Variablen gesammelt
	

	//Kopf mit Überschirft und Copyright
	songtext += "=============================================================\n";
	songtext += "Songtext-Writer zum 32.BwInf\n";
	songtext += "Junioraufgabe 1\n";
	songtext += "(c) by Tim Hollmann,2013\n";//Bitte nicht verändern
	songtext += "=============================================================\n";


	int strophenanzahl  = rand()%max_strophenanzahl+2; // Zufällige Strophenanzahl

	int muster_zeilen = rand()%2 + 1; // Zufälliges Muster für die Zeilenzahlen

	int muster_silben = rand()%2 +1; //Zufälliges Muster für die Silbenzahlen

	//muster_zeilen:
	//1= immer 4 Zeilen pro Strophe
	//2= abwechselnd 4 und 5 Zeilen pro Strophe

	//muster_silben:
	//1= immer 5 Silbenwiederholungen
	//2= abwechselnd 5 und 7 Silbenwiederholungen


	for(int x = 1; x<= strophenanzahl; x++){ //Für jede Strophe
		
		//Das Muster der Silben-und Zeilenzahlen ist Strophenübergreifend, deshalb wird es in der songwriter-Funktion eine Ebebene über den Strophen definiert
		int zeilenzahl = 0;
		int silbenzahl = 0;


		//Zeilenzahl-Muster---------------------
		if (muster_zeilen == 1){//Muster 'immer 4'

			zeilenzahl = 4;

		}else{//Muster 'abwechselnd 4 und 5 Zeilen'

			if (x % 2 == 0){ //Durch 2 teilbar(gerade?)

				zeilenzahl = 4;

			}else{

				zeilenzahl = 5;

			}

		}
		//Silbenzahl-Muster------------------------

		if (muster_silben == 1){
			silbenzahl = 5;// Muster 'immer 5'

		}else{

			if (x % 2 == 0){//Muster 'abwechselnd 5 und 7'
				silbenzahl = 5;
			}else{
				silbenzahl = 7;
			}
		}

		//Strophe erzeugen und mit einigen Umbrüchen(zur optischen Trennung der einzelnen Strophen) zum strophentext hinzufügen
		songtext = songtext + "\n\n\n" +strophe(zeilenzahl,silbenzahl);
	}


	songtext += "\n\n\n=============================================================\nEingabe:(1=Neuer Songtext;alles Andere:Ende):"; //"Menu" am Ende

	return songtext; //Der fertige Songtext wird zurückgegeben.

}

string strophe( int zeilenanzahl, int silbenzahl){

	string strophentext = "";

	string Konsonanten[21]; //Vokalmenge definieren
	string Vokale[9]; //Konsonantenmenge definieren

	//Konsonantenmenge füllen
	//Siehe http://de.wikipedia.org/wiki/Konsonant#Konsonantenbuchstaben

	Konsonanten[0] = "B";
	Konsonanten[1] = "C";
	Konsonanten[2] = "D";
	Konsonanten[3] = "F";
	Konsonanten[4] = "G";
	Konsonanten[5] = "H";
	Konsonanten[6] = "J";
	Konsonanten[7] = "K";
	Konsonanten[8] = "L";
	Konsonanten[9] = "M";
	Konsonanten[10] = "N";
	Konsonanten[11] = "P";
	Konsonanten[12] = "Q";
	Konsonanten[13] = "R";
	Konsonanten[14] = "S";
	Konsonanten[15] = "ß";
	Konsonanten[16] = "T";
	Konsonanten[17] = "V";
	Konsonanten[18] = "W";
	Konsonanten[19] = "X";
	Konsonanten[20] = "Z";

	//Vokalemenge füllen
	//Siehe http://de.wikipedia.org/wiki/Vokal#Vokalbuchstaben

	Vokale[0] = "A";
	Vokale[1] = "Ä";
	Vokale[2] = "E";
	Vokale[3] = "I";
	Vokale[4] = "O";
	Vokale[5] = "Ö";
	Vokale[6] = "U";
	Vokale[7] = "Ü";
	Vokale[8] = "Y";




	//Die Anzahl der Vokale und Konsonanten ist pro Strophe begrenzt(in diesem Programm auf jeweils 3)
	
	//Enthalten die Nummern, die den Buchstaben entsprechen, enthalten
	int konsonant[3];
	int vokal[3];

	//werden die tatsächlichen Buchstaben enthalten
	string menge_konsonanten[3];
	string menge_vokale[3];



	//-------------------------------Konsoantenmenge füllen
	
	

	//-1 ist der Standartwert( so kann man leicht mit konsonant < 0 prüfen, ob dieser schon gesetzt wurde
	konsonant[0] = -1;
	konsonant[1] = -1;
	konsonant[2] = -1;

	for (int y = 0; y <= 2; y++){//für jeden konsonant

		
		while (konsonant[y] < 0){//Solange konsonant < 0 = -1 = Standartwert

			int test_konsonant = rand() % 21;//Eine zufällige Zahl generieren

			if (konsonant[0] != test_konsonant && konsonant[1] != test_konsonant && konsonant[2] != test_konsonant ){//ist diese Zahl schon verwendet?
				//wenn nicht,wird diese verwendet
				konsonant[y] = test_konsonant;
			}
			//ansonsten bleibt konsonant auf standartwert (-1) und die While-Schleife fährt fort
		}

	}

	//sind die Zahlen gesetzt, können diese in Buchstaben umgewandelt werden:

	menge_konsonanten[0] = Konsonanten[konsonant[0]];
	menge_konsonanten[1] = Konsonanten[konsonant[1]];
	menge_konsonanten[2] = Konsonanten[konsonant[2]];




	//-------------------------------Vokalmenge füllen

	//Das gleiche Spiel mit den Vokalen
	//Startwert
	konsonant[0] = -1;
	konsonant[1] = -1;
	konsonant[2] = -1;


	for (int y = 0; y <= 2; y++){//für jeden Vokal

		
		while (vokal[y] < 0){//Solange vokal < 0 = -1 = Standartwert

			int test_vokal = rand() % 9;//Eine zufällige Zahl generieren

			if (vokal[0] != test_vokal && vokal[1] != test_vokal && vokal[2] != test_vokal ){//ist diese Zahl schon verwendet?
				//wenn nicht,wird diese verwendet
				vokal[y] = test_vokal;
			}
			//ansonsten bleibt vokal auf standartwert (-1) und die While-Schleife fährt fort
		}

	}

	//sind die Zahlen gesetzt, können diese in Buchstaben umgewandelt werden:

	menge_vokale[0] = Vokale[vokal[0]];
	menge_vokale[1] = Vokale[vokal[1]];
	menge_vokale[2] = Vokale[vokal[2]];




	for (int x = 1; x <= zeilenanzahl; x++){
				strophentext = strophentext + "\n" + zeile(silbenzahl,menge_konsonanten,menge_vokale);//der strophentext erhält die zeilen
	}

	if (rand()%100 +1 <= 50){ // Ein Call wird mit der Wahrscheinlichkeit von 50/50 hinzugefügt
		 //Einen fetzigen call hinzufügen
		string Calls[3];//Menge der Calls definieren

		//Menge der Calls füllen(Calls aus der Angabe)
		Calls[0] = "yeah!";
		Calls[1] = "yo man!";
		Calls[2] = "fake that!";
		Calls[3] = "Ich bin adoptiert!";
		Calls[4] = "BwInf";
		
		//einen zufälligen Call zum Strophentext hinzufügen
		strophentext += "\n" + Calls[rand()%4];

	}else{
		//oder eben nicht
	}

	return strophentext; // Rückgabe des Strophentextes
}

string zeile(int silbenzahl,string menge_konsonanten[3], string menge_vokale[3]){
	string zeilentext= "";

	string grundsilbe = silbe(menge_konsonanten, menge_vokale); //Grundsilbe der Zeile erzeugen

	string* temparray = new string[silbenzahl]; //Temporäres Array, in dem die (gleichen) Grundsilben gespeichert werden


	for (int x = 1; x <= silbenzahl; x++){
		temparray[x-1] = grundsilbe;//speichern der Grundsilben in temparray
	}

	temparray[int( silbenzahl /2)] += "p di"; //durch die Speicherung in einem Array kann leicht auf den Median (das Feld in der Mitte) zugegriffen werden und an ihn "p di" angehängt werden


	for (int x = 1; x <= silbenzahl; x++){
		zeilentext += " " + temparray[x-1];//Temparray wird in einen String umgewandelt
	}

	return zeilentext; // Rückgabe des Zeilentextes
}

string silbe(string menge_konsonanten[3], string menge_vokale[3]){

	 string silbentext = menge_konsonanten[int(rand()%3)] + menge_vokale[int(rand()%3)]; //Silbe zusammensetzen und zurückgeben

	 return silbentext;
}