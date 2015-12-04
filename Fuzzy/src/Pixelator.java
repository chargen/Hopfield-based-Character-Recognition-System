import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;


import javax.swing.JComponent;

public class Pixelator extends JComponent{
	
	private static final long serialVersionUID = 1L;
	private final int RES = 28;
	private final int XDIMENSION = 7, YDIMENSION = 9;
	Image image;
	Graphics2D graphics2D;
	int currentX, currentY, oldX, oldY;
	int[][] coord;
	ArrayList<Integer[]> pixCoord = new ArrayList<Integer[]>();

	protected Pixelator(){
		coord = null;
	}

	//Initialize JComponent
	protected void paintComponent(Graphics g){
		if(image == null){
			image = createImage(XDIMENSION * RES, YDIMENSION * RES);
			graphics2D = (Graphics2D)image.getGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clear();
		}
		g.drawImage(image, 0, 0, null);
		graphics2D.setStroke(new BasicStroke(6));
	}

	//resets pixelCoord values, restores JComponent window to default
	protected void clear(){
		graphics2D.setPaint(Color.white);
		graphics2D.fillRect(0, 0, XDIMENSION * RES, YDIMENSION * RES);
		graphics2D.setPaint(Color.red);
		pixCoord.clear();
		repaint();
	}
	
	//get coordinates to be pixelated
	protected void setCoord(int[][] val){
		coord = val;
	}
	
	/**
	 * gets the coordinate values for the drawn rectangles to represent pixels
	 * adds only pixel locations to 'pixCoord' ArrayList
	 */
	protected void pixelate(){
		for(int x = 0; x < XDIMENSION; x++){
			for(int y = 0; y < YDIMENSION; y++){
				if(coord[x][y] >= 0){
					Integer[] pixXY = {(x*RES),(y*RES)};
					pixCoord.add(pixXY);
				}
			}
		}
	}

	//Draws pixelated version of handwritten image
	protected void drawImage(){
		while(!pixCoord.isEmpty()){
			graphics2D.fillRect(pixCoord.get(0)[0], pixCoord.get(0)[1], RES, RES);
			repaint();
			pixCoord.remove(0);
		}
	}
	
	//debugger
	protected void printPix(){
		for(int i = 0; i < pixCoord.size(); i++){
			System.out.print(" (" + pixCoord.get(i)[0] + ", " + pixCoord.get(i)[1] + ") ;");
		}
	}
}
