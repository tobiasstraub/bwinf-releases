#!/bin/bash

echo '<RCC>' >examples.qrc
echo '  <qresource prefix="/examples">' >>examples.qrc
for i in {0..6}
do
    echo -n "Downloading example $i ..."
    wget -q -O ".tmp.txt" "http://www.bundeswettbewerb-informatik.de/uploads/media/yamyams$i.txt"
    height=$(cat ".tmp.txt" | wc -l)
    chars=$(cat ".tmp.txt" | wc -c)
    width=$(( chars / height - 2 ))
    echo "$width $height" >"$i.txt"
    cat ".tmp.txt" >>"$i.txt"
    echo "    <file alias=\"$i\">$i.txt</file>" >>examples.qrc
    echo " done"
done
echo '  </qresource>' >>examples.qrc
echo '</RCC>' >>examples.qrc
