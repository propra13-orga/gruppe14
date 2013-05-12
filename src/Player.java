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
		if(getX() < 0){
			setHorizontalSpeed(0);
			x = 0;
		}
		if(getY() < 0){
			setHorizontalSpeed(0);
			y = 0;
		}
		
		if(getX()+getWidth() > parent.getWidth()){
			setVerticalSpeed(0);
			x = parent.getWidth() - getWidth();
		}
		
		if(getY()+getHeight() > parent.getHeight()){
			setVerticalSpeed(0);
			y = parent.getHeight() - getHeight();
		}
		
		//System.out.println(getX());
		//System.out.println(getY());
		//Mittels der Abfrage der Farbe kann festgestellt werden, auf welchem Tile sich der Spieler zurzeit befindet, Stichwort: ShadowMap
		//Denkbar: Weitere Punkte überprüfen, um höhere Genauigkeit zu erreichen, hängt von Ausarbeitung der ShadowMap ab
		Color col1 = parent.getMap().getColorForPoint(new Point((int)(getX()), (int)getY())); //Ecke oben links
		Color col2= parent.getMap().getColorForPoint(new Point((int)(getX() + 40), (int)(getY() + 40))); //Ecke unten rechts, 40 = Höhe/Breite der Tiles
		Color col3= parent.getMap().getColorForPoint(new Point((int)(getX()), (int)(getY() + 40))); //Ecke unten links
		
		checkColor(col1);
		checkColor(col2);
		checkColor(col3);
	}

	private void checkColor(Color col){
		if(col.equals(Color.gray)){ //grau = 128, 128, 128
			System.out.println("Mauer");
			/*if (getVerticalSpeed() > 0){
				setVerticalSpeed(0);
			}
			if (getHorizontalSpeed() > 0){
				setHorizontalSpeed(0);
			}*/
			
			//TODO: Bei Kollision mit Mauer Stoppen des Spielers
		}
		
		if(col.equals(Color.green)){ //grün = 0, 255, 0
			System.out.println("Wiese");
		}
		
		if(col.equals(Color.red)){ //rot = 255, 0, 0
			System.out.println("Tod!");//Tod durch Feuer!
			parent.setStarted(false); //Spiel wird beendet, jedoch unschön - ohne Ausgabe und Neustartmöglichkeit
			//TODO: Spielbeendigung und entsprechende Ausgabe oder Verlust von Lebenspunkten
		}
		
		if(col.equals(Color.yellow)){ //gelb = 255, 255, 0
			System.out.println("Bravo, du hast den Ausgang gefunden!");
			if (parent.getLevel() == 1){
				
				parent.doInitializations2();
			}
			if (parent.getLevel() == 2){
				parent.doInitializations3();
			}
			//TODO: Level wechseln (s. gray)
		}
		
		if(col.equals(Color.blue)){
			System.out.println("Sieg!");
		}
		//Denkbar: Wenn Spieler auf...Asphalt oder ähnlichem erhöht sich Grundgeschwindigkeit
	}
	@Override
	public boolean collidedWith(Sprite s) {
		return false;
	}
}

