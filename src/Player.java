import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Timer;

public class Player extends Sprite {
	private static final long serialVersionUID = 1L;
	int corner;
	private Point ol; //oben links
	private Point or; //oben rechts
	private Point ul; //unten links
	private Point ur; //unten rechts
	private double copy_dx;
	private double copy_dy;
	private int coins;
	private int lifes;
	private int mana;
	private boolean hasArmour; //trägt Rüstung?
	
	private int damage;			//Schaden, den der Spieler verursacht
	private int range;			//Reichweite seines Angriffes vertikal und horizontal
	private int diagRange;		//Diagonale Reichweite (range/Wurzel 2 (ca. 1.4))
	private int radialRange;	//Reichweite für den Rundumangriff (range + Hälfte der Spielerhöhe))
	private int health;
	private boolean attacking;
	private boolean canAttack;
	private boolean canLoseHealth;
	private Timer timer;
	private Timer timer2;
	
	public Player(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		ol = new Point();
		or = new Point();
		ul = new Point();
		ur = new Point();
		
		lifes = 3;
		coins = 0;
		mana = 0;
		health = 100;
		attacking = false;
		timer = new Timer();
		timer2 = new Timer();
		canAttack = true;
		damage = 50;
		range = 40;
		diagRange = (int)(range/1.4);
		radialRange = (int)(range + height);
		hasArmour = false;
	}
	
