import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JRadioButton;

public class GamePanel extends JPanel  implements Runnable, KeyListener{
	private static final long serialVersionUID = 1L;
	JFrame frame;
	JFrame frame2;
	JFrame frame3;
	JFrame frame4;
	JFrame frame5;
	JFrame frame6;
	JFrame f;
	JFrame chat = new JFrame ("Chat");
	JFrame shop2 = new JFrame("Shop");
	JFrame skills = new JFrame ("Skills");
	
	JEditorPane chatPane;
	JTextField chatarea = new JTextField();
	long delta = 0;
	long last = 0;
	long fps = 0;
	long gameover = 0;
	
	final GamePanel p = this;
	
	Server server;
	Client client; 
	
	SpriteLib lib;
	SoundLib soundlib;
	Player player;
	Player player2;
	
	Enemy enemy;
	Enemy enemy2;
	Enemy boss;
	Enemy enemy3;
	Enemy enemy4;
	Enemy enemy5;
	Enemy enemy6;
	Enemy enemy7;

	Item coin;
	Item coin2;
	Item coin3;
	Item coin4;
	Item coin5;
	Item coin6;
	Item coin7;
	Item coin8;
	Item coin9;
	Item coin10;
	Item mana;
	Item shop;
	Item npc;
	Item schwerteis;
	Item schwertfeuer;
	Item schildeis;
	Item schildfeuer;
	Item healthpack;
	Item healthpack2;

	Quest quest;
	Quest quest2;
	
	MapDisplay map;
	
	CopyOnWriteArrayList<Object> attacks;	//Liste für die Attack-Objekte
	CopyOnWriteArrayList<Sprite> actors; //beinhaltet alle Objekte im Spiel, also Gegner, Items etc.

	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean attack;	//Spieler1 will attackieren
	boolean attack2; //Spieler2 will attackieren
	boolean waitingForKeyPress;
	boolean game_running = true;
	boolean started = false;
	boolean shopmode = false; //Ist Spieler im Shop oder nicht?
	boolean skillmode = false;	//Ist der Spieler im Fertigkeitsbaum, oder nicht?
	boolean enterShop = false;
	boolean enterNPC = false;
	boolean talkwithnpc = false;
	boolean magic = false; //Will Spieler zaubern?
	boolean inquest = false;
	boolean multiplayer;
	boolean singleplayer;
	boolean serverMode;
	boolean clientMode;
	boolean chatmode;
	boolean sound_running = false;
	String out;
	
	int spiel_status = 3; // 0 = Verloren, 1 = Gewonnen, 2 = Pause, 3 = noch nicht gestartet;
	int pressCount;
	int speed = 80;
	int x = 0;
	int y = 0;
	int checkpointx;
	int checkpointy;
	int room; //zeigt an im wievielten Raum man sich im Level befindet, möglich sind 1, 2 oder 3
	int level; //zeigt an im wievielten Lebel man ist, möglich sind 1, 2 oder 3
	
	boolean skillsOnce;	//Wurde das Skills-Menü bereits initialisiert?
	JButton t1;			//Buttons für das Skill-Menü
	JButton t2;
	JButton t3;
	JButton t4;
	JButton t5;
	
	static int rows, columns;
	
	/**
	 * Mainmethode, die lediglich ein GamePanel generiert
	 */
	public static void main(String[] args){
		new GamePanel(790,630); //Sonst grauer Streifen an den Rändern rechts und unten
	}
	
	/**
	 * Konstruktor der GamePanel-Klasse. Hier wird das Hauptfenster generiert und der Spielthread gestartet. Außerdem wird das Menü gezeichnet
	 * @param w Breite des Fensters
	 * @param h Höhe des Fensters
	 */
	public GamePanel(int w, int h){
		
		this.setPreferredSize(new Dimension(w,h));
		this.setBackground(Color.darkGray);
		frame = new JFrame("Dungeon MYS");
		frame.setLocation(300,50);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.addKeyListener(this);
		
		paintMenu();
				
		Thread t = new Thread(this);
		t.start(); //ruft run auf
		
	}

