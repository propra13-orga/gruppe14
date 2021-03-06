import java.awt.image.BufferedImage;

public class Item extends Sprite{

	public final static int COIN = 1;
	public final static int MANA = 2;
	public final static int SHOP = 3;
	public final static int RUESTUNGEIS = 4;
	public final static int RUESTUNGFEUER = 9;
	public final static int EISWAFFE = 5;
	public final static int FEUERWAFFE = 10;
	public final static int NPC = 6;
	public final static int HEALTHPACK = 7;
	public final static int XPDINGS = 11;
	
	
	private static final long serialVersionUID = 1L;
	
	private int type; //1 = Coins, 2 = Mana, 3 = Shop, 4 = R�stung, 5 = Waffe, 6 = NPC, 7 = healthpack
	/**
	 * Konstruktor fuer ein Item.
	 * @param i, BufferedImage-Array fuer die Graphik des Items
	 * @param x, double-X-Koordinate
	 * @param y, double-Y-Koordinate
	 * @param type, int-Wert fuer den Typ des Items (siehe final static int - Werte)
	 * **/
	public Item(BufferedImage[] i, double x, double y, int type, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		this.type = type;
		// TODO Auto-generated constructor stub
	}
	/**
	 * Liefert den Typ des Items zurueck (siehe final static int - Werte)
	 * @return int-Typ des Items
	 * **/
	public int getType(){
		return type;
	}
	
}
