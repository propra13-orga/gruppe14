import java.awt.*;
import java.awt.geom.Rectangle2D;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MapDisplay extends Rectangle{

	private static final long serialVersionUID = 1L;
	
	ImageControl control;
	Vector<Tile> tiles;
	Rectangle2D display;
	
	
	public MapDisplay(String level, String picpath, String shadowpath, int column, int row, GamePanel p){
		tiles = new Vector<Tile> ();
		
		loadLevelData(level);
		
		control = ImageControl.getInstance();
		control.setSourceImage(picpath, column, row);
		control.setShadowImage(shadowpath,  column, row);
		display = new Rectangle2D.Double(0,0,800,600);
		
	}

	private void loadLevelData(String level){
		try{
			InputStreamReader isr = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(level));
			BufferedReader bufread = new BufferedReader(isr);
			
			String line = null;
			String delimiters = " ";
			String[] tokens;
			
			/*do{ //Einlesen der Leveldaten
				line = bufread.readLine();
				
				if(line == null){
					continue;
				}
				//TODO: Eingabeformat einfacher gestalten, L�nge und Breite kann wegen Standarisierung rausgeschmissen werden - s. Max' Version
				String[] split = line.split("/"); //Seperation der einzelnen Informationen
				int posx = Integer.parseInt(split[0]); 
				int posy = Integer.parseInt(split[1]);
				int width = Integer.parseInt(split[2]);
				int height = Integer.parseInt(split[3]);
				int num = Integer.parseInt(split[4]);
				
				if((posx + width) > this.width){
					this.width = posx + width;
				}
				
				if((posy + height) > this.height){
					this.height = posy + height;
				}
				
				Tile t = new Tile(posx, posy, width, height, num);
				tiles.add(t);
				
			} while (line!= null);*/
			
			line = bufread.readLine();
			int height= Integer.parseInt(line);
			line = bufread.readLine();
			int width = Integer.parseInt(line);
			line = bufread.readLine();
			int tile_height = Integer.parseInt(line);
			line = bufread.readLine();
			int tile_width = Integer.parseInt(line);
			
			int posx;
			int posy;
			int num;

			for (int row = 0; row < height; row++){
				
				line = bufread.readLine();
				if(line == null){
					continue;
				}
				tokens = line.split(delimiters);
				for (int col = 0; col < width; col++){
					posx = col*tile_width;
					posy = row*tile_height;
					num = Integer.parseInt(tokens[col]);
					
					if((posx + tile_width) > this.width){
						this.width = posx + tile_width;
					}
					if((posy + tile_height) > this.height){
						this.height = posy + tile_height;
					}
					Tile t = new Tile (posx, posy, tile_width, tile_height, num);
					tiles.add(t);					
				}
			}
		
			bufread.close(); //Reader wieder schlie�en
			isr.close();
		
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void drawVisibleMap(Graphics g){
		
		for(Tile t:tiles){
			if(t.intersects(display)){
				//double dx = t.x - display.getX();
				//double dy = t.y-display.getY();
				g.drawImage((Image)(control.getImageAt(t.getImageNumber())), (int) t.x, (int) t.y, null);
			}
		}
	}
	
	public Color getColorForPoint(Point p){//TODO: Finale Version schaffen + Problem mit ArrayIndexOutOfBounds
		//
		for(Tile t:tiles){ //alle Tiles werden �berpr�ft
			//double dx = t.x - display.getX(); //Wir bewegen display gar nicht, also hier wohl unn�tig
			//double dy = t.y - display.getY();
			double dx = t.x;
			double dy = t.y;
			//Erstellt Rechteck von der Gr��e eines Tiles
			Rectangle temp = new Rectangle((int)dx, (int)dy, (int)t.getWidth(), (int)t.getHeight()); //oder:
			//Rectangle temp = new Rectangle((t.x, t.y, (int)t.getWidth(), (int)t.getHeight());
			
			if(temp.contains(p)){ //Wenn Tile gefunden, welches p beinhaltet:
				//int px = (int) (p.x - dx); //Was hat diese �nderung zu bedeuten?
				//int py = (int) (p.y - dy);
				
				Color c = new Color(ImageControl.getInstance().getShadowImageAt(t.getImageNumber()).getRGB((int)dx, (int)dy));
				return c;
			}
		}
				
		return null;
	}
}
