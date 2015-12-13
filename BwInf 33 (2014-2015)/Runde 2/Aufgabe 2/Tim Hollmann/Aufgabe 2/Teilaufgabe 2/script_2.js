/*

	Panorama-Kegeln
	Spielen einer natürlichen Person gegen Randy
	Tim Hollmann @ 33.BwInf 2014/'15 2.Runde

*/

/* globale Variablen: */

// Status-Variable
var status = 0;

// Kegel-Array
var arrayKegel = null;

// Punktestände: aktuelle Runde und Rundenverlauf
var punkteAktuelleRunde = null;
var punkteRunden = null;

// Array zum Sammeln von eingegebenen Punkten (für Annas Wurf)
var punkteEingabe = null;

/* Neues Spiel starten */
function newGame(){
	log("\n\n\n ---- Neues Spiel gestartet ----");

	/* Alte Rundenwerte löschen */
	punkteRunden = new Array();
	
	/* Erste Runde starten */
	log("\n\n --- Initialisiere erste Runde ---");
	newRound();
}

/* Neue Runde starten */
function newRound(){
	log("\n -- Neue Runde gestartet --");
	
	/* Alte Rundenwerte? */
	if (punkteAktuelleRunde != null){
		punkteRunden.push(punkteAktuelleRunde);
		log("Alter Punktestand gefunden und gespeichert.");
	}
	
	/* Alten Punktestand löschen */
	punkteAktuelleRunde = new Array();
	punkteAktuelleRunde["anna"] = 0;
	punkteAktuelleRunde["randy"]= 0;
	
	/* Neue Kegel erzeugen */
	
	var kreisRadius = parseInt(document.getElementById("inputKreisRadius").value);
	var anzahlKegel = parseInt(document.getElementById("inputAnzahlKegel").value);
	
	arrayKegel = createRandomPoints(kreisRadius, anzahlKegel);
	log("Neue Kegelpositionen generiert.");
	
	status = 0;
	draw();
	
	log("Starte Randys ersten Zug.");
	setTimeout(randyZug, parseInt(document.getElementById("inputWartezeit").value));
}

/* Log-Funktion */
function log(text){
	console.log(text);
	var logBox = document.getElementById("logBox");
	logBox.value += "\n" + text;
	
	/* Herunter scrollen */
	logBox.scrollTop = 1000000;
}

/* Eine bestimmte Anzanl an Punkten zufällig ung gleichmäßig erzeugen */
function createRandomPoints(kreisRadius, anzahlKegel){

	/* Temporäres Array zum Sammeln der Punkte */
	var tempArrayKegel = new Array();
	
	/* Kreisfläche ausrechnen */
	var kreisflaeche = Math.PI * kreisRadius * kreisRadius;
	
	/* Kreisfläche pro Kegel */
	var flaecheProKegel = kreisflaeche / anzahlKegel;
	
	/* Minimaler Abstand berechnen */
	var minimalerAbstand = Math.sqrt(flaecheProKegel / Math.PI);
	
	for(var x = 0; x < anzahlKegel; x++){
		var ok = false;
		
		while (ok != true){
		
			/* zufälliger Abstand zur Kreismitte */
			var abstand = random(1,100)/(100/kreisRadius);
			
			/* zufälliger Winkel */
			var winkel = random(1,360);
			
			/* Winkel in Bogenmaß umrechnen (wird von den Sinus- und Kosinus-Funktionen benötigt) */
			var winkelBogen = (winkel * (Math.PI / 180));
			
			/* Errechnen, wo die x- und y-Koordintaten lägen */
			var xPos = kreisRadius + (Math.cos(winkelBogen) * abstand);
			var yPos = kreisRadius + (Math.sin(winkelBogen) * abstand);
		
			ok = true;

			/* Alle bereits vorhandenen Punkte durchgehen, ob ihr Abstand zum aktuellen Punkt niedriger ist als der Minimalwert. */
			for(var i = 0; i < tempArrayKegel.length; i++){
				var xAbstand = Math.abs(tempArrayKegel[i]["x"] - xPos);
				var yAbstand = Math.abs(tempArrayKegel[i]["y"] - yPos);
				
				/* Abstände berechnen (Satz des Pythagoras) */
				var abstand = Math.sqrt(xAbstand * xAbstand + yAbstand * yAbstand);
				
				if (abstand < minimalerAbstand){  ok = false;  }
				
			}
		
		}
	
		/* Die Abstände sind OK -> Punkt aufnehmen */
		var temp = new Array();
		temp["x"] = xPos;
		temp["y"] = yPos;
		temp["status"] = true;
		
		tempArrayKegel.push(temp);
	
	}
	
	return tempArrayKegel;
}

