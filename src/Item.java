import java.awt.image.BufferedImage;

public class Item extends Sprite{

	private int type; //1 = Coins, 2 = Mana, 3 = Shop, 4 = Rüstung, 5 = Waffe
	public Item(BufferedImage[] i, double x, double y, int type, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		this.type = type;
		// TODO Auto-generated constructor stub
	}
	
	public int getType(){
		return type;
	}
	
}
