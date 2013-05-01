import java.awt.image.BufferedImage;


public class Wall extends Sprite {
	private static final long serialVersionUID = 1L;

	public Wall(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
	}
	
	@Override
	public void doLogic(long delta){
		
	}

	@Override
	public boolean collidedWith(Sprite s) {
		if(this.intersects(s)){
			System.out.println("Kollision Player");
				return true;
		}
		return false;		
	}
}
