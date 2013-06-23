import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
	JFrame f = new JFrame("Auf Client warten");
	
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
		waitingWindow();
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
				System.out.println(in_string);
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
	
	public void waitingWindow() {
		// Richte JFrame ein
		f.setLocation(400, 400);
		f.setPreferredSize(new Dimension(300,100));
		f.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
				try {
					serverSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }
		});
		
		// Richte JButton ein
		JButton abbrechen = new JButton();
		abbrechen.setText("Abbrechen");
		
		// Bei Klick auf abbrechen
		abbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				f.setVisible(false);
				f.dispose();
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		// Richte Ausgabetext ein
		JLabel text = new JLabel("Bitte warten bis Client verbindet.");
		text.setHorizontalAlignment(JLabel.CENTER);
		text.setVerticalAlignment(JLabel.TOP);
		
		f.add(text);
		f.add(BorderLayout.CENTER, abbrechen);
		f.pack();
		f.setVisible(true);
	}
	
}
