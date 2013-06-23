import java.awt.image.BufferedImage;


public class FireEnemy extends Enemy{

	private static final long serialVersionUID = 1L;
	public FireEnemy(BufferedImage[] i, double x, double y, int damage, long delay, GamePanel p) {
		super(i, x, y, damage, delay, p);
		
	}
	@Override
	public void reduceHealth(int schaden){ //TODO: Timer einbauen mit setAbleToLoseHeatlh und canLoseHealth
		int w = parent.player.hasWeapon();
		if(w == 1){
			health = health - 3*schaden;
		}else if(w == 2){
			health = health - schaden/3;
		}else{
			health = health - schaden;
		}
		if(health <= 0){ //Wenn Lebenspunkte aufgebraucht: Enemy aus ActorsListe löschen
			remove = true;
			parent.player.setCoins(parent.player.getCoins() + 20); //Spieler kriegt 20 Münzen für das Killen eines Gegner
			parent.player.setXP(parent.player.getXP() + 1);			//Der Spieler bekommt Erfahrungspunkte für das Töten eines Gegners
			
		}
	}
}
