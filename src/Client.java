import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//Nach Vorlage von Java ist auch eine Insel
public class Client extends Thread{
	protected Socket socket = null;
	PrintWriter out;
	BufferedReader in;
	int x, y, level;
	String ip;
	String in_string, out_string;
	GamePanel parent;
	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean attack;
	boolean stop;
	boolean pos;
	int posx;
	int posy;
	
	/**
	 * Konstruktor des Clients, Initialisierung der wichtigsten Dinge
	 * @param IP Gibt gewünschte IP-Adresse an
	 * @param p Gibt GamePanel an, damit Zugriff auf dessen Methoden möglich ist
	 */
	public Client(String IP, GamePanel p){
		stop = false;
		parent = p;
		this.ip = IP;
		try{
			socket = new Socket(ip, 4711);
			out = new PrintWriter(socket.getOutputStream(), true);
		    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    out.println("Hey! Hier spricht der Client");
		    out.flush();
		    
		}catch(IOException e){
			System.out.println("Fehler: " + e);
		}
		
	}
	
	/**
	 * Schliesst den Client ordnungsgemaess, um einen Neustart zu ermoeglichen
	 * **/
	public void schliesse(){
		stop = true;
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Schliessen von Client fehlgeschlagen");
			e.printStackTrace();
		}
	}
	
	/**
	 * In dieser Dauerschleife wird zeilenweise ausgelesen, was der Server schickt
	 * Entsprechend den übermittelten Zeilen wird Logik für den 2. Spieler ausgeführt und dargestellt
	 * Auch der Chat wird hier realisiert, indem zuerst das Stichwort "Chat" gesendet wird und daraufhin die nächste Zeile im entsprechenden Fenster dargestellt wird
	 */
	public void warte(){
		if (stop == true){
			return;
		}
		try {
			while(System.in.available() == 0 & !isInterrupted()){
				in_string = in.readLine(); //nehme Server-Nachricht entgegen
//				System.out.println("Der Client hat empfangen: " + in_string);
				
				if(in_string.equals("level")){
					level = Integer.parseInt(in.readLine());
					System.out.println("Der Client hat empfangen: " + level);
				}else if(in_string.equals("Start")){
					parent.doInitializationsMulti(parent.frame4);
				}else if(in_string.equals("Chat")){
					String text = in.readLine();
					System.out.println("Der Client hat empfangen: " + text);
					parent.chatPane.setText(parent.chatPane.getText()  + "\n" + "Server: " + text);
					parent.chatarea.setText("");
				}else if(in_string.equals("Magic")){
				}else if(in_string.length() >= 13 && in_string.substring(0, 3).equals("pos")){
					posx = Integer.parseInt(in_string.substring(4, 8));
					posy = Integer.parseInt(in_string.substring(9, 13));
					pos = true;
//					System.out.println("Client: pos = true, posx = "+posx+", posy = "+posy);
				}else if(in_string.equals("Attack")){
					attack = true;
					checkKeys();
				}else if(in_string.equals("notattack")){
					attack = false;
					checkKeys();
				}else if(in_string.equals("Skill")){
					
				}else if(in_string.equals("up")){
					up = true;
					checkKeys();					
				}else if(in_string.equals("left")){
					left = true;
					checkKeys();					
				}else if(in_string.equals("right")){
					right = true;
					checkKeys();
				}else if(in_string.equals("down")){
					down = true;
					checkKeys();
				}else if(in_string.equals("notup")){
					up = false;
					checkKeys();					
				}else if(in_string.equals("notleft")){
					left = false;
					checkKeys();					
				}else if(in_string.equals("notright")){
					right = false;
					checkKeys();
				}else if(in_string.equals("notdown")){
					down = false;
					checkKeys();
				}
			}
		}catch(IOException e){
			System.out.println("Verbindungsfehler "+ e);
		}
	}
	
	/**
	 * Run-Methode des Threads
	 */
	public void run(){
		warte();
		if (stop == true){
			return;
		}
	}
	
	/**
	 * Methode zur Logik des Gegenspielers
	 * Erhält der Server Information zu Spieler 2 wird hier entsprechend der boolean-Werte der Spieler manipuliert
	 */
	public void checkKeys(){
		if(left){
			parent.player2.setHorizontalSpeed(-parent.speed);
		}
		if(right){
			parent.player2.setHorizontalSpeed(parent.speed);
		}
		if(down){
			parent.player2.setVerticalSpeed(parent.speed);
		}
		if(up){
			parent.player2.setVerticalSpeed(-parent.speed);
		}
		if(!up&&!down){ 						//wenn weder up noch down gedrückt
			parent.player2.setVerticalSpeed(0);
		}
		if(!left&&!right){ 						//wenn weder left noch rechts gedrückt
			parent.player2.setHorizontalSpeed(0);
		}
		if(attack){
			parent.attack2 = true;
		}
		if(!attack){
			parent.attack2 = false;
		}
		if(pos){
			pos = false;
			parent.player2.setPosition(posx, posy);
		}
	}
}