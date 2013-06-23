import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

//angelehnt an ProPra12 - Gruppe 09
public abstract class  Server extends Thread{
	
	private Prozess prozess;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private int port;
	PrintWriter out;
	BufferedReader in;
	boolean connected = false;
	
	public Server(int port){
		this.port = port;
	}
	
	public void start(){
		try {
			serverSocket = new ServerSocket(port);
			while(true){//Dauerschleife
				//Warten auf eingehende Verbindungen
				Socket socket = serverSocket.accept(); //Erzeugung eines Sockets zur Kommunikation mit dem Client
				System.out.println("Verbindung von " + socket);
				Connection verbindung = new Connection(socket);
				prozess = new Prozess(verbindung, this);
				prozess.start();
			}
		}catch (IOException e){
			System.out.println("Verbindungfehler: " +e);
		}
	}
	
	public void starten(){//s. gruppe 23
		try{
			serverSocket = new ServerSocket(4711);
			System.out.println("Ihre IP-Adresse: ");
			String localHost = InetAddress.getLocalHost().getHostName();
			
			clientSocket = serverSocket.accept();
			//Verbindungsstatus aktualisieren:
			connected = true;
			//hier im GamePanel multiplayer = true
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    
		    //Levelnummer schicken
		    out.println("level");
		    //out.println(parent.level); //parent?
		    
		    warte();
		}catch (IOException e){
			System.out.println("Verbindungsfehler: " +e);
		}
	}
	
	void warte(){
		try{
			//Server extends Thread!
			while(System.in.available() == 0 && !isInterrupted()){
				String in_string = in.readLine(); //Client-Nachricht entgegen nehmen
				
				if(in_string.equals("Schluss")){
					//Schieﬂen)
				}
			}
		}catch (IOException e){
			System.out.println("Verbindungfehler: " +e);
	}
	}
	public abstract void protokoll(Connection verbindung);
	
	public class Prozess extends Thread{
		private Connection verbindung;
		private Server server;
		
		public Prozess(Connection verbindung, Server server){
			super();
			this.verbindung = verbindung;
			this.server = server;
		}
		
		public void run(){
			server.protokoll(verbindung);
		}
	}
	
	public void run(){
		starten();
	}
}
