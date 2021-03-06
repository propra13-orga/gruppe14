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
	
	private boolean remote;
	
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
	
	private int i;
	
	
	
	
	/**
	 * Konstruktor fuer den Spieler
	 * 
	 * @param i 	das BufferedImage-Array zum Einlesen der Grafik
	 * @param x 	die x-Koordinate des Spielers
	 * @param y 	die y-Koordinate des Spielers
	 * @param p 	das zugehoerige Gamepanel
	 * **/
	public Player(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		remote = false;
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
	/**
	 * move Methode zum Bewegen des Spielers. Bewegt den Spieler abh�ngig von den Variablen dx und dy. Verwendet eine Kollisionserkennung fuer die statische Umgebung.
	 * @param delta		long Variable, die den zeitlichen Abstand seit dem letzen Schleifendurchlauf darstellt, wird verwendet um fluessige Bewegung zu garantieren.
	 * **/
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
		if(!remote){
			checkColor(col1);
		}else{
			checkColorRemote(col1);
		}
		
		corner = 2;
		ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
		Color col2 = parent.getMap().getColorForPoint(ur); //Ecke unten rechts
		if(!remote){
			checkColor(col2);
		}else{
			checkColorRemote(col2);
		}
		
		corner = 3;
		ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
		Color col3 = parent.getMap().getColorForPoint(ul); //Ecke unten links
		if(!remote){
			checkColor(col3);
		}else{
			checkColorRemote(col3);
		}
		
		corner = 4;
		or.setLocation((int)(getX() + (width - 1)), (int)getY());
		Color col4 = parent.getMap().getColorForPoint(or); //Ecke oben rechts
		if(!remote){
			checkColor(col4);
		}else{
			checkColorRemote(col4);
		}
	}
	/**
	 * �berpr�ft die Farbe, der aktuellen Position des Spielers auf der Shadowmap
	 * Gray = Wand, Red = Fl�che die Schaden macht, Blue = eine T�r, Yellow = Pokal
	 * @param col die Farbe auf der Shadowmap
	 */

	private void checkColor(Color col){
//		i=0;
		switch(corner){
		case 1:
			if(col.equals(Color.gray)){ //grau = 128, 128, 128
				while(parent.getMap().getColorForPoint(ol).equals(Color.gray) && (copy_dy < 0 || copy_dy < 0)){
//					System.out.println("Move-Problem ol: Copy dx = "+copy_dx+" Copy dy = " +copy_dy);
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
				
				while(parent.getMap().getColorForPoint(ur).equals(Color.gray) && (copy_dy > 0 || copy_dx > 0)){
//					System.out.println("Move-Problem ur: Copy dx = "+copy_dx+" Copy dy = " +copy_dy);
//					i++;
//					if(i>20){
//						System.out.println("-");
//					}
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
				while(parent.getMap().getColorForPoint(ul).equals(Color.gray) && (copy_dy > 0 || copy_dx < 0)){
//					System.out.println("Move-Problem ul: Copy dx = "+copy_dx+" Copy dy = " +copy_dy);
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
				while(parent.getMap().getColorForPoint(or).equals(Color.gray) && (copy_dy < 0 || copy_dx > 0)){
//					System.out.println("Move-Problem or: Copy dx = "+copy_dx+" Copy dy = " +copy_dy);
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
		/**
		 * �berpr�ft die Farbe, der aktuellen Position des Spielers auf der Shadowmap
		 * Gray = Wand, Red = Fl�che die Schaden macht, Blue = eine T�r, Yellow = Pokal
		 * @param col die Farbe auf der Shadowmap
		 */

			private void checkColorRemote(Color col){
				i = 0;
				switch(corner){
				case 1:
					if(col.equals(Color.gray)){ //grau = 128, 128, 128
						while(parent.getMap().getColorForPoint(ol).equals(Color.gray) && i<=10){
							System.out.println("Move-Problem "+ i);
							if(copy_dy < 0){
								setVerticalSpeed(0);
								y = y + 1;
							}
							if(copy_dx < 0){
								setHorizontalSpeed(0);
								x = x + 1;
							}
							
							
							ol.setLocation((int)getX(), (int)getY());
							i++;
						}
					
					}
					break;
				case 2:
					if(col.equals(Color.gray)){
						
						while(parent.getMap().getColorForPoint(ur).equals(Color.gray) && i<= 10){
							System.out.println("Move-Problem "+ i);
							if(copy_dy > 0){
								setVerticalSpeed(0);
								y = y - 1;
							}
							
							if(copy_dx > 0){
								setHorizontalSpeed(0);
								x = x - 1;
							}
							ur.setLocation((int)(getX() + (width - 1)), (int)(getY() + (height - 1)));
							i++;
						}
					}
					break;
				case 3:
					if(col.equals(Color.gray)){
						while(parent.getMap().getColorForPoint(ul).equals(Color.gray) && i<=10){
							System.out.println("Move-Problem "+ i);
							if(copy_dy > 0){
								setVerticalSpeed(0);
								y = y - 1;
							}
							if(copy_dx < 0){
								setHorizontalSpeed(0);
								x = x + 1;
							}
							
							ul.setLocation((int)(getX()), (int)(getY() + (height - 1)));
							i++;
						}
						
					}
					break;
				case 4:
					if(col.equals(Color.gray)){
						while(parent.getMap().getColorForPoint(or).equals(Color.gray) && i<=10){
							System.out.println("Move-Problem "+ i);
							if(copy_dy < 0){
								setVerticalSpeed(0);
								y = y + 1;
							}
							
							if(copy_dx > 0){
								setHorizontalSpeed(0);
								x = x - 1;
							}
							or.setLocation((int)(getX() + (width - 1)), (int)getY());
							i++;
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
	/**
	 * Methode, die prueft, ob der Spieler mit dem uebergebenen Sprite kollidiert ist.
	 * @param s		Sprite s, mit dem die Kollision geprueft wird.
	 * @return		Gibt true zurueck, falls der Spieler kollidiert ist, andernfalls false.
	 * 
	 * **/
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
				case Item.RUESTUNGEIS:
					System.out.println("Bravo, du hast eine Gefrierschutz-R�stung gesammelt");
					hasArmour = 1;
					loop_from = (pics.length/3);
					loop_to = (pics.length/3)*2 - 1;
					s.remove = true;
				break;
				case Item.RUESTUNGFEUER:
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
				case Item.XPDINGS:
					xp++;
					s.remove = true;
				break;
				}
			}
			if(s instanceof Quest){//Welche Types? Beispielsweise 1 = Matheaufgabe, 2 = M�nzen sammeln, 3 = Gegner t�ten, 4 = Was wei� ich!
				quest = (Quest) s;
				switch(type){
				
				case 1:
				parent.inquest = true;
				
				if(collectedCoins > 0){
					if(collectedCoins == quest.getQuestCoins()){
						System.out.println("Bravo - Du hast meine Aufgabe erf�llt, hier deine Pr�mie: TODO");
						xp = xp + 6;
						parent.inquest = false;
						collectedCoins = 0;
						s.remove = true;
					} else {
						System.out.println("Du hast leider die falsche Anzahl an M�nzen mitgebracht, du verlierst 6 Erfahrungspunkte");
						xp = xp - 6;
						if (xp < 0){
							xp = 0;
						}
						parent.inquest = false;
						collectedCoins = 0;
						s.remove = true;
					}
				}
				
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
			return true;
		}
		return false;
	}
/*#################################################Angriffsobjekte##############################################################################################*/	
/*######################################################################################################################################################*/	
	
	/**
	 * Gibt ein Angriffsobjekt zurueck, sofern der Spieler attackieren kann
	 * @return		das Angriffsobjekt (Linie oder Kreis) wird zurueckgeliefert
	 * **/
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
	/**
	 * Gibt ein Magieobjekt zurueck, sofern der Spieler zaubern kann
	 * @return		das Magieobjekt (Linie oder Kreis) wird zurueckgeliefert
	 * **/
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
	/**
	 * Gibt einen Effekt fuer die Attacke zurueck
	 * @return		der Angriffseffekt wird zurueckgeliefert
	 * **/	
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
					effect = new Effect(parent.lib.getSprite("resources/pics/AttackeDiagonalRot.png", 10, 1), 28, 28, 100, this.parent);
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
/**
 * Gibt einen Effekt fuer die Attacke zurueck
 * @return		der Angriffseffekt wird zurueckgeliefert
 * **/	
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
	
	/**
	 * Setzt die aktuelle Spielerposition
	 * @param x  x-Position
	 * @param y  y-Position
	 * **/
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	/**
	 * Setzt den Spieler in den Status "will attackieren"
	 * **/
	public void setAttacking(){
		attacking = true;
	}
	/**
	 * Setzt den Status "will attackieren" zurueck
	 * **/
	public void resetAttacking(){
		attacking = false;
	}
	/**
	 * Gibt zurueck, ober der Spieler attackieren will
	 * 
	 *@return	boolean, will der Spieler attackieren?
	 * **/	
	public boolean isAttacking(){
		return attacking;
	}
	/**
	 * Gibt zurueck, ob der Spieler eine Ruestung traegt
	 * 
	 * @return		int, 0 = keine Ruestung, 1 = Eisruestung, 2 = Feuerruestung
	 * **/
	public int hasArmour(){
		return hasArmour;
	}
	/**
	 * Gibt zurueck, ob der Spieler eine Waffe traegt
	 * 
	 * @return		int, 0 = keine Waffe, 1 = Eisschwert, 2 = Feuerschwert
	 * **/
	public int hasWeapon(){
		return hasWeapon;
	}
	/**
	 * Setzt die Lebensenergie des Spielers auf den Wert von h
	 * @param h		int-Wert fuer die Lebensenergie
	 * **/
	public void setHealth(int h){
		health = h;
	}
	/**
	 * Gibt zurueck, wie viel Lebensenergie der Spieler noch hat
	 * 
	 * @return		integer-Wert der Lebensenergie des Spielers
	 * **/
	public int getHealth(){
		return health;
	}
	
	/**
	 * Reduziert die Lebensenergie des Spielers um den gegebenen Schaden. Falls der Spieler eine Ruestung traegt, wird der Schaden abhaengig vom Ruestungstyp und Gegnertyp, der den Schaden verursacht veraendert.
	 * @param schaden		Schaden, um den die Lebensenergie unter Beruecksichtigung von Ruestung und Waffe reduziert wird.
	 * 
	 * @return		int, 0 = keine Ruestung, 1 = Eisruestung, 2 = Feuerruestung
	 * **/
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
	/**
	 * Setzt die Anzahl der Leben
	 * 
	 * @param l		Anzahl der Leben
	 * **/
	public void setLifes(int l){
		this.lifes = l;
	}
	/**
	 * Gibt zurueck, wie viele Leben der Spieler hat
	 * 
	 * @return		int, Anzahl der Leben
	 * **/
	public int getLifes(){
		return lifes;
	}
	/**
	 * Gibt zurueck, wie viele Muenzen der Spieler hat
	 * 
	 * @return		int, Anzahl der Muenzen
	 * **/
	public int getCoins(){
		return coins;
	}
	/**
	 * Setzt die Anzahl der Muenzen
	 * 
	 * @param c		int, Anzahl der Muenzen
	 * **/
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
	public boolean isRemote(){
		return remote;
	}
	public void setRemote(boolean value){
		remote = value;
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
	/**
	 * Gibt das Quest zurueck, indem sich der Spieler befindet
	 * 
	 * @return Quest, in dem sich der Spieler befindet
	 * **/
	public Quest getQuest(){
		return quest;
	}
	
	public int getCollectedCoins(){
		return collectedCoins;
	}
	
	/**
	 * Verringert die Anzahl der Leben, setzt den Spieler an einen Checkpoint zurueck, beendet das Spiel, wenn die Anzahl der Leben nun 0 ist.
	 * 
	 * 
	 * **/
	public void lostLife(){
		System.out.println("Du hast ein Leben verloren, streng dich naechstes mal mehr an!");
		lifes--;

		if(lifes == 0){
			parent.lostGame();	
		}
		if(parent.singleplayer){
			parent.doInitializations(parent.level, 1); //Laden vom 1. Raum im jeweiligen Level
			x = parent.checkpointx;	//Spieler im 1. Raum an die Startposition setzen
			y = parent.checkpointy;
		}else if(parent.multiplayer){
			x = 650;
			y = 500;
		}
		
		//Bei Lebensverlust Werte auf alte Werte vom Checkpoint setzen
		coins = oldcoins;
		mana = oldmana;
		hasArmour = oldHasArmour;
		hasWeapon = oldHasWeapon;
				
		health = 100;
		hasArmour = 0;
		hasWeapon = 0;
				
	}
}

