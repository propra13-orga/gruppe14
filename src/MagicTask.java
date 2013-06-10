import java.util.TimerTask;

//Klasse zur Verzögerung der Magieausuebung des Spielers (erweitert TimerTask)
public class MagicTask extends TimerTask {
	Player player;
	Enemy enemy;
	public MagicTask(Player p){
		super();
		player = p;
	}
	
	@Override
	public void run(){		//Run wird ausgeführt, wenn der Timer (im Player) den Impuls dazu gibt
		player.setAbleToSummon(true);	//Dann wird die Angriffsmöglichkeit für den Spieler wiederhergestellt
	}
}
