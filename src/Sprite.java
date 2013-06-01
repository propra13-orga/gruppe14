import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


//implementiert die Interfaces moveable und drawable
public abstract class Sprite extends Rectangle2D.Double implements Drawable, Movable{
	private static final long serialVersionUID = 1L;
	long delay;
	long animation = 0;
	
	protected double dx; //horizontale Veränderung, bzw. Position
	protected double dy; //vertikale Veränderung, bzw. Position
	
	GamePanel parent;
	BufferedImage[] pics;
	int currentpic = 0;
	
	boolean remove;
	

	
	public Sprite(BufferedImage[] i, double x, double y, long delay, GamePanel p ){
		pics = i;
		this.x = x;
		this.y = y;
		this.delay = delay;
		this.width = pics[0].getWidth();
		this.height = pics[0].getHeight();
		parent = p;
	}
	
	public void drawObjects(Graphics g){
		g.drawImage(pics[currentpic], (int) x, (int) y, null); //x, y runden
	}
	
	public void doLogic(long delta){
		animation = animation + (delta/1000000);
		if (animation > delay){
			animation = 0;
			computeAnimation();
		}
	}
	
	private void computeAnimation(){
		currentpic++;
		
		if(currentpic >= pics.length){ //Wenn Anzahl der vorhandenen Bilder überschreitet
			currentpic = 0;
		}
	}
	
	public void move(long delta){ //Wenn delta ungleich null, werden Positionen verändert
		if(dx != 0){
			x = x + dx*(delta/1e9);
		}
		
		if(dy != 0){
			y = y + dy*(delta/1e9);
		}
	}
	
	public double getHorizontalSpeed() {
		return dx;
	}

	public void setHorizontalSpeed(double dx) {
		this.dx = dx;
	}

	public double getVerticalSpeed() {
		return dy;
	}

	public void setVerticalSpeed(double dy) {
		this.dy = dy;
	}

	public abstract boolean collidedWith(Sprite s);
}
