Funktionalität zurzeit:

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
- Erfahrungspunkte und Fertigkeiten, Fertigkeitsmenü kann mit S geöffnet werden. Von beiden Fertigkeiten gibt es verbesserte Varianten, die die vorangehende Variante voraussetzt.
- Erfahrungspunkte werden durch getötete Gegner oder Quests (sehr lukrativ) erlangt.
- Schere-Stein-Papier-Prinzip 
- Quests: Zur Zeit zwei Typen, einmal wird die Anzahl der einzusammelnden Münzen explizit genannt, einmal durch eine Rechenaufgabe. Entsprechend müssen die Münzen gesammelt werden und zurückgebracht werden
- Netzwerk: Server muss sich zuerst anmelden, wenn sich dann auch Client einloggt kann kommuniziert werden, wobei testweise einmal die Levelnummer vom Server zum Client geschickt wird, das funktioniert soweit

**********************************************************************************************************************************************************************************
TODO:

- Netzwerk (Deathmatch)
- Chat
- Speichern und Laden von Spielständen



Hinweis:

(Saskia:)
Ich habe mal testweise Sound eingebaut, wobei dies zuerst wieder nur aus dem bin-Ordner geladen werden konnte. Zurzeit ist es so, dass man in Eclipse ein bisschen die Einstellungen ändern muss und zwar so:
Run -> Run Configurations -> ClassPath -> Select User Entries -> advanced -> add Folders -> resources auswählen.
Dann sollte es eigentlich starten, wobei der resources-Eintrag glaube ich über dem Eintrag "Gruppe14" platziert sein muss.

Die Klassen Server, Client und Connection bestehen zur Zeit lediglich aus Zusammentragungen von Ansätzen aus den verschiedensten Quellen, hier ist noch nichts lauffähig oder durchdacht.