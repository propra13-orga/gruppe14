import java.util.TimerTask;

//Klasse zur Verzögerung des Angriffes des Spielers (erweitert TimerTask)
public class AttackTask extends TimerTask {
	Player player;
	Enemy enemy;
	/**
	 * Konstruktor fuer den AttackTask
	 * @param p, der Spieler fuer den der Attacktask vorgesehen ist
	 * **/
	public AttackTask(Player p){
		super();
		player = p;
	}
	
	/**
	 * Run-Methode, die ausgefuehrt wird, wenn der Timer den Impuls dazu gibt. Daraufhin wird canAttack im Player auf true gesetzt
	 * **/
	@Override
	public void run(){		//Run wird ausgeführt, wenn der Timer (im Player) den Impuls dazu gibt
		player.setAbleToAttack(true);	//Dann wird die Angriffsmöglichkeit für den Spieler wiederhergestellt
	}
}
