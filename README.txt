Funktionalit�t zurzeit:

- Steuerung eines Spielers
- Erkennung des Untergrunds
- Tod bei Betreten des Feuers (sinnvolle Ausgabe und M�glichkeit zum Neustart fehlt)
- Betreten eines neuen Levels bei Erreichen der T�r
- Sieg durch Erreichen der Troph�e
- Bewegung durch Mauer nicht mehr m�glich, Methode aber noch n bisschen verbuggt
- Einlesen der Karte aus txt.Datei
- Men�f�hrung mit zwei Buttons, funktioniert bei Neustart noch nicht
- Gegner verfolgt Spieler (KI)
- Coins
- Anzeige der Anzahl der Coins, hier k�nnen auch einfach weitere Informationen eingearbeitet werden
- Shop, Betreten durch Dr�cken von ENTER
- Bek�mpfen mit x - entweder im Kreis oder in die Richtung, in die man l�uft
- Zaubern mit c - entweder im Kreis oder in die Richtung, in die man l�uft, danach kann sich der Gegner, der getroffen wird nicht mehr bewegen
- verschiedene TileSets f�r die Level
- Items
- Checkpoints
- NPC, reden mit ENTER
- Sound
- Erfahrungspunkte und Fertigkeiten, Fertigkeitsmen� kann mit S ge�ffnet werden. Von beiden Fertigkeiten gibt es verbesserte Varianten, die die vorangehende Variante voraussetzt.
- Erfahrungspunkte werden durch get�tete Gegner oder Quests (sehr lukrativ) erlangt.
- Schere-Stein-Papier-Prinzip 
- Quests: Zur Zeit zwei Typen, einmal wird die Anzahl der einzusammelnden M�nzen explizit genannt, einmal durch eine Rechenaufgabe. Entsprechend m�ssen die M�nzen gesammelt werden und zur�ckgebracht werden
- Netzwerk: Server muss sich zuerst anmelden, wenn sich dann auch Client einloggt kann kommuniziert werden, wobei testweise einmal die Levelnummer vom Server zum Client geschickt wird, das funktioniert soweit

**********************************************************************************************************************************************************************************
TODO:

- Netzwerk (Deathmatch)
- Chat
- Speichern und Laden von Spielst�nden



Hinweis:

(Saskia:)
Ich habe mal testweise Sound eingebaut, wobei dies zuerst wieder nur aus dem bin-Ordner geladen werden konnte. Zurzeit ist es so, dass man in Eclipse ein bisschen die Einstellungen �ndern muss und zwar so:
Run -> Run Configurations -> ClassPath -> Select User Entries -> advanced -> add Folders -> resources ausw�hlen.
Dann sollte es eigentlich starten, wobei der resources-Eintrag glaube ich �ber dem Eintrag "Gruppe14" platziert sein muss.

Die Klassen Server, Client und Connection bestehen zur Zeit lediglich aus Zusammentragungen von Ans�tzen aus den verschiedensten Quellen, hier ist noch nichts lauff�hig oder durchdacht.