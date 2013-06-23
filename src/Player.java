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
	private int xp;
	private int lifes;
	private int mana;
	private int oldcoins;
	private int oldxp;
	private int oldmana;
	private int oldHasArmour;
	private int oldHasWeapon;
	private int hasArmour; //tr�gt R�stung? -> 0 = Ne, nix, 1 = Eisr�stung, 2 = Feuerr�stung
	private int hasWeapon;	//tr�gt Waffe? -> 0 = Ne, nix, 1 = Eiswaffe, 2 = Feuerwaffe
	private int collectedCoins; //Innerhalb von einem Quest gesammelte Coins
	
	private boolean skillhealth1;	//Variablen f�r die Skills (Sind die Skills schon erlernt?)
	private boolean skillhealth2;
	private boolean skillstrength1;
	private boolean skillstrength2;
	
	private boolean oldskillhealth1;
	private boolean oldskillhealth2;
	private boolean oldskillstrength1;
	private boolean oldskillstrength2;
	
	private int maxhealth;			//Maximale Lebensenergie
	private int oldmaxhealth;
	
	private int damage;			//Schaden, den der Spieler verursacht
	private int range;			//Reichweite seines Angriffes vertikal und horizontal
	private int diagRange;		//Diagonale Reichweite (range/Wurzel 2 (ca. 1.4))
	private int radialRange;	//Reichweite f�r den Rundumangriff (range + H�lfte der Spielerh�he))
	private int health;
	private boolean attacking;	//Angriff
	private boolean canSummon;
	private boolean canAttack;
	private boolean canLoseHealth;
	private Timer attackTimer;
	private Timer magicTimer;
	private Timer healthTimer;
	private Quest quest;
	
	
	
	
	
	public Player(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		loop_to = (pics.length/ 3)-1;
		ol = new Point();
		or = new Point();
		ul = new Point();
		ur = new Point();
		
		skillhealth1 = false;
		skillhealth2 = false;
		skillstrength1 = false;
		skillstrength2 = false;

		coins = 0;
		lifes = 3;
		xp = 0;
		mana = 0;
		health = 100;
		maxhealth = 100;
		oldmaxhealth = 100;
		attacking = false;
		attackTimer = new Timer();
		magicTimer = new Timer();
		healthTimer = new Timer();
		canAttack = true;
		canSummon = true;
		damage = 50;
		range = 40;
		diagRange = (int)(range/1.4);
		radialRange = (int)(range + height);
		hasArmour = 0;
		hasWeapon = 0;
		canLoseHealth = true;
	}
	
	@Override
	public void doLogic(long delta)	{
		super.doLogic(delta);
		
	}
