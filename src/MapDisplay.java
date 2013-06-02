import java.awt.*;
import java.awt.geom.Rectangle2D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MapDisplay extends Rectangle{

	private static final long serialVersionUID = 1L;
	
	ImageControl control;
	Vector<Tile> tiles;
	Vector<Sprite> sprites;
	Rectangle2D display;
	private Tile t;
	
	
	
	public MapDisplay(String level, String picpath, String shadowpath, int column, int row, GamePanel p){
		tiles = new Vector<Tile> ();
		sprites = new Vector<Sprite> ();
		
		loadLevelData(level);
		
		control = ImageControl.getInstance();
		control.setSourceImage(picpath, column, row);
		control.setShadowImage(shadowpath, column, row);
		display = new Rectangle2D.Double(0,0,800,600);
		
	}

	private void loadLevelData(String level){
		try{
			FileReader fr = new FileReader(level);
			BufferedReader bufread = new BufferedReader(fr);
			
			String line = null;
			String delimiters = " ";
			String[] tokens;
			
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
		
			bufread.close(); //Reader wieder schließen
			fr.close();
		
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void drawVisibleMap(Graphics g){
		
		for(ListIterator<Tile> it = tiles.listIterator();it.hasNext();){
			t = it.next();

			g.drawImage((Image)(control.getImageAt(t.getImageNumber())), (int) t.x, (int) t.y, null);
			
		}
		
	}
	
	public Color getColorForPoint(Point p){//TODO: Finale Version schaffen + Problem mit ArrayIndexOutOfBounds (ab x = 160)
		//
		for(ListIterator<Tile> it = tiles.listIterator();it.hasNext();){ //alle Tiles werden überprüft
			t = it.next();
			
			if(t.contains(p)){
				int px = (int) (p.x - t.x); //Hier werden px und py auf die jeweiligen tile-bezogenen Koordinaten gesetzt (x und y Koordinate innerhalb des Tiles)
				int py = (int) (p.y - t.y);
				Color c = new Color(ImageControl.getInstance().getShadowImageAt(t.getImageNumber()).getRGB(px, py));
				return c;
			}
			
		}
				
		return Color.gray;
	}
}
