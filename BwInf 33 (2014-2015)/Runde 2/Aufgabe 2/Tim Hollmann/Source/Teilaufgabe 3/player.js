/*
Player Skript
Tim Hollmann, 33.BwInf
Aufgabe 2.3
*/

var isPlaying = true;

var interval = 0;

var currentFrame = 0;
var playerCounter = interval;

var imageBox = document.getElementById("imageBox");

var gameID = parseInt(document.getElementById("gameID").value);
var anzahlFrames = parseInt(document.getElementById("anzahlFrames").value);

setInterval("playerTick()", 10);

function playerTick(){
	playerCounter++;
	
	document.getElementById("playPauseButton").innerHTML = (isPlaying) ? "Pause" : "Abspielen";
	
	interval = parseInt(document.getElementById("intervalInput").value);
	
	if (isPlaying){
		if (playerCounter % interval == 0){
			currentFrame++;
			if (currentFrame > anzahlFrames){ currentFrame = 1; }
			loadImage();
		}
	}
}

function loadImage(){
	imageBox.src = gameID + "/" + currentFrame + ".png";
	document.getElementById("imageIndicator").innerHTML = currentFrame + "/" + anzahlFrames;
	
	//Log-Box
	xmlHttpObjectLog.open('get', gameID + "/" + currentFrame + "_log.txt");
    xmlHttpObjectLog.onreadystatechange = handleContentLog;
    xmlHttpObjectLog.send(null);
	
	//Punkte-Box
	xmlHttpObjectPoints.open('get', gameID + "/" + currentFrame + "_points.txt");
    xmlHttpObjectPoints.onreadystatechange = handleContentPoints;
    xmlHttpObjectPoints.send(null);
}

function handleContentLog(){
	if (xmlHttpObjectLog.readyState == 4){
        document.getElementById('logBox').innerHTML = xmlHttpObjectLog.responseText;
    }
}

function handleContentPoints(){
	if (xmlHttpObjectPoints.readyState == 4){
        document.getElementById('pointsBox').innerHTML = xmlHttpObjectPoints.responseText;
    }
}
function previousFrame(){
	
	currentFrame = (currentFrame == 1) ? anzahlFrames : currentFrame -1;
	loadImage();
}

function nextFrame(){
	currentFrame = (currentFrame == anzahlFrames) ? 1 : currentFrame + 1;
	loadImage();
}

//Ajax für Log-Box

var xmlHttpObjectLog = false;

if (typeof XMLHttpRequest != 'undefined') 
{
    xmlHttpObjectLog = new XMLHttpRequest();
}

if (!xmlHttpObjectLog) 
{
    try 
    {
        xmlHttpObjectLog = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch(e) 
    {
        try 
        {
            xmlHttpObjectLog = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch(e) 
        {
            xmlHttpObjectLog = null;
        }
    }
}

var xmlHttpObjectPoints = false;

if (typeof XMLHttpRequest != 'undefined') 
{
    xmlHttpObjectPoints = new XMLHttpRequest();
}

if (!xmlHttpObjectPoints) 
{
    try 
    {
        xmlHttpObjectPoints = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch(e) 
    {
        try 
        {
            xmlHttpObjectPoints = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch(e) 
        {
            xmlHttpObjectPoints = null;
        }
    }
}