import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


import javax.swing.JComponent;

class DrawPanel extends JComponent{
	
	private static final long serialVersionUID = 1L;
	Image image;
	private final int RES;
	protected final int XDIMENSION, YDIMENSION;
	private int xxDimension;
	private int yyDimension;
	Graphics2D graphics2D;
	protected int currentX, currentY, oldX, oldY;
	protected static volatile int drawnCoord[][];
	protected static volatile int gridCoord[][];//hi-res matrix
	protected ArrayList<int[][]> trainingList = new ArrayList<int[][]>();
	protected ArrayList<String> sampleChar = new ArrayList<String>();
	private int sampleCount = 0;
	private Hopfield hopfield;
	//set debug mode by adjusting this value.
	//OFF-0, MATRIX-1, PIXELS-2, MATRIX_AND_PIXELS-3
	int debug = 4;

	public DrawPanel(int multiplier){
		XDIMENSION = 7 * multiplier;
		YDIMENSION = 9 * multiplier;
		RES = 28/multiplier;
		

		init();
		
		setDoubleBuffered(true);
		
		//if the mouse is pressed it sets the oldX & oldY
		//coordinates as the mouses x & y coordinates
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				oldX = e.getX();
				oldY = e.getY();
			}
		});
		
		/**
		 * while the mouse is dragged it sets currentX & currentY as the mouses x and y
		 * then it draws a line at the coordinates
		 * it repaints it and sets oldX and oldY as currentX and currentY
		**/
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				currentX = e.getX();
				currentY = e.getY();
				if(graphics2D != null)
				graphics2D.drawLine(oldX, oldY, currentX, currentY);
				repaint();
				oldX = currentX;
				oldY = currentY;
				//applies the coordinates to the array
				try{
				setCoord(currentX, currentY);
				}catch(Exception err){}
			}

		});
		
		//when mouse is released, pixel values are generated and drawn
		addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				setDrawnCoord();
				//hopfieldLearning();
				try{
					CharacterRecog.pixelPad.setCoord(drawnCoord);
					CharacterRecog.pixelPad.pixelate();
					if(debug==1||debug>2){
						printCoord();
						printDrawnArray();
					}
					if(debug>1)CharacterRecog.pixelPad.printPix();
					CharacterRecog.pixelPad.drawImage();
				}catch(Exception error){
					if(drawnCoord == null){
						System.err.print("Error in \'setCoord(<int[][]>)\' call.\n"
								+ "Pixelized array value \'null\'.");
					}
					error.printStackTrace();
				}
				if(CharacterRecog.train){
					hopfield.init(trainingList, drawnCoord, sampleChar);
					CharacterRecog.train = false;
				}
			}
		});
	}
	
	private void init(){
		hopfield = new Hopfield(CharacterRecog.multiplier);
		drawnCoord = new int[XDIMENSION][YDIMENSION];
		xxDimension = XDIMENSION *2;
		yyDimension = YDIMENSION *2;
		gridCoord = new int[xxDimension][yyDimension];
	}
	
	//initializes the JComponent drawing screens
	protected void paintComponent(Graphics g){
		if(image == null){
			clear();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.drawImage(image, 0, 0, null);
		graphics2D.setStroke(new BasicStroke(6));
	}

	//sets JComponent screens to their initial states
	protected void clear(){
		try{//load in the grid image file
			image = ImageIO.read(new File("src/grid.gif"));
			graphics2D = (Graphics2D)image.getGraphics();
			graphics2D.drawImage(image, 0, 0, this);
			graphics2D = (Graphics2D)image.getGraphics();
		}catch(IOException e){//if image file cannot load set blank default
			System.err.println("image: \'grid.gif\' not found");
			image = createImage(XDIMENSION * RES, YDIMENSION * RES);
			graphics2D.setPaint(Color.white);
			graphics2D.fillRect(0, 0, XDIMENSION * RES, YDIMENSION * RES);
		}finally{//set brush color, generate new pixel array;
			graphics2D.setPaint(Color.red);
			for(int i = 0; i < YDIMENSION; i++)
				for(int j = 0; j < XDIMENSION; j++)
					drawnCoord[j][i] = -1;
			gridCoord = new int[xxDimension][yyDimension];
			
			repaint();
			CharacterRecog.pixelPad.clear();
		}
	}
	
	/**
	 * If mouse coordinate values fall within a certain range(i.e. 0-14)
	 * they will return the value of activation (1) to the same location
	 * in the gridCoord array
	 * @param x : mouse coordinate
	 * @param y : mouse coordinate
	 */
	protected void setCoord(int x, int y){
		gridCoord[x/(RES / 2)][y/(RES / 2)] = 1;	
	}
	
	/**
	 * This takes the higher resolution gridCoord matrix (14x18) and
	 * uses its activation values to determine the values placed in 
	 * the lower resolution drawnCoord matrix(7x9). 
	 * Since for every coordinate in the drawnCoord matrix 
	 * there exists a 2x2 gridCoord sub-matrix...
	 * -1 : < 50% activation
	 * 0 : 50% activation
	 * 1 : > 50% activation
	 */
	/*
	 * TEST
	 * Setting drawnCoord activation values to binary
	 * -1 : 0% activation
	 * 1 : > 0% activation
	 */
	private void setDrawnCoord(){
		for(int y = 0; y < (YDIMENSION); y++){
			for(int x = 0; x < (XDIMENSION); x++){
				int total = 0;
				if(gridCoord[x*2][y*2] == 1){
					total++;
				}
				if(gridCoord[x*2 + 1][y*2] == 1){
					total++;
				}
				if(gridCoord[x*2][y*2 + 1] == 1){
					total++;
				}
				if(gridCoord[x*2 + 1][y*2 + 1] == 1){
					total++;
				}
				
				switch(total){
				case 0: drawnCoord[x][y] = -1;
				break;
				case 1: drawnCoord[x][y] = -1;
				break;
				case 2: drawnCoord[x][y] = 1;
				break;
				case 3: drawnCoord[x][y] = 1;
				break;
				default: drawnCoord[x][y] = 1;
				break;
				}
			}
		}
	}
	
	protected void addSample(){
		trainingList.add(drawnCoord);
		if(debug > 3){
			System.out.println("\nADDING:");
			printSample(sampleCount);
			System.out.println("Added character: " + sampleChar.get(sampleCount));
		}
		sampleCount++;
	}
	
	//debugger
	private void printSample(int i){
		for(int y = 0; y < YDIMENSION; y++){
			for(int x = 0; x < XDIMENSION; x++){
				if(trainingList.get(i)[x][y] >= 0){
					System.out.print(" ");
				}
				System.out.print(trainingList.get(i)[x][y]);
			}
			System.out.println();
		}
	}
	//debugger
	private void printCoord(){
		for(int y = 0; y < yyDimension; y++){
			for(int x = 0; x < xxDimension; x++){
				System.out.print(gridCoord[x][y]);
			}
			System.out.println();
		}
		System.out.println("\n--------------------\n");
	}	
	//debugger
	private void printDrawnArray(){
		for(int y = 0; y < YDIMENSION; y++){
			for(int x = 0; x < XDIMENSION; x++){
				if(drawnCoord[x][y] >= 0){
					System.out.print(" ");
				}
				System.out.print(drawnCoord[x][y]);
			}
			System.out.println();
		}
		System.out.println("\n--------------------\n");
	}
	
	//add new matrix to the arrayList
	protected void setSampleChar(String tChar){
		if(!CharacterRecog.passed){
			sampleChar.add(sampleCount, tChar);
		}
	}
	
}