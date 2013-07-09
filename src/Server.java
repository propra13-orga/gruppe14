import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class  Server extends Thread{
	
	ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private int port;
	PrintWriter out;
	BufferedReader in;
	int meldungen_zaehler, x, y;
	boolean connected = false;
	String in_string;
	int posx;
	int posy;
	GamePanel parent;
	JFrame f;

	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean attack;
	
	/**
	 * Konstruktor f�r den Server, es wird ein neues ServerSocket erstellt
	 * @param port gibt den Port an, mit dem das ServerSocket erstellt wird
	 * @param p gibt das GamePanel an, damit Zugriff zu dessen Methoden m�glich ist
	 */
	public Server(int port, GamePanel p){
		this.port = port; 
		parent = p;
		try{
			serverSocket = new ServerSocket(port); //erstellt ServerSocket mit Port port
			System.out.println("serverSocket erfolgreich erstellt");
		}catch(IOException e){
			System.out.println("Fehler " + e);
		}		
	}
	
	/**
	 * Hier wird gewartet, bis sich ein Client verbindet und dann eien Willkommensnachricht versendet
	 */
	public void starten(){
		//Client muss eine Port-Adresse zugeteilt sein, sonst IOException! Aber �ber 1023!
		waitingWindow();
		while(true){
			try{
				
				clientSocket = serverSocket.accept(); //nimmt wartende Verbindungen an
				//Verbindungsstatus aktualisieren:
				connected = true;
				
				//hier im GamePanel multiplayer = true
				out = new PrintWriter(clientSocket.getOutputStream(), true);
			    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			    
			    out.println("Hallo Client! Hier spricht der Server");
				out.flush();

			    warte();		
				
			}catch (IOException e){
				System.out.println("Verbindungsfehler: " +e);
			}
		}
		
	}
	
	/**
	 * In dieser Dauerschleife wird zeilenweise ausgelesen, was der Client schickt
	 * Entsprechend den �bermittelten Zeilen wird Logik f�r den 2. Spieler ausgef�hrt und dargestellt
	 * Auch der Chat wird hier realisiert, indem zuerst das Stichwort "Chat" gesendet wird und daraufhin die n�chste Zeile im entsprechenden Fenster dargestellt wird
	 */
	public void warte(){
		try{
			//Server extends Thread!
			while(System.in.available() == 0 && !isInterrupted()){
				in_string = in.readLine(); //Client-Nachricht entgegen nehmen
				//Je nach Eingabe f�hre verschiedene aus
				//M�gliche Begriffe: "Magic", "Attack", "Skill", "Shop", "Position", entsprechend muss der Server reagieren, vllt. mehrere Operanden danach noch auslesen etc.
				if(in_string.equals("Schluss")){
					clientSocket.close();//Schie�en)
				}else{
					//System.out.println("Der Server hat empfangen: " + in_string);
				}
				
				if(in_string.equals("Magic")){
					
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
				}else if(in_string.equals("Hey! Hier spricht der Client")){ 
					f.dispose();
					out.println("Start");
					out.flush();
					parent.doInitializationsMulti(parent.frame4);
				
				}else if(in_string.equals("Chat")){
					String text = in.readLine();
					System.out.println("Der Server hat empfangen: " + text);
					parent.chatPane.setText( parent.chatPane.getText() + "\n" + "Client: " + text);
					parent.chatarea.setText("");
				}
			}
		}catch (IOException e){
			System.out.println("Verbindungfehler: " +e);
		}
	}
	
	/**
	 * Run-Methode des Threads
	 */
	public void run(){
		starten();
	}
	
	/**
	 * Methode zur Darstellung eiens Wartefensters bis sich der Client verbindet
	 * Dieses Fenster wird angezeigt, solange sich der Client noch nicht verbunden hat. Der User hat in dieser Zeit die M�glichkeit die Verbindung zu unterbrechen, falls es ihm zu lange dauert
	 */
	public void waitingWindow() {
		// Richte JFrame ein
		f = new JFrame("Warte auf Client");
		f.setLocation(650, 300);
		f.setBackground(Color.darkGray);
		f.setSize(100, 90);	
		// Richte JButton ein
		JButton abbrechen = new JButton("Abbrechen");
		abbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				f.setVisible(false);
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		// Richte Ausgabetext ein
		JLabel text = new JLabel("Warte auf Client");

		f.add(BorderLayout.NORTH, text);
		f.add(BorderLayout.CENTER, abbrechen);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

	}
	
	/**
	 * Methode zur Logik des Gegenspielers
	 * Erh�lt der Server Information zu Spieler 2 wird hier entsprechend der boolean-Werte der Spieler manipuliert
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
		if(!up&&!down){ 						//wenn weder up noch down gedr�ckt
			parent.player2.setVerticalSpeed(0);
		}
		if(!left&&!right){ 						//wenn weder left noch rechts gedr�ckt
			parent.player2.setHorizontalSpeed(0);
		}
		if(attack){
			parent.attack2 = true;
		}
		if(!attack){
			parent.attack2 = false;
		}
	}
}
