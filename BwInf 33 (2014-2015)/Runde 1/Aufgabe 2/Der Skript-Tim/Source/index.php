<?php

//////////////////////////////////////////////////
//                                              //
//  +----------------------------------------+  //
//  | Aufgabe 2 - Der Script-Tim             |  //
//  | 33.BwInf 2014/'15                      |  //
//  |                                        |  //
//  +----------------------------------------+  //
//                                              //
//////////////////////////////////////////////////

?>
<html>
	<head>
		<!-- HASH: <?php Echo @hash_file('md5',__FILE__); ?> -->
		<title>Aufgabe 2 :: Der Script-Tim @ 33.BwInf 2014/'15</title>
		<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>

		<style type="text/css">
			html{
				font-family: 'Open Sans', Verdana, Arial;
			}

			#wrapper{
				width: 900px;
				margin: 0 auto;
				min-height: 100%;
			}

		</style>
	</head>

	<body>
		<div id="wrapper">
			<h1>Mobile</h1>
<?php

class Figur{
	public $abstand = 0;
	public $gewicht = 0;

	public function __construct($gewicht){
		$this->gewicht = $gewicht;
	}

	public function gewicht(){ return $this->gewicht; }

}

class Balken{
	public $abstand = 0;

	public $strukturen = array();
	
	public function gewicht(){
		$gewicht_insg = 0;
		foreach($this->strukturen as $struktur){
			$gewicht_insg += $struktur->gewicht();
		}
		return $gewicht_insg;
	}

	public function __construct($gewichte = array()){

		if (sizeof($gewichte) == 2){
			$aufhaengungLinks = new Figur($gewichte[0]);
			$aufhaengungRechts = new Figur($gewichte[1]);


			$aufhaengungLinks->abstand = $aufhaengungRechts->gewicht();
			$aufhaengungRechts->abstand = -($aufhaengungLinks->gewicht());

			$this->strukturen[] = $aufhaengungLinks;
			$this->strukturen[] = $aufhaengungRechts;
		}

		if (sizeof($gewichte) == 3){
			$aufhaengungLinks = new Balken(array($gewichte[0],$gewichte[1]));
			$aufhaengungRechts = new Figur($gewichte[2]);

			$aufhaengungLinks->abstand = $aufhaengungRechts->gewicht();
			$aufhaengungRechts->abstand = -($aufhaengungLinks->gewicht());

			$this->strukturen[] = $aufhaengungLinks;
			$this->strukturen[] = $aufhaengungRechts;
		}

		if (sizeof($gewichte) >= 4){
			$aufhaengung = array();
			$aufhaengung[0] = array();
			$aufhaengung[1] = array();
			$aufhaengung[2] = array();
			$aufhaengung[3] = array();

			for($x = 0; $x < sizeof($gewichte); $x++){
				$aufhaengung[$x % 4][] = $gewichte[$x];
			}

			$aufhaengung1 = Struktur($aufhaengung[0]);
			$aufhaengung2 = Struktur($aufhaengung[1]);
			$aufhaengung3 = Struktur($aufhaengung[1]);
			$aufhaengung4 = Struktur($aufhaengung[1]);

			$OK = false;
			for($faktor = 1; ($faktor <= 3 && $OK == false) ; $faktor++){
				//Paar 1: 1vs2
				$aufhaengung1->abstand = $aufhaengung2->gewicht();
				$aufhaengung2->abstand = -($aufhaengung1->gewicht());

				//Paar 2: 3vs4
				$aufhaengung3->abstand = $faktor*$aufhaengung4->gewicht();
				$aufhaengung4->abstand = -($faktor*$aufhaengung3->gewicht());

				if ($aufhaengung1->abstand != $aufhaengung3->abstand && $aufhaengung2->abstand != $aufhaengung4->abstand){
					$OK = true;
				}else{
					$OK = false;
				}
			}

			if ($OK == false){ Die("FEHLER - STRATEGIE FEHLGESCHLAGEN."); }

			$this->strukturen[] = $aufhaengung1;
			$this->strukturen[] = $aufhaengung2;
			$this->strukturen[] = $aufhaengung3;
			$this->strukturen[] = $aufhaengung4;
		}

	}
}

function Struktur($gewichte = array()){
	if (sizeof($gewichte) == 1){
		return new Figur($gewichte[0]);
	}else{
		return new Balken($gewichte);
	}
}

function ausgeben($strukturen){
	$ausgabe = "{";
	$didfirst = false;

	foreach($strukturen as $struktur){
		if ($struktur instanceof Balken){
			$ausgabe .= (($didfirst == true) ? "," : "") . $struktur->abstand . ":" . ausgeben($struktur->strukturen);
		}elseif($struktur instanceof Figur){
			$ausgabe .= (($didfirst == true) ? "," : "") . $struktur->abstand .":" . $struktur->gewicht();
		}
		$didfirst = true;
	}
	$ausgabe .= "}";
	return $ausgabe;
}


if (!empty($_REQUEST["gewichte"])){

	//Gewichte/Figuren einlesen
	$gewichte = $_REQUEST["gewichte"];
	$gewichte = preg_replace("| |", "", $gewichte); //evtl. Leerzeichen entfernen
	$gewichte = preg_replace("|,|", ";", $gewichte); //evtl. Kommata in Semikolon
	$gewichte = explode(";", $gewichte);

	foreach($gewichte as $gewicht){
		if (!(is_numeric($gewicht)) || $gewicht == 0) Die("Fehler- Mindestens ein Datensatz enthält ungültige (nicht numerische) Zeichen! Ausführung abgebrochen.<a href=\"?gewichte\">Gewichte erneut eingeben</a>");
	}

	//Ausbalancierung in Gang setzen
	$Mobile = Struktur($gewichte);

	//Mobile darstellen (Textform)
	$ausgabe = "";
	
	if ($Mobile instanceof Balken){
		$ausgabe = ausgeben($Mobile->strukturen);
	}else{
		//Eine Figur!
		$ausgabe = "{0:".$Mobile->gewicht()."}";
	}

	Echo "<a href=\"?gewichte\">Gewichte neu eingeben</a><h2>Mobile (ausbalanciert; textural)</h2><hr />".$ausgabe."<hr />";

}else{
	Echo "<hr>Keine Daten übergeben. Geben Sie hier die Gewichte der Figuren ein (durch Kommata oder Semikolon getrennt; Leerzeichen können benutzt werden):";
	Echo "<br><Form Action=\"\" method=\"get\"><textarea style=\"width: 350px; height: 60px;\" name=\"gewichte\"></textarea><br><input type=\"submit\" value=\"Ausbalancieren!\"/></Form>";
}

//Echo "still alive!";

?>
		</div>
	</body>
</html>