#!/bin/bash

# überprüfen, dass apcalc installiert ist
if [ ! -f "/usr/bin/calc" ]; then
  echo "Konnte Befehl \"calc\" nicht finden!"
  echo "Bitte installieren Sie das Packet \"apcalc\", etwa so:"
  echo -e "\tDebian/Ubuntu: # apt-get install apcalc"
  echo -e "\topenSUSE:      # zypper install apcalc"
fi

# diese Variable enthält den maximalen Speicherwert in MiB
MAXSTORE=$(calc 2*1024)

# Argument lesen
if [ "$1" == "" ]; then
  echo "Achtung: kein maximaler Speicherverbrauch angegeben! Benutze ${MAXSTORE:1} MiB."
else
  MAXSTORE=" $1"
fi

# Soll-Speicherverbrauch berechnen
STOREBOUNDARY=$(calc ${MAXSTORE:1}-500)

# Programm aufrufen
java "-Xmx${MAXSTORE:1}m" "-Dtreebuilding.maxstore=${STOREBOUNDARY:1}" "buschfeuer.Buschfeuer"
