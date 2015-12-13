<?php
error_reporting(E_ALL);
/*
Aufgabe 2.
*/

//game.php direkt aufgerufen? -> Eingabemaske
if (empty($_REQUEST["anzahlRunden"])) Die("Bitte rufen Sie zuerst das <a href='index.html'>Eingabe-Formular</a> auf.");

/* Laden der Einstellungen */

define("anzahlRunden", $_REQUEST["anzahlRunden"]); //Anzahl der Runden
define("anzahlKegel", $_REQUEST["anzahlKegel"]); //Anzahl der Kegel
define("kreisRadius", $_REQUEST["kreisRadius"]); //Kreis-Radius
define("durchmesserKugel", $_REQUEST["durchmesserKugel"]); //Durchmesser der Wurf-Kugel
define("verteilungsFaktor", $_REQUEST["verteilungsFaktor"]); //Gleichmäßigkeits-Faktor

define("kegelDurchmesser", $_REQUEST["kegelDurchmesser"]); //Durchmesser der Kegel auf dem erzeugten Bild
define("bildAufloesung", $_REQUEST["bildAufloesung"]); //Auflösung der erzeugten Bilder (px)

define("zwischenstand", (($_REQUEST["zwischenstand"] == 1) ? true: false)); //Zwischenstände anzeigen?

define("transparency", $_REQUEST["transparency"]); //Transparenz der Wurf-Linien

$kreisFarbe = Array();
$kreisFarbe["r"] = $_REQUEST["kreisFarbeR"];
$kreisFarbe["g"] = $_REQUEST["kreisFarbeG"];
$kreisFarbe["b"] = $_REQUEST["kreisFarbeB"];

$kegelFarbe = Array();
$kegelFarbe["r"] = $_REQUEST["kegelFarbeR"];
$kegelFarbe["g"] = $_REQUEST["kegelFarbeG"];
$kegelFarbe["b"] = $_REQUEST["kegelFarbeB"];

$wurfFarbeAnna = Array();
$wurfFarbeAnna["r"] = $_REQUEST["wurfFarbeAnnaR"];
$wurfFarbeAnna["g"] = $_REQUEST["wurfFarbeAnnaG"];
$wurfFarbeAnna["b"] = $_REQUEST["wurfFarbeAnnaB"];

$wurfFarbeRandy = Array();
$wurfFarbeRandy["r"] = $_REQUEST["wurfFarbeRandyR"];
$wurfFarbeRandy["g"] = $_REQUEST["wurfFarbeRandyG"];
$wurfFarbeRandy["b"] = $_REQUEST["wurfFarbeRandyB"];

/* Log zum aktuellen Frame */
function logFrame($text){
	global $currentFrame;
	
	$fp = fopen(path.$currentFrame."_log.txt","a");
	fwrite($fp, "\n".$text);
	fclose($fp);

}

/* Punktestand speichern */
function savePoints(){
	global $currentFrame;
	global $punkte;
	
	$fp = fopen(path.$currentFrame."_points.txt","a");
	
	$punkteAnna = 0;
	$punkteRandy = 0;
	
	for($i = 0; $i < sizeof($punkte) - 1; $i++){
		$punkteAnna += $punkte[$i]["anna"];
		$punkteRandy += $punkte[$i]["randy"];
	}

	fwrite($fp, "Anna: ".$punkte[sizeof($punkte) - 1]["anna"]." (".$punkteAnna.")	Randy:".$punkte[sizeof($punkte) - 1]["randy"]." (".$punkteRandy.")");
	fclose($fp);
}

/* Nächster Frame*/
function nextFrame(){
	global $currentFrame;
	$currentFrame++;
}

