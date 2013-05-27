import java.awt.image.BufferedImage;


public class Coin extends Sprite {

	
	public Coin(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public boolean collidedWith(Sprite s) {
		// TODO Auto-generated method stub
		return false;
	}

}
