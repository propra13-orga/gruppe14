import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class Enemy extends Sprite {
	private static final long serialVersionUID = 1L;
	int corner;
	private Point ol; //oben links
	private Point or; //oben rechts
	private Point ul; //unten links
	private Point ur; //unten rechts
	private double copy_dx;
	private double copy_dy;
	private Color check;
	private int speed = 50;
	private double diffX;
	private double diffY;
	
	boolean locked = false; //noch kein Ziel erfasst
	
	public Enemy(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		ol = new Point();
		or = new Point();
		ul = new Point();
		ur = new Point();
	}
	
	@Override
	public void doLogic(long delta)	{
		super.doLogic(delta);
		diffX = parent.player.getX() - this.getX();
		diffY = parent.player.getY() - this.getY();
		if(Math.abs(diffX) < 100 && Math.abs(diffY)  < 100){ //Überprüfung ob der Spieler in der Nähe ist
			locked = true;
			if(diffX < 0){
				setHorizontalSpeed(-speed);
			}else{
				setHorizontalSpeed(speed);
			}
			if(diffY < 0){
				setVerticalSpeed(-speed);
			}else{
				setVerticalSpeed(speed);
			}
			
			
		}else{
			locked = false;
			setHorizontalSpeed(0);
			setVerticalSpeed(0);
		}
		
		
		
	}
	private void checkColor(Color col){
		switch(corner){
		case 1:
			if(col.equals(Color.gray) || col.equals(Color.red)){ //grau = 128, 128, 128
				check = parent.getMap().getColorForPoint(ol);
				while(check.equals(Color.gray) || check.equals(Color.red)){
					if(copy_dy < 0){
						setVerticalSpeed(0);
						y = y + 1;
					}
					
					if(copy_dx < 0){
						setHorizontalSpeed(0);
						x = x + 1;
					}
					
					ol.setLocation((int)getX(), (int)getY());
					check = parent.getMap().getColorForPoint(ol);
					
					
				}
			
			}
		case 2:
			if(col.equals(Color.gray)){
				check = parent.getMap().getColorForPoint(ur);
				while(check.equals(Color.gray) || check.equals(Color.red)){
					
					if(copy_dy > 0){
						setVerticalSpeed(0);
						y = y - 1;
					}
					
					if(copy_dx > 0){
						setHorizontalSpeed(0);
						x = x - 1;
					}
					ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
					check = parent.getMap().getColorForPoint(ur);
					
				}
			}
			
		case 3:
			if(col.equals(Color.gray)){
				check = parent.getMap().getColorForPoint(ul);
				while(check.equals(Color.gray) || check.equals(Color.red)){
					
					if(copy_dy > 0){
						setVerticalSpeed(0);
						y = y - 1;
					}
					if(copy_dx < 0){
						setHorizontalSpeed(0);
						x = x + 1;
					}
					
					ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
					check = parent.getMap().getColorForPoint(ul);
				}
				
			}
		case 4:
			if(col.equals(Color.gray)){
				check = parent.getMap().getColorForPoint(or);
				while(check.equals(Color.gray) || check.equals(Color.red)){
					if(copy_dy < 0){
						setVerticalSpeed(0);
						y = y + 1;
					}
					
					if(copy_dx > 0){
						setHorizontalSpeed(0);
						x = x - 1;
					}
					or.setLocation((int)(getX() + (width - 1)), (int)getY());
					check = parent.getMap().getColorForPoint(or);
					
				}
			}

		}

	}
	@Override
	public void move(long delta){ //Wenn delta ungleich null, werden Positionen verändert
		//dx = Verschiebung inkl. Speed
		
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
		
		//Überprüfung der Farben jetzt in der move-Methode
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

