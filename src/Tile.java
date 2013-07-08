import java.awt.Graphics;
import java.awt.Rectangle;


public class Tile extends Rectangle{
	
	private static final long serialVersionUID = 1L;
	int image_num; //eigentliches Bild ist in ImageControl. 
	ImageControl control;
	/**
	 * Konstruktor des Tiles
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 * @param width Breite der Bilddatei
	 * @param height Höhe der Bilddatei
	 * @param num Nummer der Bilddatei, die sich in ImageControl befindet
	 */
	public Tile(int x, int y, int width, int height, int num){
		super(x,y,width,height);
		image_num = num;
		control = ImageControl.getInstance();
	}
	/**
	 * Gibt die Nummer des Tiles zurück
	 * @return
	 */
	public int getImageNumber(){
		return image_num;
	}
	/**
	 * Zeichnet das Tiles
	 * @param g
	 */
	public void drawTile(Graphics g){
		g.drawImage(control.getImageAt(image_num), x, y, null);
	}

}
