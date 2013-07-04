import java.awt.image.BufferedImage;


public class Effect extends Sprite {

	private static final long serialVersionUID = 1L;
	int old_pic = 0;
	
	public Effect(BufferedImage[] i, double x, double y, long delay, GamePanel p ){
		super(i, x, y, delay, p);
		
	}
	/**
	 * Logik-Methode, die einmal ueber die Einzelbilder iteriert und danach die Freigabe zum Loeschen des Effekts gibt.
	 * @param delta, long-Wert, der die Differenz zum letzen Haupt-Schleifendurchlauf angibt. Fuer fluessige Bewegung.
	 * **/
	@Override
	public void doLogic(long delta){
		old_pic = currentpic;
		super.doLogic(delta);
		if(currentpic == loop_from && old_pic != loop_from){
			remove = true;
		}
	}
	/**
	 * Immer false;
	 * @return false
	 * **/
	public boolean collidedWith(Sprite s){
		return false;
	}
	/**
	 * Immer 0
	 * @return 0
	 * **/
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}
		
}
