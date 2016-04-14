#!/bin/bash

# jdk suchen
jdk=
if [ "$JDK_HOME" == "" ]
then
	jdk="$JAVA_HOME"
else
	jdk="$JDK_HOME"
fi

# jdk überprüfen
if [ ! -r "$jdk/lib/tools.jar" ] || [ ! -e "$jdk/jre/bin/java" ]
then
	echo "Fehler: Kann das Installationsverzeichnis der JDK nicht finden! \$JDK_HOME oder \$JAVA_HOME müssen auf ein gültiges JDK-Installationsverzeichnis zeigen!"
	echo "        Aktuelles Verzeichnis: $jdk"
	exit 1
fi

# java 8 benötigt
version=$("$jdk/jre/bin/java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "$version" < "1.8" ]]
then
	echo "Fehler: Java 8 benötigt! Aktuelle Java-Version is $version."
	exit 1
fi

"$jdk/jre/bin/java" -cp "bin/Aufgabe2-PanoramaKegeln.jar:lib/*:$jdk/lib/tools.jar" bwinf33_2.aufgabe2.ai.AiObfuscator ${@}
