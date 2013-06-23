import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	private String serverName;
	private int port;
	
	public Connection(String serverName, int port){
		this.serverName = serverName;
		this.port = port;
		connect();
	}
	
	public Connection(Socket socket){
		s = socket;
		try{
			//Objekt zum Versand von Nachrichten über den Socket erzeugen
			out = new PrintWriter(s.getOutputStream(), true);
			//Objekt zum Empfangen vno Nachrichten über das Socketobjekt erzeugen
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String connect(){
		try{
			s = new Socket(serverName, port); //
			//Objekt zum Versenden von Nachrichten über den Socket erzeugen
			out = new PrintWriter(s.getOutputStream(), true); //pr und br zweimal instanzieren?
			//Objekt zum Empfangen von Nachrichten über dem Socketobjekt
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			return "Verbindung: " + s;
		} catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	public String receive(){
		try{
			return in.readLine();
		} catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void send(String nachricht){
		out.println(nachricht);
		out.flush();
	}
	
	public synchronized void syncSend(String nachricht){
		out.println(nachricht);
		out.flush();
	}
	
	public String ownPort(){
		return "" + s.getLocalPort();
	}
	
	public String ownAddress(){
		return "" + s.getLocalAddress();
	}
	
	public String deletedAdress(){
		return "" + s.getInetAddress();
	}
	
	public String deletedPort(){
		return "" + s.getPort();
	}
	
	public boolean isConnected(){
		return s.isConnected();
	}
	
	public void close(){
		try{
			s.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
