

import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * Class which construct the GUI for easier viewing and better access to
 * variations
 * 
 * @author mak1g11
 * 
 */
public class KNN_GUI {
	private KNN knn;
	private JFrame kFrame;
	private JLabel file, kLabel;
	private JTextField kValue;
	private JPanel kPanel, cPanel;
	private JButton classify, help, browse;
	private JCheckBox[] boxes;
	private JScrollPane jscp;
	private JTextArea results;
	private SpringLayout sp;

	/**
	 * Constructor for the class, initialises some of the private variables
	 */
	public KNN_GUI() {
		knn = new KNN();
		kFrame = new JFrame("KNN Classifier");
		kPanel = new JPanel();
		cPanel = new JPanel();
		jscp = new JScrollPane(cPanel);
		sp = new SpringLayout();
		kFrame.setSize(300, 375);
		kFrame.setResizable(false);
		kFrame.setLocationRelativeTo(null);
		kFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		KNN_GUI kg = new KNN_GUI();
		kg.initiate();
	}

	/**
	 * Initiates the building of the GUI
	 */
	public void initiate() {
		kFrame.add(kPanel);
		kFrame.setContentPane(kPanel);
		addComponents();
		kFrame.setVisible(true);
	}

	/**
	 * Adds the buttons, fields etc and also lays them out with SpringLayout
	 */
	public void addComponents() {
		kValue = new JTextField(2);
		// can't enter value unless file is chosen
		kValue.setEditable(false);
		kLabel = new JLabel("k:");
		file = new JLabel("File not selected");
		browse = new JButton("Choose File...");
		classify = new JButton("Classify");
		// button can't be clicked until file is selected
		classify.setEnabled(false);
		help = new JButton("Help");
		results = new JTextArea();
		results.setEditable(false);
		jscp.setPreferredSize(new Dimension(kFrame.getWidth() - 25, 100));
		JScrollPane jsp = new JScrollPane(results);
		jsp.setPreferredSize(new Dimension(250, 200));

		ButtonListener bl = new ButtonListener();
		kPanel.setLayout(sp);

		browse.addActionListener(bl);
		help.addActionListener(bl);
		classify.addActionListener(bl);
		kValue.addKeyListener(new Keys());

		kPanel.add(file);
		kPanel.add(browse);
		kPanel.add(kLabel);
		kPanel.add(kValue);
		kPanel.add(jscp);
		kPanel.add(classify);
		kPanel.add(jsp);
		kPanel.add(help);

		// Panel containing check boxes won't be showing curently
		jscp.setVisible(false);

		sp.putConstraint("North", file, 20, "North", kPanel);
		sp.putConstraint("West", file, 20, "West", kPanel);
		sp.putConstraint("North", browse, 15, "North", kPanel);
		sp.putConstraint("West", browse, 100, "West", file);

		sp.putConstraint("North", kLabel, 40, "North", file);
		sp.putConstraint("West", kLabel, 20, "West", kPanel);
		sp.putConstraint("North", kValue, 40, "North", file);
		sp.putConstraint("West", kValue, 20, "West", kLabel);
		sp.putConstraint("North", classify, 35, "North", file);
		sp.putConstraint("West", classify, 90, "West", kValue);
		sp.putConstraint("North", jsp, 40, "North", classify);
		sp.putConstraint("West", jsp, 20, "West", kPanel);
		sp.putConstraint("North", help, 210, "North", jsp);
		sp.putConstraint("West", help, 75, "West", kValue);
	}

	/**
	 * Makes the panel visible which is to contain the checkboxes, layout is
	 * also changed accordingly
	 */
	public void showChoices() {
		kFrame.setSize(300, 500);
		jscp.setVisible(true);

		sp.putConstraint("North", jscp, 40, "North", file);
		sp.putConstraint("West", jscp, 5, "West", kPanel);

		sp.putConstraint("North", kLabel, 120, "North", jscp);
		sp.putConstraint("West", kLabel, 20, "West", kPanel);
		sp.putConstraint("North", kValue, 120, "North", jscp);
		sp.putConstraint("West", kValue, 20, "West", kLabel);
		sp.putConstraint("North", classify, 115, "North", jscp);

	}

