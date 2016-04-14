#!/bin/bash

# jre suchen
jre=
if [ "$JRE_HOME" == "" ]
then
	if [ "$JAVA_HOME" == "" ]
	then
		jre="$JDK_HOME"
	else
		jre="$JAVA_HOME"
	fi
else
	jre="$JRE_HOME"
fi

# falls jdk, jre benutzen
if [ -d "$jre/jre" ]
then
	jre="$jre/jre"
fi

# jre überprüfen
if [ ! -e "$jre/bin/java" ]
then
	echo "Fehler: Kann das Installationsverzeichnis der JRE nicht finden! \$JRE_HOME, \$JAVA_HOME oder \$JDK_HOME müssen auf ein gültiges JRE- oder JDK-Installationsverzeichnis zeigen!"
	echo "        Aktuelles Verzeichnis: $jre"
	exit 1
fi

# java 8 benötigt
version=$("$jre/bin/java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "$version" < "1.8" ]]
then
	echo "Fehler: Java 8 benötigt! Aktuelle Java-Version is $version."
	exit 1
fi

"$jre/bin/java" -cp "bin/Aufgabe2-PanoramaKegeln.jar:lib/*" bwinf33_2.aufgabe2.Main ${@}
