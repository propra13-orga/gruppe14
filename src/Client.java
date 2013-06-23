
public abstract class Client {

	protected Connection connection;
	private ClientListener listener;
	
	public Client(String serverName, int port){
		connection = new Connection(serverName, port);
		listener = new ClientListener(connection, this);
	}
	
	protected void start(){
		listener.start();
	}
	
	public void print(String text){
		
	}
	
	class ClientListener extends Thread{
		private Connection connection;
		private Client client;

		public ClientListener(Connection connection, Client client){
			this.connection = connection;
			this.client = client;
		}
		
		public synchronized void run(){
			String nachricht;
			nachricht = connection.receive();
			while (nachricht != null){
				System.out.println(nachricht);
				print(nachricht);
				nachricht = connection.receive();
			}
		}
		
	}
	
}