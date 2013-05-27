import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
	
	long testtime = 0;
	
	long delta = 0;
	long last = 0;
	long fps = 0;
	long gameover = 0;
	
	SpriteLib lib;
	Player player;
	Enemy enemy;
	MapDisplay map;
	
	CopyOnWriteArrayList<Sprite> actors;

	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean waitingForKeyPress;
	boolean game_running = true;
	boolean started = false;

	
	int spiel_status = 3; // 0 = Verloren, 1 = Gewonnen, 2 = Pause, 3 = noch nicht gestartet; Ersetzt boolean gamewon, lost
	int pressCount;
	int speed = 80;
	int x = 0;
	int y = 0;
	int level;
	
	static int rows, columns;
	

	public static void main(String[] args){
		new GamePanel(790,590); //Sonst grauer Streifen an den Rändern rechts und unten
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
		
		//createBackbuffer();		//Ein Puffer wird angelegt
		
		level = 1;
		last = System.nanoTime();
		gameover = 0;
		
		actors = new CopyOnWriteArrayList<Sprite>();
		
		lib = SpriteLib.getInstance();
		player = new Player(lib.getSprite("resources/pics/player.gif", 4, 1), 50, 50, 100, this);
		enemy = new Enemy(lib.getSprite("resources/pics/enemy.gif", 4, 1), 100, 100, 100, this);
		//TODO: Wie verschiedene Enemys organisieren, auch bzgl. Namen?
		actors.add(player); //actors(0) == player
		actors.add(enemy); //actors(1) == enemy
		enemy.setHorizontalSpeed(80); //Spieler läuft nur von links nach rechts, entsprechend lassen sich hier auch vertikale Gegner einbauen

		
		//Erstellen der Karte, wobei die ersten 3 Parameter für die Eingabedateien stehen, die erste Zahl für die Anzahl der Spalten im TileSet, die zweite für die Anzahl der Zeilen
		map = new MapDisplay("resources/level/TileMap.txt", "resources/pics/tiles.gif", "resources/pics/shadow.png", 5, 1, this);
		frame.setVisible(true);
		frame.add(this);
		menu.dispose();
		setStarted(true);
		
	}
	
	//Vllt. lieber in doInitializations Abfrage nach Wert von level und entsprechendes Laden von Map?
	public void doInitializations2(){
		level = 2;
		map = new MapDisplay("resources/level/TileMap_2.txt", "resources/pics/tiles.gif", "resources/pics/shadow.png", 5, 1, this); 
	}
	
	public void doInitializations3(){
		level = 3;
		map = new MapDisplay("resources/level/TileMap_3.txt", "resources/pics/tiles.gif", "resources/pics/shadow.png", 5, 1, this);
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
			
	}
	
	private void stopGame(){
		setStarted(false);
		gameover = 1;
	}
	
	public void wonGame(){
		System.out.println("Bravo, du hast gewonnen! Möchtest du noch einmal spielen?");
		stopGame();
		started = false;
		//game_running = false;Darf hier nicht stehen. Spiel lässt sich sonst nicht starten.
		spiel_status = 1;
		paintMenu();
	}
	
	public void lostGame(){
		System.out.println("Schade, du hast verloren. Möchtest du es noch einmal versuchen?");
		stopGame();
		started = false;
		//game_running = false; Darf hier nicht stehen. Spiel lässt sich sonst nicht starten.
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
	}
	
	private void doLogic(){
		
		//Neuerdings mit Iterator, der ist nämlich sicher vor Concurent-Modification-Exception (ist ja ne CopyOnWriteArrayList)
		for (ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
			Sprite r = it.next();
			r.doLogic(delta);
		}
		
		
		if(gameover == 1){
			if(System.currentTimeMillis() - gameover > 3000){
				stopGame();
			}
		}
		
		Sprite s1 = actors.get(0); //Player! Nur Überprüfung von Kollision des Player mit beliebigem Sprite
		for (int n = 1; n < actors.size(); n++){ //Es werden alle weiteren Sprites zur Überprüfung durchlaufen
				
				Sprite s2 = actors.get(n);
				
				s1.collidedWith(s2);
			
		}
	}
	
	private void moveObjects(){

		//Neuerdings mit Iterator, der ist nämlich sicher vor Concurent-Modification-Exception (ist ja ne CopyOnWriteArrayList)
		for (ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
			Sprite r = it.next();
			r.move(delta);
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
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){//Escape zum B
			if(isStarted()){
				stopGame(); 
				paintMenu(); 
			}else {
				//hier auch stopGame()?
				setStarted(false);
				System.exit(0);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
