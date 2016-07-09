package ase_source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Class represents the effects panel which includes a transition sound and 
 * fader buttons to fade between the transition sound and separately loaded
 * soundscapes.
 * 
 * @author Kevin C. Gall
 *
 */

/*TODO Update buttons for all instances when a soundscape begins, ends, or preps for playing:
	* double-clicking a soundscape to send it to Console 1
	* Manually play/pause master (main play/pause button for soundscape)
*/
public class EffectsPanel extends JPanel {
	private static final long serialVersionUID = 9006615987451910818L;
	
	private static ImageIcon settingsIcon = new ImageIcon("transitionSettings.jpg");
	private static ImageIcon nothingLoaded = new ImageIcon("nothingLoaded.png");
	private static ImageIcon fadeIn = new ImageIcon("fadeIn.jpg");
	private static ImageIcon fadeOut = new ImageIcon("fadeOut.jpg");
	private static ImageIcon crossfade = new ImageIcon("crossfade.jpg");
	private final JLabel panelLabel;
	private final TransitionButton configButton;
	private final JTextField transSoundInput;
	private final JLabel presetALabel;
	private final JLabel transitionLabel;
	private final JLabel presetBLabel;
	private final TransitionButton presetAButton;
	private final TransitionButton transitionButton;
	private final TransitionButton presetBButton;
	private final TransitionButton crossfadeButton;
	private final JLabel crossfadeLabel;
	
//Static constants of the states the transition buttons can be in
	public static final int NOCHANGE = -1;
	public static final int NONE = 0;
	public static final int FADEIN = 1;
	public static final int FADEOUT = 2;
	public static final int CROSSPRESETS = 3;
	public static final int CROSSPRESETTOTRANS = 4;
	public static final int CROSSTRANSTOPRESET = 5;
	public static final int FADEINTRANS = 6;
	public static final int FADEOUTTRANS = 7;
	
//State variables, exposed through the settings menu
	private int delay = 200;
	private int fadeTime = 5000;
	
	private final SoundControlPanel panel1;
	private final SoundControlPanel panel2;
	
