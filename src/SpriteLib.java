import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class SpriteLib {
	private static SpriteLib single;

	private static HashMap<URL, BufferedImage>sprites; //speichert unsere Bilder ab
	
	public static SpriteLib getInstance(){
		if(single==null){
			single = new SpriteLib();
		}
		
		return single;
	}
	
	private SpriteLib(){//Konstruktor
		
		sprites = new HashMap<URL, BufferedImage>(); 
	}
	
	/**
	 * liefert BufferedImage zurück für übergebenden Pfad
	 * @param path Pfad, an dem das Bild liegt
	 * @return BufferedImage, welches im path liegt
	 */
	public BufferedImage getSprite(String path){//liest aus URL Bild aus und speichert es in pic
		BufferedImage pic = null;
		URL location = getURLfromResource(path);
		pic = (BufferedImage) sprites.get(path);
		
		if(pic != null){
			return pic;
		}
		
		try{
			pic = ImageIO.read(new File(path)); //Über ImageIO das Bild lesen
		} catch (IOException e1){
			System.out.println("Fehler beim Image laden: " +e1);
			return null;
		}
				
		sprites.put(location, pic); //speichern des Bildes in HashMap (hier better statt pic für V.Images nehmen)
		
		return pic;
	}
	
	/**
	 * liefert BufferedImage zurück für übergebenden Pfad, speziell für Animation
	 * @param path Pfad, an dem das Bild liegt
	 * @param column Spalte im Bild
	 * @param row Zeile im Bild
	 * @return BufferedImage, welches im path liegt
	 */
	public BufferedImage [] getSprite(String path, int column, int row){//liest aus URL Bild bzw. Animation aus und speichert es in pics
		BufferedImage source = null;
		URL location = getURLfromResource(path);
		source = (BufferedImage) sprites.get(path);
		
		if(source == null){
			try{
				source = ImageIO.read(new File(path)); //Über ImageIO das Bild lesen
			} catch (IOException e1){
				System.out.println("Fehler beim Image laden: " +e1);
				return null;
			}
			
			sprites.put(location, source);
		}
		
		
		int width = source.getWidth() / column;
		int height = source.getHeight()/row;
		
		BufferedImage[] pics = new BufferedImage[column * row];
		int count = 0;
		
		for (int n = 0; n < row; n ++){
			for(int i = 0; i < column; i++){
				pics[count] = source.getSubimage(i * width, n * height, width, height);
				count++;
			}
		}
		
		//System.out.println("getSprite2");
		return pics;
	}
	
	public URL getURLfromResource(String path){
		return getClass().getClassLoader().getResource(path); 
	}
}
