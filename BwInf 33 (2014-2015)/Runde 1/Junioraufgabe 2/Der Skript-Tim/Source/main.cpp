///////////////////////////////////////////////
// BruchCreator 2.0 - Juniorliga             //
// Der Script-Tim @ 33.BwInf 2014/'15        //
///////////////////////////////////////////////

#include<iostream>
#include<stdlib.h>
#include<time.h>
#include<math.h>
#include<sstream>

using namespace std;

void ausgeben(int y, int p, int q, int x, int obergr, int untergr, int bruchLaenge){
cout << "\n";
//Nummer
cout << "| ";
if ((int)(log10((double)(y))) +1 == 4){ cout << "";    }
if ((int)(log10((double)(y))) +1 == 3){ cout << "0";   }
if ((int)(log10((double)(y))) +1 == 2){ cout << "00";  }
if ((int)(log10((double)(y))) +1 == 1){ cout << "000"; }
cout << y;
//a/b
cout << " | ";
if ((((int)(log10((double)(p*x))))+1) + (((int)(log10((double)(q*x))))+1) == 3){ cout << "  "; }
if ((((int)(log10((double)(p*x))))+1) + (((int)(log10((double)(q*x))))+1) == 4){ cout << " ";  }
cout << p*x;
cout << "/";
cout << q*x;
//Faktor x
cout << " | ";
if (x < 10){ cout << " "; }
cout << x;
//p-q-Summe
cout << "     | ";
if ((((int)(log10((double)(p))))+1) + (((int)(log10((double)(q))))+1) == 2){ cout << "  "; }
if ((((int)(log10((double)(p))))+1) + (((int)(log10((double)(q))))+1) == 3){ cout << " ";  }
cout << p << "/" << q;
//Check: p-q-Summe
cout << " | ";
cout << ((p+q > untergr && p+q <= obergr) ? "OK   [" : "FAIL [");
if (p+q < 10 ){ cout << "0"; }
cout << p+q;
//Check: BruchLänge
cout << "] | ";
int laenge = (((int)(log10((double)(p*x))))+1) + (((int)(log10((double)(q*x))))+1);
cout << ((laenge == bruchLaenge) ? "OK   [" : "FAIL [");
cout << laenge;
cout << "]   |";
}


