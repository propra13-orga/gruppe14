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
	
	public void starten(String IP){
		this.ip = IP;
		try{
			socket = new Socket(ip, 4711);
			out = new PrintWriter(socket.getOutputStream(), true);
		    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    out.write("Hey!");
		    System.out.println("Hallo Server!");
		    warte();
		    
		}catch(IOException e){
			System.out.println("Fehler: " + e);
		}
		
	}
	
	public void warte(){
		try {
			while(System.in.available() == 0 & !isInterrupted()){
				in_string = in.readLine(); //nehme Server-Nachricht entgegen
				System.out.println(in_string);
				if(in_string.equals("level")){
					System.out.println("Empfangenes Level vom Server: ");
					level = Integer.parseInt(in.readLine());
					System.out.println(level);
				}
				out.write("Der Client schickt etwas an den Server");
				
			}
		}catch(IOException e){
			System.out.println("fehlerchen "+ e);
		}
	}
	
	public void run(){
		starten("127.0.0.1");
	}
		
}