	/**
	 * Adds the check boxes in a vertical list to allow the user to experiment
	 * with different dimensions
	 */
	public void makeCheckBoxes() {
		// number of boxes is found dynamically
		boxes = new JCheckBox[knn.getDimensionNames().length];
		for (int i = 0; i < boxes.length; i++) {
			String name = knn.getDimensionNames()[i];
			boxes[i] = new JCheckBox(name, true);
			cPanel.add(boxes[i]);
			// allows viewing in a nice vertical list
			cPanel.setLayout((new GridLayout(boxes.length, 1)));
		}
	}

	/**
	 * Custom Action Listener class for the buttons
	 * 
	 * @author mak1g11
	 * 
	 */
	public class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// if browse has been clicked
			if (e.getSource() == browse) {
				JFileChooser jfc = new JFileChooser();
				int value = jfc.showOpenDialog(null);
				jfc.setAcceptAllFileFilterUsed(false);
				File f = jfc.getSelectedFile();
				if (value == JFileChooser.APPROVE_OPTION) {
					// file must be in .txt format
					if (!f.getName().endsWith(".txt")) {
						JOptionPane
								.showMessageDialog(
										kPanel,
										"The file is not in the correct format (should be .txt)",
										"Wrong Format",
										JOptionPane.WARNING_MESSAGE);
					} else { // file is valid
						// data set built
						knn.buildDataset(f);
						// results scaled
						knn.scaleResults();
						// label shows the file which has been selected
						file.setText(f.getName());

						// user can now click on classify and specify k
						classify.setEnabled(true);
						kValue.setEditable(true);

						// the choices for dimensions are shown as well
						KNN_GUI.this.showChoices();
						KNN_GUI.this.makeCheckBoxes();

					}
				}
			} else if (e.getSource() == help) { // small help text on the text
												// area
				results.setText("Choose a .txt file, value of k and click on \n'Classify'. This field will show the results.");
			} else if (e.getSource() == classify) {
				// tries to classify but needs to check if k is number
				try {
					// gets value of k
					int k = Integer.parseInt(kValue.getText());
					// value shouldn't be greater than 25, accuracy will be
					// decreased/not be optimum
					if (k > 25) {
						JOptionPane.showMessageDialog(kPanel,
								"The value of K is too high", "K Value",
								JOptionPane.WARNING_MESSAGE);
					} else {
						// k set
						knn.setK(k);
						int val = boxes.length;

						// checked and notes which dimensions are checked and
						// which aren't
						for (int i = 0; i < boxes.length; i++) {
							if (boxes[i].isSelected()) {
								knn.getChoices()[i] = 1;
							} else {
								knn.getChoices()[i] = 0;
								val -= 1;
							}
						}
						// all dimensions can't be unchecked
						if (val == 0) {
							JOptionPane
									.showMessageDialog(
											kPanel,
											"No Dimension has been selected. Please select at least one dimension.",
											"No Dimension Selected",
											JOptionPane.WARNING_MESSAGE);
						} else {
							// all data sets are classified and results are
							// displayed in TextArea
							results.setText(knn.classifyAll());
						}
					}
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(kPanel,
							"The value of k is not a number", "Not a Number",
							JOptionPane.WARNING_MESSAGE);
				}

			}
		}

	}

	/**
	 * Custom class to listen to key presses
	 * 
	 * @author mak1g11
	 * 
	 */
	public class Keys implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
			// not allowing user to write k more than 2 digits
			int length = kValue.getText().length();
			if (length == 2) {
				kValue.setText(kValue.getText().substring(0, 1));
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
}
