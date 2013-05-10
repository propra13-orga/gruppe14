import java.awt.Color;
import java.awt.Point;
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
		
		//Mittels der Abfrage der Farbe kann festgestellt werden, auf welchem Tile sich der Spieler zurzeit befindet, Stichwort: ShadowMap
		Color col1 = parent.getMap().getColorForPoint(new Point((int)(getX()), (int)getY()));
		Color col2= parent.getMap().getColorForPoint(new Point((int)(getX()+getWidth()/2), (int)getY()));
		Color col3 = parent.getMap().getColorForPoint(new Point((int)(getX()+getWidth()), (int)getY()));	
	
		checkColor(col1);
		checkColor(col2);
		checkColor(col3);
	}

	private void checkColor(Color col){
		if(col.equals(Color.gray)){
			//Wenn Spieler auf Mauer, soll er nicht weitergehen können -> Manipulation der Geschwindigkeit?
		}
		
		if(col.equals(Color.green)){
		}
		//Denkbar: Wenn Spieler auf...Asphalt oder ähnlichem erhöht sich Grundgeschwindigkeit
	}
	@Override
	public boolean collidedWith(Sprite s) {
		return false;
	}
}

