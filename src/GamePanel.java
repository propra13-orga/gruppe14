import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	private static final long serialVersionUID = 1L;
	JFrame frame;
	
	long delta = 0;
	long last = 0;
	long fps = 0;
	long gameover = 0;
	
	SpriteLib lib;
	Player player;
	MapDisplay map;
	
	Vector<Sprite> actors;

	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean waitingForKeyPress;
	boolean game_running = true;
	boolean started = false;
	boolean once = false; //bei Neustart keinen neuen Thread - wie muss once am Anfang sein, false oder true?
	int pressCount;
	int speed = 80;
	int x = 0;
	int y = 0;

	static int rows, columns;
	

	public static void main(String[] args){
		new GamePanel(800,600);
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
		
		Thread t = new Thread(this);
		t.start(); //ruft run auf
	}
	
	private void doInitializations(){
		
		last = System.nanoTime();
		gameover = 0;
		
		actors = new Vector<Sprite>();
		
		lib = SpriteLib.getInstance();
		player = new Player(lib.getSprite("pics/player.gif", 1, 1), 40, 40, 100, this);
		actors.add(player);
		
		//Erstellen der Karte, wobei die ersten 3 Parameter f�r die Eingabedateien stehen, die erste Zahl f�r die Anzahl der Spalten im TileSet, die zweite f�r die Anzahl der Zeilen
		map = new MapDisplay("level/TileMap.txt", "pics/tiles.gif", "pics/shadow.png", 4, 1, this);
	
		if(!once){//verhindert, dass bei Neustart neuer Thread gestartet wird
			once = true;
			Thread t = new Thread(this);
			t.start();
		}
		
	}
	
	public void doInitializations2(){
		map = new MapDisplay("level/TileMap_2.txt", "pics/tiles.gif", "pics/shadow.png", 4, 1, this); //auch entsprechend angepasste ShadowMap muss geladen werden! Man k�nnte auch verschiedene TileSets �bergeben
		
	}
	
	private void paintMenu(){ //Wird bisher noch nicht angesprochen, da Methode buggt (ArrayIndexOutOfBoundsException in MapDisplay.getColorForPoint)
		JButton b1 = new JButton("Spiel starten");
		
		JButton b2 = new JButton("Beenden");
		
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){ //bzgl. Starten
				doInitializations();
				setStarted(true);
			}
		});
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){ //bzgl. Schlie�en
				System.exit(0);
			}
			
		});
		
		frame.add(b1);
		frame.pack();
		frame.add(b2);

		frame.setVisible(true);
	}

	
	public void run(){
		while(game_running){
			
			computeDelta(); //Zeit f�r vorausgehenden Schleifendurchlauf wird errechnet
			//Erst Methoden abarbeiten, wenn Spiel gestartet ist
			if(isStarted()){
				checkKeys(); //Tastaturabfrage
				doLogic(); //Ausf�hrung
				moveObjects(); //Bewegen von Objekten
				
			}else{
				System.out.println("Das Spiel ist leider beendet"); //Wenn Spiel beendet, wird GameLoop nicht mehr ausgef�hrt
			}
			
			repaint(); //Von Component geerbt, st��t Neuzeichnen an, geh�rt vllt. auch hinter die Schleife?
			
			try{
				Thread.sleep(5);
			}catch (InterruptedException e){}
		}	
	}
	
	private void computeDelta(){
		delta = System.nanoTime() - last; //Errechnung der Zeit f�r Schleifendurchlauf in NS
		last = System.nanoTime(); //Speichern der aktuellen Systemzeit
		
		fps = ((long) 1e9)/delta; //Errechnen der Framerate
	}
	
	public void paintComponent(Graphics g){ //paintComponent-Methode �berschreiben
		super.paintComponent(g);
		
		g.setColor(Color.red);
		g.drawString("FPS " + Long.toString(fps), 20, 10); //Zur �berpr�fung des fll�ssigen Spiellaufs
		
		if(!isStarted()){
			return; //es wird erst gezeichnet, wenn Spiel gestartet ist
		}

		if(actors!=null){
			for(Drawable draw:actors){
				map.drawVisibleMap(g); //Erst Karte, dann Objekte! Karte muss nicht jedes mal neu gezeichnet werden - woandershin auslagern?
				draw.drawObjects(g);
			}
		}
		
		
	
	}
	
    public boolean isStarted(){
    	return started;
    }
    
    public void setStarted(boolean started){
    	this.started = started;
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
		if(!up&&!down){ //wenn weder up noch down gedr�ckt
			player.setVerticalSpeed(0);
		}
		if(!left&&!right){ //wenn weder left noch rechts gedr�ckt
			player.setHorizontalSpeed(0);
		}
	}
	
	private void doLogic(){
		for(Movable mov:actors){
			mov.doLogic(delta);
		}
		
		//hier Kollisionsabfrage?
		
		if(gameover == 1){
			if(System.currentTimeMillis() - gameover > 3000){
				stopGame();
			}
		}
	}
	
	private void moveObjects(){
		for(Movable mov:actors){
			mov.move(delta);
		}
	}
	
	private void stopGame(){
		setStarted(false);
		gameover = 1;
		//TODO: Sinnvolle Ausgabe und M�glichkeit des Neustarts
		//Oder Gamerunning auf false?
	}
	
	public MapDisplay getMap(){
		return map; //gibt die Karte zur�ck
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
		if (e.getKeyCode() == KeyEvent.VK_ENTER){//Enter zum starten
			if(!isStarted()){
				doInitializations();
				setStarted(true);
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){//Escape zum B
			if(isStarted()){
				stopGame(); //oder gamerunning = false?
			}else {
				//hier auch stopGame()?
				setStarted(false);
				System.exit(0);
			}
		}
	}
	
	public void keyTyped(KeyEvent e){ //???
		if (waitingForKeyPress){
			if (pressCount == 1){
				waitingForKeyPress = false;
				//startGame();
				pressCount = 0;
			} else{
				pressCount++;
			}
		}
	}

}
