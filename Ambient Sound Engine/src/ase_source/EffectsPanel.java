package ase_source;

import java.awt.BasicStroke;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.StrokeBorder;

/**
 * Class represents the effects panel which includes a transition sound and 
 * fader buttons to fade between the transition sound and separately loaded
 * soundscapes.
 * 
 * @author Kevin C. Gall
 *
 */

/* The styling in the below is currently only test styling. Needs to be set correctly */
public class EffectsPanel extends JPanel {
	private static final long serialVersionUID = 9006615987451910818L;
	
	private final JLabel panelLabel;
	private final ImageIcon settingsIcon = new ImageIcon("config.gif");
	private final ImageIcon nothingLoaded = new ImageIcon("nothingLoaded.png");
	
	public EffectsPanel(){
		GridBagLayout layout = new GridBagLayout();
		panelLabel = new JLabel("Transition");
		
		layout.columnWidths = new int[]{50, 20, 20, 20, 50, 100};
		layout.columnWeights = new double[]{0.1, 1.0, 0.1, 0.1, 0.1, 0.1};
		
		setLayout(layout);
		
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.anchor = java.awt.GridBagConstraints.WEST;
		gbc1.fill = java.awt.GridBagConstraints.NONE;
		
		add(panelLabel, gbc1);
	}
	
	public static void main(String[] args){
		JFrame testFrame = new JFrame();
		GridBagLayout testLayout = new GridBagLayout();
		testLayout.columnWeights = new double[]{1.0};
		testLayout.rowWeights = new double[]{1.0};
		testFrame.setLayout(testLayout);
		
		EffectsPanel effectsPanel = new EffectsPanel();
		
		testFrame.setMinimumSize(new java.awt.Dimension(500, 600));
		testFrame.setPreferredSize(new java.awt.Dimension(500, 720));
		
		effectsPanel.setMinimumSize(new java.awt.Dimension(414, 100));
		effectsPanel.setPreferredSize(new java.awt.Dimension(450, 70));
		effectsPanel.setBorder(new StrokeBorder(new BasicStroke()));
		
		GridBagConstraints gbc_test = new GridBagConstraints();
		gbc_test.gridx = 0;
		gbc_test.gridy = 0;
		gbc_test.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc_test.anchor = java.awt.GridBagConstraints.WEST;
		
		testFrame.getContentPane().add(effectsPanel, gbc_test);
		testFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		testFrame.setVisible(true);
	}
}
