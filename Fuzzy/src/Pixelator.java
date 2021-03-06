import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;


import javax.swing.JComponent;

public class Pixelator extends JComponent{
	
	private static final long serialVersionUID = -6519692501682081558L;
	protected static final int WIDTH = 196;
	protected static final int HEIGHT = 252;
	private static int RES;
	private static int XDIMENSION, YDIMENSION;
	Image image;
	Graphics2D graphics2D;
	int currentX, currentY, oldX, oldY;
	protected double[][] coord;
	ArrayList<Integer[]> pixCoord = new ArrayList<Integer[]>();

	protected Pixelator(int multiplier){
		XDIMENSION = 7 * multiplier;
		YDIMENSION = 9 * multiplier;
		RES = 28 / multiplier;
		
		coord = null;
	}

	//Initialize JComponent
	protected void paintComponent(Graphics g){
		if(image == null){
			image = createImage(WIDTH, HEIGHT);
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
		graphics2D.fillRect(0, 0, WIDTH, HEIGHT);
		graphics2D.setPaint(Color.red);
		pixCoord.clear();
		repaint();
	}
	
	//get coordinates to be pixelated
	protected void setCoord(double[][] val){
		coord = val;
	}
	
	/**
	 * gets the coordinate values for the drawn rectangles to represent pixels
	 * adds only pixel locations to 'pixCoord' ArrayList
	 */
	protected void pixelate(){
		for(int y = 0; y < YDIMENSION; y++){
			for(int x = 0; x < XDIMENSION; x++){
				if(coord[y][x] >= 0){
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
