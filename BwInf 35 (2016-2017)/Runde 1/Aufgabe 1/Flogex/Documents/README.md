# Aufgabe 1
Es soll das Jahr ermittelt werden, in welchem Ostern und Weihnachten aufeinander fallen.

## Lösungsidee
### Julianischer und Gregorianischer Kalender
Der Gregorianische Kalender wurde als Erweiterung des Julianischen Kalenders von Papst Gregor XIII. eingeführt. Es wurden neue Schaltregeln eingeführt, um die durchschnittliche Länge eines Jahres auf 365,2425 Tagen zu verringern und damit nahe an die korrekte Länge von 365,24219 Tagen heranzukommen. Ein Jahr des Julianischen Kalenders war im Durchschnitt 365,25 Tage lang. Dies führte dazu, dass der Kalender dem korrekten Datum hinterherlief und nicht mehr mit dem metrologischen Frühlingsanfang übereinstimmte (10 Tage Differenz im Jahr 1582). Dies machte eine Reform notwendig.

Es soll das Datum ermittelt werden, an dem Weihnachten nach Julianischem (orthodoxen) und Ostern nach Gregorianischem (protestantisch/katholisch) bzw. Weihnachten nach Gregorianischem und Ostern nach Julianischem Kalender zusammenfallen. Dies ist möglich, weil
* im Reformjahr 1582 10 Tage übersprungen und auf den 4. Oktober direkt der 15. Oktober folgte und
* neue Regeln für das Schaltjahr eingeführt wurde. Es gilt nun:  
    *Boolean schaltjahr = (jahr%4==0 && jahr%100!=0) || jahr%400==0 ;*  


Da die Jahre 1700, 1800 und 1900 nach Julianischem Kalender Schaltjahre und damit 366 Tage lang waren, nach Gregorianischem allerdings nicht, beträgt die Differenz zwischen beiden Kalendern zurzeit 13 Tage.

### Berechnung des Osterdatums
Das Osterdatum wird durch die Formel von Spencer Jones berechnet. Diese hat im Vergleich zur Gaußschen Osterformel den Vorteil, dass keine Ausnahmeregelungen implementiert werden müssen. Eine Beispielimplementation in Java findet man unter http://www.hib-wien.at/leute/wurban/mathematik/Ostern/Osterdatum.html (letzter Zugriff: 04.11.2016).

### Umrechnung zwischen Julianischem und Gregorianischem Kalender
Um Daten zwischen beiden Kalendern umzurechnen, muss die Tagesdifferenz des entsprechenden Jahres vom gegebenen Datum addiert (Julianisch -> Gregorianisch) oder subtrahiert (Gregorianisch -> Julianisch) werden. Wie man die Tagesdifferenz berechnet, ist hier nachzulesen: 
https://de.wikipedia.org/wiki/Umrechnung_zwischen_julianischem_und_gregorianischem_Kalender#Berechnung_der_Tagesdifferenz .

### Orthodoxes Weihnachten fällt auf evangelisch-katholisches Ostern
Prinzipiell wird das Osterdatum nach Gregorianischem Kalender in jedem Jahr berechnet und es wird das Jahr zurückgegeben, in welchem dieses Datum der 25. Dezember ist. Um aber die Zeit, die für die Berechnung benötigt wird, zu verkürzen, berechnet man die Tagesdifferenz zwischen Julianischem und Gregorianischem Kalender im jeweiligen zu überprüfenden Jahr.
Das Osterdatum kann je nach Erscheinungsdatum des ersten Vollmonds des Frühlings eines Jahres frühestens auf 22. März und spätestens auf den 25. April fallen. Die Tagesdifferenz vom 25. Dezember beträgt also mindestens 87 und maximal 121 Tage. Da sich die Differenz höchstens einmal in 100 Jahren ändert, wenn also ein Schaltjahr nach Julianischem, nicht aber nach Gregorianischem Kalender vorkommt, müssen nicht alle Jahre überprüft werden, sondern es kann in 100er-Schritten vorgegangen werden, bis man in die Nähe der genauer zu untersuchenden Jahre gekommen ist (tagesdifferenz == 86 Tage).
Im Folgenden wird dann das Gregorianische Osterdatum im gegebenen Jahr berechnet und in das Julianische Kalenderformat umgewandelt. Entsprechen Tag und Monat nach Julianischem Kalender nun dem 25. Dezember, wird das entsprechende Jahr zurückgegeben.

### Evangelisch-katholisches Weihnachten fällt auf orthodoxes Ostern
Die Differenz zwischen dem 25.04. und dem 25.12. beträgt 244 Tage. Auch hier wird als erstes die Tagesdifferenz zwischen Julianischem und Gregorianischem Kalender im jeweiligen Jahr berechnet, wobei das Jahr jeweils um 100 erhöht wird, solange diese kleiner 244 ist.
Im Folgenden wird dann das Julianische Osterdatum im gegebenen Jahr berechnet und in das Gregorianische Kalenderformat umgewandelt. Entsprechen Tag und Monat nach Gregorianischem Kalender nun dem 25. Dezember, wird das entsprechende Jahr zurückgegeben.

## Implementierung
Verwendete Programmiersprache: C#

### Date-Klasse
Zur Lösung der Aufgabe wurde eine neue Klasse Date implementiert, da die Standardklasse DateTime nicht mit solch großen Jahreszahlen zurechtkommt und eine Exception geworfen wird. Ebenfalls wurde die Equals-Methode dahingehend überschrieben, dass ein Date-Objekt date1 gleich eines Date-Objekts date2 ist, wenn beide in Tag und Monat übereinstimmen, wobei das Jahr unterschiedlich sein kann.

### dummieDate
Wie im Abschnitt Lösungsidee beschrieben, wird als erstes das Jahr ermittelt, in welchem die Tagesdifferenz zwischen Gregorianischem und Julianischem Kalender es erlaubt, dass Weihnachten und Ostern zusammenfallen. Für die Berechnung sind Tag und Monat nicht entscheidend. Warum dann überhaupt ein Objekt der Klasse Date verwenden anstatt ein int für das Jahr? Der Methode BerechneTagesDifferenz() muss ein Date übergeben, da der Monat dort eine große Rolle spielt. Das wird später noch einmal wichtig. Es wird also ein dummieDate benötigt.
Suche nach dem korrekten Datum
Wurde der Bereich eingegrenzt, in dem es überhaupt möglich ist, dass Weihnachten und Ostern aufeinander fallen, kann nun in diesem Bereich nach dem konkreten Datum gesucht werden. Dazu wird mithilfe von Jones Osterformel das Osterdatum im jeweiligen Jahr gesucht. Das Startjahr entspricht dem Jahr des dummieDates. Das Jahr wird in einer Schleife um 1 erhöht, solange nicht das korrekte Datum gefunden wurde. Das korrekte Datum entspricht dem 24.12. im Julianischem (Teilaufgabe 1) bzw. Gregorianischem (Teilaufgabe 2) Kalender.

## Ergebnis
Der **23.03.11919** im Gregorianischem Kalender entspricht dem **25.12.11918** im Julianischem Kalender.  
Der **25.04.32839** im Julianischem Kalender entspricht dem **25.12.32839** im Gregorianischem Kalender.
