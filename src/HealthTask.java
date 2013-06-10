import java.util.TimerTask;


public class HealthTask extends TimerTask {
	Player player;
	
	public HealthTask(Player p){
		super();
		player = p;
	}
	@Override
	public void run() {
		player.setAbleToLoseHealth(true);
	}

}
