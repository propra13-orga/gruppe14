import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


//angelehnt an ProPra12 - Gruppe 09
public class  Server extends Thread{
	
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private int port;
	PrintWriter out;
	BufferedReader in;
	int meldungen_zaehler, x, y;
	boolean connected = false;
	String in_string;
	
	public Server(int port){
		this.port = port; 
		try{
			serverSocket = new ServerSocket(port); //erstellt ServerSocket mit Port port
			System.out.println("serverSocket erfolgreich erstellt");
		}catch(IOException e){
			System.out.println("Fehler " + e);
		}		
	}
	
	public void starten(){//s. gruppe 23
		//Client muss eine Port-Adresse zugeteilt sein, sonst IOException! Aber über 1023!
		while(true){
			try{
				
				clientSocket = serverSocket.accept(); //nimmt wartende Verbindungen an
				//Verbindungsstatus aktualisieren:
				connected = true;
				System.out.println("Hallo Client!");
				//hier im GamePanel multiplayer = true
				out = new PrintWriter(clientSocket.getOutputStream(), true);
			    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			    
			    //Levelnummer schicken
			    out.println("level");
			    out.println(3);
			    out.flush();

			    warte();			
				
			}catch (IOException e){
				System.out.println("Verbindungsfehler: " +e);
			}
		}
		
	}
	
	public void warte(){
		try{
			//Server extends Thread!
			while(System.in.available() == 0 && !isInterrupted()){
				in_string = in.readLine(); //Client-Nachricht entgegen nehmen
			
				//Je nach Eingabe führe verschiedene aus
				if(in_string.equals("Schluss")){
					clientSocket.close();//Schießen)
				}else{
					out.write("Dies ist nur ein Test");
				}
			}
		}catch (IOException e){
			System.out.println("Verbindungfehler: " +e);
		}
	}
	
	public void run(){
		starten();
	}
}
