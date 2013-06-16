FunktionalitÃ¤t zurzeit:

- Steuerung eines Spielers
- Erkennung des Untergrunds
- Tod bei Betreten des Feuers (sinnvolle Ausgabe und Möglichkeit zum Neustart fehlt)
- Betreten eines neuen Levels bei Erreichen der Tür
- Sieg durch Erreichen der Trophäe
- Bewegung durch Mauer nicht mehr möglich, Methode aber noch n bisschen verbuggt
- Einlesen der Karte aus txt.Datei
- Menüführung mit zwei Buttons, funktioniert bei Neustart noch nicht
- Gegner verfolgt Spieler (KI)
- Coins
- Anzeige der Anzahl der Coins, hier können auch einfach weitere Informationen eingearbeitet werden
- Shop, Betreten durch Drücken von ENTER
- Bekämpfen mit x - entweder im Kreis oder in die Richtung, in die man läuft
- Zaubern mit c - entweder im Kreis oder in die Richtung, in die man läuft, danach kann sich der Gegner, der getroffen wird nicht mehr bewegen
- verschiedene TileSets für die Level
- Items
- Checkpoints
- NPC, reden mit ENTER
- Sound

TODO:

- schönere Bilder
- Ausarbeitung von Leveln - man darf nach dem Betreten der Tür nicht direkt in Feuer laufen
- Räume/Level


Hinweis:

Ich habe mal testweise Sound eingebaut, wobei dies zuerst wieder nur aus dem bin-Ordner geladen werden konnte. Zurzeit ist es so, dass man in Eclipse ein bisschen die Einstellungen ändern muss und zwar so:
Run -> Run Configurations -> ClassPath -> Select User Entries -> advanced -> add Folders -> resources auswählen.
Dann sollte es eigentlich starten, wobei der resources-Eintrag glaube ich über dem Eintrag "Gruppe14" platziert sein muss.