/* Zufallsfunktion */
function random(min, max){
	return Math.floor(Math.random() * (max-min + 1)) + min;
}

/* Randys Zug*/
function randyZug(){
	status = 0;
	
	draw();
	log("\n -- Randys Zug -- ");
	
	/* Zufälligen Winkel v generieren */
	var v = random(0,360);
	var vBogen = (v * (Math.PI / 180));
	
	log("Winkel v: " + v);
	
	/* Zufälligen Punkt x genierieren */
	var a = random(0,360);
	var aBogen = (a * (Math.PI / 180));
	
	kreisRadius = parseInt(document.getElementById("inputKreisRadius").value);
	
	var abstand = random(1,100)/(100/kreisRadius);
	
	// Punkt P : (pX | pY)
	var pX = Math.cos(aBogen) * abstand;
	var pY = Math.sin(aBogen) * abstand;
	
	pX += kreisRadius;
	pY += kreisRadius;
	
	log("Punkt x: (" + pX + "|" + pY + ")");
	
	/* Geradengleichung errechnen */
	
	var m = Math.tan(vBogen);
	var a = pY - m * pX;
	
	log("Geradengleichung: y = " + m + "* x + " + a);
	
	/* Wurf ausführen */
	punkteAktuelleRunde["randy"] += wurf(m, a);
	
	status = 1;
	
	log(" - Randys Zug beendet, warte einen Moment ...");
	
	setTimeout(draw, parseInt(document.getElementById("inputWartezeit").value));
	
	log("Warte auf Eingabe des Wurfes durch den Benutzer....");
	
}

/* Befinden sich noch Kegel auf dem Spielfeld? */
function checkFeldLeer(){

	var leer = true;
	
	for(var i = 0; i < arrayKegel.length; i++){
		if (arrayKegel[i]["status"] == true) { leer = false; }
	}
	
	if (leer){
		log("\n\nAuf dem Spielfeld befinden sich keine Kegel mehr. Starte eine neue Runde.");
		newRound();
		return false;
	}else{
		return true;
	}
}

/* Wurf ausführen */
function wurf(m, a, isAnna){

	// Wurf entlang einer Geraden y = m * x + a
	// boolaen isAnna := wirft Anna?
	
	var kegelUmgeworfen = 0;
	
	var durchmesserKugel = parseInt(document.getElementById("inputDurchmesserKugel").value);
	
	for (var i = 0; i < arrayKegel.length; i++){
		if (arrayKegel[i]["status"]){
		
			/* Koordinaten des aktuellen Kegels ermitteln */
			var px = arrayKegel[i]["x"];
			var py = arrayKegel[i]["y"];

			/* 1. Ermittle den Fusspunkt (fx | fy)
			= Schnittpunkt der gegebenen Geraden mit der Normalen, 
			die durch den Punkt (Kegel) geht */
			var fx = ( m * py + px - a * m) / (m * m +1);
			var fy = m * fx + a;
			
			/* 2. Abstand zwischen Kegel und Fusspunkt
			= wurzel ( delta x² + delta y² ) --> Satz des Pythagoras */
			
			var dx = px - fx;
			var dy = py - fy;
			
			var abstand = Math.sqrt( (dx * dx) + (dy * dy));

			if (abstand <= durchmesserKugel){
				arrayKegel[i]["status"] = false;
				log("Kegel " + (i+1) +  " getroffen (Abstand: " + abstand + ")");
				kegelUmgeworfen++;
			}

		}
	}
	
	/* Grafische Ausgabe */
	
	var canvas = document.getElementById("canvas");
	var ctx = canvas.getContext("2d");

	var kreisRadius = parseInt(document.getElementById("inputKreisRadius").value);
	var faktor = canvas.width/(2 * kreisRadius);

	var wurfFarbe = (isAnna) ? document.getElementById("inputWurfFarbeAnna").value : document.getElementById("inputWurfFarbeRandy").value;
	
	/* Dünne Linie */
	ctx.beginPath();
	ctx.lineWidth = 1;
	ctx.strokeStyle= wurfFarbe;
	ctx.globalAlpha = 1;
	ctx.moveTo(0, faktor * a);
	ctx.lineTo(faktor * 100, faktor * (m * 100 + a));
	ctx.stroke();
	
	/* Dickere, durchsichtigere Linie */
	ctx.beginPath();
	ctx.lineWidth = 2 * faktor * durchmesserKugel;
	ctx.strokeStyle=wurfFarbe;
	ctx.globalAlpha = 0.5;
	ctx.moveTo(0, faktor * a);
	ctx.lineTo(faktor * 100, faktor * (m * 100 + a));
	ctx.stroke();
	
	log("Es wurden " + kegelUmgeworfen + " Kegel umgeworfen.");
	
	return kegelUmgeworfen;
}

