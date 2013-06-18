import java.awt.image.BufferedImage;

public class Quest extends Sprite{
	private static final long serialVersionUID = 1L;
	private int questcoins;
	
	private int type; //Welche Types? Beispielsweise 1 = Matheaufgabe, 2 = Münzen sammeln, 3 = Gegner töten, 4 = Was weiß ich!
	public Quest(BufferedImage[] i, double x, double y, int type, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		this.type = type;
		if(type == 2){
			questcoins = (int)(Math.random()*10); //liefert Zufallszahl zwischen 0 bis 50
		}		
	}
	
	public int getType(){
		return type;
	}
	
	public int getQuestCoins(){

		return questcoins; //Gibt, falls Typ = 2 ist, Anzahl der benötigten Münzen aus
		
	}
}
