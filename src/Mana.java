import java.awt.image.BufferedImage;


public class Mana extends Sprite	 {

	private static final long serialVersionUID = 1L;
	
	public Mana(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		
		super(i, x, y, delay, p);

	}

	@Override
	public boolean collidedWith(Sprite s) {
		// TODO Auto-generated method stub
		return false;
	}

}
