<?php

//Programmcode zum 32.Bundeswettbewerb Informatik von Tim Hollmann 
//Junioraufgabe 1 - Songwriter

function song(){
	
	//Maximale Zeilenanzahl pro Strophe
	$maximale_Zeilenanzahl = 4;
	
	//Maximale Strophenanzahl pro Song
	$maximale_Strophenanzahl = 5;
	
	//Songtext-Variable
	$songtext = "";
	
	//Minimal 2 Strophen, maximale Anzahl in Variablen
	$strophenanzahl = rand(2,$maximale_Strophenanzahl);
	
	//Muster für Zeilenanzahl festlegen
	//$muster = 1; Immer 3
	//$muster = 2; Abwechselnd 4 und 6
	
	$muster = rand(1,2);
	
	//Für jede Strophe
	for ($i = 1; $i <= $strophenanzahl; $i++){
		
		if ($muster == 1){
			$songtext .= "<br><br>" .strophe(3);
		}else{
			if ($i % 2 == 0){
				$songtext .= "<br><br>" .strophe(4);
			}else{
				$songtext .= "<br><br>" .strophe(6);
			}
		}
		
	}
	
	
	
	//Songtext wird schließlich zurückgegeben
	return $songtext;
}

function strophe($zeilenanzahl){

	$maximale_Konsonantenmenge_pro_Grundsilbe = 15;
	$maximale_Vokalmenge_pro_Grundsilbe = 9;
	$maximale_Zeilenanzahl = 4;
	$maximale_Silbenanzahl = 6;
	$maximale_Strophenanzahl = 4;

	$strophe_text = "";
	
	//Konsonantenanzahl möglich pro Grundsilbe
	$anzahl_konsonanten = rand(1,$maximale_Konsonantenmenge_pro_Grundsilbe);
	
	//Vokalanzahl möglich pro Grundsilbe
	$anzahl_vokale = rand(1,$maximale_Vokalmenge_pro_Grundsilbe);
	
	//Vokale und Konsonanten
	
	//Vokalmenge
	$Vokale[] = "A";
	$Vokale[] = "Ä";
	$Vokale[] = "E";
	$Vokale[] = "I";
	$Vokale[] = "O";
	$Vokale[] = "Ö";
	$Vokale[] = "Ü";
	$Vokale[] = "Y";
	
	//Konsonantenmenge
	$Konsonanten[] = "B";
	$Konsonanten[] = "C";
	$Konsonanten[] = "D";
	$Konsonanten[] = "F";
	$Konsonanten[] = "G";
	$Konsonanten[] = "H";
	$Konsonanten[] = "J";
	$Konsonanten[] = "K";
	$Konsonanten[] = "L";
	$Konsonanten[] = "M";
	$Konsonanten[] = "N";
	$Konsonanten[] = "P";
	$Konsonanten[] = "Q";
	$Konsonanten[] = "R";
	$Konsonanten[] = "S";
	$Konsonanten[] = "ß";
	$Konsonanten[] = "T";
	$Konsonanten[] = "V";
	$Konsonanten[] = "W";
	$Konsonanten[] = "X";
	$Konsonanten[] = "Z";
	
	//Menge der pro Zeile möglichen Konsonanten und Vokalen wird vereinbart
	$menge_Konsonanten = array();
	$menge_Vokale = array();
	
	//Konsonantenmenge, die pro Zeile möglich ist, wird gefüllt
	for ($i = 1; $i <= $anzahl_konsonanten; $i++){
		$menge_Konsonanten[] = $Konsonanten[rand(1,count($Konsonanten))-1];
	}
	
	//Vokalmenge, die pro Zeile möglich ist, wird gefüllt
	for ($i = 1; $i <= $anzahl_konsonanten; $i++){
		$menge_Vokale[] = $Vokale[rand(1,count($Vokale))-1];
	}
	
	//Die Silbenzahl ist für alle Zeilen einer Strophe gleich, deshalb wird sie außerhalb der folgenden for-Schleife gesetzt
	//Die Silbenzahl ist ungerade, das wird gewährleistet mit 2n+1= ungerade
	$silbenzahl = 1+(mt_rand(2,3)*2);
	
	//Für jede Zeile wird eine Grundsilbe erzeugt und die Funktion zeile() aufgerufen, welche die Silben zusammensetzt und das 'p di' an den Median anhängt
	
	$muster = rand(1,2);
	
	
	for ($i = 1; $i <= rand(2,$maximale_Zeilenanzahl) ; $i++){
				$strophe_text .= "<br>" .zeile($silbenzahl,silbe($menge_Konsonanten,$menge_Vokale));//Muster 'Immer '
	}
	
	//Überhaupt ein Call bei dieser Strophe? (Chance ~50/50)
	if (mt_rand(1,100) < 50){
		//Wenn ja,
		//Menge der Calls definieren
		$Call = array();
	
		//Menge füllen
		$Call[] = "yeah!";
		$Call[] = "yo man";
		$Call[] = "fake that!";
		
		//zufälligen Call hinzufügen
		$strophe_text .= "<br>" . $Call[rand(1,count($Call)) -1];
	}
	
	//Rückgabe der Strophe an die song()-Funktion
	return $strophe_text;
}

function zeile($silbenzahl,$silbe){
	
	//Temporäres Array, in dem die (alle gleichen) Silben gespeichert werden, wird definiert.
	$tempArray = array();
	
	//Silben werden in Array gespeichert.
	for ($i = 0; $i < $silbenzahl; $i++){
		$tempArray[] = $silbe;
	}
	
	//Am Median(Mitte) 'p di' ansetzen
	$tempArray[round(count($tempArray) / 2) -1] .= "p di";
	
	//Array zu Zeilentext zusammenfügen
	$zeilentext = "";
	foreach($tempArray as $item){
		$zeilentext .= " ".$item;
	}
	
	//Zeilentext zu strophe()-Funktion zurückgeben
	return $zeilentext;
}

function silbe($menge_Konsonanten,$menge_Vokale){

//Silbe wird zusammengesetzt
//Es müssen große Zahlen verwendet werden, da die PHP-Zufallsfunktion kleine Zahlen häufig doppelt zurückliefert(Gesetz der kleinen Zahlen)
//um große Zahlen verwenden zu können, wird hier der Modulo-Operator (%) verwendet.
return $menge_Konsonanten[mt_rand(20000,100000) % count($menge_Konsonanten)] . $menge_Vokale[mt_rand(100,2000)% count($menge_Vokale)];

}

//Anzeigen des HTML-Körpers und Aufruf der song()-Funktion
?>
<html>
<head>
<title>Junioraufgabe 1 | 32.BwInf | Songtext-Writer | Tim Hollmann</title>
</head>
<body>
<!-- Überschrift-->
<h2>Songtext-Writer Zum 32.BwInf (1.Junioraufgabe); Tim Hollmann</h2><br>Die häufigere Ausgabe von gleichen Silben ist fehler der Zufallsfunktion und nicht des Scripts.

<!-- Neuer Songtext(Aktualisieren)-->
<hr><a href="">Neuer Songtext</a>
<!-- Mehrere Songtexte-->
<br>Mehrere Songtexte Anfordern:
<br>Anzahl:<form action='' method='get'><input name='anzahl' size="5"><input type='submit' value='Anfordern'></form>

<?php
if (isset($_GET["anzahl"]) && is_numeric($_GET["anzahl"])){
for($x = 1; $x <= $_GET["anzahl"]; $x++){
Echo "<hr>Songtext Nr.".$x.song();
}

}else{
Echo "<hr>".song();
}



?>
</body>
</html>