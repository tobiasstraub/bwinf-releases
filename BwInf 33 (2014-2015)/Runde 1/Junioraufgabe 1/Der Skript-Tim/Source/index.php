<?php

//////////////////////////////////////////////////
//                                              //
//  +----------------------------------------+  //
//  | Junioraufgabe #1 - Der Script-Tim      |  //
//  | 33.BwInf 2014/'15                      |  //
//  |                                        |  //
//  +----------------------------------------+  //
//                                              //
//////////////////////////////////////////////////

//Konstanten definieren
define("abstand", "0.3");
define("fahrbahnLaenge", 20);

//Benötigte Klassen definieren
class spur{
	public $fahrzeuge = array();
	public $belegt = 0;
};

class fahrzeug{
	public $nummer;
	public $laenge;
	
	function __construct($nummer, $laenge){
		$this->nummer = $nummer;
		$this->laenge = $laenge;
	}
};


//Überprüft, ob ein Auto $fahrzeug auf die Parkbahn $spur passen würde.
//Hierbei ist zu beachten, dass der Abstand von 0.3m nicht gilt, wenn das Auto das erste auf der Parkbahn ist.
function passt($spur, $fahrzeug){
	return ($spur->belegt + (($spur->belegt == 0) ? 0: abstand ) + $fahrzeug->laenge <= fahrbahnLaenge);
}

//Datenquelle ermitteln
if (empty($_GET["source"])){ 
	//Keine Quelle übergeben -> standard-Quelle
	if (!($daten = file_get_contents("fahrzeuge_01.txt"))) Die("Fehler beim Laden der standard-Datei 'fahrzeuge_01.txt'");
	$source_description = "Standardmäßige Datenquelle (Beispiel #1 aus der Angabe; fahrzeuge_01.txt)";
}elseif($_GET["source"] == "file" && !empty($_GET["file"]) && file_exists($_GET["file"])){
	//Name der zu ladenden Dateiquelle übergeben
	if (!($daten = file_get_contents($_GET["file"]))) Die("Fehler beim Laden der benutzerdefinierten Dateiquelle '".$_GET["file"]."'");
	$source_description = "Benutzerdefnierte Datenquelle '<strong>".$_GET["file"]."</strong>' geladen.";
}elseif($_GET["source"] == "custom" && !empty($_GET["custom"])){
	//Benutzerdefinierte Dateiquelle direkt übergeben
	$daten = $_GET["custom"];
	$source_description = "Übergebene Datenquelle '".$_GET["custom"]."'.";
}elseif($_GET["source"] == "random"){
	//Benutzer fordert zufällige Daten an
	$runden = (rand()%15) + 5; //5-19 Fahrzeuge in der Warteschalge
	$daten = "";
	for ($x = 0; $x <= $runden; $x++){
		$daten .= (empty($daten) ? "" : ";").((rand()%10)+2) . "." .((rand()%9)+1) . ((rand()%10)+1); //Fahrzegue sind 2.00 - 12.99 Meter lang.
	}
	$source_description = "Zufällige Werte wurden generiert.";
}

if (empty($daten)) Die("Fehler - Der Datensatz ist leer.");

//Dezimalpunktierung von (,) zu (.) ändern -> Zahlen können sowohl 
//in amerikanischer als auch in europäischer Dezimalschreibweise übergeben werden
$daten = preg_replace("|,|", ".", $daten);

//evtl. Leerzeichen entfernen
$daten = preg_replace("| |", "", $daten);

//Zahlen am Semikolon von einander trennen
$daten = explode(";", $daten);

//Daten überprüfen (anumerisch?) Leider nötig, da PHP sehr schwach typisiert ist (die Fließkommazahlen könnten als String interpretiert werden)
foreach($daten as $satz){ if (!(is_numeric($satz))) Die("Fehler! Der Datensatz '".$satz."' ist nicht numerisch!"); }

//Fahrzeugschlage definieren...
$fahrzeuge = array();
//... und füllen
for ($i = 0; $i <= sizeof($daten)-1; $i++){ $fahrzeuge[] = new fahrzeug($i, $daten[$i]); }

//Fahrspuren erzeugen
$spur = array(array(new spur, new spur, new spur), array(new spur, new spur, new spur));
//$spur[0] ^= Speicher für Strategie 1
//$spur[1] ^= Speicher "    "        2

// === Strategie A === //

$i = 0;
$ende = false;
$position = 0;

while ($ende == false && $i < sizeof($fahrzeuge)){
	
	//"Sobald das Fahrzeug am Kopf der Warteschlange nicht mehr untergebracht werden kann, legt die Fahre ab."
	if (!passt($spur[0][0], $fahrzeuge[$i]) && !passt($spur[0][1], $fahrzeuge[$i]) && !passt($spur[0][2], $fahrzeuge[$i])){ $ende = true; }
	
	if (passt($spur[0][$position], $fahrzeuge[$i])){
		$spur[0][$position]->fahrzeuge[] = $fahrzeuge[$i];
		$spur[0][$position]->belegt += (($spur[0][$position]->belegt == 0) ? 0: abstand) + $fahrzeuge[$i]->laenge;
		$i++;
	}
	
	$position = ($position +1)%3;
}

