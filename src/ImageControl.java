import java.awt.image.BufferedImage;

public class ImageControl {
	
	BufferedImage[] tiles; //Array aus BufferedImages zum Speichern der Bilddateien
	BufferedImage[] shadow; //Array aus BufferedImages zum Speichern der Shadow-Bilddateien
	private static ImageControl instance;
	/**
	 * Liefert die einzige Instanz von ImageControl zurueck.
	 * @return die einzige Instanz von ImageControl.
	 * 
	 * **/
	public static ImageControl getInstance(){
		if(instance == null){
			instance = new ImageControl();
		}
		
		return instance;
	}
	
	private ImageControl(){
		tiles = null;
	}
	/**
	 * Setzt die Quelldatei fuer die Tiles.
	 * @param path, Pfad zu den Tile-Bilddateien
	 * @param column, Anzahl der Zeilen der Tile-Bilddatei
	 * @param row, Anzahl der Spalten der Tile-Bilddatei
	 * **/
	public void setSourceImage(String path, int column, int row){
		SpriteLib lib = SpriteLib.getInstance();
		tiles = lib.getSprite(path, column, row);
	}
	/**
	 * Setzt die Quelldatei fuer die Schatten- (Kollisions-) Tiles
	 * @param path, Pfad zu den Schatten-Tile-Bilddateien
	 * @param column, Anzahl der Zeilen der Schatten-Tile-Bilddatei
	 * @param row, Anzahl der Spalten der Schatten-Tile-Bilddatei
	 * **/
	public void setShadowImage(String path, int column, int row){
		SpriteLib lib = SpriteLib.getInstance();
		shadow = lib.getSprite(path, column, row);
	}
	/**
	 * Liefert das Bild aus den Tiles am gegebenen Index
	 * @param num, der Index
	 * @return, das Bild am gegebenen Index
	 * **/
	public BufferedImage getImageAt(int num){
		return tiles[num]; //Abfragen eines bestimmten Bildes durch Index
	}
	/**
	 * Liefert das Bild aus den SchattenTiles am gegebenen Index
	 * @param num, der Index
	 * @return, das Bild am gegebenen Index
	 * **/
	BufferedImage getShadowImageAt(int num){
		return shadow[num];
	}
	/**
	 * Liefert die Breite des Bildes aus den Tiles am gegebenen Index
	 * @param num, der Index
	 * @return, Breite des Bildes am gegebenen Index
	 * **/
	public int getTileWidth(int num){
		return tiles[num].getWidth(); //gibt Breite von Tile zurück
	}
	/**
	 * Liefert die Hoehe des Bildes aus den Tiles am gegebenen Index
	 * @param num, der Index
	 * @return, Hoehe des Bildes am gegebenen Index
	 * **/
	public int getTileHeight(int num){
		return tiles[num].getHeight(); //gibt Höhe von Tile zurück
	}
}
