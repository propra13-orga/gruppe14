import java.util.TimerTask;

//Klasse zur Verz�gerung des Angriffes des Spielers (erweitert TimerTask)
public class AttackTask extends TimerTask {
	Player player;
	Enemy enemy;
	public AttackTask(Player p){
		super();
		player = p;
	}
	
	@Override
	public void run(){		//Run wird ausgef�hrt, wenn der Timer (im Player) den Impuls dazu gibt
		player.setAbleToAttack(true);	//Dann wird die Angriffsm�glichkeit f�r den Spieler wiederhergestellt
	}
}