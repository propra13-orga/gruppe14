import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Enemy extends Sprite {
	private static final long serialVersionUID = 1L;
	
	Rectangle2D.Double target;
	boolean locked = false; //noch kein Ziel erfasst
	
	public Enemy(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
	}
	
	@Override
	public void doLogic(long delta)	{
		super.doLogic(delta);		
		//Idee für Verfolgung von Spieler:
		/*if(getHorizontalSpeed() > 0){
			target = new Rectangle2D.Double(getX() + getWidth(), getY(), parent.getWidth() - getX(), getHeight());
		}else{
			target = new Rectangle2D.Double(0, getY(), getX(), getHeight());
		}
		
		if(!locked && parent.player.intersects(target)){
			setVerticalSpeed(0);
			locked = true;
		}
		
		if(locked){
			if(getY() < parent.player.getY()) {
				setVerticalSpeed(40);
			}
			if(getY() > parent.player.getY() + parent.player.getHeight()){
				setVerticalSpeed(-40);
			}
		}
		
		if(getVerticalSpeed() > 0){
			target = new Rectangle2D.Double(getX() + getWidth(), getY(), parent.getWidth() - getX(), getHeight());
		}else{
			target = new Rectangle2D.Double(0, getY(), getX(), getHeight());
		}
		
		if(!locked && parent.player.intersects(target)){
			setVerticalSpeed(0);
			locked = true;
		}
		
		if(locked){
			if(getY() < parent.player.getY()) {
				setVerticalSpeed(40);
			}
			if(getY() > parent.player.getY() + parent.player.getHeight()){
				setVerticalSpeed(-40);
			}
		}*/
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
		
	public void drawObjects(Graphics g){
		super.drawObjects(g);
		g.setColor(Color.ORANGE);
	}
	public boolean collidedWith(Sprite s){
		if(this.intersects(s)){
			System.out.println("Ausgabe von Enemy: Lecker, lecker Ohren!");
			return true;
		}
		return false;
	}
	
	
}

