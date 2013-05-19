import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Player extends Sprite {
	private static final long serialVersionUID = 1L;
	int corner;
	private Point ol;
	private Point or;
	private Point ul;
	private Point ur;
	private double copy_dx;
	private double copy_dy;
	
	
	public Player(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		ol = new Point();
		or = new Point();
		ul = new Point();
		ur = new Point();
	}
	
	@Override
	public void doLogic(long delta)	{
		super.doLogic(delta);
		//Überprüfen der Ränder, damit Player nicht darüber hinaus kann
		
		/*
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
		//Denkbar: Weitere Punkte überprüfen, um höhere Genauigkeit zu erreichen, hängt von Ausarbeitung der ShadowMap ab
		corner = 1;
		ol.setLocation((int)getX(), (int)getY());
		Color col1 = parent.getMap().getColorForPoint(ol); //Ecke oben links
		checkColor(col1);
		
		corner = 2;
		ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
		Color col2 = parent.getMap().getColorForPoint(ur); //Ecke unten rechts
		checkColor(col2);
		
		corner = 3;
		ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
		Color col3 = parent.getMap().getColorForPoint(ul); //Ecke unten links
		checkColor(col3);
		
		
		corner = 4;
		or.setLocation((int)(getX() + (width - 1)), (int)getY());
		Color col4 = parent.getMap().getColorForPoint(or); //Ecke oben rechts
		checkColor(col4);
		 */
		
	}
	@Override
	public void move(long delta){ //Wenn delta ungleich null, werden Positionen verändert
		copy_dx = dx;
		copy_dy = dy;
		if(dx != 0){
			x = x + dx*(delta/1e9);
		}
		
		if(dy != 0){
			y = y + dy*(delta/1e9);
		}
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
		
		
		corner = 1;
		ol.setLocation((int)getX(), (int)getY());
		Color col1 = parent.getMap().getColorForPoint(ol); //Ecke oben links
		checkColor(col1);
		
		corner = 2;
		ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
		Color col2 = parent.getMap().getColorForPoint(ur); //Ecke unten rechts
		checkColor(col2);
		
		corner = 3;
		ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
		Color col3 = parent.getMap().getColorForPoint(ul); //Ecke unten links
		checkColor(col3);
		
		
		corner = 4;
		or.setLocation((int)(getX() + (width - 1)), (int)getY());
		Color col4 = parent.getMap().getColorForPoint(or); //Ecke oben rechts
		checkColor(col4);
	}
	private void checkColor(Color col){
		switch(corner){
		case 1:
			if(col.equals(Color.gray)){ //grau = 128, 128, 128
				while(parent.getMap().getColorForPoint(ol).equals(Color.gray)){
					if(copy_dy < 0){
						setVerticalSpeed(0);
						y = y + 1;
					}
					if(copy_dy > 0){
						//setVerticalSpeed(0);
						//y = y - 1;
					}
					if(copy_dx < 0){
						setHorizontalSpeed(0);
						x = x + 1;
					}
					if(copy_dx > 0){
						//setHorizontalSpeed(0);
						//x = x - 1;
					}
					ol.setLocation((int)getX(), (int)getY());
					
				}
			
			}
		case 2:
			if(col.equals(Color.gray)){
				
				while(parent.getMap().getColorForPoint(ur).equals(Color.gray)){
					if(copy_dy < 0){
						//setVerticalSpeed(0);
						//y = y + 1;
					}
					if(copy_dy > 0){
						setVerticalSpeed(0);
						y = y - 1;
					}
					if(copy_dx < 0){
						//setHorizontalSpeed(0);
						//x = x + 1;
					}
					if(copy_dx > 0){
						setHorizontalSpeed(0);
						x = x - 1;
					}
					ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
					
				}
			}
			
		case 3:
			if(col.equals(Color.gray)){
				while(parent.getMap().getColorForPoint(ul).equals(Color.gray)){
					if(copy_dy < 0){
						//setVerticalSpeed(0);
						//y = y + 1;
					}
					if(copy_dy > 0){
						setVerticalSpeed(0);
						y = y - 1;
					}
					if(copy_dx < 0){
						setHorizontalSpeed(0);
						x = x + 1;
					}
					if(copy_dx > 0){
						//setHorizontalSpeed(0);
						//x = x - 1;
					}
					ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
				}
				
			}
		case 4:
			if(col.equals(Color.gray)){
				while(parent.getMap().getColorForPoint(or).equals(Color.gray)){
					if(copy_dy < 0){
						setVerticalSpeed(0);
						y = y + 1;
					}
					if(copy_dy > 0){
						//setVerticalSpeed(0);
						//y = y - 1;
					}
					if(copy_dx < 0){
						//setHorizontalSpeed(0);
						//x = x + 1;
					}
					if(copy_dx > 0){
						setHorizontalSpeed(0);
						x = x - 1;
					}
					or.setLocation((int)(getX() + (width - 1)), (int)getY());
					
				}
			}

		}

		if(col.equals(Color.green)){ //grün = 0, 255, 0
			
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
	
}

