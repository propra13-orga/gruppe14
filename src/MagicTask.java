import java.util.TimerTask;

//Klasse zur Verz�gerung der Magieausuebung des Spielers (erweitert TimerTask)
public class MagicTask extends TimerTask {
	Player player;
	Enemy enemy;
	/**
	 * Konstruktor um einen MagicTask zu erstellen
	 * @param p Spieler, f�r den der Timer gestartet werden soll
	 */
	public MagicTask(Player p){
		super();
		player = p;
	}
	
	/**
	 * Run wird ausgef�hrt, sobald der Timer den Impuls dazu gibt
	 */
	@Override
	public void run(){		//Run wird ausgef�hrt, wenn der Timer (im Player) den Impuls dazu gibt
		player.setAbleToSummon(true);	//Dann wird die Angriffsm�glichkeit f�r den Spieler wiederhergestellt
	}
}
