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
			
			do{ //Einlesen der Leveldaten
				line = bufread.readLine();
				
				if(line == null){
					continue;
				}
				
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
				
			} while (line!= null);
		
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
	
	public Color getColorForPoint(Point p){
		for(Tile t:tiles){
			double dx = t.x - display.getX();
			double dy = t.y - display.getY();
			//double dx = t.x;
			//double dy = t.y;
			//Rectangle temp2 = new Rectangle((int)dx, (int)dy, (int)t.getWidth(), (int)t.getHeight());
			
			/*if(temp2.contains(p)){*/
				int px = (int) (p.x - dx);
				int py = (int) (p.y - dy);
				
				Color c = new Color(ImageControl.getInstance().getShadowImageAt(t.getImageNumber()).getRGB(px, py));
				System.out.println(c.toString());
				return c;
			//}
		}
				
		return null;
	}
}