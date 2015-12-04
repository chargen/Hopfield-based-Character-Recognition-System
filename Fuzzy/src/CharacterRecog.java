import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class CharacterRecog{
	protected static Pixelator pixelPad = new Pixelator();
	JFrame frame = new JFrame("Fuzzy Character Recognition");
	protected static String fontChar;
	protected static boolean passed = false;
	
	public CharacterRecog(){
		init();
	}
	
	private void init(){
		Container content = frame.getContentPane();
		content.setLayout(new GridLayout(1, 3));
		
		final DrawPanel drawPad = new DrawPanel();
		//sets the padDraw in the center		
		content.add(drawPad, BorderLayout.WEST);
		content.add(pixelPad, BorderLayout.EAST);
		
		JPanel panel = new JPanel();
		JPanel text = new JPanel();
		//creates a JPanel		
		JLabel character = new JLabel("Character");
		JLabel cStyle = new JLabel("Label");
		text.setLayout(new GridLayout(2, 2));
		//This sets the size of the panel and its segments
		panel.setLayout(new GridLayout(7,1));
		panel.setPreferredSize(new Dimension(32, 68));
		panel.setMinimumSize(new Dimension(32, 68));
		panel.setMaximumSize(new Dimension(32, 68));

		
		/**
		 * Create and add JFrame Elements
		 */
		final JButton clearButton = new JButton("Clear");
		final JButton trainButton = new JButton("Train");
		clearButton.setBounds(0, 0, 25, 10);
		trainButton.setBounds(0, 10, 25, 10);
		final JTextField trainChar = new JTextField();
		trainChar.setDocument(new TextFieldLimit(1));
		final JTextField font = new JTextField();
		font.setDocument(new TextFieldLimit(4));
		text.add(character);
		text.add(cStyle);
		text.add(trainChar);
		text.add(font);
		panel.add(clearButton);
		panel.add(trainButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(text);
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
		trainButton.setEnabled(false);
		trainChar.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e){changed();}
			@Override
			public void insertUpdate(DocumentEvent arg0) {changed();}
			@Override
			public void removeUpdate(DocumentEvent arg0) {changed();}
			public void changed(){
				if(trainChar.getText().equals("") || font.getText().equals("")){
					trainButton.setEnabled(false);
					}else{ trainButton.setEnabled(true);}
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
					trainButton.setEnabled(false);
				}else{trainButton.setEnabled(true);}
			}
		});
		
		//'Train' button listener
		trainButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String fChar = trainChar.getText();
				String charFont = font.getText();
				drawPad.setTrainedChar(fChar +"-"+ charFont);
				passed = true;
				drawPad.trainNetwork();
			}
		});
		
		content.add(panel, BorderLayout.WEST);
		
		frame.setSize(800, 300);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args){
		@SuppressWarnings("unused")
		CharacterRecog myProgram = new CharacterRecog();
	}
}