/* draw - Zeichnen des Kreises mit Punkten, Kegel- und Rundentabelle füllen */
function draw(){

	if (!(checkFeldLeer())) return false;
	
	var canvas = document.getElementById("canvas");
	var canvasContext = canvas.getContext("2d");

	var kreisRadius = parseInt(document.getElementById("inputKreisRadius").value);
	var kegelRadius = parseInt(document.getElementById("inputRadiusKegel").value);
	var kreisFarbe = document.getElementById("inputKreisFarbe").value;
	var kegelFarbe = document.getElementById("inputKegelFarbe").value;
	
	/* Darstellungsfaktor errechnen */
	var faktor = canvas.width/(2 * kreisRadius);
	
	/* ---- Canvas-Element zeichnen ---- */
	
	/* Zeichenfläche leeren */
	canvasContext.clearRect(0, 0, canvas.width, canvas.height);
	
	canvasContext.beginPath();
	
	canvasContext.lineWidth = 1;
	canvasContext.globalAlpha = 1;
	
	canvasContext.strokeStyle = kreisFarbe;
	canvasContext.arc(faktor * kreisRadius, faktor * kreisRadius, faktor * kreisRadius, 0, 2*Math.PI);
	canvasContext.stroke();
	
	/* Kegel-Box ermitteln, um diese mit den Koordinaten der Punkte zu füllen */
	var kegelBox = document.getElementById("kegelBox");
	kegelBox.value = "Noch nicht umgeworfene Kegel:";
	
	for(var i = 0; i < arrayKegel.length; i++){
		if (arrayKegel[i]["status"]){
			canvasContext.beginPath();
			canvasContext.strokeStyle = kegelFarbe;
			canvasContext.moveTo(faktor * arrayKegel[i]["x"] + kegelRadius, faktor * arrayKegel[i]["y"]);
			canvasContext.arc(faktor * arrayKegel[i]["x"], faktor * arrayKegel[i]["y"], kegelRadius, 0, 2*Math.PI);
			canvasContext.stroke();
			
			// Kegel-Box mit Koordinaten füllen
			kegelBox.value += "\n" + (i+1) + "	( " + arrayKegel[i]["x"] + "| " + arrayKegel[i]["y"] + " )";
		}
	}
	
	/* Unten in die Darstellungsfläche die groben Informationen schreiben: Kreisradius und Kegelanzahl */
	
	var textabstand = 10;
	var fontsize = 10;
	
	canvasContext.beginPath();
	canvasContext.font = fontsize + "px Verdana";
	canvasContext.fillText("r = " + kreisRadius + "  a = " + arrayKegel.length, 10, canvas.height - textabstand);
	canvasContext.stroke();
	
	/* ---- Runden-Übersicht zeichnen ---- */
	var roundBox = document.getElementById("roundBox");
	roundBox.readOnly = true;
	roundBox.style.resize = 'none';
	roundBox.value = "Runde	Anna	Randy";
	roundBox.value += "\n-- gespielte Runden --";
	
	var punkteRandy = 0;
	var punkteAnna = 0;
	
	for(var i = 0; i < punkteRunden.length; i++){
		roundBox.value += "\n" + (i + 1) + "	" + punkteRunden[i]["anna"] + "	" + punkteRunden[i]["randy"];
		punkteRandy += punkteRunden[i]["randy"];
		punkteAnna += punkteRunden[i]["anna"];
	}
	
	/* Noch keine Runden gespielt? -> Platzhalter */
	if (punkteRunden.length == 0){
		roundBox.value += "\n -/-";
	}
	
	roundBox.value += "\n-- aktuelle Runde --";
	roundBox.value += "\n" + (punkteRunden.length +1) + "	" + punkteAktuelleRunde["anna"] + "	" + punkteAktuelleRunde["randy"];
	
	punkteAnna += punkteAktuelleRunde["anna"];
	punkteRandy += punkteAktuelleRunde["randy"];
	
	roundBox.value += "\n-- Insgesamt-Sieger --";
	roundBox.value += "\n";
	
	if (punkteAnna > punkteRandy){
		roundBox.value += "Anna"
	}else if(punkteAnna < punkteRandy){
		roundBox.value += "Randy"
	}else{
		roundBox.value += "Unent."
	}
	
	roundBox.value += "	" + punkteAnna + "	" + punkteRandy;
	
	/* Sonstige Eigenschaften der Boxen */
	document.getElementById("logBox").readOnly = true;
	document.getElementById("logBox").style.resize = 'none';

	document.getElementById("kegelBox").readOnly  = true;
	document.getElementById("kegelBox").style.resize = 'none';
	
	/* Status-Abhängige Eigenschaften */
	
	if (status == 0){
		/* Weder Wurf noch Entscheidung erlaubt */
		//Wurf deaktivieren
		document.getElementById("canvas").removeEventListener("click", canvasClick, false);
		document.getElementById("canvas").style.cursor = 'no-drop';
		//Entscheidung deaktivieren
		document.getElementById("endRoundButton").disabled = true;
		document.getElementById("noEndRoundButton").disabled = true;
		//Phasen-Überschrift
		document.getElementById("phase").innerHTML = "Phase <b>0</b>: Randy's Wurf";
	}else if(status == 1){
		/* Nur Wurf erlaubt */
		//Wurf aktivieren
		document.getElementById("canvas").addEventListener("click", canvasClick, false);
		document.getElementById("canvas").style.cursor = 'crosshair';
		//Entscheidung deaktivieren
		document.getElementById("endRoundButton").disabled = true;
		document.getElementById("noEndRoundButton").disabled = true;
		//Phasen-Überschrift
		document.getElementById("phase").innerHTML = "Phase <b>1</b>: Annas Wurf";
	}else{
		/* Nur Entscheidung erlaubt */
		// Wurf deaktivieren
		document.getElementById("canvas").removeEventListener("click", canvasClick, false);
		document.getElementById("canvas").style.cursor = 'no-drop';
		// Entscheidung aktivieren
		document.getElementById("endRoundButton").disabled = false;
		document.getElementById("noEndRoundButton").disabled = false;
		//Phasen-Überschrift
		document.getElementById("phase").innerHTML = "Phase <b>2</b>: Annas Entscheidung";
	}
	
	return false;

}

