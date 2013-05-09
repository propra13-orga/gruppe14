import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	private static final long serialVersionUID = 1L;
	JFrame frame;
	
	long delta = 0;
	long last = 0;
	long fps = 0;
	
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
	
	BufferedImage background;
	//Main-Methode
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
		doInitializations();
		
		Thread t = new Thread(this);
		t.start();
	}
	
	private void doInitializations(){
		
		last = System.nanoTime();
		background = loadPics("pics/background.jpg", 1) [0];
		actors = new Vector<Sprite>();
		
		lib = SpriteLib.getInstance();
		player = new Player(lib.getSprite("pics/player.gif", 1, 1), 50, 50, 100, this);
		actors.add(player);
		
		map = new MapDisplay("level/TileMap.txt", "pics/tiles_2.gif", "pics/shadow.png", 1, 1, this);

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
			}
			
			
			repaint(); //Von Component geerbt, stößt Neuzeichnen an
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
		
		g.drawImage(background, 0, 0, this); //liegt ganz hinten, alles andere wird darüber gezeichnet
		
		g.setColor(Color.red);
		g.drawString("FPS " + Long.toString(fps), 20, 10); //Zur Überprüfung des fllüssigen Spiellaufs
		
		if(!isStarted()){
			return; //es wird erst gezeichnet, wenn Spiel gestartet ist
		}
		
		/*
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < columns; j++){
				
				int mod_i = 40*i;
				int mod_j = 40*j;
				
				switch (tilemap[i][j]) {
				case 0:
					g.drawImage(mauer, mod_i, mod_j, this);
				break;
				case 1:
					g.drawImage(gras, mod_i, mod_j, this);
				break;
				}
			}
		}*/ //gehört hier nicht hin und verlangsamt ungemein!
		

		if(actors!=null){
			for(Drawable draw:actors){
				map.drawVisibleMap(g); //Erst Karte, dann Objekte!
				draw.drawObjects(g);
				
				//g.drawImage(ImageControl.getInstance().getImageAt(0), 300, 50, this); //nur zum Testen - Tile wird korrekt dargestellt!
			}
		}
	
	}
	
	private BufferedImage[] loadPics(String path, int pics){
		
		BufferedImage[] anim = new BufferedImage[pics];
		BufferedImage source = null;
		URL pic_url = getClass().getClassLoader().getResource(path);
		
		try {
			source = ImageIO.read(pic_url); //null-Exception oO
		} catch (IOException e) {}
		
		for(int x=0;x < pics;x++){
			anim[x] = source.getSubimage(x*source.getWidth()/pics, 0, source.getWidth()/pics, source.getHeight());
		}
		
		return anim;
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
		
		for(int i = 0; i < actors.size(); i++){
			for(int n = i+1; n < actors.size(); n++){
				Sprite s1 = actors.elementAt(i);
				Sprite s2 = actors.elementAt(n);
				
				s1.collidedWith(s2);
								
			}
		}
	}
	
	private void moveObjects(){
		for(Movable mov:actors){
			mov.move(delta);
		}
	}
	
	
	public void keyPressed(KeyEvent e){
		
		//Eleganter wäre ein Switch
		if (e.getKeyCode() == KeyEvent.VK_LEFT){ //linke Pfeiltaste
			left = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){ //rechte Pfeiltaste
			right = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP){ //Pfeil nach oben
			up = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){//Pfeil nach unten
			down = true;
		}
	}
	//Taste wieder losgelassen?
	public void keyReleased(KeyEvent e){
				
		if (e.getKeyCode() == KeyEvent.VK_LEFT){
			left = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){
			right = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP){
			up = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){
			down = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER){
			if(!isStarted()){
				doInitializations();
				setStarted(true);
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
			if(isStarted()){
				setStarted(false);
			}else {
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
	
	public MapDisplay getMap(){
		return map;
	}
}
