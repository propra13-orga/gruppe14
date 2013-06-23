import java.awt.image.BufferedImage;

public class Item extends Sprite{

	public final static int COIN = 1;
	public final static int MANA = 2;
	public final static int SHOP = 3;
	public final static int RÜSTUNGEIS = 4;
	public final static int RÜSTUNGFEUER = 9;
	public final static int EISWAFFE = 5;
	public final static int FEUERWAFFE = 10;
	public final static int NPC = 6;
	public final static int HEALTHPACK = 7;
	
	
	private static final long serialVersionUID = 1L;
	
	private int type; //1 = Coins, 2 = Mana, 3 = Shop, 4 = Rüstung, 5 = Waffe, 6 = NPC, 7 = healthpack
	public Item(BufferedImage[] i, double x, double y, int type, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		this.type = type;
		// TODO Auto-generated constructor stub
	}
	
	public int getType(){
		return type;
	}
	
}