/* Aktiellen Spielzustand zeichnen */
function draw($m = false, $a = false, $isAnna = false){
	global $kegelArray;
	global $currentFrame;
	
	global $kreisFarbe;
	global $kegelFarbe;
	global $wurfFarbeAnna;
	global $wurfFarbeRandy;


	// Bild mit Auflösung erstellen (Verhältnis: 1:1; Quadrat)
	$bild = imagecreatetruecolor(bildAufloesung, bildAufloesung);

	//Hintergrundfarbe
	$farbe_weiss = imagecolorallocate($bild, 255, 255, 255);
	imagefilledrectangle($bild, 0, 0, bildAufloesung, bildAufloesung, $farbe_weiss);

	//Kreis zeichnen
	$farbe_kreis = imagecolorallocate($bild, $kreisFarbe["r"], $kreisFarbe["g"], $kreisFarbe["b"]);
	imageellipse($bild, faktor * kreisRadius, faktor * kreisRadius, 2 * faktor * kreisRadius - 1, 2 * faktor * kreisRadius - 1, $farbe_kreis);

	//Kegel zeichnen
	$farbe_kegel = imagecolorallocate($bild, $kegelFarbe["r"], $kegelFarbe["g"], $kegelFarbe["b"]);

	foreach($kegelArray as $kegel){
		if ($kegel["status"]){
			imagefilledellipse($bild, faktor * $kegel["x"], faktor * $kegel["y"], kegelDurchmesser, kegelDurchmesser, $farbe_kegel);
		}
	}
	
	//Evtl. Wurf einzeichnen
	if ($m !== false || $a !== false){
		
		$farbe = ($isAnna) ? $wurfFarbeAnna : $wurfFarbeRandy;
		
		//Dünne Linie
		$farbe_wurf = imagecolorallocate($bild, $farbe["r"], $farbe["g"], $farbe["b"]);
		imageline($bild, 0, faktor * $a, faktor * 100, faktor * ($m * 100 + $a), $farbe_wurf);
		//Dickere, transparente Linie
		imagesetthickness($bild, 2 * faktor * durchmesserKugel);
		
		$farbe_wurf_transparent = imagecolorallocatealpha($bild, $farbe["r"], $farbe["g"], $farbe["b"], transparency);
		imageline($bild, 0, faktor * $a, faktor * 100, faktor * ($m * 100 + $a), $farbe_wurf_transparent);
		
		imagesetthickness($bild, 1);
	}
	
	//Bild speichern
	imagepng($bild, path.$currentFrame.".png");
	
}

/* Zufällige und gleichmäßig verteilte Kegelpositionen */
function createRandomPoints(){
	$tempArrayKegel = "";
	$tempArrayKegel = Array();
	
	$kreisflaeche = pi() * kreisRadius * kreisRadius;
	
	$flaecheProKegel = $kreisflaeche / anzahlKegel;
	
	$minimalerAbstand = sqrt($flaecheProKegel / pi());

	for ($x = 0; $x < anzahlKegel; $x++){
		$ok = false;
		
		while(!$ok){
			/* Zufälligen Abstand zur Kreismitte */
			$abstand = rand(1,100)/(100/kreisRadius);
			$winkel = rand(1,360);
			
			$winkelBogen = ($winkel * (pi() / 180));
			
			$xPos = kreisRadius + (cos($winkelBogen) * $abstand);
			$yPos = kreisRadius + (sin($winkelBogen) * $abstand);
			
			$ok = true;
			
			foreach($tempArrayKegel as $kegel){
				$xAbstand = abs($kegel["x"] - $xPos);
				$yAbstand = abs($kegel["y"] - $yPos);
				
				$tempAbstand = sqrt($xAbstand * $xAbstand + $yAbstand + $yAbstand);
				
				if ($tempAbstand < verteilungsFaktor * $minimalerAbstand) $ok = false;
			}
		}
		
		$temp = Array();
		$temp["x"] = $xPos;
		$temp["y"] = $yPos;
		$temp["status"] = true;
		
		$tempArrayKegel[] = $temp;
	
	}
	
	return $tempArrayKegel;
}

/* Randys Zug*/
function randyZug(){
	global $punkte;
	
	nextFrame();
	logFrame("-- Randys Zug --");
	
	/* Winkel v generieren */
	$v = rand(1,360);
	$vBogen = ($v * (pi()/180));
	
	logFrame("Winkel v: ".$v);
	
	/* Punkt x generieren */
	$a = rand(1,360); // Winkel a
	$aBogen = ($a * (pi() / 180));
	
	$abstand = rand(1,100)/(100/kreisRadius); //Abstand r
	$pX = cos($aBogen) * $abstand;
	$pY = sin($aBogen) * $abstand;
	
	$pX += kreisRadius;
	$pY += kreisRadius;
	
	// Punkt x = (pX|pY)
	logFrame("Punkt x: (".$pX."|".$pY.")");
	
	$m = tan($vBogen);
	$a = $pY - $m * $pX;
	
	logFrame("Geradengleichung: y = ".$m."*x + ".$a);
	
	// Wurf ausführen
	draw($m, $a, false);
	$anzahlUmgeworfen = wurf($m, $a);
	
	$punkte[sizeof($punkte)-1]["randy"] += $anzahlUmgeworfen;
	savePoints();
	
	logFrame("Insgesamt ".$anzahlUmgeworfen." Kegel Umgeworfen");
	
	logFrame("-- Randys Zug beendet --");
}

