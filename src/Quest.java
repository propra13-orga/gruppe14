import java.awt.image.BufferedImage;

public class Quest extends Sprite{
	private static final long serialVersionUID = 1L;
	private int questcoins;
	int teiler;
	int multi;
	
	private int type; //Welche Types? Beispielsweise 1 = Matheaufgabe, 2 = Münzen sammeln, 3 = Gegner töten, 4 = Was weiß ich!
	public Quest(BufferedImage[] i, double x, double y, int type, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		this.type = type;
		if(type == 2){
			questcoins = (int)(Math.random()*5) + 1; //liefert Zufallszahl zwischen 0 bis 50
		}		
		if(type == 1){
			questcoins = (int)(Math.random()*5) + 1; //liefer Zufallszahl zwischen 0 bis 50
			
			System.out.println("Questcoins: " + questcoins);
			teiler = 13; //13 ist Primzahl, hier kann also beim ersten Schleifendurchlauf nix passieren
			while((questcoins % teiler != 0)){
				teiler = (int)(Math.random()*5) + 1; //hier wird abgefangen, dass teiler 0 wird
				System.out.println(teiler);
			}

			multi = questcoins / teiler;
		}
	}
	
	public int getType(){
		return type;
	}
	
	public int getQuestCoins(){

		return questcoins; //Gibt, falls Typ = 2 oder 1 ist, Anzahl der benötigten Münzen aus
		
	}
}
