import java.awt.image.BufferedImage;
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
	
	public BufferedImage getSprite(String path){//liest aus URL Bild aus und speichert es in pic
		BufferedImage pic = null;
		URL location = getURLfromResource(path);
		pic = (BufferedImage) sprites.get(location);
		
		if(pic != null){
			return pic;
		}
		
		try{
			pic = ImageIO.read(location); //Über ImageIO das Bild lesen
		} catch (IOException e1){
			System.out.println("Fehler beim Image laden: " +e1);
			return null;
		}
		
		sprites.put(location, pic); //speichern des Bildes in HashMap
		
		return pic;
	}
	
	public BufferedImage [] getSprite(String path, int column, int row){//liest aus URL Bild bzw. Animation aus und speichert es in pics
		BufferedImage source = null;
		URL location = getURLfromResource(path);
		source = (BufferedImage) sprites.get(location);
		
		if(source == null){
			try{
				source = ImageIO.read(location); //Über ImageIO das Bild lesen
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
		
		return pics;
	}
	
	public URL getURLfromResource(String path){
		return getClass().getClassLoader().getResource(path); 
	}
}