/*#################################################Kollision und Bewegung###############################################################################*/	
/*######################################################################################################################################################*/	
	@Override
	public void move(long delta){ //Wenn delta ungleich null, werden Positionen ver�ndert
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
		
		//�berpr�fung der Farben jetzt in der move-Methode
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
	

		if(col.equals(Color.green)){ //gr�n = 0, 255, 0
			
		}
		
		if(col.equals(Color.red)){ //rot = 255, 0, 0
			if(canLoseHealth){
				if(hasArmour > 0){
					reduceHealth(10);
				}else{
					reduceHealth(20);
				
				}
			}
		}
		
		if(col.equals(Color.blue)){
			System.out.println("T�r!");
			if(parent.getRoom()+1 == 4){ //Levelwechsel
				parent.doInitializations(parent.getLevel()+1, 1);
			}else{
				parent.doInitializations(parent.getLevel(), parent.getRoom()+1);
			}
			
			
		}
		
		if(col.equals(Color.yellow)){  //gelb = 255, 255, 0
			parent.wonGame();
		}
	}
	
	public boolean collidedWith(Sprite s){
		if(this.intersects(s)){
			int type = s.getType();
			if(s instanceof Enemy){
				if(canLoseHealth){
					if(s instanceof IceEnemy){
						if(hasArmour() == 1){
							reduceHealth(((Enemy)s).getDamage() / 3);
						}else{
							reduceHealth(((Enemy)s).getDamage());
						}
					}else if (s instanceof FireEnemy){
						if(hasArmour() == 2){
							reduceHealth(((Enemy)s).getDamage() / 3);
						}else{
							reduceHealth(((Enemy)s).getDamage());
						}
					}else{
						reduceHealth(((Enemy)s).getDamage());
					}
				}
				return true;
			}
			
			if(s instanceof Item){ //1 = Coins, 2 = Mana, 3 = Shop, 4 = R�stung, 5 = Waffe, 6 = NPC
				
				switch(type){
				
				case Item.COIN:
				
					System.out.println("Bravo, du hast eine M�nze gesammelt");
					//Anzahl der Leben wird erh�ht, eigentlich aber Kontostand
					coins++;
					if(parent.inquest == true){
						collectedCoins++;
					}
					s.remove = true;
				break;
				case Item.MANA: 
		
					System.out.println("Bravo, du hast einen Manatrank gesammelt");
					mana++;
					s.remove = true;
					
				break;
				case Item.SHOP:
					
					if(parent.enterShop == true && parent.shopmode == false){
						parent.shopmode = true;
						parent.shop();
					}
					
				break;
				case Item.R�STUNGEIS:
					System.out.println("Bravo, du hast eine Gefrierschutz-R�stung gesammelt");
					hasArmour = 1;
					loop_from = (pics.length/3);
					loop_to = (pics.length/3)*2 - 1;
					s.remove = true;
				break;
				case Item.R�STUNGFEUER:
					System.out.println("Bravo, du hast eine Feuerschutz-R�stung gesammelt");
					hasArmour = 2;
					loop_from = (pics.length/3)*2;
					loop_to = pics.length - 1;
					s.remove = true;
				break;
				case Item.EISWAFFE:
					System.out.println("Bravo, du hast eine Eis-Waffe eingesammelt");
					damage = damage + 20;
					range = range + 10;
					hasWeapon = 1;
					s.remove = true;
				break;
				case Item.FEUERWAFFE:
					System.out.println("Bravo, du hast eine Feuer-Waffe eingesammelt");
					damage = damage + 20;
					range = range + 10;
					hasWeapon = 2;
					s.remove = true;
				break;
				case Item.NPC:
					if(parent.enterNPC == true){
						parent.talkwithnpc = true;
					}
					
				break;
				case Item.HEALTHPACK:
					if(health < maxhealth){
						health = health + 50;
					}
					if (health > maxhealth){
						health = maxhealth;
					}
					s.remove = true;
				break;
				}
			}
			if(s instanceof Quest){//Welche Types? Beispielsweise 1 = Matheaufgabe, 2 = M�nzen sammeln, 3 = Gegner t�ten, 4 = Was wei� ich!
				quest = (Quest) s;
				switch(type){
				
				case 1:
				//TODO: Mathequest
				break;
				case 2: 
					
					parent.inquest = true;
					
					if (collectedCoins >= quest.getQuestCoins()){
						System.out.println("Bravo - Du hast meine Aufgabe erf�llt, hier deine Pr�mie: TODO");
						xp = xp + 6;
						parent.inquest = false;
						collectedCoins = 0;
						s.remove = true;
						//TODO: Belohnung bzw. Pr�mie!
					}
				//TODO: Random Anzahl von M�nzen (> x, < y?) bestimmen und dann muss Spieler diese einsammeln
				break;
				case 3:
				//TODO: Random Anzahl von Enemy (> x, < y?) bestimmen und dann muss Spieler diese t�ten										
				break;
				case 4:
				//TODO: Hier ist Kreativit�t gefordert!
				break;
				
				}
				
			}
		}
		return false;
	}
