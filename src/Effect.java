import java.awt.image.BufferedImage;


public class Effect extends Sprite {

	private static final long serialVersionUID = 1L;
	int old_pic = 0;
	
	public Effect(BufferedImage[] i, double x, double y, long delay, GamePanel p ){
		super(i, x, y, delay, p);
		
	}

	@Override
	public void doLogic(long delta){
		old_pic = currentpic;
		super.doLogic(delta);
		if(currentpic == 0 && old_pic != 0){
			remove = true;
		}
	}
	
	public boolean collidedWith(Sprite s){
		return false;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}
		
}