	/**
	 * In der doInitializations-Methode werden grundlegende Objekte zum Spielstart instanziert
	 * Insbesondere werden die Spieler und Gegner generiert, die Karte geladen etc.
	 * @param menu Fenster, welches geschlossen wird
	 */
	public void doInitializations(JFrame menu){
		up = false;
		down = false;
		left = false;
		right = false; //sonst läuft Spieler nach Neustart einfach los
		attack = false;
		attack2 = false;
		
		level = 1;
		room = 1;
		last = System.nanoTime();
		gameover = 0;
		
		actors = new CopyOnWriteArrayList<Sprite>();
		attacks = new CopyOnWriteArrayList<Object>();
		
		lib = SpriteLib.getInstance();
		
		
		soundlib = new SoundLib();
		soundlib.loadSound("Hintergrund", "sound/Greensleeves.wav");
		soundlib.loadSound("Angriff", "sound/Angriff.wav");
		soundlib.loadSound("Zauber", "sound/Zauber.wav");
		if(sound_running == true){
		soundlib.loopSound("Hintergrund");
		}
			
		////1 = Coins, 2 = Mana, 3 = Shop, 4 = Rüstung, 5 = Waffe, 6 = NPC
		player = new Player(lib.getSprite("resources/pics/player.gif", 12, 1), 50, 50, 100, this);
		enemy = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 100, 500, 10, 100, this);
		enemy3 = new IceEnemy(lib.getSprite("resources/pics/Eisgegner.gif", 4, 1), 500, 50, 10, 100, this);
		enemy4 = new FireEnemy(lib.getSprite("resources/pics/Feuergegner.gif", 4, 1), 600, 50, 10, 100, this);
		enemy2 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 300, 200, 10, 100, this);
		coin = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 700, 400, 1, 100, this);
		coin2 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 670, 400, 1, 100, this);
		coin3 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 640, 400, 1, 100, this);
		coin4 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 610, 400, 1, 100, this);
		coin5 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 580, 400, 1, 100, this);
		coin6 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 100, 50, 1, 100, this);
		coin7 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 140, 50, 1, 100, this);
		coin8 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 180, 50, 1, 100, this);
		coin9 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 220, 50, 1, 100, this);
		coin10 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 260, 50, 1, 100, this);
		mana = new Item(lib.getSprite("resources/pics/mana.gif", 1, 1), 470, 500, 2, 100, this);
		shop = new Item(lib.getSprite("resources/pics/shop.gif", 1, 1), 400, 500, 3, 100, this);
		npc = new Item(lib.getSprite("resources/pics/npc.gif", 1, 1), 100, 100, 6, 100, this);
		schildeis = new Item(lib.getSprite("resources/pics/IceArmour.gif", 1, 1), 100, 200, 4, 100, this);
		schildfeuer = new Item(lib.getSprite("resources/pics/FireArmour.gif", 1, 1), 150, 200, 9, 100, this);
		schwerteis = new Item(lib.getSprite("resources/pics/IceSword.gif", 1, 1), 100, 150, 5, 100, this);
		schwertfeuer = new Item(lib.getSprite("resources/pics/FireSword.gif", 1, 1), 150, 150, 10, 100, this);
		healthpack = new Item(lib.getSprite("resources/pics/healthpack.gif", 1, 1), 500, 100, 7, 100, this);
		quest = new Quest(lib.getSprite("resources/pics/quest.gif", 1, 1), 500, 200, 2, 100, this); //Münz-Quest
		quest2 = new Quest(lib.getSprite("resources/pics/quest.gif", 1, 1), 100, 250, 1, 100, this); //Mathe-Münz-Quest

		actors.add(enemy); 
		actors.add(enemy2); 
		actors.add(enemy3);
		actors.add(enemy4); 
		actors.add(coin); 
		actors.add(coin2); 
		actors.add(coin3); 
		actors.add(coin4); 
		actors.add(coin5);
		actors.add(coin6); 
		actors.add(coin7); 
		actors.add(coin8); 
		actors.add(coin9); 
		actors.add(coin10);
		actors.add(mana);
		actors.add(shop);
		actors.add(npc);
		actors.add(schwerteis);
		actors.add(schildeis);
		actors.add(schwertfeuer);
		actors.add(schildfeuer);
		actors.add(healthpack);
		actors.add(quest);
		actors.add(quest2);
		actors.add(player);
		
		checkpointx = 50;
		checkpointy = 50;

		
		player.setOldCoins(100);
		player.setOldXP(0);
		player.setOldMana(0);
		player.setOldWeapon(0);
		player.setOldArmour(0);
		player.setCoins(100);
		player.setXP(0);
		player.setOldMaxhealth(100);
		
		player.setSkillHealth1(false);
		player.setSkillHealth2(false);
		player.setSkillStrength1(false);
		player.setSkillStrength2(false);
		
		player.setOldSkillHealth1(false);
		player.setOldSkillHealth2(false);
		player.setOldSkillStrength1(false);
		player.setOldSkillStrength2(false);
		
		
		skillsOnce = false;
		
		//Erstellen der Karte, wobei die ersten 3 Parameter für die Eingabedateien stehen, die erste Zahl für die Anzahl der Spalten im TileSet, die zweite für die Anzahl der Zeilen
		map = new MapDisplay("resources/level/TileMap_1_1.txt", "resources/pics/tiles_1.gif", "resources/pics/shadow.png", 5, 1, this);
		
		initSkills();
		menu.dispose();
		setStarted(true);
		
	}
	
	/**
	 * In der doInitializations-Methode werden neue Level bzw. Räume geladen und entsprechende Sprites geladen. 
	 * Außerdem sind Checkpoints generiert und es werden die Zwischenstände, wie Anzahl der Münzen etc. gespeichert
	 * @param l Levelnummer, die übergeben werden, um zu bestimmen, welches Level geladen wird
	 * @param r Raumnummer, die übergeben wird, um zu bestimmen, welcher Raum geladen wird
	 */
	public void doInitializations(int l, int r){ //l = level, r = room
		level = l;
		room = r;
		actors.clear(); //Actors-Liste wird gelöscht und dann entsprechend der Information in TileMap_n.txt neu erstellt

		if((level ==1) && (room == 1)){
			map = new MapDisplay("resources/level/TileMap_1_1.txt", "resources/pics/tiles_1.gif", "resources/pics/shadow.png", 5, 1, this);
			enemy = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 100, 500, 10, 100, this);
			enemy2 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 300, 200, 10, 100, this);
			enemy3 = new IceEnemy(lib.getSprite("resources/pics/Eisgegner.gif", 4, 1), 500, 50, 10, 100, this);
			enemy4 = new FireEnemy(lib.getSprite("resources/pics/Feuergegner.gif", 4, 1), 600, 50, 10, 100, this);
			coin = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 700, 400, 1, 100, this);
			coin2 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 670, 400, 1, 100, this);
			coin3 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 640, 400, 1, 100, this);
			coin4 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 610, 400, 1, 100, this);
			coin5 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 580, 400, 1, 100, this);
			coin6 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 100, 50, 1, 100, this);
			coin7 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 140, 50, 1, 100, this);
			coin8 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 180, 50, 1, 100, this);
			coin9 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 220, 50, 1, 100, this);
			coin10 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 260, 50, 1, 100, this);
			mana = new Item(lib.getSprite("resources/pics/mana.gif", 1, 1), 470, 500, 2, 100, this);
			shop = new Item(lib.getSprite("resources/pics/shop.gif", 1, 1), 400, 500, 3, 100, this);
			npc = new Item(lib.getSprite("resources/pics/npc.gif", 1, 1), 100, 100, 6, 100, this);
			schildeis = new Item(lib.getSprite("resources/pics/IceArmour.gif", 1, 1), 100, 200, 4, 100, this);
			schildfeuer = new Item(lib.getSprite("resources/pics/FireArmour.gif", 1, 1), 150, 200, 9, 100, this);
			schwerteis = new Item(lib.getSprite("resources/pics/IceSword.gif", 1, 1), 100, 150, 5, 100, this);
			schwertfeuer = new Item(lib.getSprite("resources/pics/FireSword.gif", 1, 1), 150, 150, 10, 100, this);
			healthpack = new Item(lib.getSprite("resources/pics/healthpack.gif", 1, 1), 500, 100, 7, 100, this);
			quest = new Quest(lib.getSprite("resources/pics/quest.gif", 1, 1), 500, 200, 2, 100, this);
			quest2 = new Quest(lib.getSprite("resources/pics/quest.gif", 1, 1), 500, 200, 1, 150, this); //Mathe-Münz-Quest
			
			actors.add(enemy); 
			actors.add(enemy2); 
			actors.add(enemy3); 
			actors.add(enemy4); 
			actors.add(coin);
			actors.add(coin2); 
			actors.add(coin3); 
			actors.add(coin4); 
			actors.add(coin5);
			actors.add(coin6); 
			actors.add(coin7); 
			actors.add(coin8); 
			actors.add(coin9); 
			actors.add(coin10);
			actors.add(mana);
			actors.add(shop);
			actors.add(npc);
			actors.add(schwerteis);
			actors.add(schildeis);
			actors.add(schwertfeuer);
			actors.add(schildfeuer);
			actors.add(healthpack);
			actors.add(quest);
			actors.add(quest2);
		}else if((level == 1) && (room == 2)){
			
			map = new MapDisplay("resources/level/TileMap_1_2.txt", "resources/pics/tiles_1.gif", "resources/pics/shadow.png", 5, 1, this); 
			coin = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 600, 400, 1, 100, this);
			shop = new Item(lib.getSprite("resources/pics/shop.gif", 1, 1), 380, 480, 3, 100, this);
			enemy = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 50, 450, 10, 100, this);
			enemy2 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 490, 100, 10, 100, this);
			
			actors.add(enemy); 
			actors.add(enemy2); 
			actors.add(coin); 
			actors.add(shop);

		}
		else if((level == 1) && (room == 3)){
			map = new MapDisplay("resources/level/TileMap_1_3.txt", "resources/pics/tiles_1.gif", "resources/pics/shadow.png", 5, 1, this);
			boss = new Enemy(lib.getSprite("resources/pics/boss.gif", 4, 1), 80, 460, 20, 100, this);
			boss.reduceHealth(-100); //health = 200 -(-100) = 300
			actors.add(boss);
		}
		else if((level == 2) && (room == 1)){
			map = new MapDisplay("resources/level/TileMap_2_1.txt", "resources/pics/tiles_2.gif", "resources/pics/shadow.png", 5, 1, this);
			checkpointx = 90;
			checkpointy = 400;
			//Player-Werte sichern für Checkpoint
			player.setOldCoins(player.getCoins());
			player.setOldMaxhealth(player.getMaxhealth());
			player.setOldXP(player.getXP());
			player.setOldMana(player.getMana());
			player.setOldWeapon(player.hasWeapon());
			player.setOldArmour(player.hasArmour());
			
			player.setOldSkillHealth1(player.hasSkillHealth1());
			player.setOldSkillHealth2(player.hasSkillHealth2());
			player.setOldSkillStrength1(player.hasSkillStrength1());
			player.setOldSkillStrength2(player.hasSkillStrength2());
			
			
		}
		else if((level == 2) && (room == 2)){
			map = new MapDisplay("resources/level/TileMap_2_2.txt", "resources/pics/tiles_2.gif", "resources/pics/shadow.png", 5, 1, this);
			
			coin2 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 50,50, 1, 100, this);
			enemy3 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 200, 200, 10, 100, this);
			actors.add(coin2);
			actors.add(enemy3);
		}
		else if((level == 2) && (room == 3)){
			map = new MapDisplay("resources/level/TileMap_2_3.txt", "resources/pics/tiles_2.gif", "resources/pics/shadow.png", 5, 1, this);
			boss = new Enemy(lib.getSprite("resources/pics/boss.gif", 4, 1), 120, 80, 20, 100, this);
			actors.add(boss);
		}
		
		else if((level == 3) && (room == 1)){
			map = new MapDisplay("resources/level/TileMap_3_1.txt", "resources/pics/tiles_3.gif", "resources/pics/shadow.png", 5, 1, this);
			
			checkpointx = 80;
			checkpointy = 40;
			player.setOldCoins(player.getCoins());
			player.setOldMaxhealth(player.getMaxhealth());
			player.setOldXP(player.getXP());
			player.setOldMana(player.getMana());
			player.setOldWeapon(player.hasWeapon());
			player.setOldArmour(player.hasArmour());
			
			player.setOldSkillHealth1(player.hasSkillHealth1());
			player.setOldSkillHealth2(player.hasSkillHealth2());
			player.setOldSkillStrength1(player.hasSkillStrength1());
			player.setOldSkillStrength2(player.hasSkillStrength2());
			
			enemy4 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 500, 50, 10, 100, this);
			enemy5 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 200, 50, 10, 100, this);
			healthpack2 = new Item(lib.getSprite("resources/pics/healthpack.gif", 1, 1), 720, 150, 7, 100, this);
			coin3 = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 720, 190, 1, 100, this);

			actors.add(enemy4);
			actors.add(enemy5);
			actors.add(healthpack2);
			actors.add(coin3);
		}
		else if((level == 3) && (room == 2)){
			map = new MapDisplay("resources/level/TileMap_3_2.txt", "resources/pics/tiles_3.gif", "resources/pics/shadow.png", 5, 1, this);
			enemy6 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 300, 300, 10, 100, this);
			actors.add(enemy6);
		}
		else if((level == 3) && (room == 3)){
			map = new MapDisplay("resources/level/TileMap_3_3.txt", "resources/pics/tiles_3.gif", "resources/pics/shadow.png", 5, 1, this);
			enemy7 = new Enemy(lib.getSprite("resources/pics/boss.gif", 4, 1), 150, 150, 10, 100, this);
			actors.add(enemy7);
		}
		 //Nur der Player bleibt bestehen
		actors.add(player);
		
	}
	/**
	 * Hier werden die multiplayerspezifischen Initialisierungen vorgenommen: zwei Spieler werden initialisiert, die entsprechende Karte geladen und das Spiel auf "gestartet" gesetzt
	 * @param f	Fenster, welches geschlossen wird
	 * **/	
	public void doInitializationsMulti(JFrame f){
		
		//TODO: Hier wird nix gemalt, solange Server und Client aktiv sind -> Zwei parallel Threads? Wie implementieren?
		f.dispose();
		chat();

		System.out.println("Netzwerk-Fenster verstecken");
		System.out.println("doInitializationsMulti ausführen");
		
		up = false;
		down = false;
		left = false;
		right = false; //sonst läuft Spieler nach Neustart einfach los
		attack = false;
		attack2 = false;
		
		level = 1;
		room = 1;
		last = System.nanoTime();
		gameover = 0;
		
		actors = new CopyOnWriteArrayList<Sprite>();
		attacks = new CopyOnWriteArrayList<Object>();
		
		lib = SpriteLib.getInstance();
		soundlib = new SoundLib();
		soundlib.loadSound("Hintergrund", "sound/Greensleeves.wav");
		soundlib.loadSound("Angriff", "sound/Angriff.wav");
		soundlib.loadSound("Zauber", "sound/Zauber.wav");
		if(sound_running == true){
		soundlib.loopSound("Hintergrund");
		}
		
		if(serverMode){
			player = new Player(lib.getSprite("resources/pics/player.gif", 12, 1), 50, 50, 100, this); //player immer eigener Spieler, player 2 der andere
			player2 = new Player(lib.getSprite("resources/pics/player2.gif", 12, 1), 650, 50, 100, this);
			player2.setRemote(true);
			actors.add(player);
			actors.add(player2);
		}else{
			player = new Player(lib.getSprite("resources/pics/player2.gif", 12, 1), 650, 50, 100, this);
			player2 = new Player(lib.getSprite("resources/pics/player.gif", 12, 1), 50, 50, 100, this);
			player2.setRemote(true);
			actors.add(player2);
			actors.add(player);
		}
		schwerteis = new Item(lib.getSprite("resources/pics/IceSword.gif", 1, 1), 100, 500, 5, 100, this);
		schwertfeuer = new Item(lib.getSprite("resources/pics/FireSword.gif", 1, 1), 600, 500, 10, 100, this);
		
//		Item xp1 = new Item(lib.getSprite("resources/pics/Doughnut.gif", 1, 1), 50,  530, 11, 100, this);
//		Item xp2 = new Item(lib.getSprite("resources/pics/Doughnut.gif", 1, 1), 100, 530, 11, 100, this);
//		Item xp3 = new Item(lib.getSprite("resources/pics/Doughnut.gif", 1, 1), 150, 530, 11, 100, this);
//		Item xp4 = new Item(lib.getSprite("resources/pics/Doughnut.gif", 1, 1), 200, 530, 11, 100, this);
//		Item xp5 = new Item(lib.getSprite("resources/pics/Doughnut.gif", 1, 1), 250, 530, 11, 100, this);
//		Item xp6 = new Item(lib.getSprite("resources/pics/Doughnut.gif", 1, 1), 300, 530, 11, 100, this);
//		Item xp7 = new Item(lib.getSprite("resources/pics/Doughnut.gif", 1, 1), 350, 530, 11, 100, this);
//		Item xp8 = new Item(lib.getSprite("resources/pics/Doughnut.gif", 1, 1), 400, 530, 11, 100, this);
		
		actors.add(schwerteis);
		actors.add(schwertfeuer);
//		actors.add(xp1);
//		actors.add(xp2);
//		actors.add(xp3);
//		actors.add(xp4);
//		actors.add(xp5);
//		actors.add(xp6);
//		actors.add(xp7);
//		actors.add(xp8);
		
		map = new MapDisplay("resources/level/MultiTileMap_1_1.txt", "resources/pics/tiles_2.gif", "resources/pics/shadow.png", 5, 1, this);
		
		frame.add(this);
		initSkills();
		setStarted(true);
	}
	/**
	 * Zeichnet das Netzwerk-Menue: Der Spieler kann auswaehlen, ob er Server oder Client ist und wartet auf eine eingehende Verbindung.
	 * Hier werden Server bzw. Client erzeugt, die als separate Threads fuer die Entgegennahme von Nachrichten verantwortlich sind und dementsprechend den nicht lokalen Spieler 2 bewegen usw..
	 * 
	 * **/
	private void paintNetworkMenu(){
		frame3.dispose();
		
		frame4 = new JFrame("Netzwerk");
		frame4.setLocation(650,300);
		frame4.setSize(100, 100);
		JButton b1 = new JButton("Server");
		JButton b2 = new JButton("Client");
		JButton b3 = new JButton("Zurück");
		frame4.add(BorderLayout.NORTH, b1);
		frame4.add(BorderLayout.CENTER, b2);
		frame4.add(BorderLayout.SOUTH, b3);

		frame4.pack();
		frame4.setVisible(true);

		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				serverMode = true;
				server = new Server(4711, p);
				server.start(); //Client starten; Auf gar keinen Fall mit run starten!
				
			}
		});
		
		
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){ 
				clientMode = true;
				client = new Client("127.0.0.1", p);
				client.start(); //Thread starten; Auf gar keinen Fall mit run starten!
			}
			
		});
		
		b3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){ 
				spiel_status=3;
				paintMenu();
				frame4.setVisible(false);
			}
			
		});
	}
	/**
	 * Zeichnet das Einstellungsmenü. Es wird durch das Hauptmenü aufgerufen.
	 * Hier kann der Sound ein und aus geschaltet werden.
	 */
	private void paintSettingMenu(){
		frame3.dispose();
		
		frame5 = new JFrame("Einstellungen");
		frame5.setLocation(650,300);
		frame5.setSize(100, 100);
		JButton b1 = new JButton("Zurück");
		JButton b2 = new JButton("Tastaturbelegung");
		JRadioButton sound_on = new JRadioButton("Sound An");
        JRadioButton sound_off = new JRadioButton("Sound Aus");
        
        ButtonGroup gruppe = new ButtonGroup();
        gruppe.add(sound_on);
        gruppe.add(sound_off);
        
        if(sound_running){
             sound_on.setSelected(true);
        }else{
        	sound_off.setSelected(true);
        }
      
		
		frame5.setLayout(new GridLayout(4,1));
		
		frame5.add(sound_on);
		frame5.add(sound_off);
		frame5.add(b2);
		frame5.add(b1);

		frame5.pack();
		frame5.setVisible(true);

	    

		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				spiel_status=3;
				paintMenu();
				frame5.dispose();
					
				
			}
		});
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				
				paintKeySettingMenu();
				
				
				
			}
		});
		
		sound_on.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		      sound_running= true;
		    }
		});
		sound_off.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		      sound_running= false;
		    }
		});
	
	}
	/**
	 * Zeichnet das Menü, in dem man die Tastaturbelegung ändern kann.
	 * Noch nicht intigriert.
	 */
	private void paintKeySettingMenu(){
		frame5.dispose();
		
		frame6 = new JFrame("Einstellungen");
		frame6.setLocation(650,300);
		frame6.setSize(100, 100);
		JButton b1 = new JButton("Zurück");
		        
       
      
		
		frame6.setLayout(new GridLayout(3,1));
		
		frame6.add(b1);

		frame6.pack();
		frame6.setVisible(true);

		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				
				paintSettingMenu();
				frame6.dispose();
				
				
			}
		});
	}
	
	
	
	
	/**
	 * Zeichnet das Hauptmenue, indem der Spielmodus (Einzelspieler bzw. Multiplayer) ausgewaehlt oder das Spiel beendet werden kann.
	 * **/
	private void paintMenu(){ 
		
		//Idee: Vllt. lieber Menü auf unsichtbar setzen und immer wieder anzeigen, wenn benötigt? Vllt. auch praktisch für Pause...Aber was mit entsprechenden Labels?
		if(spiel_status == 3){ //Spiel noch gar nicht gestartet
			frame3 = new JFrame("Spiel starten?");
			frame3.setLocation(650,300);
			frame3.setSize(100, 100);
			JButton b1 = new JButton("Einzelspieler");
			b1.setMnemonic(KeyEvent.VK_ENTER);//Shortcut Enter
			JButton b2 = new JButton("Beenden");
			b2.setMnemonic(KeyEvent.VK_ESCAPE);//Shortcut Escape
			JButton b3 = new JButton("Mehrspieler");
			JButton b4 = new JButton("Einstellungen");
			JButton b5 = new JButton("Handbuch");
			
			//Neues Layout. 4 Zeilen, 1 Spalte. 
			frame3.setLayout(new GridLayout(5,1));
			frame3.add(b1);
			frame3.add(b3);
			frame3.add(b4);
			frame3.add(b5);
			frame3.add(b2);

			frame3.pack();
			frame3.setVisible(true);
			
			
			b1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0){
					singleplayer = true;
					doInitializations(frame3);
				}
			});
			
			
			b2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
					System.exit(0);
				}
				
			});
			
			b3.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg1){
					multiplayer = true;
					paintNetworkMenu();
				}
			});			
			
			b4.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg1){
					paintSettingMenu();
				}
			});
			
			b5.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg1){
					try {
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+ "resources\\handbuch\\Handbuch.pdf");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		}
		
		if(spiel_status == 1|| spiel_status == 0){ //Wenn Spiel gewonnen oder verloren
//			if (!multiplayer){
				frame2 = new JFrame("Neues Einzelspielerspiel?");
				frame2.setLocation(500,300);
				frame2.setSize(100, 100);
				JLabel label;
				if(spiel_status == 1){
					label = new JLabel("Bravo, du hast gewonnen! Neues Einzelspielerspiel?");
				}else{
					if(player.getLifes() == 0){
						label = new JLabel("Schade, du hast verloren! Neues Einzelspielerspiel?");
					}else{
						label = new JLabel("Du hast gewonnen! Neues Einzelspielerspiel?");
					}
				}
				
				frame2.add(BorderLayout.NORTH, label);
				JButton b1 = new JButton("Neues Einzelspielerspiel");
				b1.setMnemonic(KeyEvent.VK_ENTER);//Shortcut Enter
				b1.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0){ //bzgl. Starten
						soundlib.stopLoopingSound();
						doInitializations(frame2);
		
					}
				});
				
				JButton b2 = new JButton("Zurück zum Hauptmenü");
				b2.setMnemonic(KeyEvent.VK_ESCAPE);//Shortcut Escape
				b2.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
						soundlib.stopLoopingSound();
						spiel_status=3;
						paintMenu();
						frame2.setVisible(false);
						
				
						
					}
					
				});
				frame2.add(BorderLayout.CENTER, b1);
				frame2.add(BorderLayout.SOUTH, b2);
				frame2.pack();
				frame2.setVisible(true);
				spiel_status = 0;
			}
