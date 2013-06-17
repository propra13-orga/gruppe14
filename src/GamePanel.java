import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GamePanel extends JPanel  implements Runnable, KeyListener{
	private static final long serialVersionUID = 1L;
	JFrame frame;
	JFrame frame2;
	JFrame frame3;
	JFrame frame4;
	JFrame shop2 = new JFrame("Shop");
	
	long delta = 0;
	long last = 0;
	long fps = 0;
	long gameover = 0;
	
	SpriteLib lib;
	SoundLib soundlib;
	Player player;
	Enemy enemy;
	Enemy enemy2;

	Enemy boss;
	Enemy enemy3;
	Enemy enemy4;
	Enemy enemy5;
	Enemy enemy6;
	Enemy enemy7;
	//Enemy enemy8;
	//Enemy enemy9;

	Item coin;
	Item coin2;
	Item coin3;
	Item mana;
	Item shop;

	Item npc;
	Item schwert;
	Item schild;
	Item healthpack;
	Item healthpack2;
	//Item healthpack3;
	
	MapDisplay map;
	
	CopyOnWriteArrayList<Object> attacks;	//Liste für die Attack-Objekte
	CopyOnWriteArrayList<Sprite> actors; //beinhaltet alle Objekte im Spiel, also Gegner, Items etc.

	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean attack;	//Spieler will attackieren
	boolean waitingForKeyPress;
	boolean game_running = true;
	boolean started = false;
	boolean shopmode = false; //Ist Spieler im Shop oder nicht?
	boolean enterShop = false;
	boolean enterNPC = false;
	boolean talkwithnpc = false;
	boolean magic = false; //Will Spieler zaubern?

	int spiel_status = 3; // 0 = Verloren, 1 = Gewonnen, 2 = Pause, 3 = noch nicht gestartet; Ersetzt boolean gamewon, lost
	int pressCount;
	int speed = 80;
	int x = 0;
	int y = 0;
	int checkpointx;
	int checkpointy;
	int room; //zeigt an im wievielten Raum man sich im Level befindet, möglich sind 1, 2 oder 3
	int level; //zeigt an im wievielten Lebel man ist, möglich sind 1, 2 oder 3
	
	static int rows, columns;
	

	public static void main(String[] args){
		new GamePanel(790,630); //Sonst grauer Streifen an den Rändern rechts und unten
	}
	
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

	private void doInitializations(JFrame menu){
		
		up = false;
		down = false;
		left = false;
		right = false; //sonst läuft Spieler nach Neustart einfach los
		attack = false;
		
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
		soundlib.loopSound("Hintergrund");
		
		////1 = Coins, 2 = Mana, 3 = Shop, 4 = Rüstung, 5 = Waffe, 6 = NPC
		player = new Player(lib.getSprite("resources/pics/player.gif", 8, 1), 50, 50, 100, this);
		enemy = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 100, 500, 10, 100, this);
		enemy2 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 300, 200, 10, 100, this);
		coin = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 700, 400, 1, 100, this);
		mana = new Item(lib.getSprite("resources/pics/mana.gif", 1, 1), 470, 500, 2, 100, this);
		shop = new Item(lib.getSprite("resources/pics/shop.gif", 1, 1), 400, 500, 3, 100, this);
		npc = new Item(lib.getSprite("resources/pics/npc.gif", 1, 1), 100, 100, 6, 100, this);
		schild = new Item(lib.getSprite("resources/pics/armour.gif", 1, 1), 100, 200, 4, 100, this);
		schwert = new Item(lib.getSprite("resources/pics/weapon.gif", 1, 1), 100, 150, 5, 100, this);
		healthpack = new Item(lib.getSprite("resources/pics/healthpack.gif", 1, 1), 500, 100, 7, 100, this);


		actors.add(enemy); 
		actors.add(enemy2); 
		actors.add(coin); 
		actors.add(mana);
		actors.add(shop);
		actors.add(npc);
		actors.add(schwert);
		actors.add(schild);
		actors.add(healthpack);
		actors.add(player);
		
		checkpointx = 50;
		checkpointy = 50;

		player.setOldCoins(100);
		player.setOldMana(0);
		player.setOldWeapon(false);
		player.setOldArmour(false);
		player.setCoins(100);
		
		//Erstellen der Karte, wobei die ersten 3 Parameter für die Eingabedateien stehen, die erste Zahl für die Anzahl der Spalten im TileSet, die zweite für die Anzahl der Zeilen
		map = new MapDisplay("resources/level/TileMap_1_1.txt", "resources/pics/tiles_1.gif", "resources/pics/shadow.png", 5, 1, this);
		
		frame.setVisible(true);
		frame.add(this);
		menu.dispose();
		setStarted(true);
		//doInitializations(3,2); //Zum Level jumpen
		
	}
	
	/**
	 * In der doInitializations-Klasse werden neue Level bzw. Räume geladen und entsprechende Sprites geladen. 
	 * Außerdem werden teilweise Checkpointwerte gespeichert
	 */
	public void doInitializations(int l, int r){ //l = level, r = room
		level = l;
		room = r;
		actors.clear(); //Actors-Liste wird gelöscht und dann entsprechend der Information in TileMap_n.txt neu erstellt

		if((level ==1) && (room == 1)){
			map = new MapDisplay("resources/level/TileMap_1_1.txt", "resources/pics/tiles_1.gif", "resources/pics/shadow.png", 5, 1, this);
			player = new Player(lib.getSprite("resources/pics/player.gif", 8, 1), 50, 50, 100, this);
			enemy = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 100, 500, 10, 100, this);
			enemy2 = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 300, 200, 10, 100, this);
			coin = new Item(lib.getSprite("resources/pics/coin.gif", 1, 1), 700, 400, 1, 100, this);
			mana = new Item(lib.getSprite("resources/pics/mana.gif", 1, 1), 470, 500, 2, 100, this);
			shop = new Item(lib.getSprite("resources/pics/shop.gif", 1, 1), 400, 500, 3, 100, this);
			npc = new Item(lib.getSprite("resources/pics/npc.gif", 1, 1), 100, 100, 6, 100, this);
			schild = new Item(lib.getSprite("resources/pics/armour.gif", 1, 1), 100, 200, 4, 100, this);
			schwert = new Item(lib.getSprite("resources/pics/weapon.gif", 1, 1), 100, 150, 5, 100, this);
			healthpack = new Item(lib.getSprite("resources/pics/healthpack.gif", 1, 1), 500, 100, 7, 100, this);
			
			actors.add(enemy); 
			actors.add(enemy2); 
			actors.add(coin); 
			actors.add(mana);
			actors.add(shop);
			actors.add(npc);
			actors.add(schwert);
			actors.add(schild);
			actors.add(healthpack);
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
			player.setOldMana(player.getMana());
			player.setOldWeapon(player.hasWeapon());
			player.setOldArmour(player.hasArmour());
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
			player.setOldMana(player.getMana());
			player.setOldWeapon(player.hasWeapon());
			player.setOldArmour(player.hasArmour());
			
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

	
	private void paintMenu(){ 
		//Idee: Vllt. lieber Menü auf unsichtbar setzen und immer wieder anzeigen, wenn benötigt? Vllt. auch praktisch für Pause...Aber was mit entsprechenden Labels?
		if(spiel_status == 3){ //Spiel noch gar nicht gestartet
			frame3 = new JFrame("Spiel starten?");
			frame3.setLocation(650,300);
			frame3.setSize(100, 100);
			JButton b1 = new JButton("Spiel starten");
			b1.setMnemonic(KeyEvent.VK_ENTER);//Shortcut Enter
			JButton b2 = new JButton("Beenden");
			b2.setMnemonic(KeyEvent.VK_ESCAPE);//Shortcut Escape
			
			frame3.add(BorderLayout.NORTH, b1);
			frame3.add(BorderLayout.SOUTH, b2);
			frame3.pack();
			frame3.setVisible(true);
			
			
			b1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0){
					
					doInitializations(frame3);
	
				}
			});
			
			
			b2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
					System.exit(0);
				}
				
			});
			
			
		}
		
		if(spiel_status == 1|| spiel_status == 0){ //Wenn Spiel gewonnen oder verloren
			frame2 = new JFrame("Neustart?");
			frame2.setLocation(500,300);
			frame2.setSize(100, 100);
			JLabel label;
			if(spiel_status == 1){
				label = new JLabel("Bravo, du hast gewonnen! Möchtest du noch einmal spielen?");
			}else{
				label = new JLabel("Schade, du hast verloren. Möchtest du es noch einmal versuchen?");
			}
			
			frame2.add(BorderLayout.NORTH, label);
			JButton b1 = new JButton("Ich möchte nocheinmal spielen");
			b1.setMnemonic(KeyEvent.VK_ENTER);//Shortcut Enter
			b1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0){ //bzgl. Starten

					doInitializations(frame2);
	
				}
			});
			
			
			JButton b2 = new JButton("Es reicht mir...");
			b2.setMnemonic(KeyEvent.VK_ESCAPE);//Shortcut Escape
			b2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
					System.exit(0);
				}
				
			});
			frame2.add(BorderLayout.CENTER, b1);
			frame2.add(BorderLayout.SOUTH, b2);
			frame2.pack();
			frame2.setVisible(true);
			spiel_status =0;
		}
		spiel_status = 0; // Daraus folgt, dass wenn man das Spiel per ESC-taste verlässt, man verliert.
		
	}

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
	public void run(){
		while(game_running){
			
			computeDelta(); //Zeit für vorausgehenden Schleifendurchlauf wird errechnet
			//Erst Methoden abarbeiten, wenn Spiel gestartet ist
			if(isStarted()){
				
				checkKeys(); //Tastaturabfrage
				doLogic(); //Ausführung der Logik
				moveObjects(); //Bewegen von Objekten
				
			}

			repaint();
			
			try{
				Thread.sleep((1000000000 - (System.nanoTime() - last))/60000000); //Zum flüssigen Spiellauf und stabiler FPS-Rate
				
			}catch (InterruptedException e){}
			
		}	
	}
	
	private void computeDelta(){
		delta = System.nanoTime() - last; //Errechnung der Zeit für Schleifendurchlauf in NS
		last = System.nanoTime(); //Speichern der aktuellen Systemzeit
		fps = ((long) 1e9)/delta; //Errechnen der Framerate
	}
	@Override
	public void paintComponent(Graphics g){ //paintComponent-Methode überschreiben
		super.paintComponent(g);
		
		
		if(!isStarted()){
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
		g.drawString("Lebenspunkte: " + player.getHealth() + "/100", 350, 610);
		g.drawString("Story: ", 20, 620);
		if(talkwithnpc == true){
			g.drawString("Es war einmal in einem weit entfertenten Schloss...blablabla!", 60, 620);
		}
		g.drawString("Schaden: " + player.getDamage(), 485, 610);
		g.drawString("Enemy: " + enemy2.getHealth(), 560, 610);
		g.drawString("Rüstung: " + player.hasArmour(), 630, 610);
		g.drawString("Waffe: " + player.hasWeapon(), 715, 610);
		g.drawString("Level: " + level, 700, 620);
		g.drawString("Raum: " + room, 750, 620);
		
	}
	
private void doLogic(){
		
		if(attack){	//Wenn der Spieler angreifen will
			Object angriff;
			Sprite opfer;
			angriff = player.getAttackObject();
			if(angriff != null){
				actors.add(player.getAttackEffect());	//Effekt wird hinzugefügt zu Actors
				soundlib.playSound("Angriff");
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
				soundlib.playSound("Zauber");
				attacks.add(magic);	
				for (ListIterator<Object> it1 = attacks.listIterator(); it1.hasNext();){
					magic = it1.next();
					if((magic instanceof java.awt.geom.Ellipse2D.Double)){ //wenn Angriff Kreis
						Ellipse2D.Double circle = (Ellipse2D.Double) magic;
						for (ListIterator<Sprite> it2 = actors.listIterator(); it2.hasNext();){
							opfer = it2.next();
							if(opfer instanceof Enemy){
								if (circle.intersects(opfer.getX(), opfer.getY(), opfer.getWidth(), opfer.getHeight())){ //falls Kreis Enemy trifft
									((Enemy)opfer).reduceHealth(player.getDamage()); 
								}
							}
							
						}
					}else if (magic instanceof java.awt.geom.Line2D.Double){ //wenn Angriff Linie
						Line2D.Double line = (Line2D.Double) magic;
						for (ListIterator<Sprite> it2 = actors.listIterator(); it2.hasNext();){
							opfer = it2.next();
							if(opfer instanceof Enemy){
								if (line.intersects(opfer.getX(), opfer.getY(), opfer.getWidth(), opfer.getHeight())){ //falls Linie Enemy trifft
									((Enemy)opfer).stop();
								}
							}
							
						}
					}
					
				}
			}
			
			attacks.clear();
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
		
		for (int n = 0; n < actors.size(); n++){ //Es werden alle weiteren Sprites zur Überprüfung durchlaufen
				
				Sprite s2 = actors.get(n);
				
				player.collidedWith(s2); //Überprüfung ob Spieler kollidiert ist
			
		}
		
		if (shopmode == false){
			shop2.setVisible(false);
		}
	}
	
	private void moveObjects(){

		//Neuerdings mit Iterator, der ist nämlich sicher vor Concurent-Modification-Exception (ist ja ne CopyOnWriteArrayList)
		for (ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
			Sprite r = it.next();
			r.move(delta);
		}
	}
	private void stopGame(){
		setStarted(false);
		gameover = 1;
	}
	
	public void wonGame(){
		System.out.println("Bravo, du hast gewonnen! Möchtest du noch einmal spielen?");
		stopGame();
		started = false;
		spiel_status = 1;
		paintMenu();
	}
	
	public void lostGame(){
		System.out.println("Schade, du hast verloren. Möchtest du es noch einmal versuchen?");
		stopGame();
		started = false;
		spiel_status = 0;
		paintMenu();
	}

	
    public boolean isStarted(){
    	return started;
    }
    
    public void setStarted(boolean started){
    	this.started = started;
    }
    
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
	
	public MapDisplay getMap(){
		return map; //gibt die Karte zurück
	}
	
	public int getRoom(){
		return room;
	}
	
	
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
		if(magic){
			player.setSummoning();
		}
		if(!magic){
			player.resetSummoning();
		}
		
	}
	
	
	//Tastaturabfragen zur Steuerung
	public void keyPressed(KeyEvent e){
		
		if (e.getKeyCode() == KeyEvent.VK_LEFT){ //linke Pfeiltaste
			left = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){ //rechte Pfeiltaste
			right = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP){ //obere Pfeiltaste
			up = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){//untere Pfeiltaste
			down = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER){
			enterShop = true;
			enterNPC = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_X){
			attack = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_C){
			magic = true;
		}
	}
	//Taste wieder losgelassen?
	public void keyReleased(KeyEvent e){
				
		if (e.getKeyCode() == KeyEvent.VK_LEFT){//linke Pfeiltaste
			left = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){//rechte Pfeiltaste
			right = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP){//obere Pfeiltaste
			up = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){//untere Pfeiltaste
			down = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_X){
			attack = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_C){
			magic = false;
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

}
