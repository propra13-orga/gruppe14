import java.util.TimerTask;


public class HealthTask extends TimerTask {
	Player player;
	/**
	 * Konstruktor fuer den HealthTask
	 * @param p, der Spieler fuer den der Healthktask vorgesehen ist
	 * **/
	public HealthTask(Player p){
		super();
		player = p;
	}
	/**
	 * Run-Methode, die ausgefuehrt wird, wenn der Timer den Impuls dazu gibt. Daraufhin wird canLooseHealth im Player auf true gesetzt
	 * **/
	@Override
	public void run() {
		player.setAbleToLoseHealth(true);
	}

}
