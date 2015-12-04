import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class CharacterRecog{
	
	private final static int XDIMENSION = 7;
	private final static int YDIMENSION = 9;
	protected static Pixelator pixelPad = new Pixelator(XDIMENSION, YDIMENSION);
	JFrame frame = new JFrame("Fuzzy Character Recognition");
	protected static String fontChar;
	protected static boolean passed = false;
	
	public CharacterRecog(){
		init();
	}
	
	private void init(){
		Container content = frame.getContentPane();
		content.setLayout(new CardLayout());
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(1, 4));
		content.add(contentPane);
		
		final DrawPanel drawPad = new DrawPanel(XDIMENSION, YDIMENSION);
		//sets the padDraw in the center		
		contentPane.add(drawPad);
		contentPane.add(pixelPad);
		
		final JSlider slider = new JSlider();
		slider.setMinimum(2);
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
		panel.setPreferredSize(new Dimension(32, 68));
		panel.setMinimumSize(new Dimension(32, 68));
		panel.setMaximumSize(new Dimension(32, 68));

		
		/**
		 * Create and add JFrame Elements
		 */
		final JButton clearButton = new JButton("Clear");
		final JButton addButton = new JButton("Add Sample");
		final JButton trainButton = new JButton("Train");
		
		//output screen for matches
		final JTextArea matchScreen = new JTextArea();
		matchScreen.setPreferredSize(new Dimension(32, 68));
		matchScreen.setMinimumSize(new Dimension(32, 68));
		matchScreen.setMaximumSize(new Dimension(32, 68));
		matchScreen.setEditable(false);
		
		JScrollPane matchPane = new JScrollPane(matchScreen, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final JScrollBar scrollbar = new JScrollBar();
		matchPane.add(scrollbar);
		
		clearButton.setBounds(25, 25, 100, 25);
		addButton.setBounds(135, 25, 100, 25);
		final JTextField trainChar = new JTextField();
		trainChar.setDocument(new TextFieldLimit(1));
		final JTextField font = new JTextField();
		font.setDocument(new TextFieldLimit(4));
		text.add(character);
		text.add(cStyle);
		text.add(trainChar);
		text.add(font);
		text.setBounds(25, 55, 200, 50);
		panel.add(clearButton);
		panel.add(addButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(text);
		panel.add(Box.createHorizontalStrut(10));
		trainButton.setBounds(25, 125, 200, 25);
		panel.add(trainButton);
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
				trainChar.setText("");
			}
		});
		
		/**
		 * Disables trainButton until both text fields are filled
		 */
		addButton.setEnabled(false);
		trainChar.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e){changed();}
			@Override
			public void insertUpdate(DocumentEvent arg0) {changed();}
			@Override
			public void removeUpdate(DocumentEvent arg0) {changed();}
			public void changed(){
				if(trainChar.getText().equals("") || font.getText().equals("")){
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
				if(font.getText().equals("") || trainChar.getText().equals("")){ 
					addButton.setEnabled(false);
				}else{addButton.setEnabled(true);}
			}
		});
		
		//'Train' button listener
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String fChar = trainChar.getText();
				String charFont = font.getText();
				drawPad.setSampleChar(fChar +"-"+ charFont);
				passed = true;
				drawPad.addSample();
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
		@SuppressWarnings("unused")
		CharacterRecog myProgram = new CharacterRecog();
	}
}