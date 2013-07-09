import java.util.TimerTask;

//Klasse zur Verzögerung der Magieausuebung des Spielers (erweitert TimerTask)
public class MagicTask extends TimerTask {
	Player player;
	Enemy enemy;
	/**
	 * Konstruktor um einen MagicTask zu erstellen
	 * @param p Spieler, für den der Timer gestartet werden soll
	 */
	public MagicTask(Player p){
		super();
		player = p;
	}
	
	/**
	 * Run wird ausgeführt, sobald der Timer den Impuls dazu gibt
	 */
	@Override
	public void run(){		//Run wird ausgeführt, wenn der Timer (im Player) den Impuls dazu gibt
		player.setAbleToSummon(true);	//Dann wird die Angriffsmöglichkeit für den Spieler wiederhergestellt
	}
}
