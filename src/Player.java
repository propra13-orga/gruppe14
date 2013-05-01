import java.awt.image.BufferedImage;

public class Player extends Sprite {
	private static final long serialVersionUID = 1L;
	public Player(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
	}
	
	@Override
	public void doLogic(long delta)	{
		super.doLogic(delta);
		//Überprüfen der Ränder, damit Player nicht darüber hinaus kann
		if(getX()<0){
			setHorizontalSpeed(0);
			x = 0;
		}
		if(getY()<0){
			setHorizontalSpeed(0);
			y = 0;
		}
		
		if(getX()+getWidth()>parent.getWidth()){
			setVerticalSpeed(0);
			x = parent.getWidth()-getWidth();
		}
		
		if(getY()+getHeight()>parent.getHeight()){
			setVerticalSpeed(0);
			y = parent.getHeight()-getHeight();
		}
		
	}

	@Override
	public boolean collidedWith(Sprite s) {
		if(this.intersects(s)){
			System.out.println("Kollision Player");
			setHorizontalSpeed(0);
			setVerticalSpeed(0);
			return true;
		}
		return false;
	}
}
