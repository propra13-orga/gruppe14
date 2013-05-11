import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

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
	boolean once = false; //bei Neustart keinen neuen Thread
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
		//doInitializations();
		
		Thread t = new Thread(this);
		t.start();
	}
	
	private void doInitializations(){
		
		last = System.nanoTime();
		gameover = 0;
		
		actors = new Vector<Sprite>();
		
		lib = SpriteLib.getInstance();
		player = new Player(lib.getSprite("pics/player.gif", 1, 1), 50, 50, 100, this);
		actors.add(player);
		
		//Erstellen der Karte, wobei die ersten 3 Parameter für die Eingabedateien stehen, die erste Zahl für die Anzahl der Spalten im TileSet, die zweite für die Anzahl der Zeilen
		map = new MapDisplay("level/TileMap.txt", "pics/tiles.gif", "pics/shadow.png", 4, 1, this);
	
		if(!once){//verhindert, dass bei Neustart neuer Thread gestartet wird
			once = true;
			Thread t = new Thread(this);
			t.start();
		}
		
	}

	
	public void run(){
		while(game_running){
			
			computeDelta(); //Zeit für vorausgehenden Schleifendurchlauf wird errechnet
			//Erst Methoden abarbeiten, wenn Spiel gestartet ist
			if(isStarted()){
				checkKeys(); //Tastaturabfrage
				doLogic(); //Ausführung
				moveObjects(); //Bewegen von Objekten
				
			}else{
				System.out.println("Das Spiel ist leider beendet"); //Wenn Spiel beendet, wird GameLoop nicht mehr ausgeführt
			}
			
			repaint(); //Von Component geerbt, stößt Neuzeichnen an, gehört vllt. auch hinter die Schleife?
			
			try{
				Thread.sleep(10);
			}catch (InterruptedException e){}
		}	
	}
	
	private void computeDelta(){
		delta = System.nanoTime() - last; //Errechnung der Zeit für Schleifendurchlauf in NS
		last = System.nanoTime(); //Speichern der aktuellen Systemzeit
		
		fps = ((long) 1e9)/delta; //Errechnen der Framerate
	}
	
	public void paintComponent(Graphics g){ //paintComponent-Methode überschreiben
		super.paintComponent(g);
		
		g.setColor(Color.red);
		g.drawString("FPS " + Long.toString(fps), 20, 10); //Zur Überprüfung des fllüssigen Spiellaufs
		
		if(!isStarted()){
			return; //es wird erst gezeichnet, wenn Spiel gestartet ist
		}

		if(actors!=null){
			for(Drawable draw:actors){
				map.drawVisibleMap(g); //Erst Karte, dann Objekte!
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
		if(!up&&!down){ //wenn weder up noch down gedrückt
			player.setVerticalSpeed(0);
		}
		if(!left&&!right){ //wenn weder left noch rechts gedrückt
			player.setHorizontalSpeed(0);
		}
	}
	
	private void doLogic(){
		for(Movable mov:actors){
			mov.doLogic(delta);
		}
		
		//hier Kollisionsabfrage?
		
		if(gameover == 1){
			if(System.currentTimeMillis() -gameover > 3000){
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
		//TODO: Sinnvolle Ausgabe und Möglichkeit des Neustarts
	}
	
	public MapDisplay getMap(){
		return map; //gibt die Karte zurück
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
				stopGame();
			}else {
				//hier auch stopGame()?
				setStarted(false);
				System.exit(0);
			}
		}
	}
	
	public void keyTyped(KeyEvent e){
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
