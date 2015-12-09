@echo off
SET anzahlAmeisen=100
SET anzahlFutterquellen=5
SET	verdunstungszeit=300
SET nestPositionX=249
SET nestPositionY=249

java -jar %~dp0\a2.jar %anzahlAmeisen% %anzahlFutterquellen% %verdunstungszeit% %nestPositionX% %nestPositionY%
pause