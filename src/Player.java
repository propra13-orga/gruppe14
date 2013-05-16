import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class Player extends Sprite {
	private static final long serialVersionUID = 1L;
	int corner;
	
	
	public Player(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
	}
	
	@Override
	public void doLogic(long delta)	{
		super.doLogic(delta);
		//�berpr�fen der R�nder, damit Player nicht dar�ber hinaus kann
		if(getX() < 0){
			setHorizontalSpeed(0);
			x = 0;
		}
		if(getY() < 0){
			setVerticalSpeed(0);
			y = 0;
		}
		
		if(getX()+getWidth() > parent.getWidth()){
			setHorizontalSpeed(0);
			x = parent.getWidth() - getWidth();
		}
		
		if(getY()+getHeight() > parent.getHeight()){
			setVerticalSpeed(0);
			y = parent.getHeight() - getHeight();
		}
		
		//System.out.println(getX());
		//System.out.println(getY());
		//Mittels der Abfrage der Farbe kann festgestellt werden, auf welchem Tile sich der Spieler zurzeit befindet, Stichwort: ShadowMap
		//Denkbar: Weitere Punkte �berpr�fen, um h�here Genauigkeit zu erreichen, h�ngt von Ausarbeitung der ShadowMap ab
		corner = 1;
		Color col1 = parent.getMap().getColorForPoint(new Point((int)(getX()), (int)getY())); //Ecke oben links
		checkColor(col1);
		corner = 2;
		Color col2 = parent.getMap().getColorForPoint(new Point((int)(getX() + 40), (int)(getY() + 40))); //Ecke unten rechts, 40 = H�he/Breite der Tiles
		checkColor(col2);
		corner = 3;
		Color col3 = parent.getMap().getColorForPoint(new Point((int)(getX()), (int)(getY() + 40))); //Ecke unten links
		checkColor(col3);
		corner = 4;
		Color col4 = parent.getMap().getColorForPoint(new Point((int)(getX() + 40), (int)getY())); //Ecke oben rechts
		checkColor(col4);
		
		
		
	
		
		
	}
	private void checkColor(Color col){
		switch(corner){
		case 1:
			if(col.equals(Color.gray)){ //grau = 128, 128, 128
				
				if(parent.up){
					setVerticalSpeed(0);
					y = y + 1;
				}
				if(parent.down){
					//setVerticalSpeed(0);
					//y = y - 1;
				}
				if(parent.left){
					setHorizontalSpeed(0);
					x = x + 1;
				}
				if(parent.right){
					//setHorizontalSpeed(0);
					//x = x - 1;
				}
			
			}
		case 2:
			if(col.equals(Color.gray)){
				
				if(parent.up){
					//setVerticalSpeed(0);
					//y = y + 1;
				}
				if(parent.down){
					setVerticalSpeed(0);
					y = y - 1;
				}
				if(parent.left){
					//setHorizontalSpeed(0);
					//x = x + 1;
				}
				if(parent.right){
					setHorizontalSpeed(0);
					x = x - 1;
				}
				
			}
			
		case 3:
			if(col.equals(Color.gray)){
				
				if(parent.up){
					//setVerticalSpeed(0);
					//y = y + 1;
				}
				if(parent.down){
					setVerticalSpeed(0);
					y = y - 1;
				}
				if(parent.left){
					setHorizontalSpeed(0);
					x = x + 1;
				}
				if(parent.right){
					//setHorizontalSpeed(0);
					//x = x - 1;
				}
				
			}
		case 4:
			if(col.equals(Color.gray)){
				
				if(parent.up){
					setVerticalSpeed(0);
					y = y + 1;
				}
				if(parent.down){
					//setVerticalSpeed(0);
					//y = y - 1;
				}
				if(parent.left){
					//setHorizontalSpeed(0);
					//x = x + 1;
				}
				if(parent.right){
					setHorizontalSpeed(0);
					x = x - 1;
				}
				
			}

		}

		if(col.equals(Color.green)){ //gr�n = 0, 255, 0
			
		}
		
		if(col.equals(Color.red)){ //rot = 255, 0, 0
			//Tod durch Feuer!
			parent.setStarted(false); 
			
		}
		
		if(col.equals(Color.blue)){ //gelb = 255, 255, 0
			
			if (parent.getLevel() == 2){
				parent.doInitializations3();
			}
			
			if (parent.getLevel() == 1){
				
				parent.doInitializations2();
			}
			
		}
		
		if(col.equals(Color.yellow)){
			parent.wonGame();
		}
	}
	
	public boolean collidedWith(Sprite s) {
		return false;
	}
	@Override
	public void move(long delta){ //Wenn delta ungleich null, werden Positionen ver�ndert
		if(dx != 0){
			x = x + dx*(delta/1e9);
			
		}
		if(dy != 0){
			y = y + dy*(delta/1e9);
		}
	}
}