/* Einen Wurf entlang einer Linie, die die Funktion f(x) = $m * x + $a hat. */
function wurf($m, $a, $count = true){
	global $kegelArray;

	$kegelUmgeworfen = 0;

	for($i = 0; $i < sizeof($kegelArray); $i++){
		if ($kegelArray[$i]["status"]){
			$px = $kegelArray[$i]["x"];
			$py = $kegelArray[$i]["y"];
			
			//Fußpunkt F = (fx|Fy)
			$fx = ($m * $py + $px - $a * $m) / ($m * $m + 1);
			$fy = $m * $fx + $a;
			
			//Veränderung
			$dx = $px - $fx;
			$dy = $py - $fy;
			
			//Abstand mit dem Satz des Pythagoras
			$abstand = sqrt(($dx * $dx) + ($dy * $dy));
			
			//Kegel umgeworfen?
			if ($abstand <= durchmesserKugel){
				if ($count){ //Zählt dieser Wurf
					$kegelArray[$i]["status"] = false;
					logFrame("Kegel ".($i+1)." umgeworfen (Abstand: ".$abstand.")");
				}
				$kegelUmgeworfen++;
			}
		}
	}
	
	return $kegelUmgeworfen;
}

/* Überprüft, ob überhaupt noch ein Kegel auf dem Spielfeld steht*/
function checkFeldLeer(){
	global $kegelArray;
	
	$feldLeer = true;
	
	foreach($kegelArray as $kegel){
		if ($kegel["status"]) $feldLeer = false;
	}
	
	return $feldLeer;
}

/* Gibt die Anzahl der Kegel auf dem Spielfeld zurück */
function anzahlKegelAufDemSpielfeld(){
	global $kegelArray;
	
	$anzahl = 0;
	
	foreach($kegelArray as $kegel){
		if ($kegel["status"]) $anzahl++;
	}
	return $anzahl;
}

/* Errechnet die Punktestände von Anna und Randy in allen bereits gespielten Runden zusammen */
function punkteInsgesamt(){
	global $punkte;
	
	$punkteAnna = 0;
	$punkteRandy = 0;
	
	foreach($punkte as $runde){
		$punkteAnna += $runde["anna"];
		$punkteRandy += $runde["randy"];
	}
	
	return Array("anna" => $punkteAnna, "randy" => $punkteRandy);
}

/* Annas KI, die nach ihrer Strategie spielt */
function annaZug(){
	global $kegelArray;
	global $punkte;
	
	nextFrame();
	logFrame("\n--- Annas Zug ---");
	logFrame("- Wurf -");
	
	$besteWurfLinie = Array();
	$besteWurfLinie["m"] = 0;
	$besteWurfLinie["a"] = 0;
	$besteWurfLinie["getroffen"] = 0;
	
	logFrame("Ermittle beste Wurf-Linie");
	
	for($p1 = 0; $p1 < sizeof($kegelArray); $p1++){
		if ($kegelArray[$p1]["status"]){
			if (anzahlKegelAufDemSpielfeld() !== 1){
				for($p2 = 0; $p2 < sizeof($kegelArray); $p2++){
					if ($kegelArray[$p2]["status"] && $p1 != $p2){
					
						//Koordinaten übersichtlicher
						$p1X = $kegelArray[$p1]["x"];
						$p1Y = $kegelArray[$p1]["y"];
						$p2X = $kegelArray[$p2]["x"];
						$p2Y = $kegelArray[$p2]["y"];
					
						//m errechnen
						$dX = $p1X - $p2X;
						$dY = $p1Y - $p2Y;
					
						if ($dX == 0){ $dX = 1; } //Division durch Null verhindern
					
						$m = $dY / $dX;
					
						//a errechnen
						$a = $p1Y - ($m * $p1X);
					
						$getroffen = wurf($m, $a, false);
					
						logFrame("Es könnten ".$getroffen." Kegel getroffen werden.");
					
						if ($getroffen > $besteWurfLinie["getroffen"]){
							$besteWurfLinie["m"] = $m;
							$besteWurfLinie["a"] = $a;
							$besteWurfLinie["getroffen"] = $getroffen;
						}
					}
				}
			}else{
				$besteWurfLinie["m"] = 0;
				$besteWurfLinie["a"] = $kegelArray[$p1]["y"];
			}
		}
	}
	
	logFrame("Beste wurf-Linie ermittelt: y = ".$besteWurfLinie["m"]." * x + ".$besteWurfLinie["a"]);
	draw($besteWurfLinie["m"], $besteWurfLinie["a"], true); //Besten wurf zeichnen
	
	$anzahlUmgeworfen = wurf($besteWurfLinie["m"], $besteWurfLinie["a"], true); //Besten Wurf ausführen
	logFrame("Insgesmat ".$anzahlUmgeworfen." Kegel umgeworfen.");
	$punkte[sizeof($punkte) - 1]["anna"] += $anzahlUmgeworfen;
	
	savePoints(); //Punktestand speichern

	//Entscheidung: Runde fortsetzen?
	$temp = punkteInsgesamt();
	if ($temp["randy"] + (anzahlKegelAufDemSpielfeld() * 0.5) <= $temp["anna"]){
		logFrame("\n---- Anna hat sich für die Fortsetzung der Runde entschieden. ----");
		return true;
	}else{
		logFrame("\n---- Anna beendet die aktuelle Runde. ----");
		return false;
	}
	
}

