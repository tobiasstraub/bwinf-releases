/*
======================================
| Aufgabe 3                          |
| Der Script-Tim @ 33.BwInf 2014/'15 |
|                                    |
======================================
*/

//Benötigte Header einbinden
#include<stdlib.h>
#include<iostream>
#include<vector>
#include<sstream>

using namespace std;

struct loesung{
    int platz;
    vector<bool> pfad;
};

vector<loesung> loesungen;

void entscheidung(vector<bool> pfad, unsigned int personenImKreis, int silbe, bool entscheidungZweiSilben, int platz){
    pfad.push_back(entscheidungZweiSilben);

    silbe++; //"Normale" Silbe
    if (entscheidungZweiSilben) silbe++; //Entscheidung ausführen

    unsigned int person = 2; //Die nächste Person ist logischer Weise rechts neben dem Geburtstagskind

    for(; silbe <= 16; silbe++){
        if (personenImKreis <= 1) return;

        if (silbe == 16){
            if (person == 1){
                //Geburtstagskind wurde getroffen
                loesung temp;
                temp.platz = platz + 1;
                temp.pfad = pfad;
                loesungen.push_back(temp);
				return;
            }else{
                //Irgend jemand anderes wurde getroffen
                personenImKreis--;
                platz++;
                silbe = 0;
            }
        }else{
            if (person == 1){
                if (personenImKreis >= 16){
                    entscheidung(pfad, personenImKreis, silbe, true, platz  );
                    entscheidung(pfad, personenImKreis, silbe, false, platz );
                    return;
                }
                
            }
        }
        person = (person % personenImKreis)+1; //Neue Person ermitteln
    }

    return;
}


int main(int argc, char* argv[]){

    cout << "\n";
    cout << "**************************************************\n";
    cout << "* Aufgabe 3                                      *\n";
    cout << "* Der Script-Tim @ 33.BwInf 2014/'15             *\n";
    cout << "**************************************************\n";

    unsigned int personenImKreis = 0;
    bool kommandozeilenparameterOK = false;

    if (argc == 2){
        stringstream sstream;
        sstream.clear();
        sstream << argv[1];
        
        sstream >> personenImKreis;
        if (personenImKreis > 0) kommandozeilenparameterOK = true;
    }

    if (kommandozeilenparameterOK == true){
        cout << "\nKommandozeilenparameter uebergeben: " << personenImKreis << " Personen";
    }else{
        cout << "\nKeine oder ungueltige Kommandozeilenparameter uebergeben. Manuelle Eingabe:";
        do{
            cout << "\nAnzahl der Personen im Kreis:";            
            cin >> personenImKreis;
        }while(personenImKreis <= 0);
    }

    vector<bool> pfad; //Leeren Pfad erstellen

    cout << "\n\nVorgang gestartet, bitte warten :P ...";

    entscheidung(pfad, personenImKreis, 0, false, 0 );
    entscheidung(pfad, personenImKreis, 0, true, 0  );

    cout << "\nErgebnisse: " << loesungen.size() << "\n\n";

    if (loesungen.size() == 0){
        cout << "\n\nEs wurden leider keine Loesungen gefunden. Das Geburtstagskind ist spaetestens " << personenImKreis << ".";
    }else{
        int besterPlatz = personenImKreis;

        for(unsigned int loesung = 0; loesung < loesungen.size(); loesung++){
            if (loesungen[loesung].platz < besterPlatz){ besterPlatz = loesungen[loesung].platz; }
        }

        cout << "Bester Platz: " << besterPlatz;

		for(unsigned int loesung = 0; loesung < loesungen.size(); loesung++){
			if (loesungen[loesung].platz == besterPlatz){
				cout << "\n\n----------\n";
				for(unsigned int schritt = 0; schritt < loesungen[loesung].pfad.size(); schritt++){
					cout << "\n" << schritt +1 << ":" << ((loesungen[loesung].pfad[schritt]) ? "2 Silben" : "1 Silbe");
				}
			}
		}
    }

    cout << "\n\n\n";
    cin.sync();
    cin.get();
    return 0;
}