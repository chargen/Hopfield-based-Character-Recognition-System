import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class CharacterRecog{

	protected static final int XDIMENSION = 7;
	protected static final int YDIMENSION = 9;
	protected static final int WIDTH = 1200;
	protected static final int HEIGHT = 300;
	protected static Pixelator pixelPad;
	protected static DrawPanel drawPad;
	static JFrame frame = new JFrame("Hopfield Character Recognition");
	static JTextArea matchScreen;
	protected static String fontChar;
	protected static int multiplier;
	protected static volatile boolean train = false;
	private static Hopfield hopfield;
	static CharacterRecog myProgram;
	protected static volatile boolean tOutput = false;
	
	final static JButton trainButton = new JButton("Train Network");
	
	public CharacterRecog(int mult){
		multiplier = mult;
		
		
		init();
	}
	
	protected static void init(){
		pixelPad = new Pixelator(multiplier);
		drawPad = new DrawPanel(multiplier);
		hopfield = new Hopfield(multiplier);
		
		Container content = frame.getContentPane();
		content.setLayout(new CardLayout());
		
		final JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(1, 4));
		contentPane.setBackground(Color.DARK_GRAY);
		content.add(contentPane);
			
		contentPane.add(drawPad);
		contentPane.add(pixelPad);
		JPanel panel = new JPanel();
		JPanel text = new JPanel();
		//creates a JPanel		
		JLabel character = new JLabel("Character");
		JLabel cStyle = new JLabel("Label");
		text.setLayout(new GridLayout(2, 2));
		//This sets the size of the panel and its segments
		//panel.setLayout(new GridLayout(7,1));
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(32, 68));
		panel.setMinimumSize(new Dimension(32, 68));
		panel.setMaximumSize(new Dimension(32, 68));
		
		
		/**
		 * Create and add JFrame Elements
		 */
		final JButton clearButton = new JButton("Clear");
		final JButton addButton = new JButton("Add Sample");
		final JButton testButton = new JButton("Test");
		final JButton resetButton = new JButton("Discard");
		final JCheckBox outputBox = new JCheckBox("Draw Input Characters");
		
		
		//output screen for matches
		matchScreen = new JTextArea();
		matchScreen.setEditable(false);
		
		JScrollPane matchPane = new JScrollPane(matchScreen,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final JScrollBar scrollbar = new JScrollBar();
		matchPane.add(scrollbar);
		
		clearButton.setBounds(25, 25, 100, 25);
		addButton.setBounds(135, 25, 100, 25);
		final JTextField sampleChar = new JTextField();
		sampleChar.setDocument(new TextFieldLimit(1));
		final JTextField font = new JTextField();
		font.setDocument(new TextFieldLimit(4));
		text.add(character);
		text.add(cStyle);
		text.add(sampleChar);
		text.add(font);
		text.setBounds(25, 55, 200, 50);
		panel.add(clearButton);
		panel.add(addButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(text);
		panel.add(Box.createHorizontalStrut(10));
		trainButton.setBounds(25, 125, 200, 25);
		panel.add(trainButton);
		trainButton.setEnabled(false);
		resetButton.setBounds(25, 155, 100, 25);
		panel.add(resetButton);
		resetButton.setEnabled(false);
		resetButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				drawPad.trainingList.clear();
				drawPad.sampleChar.clear();
				drawPad.sampleCount = 0;
				drawPad.clear();
				trainButton.setEnabled(false);
				testButton.setEnabled(false);
				font.setText("");
				sampleChar.setText("");
				matchScreen.setText("");
			}
			
		});
		
		testButton.setBounds(130, 155, 100, 25);
		panel.add(testButton);
		testButton.setEnabled(false);
		testButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(train){
					train = false;
					testButton.setText("Test");
					trainButton.setEnabled(true);
					drawPad.clear();
					resetButton.setEnabled(true);
				}else{
					train = true;
					testButton.setText("Testing...");
					resetButton.setEnabled(false);
					trainButton.setEnabled(false);
				}
			}
			
		});
		outputBox.setBounds(25, 190, 200, 25 );
		panel.add(outputBox);
		outputBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tOutput = outputBox.isSelected();
			}
			
		});
		//END JFrame Components
		
		//'Clear' button listener
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pixelPad.setCoord(drawPad.drawnCoord);
				pixelPad.drawImage();
				drawPad.clear();
				fontChar = null;
				font.setText("");
				sampleChar.setText("");
				matchScreen.setText("");
			}
		});
		
		/**
		 * Disables trainButton until both text fields are filled
		 */
		addButton.setEnabled(false);
		sampleChar.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e){changed();}
			@Override
			public void insertUpdate(DocumentEvent arg0) {changed();}
			@Override
			public void removeUpdate(DocumentEvent arg0) {changed();}
			public void changed(){
				if(sampleChar.getText().equals("") || font.getText().equals("")){
					addButton.setEnabled(false);
					}else{ addButton.setEnabled(true);}
			}
		});
		
		font.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e){changed();}
			@Override
			public void insertUpdate(DocumentEvent arg0) {changed();}
			@Override
			public void removeUpdate(DocumentEvent arg0) {changed();}
			public void changed(){
				if(font.getText().equals("") || sampleChar.getText().equals("")){ 
					addButton.setEnabled(false);
				}else{addButton.setEnabled(true);}
			}
		});
		
		//'Add Sample' button listener
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String fChar = sampleChar.getText();
				String charFont = font.getText();
				drawPad.setSampleChar(fChar +"-"+ charFont);
				drawPad.addSample();
				drawPad.clear();
				font.setText("");
				sampleChar.setText("");
				trainButton.setEnabled(true);
				
			}
		});
		
		trainButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				drawPad.clear();
				testButton.setEnabled(true);
				resetButton.setEnabled(true);
			}
			
		});
		contentPane.add(matchPane);
		contentPane.add(panel, BorderLayout.WEST);
		
		frame.setSize(1200, 300);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args){
		 myProgram = new CharacterRecog(1);
	}
}