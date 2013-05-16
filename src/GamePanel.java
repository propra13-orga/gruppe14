import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel  implements Runnable, KeyListener{
	private static final long serialVersionUID = 1L;
	JFrame frame;
	JFrame frame2;
	
	long testtime = 0;
	
	long delta = 0;
	long last = 0;
	long fps = 0;
	long gameover = 0;
	
	SpriteLib lib;
	Player player;
	MapDisplay map;
	
	CopyOnWriteArrayList<Sprite> actors;

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
	int level;
	
	//VolatileImage als hardwarebeschleunigte Render-Methode verwendet einen 2fach Puffer, der zwischen aktuell berechnetem und altem Bild hin und her schaltet
	VolatileImage backbuffer;	//Der aktuelle Puffer
	GraphicsEnvironment ge;		//Umgebungsvariablen
	GraphicsConfiguration gc;	
	BufferStrategy strategy;	//Der 2Fach Puffer

	static int rows, columns;
	

	public static void main(String[] args){
		new GamePanel(800,600);
	}
	
	public GamePanel(int w, int h){
		
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		
		
		//frame.setIgnoreRepaint(true);	//Wird für den Pufferwechsel benötigt
		//createBufferStrategy(2);		//2Fach Puffer-Strategy wird angelegt
		//strategy = getBufferStrategy();	//Unser Puffer
		
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
		
		frame2 = new JFrame("Dungeon MYS");
		frame2.setLocation(600,350);
		paintMenu();
		
				
		
		/*Thread t = new Thread(this);
		t.start(); //ruft run auf
		run();*/
		
	}
	private void start(){	
		if(!once){//verhindert, dass bei Neustart neuer Thread gestartet wird
			once = true;
			Thread t = new Thread(this);
			t.start();
		}
		run();
	}
	
	private void doInitializations(){
		
		//createBackbuffer();		//Ein Puffer wird angelegt
		
		level = 1;
		last = System.nanoTime();
		gameover = 0;
		
		actors = new CopyOnWriteArrayList<Sprite>();
		
		lib = SpriteLib.getInstance();
		player = new Player(lib.getSprite("resources/pics/player.gif", 1, 1), 40, 40, 100, this);
		actors.add(player);
		
		//Erstellen der Karte, wobei die ersten 3 Parameter für die Eingabedateien stehen, die erste Zahl für die Anzahl der Spalten im TileSet, die zweite für die Anzahl der Zeilen
		map = new MapDisplay("level/TileMap.txt", "resources/pics/tiles.gif", "resources/pics/shadow.png", 5, 1, this);
		frame.setVisible(true);
		frame.add(this);

		
		
	}
	
	/*Methoden für die Volatile-Images Variante*/
	/*
	private void createBackbuffer(){
		if(backbuffer != null){
			backbuffer.flush();
			backbuffer = null;
		}
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		backbuffer = gc.createCompatibleVolatileImage(getWidth(), getHeight());
	}
	
	
	private void doPainting(){	//Neue Zeichenmethode basierend auf dem 2fach Puffer
		checkBackbuffer();		//Ist der Puffer da? (Volatile Images können verloren gehen, daher immer prüfen!)
		
		Graphics g = backbuffer.getGraphics();	//Graphics-Objekt vom Volatile Image holen
		render(g);								//Es wird ins Volatile-Image gezeichnet
		g.dispose();							//Graphics-Objekt wird nicht mehr benötigt
		
		Graphics g2 = strategy.getDrawGraphics();	//Graphics-Objekt von der Strategy holen
		g2.drawImage(backbuffer, 0, 0, this);		//Das backbuffer V.Image in die Strategy reinmalen
		g2.dispose();								//Graphics-Objekt wird nicht mehr benötigt
		
		strategy.show();							//Den aktuellen Puffer zeigen (Flip)
		
	}
	
	private void checkBackbuffer(){
		if(backbuffer == null){
			createBackbuffer();	
		}
		if(backbuffer.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE){
			createBackbuffer();
		}
	}
	//Render-Methode für die Volatile-Image, 2Fach-Puffer - Methode
	public void render(Graphics g){
		if(isStarted()){
			map.drawVisibleMap(g);
			player.drawObjects(g);
		}else{
			g.setColor(Color.red);
			g.drawString("Press Enter!", 50, 50);
		}
		g.setColor(Color.red);
		g.drawString(Long.toString(fps), 20, 20);
	}
	*/
	public void doInitializations2(){
		level = 2;
		//Player muss neu platziert werden
		map = new MapDisplay("level/TileMap_2.txt", "resources/pics/tiles.gif", "resources/pics/shadow.png", 5, 1, this); //auch entsprechend angepasste ShadowMap muss geladen werden! Man könnte auch verschiedene TileSets übergeben
		
	}
	
	public void doInitializations3(){
		level = 3;
		//Player muss neu platziert werden
		map = new MapDisplay("level/TileMap_3.txt", "resources/pics/tiles.gif", "resources/pics/shadow.png", 5, 1, this);
	}
	
	private void paintMenu(){ //Wird bisher noch nicht angesprochen, da Methode buggt (ArrayIndexOutOfBoundsException in MapDisplay.getColorForPoint)
		JButton b1 = new JButton("Spiel starten");
		
		JButton b2 = new JButton("Beenden");
		
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){ //bzgl. Starten
				setStarted(true);
				doInitializations();
				frame2.setVisible(false);
				frame2.dispose();
				start();
				run();
				
			}
		});
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1){ //bzgl. Schließen
				System.exit(0);
			}
			
		});
		
		frame2.add(b1);
		frame2.pack();
		frame2.setVisible(true);
	}

	
	public void run(){
		while(game_running){
			
			computeDelta(); //Zeit für vorausgehenden Schleifendurchlauf wird errechnet
			//Erst Methoden abarbeiten, wenn Spiel gestartet ist
			if(isStarted()){
				checkKeys(); //Tastaturabfrage
				
				
				doLogic(); //Ausführung der Logik
				
				
				moveObjects(); //Bewegen von Objekten
				
				
			}else{
				//System.out.println("Das Spiel ist nocht nicht gestartet bzw. beendet"); //Wenn Spiel beendet, wird GameLoop nicht mehr ausgeführt
			}
			
			//testtime = System.nanoTime();
			repaint();
			//doPainting();
			//System.out.println((System.nanoTime() - testtime)/1000);
			
			//repaint(); //Von Component geerbt, stößt Neuzeichnen an, gehört vllt. auch hinter die Schleife?
			
			try{
				
				Thread.sleep((System.nanoTime() - last + 1000000000)/60000000);	
				//Thread.sleep(40);
				
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
		
		if(actors!=null){
			for(ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
				Sprite draw = it.next();
				map.drawVisibleMap(g); //Erst Karte, dann Objekte! Karte muss nicht jedes mal neu gezeichnet werden - woandershin auslagern?
				draw.drawObjects(g);
			}
		}
		g.setColor(Color.red);
		g.drawString("FPS " + Long.toString(fps), 20, 10); //Zur Überprüfung des flüssigen Spiellaufs
		
		
		
	
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
		if(!up&&!down){ 						//wenn weder up noch down gedrückt
			player.setVerticalSpeed(0);
		}
		if(!left&&!right){ 						//wenn weder left noch rechts gedrückt
			player.setHorizontalSpeed(0);
		}
	}
	
	private void doLogic(){
		/*
		for(Movable mov:actors){
			mov.doLogic(delta);
		}*/
		//Neuerdings mit Iterator, der ist nämlich sicher vor Concurent-Modification-Exception (ist ja ne CopyOnWriteArrayList)
		for (ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
			Sprite r = it.next();
			r.doLogic(delta);
		}
		
		//hier Kollisionsabfrage?
		
		if(gameover == 1){
			if(System.currentTimeMillis() - gameover > 3000){
				stopGame();
			}
		}
	}
	
	private void moveObjects(){
		/*
		for(Movable mov:actors){
			mov.move(delta);
		}
		*/
		//Neuerdings mit Iterator, der ist nämlich sicher vor Concurent-Modification-Exception (ist ja ne CopyOnWriteArrayList)
		for (ListIterator<Sprite> it = actors.listIterator(); it.hasNext();){
			Sprite r = it.next();
			r.move(delta);
		}
	}
	
	private void stopGame(){
		setStarted(false);
		gameover = 1;
		//TODO: Sinnvolle Ausgabe und Möglichkeit des Neustarts
		//Oder Gamerunning auf false?
	}
	
	public void wonGame(){
		System.out.println("Bravo, du hast gewonnen! Möchtest du noch einmal spielen?");
		paintMenu();
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
		/*
		if (waitingForKeyPress){
			if (pressCount == 1){
				waitingForKeyPress = false;
				//startGame();
				pressCount = 0;
			} else{
				pressCount++;
			}
		}
		*/
	}

}
