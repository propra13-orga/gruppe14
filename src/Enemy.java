
import java.awt.image.BufferedImage;

public class Enemy extends Sprite {
	private static final long serialVersionUID = 1L;
	
	public Enemy(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
	}
	
	@Override
	public void doLogic(long delta)	{
		super.doLogic(delta);		
	}
	@Override
	public void move(long delta){ //Wenn delta ungleich null, werden Positionen verändert
		//dx = Verschiebung inkl. Speed
	
		if(dx != 0){
			x = x + dx*(delta/1e9);
		}
		if(dy != 0){
			y = y + dy*(delta/1e9);
		}
		if(getX() < 0){
			setHorizontalSpeed(-getHorizontalSpeed());
			x = 0;
		}
		if(getY() < 0){
			setVerticalSpeed(-getVerticalSpeed());
			y = 0;
		}
		if(getX()+getWidth() > parent.getWidth()){
			setHorizontalSpeed(-getHorizontalSpeed());
			x = parent.getWidth() - getWidth();
		}
		
		if(getY()+getHeight() > parent.getHeight()){
			setVerticalSpeed(-getVerticalSpeed());
			y = parent.getHeight() - getHeight();
		}
	}
	public boolean collidedWith(Sprite s) {
		return false;
	}
	
}