//		}else{
//			frame2 = new JFrame("Ende");
//			frame2.setLocation(500,300);
//			frame2.setSize(100, 100);
//			JLabel label;
//			if(player.getLifes() == 0){
//				label = new JLabel("Du Lusche hast verloren!");
//			}else{
//				label = new JLabel("Boom-Headshot! Du hast gewonnen!");
//			}
//			
//			frame2.add(BorderLayout.NORTH, label);
//			
//			JButton b2 = new JButton("Zurück zum Hauptmenü");
//			b2.setMnemonic(KeyEvent.VK_ESCAPE);//Shortcut Escape
//			b2.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
//					if(sound_running){
//						soundlib.stopLoopingSound();
//					}
//					spiel_status=3;
//					paintMenu();
//					frame2.setVisible(false);
//					frame2.dispose();
//					
//			
//					
//				}
//				
//			});
//			frame2.add(BorderLayout.CENTER, b2);
//			frame2.pack();
//			frame2.setVisible(true);
//			spiel_status = 0;
//		}
		spiel_status = 0; // Daraus folgt, dass wenn man das Spiel per ESC-taste verlässt, man verliert.
		
	}
	/**
	 * Ruft den Shop auf, indem Spielobjekte (Manatrank, Leben) gekauft werden koennen.
	 * **/
	public void shop(){
		up = false;
		down = false;
		left = false;
		right = false;
		shopmode = true;
		
		shop2.setLocation(500,300);
		shop2.setSize(800, 600);
		shop2.pack();
		shop2.setVisible(true);
		
		JButton b1 = new JButton("1 Manatrank für 10 Münzen");
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){ //bzgl. Starten
				if(player.getCoins() >= 10){
					player.setMana(player.getMana()+1);
					player.setCoins(player.getCoins()-10);
				}else{
					System.out.println("Du hast nicht genug Münzen!");
				}
				shopmode = false;
				enterShop = false;
			}
		});
		
		
		JButton b2 = new JButton("1 Leben für 50 Münzen");

		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
				if(player.getCoins() >= 50){
					player.setLifes(player.getLifes()+1);
					player.setCoins(player.getCoins()-50);
				}else{
					System.out.println("Du hast nicht genug Münzen!");
				}
				shopmode = false;
				enterShop = false;
			}
			
		});
		
		JButton b3 = new JButton("Ich will nix - danke");
		b3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){
				shopmode = false;
				enterShop = false;
			}
		});
		
		shop2.add(BorderLayout.NORTH, b1);
		shop2.add(BorderLayout.CENTER, b2);
		shop2.add(BorderLayout.SOUTH, b3);
		shop2.pack();
		shop2.setVisible(true);
		
	}
	/**
	 * Spielschleife, fuehrt die Zeitberechnung fuer fluessige Darstellung, Tastenabfrage, Logik, Bewegung und Neuzeichnung aus.
	 * **/
	public void run(){
		while(game_running){
			if(singleplayer){
				computeDelta(); //Zeit für vorausgehenden Schleifendurchlauf wird errechnet
				//Erst Methoden abarbeiten, wenn Spiel gestartet ist
				if(isStarted()){
					
					checkKeys(); //Tastaturabfrage
					doLogic(); //Ausführung der Logik
					moveObjects(); //Bewegen von Objekten
					
				}

				repaint();
				
				try{
					if(System.nanoTime()-last < 16666666){
						Thread.sleep((16666666 - (System.nanoTime() - last))/1000000); //Zum flüssigen Spiellauf und stabiler FPS-Rate
					}
					
					
				}catch (InterruptedException e){}
			} else{
				if(serverMode){
					 //Zeit für vorausgehenden Schleifendurchlauf wird errechnet
					//Erst Methoden abarbeiten, wenn Spiel gestartet ist
					computeDelta();
					if(isStarted()){
						
						checkKeys(); //Tastaturabfrage
						doLogic(); //Ausführung der Logik
						moveObjects(); //Bewegen von Objekten
						
					}

					repaint();
					
					//System.out.println(System.nanoTime() - last);
					try{
						if(!isStarted()){
							Thread.sleep(20);
						}else if(System.nanoTime()-last < 16666666){
							Thread.sleep((16666666 - (System.nanoTime() - last))/1000000);
						}
//						if(System.nanoTime()-last < 16666666){
//							Thread.sleep((16666666 - (System.nanoTime() - last))/1000000); //Zum flüssigen Spiellauf und stabiler FPS-Rate
//						}
					}catch (InterruptedException e){}
				}else if(clientMode){
					computeDelta();
					if(isStarted()){
						checkKeys();
						doLogic();
						moveObjects();
					}
				
					repaint();
					
					//System.out.println(System.nanoTime() - last);
					try{
						//Thread.sleep(20);
						if(!isStarted()){
							Thread.sleep(20);
						}else if(System.nanoTime()-last < 16666666){
							Thread.sleep((16666666 - (System.nanoTime() - last))/1000000);
						}
//						if(System.nanoTime()-last < 16666666){
//							Thread.sleep((16666666 - (System.nanoTime() - last))/1000000); //Zum flüssigen Spiellauf und stabiler FPS-Rate
//						}
					}catch (InterruptedException e){}
				}
			}
			
			
		}	
	}
	
	/**
	 * Methode zur Initialisierung des Skill-Menues, muss einmal vor Aufruf von skills() ausgefuehrt werden.
	 * 
	 * **/
	public void initSkills(){
		up = false;
		down = false;
		left = false;
		right = false;
		skillmode = true;
		
		skills.setLocation(500,300);
		skills.setSize(400,300);
			
		t1 = new JButton("Dicker Brecher (benötigt 2 Skillpunkte)");
		t1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){ //bzgl. Starten
				if(player.getXP() >= 2){
					player.setXP(player.getXP()-2);
					player.setMaxhealth(player.getMaxhealth() + 50);
					player.setSkillHealth1(true);
				}else{
					System.out.println("Du hast nicht genug Erfahrung");
				}
				skillmode = false;
			}
		});
		
		t3 = new JButton("Kanten-Paule (benötigt 4 Skillpunkte und Dicker Brecher)");
		t3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){ //bzgl. Starten
				if(player.getXP() >= 4){
					player.setXP(player.getXP()-4);
					player.setMaxhealth(player.getMaxhealth() + 50);
					player.setSkillHealth2(true);
				}else{
					System.out.println("Du hast nicht genug Erfahrung");
				}
				skillmode = false;
			}
		});
		
		
		t2 = new JButton("Brutaler Prügelknabe (benötigt 2 Skillpunkte)");
		t2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
				if(player.getXP() >= 2){
					player.setXP(player.getXP()-2);
					player.setDamage(player.getDamage() + 20);
					player.setSkillStrength1(true);
				}else{
					System.out.println("Du hast nicht genug Erfahrung!");
				}
				skillmode = false;
			}
				
		});
		
		t4 = new JButton("Mega-Mörder brutaler Prügelknabe (benötigt 4 Skillpunkte und Brutaler Prügelknabe)");
		t4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
				if(player.getXP() >= 4){
					player.setXP(player.getXP()-4);
					player.setDamage(player.getDamage() + 30);
					player.setSkillStrength2(true);
				}else{
					System.out.println("Du hast nicht genug Erfahrung!");
				}
				skillmode = false;
			}
			
		});
		
		t5 = new JButton("Ich will nix - danke");
		t5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){
				skillmode = false;
			}
		});
		
		skills.setLayout(new GridLayout(3,2));
		skills.add(t1);
		skills.add(t2);
		skills.add(t3);
		skills.add(t4);
		skills.add(t5);
	
		if(player.hasSkillHealth1()){
			t1.setEnabled(false);
			t3.setEnabled(true);
		}else{
			t3.setEnabled(false);
		}
		if(player.hasSkillHealth2()){
			t3.setEnabled(false);
		}
		if(player.hasSkillStrength1()){
			t2.setEnabled(false);
			t4.setEnabled(true);
		}else{
			t4.setEnabled(false);
		}
		if(player.hasSkillStrength2()){
			t4.setEnabled(false);
		}
		
		skills.pack();
		
	}
	/**
	 * Erzeugt das Chatfenster.
	 * **/
	public void chat(){
		up = false;
		down = false;
		left = false;
		right = false;
		skillmode = false;
		
		chat.setLocation(500,300);
		chat.setSize(300, 300);
		
		chatPane = new JEditorPane();
		chatarea.addKeyListener(this);
		JScrollPane schieber = new JScrollPane(chatPane);
		schieber.setBounds(20, 50, 400, 200);
		chatPane.setEnabled(false);
	
		JButton close = new JButton("Chat verbergen");
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){
				chatmode = false;
			}
		});
		chat.add(BorderLayout.NORTH, chatPane);
		chat.add(BorderLayout.CENTER, chatarea);
		chat.add(BorderLayout.SOUTH, close);

	}
	/**
	 * 
	 * Methode zum Aufruf des Skill-Menues (Faehigkeiten koennen gegen Erfahrung erlernt werden). Vorher muss initSkills() aufgerufen werden.
	 * 
	 * **/
	public void skills(){
		up = false;
		down = false;
		left = false;
		right = false;
		skillmode = true;
		
		if(player.hasSkillHealth1()){
			t1.setEnabled(false);
			t3.setEnabled(true);
		}else{
			t3.setEnabled(false);
		}
		if(player.hasSkillHealth2()){
			t3.setEnabled(false);
		}
		if(player.hasSkillStrength1()){
			t2.setEnabled(false);
			t4.setEnabled(true);
		}else{
			t4.setEnabled(false);
		}
		if(player.hasSkillStrength2()){
			t4.setEnabled(false);
		}
		skills.setVisible(true);
		
	}
	
	private void computeDelta(){
		delta = System.nanoTime() - last; //Errechnung der Zeit für Schleifendurchlauf in NS
		last = System.nanoTime(); //Speichern der aktuellen Systemzeit
		fps = ((long) 1e9)/delta; //Errechnen der Framerate
	}
	
	/**
	 * Zeichnet die Spielobjekte.
	 * 
	 * @param g	Graphics-Objekt zur Angabe des Ortes zum Zeichnen.
	 * **/
	@Override
	public void paintComponent(Graphics g){ //paintComponent-Methode überschreiben
		super.paintComponent(g);
		
		if(!started){
			return; //es wird erst gezeichnet, wenn Spiel gestartet ist
		}
		map.drawVisibleMap(g); //Erst Karte, dann Objekte! Karte wird nur noch einmal gezeichnet, nicht für jeden Sprite in actors neu
		
		if(actors!=null){

			for(ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
				
				Sprite draw = it.next();
				draw.drawObjects(g);

			}
		}
		g.setColor(Color.red);
		g.drawString("FPS " + Long.toString(fps), 20, 10); //Zur Überprüfung des flüssigen Spiellaufs
		g.setColor(Color.white);
		g.drawString("Du hast " + player.getLifes() + " Leben", 20, 610);
		g.drawString(", " + player.getCoins() + " Münze(n)", 115, 610);
		g.drawString(" und " + player.getMana() + " Einheit(en) Mana", 200, 610);
		g.drawString("Lebenspunkte: " + player.getHealth() + "/" + player.getMaxhealth(), 350, 610);
		g.drawString("Story: ", 20, 620);
		g.drawString("XP: " + player.getXP(), 20, 630);
		if(talkwithnpc == true){
			g.drawString("Es war einmal in einem weit entfertenten Schloss...blablabla!", 60, 620);
		}
		g.drawString("Quest: ", 320, 620);
		if(inquest == true){
			if(player.getQuest().getType() == 1){
				
				if(player.getCollectedCoins() > 0){
					g.drawString("Du musst ? Münzen einsammeln", 360, 620);
				}else{
					g.drawString("Du musst " + player.getQuest().teiler + " * " + player.getQuest().multi +" Münzen einsammeln", 360, 620);
				}
			}
			if(player.getQuest().getType() == 2){
				int diff = player.getQuest().getQuestCoins() - player.getCollectedCoins();
				if(diff <= 0){
					diff = 0;
				}
				
				if(diff == 0){
					g.drawString(" - Geh zurück zum Quest", 360, 620);
				}else{
					g.drawString("Du musst noch " + diff + " Münze(n) sammeln", 360, 620);
				}
			}
			
		}
		g.drawString("Schaden: " + player.getDamage(), 485, 610);
		g.drawString("Rüstung: " + player.hasArmour(), 630, 610);
		g.drawString("Waffe: " + player.hasWeapon(), 715, 610);
		g.drawString("Level: " + level, 700, 620);
		g.drawString("Raum: " + room, 750, 620);
		
	}
	/**
	 * Fuehrt die Spiellogik aus. Prueft auf erfolgte Angriffe und löscht Objekte, die nicht mehr gezeichnet werden sollen.
	 * **/
	public  void doLogic(){
		if(singleplayer){
				if(attack){	//Wenn der Spieler angreifen will
					Object angriff;
					Sprite opfer;
					angriff = player.getAttackObject();
					if(angriff != null){
						
						actors.add(player.getAttackEffect());	//Effekt wird hinzugefügt zu Actors
						if(sound_running == true){
							soundlib.playSound("Angriff");
						}
						attacks.add(angriff);	
						for (ListIterator<Object> it1 = attacks.listIterator(); it1.hasNext();){
							angriff = it1.next();
							if((angriff instanceof java.awt.geom.Ellipse2D.Double)){ //wenn Angriff Kreis
								Ellipse2D.Double circle = (Ellipse2D.Double) angriff;
								for (ListIterator<Sprite> it2 = actors.listIterator(); it2.hasNext();){
									opfer = it2.next();
									if(opfer instanceof Enemy){
										if (circle.intersects(opfer.getX(), opfer.getY(), opfer.getWidth(), opfer.getHeight())){ //falls Kreis Enemy trifft
											((Enemy)opfer).reduceHealth(player.getDamage()); 
										}
									}
									
								}
							}else if (angriff instanceof java.awt.geom.Line2D.Double){ //wenn Angriff Linie
								Line2D.Double line = (Line2D.Double) angriff;
								for (ListIterator<Sprite> it2 = actors.listIterator(); it2.hasNext();){
									opfer = it2.next();
									if(opfer instanceof Enemy){
										if (line.intersects(opfer.getX(), opfer.getY(), opfer.getWidth(), opfer.getHeight())){ //falls Linie Enemy trifft
											((Enemy)opfer).reduceHealth(player.getDamage());
										}
									}
									
								}
							}
							
						}
					}
					attacks.clear();
				}	
				if(magic){
					Object magic;
					Sprite opfer;
					magic = player.getMagicObject();
					if(magic != null){
						actors.add(player.getMagicEffect());	//Effekt wird hinzugefügt zu Actors
						if(sound_running == true){
							soundlib.playSound("Zauber");
						}
						attacks.add(magic);	
						for (ListIterator<Object> it1 = attacks.listIterator(); it1.hasNext();){
								magic = it1.next();
								if((magic instanceof java.awt.geom.Ellipse2D.Double)){ //wenn Angriff Kreis
									Ellipse2D.Double circle = (Ellipse2D.Double) magic;
									for (ListIterator<Sprite> it2 = actors.listIterator(); it2.hasNext();){
										opfer = it2.next();
										if(opfer instanceof Enemy){
											if (circle.intersects(opfer.getX(), opfer.getY(), opfer.getWidth(), opfer.getHeight())){ //falls Kreis Enemy trifft
												if(opfer instanceof IceEnemy){
													((IceEnemy)opfer).reduceHealth(-10);
												}else if (opfer instanceof FireEnemy){
													((FireEnemy)opfer).reduceHealth(50);
												}else{
													((Enemy)opfer).stop();
												}
											}
										}

									}
								}else if (magic instanceof java.awt.geom.Line2D.Double){ //wenn Angriff Linie
									Line2D.Double line = (Line2D.Double) magic;
									for (ListIterator<Sprite> it2 = actors.listIterator(); it2.hasNext();){
										opfer = it2.next();
										if(opfer instanceof Enemy){
											if (line.intersects(opfer.getX(), opfer.getY(), opfer.getWidth(), opfer.getHeight())){ //falls Linie Enemy trifft
												if(opfer instanceof IceEnemy){
													((IceEnemy)opfer).reduceHealth(-10);
												}else if (opfer instanceof FireEnemy){
													((FireEnemy)opfer).reduceHealth(50);
												}else{
													((Enemy)opfer).stop();
												}


											}
										}

									}
								}

						}
						}

						attacks.clear();
					}


			}else if(clientMode || serverMode){
					
					/*   					Attacke vom Spieler 1 auf Spieler 2											*/
					/*#############################################################################################################*/
					
					out = convert((int)player.x, 4);
					out = out + " " + convert((int)player.y, 4);
					out = "pos " + out;
					//System.out.println(out);
					if(serverMode){
						server.out.println(out);
						server.out.flush();
					}else{
						client.out.println(out);
						client.out.flush();
					}
					Object angriff;
					if(attack){
						angriff = player.getAttackObject();
						if(angriff != null){
							System.out.println("Attacke-Effekt Spieler 1");
							actors.add(player.getAttackEffect());	//Effekt wird hinzugefügt zu Actors
							if(sound_running == true){
								soundlib.playSound("Angriff");
							}
						
							
								if((angriff instanceof java.awt.geom.Ellipse2D.Double)){ //wenn Angriff Kreis
									
									Ellipse2D.Double circle = (Ellipse2D.Double) angriff;
									if (circle.intersects(player2.getX(), player2.getY(), player2.getWidth(), player2.getHeight())){ //falls Kreis Enemy trifft
										player2.reduceHealth(player.getDamage()); 
									}
								}else if (angriff instanceof java.awt.geom.Line2D.Double){ //wenn Angriff Linie
									
									Line2D.Double line = (Line2D.Double) angriff;
										if (line.intersects(player2.getX(), player2.getY(), player2.getWidth(), player2.getHeight())){ //falls Linie Enemy trifft
												player2.reduceHealth(player.getDamage());
										}
								}
						}
					}
					
					
					/*   					Attacke vom Spieler 2 auf Spieler 1											*/
					/*#############################################################################################################*/
					if(attack2){
						angriff = player2.getAttackObject();
						if(angriff != null){
							System.out.println("Attacke-Effekt Spieler 2");
							actors.add(player2.getAttackEffect());	//Effekt wird hinzugefügt zu Actors
							if(sound_running == true){
								soundlib.playSound("Angriff");
							}
							
								if((angriff instanceof java.awt.geom.Ellipse2D.Double)){ //wenn Angriff Kreis
									
									Ellipse2D.Double circle = (Ellipse2D.Double) angriff;
									if (circle.intersects(player.getX(), player.getY(), player.getWidth(), player.getHeight())){ //falls Kreis Enemy trifft
										player.reduceHealth(player2.getDamage()); 
									}
								}else if (angriff instanceof java.awt.geom.Line2D.Double){ //wenn Angriff Linie
									
									Line2D.Double line = (Line2D.Double) angriff;
										if (line.intersects(player.getX(), player.getY(), player.getWidth(), player.getHeight())){ //falls Linie Enemy trifft
												player.reduceHealth(player2.getDamage());
										}
								}
						}
					}
		
			
			
				
			}
			
			//Neuerdings mit Iterator, der ist nämlich sicher vor Concurent-Modification-Exception (ist ja ne CopyOnWriteArrayList)
			for (ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
				Sprite r = it.next();
				r.doLogic(delta);
			
				if(r.remove){
					actors.remove(r);//Löschen von Sprite, remove == true; Wichtig für z.B. damit eine Münze nach dem Einsammeln nicht mehr angezeigt wird
				}
			}
			
			
			if(gameover == 1){
				if(System.currentTimeMillis() - gameover > 3000){
					stopGame();
				}
			}
			
			if(singleplayer){
				for (int n = 0; n < actors.size(); n++){ //Es werden alle weiteren Sprites zur Überprüfung durchlaufen
						
						Sprite s2 = actors.get(n);
						
						player.collidedWith(s2); //Überprüfung ob Spieler kollidiert ist
					
				}
			}else{
				for (int n = 0; n < actors.size(); n++){ //Es werden alle weiteren Sprites zur Überprüfung durchlaufen
					
					Sprite s2 = actors.get(n);
					
					player.collidedWith(s2); //Überprüfung ob Spieler kollidiert ist
					player2.collidedWith(s2);
				
			}
			}
			
			if (shopmode == false){
				shop2.setVisible(false);
			}
			if (skillmode == false){
				skills.setVisible(false);
			}
			if (chatmode == false){
				chat.setVisible(false);
			}
		
			
		
	}
	public String convert(int number, int digit) {
	    String buffer = String.valueOf(number);
	    while(buffer.length() != digit)
	        buffer="0" + buffer;
	    return buffer;
	}
	private void moveObjects(){

		//Neuerdings mit Iterator, der ist nämlich sicher vor Concurent-Modification-Exception (ist ja ne CopyOnWriteArrayList)
		for (ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
			Sprite r = it.next();
			r.move(delta);
		}
	}
	/**
	 * Beendet das Spiel, indem die Variablen setStarted und gameover geändert werden.
	 * Damit wird der Thread des Spiels beendet.
	 */
	private void stopGame(){
		setStarted(false);
		gameover = 1;
		if(serverMode){
			server.schliesse();
		}else if(clientMode){
			client.schliesse();
		}
		
	}
	/**
	 * Laesst den Spieler das Spiel gewinnen, fragt nach Neustart bzw. Beenden.
	 * **/
	public void wonGame(){
		System.out.println("Bravo, du hast gewonnen! Möchtest du noch einmal spielen?");
		stopGame();
		started = false;
		spiel_status = 1;
		paintMenu();
	}
	/**
	 * Laesst den Spieler verlieren, fragt nach Neustart bzw. Beenden.
	 * **/
	public void lostGame(){
		System.out.println("Schade, du hast verloren. Möchtest du es noch einmal versuchen?");
		if(!singleplayer){
			if(player.getLifes() == 0){
				System.out.println("Spieler 2 gewinnt!");
			}else{
				System.out.println("Spieler 1 gewinnt!");
			}
		
		}
		stopGame();
		started = false;
		spiel_status = 0;
		paintMenu();
	}
	/**
	 * Hier wird dauerhaft die Tastatureingabe für die Bewegung und dem Angriff überprüft und
	 * ggf. wird der Player angepasst.
	 */
	private void checkKeys(){
		
		if(left){
			player.setHorizontalSpeed(-speed);
		}
		if(right){
			player.setHorizontalSpeed(speed);
		}
		if(down){
			player.setVerticalSpeed(speed);
		}
		if(up){
			player.setVerticalSpeed(-speed);
		}
		if(!up&&!down){ 						//wenn weder up noch down gedrückt
			player.setVerticalSpeed(0);
		}
		if(!left&&!right){ 						//wenn weder left noch rechts gedrückt
			player.setHorizontalSpeed(0);
		}
		if(attack){
			player.setAttacking();
		}
		if(!attack){
			player.resetAttacking();
		}
		
	}
	
	/**
	 * Prueft auf Tastendruecke und setzt entsprechende Boolean-Variablen zur Steuerrung des Spielers, Aufrufen von Menues usw.
	 * 
	 * @param e KeyEvent fuer den KeyListener
	 * **/
	//Tastaturabfragen zur Steuerung
	public void keyPressed(KeyEvent e){
		if(chatmode){
			char key = e.getKeyChar();
			if(key == KeyEvent.VK_ENTER){
				if(clientMode){
					client.out.println("Chat");
					client.out.flush();
					if(!chatPane.getText().equals("")){
						client.out.println(chatarea.getText());
						chatPane.setText(chatPane.getText() + "\n" + "Ich: " + chatarea.getText());
					}else{
						client.out.println(chatarea.getText());
						chatPane.setText("Ich: " + chatarea.getText());
					}
					chatarea.setText("");
					client.out.flush();
				}else if(serverMode){
					server.out.println("Chat");
					server.out.flush();
					if(!chatPane.getText().equals("")){
						server.out.println(chatarea.getText());
						chatPane.setText(chatPane.getText() + "\n" + "Ich: " + chatarea.getText());
					}else{
						server.out.println(chatarea.getText());
						chatPane.setText("Ich: " + chatarea.getText());
					}
					chatarea.setText("");
					server.out.flush();
				}
				
			}
			
			
		}else{
			if (e.getKeyCode() == KeyEvent.VK_LEFT){ //linke Pfeiltaste
				left = true;
				if(clientMode){
					client.out.println("left");
					client.out.flush();
				} 
				if(serverMode){
					server.out.println("left");
					server.out.flush();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT){ //rechte Pfeiltaste
				right = true;
				if(clientMode){
					client.out.println("right");
					client.out.flush();
				}
				if(serverMode){
					server.out.println("right");
					server.out.flush();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_UP){ //obere Pfeiltaste
				up = true;
				if(clientMode){
					client.out.println("up");
					client.out.flush();
				}
				if(serverMode){
					server.out.println("up");
					server.out.flush();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN){//untere Pfeiltaste
				down = true;
				if(clientMode){
					client.out.println("down");
					client.out.flush();
				}
				if(serverMode){
					server.out.println("down");
					server.out.flush();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER){
				
				enterShop = true;
				enterNPC = true;
				
			}
			if(e.getKeyCode() == KeyEvent.VK_S){
				if(!multiplayer){
					skillmode = true;
					skills();
				}
			}
			if(e.getKeyCode() == KeyEvent.VK_T){
				if(multiplayer){
					chatmode = true;
					chat.setVisible(true);
					
				}
				
			}
			if(e.getKeyCode() == KeyEvent.VK_X){
				if(clientMode){
					client.out.println("Attack");
					client.out.flush();
				}
				if(serverMode){
					server.out.println("Attack");
					server.out.flush();
				}
				attack = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_C){
				if(clientMode){
					client.out.println("Magic");
					client.out.flush();
				}
				if(serverMode){
					server.out.println("Magic");
					server.out.flush();
				}
				magic = true;
			}
		}
		
	}
	/**
	 * Prueft auf Tastendruecke (Loslassen) und setzt entsprechende Boolean-Variablen zur Steuerrung des Spielers, Aufrufen von Menues usw.
	 * 
	 * @param e KeyEvent fuer den KeyListener
	 * **/
	public void keyReleased(KeyEvent e){
				
		if (e.getKeyCode() == KeyEvent.VK_LEFT){//linke Pfeiltaste
			left = false;
			if(clientMode){
				client.out.println("notleft");
				client.out.flush();
			}
			if(serverMode){
				server.out.println("notleft");
				server.out.flush();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){//rechte Pfeiltaste
			right = false;
			if(clientMode){
				client.out.println("notright");
				client.out.flush();
			}
			if(serverMode){
				server.out.println("notright");
				server.out.flush();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_UP){//obere Pfeiltaste
			up = false;
			if(clientMode){
				client.out.println("notup");
				client.out.flush();
			}
			if(serverMode){
				server.out.println("notup");
				server.out.flush();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){//untere Pfeiltaste
			down = false;
			if(clientMode){
				client.out.println("notdown");
				client.out.flush();
			}
			if(serverMode){
				server.out.println("notdown");
				server.out.flush();
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_X){
			attack = false;
			if(clientMode){
				client.out.println("notattack");
				client.out.flush();
			}
			if(serverMode){
				server.out.println("notattack");
				server.out.flush();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_C){
			magic = false;
			if(clientMode){
				client.out.println("notmagic");
				client.out.flush();
			}
			if(serverMode){
				server.out.println("notmagic");
				server.out.flush();
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			enterNPC = false;
			enterShop = false;
	
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){//Escape zum B
			if(isStarted()){
				stopGame(); 
				paintMenu(); 
			}else {
				setStarted(false);
				System.exit(0);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	/**
	 * Gibt zurueck, ob das Spiel gestartet ist, oder nicht
	 * @return ist das Spiel gestartet?
	 * **/
    public boolean isStarted(){
    	return started;
    }
    /**
	 * Setzt das Spiel auf den Zustand started (wichtig, ob noch das Menue oder bereits das Spielfeld gezeichnet werden soll)
	 * @param started boolean-Wert, hierauf wird der Spielzustand gesetzt
	 * 
	 * **/
    public void setStarted(boolean started){
    	this.started = started;
    }
    /**
	 * Setzt das aktuelle Level auf level
	 * @param level, die Levelnummer, auf das das Ingame-Level gesetzt wird
	 * **/
	public void setLevel(int level){
		this.level = level;
	}
	/**
	 * Liefert das aktuelle Level zurueck
	 * @return int, die aktuelle Levelnummer
	 * **/
	public int getLevel(){
		return level;
	}
	/**
	 * Gibt die aktuelle Karte zurueck
	 * @return MapDisplay, die Karte auf der sich der/die Spieler spielen.
	 * **/
	public MapDisplay getMap(){
		return map; //gibt die Karte zurück
	}
	/**
	 * Gibt die aktuelle Raumnummre zurueck
	 * @return int, die Raumnummer
	 * **/
	public int getRoom(){
		return room;
	}

	
	
}