	public EffectsPanel(SoundControlPanel panel1, SoundControlPanel panel2){
		this.panel1 = panel1;
		this.panel2 = panel1;
		
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e){
				resizeButtons();
			}
		});
		Insets margin = new Insets(0,0,0,0);
		
		GridBagLayout layout = new GridBagLayout();
		
		layout.columnWidths = new int[]{20, 150, 65, 40, 40, 65};
		layout.columnWeights = new double[]{0.1, 1.0, 0.0, 0.0, 0.0, 0.0};
		layout.rowHeights = new int[]{20, 20, 20};
		
		setLayout(layout);

		panelLabel = new JLabel("Transition");
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridwidth = 2;
		gbc1.gridy = 0;
		gbc1.anchor = java.awt.GridBagConstraints.WEST;
		gbc1.fill = java.awt.GridBagConstraints.NONE;
		
		add(panelLabel, gbc1);

		configButton = new TransitionButton(settingsIcon);
		configButton.setMargin(margin);
		configButton.setToolTipText("Transition Settings");
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.anchor = java.awt.GridBagConstraints.CENTER;
		gbc2.fill = java.awt.GridBagConstraints.NONE;
		configButton.setMinimumSize(new Dimension(15, 15));
		
		add(configButton, gbc2);

		transSoundInput = new JTextField();
		transSoundInput.setEnabled(false); //disable the field by default
		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 1;
		gbc3.gridy = 1;
		gbc3.anchor = java.awt.GridBagConstraints.WEST;
		gbc3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		
		add(transSoundInput, gbc3);
		
		presetALabel = new JLabel("Preset A");
		GridBagConstraints gbc4 = new GridBagConstraints();
		gbc4.gridx = 2;
		gbc4.gridy = 0;
		gbc4.anchor = java.awt.GridBagConstraints.EAST;
		gbc4.fill = java.awt.GridBagConstraints.NONE;
		
		add(presetALabel, gbc4);
		
		transitionLabel = new JLabel("Transition");
		GridBagConstraints gbc5 = new GridBagConstraints();
		gbc5.gridx = 2;
		gbc5.gridy = 1;
		gbc5.anchor = java.awt.GridBagConstraints.EAST;
		gbc5.fill = java.awt.GridBagConstraints.NONE;
		
		add(transitionLabel, gbc5);
		
		presetBLabel = new JLabel("Preset B");
		GridBagConstraints gbc6 = new GridBagConstraints();
		gbc6.gridx = 2;
		gbc6.gridy = 2;
		gbc6.anchor = java.awt.GridBagConstraints.EAST;
		gbc6.fill = java.awt.GridBagConstraints.NONE;
		
		add(presetBLabel, gbc6);
		
		presetAButton = new TransitionButton(nothingLoaded, panel1);
		presetAButton.setMargin(margin);
		GridBagConstraints gbc7 = new GridBagConstraints();
		gbc7.gridx = 3;
		gbc7.gridy = 0;
		gbc7.anchor = java.awt.GridBagConstraints.CENTER;
		gbc7.fill = java.awt.GridBagConstraints.NONE;
		
		add(presetAButton, gbc7);
		
		transitionButton = new TransitionButton(nothingLoaded);
		transitionButton.setMargin(margin);
		GridBagConstraints gbc8 = new GridBagConstraints();
		gbc8.gridx = 3;
		gbc8.gridy = 1;
		gbc8.anchor = java.awt.GridBagConstraints.CENTER;
		gbc8.fill = java.awt.GridBagConstraints.NONE;
		
		add(transitionButton, gbc8);
		
		presetBButton = new TransitionButton(nothingLoaded, panel2);
		presetBButton.setMargin(margin);
		GridBagConstraints gbc9 = new GridBagConstraints();
		gbc9.gridx = 3;
		gbc9.gridy = 2;
		gbc9.anchor = java.awt.GridBagConstraints.CENTER;
		gbc9.fill = java.awt.GridBagConstraints.NONE;
		
		add(presetBButton, gbc9);
		
		crossfadeButton = new TransitionButton(nothingLoaded);
		crossfadeButton.setMargin(margin);
		GridBagConstraints gbc10 = new GridBagConstraints();
		gbc10.gridx = 4;
		gbc10.gridy = 1;
		gbc10.anchor = java.awt.GridBagConstraints.CENTER;
		gbc10.fill = java.awt.GridBagConstraints.NONE;
		
		add(crossfadeButton, gbc10);
		
		crossfadeLabel = new JLabel("Crossfade");
		GridBagConstraints gbc11 = new GridBagConstraints();
		gbc11.gridx = 5;
		gbc11.gridy = 1;
		gbc11.anchor = java.awt.GridBagConstraints.WEST;
		gbc11.fill = java.awt.GridBagConstraints.NONE;
		
		add(crossfadeLabel, gbc11);
		
		//Set Action Listeners for buttons
		presetAButton.addActionListener(new FadeButtonActionListener(presetAButton));
		presetBButton.addActionListener(new FadeButtonActionListener(presetBButton));
		crossfadeButton.addActionListener(new FadeButtonActionListener(crossfadeButton));
	}
	
	/**
	 * Resets the icons on each of the transition buttons and sets them up for calling transitions.
	 * You can use the static constants defined on this class, which are named for the different buttons.<br/>
	 * @param presetA 
	 * @param presetB
	 * @param transition
	 * @param crossfade
	 * @throws IllegalArgumentException if you try to pass an option to one of the buttons that it should never be. (For instance, if you passed 3 (CROSSPRESETS) as presetA)
	 */
	public void setTransitionButtonStates(int presetA, int presetB){
		setTransitionButtonState(presetAButton, presetA);
		setTransitionButtonState(presetBButton, presetB);
		
		presetA = presetAButton.getState();
		presetB = presetBButton.getState();
		
		boolean shouldCrossfade = presetA != presetB && presetA != NONE && presetB != NONE;
		int crossfadeState = shouldCrossfade ? CROSSPRESETS : NONE;
		
		setTransitionButtonState(crossfadeButton, crossfadeState);
	}
	
	private void setTransitionButtonState(TransitionButton b, int i){
		ImageIcon icon = getStateIcon(i);
		if (i > -1){
			b.setState(i);
			b.setIcon(icon);
		}
	}
	private ImageIcon getStateIcon(int i){
		switch(i){
			case 0:
				return nothingLoaded;
			case 1:
				return fadeIn;
			case 2:
				return fadeOut;
			case 3:
				return crossfade;
			//more cases here for the rest of the icons
			default:
				return null;
		}
	}
	
	private void resizeButtons(){
		int width = getWidth();
		int height = getHeight();
		int size;
		
		if(EnvVariables.debug){
			System.out.println("Effects Panel size:"+width+"x"+height);
		}
		
	/*
	 * 3/25/16 Removed sizes larger than 27. It looks shitty when it's any bigger...
	 * K.Gall
	 */
		if(width < 600) {
			size = 20;
		} else {
			size = 27;
		}
		
		setButtonSize(configButton,size);
		setButtonSize(presetAButton,size);
		setButtonSize(transitionButton,size);
		setButtonSize(presetBButton,size);
		setButtonSize(crossfadeButton,size);
	}
	
	private void setButtonSize(TransitionButton button, int size) {
		Dimension dimension = new Dimension(size,size);
		ImageIcon icon = getStateIcon(button.getState());
		Image img = icon.getImage();
		Image scaledImg = img.getScaledInstance(size, size, Image.SCALE_DEFAULT);
		button.setPreferredSize(dimension);
		button.setMinimumSize(dimension);
		button.setMaximumSize(dimension);
		button.setIcon(new ImageIcon(scaledImg));
	}
	
	public static void main(String[] args){
	//Whole below is invalid because of the need for the SoundControlPanels
		
		JFrame testFrame = new JFrame();
		GridBagLayout testLayout = new GridBagLayout();
		testLayout.columnWeights = new double[]{1.0};
		testLayout.rowWeights = new double[]{1.0};
		testFrame.setLayout(testLayout);
		
		//EffectsPanel effectsPanel = new EffectsPanel();
		
		testFrame.setMinimumSize(new java.awt.Dimension(500, 600));
		testFrame.setPreferredSize(new java.awt.Dimension(500, 720));
		
		//effectsPanel.setMinimumSize(new java.awt.Dimension(414, 100));
		//effectsPanel.setPreferredSize(new java.awt.Dimension(450, 70));
		//effectsPanel.setBorder(new StrokeBorder(new BasicStroke()));
		
		GridBagConstraints gbc_test = new GridBagConstraints();
		gbc_test.gridx = 0;
		gbc_test.gridy = 0;
		gbc_test.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc_test.anchor = java.awt.GridBagConstraints.WEST;
		
		//testFrame.getContentPane().add(effectsPanel, gbc_test);
		testFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		testFrame.setVisible(true);
	}
	
	/**
	 * Basic Decorator. Extends JButton so that it can retain all its features, but also exposes a way
	 * to access the ImageIcon passed to the button.
	 * 
	 * Also stores state for button (REALLY BAD!!!)
	 * This is a temporary solution to get the fading functionality running
	 * 
	 * @author Kevin
	 */
	/*
	 * TODO Move state to a centralized controller object
	 * TODO Refactor to make the "nothingloaded" icon a 'disabledIcon' so that we can set disabled
	 */
	private class TransitionButton extends JButton {
		private static final long serialVersionUID = 2802097572198817151L;
		private int state = EffectsPanel.NONE;
		public final SoundControlPanel scp;
		
		public TransitionButton(ImageIcon icon, SoundControlPanel scp){
			super(icon);
			this.scp = scp;
		}
		public TransitionButton(ImageIcon icon){
			this(icon, null);
		}

		public int getState(){
			return state;
		}
		public void setState(int newState){
			if (state < 0 || state > 7){
				throw new IllegalArgumentException();
			}
			state = newState;
		}
	}
	
	/**
	 * Action listener for A and B buttons. Reads state and performs action based on that
	 * 
	 * @author Kevin
	 */
	private class FadeButtonActionListener implements ActionListener {
		private final TransitionButton t;
		
		public FadeButtonActionListener(TransitionButton t){
			this.t = t;
		}
		
		@Override
		public void actionPerformed(ActionEvent e){
			if (t.scp != null){
				switch (t.getState()){
					case FADEIN:
						t.scp.chief.masterVolumeFadeIn(t.scp, fadeTime);
						setTransitionButtonState(t, FADEOUT);
						break;
					case FADEOUT:
						t.scp.chief.masterVolumeFadeOut(t.scp, fadeTime);
						setTransitionButtonState(t, FADEIN);
						break;
				}
				if (presetAButton.state != presetBButton.state && presetAButton.state > 0 && presetBButton.state > 0){
					setTransitionButtonState(crossfadeButton, CROSSPRESETS);
				} else {
					setTransitionButtonState(crossfadeButton, NONE);
				}
			} else {
				switch(t.getState()){
					case CROSSPRESETS:
						TransitionButton fadeOut = presetAButton.getState() == FADEOUT ? presetAButton : presetBButton;
						TransitionButton fadeIn = presetBButton.getState() == FADEIN ? presetBButton : presetAButton;
						
						if (fadeOut == fadeIn){
							throw new Error("Soundscapes should not be crossfaded: states incorrect");
						}
						fadeOut.scp.chief.masterVolumeFadeOut(fadeOut.scp, fadeTime);
						setTransitionButtonState(fadeOut, FADEIN);
						fadeIn.scp.chief.masterVolumeFadeIn(fadeIn.scp, fadeTime);
						setTransitionButtonState(fadeIn, FADEOUT);
						break;
				}
			}
		}
	}
	
	/**
	 * Action Listener for Crossfade button
	 */
}