	@Override
	public void doLogic(long delta)	{
		super.doLogic(delta);
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
	private void checkColor(Color col){
		switch(corner){
		case 1:
			if(col.equals(Color.gray)){ //grau = 128, 128, 128
				while(parent.getMap().getColorForPoint(ol).equals(Color.gray)){
					
					if(copy_dy < 0){
						setVerticalSpeed(0);
						y = y + 1;
					}
					if(copy_dx < 0){
						setHorizontalSpeed(0);
						x = x + 1;
					}
					
					
					ol.setLocation((int)getX(), (int)getY());
					
				}
			
			}
			break;
		case 2:
			if(col.equals(Color.gray)){
				
				while(parent.getMap().getColorForPoint(ur).equals(Color.gray)){
					
					if(copy_dy > 0){
						setVerticalSpeed(0);
						y = y - 1;
					}
					
					if(copy_dx > 0){
						setHorizontalSpeed(0);
						x = x - 1;
					}
					ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
					
				}
			}
			break;
		case 3:
			if(col.equals(Color.gray)){
				while(parent.getMap().getColorForPoint(ul).equals(Color.gray)){
					
					if(copy_dy > 0){
						setVerticalSpeed(0);
						y = y - 1;
					}
					if(copy_dx < 0){
						setHorizontalSpeed(0);
						x = x + 1;
					}
					
					ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
				}
				
			}
			break;
		case 4:
			if(col.equals(Color.gray)){
				while(parent.getMap().getColorForPoint(or).equals(Color.gray)){
					if(copy_dy < 0){
						setVerticalSpeed(0);
						y = y + 1;
					}
					
					if(copy_dx > 0){
						setHorizontalSpeed(0);
						x = x - 1;
					}
					or.setLocation((int)(getX() + (width - 1)), (int)getY());
					
				}
			}
			break;
		}
	

		if(col.equals(Color.green)){ //grün = 0, 255, 0
			
		}
		
		if(col.equals(Color.red)){ //rot = 255, 0, 0
			//Tod durch Feuer!
			reduceHealth(20);	
		}
		
		if(col.equals(Color.blue)){
			
			if (parent.getLevel() == 2){
				
				parent.doInitializations3();
			}
			
			if (parent.getLevel() == 1){
				
				parent.doInitializations2();
			}
			
		}
		
		if(col.equals(Color.yellow)){  //gelb = 255, 255, 0
			parent.wonGame();
		}
	}
	
	public boolean collidedWith(Sprite s){
		if(this.intersects(s)){
			if(s instanceof Enemy){
				System.out.println("Ausgabe von Player: Oh nein, er hat mein Ohr abgebissen!");

				reduceHealth(10);
				return true;
			}
			if(s instanceof Item){ //1 = Coins, 2 = Mana, 3 = Shop, 4 = Rüstung, 5 = Waffe, 6 = NPC
				int type = s.getType();
				
				switch(type){
				
				case 1:
				
					System.out.println("Bravo, du hast eine Münze gesammelt");
					//Anzahl der Leben wird erhöht, eigentlich aber Kontostand
					coins++;
					s.remove = true;
				break;
				case 2: 
		
					System.out.println("Bravo, du hast einen Manatrank gesammelt");
					mana++;
					s.remove = true;
					
				break;
				case 3:
					
					if(parent.enterShop == true && parent.shopmode == false){
						parent.shopmode = true;
						parent.shop();
					}
					
				break;
				case 4:
					System.out.println("Bravo, du hast eine Rüstung gesammelt");
					hasArmour = true;
					s.remove = true;
				break;
				case 5:
					System.out.println("Bravo, du hast eine Waffe eingesammelt");
					damage = damage + 10;
					s.remove = true;
				break;
				case 6:
					if(parent.enterNPC == true){
						parent.talkwithnpc = true;
					}
					
				break;
				}	

			}
		}
		return false;
	}
	
	public Object getAttackObject(){	//Liefert ein Attack-Objekt (line oder rectangle), welches mit den actors aus dem Gamepanel kollidieren kann
		
		
		//Hier wird der Timer gesetzt:
		
		if(!canAttack){
			return null;
		}
		setAbleToAttack(false);
		
		timer.schedule(new Task(this), 2000);
		
		
		//Hier kann je nach Ausrüstung des Spielers eine andere Sache zurückgegeben werden
		if(dx > 0){
			if(dy > 0){
				return new Line2D.Double(ur.getX(), ur.getY(), ur.getX() + diagRange, ur.getY() + diagRange);
			}else if(dy < 0){
				return new Line2D.Double(or.getX(), or.getY(), or.getX() + diagRange, or.getY() - diagRange);
			}else{
				return new Line2D.Double(or.getX(), ((ur.getY() + or.getY())/2), or.getX() + range, ((ur.getY() + or.getY())/2));
			}
		}else if(dx < 0){
			if(dy > 0){
				return new Line2D.Double(ul.getX(), ul.getY(), ul.getX() - diagRange, ul.getY() + diagRange);
			}else if(dy < 0){
				return new Line2D.Double(ol.getX(), ol.getY(), ol.getX() - diagRange, ol.getY() - diagRange);
			}else{
				return new Line2D.Double(ol.getX(), ((ul.getY() + ol.getY())/2), ol.getX() - range, ((ul.getY() + ol.getY())/2));
			}
		}else{
			if(dy > 0){
				return new Line2D.Double(((ur.getX() + ul.getX())/2), ul.getY(), ((ur.getX() + ul.getX())/2), ul.getY() + range);
			}else if(dy < 0){
				return new Line2D.Double(((or.getX() + ol.getX())/2), ol.getY(), ((or.getX() + ol.getX())/2), ol.getY() - range);
			}else{
				return new Ellipse2D.Double((or.getX() + ol.getX())/2, (ur.getY() + or.getY())/2, radialRange, radialRange);
			}
		}
	}
public Effect getEffect(){	//Liefert ein Effect-Objekt (erbt von Sprite), welches im Gamepanel zu den actors hinzugefuegt wird um dort ein paar mal gezeichnet zu werden und dann zu verschwinden 
		
		
		
		//Hier kann je nach Ausrüstung des Spielers eine andere Sache zurückgegeben werden
		Effect effect;
		if(dx > 0){
			if(dy > 0){			//Rechts unten
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRot.png", 10, 1), 28, 28, 100, this.parent);	//Diese Aufrufe machen Probleme, er findet die Dateien nicht
				effect.setLoop(0, 4);
				effect.x = ur.getX();
				effect.y = ur.getY();
				return effect;
			}else if(dy < 0){	//Rechts oben
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRot.png", 10, 1), 28, 28, 100, this.parent);
				effect.setLoop(5, 9);
				effect.x = or.getX();
				effect.y = or.getY() - 28;
				return effect;
			}else{				//Nur Rechts
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalRot.png", 5, 1), 40, 5, 100, this.parent);
				effect.x = or.getX();
				effect.y = ((ur.getY() + or.getY())/2);
				return effect;
			}
		}else if(dx < 0){
			if(dy > 0){			//Links unten
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRot.png", 10, 1), 28, 28, 100, this.parent);
				effect.setLoop(5, 9);
				effect.x = ul.getX() - 28;
				effect.y = ul.getY();
				return effect;
			}else if(dy < 0){	//Links oben
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRot.png", 10, 1), 28, 28, 100, this.parent);
				effect.setLoop(0, 4);
				effect.x = ol.getX() - 28;
				effect.y = ol.getY() - 28;
				return effect;
			}else{				//Nur Links
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalRot.png", 5, 1), 40, 5, 100, this.parent);
				effect.x = ol.getX() - 40;
				effect.y = ((ul.getY() + ol.getY())/2);
				return effect;
			}
		}else{
			if(dy > 0){			//Nur Unten
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalRot.png", 5, 1), 5, 40, 100, this.parent);
				effect.x = (ur.getX() + ul.getX())/2;
				effect.y = ul.getY() + 20;
				return effect;
			}else if(dy < 0){	//Nur Oben
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalRot.png", 5, 1), 5, 40, 100, this.parent);
				effect.x = (or.getX() + ol.getX())/2;
				effect.y = ol.getY() - 40;
				return effect;
			}else{				//Stillstand
				effect = new Effect(parent.lib.getSprite("resources/pics/AttackeRadialRot.png", 5, 1), 60, 60, 100, this.parent);
				effect.x = ol.getX()- (40-width); //angepasst, damit Kreis direkt über Spieler!
				effect.y = ol.getY() + (25-height); 
				return effect;
			}
		}
	}
	
	public void setAttacking(){
		attacking = true;
	}
	public void resetAttacking(){
		attacking = false;
	}
	
	public boolean isAttacking(){
		return attacking;
	}
	
	public void setHealth(int h){
		health = h;
	}
	
	public int getHealth(){
		return health;
	}
	
	public void reduceHealth(int schaden){
		
		setAbleToLoseHealth(false);
		timer2.schedule(new Task(this), 10000);
		health = health - schaden;
		if (health <= 0){
			lostLife();
		}
	}
	public void setLifes(int l){
		this.lifes = l;
	}
	
	public int getLifes(){
		return lifes;
	}
	
	public int getCoins(){
		return coins;
	}
	
	public void setCoins(int c){
		coins = c;
	}
	
	public void setMana(int m){
		mana = m;
	}
	
	public int getMana(){
		return mana;
	}
	public void setAbleToLoseHealth(boolean b){
		canLoseHealth = b;
	}
	public void setAbleToAttack(boolean value){
		canAttack = value;
	}
	public int getDamage(){
		return damage;
	}
	public void setDamage(int pDamage){
		damage = pDamage;
	}
	public void setRange(int pRange){
		range = pRange;
	}
	public int getRange(){
		return range;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;

	}
	
	public void lostLife(){
		System.out.println("Du hast ein Leben verloren, streng dich dieses mal mehr an!");
		lifes--;
		x = parent.startposx;
		y = parent.startposy;
		//TODO: Auch Sprites insbesondere Gegner auf Startpos setzen!
		health = 100;
		if(lifes == 0){
			parent.lostGame();
		}
	}
}