int main(int argc, char* argv[]){
	srand((unsigned) time(NULL));
	setlocale(LC_ALL, "german");

	cout << "\n +=========================================+";
	cout << "\n | BruchCreator 2.0 :: Junioraufgabe #2    |";
	cout << "\n | Der Script-Tim @ 33.BwInf 2014/'15      |";
	cout << "\n +=========================================+\n\n";

	int anzahlBrueche = 0;
	int schwierigkeit = 0;

	//Datenquelle: Befehlszeilenargumente übergeben?
	bool kommandozeilenparameterOK = false;

	if (argc == 3){
		stringstream sstream;
		sstream.clear();
		sstream << argv[1];
		sstream >> anzahlBrueche;

		if (anzahlBrueche <= 0){
			cout << "Fehler: Anzahl der Br[ue]che '" << anzahlBrueche << "' entspricht nicht den Anforderungen.\nGehe [ue]ber zu manueller Dateneingabe...";
		}else{
			sstream.clear();
			sstream << argv[2];
			string schwierigkeitsstufe;
			sstream >> schwierigkeitsstufe;
			
			schwierigkeit = 0;

			if (schwierigkeitsstufe == "leicht" || schwierigkeitsstufe == "Leicht" || schwierigkeitsstufe == "1"){
				schwierigkeit = 1;
			}

			if (schwierigkeitsstufe == "mittel" || schwierigkeitsstufe == "Mittel" || schwierigkeitsstufe == "2"){
				schwierigkeit = 2;
			}

			if (schwierigkeitsstufe == "schwer" || schwierigkeitsstufe == "Schwer" || schwierigkeitsstufe == "3"){
				schwierigkeit = 3;
			}

			if (schwierigkeit == 0){
				cout << "Fehler: Schwierigkeitsstufe '" << schwierigkeitsstufe << "' nicht bekannt. Verwenden Sie 'leicht', 'mittel' oder 'schwer'"; 
				cout << "\nGehe zu manueller Dateneingabe [ue]ber...";
			}else{
				kommandozeilenparameterOK = true;
			}
		}

	}

	if (!kommandozeilenparameterOK){
		//Datenquelle: Benutzer
		//Anzahl der zu erzeugenden Brüche
		cout << "\nAnzahl der zu erzeugenden Br[ue]che:";
	
		do{
			cin >> anzahlBrueche;
		}while(anzahlBrueche < 1);
	
		//Schwierigkeitsgrad
		cout << "\n Schwierigkeit: \n";
		cout << " +---------------------------------------+\n";
		cout << " | [1] Leicht                            |\n";
		cout << " | [2] Mittel                            |\n";
		cout << " | [3] Schwer                            |\n";
		cout << " +---------------------------------------+\n";
	
		do{
			cout << "\n:";
			cin >> schwierigkeit;
		}while(schwierigkeit != 1 && schwierigkeit != 2 && schwierigkeit != 3);
	}else{
		cout << "\n=============================================";
		cout << "\n Kommandozeilenparameter erhalten:";
		cout << "\n Anzahl der Br[ue]che: " << anzahlBrueche;
		cout << "\n Schwierigkeit: " << schwierigkeit << " ";
		switch(schwierigkeit){
			case 1:
				cout << "(leicht)";
				break;			
			case 2:
				cout << "(mittel)";
				break;
			case 3:
				cout << "(schwer)";
				break;
			default:
				cout << "\n\nUNBEKANNTER FEHLER AUFGETRETEN.BEENDE";
				return 0;	
		}

		cout << "\n=============================================";
	}

	cout << "\n\n-----Generiere Br[ue]che..-----\n";
	cout << "\n+======+========+========+=======+===========+============+";
	cout << "\n| Nr.  | a/b    | Faktor | p/q   | p-q-Summe | L[ae]nge   |";
	cout << "\n+------+--------+--------+-------+-----------+------------+";

	int untergr = 0, obergr = 0;
	int x,y,p,q;

	if (schwierigkeit == 1){
		for(unsigned int y = 1; y <= anzahlBrueche; y++){
			p = 0; q = 0; x = 0;
			do{	
				//P erzeugen	
				p = (rand() % 9) +1;

				//Q erzeugen
				do{
					q = (rand() % (10-p)) +1;
				}while(p + q > 10 || p == q);


				//X erzeugen
				if (p > q){
					x = (rand() % (((int)(100/p))-1))+2;	
				}else{
					x = (rand() % (((int)(100/q))-1))+2;
				}
			}while(!(q > 0 && p > 0 && p != q && p+q <= 10 && x >= 2 && ((int)(log10((double)(p*x))) +1) + ((int)(log10((double)(q*x))) +1) == 4));
			ausgeben(y, p, q, x, 10, 0, 4);
		}
	}else{
		obergr = 0; untergr = 0;
		if (schwierigkeit == 2){//Mittel
			untergr = 10;
			obergr = 20;
		}else{ //Schwer
			obergr = 30;
			untergr = 20;
		}

		for(unsigned int y = 1; y <= anzahlBrueche; y++){
			p = 0; q = 0; x = 0;
			do{
				//P erzeugen
				p = (rand() % (obergr - 1)) +1;

				//Q erzeugen
				do{
					q = (rand() % (obergr - p)) +1;
				}while(p == q || p + q > obergr || p+q <= untergr);

				//X ermitteln
				if (p > q){
					int differenz = ((int)(100/q)) - (((int)(100/q)) +1);
					x = (((int)(100/p))+1) + (rand() % differenz) +1;
				}else{
					int differenz = ((int)(100/q)) - (((int)(100/q)) +1);
					x = (((int)(100/p))+1) + (rand() % differenz) +1;
				}

			}while(!(q > 0 && p > 0 && p != q && p+q <= obergr && p+q > untergr && x >= 2 && (((int)(log10((double)(p*x)))) +1) + (( (int) (log10((double)(q*x)))) +1) == 5));
			ausgeben(y, p, q, x, obergr, untergr, 5);
		}
	}

	cout << "\n+======+========+========+=======+===========+============+";
	cout << "\n\n-----Generieren abgeschlossen-----\n";


	cout << "\nDruecken Sie eine beliebige Taste...";
	cin.sync();
	cin.get();
	//Ende
	return 0;
}