/*#################################################Angriffsobjekte##############################################################################################*/	
/*######################################################################################################################################################*/	
	
	public Object getAttackObject(){	//Liefert ein Attack-Objekt (line oder rectangle), welches mit den actors aus dem Gamepanel kollidieren kann
		or.setLocation((int)(getX() + (width - 1)), (int)getY());
		ol.setLocation((int)getX(), (int)getY());
		ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
		ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
		
		//Hier wird der Timer gesetzt:
		
		if(!canAttack){
			return null;
		}
		setAbleToAttack(false);
		
		attackTimer.schedule(new AttackTask(this), 2000);
		
		
		//Hier kann je nach Ausr�stung des Spielers eine andere Sache zur�ckgegeben werden
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
	public Object getMagicObject(){	//Liefert ein Magic-Objekt (line oder rectangle), welches mit den actors aus dem Gamepanel kollidieren kann
		
		or.setLocation((int)(getX() + (width - 1)), (int)getY());
		ol.setLocation((int)getX(), (int)getY());
		ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
		ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
		//Hier wird der Timer gesetzt:
		
		if(!canSummon || mana <= 0){
			return null;
		}
		setAbleToSummon(false);
		
		magicTimer.schedule(new MagicTask(this), 1000);
		
		mana--;
		
		
		//Hier kann je nach Ausr�stung des Spielers eine andere Sache zur�ckgegeben werden
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
	
/*#################################################Effekte##############################################################################################*/	
/*######################################################################################################################################################*/		
	
public Effect getAttackEffect(){	//Liefert ein Effect-Objekt (erbt von Sprite), welches im Gamepanel zu den actors hinzugefuegt wird um dort ein paar mal gezeichnet zu werden und dann zu verschwinden 
		
	or.setLocation((int)(getX() + (width - 1)), (int)getY());
	ol.setLocation((int)getX(), (int)getY());
	ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
	ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
		
		//Hier kann je nach Ausr�stung des Spielers eine andere Sache zur�ckgegeben werden
		Effect effect;
		if(dx > 0){
			if(dy > 0){			//Rechts unten
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRotExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalBlauExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRot.png", 10, 1), 28, 28, 100, this.parent);
				}
				effect.setLoop(0, 4);
				effect.x = ur.getX();
				effect.y = ur.getY();
				return effect;
			}else if(dy < 0){	//Rechts oben
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRotExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalBlauExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRotExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}
				effect.setLoop(5, 9);
				effect.x = or.getX();
				effect.y = or.getY() - 28;
				return effect;
			}else{				//Nur Rechts
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalRotExtrem.png", 5, 1), 40, 5, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalBlauExtrem.png", 5, 1), 40, 5, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalRot.png", 5, 1), 40, 5, 100, this.parent);
				}
				effect.x = or.getX();
				effect.y = ((ur.getY() + or.getY())/2);
				return effect;
			}
		}else if(dx < 0){
			if(dy > 0){			//Links unten
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRotExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalBlauExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRot.png", 10, 1), 28, 28, 100, this.parent);
				}
				effect.setLoop(5, 9);
				effect.x = ul.getX() - 28;
				effect.y = ul.getY();
				return effect;
			}else if(dy < 0){	//Links oben
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRotExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalBlauExtrem.png", 10, 1), 28, 28, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRot.png", 10, 1), 28, 28, 100, this.parent);
				}
				effect.setLoop(0, 4);
				effect.x = ol.getX() - 28;
				effect.y = ol.getY() - 28;
				return effect;
			}else{				//Nur Links
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalRotExtrem.png", 5, 1), 40, 5, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalBlauExtrem.png", 5, 1), 40, 5, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalRot.png", 5, 1), 40, 5, 100, this.parent);
				}
				effect.x = ol.getX() - 40;
				effect.y = ((ul.getY() + ol.getY())/2);
				return effect;
			}
		}else{
			if(dy > 0){			//Nur Unten
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalRotExtrem.png", 5, 1), 5, 40, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalBlauExtrem.png", 5, 1), 5, 40, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalRot.png", 5, 1), 5, 40, 100, this.parent);
				}
				effect.x = (ur.getX() + ul.getX())/2;
				effect.y = ul.getY() + 20;
				return effect;
			}else if(dy < 0){	//Nur Oben
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalRotExtrem.png", 5, 1), 5, 40, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalBlauExtrem.png", 5, 1), 5, 40, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalRot.png", 5, 1), 5, 40, 100, this.parent);
				}
				effect.x = (or.getX() + ol.getX())/2;
				effect.y = ol.getY() - 40;
				return effect;
			}else{				//Stillstand
				if(hasWeapon == 2){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeRadialRotExtrem.png", 5, 1), 60, 60, 100, this.parent);
				}else if(hasWeapon == 1){
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeRadialBlauExtrem.png", 5, 1), 60, 60, 100, this.parent);
				}else{
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeRadialRot.png", 5, 1), 60, 60, 100, this.parent);
				}
				effect.x = ol.getX()- (40-width); //angepasst, damit Kreis direkt �ber Spieler!
				effect.y = ol.getY() + (25-height); 
				return effect;
			}
		}
	}