// === Strategie B === //

$i = 0;
$ende = false;

while (!$ende && isset($fahrzeuge[$i])){
	
	$temp = array($spur[1][0]->belegt, $spur[1][1]->belegt, $spur[1][2]->belegt);
	
	sort($temp); //Softieren; der kürzeste Eintrag bekommt den Index [0]
	
	$spurKurz = NULL; //Spur mit den wenigsten "verparkten" Metern
	
	switch ($temp[0]){
		case $spur[1][0]->belegt:
			$spurKurz = 0;
			break;
		case $spur[1][1]->belegt:
			$spurKurz = 1;
			break;
		case $spur[1][2]->belegt:
			$spurKurz = 2;
			break;
		default:
			Die("Fehler");
	}
	
	if (passt($spur[1][$spurKurz], $fahrzeuge[$i])){
		$spur[1][$spurKurz]->fahrzeuge[] = $fahrzeuge[$i];
		$spur[1][$spurKurz]->belegt += (($spur[1][$spurKurz]->belegt == 0) ? 0: abstand) + $fahrzeuge[$i]->laenge;
	}else{
		$ende = true;
	}
	
	$i++;
}

// === Daten Darstellen === //
?><html>
	<head>
		<!-- HASH: <?php Echo @hash_file('md5',__FILE__); ?> -->
		<meta charset="utf8" />
		<title>Junioraufgabe #1 :: Der Script-Tim @ 33.BwInf 2014/'15</title>
		<style type="text/css">
			html, body{
				font-family: 'Open Sans', arial, verdana;
				font-size: 12pt;
				padding: 0;
				margin: 0;				
			}
		
			#wrapper{
				width: 1200px;
				min-height: 101%;
				margin: 0 auto;
				padding-left: 15px;
				padding-bottom: 100px;
			}
		
			h1{
				font-family: 'Open Sans', arial, verdana;
				font-size: 40pt;
				font-weight: normal;
				text-decoration: none;
				border-bottom: 1px solid #C5BFBF;
			}
			
			h2{			
				font-family: 'Open Sans', arial, verdana;
				font-size: 32pt;
				font-weight: normal;
				text-decoration: none;
				border-bottom: 1px solid #e5e5e5;
			}
		</style>
		<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
		<script type="text/javascript">
			var hideOptions = false;
			
			function display_hide_options(){
				hideOptions = !hideOptions;
				document.getElementById('optionen').style.display = (hideOptions) ? "none" : "block";
				document.getElementById('display_hide_link').innerHTML = (hideOptions) ? "[Optionen anzeigen]": "[Optionen verstecken]";
			}
		</script>
	</head>
	<body>
		<div id="wrapper">
			<h1>Junioraufgabe #1 :: Der Script-Tim</h1>
			<div id="display_hide_link" OnClick="javascript:display_hide_options();" style="padding-bottom: 10px;" >[Optionen verstecken]</div>
			<div id="optionen" style="background: rgb(240, 239, 245); margin-bottom: 30px; padding-left: 7px;">
				
				<div style="font-size: 20pt; margin-bottom: 15px;">Datenquelle</div> 
				<div>
					<table>
						<tr>
							<td>Beispiele aus der Angabe</td>
							<td>
								<Form style="display: table-cell;vertical-align: inherit;" Action="" method="get">
								<input type="hidden" name="source" value="file" />
								<select name="file">
									<option value="fahrzeuge_01.txt">Beispiel #1 [fahrzeuge_01.txt]</option>
									<option value="fahrzeuge_02.txt">Beispiel #2 [fahrzeuge_02.txt]</option>
									<option value="fahrzeuge_03.txt">Beispiel #3 [fahrzeuge_03.txt]</option>
								</select>
							</td>
							<td>
								<input type="submit" value="Anfordern">
								</Form>
							</td>
						</tr>
						<tr>
							<td>Benutzerdefinierte Dateneingabe</td>
							<td>
								<Form style="display: table-cell;vertical-align: inherit;" Action="" method="get">
								<input type="hidden" name="source" value="custom">
								<input name="custom" placeholder="6,96; 5,06; 3,77; 4,95" size="25"/>
							</td>
							<td>
								<input type="submit" value="Absenden" />
								</Form>
							</td>
						</tr>
						<tr>
							<td>Zufällige Werte</td>
							<td></td>
							<td>
								<Form style="display: table-cell;vertical-align: inherit;" Action="" method="get">
								<input type="hidden" name="source" value="random" />
								<input type="submit" value="Anfordern" />
								</Form>
							</td>
						</tr>
					</table>
					<div style="font-size: 18pt; margin-top: 20px;">Aktuell verwendete Datenquelle:</div><p style="padding-bottom: 20px;"><?php Echo $source_description; ?></p>
				</div>
			</div>
			
			<table border="1" rules="groups" width="100%">
				<thead>
					<tr>
						<th>Warteschlange: <?php Echo sizeof($fahrzeuge); ?> Auto(s)</th>
						<th>Strategie A</th>
						<th>Strategie B</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Fahrzeuge verladen (% von wartenden)</td>
						<td><?php $verladen = sizeof($spur[0][0]->fahrzeuge) + sizeof($spur[0][1]->fahrzeuge) + sizeof($spur[0][2]->fahrzeuge); Echo "<strong>".$verladen."</strong> (".round(100/sizeof($fahrzeuge)*$verladen)."%)"; ?></td>
						<td><?php $verladen = sizeof($spur[1][0]->fahrzeuge) + sizeof($spur[1][1]->fahrzeuge) + sizeof($spur[1][2]->fahrzeuge); Echo "<strong>".$verladen."</strong> (".round(100/sizeof($fahrzeuge)*$verladen)."%)"; ?></td>
					</tr>
					<tr>
						<td>Freier Platz insg.[m]</td>
						<td><?php Echo (fahrbahnLaenge - $spur[0][0]->belegt) + (fahrbahnLaenge - $spur[0][1]->belegt) + (fahrbahnLaenge - $spur[0][2]->belegt); ?></td>
						<td><?php Echo (fahrbahnLaenge - $spur[1][0]->belegt) + (fahrbahnLaenge - $spur[1][1]->belegt) + (fahrbahnLaenge - $spur[1][2]->belegt); ?></td>
					</tr>
					<tr>
						<td></td>
						<td>
							<table border="1" rules="groups" width="100%">
								<thead>
									<tr>
										<td><strong>Spur 1</strong> [<?php Echo sizeof($spur[0][0]->fahrzeuge); ?> Autos]</td>
										<td><strong>Spur 2</strong> [<?php Echo sizeof($spur[0][1]->fahrzeuge); ?> Autos]</td>
										<td><strong>Spur 3</strong> [<?php Echo sizeof($spur[0][2]->fahrzeuge); ?> Autos]<s/td>
									</tr>
								</thead>
								<tbody>
									<?php
										for ($x = 0; $x < sizeof($fahrzeuge); $x++){
											Echo "<tr>";
												Echo "<td>".((isset($spur[0][0]->fahrzeuge[$x])) ? "[".$spur[0][0]->fahrzeuge[$x]->nummer."](".$spur[0][0]->fahrzeuge[$x]->laenge."m)": "")."</td>";
												Echo "<td>".((isset($spur[0][1]->fahrzeuge[$x])) ? "[".$spur[0][1]->fahrzeuge[$x]->nummer."](".$spur[0][1]->fahrzeuge[$x]->laenge."m)": "")."</td>";
												Echo "<td>".((isset($spur[0][2]->fahrzeuge[$x])) ? "[".$spur[0][2]->fahrzeuge[$x]->nummer."](".$spur[0][2]->fahrzeuge[$x]->laenge."m)": "")."</td>";
											Echo "</tr>";
										}
									?>
								</tbody>
							</table>
						
						</td>
						<td>
							<table border="1" rules="groups" width="100%">
								<thead>
									<tr>
										<td><strong>Spur 1</strong> [<?php Echo sizeof($spur[1][0]->fahrzeuge); ?> Autos]</td>
										<td><strong>Spur 2</strong> [<?php Echo sizeof($spur[1][1]->fahrzeuge); ?> Autos]</td>
										<td><strong>Spur 3</strong> [<?php Echo sizeof($spur[1][2]->fahrzeuge); ?> Autos]</td>
									</tr>
								</thead>
								<tbody>
									<?php
										for ($x = 0; $x < sizeof($fahrzeuge); $x++){
											Echo "<tr>";
												Echo "<td>".((isset($spur[1][0]->fahrzeuge[$x])) ? "[".$spur[1][0]->fahrzeuge[$x]->nummer."](".$spur[1][0]->fahrzeuge[$x]->laenge."m)": "")."</td>";
												Echo "<td>".((isset($spur[1][1]->fahrzeuge[$x])) ? "[".$spur[1][1]->fahrzeuge[$x]->nummer."](".$spur[1][1]->fahrzeuge[$x]->laenge."m)": "")."</td>";
												Echo "<td>".((isset($spur[1][2]->fahrzeuge[$x])) ? "[".$spur[1][2]->fahrzeuge[$x]->nummer."](".$spur[1][2]->fahrzeuge[$x]->laenge."m)": "")."</td>";
											Echo "</tr>";
										}
									?>
								</tbody>
							</table>
						
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</body>
</html>