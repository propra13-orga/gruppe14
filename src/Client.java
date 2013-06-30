import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;


//Nach Vorlage von Java ist auch eine Insel
public class Client extends Thread{
	protected Socket socket = null;
	PrintWriter out;
	BufferedReader in;
	int x, y, level;
	String ip;
	String in_string, out_string;
	GamePanel parent;
	
	public Client(String IP, GamePanel p){
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
	
	public void warte(){
		try {
			while(System.in.available() == 0 & !isInterrupted()){
				in_string = in.readLine(); //nehme Server-Nachricht entgegen
				System.out.println("Der Client hat empfangen: " + in_string);
				if(in_string.equals("level")){
					level = Integer.parseInt(in.readLine());
					System.out.println("Der Client hat empfangen: " + level);
				}else if(in_string.equals("Start")){
					parent.doInitializationsMulti(parent.frame4);
				}
			}
		}catch(IOException e){
			System.out.println("Verbindungsfehler "+ e);
		}
	}
	
	public void run(){
		warte();
	}
		
}