public Effect getMagicEffect(){	//Liefert ein Effect-Objekt (erbt von Sprite), welches im Gamepanel zu den actors hinzugefuegt wird um dort ein paar mal gezeichnet zu werden und dann zu verschwinden 
	
	or.setLocation((int)(getX() + (width - 1)), (int)getY());
	ol.setLocation((int)getX(), (int)getY());
	ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
	ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
	
	//Hier kann je nach Ausr�stung des Spielers eine andere Sache zur�ckgegeben werden
	Effect effect;
	if(dx > 0){
		if(dy > 0){			//Rechts unten
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalBlau.png", 10, 1), 28, 28, 100, this.parent);	//Diese Aufrufe machen Probleme, er findet die Dateien nicht
			effect.setLoop(0, 4);
			effect.x = ur.getX();
			effect.y = ur.getY();
			return effect;
		}else if(dy < 0){	//Rechts oben
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalBlau.png", 10, 1), 28, 28, 100, this.parent);
			effect.setLoop(5, 9);
			effect.x = or.getX();
			effect.y = or.getY() - 28;
			return effect;
		}else{				//Nur Rechts
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalBlau.png", 5, 1), 40, 5, 100, this.parent);
			effect.x = or.getX();
			effect.y = ((ur.getY() + or.getY())/2);
			return effect;
		}
	}else if(dx < 0){
		if(dy > 0){			//Links unten
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalBlau.png", 10, 1), 28, 28, 100, this.parent);
			effect.setLoop(5, 9);
			effect.x = ul.getX() - 28;
			effect.y = ul.getY();
			return effect;
		}else if(dy < 0){	//Links oben
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalBlau.png", 10, 1), 28, 28, 100, this.parent);
			effect.setLoop(0, 4);
			effect.x = ol.getX() - 28;
			effect.y = ol.getY() - 28;
			return effect;
		}else{				//Nur Links
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeHorizontalBlau.png", 5, 1), 40, 5, 100, this.parent);
			effect.x = ol.getX() - 40;
			effect.y = ((ul.getY() + ol.getY())/2);
			return effect;
		}
	}else{
		if(dy > 0){			//Nur Unten
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalBlau.png", 5, 1), 5, 40, 100, this.parent);
			effect.x = (ur.getX() + ul.getX())/2;
			effect.y = ul.getY() + 20;
			return effect;
		}else if(dy < 0){	//Nur Oben
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeVertikalBlau.png", 5, 1), 5, 40, 100, this.parent);
			effect.x = (or.getX() + ol.getX())/2;
			effect.y = ol.getY() - 40;
			return effect;
		}else{				//Stillstand
			effect = new Effect(parent.lib.getSprite("resources/pics/AttackeRadialBlau.png", 5, 1), 60, 60, 100, this.parent);
			effect.x = ol.getX()- (40-width); //angepasst, damit Kreis direkt �ber Spieler!
			effect.y = ol.getY() + (25-height); 
			return effect;
		}
		
	}
}
/*#################################################Get- und Set- Methoden################################################################################*/	
/*######################################################################################################################################################*/		
	public void setAttacking(){
		attacking = true;
	}
	public void resetAttacking(){
		attacking = false;
	}
		
	public boolean isAttacking(){
		return attacking;
	}
	public int hasArmour(){
		return hasArmour;
	}
	public int hasWeapon(){
		return hasWeapon;
	}
	public void setHealth(int h){
		health = h;
	}
	
	public int getHealth(){
		return health;
	}
	
	public void reduceHealth(int schaden){
		
		setAbleToLoseHealth(false);
		healthTimer.schedule(new HealthTask(this), 1000); //Spieler kann erst nach gewisser Zeit wieder verwundet werden
		
		if(hasArmour > 0){
			health = health - (schaden/2); //R�stung halbiert Schaden
			
		}else{
			health = health - schaden;
			
		}
		if (health <= 0){
			lostLife(); 
			System.out.println("Leben verloren!");
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
	public int getXP(){
		return xp;
	}
	public void setXP(int x){
		xp = x;
	}
	
	public void setOldCoins(int c){
		oldcoins = c;
	}
	public void setOldXP(int x){
		oldxp = x;
	}
	public int getOldXP(){
		return oldxp;
	}
	
	public void setMana(int m){
		mana = m;
	}
	
	public void setOldMana(int m){
		oldmana = m;
	}
	
	public boolean hasSkillHealth1(){
		return skillhealth1;
	}
	
	public boolean hasSkillHealth2(){
		return skillhealth2;
	}
	
	public boolean hasSkillStrength1(){
		return skillstrength1;
	}
	
	public boolean hasSkillStrength2(){
		return skillstrength2;
	}
	
	public void setSkillHealth1(boolean b){
		skillhealth1 = b;
	}
	public void setSkillHealth2(boolean b){
		skillhealth2 = b;
	}
	public void setSkillStrength1(boolean b){
		skillstrength1 = b;
	}
	public void setSkillStrength2(boolean b){
		skillstrength2 = b;
	}
	
	public boolean hasOldSkillHealth1(){
		return oldskillhealth1;
	}
	
	public boolean hasOldSkillHealth2(){
		return oldskillhealth2;
	}
	
	public boolean hasOldSkillStrength1(){
		return oldskillstrength1;
	}
	
	public boolean hasOldSkillStrength2(){
		return oldskillstrength2;
	}
	
	public void setOldSkillHealth1(boolean b){
		oldskillhealth1 = b;
	}
	public void setOldSkillHealth2(boolean b){
		oldskillhealth2 = b;
	}
	public void setOldSkillStrength1(boolean b){
		oldskillstrength1 = b;
	}
	public void setOldSkillStrength2(boolean b){
		oldskillstrength2 = b;
	}
	public int getMaxhealth(){
		return maxhealth;
	}
	public void setMaxhealth(int m){
		maxhealth = m;
	}
	
	public int getOldMaxhealth(){
		return oldmaxhealth;
	}
	
	public void setOldMaxhealth(int m){
		oldmaxhealth = m;
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
	public void setAbleToSummon(boolean value){
		canSummon = value;
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
	
	public void setOldWeapon(int b){
		oldHasWeapon = b;
	}
	
	public void setOldArmour(int b){
		oldHasArmour = b;
	}

	@Override
	public int getType() {
		return 0;
	}
	
	public Quest getQuest(){
		return quest;
	}
	
	public int getCollectedCoins(){
		return collectedCoins;
	}
	public void lostLife(){
		System.out.println("Du hast ein Leben verloren, streng dich naechstes mal mehr an!");
		lifes--;

		if(lifes == 0){
			parent.lostGame();
		}
		parent.doInitializations(parent.level, 1); //Laden vom 1. Raum im jeweiligen Level
		//Bei Lebensverlust Werte auf alte Werte vom Checkpoint setzen
		coins = oldcoins;
		mana = oldmana;
		hasArmour = oldHasArmour;
		hasWeapon = oldHasWeapon;
		
		x = parent.checkpointx;	//Spieler im 1. Raum an die Startposition setzen
		y = parent.checkpointy;
		
		health = 100;
		hasArmour = 0;
		hasWeapon = 0;
				
	}
}

