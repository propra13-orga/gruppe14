import java.awt.image.BufferedImage;

public class ImageControl {
	
	BufferedImage[] tiles; //Array aus BufferedImages zum Speichern der Bilddateien
	BufferedImage[] shadow;
	private static ImageControl instance;
	
	public static ImageControl getInstance(){
		if(instance == null){
			instance = new ImageControl();
		}
		
		return instance;
	}
	
	private ImageControl(){
		tiles = null;
	}
	
	public void setSourceImage(String path, int column, int row){
		SpriteLib lib = SpriteLib.getInstance();
		tiles = lib.getSprite(path, column, row);
	}
	
	public void setShadowImage(String path, int column, int row){
		SpriteLib lib = SpriteLib.getInstance();
		shadow = lib.getSprite(path, column, row);
	}
	
	public BufferedImage getImageAt(int num){
		return tiles[num]; //Abfragen eines bestimmten Bildes durch Index
	}
	
	BufferedImage getShadowImageAt(int num){
		return shadow[num];
	}
	public int getTileWidth(int num){
		return tiles[num].getWidth(); //gibt Breite von Tile zurück
	}
	
	public int getTileHeight(int num){
		return tiles[num].getHeight(); //gibt Höhe von Tile zurück
	}
}
