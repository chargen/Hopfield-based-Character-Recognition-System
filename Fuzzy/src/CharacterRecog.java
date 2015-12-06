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
	static JFrame frame = new JFrame("Fuzzy Character Recognition");
	static JTextArea matchScreen;
	protected static String fontChar;
	protected static boolean passed = false;
	protected static int multiplier;
	protected static volatile boolean train = false;
	private static Hopfield hopfield;
	static CharacterRecog myProgram;
	
	final static JButton trainButton = new JButton("Train");
	
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
		
		final JSlider slider = new JSlider();
		slider.setMinimum(1);
		slider.setMaximum(8);
		JPanel panel = new JPanel();
		JPanel text = new JPanel();
		//creates a JPanel		
		JLabel character = new JLabel("Character");
		JLabel cStyle = new JLabel("Label");
		text.setLayout(new GridLayout(2, 2));
		//This sets the size of the panel and its segments
		//panel.setLayout(new GridLayout(7,1));
		panel.setLayout(null);
		panel.setBackground(Color.DARK_GRAY);
		panel.setPreferredSize(new Dimension(32, 68));
		panel.setMinimumSize(new Dimension(32, 68));
		panel.setMaximumSize(new Dimension(32, 68));
		
		
		/**
		 * Create and add JFrame Elements
		 */
		final JButton clearButton = new JButton("Clear");
		final JButton addButton = new JButton("Add Sample");
		
		
		//output screen for matches
		matchScreen = new JTextArea();
		matchScreen.setPreferredSize(new Dimension(32, 68));
		matchScreen.setMinimumSize(new Dimension(32, 68));
		matchScreen.setMaximumSize(new Dimension(32, 68));
		matchScreen.setEditable(false);
		
		JScrollPane matchPane = new JScrollPane(matchScreen, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
		slider.setBounds(25, 155, 100, 25);
		panel.add(slider);
		slider.setValue(multiplier);
		slider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(slider.getValueIsAdjusting()){
					drawPad.clear();
					multiplier = slider.getValue();
				}
				init();

			}			
		});
		//END JFrame Components
		
		//'Clear' button listener
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pixelPad.setCoord(drawPad.drawnCoord);
				pixelPad.drawImage();
				drawPad.clear();
				passed = false;
				fontChar = null;
				font.setText("");
				sampleChar.setText("");
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
				passed = true;
				drawPad.addSample();
				trainButton.setEnabled(true);
			}
		});
		
		trainButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				drawPad.clear();
				train = true;
				trainButton.setEnabled(false);
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