/* Event-Handler für einen Click auf die Canvas-Zeichenfläche*/
function canvasClick(event){

	var canvasTemp = document.getElementById("canvas");
	var canvasTempContext = canvasTemp.getContext("2d");
	
	/* Maus-Position ermitteln */
	var xPos = event.pageX - canvasTemp.offsetLeft;
	var yPos = event.pageY - canvasTemp.offsetTop;
	
	draw();

	if (status == 1){
	
		if (punkteEingabe == null){
			punkteEingabe = new Array();
		}
		
		var temp = new Array();
		temp["x"] = xPos;
		temp["y"] = yPos;
		punkteEingabe.push(temp);
		
		log("Eingabe am Punkt (" + xPos + "|" + yPos + ") registriert und gespeichert als Punkt " + punkteEingabe.length);
		
		/* Eingegebener Punkt grafisch ausgeben */
		
		var canvas = document.getElementById("canvas");
		var ctx = canvas.getContext("2d");
		var kegelRadius = parseInt(document.getElementById("inputRadiusKegel").value);
		var faktor = canvas.width/(2 * kreisRadius);
	
		for (var i = 0; i < punkteEingabe.length; i++){
			ctx.beginPath();
			ctx.strokeStyle = document.getElementById("inputEingabeFarbe").value;
			ctx.moveTo(punkteEingabe[i]["x"] + kegelRadius, punkteEingabe[i]["y"]);
			ctx.arc(punkteEingabe[i]["x"], punkteEingabe[i]["y"], kegelRadius, 0, 2*Math.PI);
			ctx.stroke();
		}
		
		if (punkteEingabe.length == 1){
			log("Erster Punkt gesammelt.");
			
		}else if(punkteEingabe.length == 2){
			log("Zwei Punkte gesammelt. Führe Annas Zug aus.");
			
			/* Annas Wurf ausführen */

			/* Punkte ermitteln und in Koordinatensystem umrechnen */
			var x1 = punkteEingabe[0]["x"] / faktor;
			var y1 = punkteEingabe[0]["y"] / faktor;
			
			var x2 = punkteEingabe[1]["x"] / faktor;
			var y2 = punkteEingabe[1]["y"] / faktor;
			
			/* Die zwei Punkte löschen */
			punkteEingabe = null;
			
			/* Geradengleichung errechnen */
			// y = m*x+a
			
			//Steigung m errechnen
			var dX = x1 - x2;
			var dY = y1 - y2;
			
			var m = dY/dX;

			//y-Achsen-Verschiebung a errechnen
			var a = y1-(m * x1);
			
			log("Geradengleichung: y = " + m + " * x + " + a);

			/* Wurf ausführen */
			punkteAktuelleRunde["anna"] += wurf(m, a, true);
			
			status = 2;
			
			setTimeout(draw, parseInt(document.getElementById("inputWartezeit").value)); 
			log("Warte auf Entscheidung des Benutzers...");
			
		}else{
			/* sehr sonderbar. Alles löschen. */
			punkteEingabe = new Array();
			log("Fehler: Zu viele Punkte. Wiederholen Sie die Eingabe.");
		}
		
	}else{
		log("Klick registriert, aber Anna ist nicht dran mit werfen.");
	}
}