/* ------ START ------- */

//Aktueller Frame
$currentFrame = 0;

//Ordner anlegen
$timestamp = time();
if (!(mkdir($timestamp))) Die("Fehler - Verzeichnis zum Speichern der Frames konnte nicht erstellt werden.");
define("path", $timestamp."/");

//Darstellungsfaktor errechnen
define("faktor", bildAufloesung / (2 * kreisRadius));

//Punktestand
$punkte = Array();

logFrame("Neues Spiel gestartet; ID: ".$timestamp);

//Spiel starten
for($a = 0; $a < anzahlRunden; $a++){
	
	nextFrame(); //Nächsten Frame
	logFrame("Neue Runde gestartet (Nr. ".($a+1).".)");
	
	//Punktestand-Variable; neue Runde
	$temp = Array();
	$temp["anna"] = 0;
	$temp["randy"] = 0;
	$punkte[] = $temp;
	
	//Neues Spielfeld generieren
	$kegelArray = createRandomPoints();
	logFrame("Kegel generiert.");
	
	//Die erzeutgen Kegel anzeigen
	draw();
	savePoints();
	logFrame("Starte den Spielablauf mit Initialisierung von Randys erstem Zug.");
	
	do{
		if (zwischenstand){ nextFrame(); logFrame(" "); draw(); savePoints(); }
		randyZug(); //Randys Zug
		if (checkFeldLeer()) break;
		if (zwischenstand){ nextFrame(); logFrame(" "); draw(); savePoints(); }
	}while(annaZug() && !checkFeldLeer()); //Annas Zug
	
}

//Fazit - Insgesamt-Sieger

$temp = punkteInsgesamt();
$punkteAnna = $temp["anna"];
$punkteRandy = $temp["randy"];

$sieger = "";
$differenz = abs($punkteAnna - $punkteRandy);

if ($punkteAnna > $punkteRandy){
	$sieger = "Anna";
}elseif($punkteAnna < $punkteRandy){
	$sieger = "Randy";
}elseif($punkteAnna == $punkteRandy){
	$sieger = "Unentschieden";
}else{
	//Fehler
}
?>
<html>
	<head>
		<!-- Stylesheet einbinden -->
		<link href="style_3_player.css" type="text/css" rel="stylesheet" />
		
		<!-- Titel -->
		<title>Aufgabe 2.3 :: KI | Tim Hollmann @ 33.BwInf 2014/'15</title>
	</head>
	<body>
		<div id="wrapper">
			<div class="row">
				<h1>Aufgabe 2.3 :: KI</h1>
				<h2>33.BwInf 2.Runde - Tim Hollmann</h2>
			</div>
			<hr>
			<div class="row">
				
				<div id="playerWrapper">
					<input type="hidden" name="gameID" id="gameID" value="<?php Echo $timestamp; ?>" />
					<input type="hidden" name="anzahlFrames" id="anzahlFrames" value="<?php Echo $currentFrame; ?>" />
					<div id="controlsWrapper">
						<div class="controlsItem" id="imageIndicator" style="width: 50px; text-align: center;">0/0</div>
						<div class="controlsItem" onClick="javascript:previousFrame()"><</div>
						<div class="controlsItem" style="width: 80px;" id="playPauseButton" onClick="javascript:isPlaying = !isPlaying;">-/-</div>
						<div class="controlsItem" onClick="javascript:nextFrame()">></div>
						<div class="controlsItem" style="border-right: none;">Geschw.: <input type="number" style="width: 50px;" id="intervalInput" value="150"/>ms</div>
					</div>
					<div id="imageBoxWrapper">
						<img id="imageBox" src="" alt="LOADING"></img>
					</div>
					<div id="pointsBoxContainer">
						<textarea readonly id="pointsBox"></textarea>
					</div>
					<div id="logBoxContainer">
						<textarea readonly id="logBox"></textarea>
					</div>
					
					<!-- Player-Skript einbinden -->
					<script type="text/javascript" src="player.js"></script>
				</div>
			</div>
			<div class="row">
				<div style="" align="center">
					<h3>Insgesamt-Sieger: <?php Echo $sieger." mit ".$differenz." Punkten Vorsprung nach ".anzahlRunden." Runden"; ?></h3>
				</div>
			</div>
			<div class="row">
				<div style="width: 100px; margin: 0 auto;"><a href="index.html">Neues Spiel</a></div>
			</div>
		</div>
	</body>
</html>