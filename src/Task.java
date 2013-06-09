import java.util.TimerTask;

//Klasse zur Verzögerung des Angriffes des Spielers (erweitert TimerTask)
public class Task extends TimerTask {
	Player player;
	public Task(Player p){
		super();
		player = p;
	}
	@Override
	public void run(){		//Run wird ausgeführt, wenn der Timer (im Player) den Impuls dazu gibt
		player.setAbleToAttack(true);	//Dann wird die Angriffsmöglichkeit für den Spieler wiederhergestellt
	}
}
