/*

	Panorama-Kegeln
	Tim Hollmann
	33.BwInf 2014/'15
	2.Runde
	Aufgabe 2.1

*/


function main(){
	
	/* Zeichenfläche und ihren 2D-Kontext laden */
	var canvas = document.getElementById("canvas");
	var ctx = canvas.getContext("2d");

	/* Sonstige Einstellungen laden */
	var anzahlKegel 	= parseInt(document.getElementById("anzahlKegel").value);
	var kreisRadius		= parseInt(document.getElementById("kreisRadius").value);

	var kreisFarbe		= document.getElementById("kreisFarbe").value;
	var kegelFarbe		= document.getElementById("kegelFarbe").value;
	var kegelRadius		= parseInt(document.getElementById("kegelRadius").value);
	
	/* Darstellungsfaktor anhand der Einstellungen errechnen */
	var faktor = canvas.width/(2 * kreisRadius);

	/* Punkte generieren */
	var kegel = new Array(createRandomPoints(kreisRadius, anzahlKegel));

	/* Kreis mit Punkten zeichnen */
	draw(canvas, ctx, faktor, kreisRadius, kegel, kreisFarbe, kegelFarbe, kegelRadius);
}

function draw(canvas, canvasContext, faktor, kreisRadius, kegel, kreisFarbe, kegelFarbe, kegelRadius){
	
	/* Variablen für die Beschriftung des Canvas */
	var textabstand = 10;
	var fontsize = 10;
	
	/* Die Punkte und ihre Koordinaten werden zusätzlich noch in eine Tabelle eingetragen;
	diese wird ermittelt, geleert und mit der Überschrift gefüllt */
	var kegelBox = document.getElementById("kegelBox");
	kegelBox.value = "Kegel:";
	
	
	kegel = kegel[0];
	
	/* Zeichenfläche leeren */
	canvasContext.clearRect(0, 0, canvas.width, canvas.height);

	/* Den Kreis zeichnen */
	canvasContext.beginPath();
	canvasContext.strokeStyle = kreisFarbe;
	canvasContext.arc(faktor * kreisRadius, faktor * kreisRadius, faktor * kreisRadius, 0, 2*Math.PI);
	canvasContext.stroke();
	
	/* Die Punkte aus dem Array auslesen und zeichnen */
	for(var i = 0; i < kegel.length; i++){
	
		canvasContext.beginPath();
		canvasContext.strokeStyle = kegelFarbe;
		canvasContext.moveTo(faktor * kegel[i]["x"] + kegelRadius, faktor * kegel[i]["y"]);
		canvasContext.arc(faktor * kegel[i]["x"], faktor * kegel[i]["y"], kegelRadius, 0, 2*Math.PI);
		canvasContext.stroke();
		
		/* Die Punkte und ihre Koordinaten werden zusätzlich noch in eine Tabelle eingetragen */
		kegelBox.value += "\n" + (i+1) + ":	(" + kegel[i]["x"] + "|" + kegel[i]["y"] + ")";
		
	}
	
	/* Unten in die Darstellungsfläche die groben Informationen schreiben: Kreisradius und Kegelanzahl */
	canvasContext.beginPath();
	canvasContext.font = fontsize + "px Verdana";
	canvasContext.fillText("r = " + kreisRadius + "  a = " + kegel.length, 10, canvas.height - textabstand);
	canvasContext.stroke();
	
}

/* Eine betimmte Anzanl an Punkten zufällig ung gleichmäßig erzeugen */
function createRandomPoints(kreisRadius, anzahlKegel){

	/* Temporäres Array zum Sammeln der Punkte */
	var tempArrayKegel = new Array();
	
	/* Kreisfläche ausrechnen */
	var kreisflaeche = Math.PI * kreisRadius * kreisRadius;
	
	/* Kreisfläche pro Kegel */
	var flaecheProKegel = kreisflaeche / anzahlKegel;
	
	/* Minimaler Abstand berechnen */
	var minimalerAbstand = Math.sqrt(flaecheProKegel / Math.PI) * 1.35;
	
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
		
		tempArrayKegel.push(temp);
	
	}
	
	return tempArrayKegel;
}

/* Zufallsfunktion */
function random(min, max){
	return Math.floor(Math.random() * (max-min + 1)) + min;
}

/* Das erzeugte Bild downloaden */
function saveCanvas(){
	var canvas = document.getElementById("canvas");
	window.location = canvas.toDataURL("image/png").replace("image/png", "image/octet-stream");
}