
<?php
# Array aus Arrays der einzelnen Blöcke
# Der erste Block ist 0
$matches = array(array(decbin(0)));
# Erstes Bit jedes Eintrages des Blocks
$binary = bindec("1");
$zeroing = bindec("1");
$length = 9;
$method = 1; # 0 = Normal, 1 = Verbessert
$debug = false;

if ($method === 0){
	# Es werden 9 neue Blöcke (bis 9-stellig) generiert
	for ($j = 0; $j < $length; $j++) {
		# Ein temporäres Array wird für den neuen Block erstellt
		$tmp = array();

		# Überprüfen, ob wir noch beim ersten neuen Block sind
		# falls nein:
		if (count($matches) - 1 > 0) {
			# Alle Blöcke bis auf den Jüngsten durchlaufen
			for ($i = 0; $i < count($matches) - 1; $i++) {
				# Alle Einträge des aktuellen Blocks durchlaufen
				for ($k = 0; $k < count($matches[$i]); $k++) {
					# Binäre Zahl in den neuen Block eintragen, die aus der neuen $binary
					# und dem Eintrag aus dem aktuellen durchlaufenen Blocks besteht.
					# Bit-Operator | (oder): Wenn eines der Bits gesetzt ist (1), wird gesetzt.
					# Da $binary nur die erste Stelle 1 hat, wird praktisch nur gesagt:
					#  1 davor, dann die 0, da der Jüngste Block übersprungen wird und
					#  die davor kürzer sind, dann die restlichen Blöcke nochmal komplett
					#  aufzählen und eintragen.

					# Verbesserung: Aus der Sicht des neuen Blocks:
					#  Nicht alle Blöcke, sondern nur den Jüngsten und Zweitjüngsten benutzen,
					#  um den neuen herzustellen. Der neue setzt sich zusammen aus jeweils
					#  seiner $binary, dem jüngsten Block ohne seine erste 1 (= 0-Zuerst-Form),
					#  was alle Blöcke vor dem zweitjüngsten widerspiegelt, da sonst eine 11
					#  auftreten würde, und schließlich dem zweitjüngsten Block.

					# Dies alles funktioniert, da wir als Ursprung 1, sprich 01 und 10 haben.
					# Wir betrachten also nur den Umschlag, bei der 0 und 1 vertauscht werden,
					# aber keine neue 1 auf der rechten Seite hinzugefügt wird. Die linke Seite
					# halten wir frei, indem wir nach einem Umschlag (= neuer Block = ein
					# Zeichen mehr) den jüngsten Block auslassen, wodurch wir nicht bis an die
					# neue eins hochzählen, sondern eine Stelle vorher aufhören.
					# Beim endgültigen Zählen der Möglichkeiten muss man die Anzahl an Einträgen
					# des neuen Blocks mit der Anzahl der Einträge des jüngsten Blocks, welcher
					# die 0-Zuerst-Form des neuen Blocks widerspiegelt, addiert.
					array_push($tmp, decbin($binary | bindec($matches[$i][$k])));
				}
			}
		} else {
			# falls ja:
			# Da kein Block vor dem vorherigen (0) existiert, einfach nur $binary eintragen, ohne es mit anderen Werten zu kombinieren
			array_push($tmp, decbin($binary));
		}
		array_push($matches, $tmp);
		$binary = $binary << 1;
	}
} else {
	# Es werden 9 neue Blöcke (bis 9-stellig) generiert
	for ($j = 0; $j < $length; $j++) {
		# Ein temporäres Array wird für den neuen Block erstellt
		$tmp = array();

		# Überprüfen, ob wir noch beim ersten neuen Block sind
		# falls nein:
		if (count($matches) > 1) {
			# Überprüfen, ob wir schon beim vierten neuen Block sind
			# falls nein:
			if (count($matches) <= 2){
			# Alle Blöcke bis auf den Jüngsten durchlaufen
				for ($i = 0; $i < count($matches) - 1; $i++) {
				# Alle Einträge des aktuellen Blocks durchlaufen
					for ($k = 0; $k < count($matches[$i]); $k++) {
					# Binäre Zahl in den neuen Block eintragen, die aus der neuen $binary
						# und dem Eintrag aus dem aktuellen durchlaufenen Blocks besteht.
						# Bit-Operator | (oder): Wenn eines der Bits gesetzt ist (1), wird gesetzt.
						# Da $binary nur die erste Stelle 1 hat, wird praktisch nur gesagt:
						#  1 davor, dann die 0, da der Jüngste Block übersprungen wird und
						#  die davor kürzer sind, dann die restlichen Blöcke nochmal komplett
						#  aufzählen und eintragen.

						# Dies alles funktioniert, da wir als Ursprung 1, sprich 01 und 10 haben.
						# Wir betrachten also nur den Umschlag, bei der 0 und 1 vertauscht werden,
						# aber keine neue 1 auf der rechten Seite hinzugefügt wird. Die linke Seite
						# halten wir frei, indem wir nach einem Umschlag (= neuer Block = ein
						# Zeichen mehr) den jüngsten Block auslassen, wodurch wir nicht bis an die
						# neue eins hochzählen, sondern eine Stelle vorher aufhören.
						# Beim endgültigen Zählen der Möglichkeiten muss man die Anzahl an Einträgen
						# des neuen Blocks mit der Anzahl der Einträge des jüngsten Blocks, welcher
						# die 0-Zuerst-Form des neuen Blocks widerspiegelt, addieren.
						
						array_push($tmp, decbin($binary | bindec($matches[$i][$k])));
					}
				}
			# falls ja:
			} else {

				# Verbesserung: Aus der Sicht des neuen Blocks:
				#  Nicht alle Blöcke, sondern nur den Jüngsten und Zweitjüngsten benutzen,
				#  um den neuen herzustellen. Der neue setzt sich zusammen aus jeweils
				#  seiner $binary, dem jüngsten Block ohne seine erste 1 (= 0-Zuerst-Form),
				#  was alle Blöcke vor dem zweitjüngsten widerspiegelt, da sonst eine 11
				#  auftreten würde, und schließlich dem zweitjüngsten Block.

				$zeroing = $binary >> 1;
				# $j = jüngster Block
				for ($i = 0; $i < count($matches[$j]); $i++){
					$tmpStr = bindec($matches[$j][$i]);

					/*
					echo "<b>Block:</b> $j - <b>Eintrag:</b> $i <br />
					Bin: " . decbin($binary) . "<br />
					Zero: " . decbin(~$zeroing) . "<br />
					tmpStr: " . decbin($tmpStr) . "<br />
					Final: " . decbin($binary | (~$zeroing & $tmpStr)) . "<br />";
					*/

					array_push($tmp, decbin($binary | (~$zeroing & $tmpStr)));
				}
				for ($i = 0; $i < count($matches[$j - 1]); $i++){
				$tmpStr = bindec($matches[$j - 1][$i]);

				array_push($tmp, decbin($binary | $tmpStr));
				}

				# Ersten Eintrag (zweitjüngster Block) aus dem Array löschen, da er nicht mehr
				# gebraucht wird.
				reset($matches);
				$key = key($matches);
				unset ($matches[$key]);
				#echo "Deleting on index $key<br />";
				
				# Nach dem letzten Durchgang auch den jüngsten Block löschen,
				# da dieser zum zweitjüngsten wird und nicht mehr gebraucht wird.
				if ($j === $length - 1){
					unset ($matches[$key + 1]);
					#echo "End-Deleting on index " . ($key + 1) . "<br />";
				}

			}
		} else {
		# falls ja:
		# Da kein Block vor dem vorherigen (0) existiert, einfach nur $binary eintragen, ohne es mit anderen Werten zu kombinieren
			array_push($tmp, decbin($binary));
		}
		array_push($matches, $tmp);
		$binary = $binary << 1;
	}
}



# Debug-Anzeige
if ($debug){
	echo "<pre>";
	var_dump($matches);
	echo "</pre><br />";
}

# Array durchlaufen und die enthaltenen Arrays durch die Anzahl ihrer Elemente ersetzen

# &$item bedeutet, dass nicht $item kopiert und an die Funktion gegeben wird, sondern
# die Referenz auf $item übergeben wird, was es ermöglicht, das Originial in der Funktion
# zu editieren

# Neuen Block doppeln, um auch die 0-Zuertst-Form mitzunehmen
array_push($matches, $matches[$length]);

# Alle Blöcke durch ihre Anzahl an Elementen ersetzen
array_walk($matches, function(&$item) {
	$item = count($item);
});

# Alle Anzahlen addieren und ausgeben
echo "Stellen: $length<br />" . array_sum($matches)." m&ouml;gliche Kombinationen.";
?>