<!DOCTYPE html>
<html>
	<head>
		<title>BWINF 32 2013 Tim Schmidt J1</title>
		<meta charset="UTF-8">
		
	</head>
	<body>
	<div>
	<?php 
		$vokale = array("a","e","i","o","u");
		$konsonanten = array("b","c","d","f","g","h","j","k","l","m","n","p","q","r","s","t","v","w","x","y","z");
		
		# Strophenanzahl -> Array(Zeilen pro Strophe)
		function zeilenanzahl($strophen){ 
			$returnArray = array();
			$musterLaenge = mt_rand(1,3);
			echo "Zeilen Musterlänge: " . $musterLaenge . "<br />";
			$muster = array();
			for ($i = 0; $i < $musterLaenge; $i++){
				array_push($muster, mt_rand(2,10));
			}
			echo "Zeilen Muster: " . implode(",",$muster) . "<br />";
			for ($i = 0; $i < $strophen; $i++){
				if ($i == 0){
					$musterPosition = 0;
				} else {
					$musterPosition = $i % $musterLaenge;
				}
				
				array_push($returnArray, $muster[$musterPosition]);
			}
			
			return $returnArray;
		}
		
		# Strophenanzahl
		function strophenanzahl(){
			return mt_rand(2,6);
		}
		
		# Anzahl an Strophen -> Zufällige Anzahl an Silbenwiederholungen in den einzelnen Zeilen pro Strophe
		function silbenanzahl($strophen){
			$returnArray = array();
			array_merge($returnArray,array(5));
			$musterLaenge = mt_rand(3,6);
			$muster = array();
			
			echo "Silben Musterlänge: " . $musterLaenge . "<br />";
			for ($i = 0; $i < $musterLaenge; $i++){
				$rnd = (int)(floor(mt_rand(2,7)));
				if ($rnd % 2 == 0){
					$rnd++;
				}
				array_push($muster, $rnd); # Nur ungerade Zahlen
			}
			echo "Silben Muster: " . implode(",",$muster) . "<br />";
			for ($i = 0; $i < $strophen; $i++){
				if ($i == 0){
					$musterPosition = 0;
				} else {
					$musterPosition = $i % $musterLaenge;
				}
				array_push($returnArray, $muster[$musterPosition]);
			}
			
			return $returnArray;
		}
		
		# Produkt -> kleinsten Faktoren
		function kleinsteFaktoren($produkt){
			$aPossible = array(1,2,3,4,5);
			$bPossible = array();
			$a;
			$b;
			
			for ($i = 0; $i < 21; $i++){
				array_push($bPossible, $i + 1);
			}
			
			foreach ($aPossible as $aCur){
				foreach ($bPossible as $bCur){
					if ($aCur * $bCur >= $produkt){
						if (is_null($a)){
							#echo "aCur (" . $aCur . ") + bCur (" . $bCur . ") = " . ($aCur + $bCur) . " < a (" . $a . ") + b (" . $b . ") = " . ($a + $b) . " | Null<br />";
							$a = $aCur;
							$b = $bCur;
						} elseif ($aCur + $bCur < $a + $b){
							#echo "aCur (" . $aCur . ") + bCur (" . $bCur . ") = " . ($aCur + $bCur) . " < a (" . $a . ") + b (" . $b . ") = " . ($a + $b) . "<br />";
							$a = $aCur;
							$b = $bCur;
						} /* 
						
						Zufallsauswahl, falls zwei Zahlenpaare die gleiche Quersumme haben.
						Auskommentieren, um das erste Zahlenpaar zu wählen.
						
						elseif ($aCur + $bCur == $a + $b){
							if (mt_rand(0,10) < 5){
								echo "aCur (" . $aCur . ") + bCur (" . $bCur . ") = " . ($aCur + $bCur) . " == a (" . $a . ") + b (" . $b . ") = " . ($a + $b) . " | Random<br />";
								$a = $aCur;
								$b = $bCur;
							} else {
								echo "aCur (" . $aCur . ") + bCur (" . $bCur . ") = " . ($aCur + $bCur) . " == a (" . $a . ") + b (" . $b . ") = " . ($a + $b) . " | False Random<br />";
							}
						} */ else {
							#echo "aCur (" . $aCur . ") + bCur (" . $bCur . ") = " . ($aCur + $bCur) . " > a (" . $a . ") + b (" . $b . ") = " . ($a + $b) . "<br />";
							break;
						}
					}
				}
			}
			
			if (in_array($b, $aPossible) && mt_rand(0,10) < 5){
				$aTemp = $a;
				$a = $b;
				$b = $aTemp;
			}
			
			return array($a,$b);
		}
		
		# Vokalliste, Konsonantliste, Anzahl an Kombinationen (für momentane Zeile) benötigt -> array(Ausgesuchte Vokale, Ausgesuchte Konsonanten)
		function buchstaben($vokale, $konsonanten, $kombinationenBenoetigt){
			$vokaleB = array();
			$konsonantenB = array();
			$vokFaktor = kleinsteFaktoren($kombinationenBenoetigt);
			$konFaktor = $vokFaktor[1];
			echo "<b>Faktoren :</b> " . implode(", ",$vokFaktor) . "<br />";
			$vokFaktor = $vokFaktor[0];
			
			
			# Möglichkeiten = Vokale * Konsonanten
			# Vokale: Primzahlen bis 5: 2,3,5
			# Konsonanten: 1-21
			
			if ($vokFaktor == 1){
				array_push($vokaleB, array_rand($vokale, $vokFaktor));
			} else {
				$vokaleB = array_rand($vokale, $vokFaktor);
			}
				
			if ($konFaktor == 1){
				array_push($konsonantenB, array_rand($konsonanten, $konFaktor));
			} else {
				$konsonantenB = array_rand($konsonanten, $konFaktor);
			}
			
			for($i = 0; $i < sizeof($vokaleB); $i++){
				
				$vokaleB[$i] = $vokale[$vokaleB[$i]];
			}
			for($i = 0; $i < sizeof($konsonantenB); $i++){
				$konsonantenB[$i] = $konsonanten[$konsonantenB[$i]];
			}
			
			return array($vokaleB, $konsonantenB);
			
		}
		
		# (Vokalliste, $konsonantenliste, Anzahl an Zeilen (in der momentanen Strophe) minus 1 -> buchstaben()) -> array(Grundsilben für die einzelnen Zeilen jeder Strophe)
		function silben($vokale, $konsonanten, $zeilenAnzahl){
			$silben = array();
			$silbenAktuelleStrophe = array();
			
			foreach($zeilenAnzahl as $zeilenAnzahlAktuell){
				$voks = buchstaben($vokale, $konsonanten, $zeilenAnzahlAktuell - 1); # Temporär; Letzte Zeile abziehen, da diese immer ein "pseudo-cooles Zeugs" ist
				$kons = $voks[1];
				$voks = $voks[0];
				
				foreach ($kons as $kon){
					foreach ($voks as $vok){
						array_push($silbenAktuelleStrophe, $kon . $vok);
					}
				}
				array_push($silben,$silbenAktuelleStrophe);
				$silbenAktuelleStrophe = array();
			}
			
			return $silben;
		}
		
		# Anzahl an Zeilen, array(Anzahl an Silben pro Zeile), Grundsilben für die einzelnen Zeilen -> array(Alle Zeilen für eine Strophe)
		function strophe($zeilenAnzahl, $silbenAnzahl, $silben){
			$strophe = array();
			$pcs = array("fake that!","yeah!","yo man");
			for($i = 0; $i < $zeilenAnzahl - 1; $i++){ # Minus dies letzte Zeile (pseudo-cooles Zeugs)
				$zeile = "";
				for($j = 0; $j < $silbenAnzahl; $j++){
					
					if (($j + 1) * 2 == $silbenAnzahl + 1){ # mittlere Silbe 
						$zeile .= $silben[$i] . "p di ";
					} elseif ($j == $silbenAnzahl - 1) { # letzte Silbe
						$zeile .= $silben[$i];
					} else {
						$zeile .= $silben[$i] . " ";
					}
				}
				array_push($strophe, $zeile);
			}
			shuffle($strophe);
			array_push($strophe, $pcs[array_rand($pcs, 1)]);
			return $strophe;
		}

		function stropheToString($strophe){
			$stropheString = "";
			for($i = 0; $i < sizeof($strophe); $i++){
				$stropheString .= $strophe[$i] . "<br />";
			}
			
			return $stropheString;
		}
		
		# Debug-Ausgabe
		$strophenAnzahl = strophenanzahl();
		echo "<b>Anzahl Strophen:</b> " . $strophenAnzahl . "<br />";
		$zeilenAnzahl = zeilenanzahl($strophenAnzahl);
		echo "<b>Anzahl Zeilen:</b> " . implode(", ",$zeilenAnzahl) . "<br />";
		$silbenAnzahl = silbenanzahl($strophenAnzahl);
		echo "<b>Anzahl Silben:</b> " . implode(", ",$silbenAnzahl) . "<br />";
		#for($i = 0; $i < sizeof($zeilenAnzahl); $i++){
		#	$fax = kleinsteFaktoren($zeilenAnzahl[$i] - 1);
		#	echo "<b>Faktoren " . ($i + 1) . ":</b> " . implode(", ",$fax) . "<br />";
		#}
		
		$silben = silben($vokale, $konsonanten, $zeilenAnzahl);
		echo "<br />";
		for($i = 0; $i < sizeof($zeilenAnzahl); $i++){
			echo "<b>Silben in Strophe " . ($i + 1) . ":</b> " . implode(", ",$silben[$i]) . "<br />";
		}
		echo "<br />";
		
		$strophen = array();
		for($i = 0; $i < $strophenAnzahl; $i++){
			array_push($strophen, stropheToString(strophe($zeilenAnzahl[$i], $silbenAnzahl[$i], $silben[$i])));
		}
		
		$text = "";
		$text = "<pre>";
		for($i = 0; $i < sizeof($strophen); $i++){
			$text .= "<i>[Strophe " . ($i + 1) . "]</i> <br />" . $strophen[$i] . "<br />";
		}
		$text .= "</pre>";
		
		echo $text;
	?>
	</div>
	</body>
</html>

<!-- 
TODO:
(- Auch Vokal, dann Konsonant-Silben möglich machen)
-->