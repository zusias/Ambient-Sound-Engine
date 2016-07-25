package ase_source;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import java.awt.GridBagLayout;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

/**
 * Row Control is the GUI element providing control of sounds or "master"
 * soundscape settings. These settings include play/pause, volume, play mode,
 * etc. They are contained inside the sound control panel.
 * 
 * <br><br>History
 * <br>10/Oct/2015 - CKidwell - Moved the random play frame from the 
 * Sound Control Panel class to this one. Fixed a bug where the
 * random play frame was no longer showing on right click of playmode.
 * <br>11/Nov/2015 - CKidwell - A little cleanup, changed the random play frame
 * so that setVisible is called by the parent invoking it rather than being
 * called during construction.
 * 
 * @author CKidwell
 */
class RowControlSetX extends JPanel {
	private static final long serialVersionUID = 8363540062163152203L;
	private final SoundControlPanel soundControlPanel;
	JButton previewButton;
	public JButton playPauseButton;
	public JButton playModeButton;
	private JProgressBar volumeBar;
	private JLabel soundName;
	boolean isSelected;
	private int volume;
	public int volumeBleed = 0;
	private int submasterBaseVolume = 0;
	public int buttonSize;
	private String name;
	private boolean consolePlaybackEnabled = false;
	int row;
	private boolean invalid;

	private void initComponents() {
		playModeButton = new JButton();

		setFocusable(true);
		setBorder(new EtchedBorder());
		Dimension dimension = new Dimension(250, 35);
		this.soundControlPanel.setItemSize(this, dimension);

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				formMouseClicked(evt);
			}
		});

		addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				rowKeyPressed(evt);
			}
		});
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{25, 165, 25, 0};
		gridBagLayout.rowHeights = new int[]{33};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0};
		setLayout(gridBagLayout);
		buttonSize = 25;
		previewButton = new JButton();
		previewButton.setMaximumSize(new Dimension(25, 25));
		previewButton.setMinimumSize(new Dimension(25, 25));
		previewButton.setPreferredSize(new Dimension(25, 25));
		
		previewButton.setFocusable(false);
		previewButton.setIcon(SoundControlPanel.PREVIEWONICON);
		previewButton.setIconTextGap(0);
		previewButton.setMargin(new Insets(0, 0, 0, 0));
					
		previewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				previewButtonActionPerformed(evt);
			}
		});
		GridBagConstraints gbc_previewButton = new GridBagConstraints();
		gbc_previewButton.insets = new Insets(0, 5, 0, 5);
		gbc_previewButton.gridx = 0;
		gbc_previewButton.gridy = 0;
		add(previewButton, gbc_previewButton);
		
		soundName = new JLabel();
					
		GridBagConstraints gbc_soundName = new GridBagConstraints();
		gbc_soundName.anchor = GridBagConstraints.NORTH;
		gbc_soundName.fill = GridBagConstraints.BOTH;
		gbc_soundName.insets = new Insets(0, 5, 12, 5);
		gbc_soundName.gridx = 1;
		gbc_soundName.gridy = 0;
		add(soundName, gbc_soundName);
		volumeBar = new JProgressBar();
		volumeBar.setMaximumSize(new Dimension(32767, 10));
		volumeBar.setMinimumSize(new Dimension(10, 10));
		volumeBar.setPreferredSize(new Dimension(146, 10));
		
		volumeBar.setForeground(new Color(0, 51, 255));
		volumeBar.setValue(0);
		GridBagConstraints gbc_volumeBar = new GridBagConstraints();
		gbc_volumeBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_volumeBar.weightx = 1.0;
		gbc_volumeBar.anchor = GridBagConstraints.SOUTH;
		gbc_volumeBar.insets = new Insets(0, 0, 3, 5);
		gbc_volumeBar.gridx = 1;
		gbc_volumeBar.gridy = 0;
		add(volumeBar, gbc_volumeBar);
		playPauseButton = new JButton();
		playPauseButton.setFocusable(false);
		playPauseButton.setPreferredSize(new Dimension(25, 25));
		playPauseButton.setMaximumSize(new Dimension(25, 25));
		playPauseButton.setMinimumSize(new Dimension(25, 25));
		
		playPauseButton.setIcon(SoundControlPanel.SPEAKERONICON);
		playPauseButton.setIconTextGap(0);
		playPauseButton.setMargin(new Insets(0, 0, 0, 0));
					
		playPauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				playPauseButtonActionPerformed(evt);
			}
		});
								
		GridBagConstraints gbc_playPauseButton = new GridBagConstraints();
		gbc_playPauseButton.gridx = 2;
		gbc_playPauseButton.gridy = 0;
		gbc_playPauseButton.insets = new Insets(0, 0, 0, 5);
		add(playPauseButton, gbc_playPauseButton);

		setBackground(Color.white);

		playModeButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				playModeButtonActionPerformed(evt);
			}
		});

		playModeButton.setFocusable(false);
		playModeButton.setIcon(SoundControlPanel.LOOPPLAYICON);
		playModeButton.setPreferredSize(new Dimension(25, 25));
		playModeButton.setMaximumSize(new Dimension(25, 25));
		playModeButton.setMinimumSize(new Dimension(25, 25));
		
		GridBagConstraints gbc_playModeButton = new GridBagConstraints();
		gbc_playModeButton.gridx = 3;
		gbc_playModeButton.gridy = 0;
		gbc_playModeButton.insets = new Insets(0, 0, 0, 5);

		if (row != -1) {
			gridBagLayout.columnWidths = new int[]{25, 165, 25, 25};
			add(playModeButton, gbc_playModeButton);
		}
	}

	private void formMouseClicked(MouseEvent evt) {
		if (this.soundControlPanel.soundscape == null) {
			String message = "A soundscape is not loaded. You cannot modify a soundscape until you load or create one.";
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, message);
			return;
		}
		requestFocusInWindow();
		updateSelectedRowsDisplay(evt);
		// repaintButtons(chief.evaluateListenerResult(row,rowCount,ROWSELECTTOGGLE,soundscape));

	}

	private void rowKeyPressed(java.awt.event.KeyEvent evt) {
		if ((evt.getKeyChar() == KeyEvent.VK_ESCAPE)
				|| (evt.getKeyChar() == KeyEvent.VK_DELETE)) {
			this.soundControlPanel.deleteRow(row);
			this.soundControlPanel.repaintButtons(this.soundControlPanel.chief.evaluateListenerResult(row, this.soundControlPanel.rowCount,
					SoundControlPanel.DELETEBUTTON, this.soundControlPanel.soundscape, this.soundControlPanel.stateMap));
		} else if (evt.getKeyChar() == 'e') {
			int value = this.soundControlPanel.volumeControl.getValue();
			if (value == 100) {
				//Do nothing, already at max volume.
			} else if (value > 90) {
				this.soundControlPanel.volumeControl.setValue(100);
			} else {
				this.soundControlPanel.volumeControl.setValue(value + 10);
			}
		} else if (evt.getKeyChar() == 'd') {
			int value = this.soundControlPanel.volumeControl.getValue();
			if (value == 0) {
				//Do nothing, already at max volume.
			} else if (value < 10) {
				this.soundControlPanel.volumeControl.setValue(0);
			} else {
				this.soundControlPanel.volumeControl.setValue(value - 10);
			}
		}
	}

	private void previewButtonActionPerformed(ActionEvent evt) {
		if ((row == -1) && (this.soundControlPanel.soundscape == null)) {
			String message = "A soundscape is not loaded. You cannot preview a soundscape until you load one.";
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, message);
			return;
		}
		this.soundControlPanel.repaintButtons(this.soundControlPanel.chief.evaluateListenerResult(row, this.soundControlPanel.rowCount,
				SoundControlPanel.PREVIEWBUTTON, this.soundControlPanel.soundscape, this.soundControlPanel.stateMap));
	}

	private void playPauseButtonActionPerformed(ActionEvent evt) {
		if (this.soundControlPanel.soundscape == null) {
			String message = "A soundscape is not loaded. You cannot play a soundscape until you load or create one.";
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, message);
			return;
		}
		this.soundControlPanel.soundscapeChanged = true;
		this.soundControlPanel.repaintButtons(this.soundControlPanel.chief.evaluateListenerResult(row, this.soundControlPanel.rowCount,
				SoundControlPanel.PLAYPAUSEBUTTON, this.soundControlPanel.soundscape, this.soundControlPanel.stateMap));
	}

	public void playModeButtonActionPerformed(MouseEvent evt) {
		int temp;
		this.soundControlPanel.soundscapeChanged = true;
		if (evt.getButton() == 1) {// if the button is left clicked, rotate
			// through options loop->single->random
			temp = this.soundControlPanel.soundscape.getSound(row).getPlaybackType();
			if (temp == SoundControlPanel.LOOPPLAY) {// 0
				this.soundControlPanel.chief.singleSoundModeChange(this.soundControlPanel.soundscape, row, SoundControlPanel.PLAYONCE);
				Image img = SoundControlPanel.PLAYONCEICON.getImage();
				Image scaledImg = img.getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
				playModeButton.setIcon(new ImageIcon(scaledImg));
			} else if (temp == SoundControlPanel.PLAYONCE) {// 1
				this.soundControlPanel.chief.singleSoundModeChange(this.soundControlPanel.soundscape, row, SoundControlPanel.RANDOMPLAY);
				Image img = SoundControlPanel.RANDOMPLAYICON.getImage();
				Image scaledImg = img.getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
				playModeButton.setIcon(new ImageIcon(scaledImg));
			} else {
				this.soundControlPanel.chief.singleSoundModeChange(this.soundControlPanel.soundscape, row, SoundControlPanel.LOOPPLAY);
				Image img = SoundControlPanel.LOOPPLAYICON.getImage();
				Image scaledImg = img.getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
				playModeButton.setIcon(new ImageIcon(scaledImg));
			}
		} else {// switch to random and bring up the window
			this.soundControlPanel.chief.singleSoundModeChange(this.soundControlPanel.soundscape, row, SoundControlPanel.RANDOMPLAY);
			Image img = SoundControlPanel.RANDOMPLAYICON.getImage();
			Image scaledImg = img.getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
			playModeButton.setIcon(new ImageIcon(scaledImg));
			RandomPlayFrame rpFrame = new RandomPlayFrame(row);
			rpFrame.setVisible(true);
		}
	}
	
	

	public void updateSelectedRowsDisplay(MouseEvent evt) {
		int position;
		if (evt.getButton() == 1) {
			if (row > -1) { 
				// this clears all selections, then makes this
				// row the only one highlighted
				selectSingleRow();
				return;
			}
		}
		if (row == -1) { // master control clicked
			selectMasterRow();
		} else {
			if (this.soundControlPanel.masterControl.isSelected == true) {
				this.soundControlPanel.masterControl.isSelected = false;
				this.soundControlPanel.masterControl.setBackground(Color.white);
				this.soundControlPanel.volumeControl.setEnabled(false);
			}

			switch (this.soundControlPanel.rowsSelectedCount) {
			case 0:
				this.soundControlPanel.rowsSelectedCount = 1;
				this.soundControlPanel.selectedCells.add(this);
				this.setBackground(Color.yellow);
				this.soundControlPanel.volumeSliderSystemSetFlag = true;
				this.soundControlPanel.volumeControl.setValue(this.getVolume());
				this.soundControlPanel.volumeControl.setEnabled(true);
				break;
			case 1:
				if (isSelected == true) {
					this.soundControlPanel.rowsSelectedCount = 0;
					this.soundControlPanel.selectedCells.removeAllElements();
					this.setBackground(Color.white);
					this.soundControlPanel.volumeControl.setEnabled(false);
				} else {
					this.soundControlPanel.rowsSelectedCount = 2;
					this.soundControlPanel.selectedCells.add(this);
					this.soundControlPanel.selectedCells.elementAt(0).setBackground(Color.green);
					this.soundControlPanel.selectedCells.elementAt(1).setBackground(Color.green);
					this.soundControlPanel.volumeSliderSystemSetFlag = true;
					this.soundControlPanel.volumeControl.setValue(50);

					this.soundControlPanel.volumeControl.setEnabled(true);

					this.soundControlPanel.clearSubMasterBleeds(this.soundControlPanel.selectedCells);
				}
				break;
			case 2:
				if (isSelected == true) {
					this.soundControlPanel.rowsSelectedCount = 1;
					this.setBackground(Color.white);
					position = this.soundControlPanel.selectedCells.indexOf(this);
					this.soundControlPanel.selectedCells.removeElementAt(position);
					this.soundControlPanel.selectedCells.elementAt(0).setBackground(Color.yellow);
					this.soundControlPanel.volumeControl.setValue(this.soundControlPanel.selectedCells.elementAt(0)
							.getVolume());
				} else {
					this.soundControlPanel.rowsSelectedCount = 3;
					this.setBackground(Color.green);
					this.soundControlPanel.selectedCells.add(this);
					this.soundControlPanel.volumeSliderSystemSetFlag = true;
					this.soundControlPanel.volumeControl.setValue(50);

					this.soundControlPanel.clearSubMasterBleeds(this.soundControlPanel.selectedCells);
				}
				break;
			default:
				if (isSelected == true) {
					this.soundControlPanel.rowsSelectedCount--;
					position = this.soundControlPanel.selectedCells.indexOf(this);
					this.soundControlPanel.selectedCells.removeElementAt(position);
					this.setBackground(Color.white);

				} else {
					this.setBackground(Color.green);
					this.soundControlPanel.selectedCells.add(this);
					this.soundControlPanel.rowsSelectedCount++;
					this.soundControlPanel.volumeSliderSystemSetFlag = true;
					this.soundControlPanel.volumeControl.setValue(50);

					this.soundControlPanel.clearSubMasterBleeds(this.soundControlPanel.selectedCells);

				}
				break;
			}
			isSelected = !isSelected;
		}

	}

	public void selectMasterRow() {
		if (this.soundControlPanel.masterControl.isSelected == false) {
			this.soundControlPanel.volumeControl.setEnabled(true);
			for (int count = 0; count < this.soundControlPanel.selectedCells.size(); count++) {
				this.soundControlPanel.selectedCells.elementAt(count).setBackground(
						Color.white);
				this.soundControlPanel.selectedCells.elementAt(count).isSelected = false;
			}
			this.soundControlPanel.selectedCells.removeAllElements();
			this.soundControlPanel.rowsSelectedCount = 0;
			this.soundControlPanel.masterControl.setBackground(Color.orange);
			this.soundControlPanel.masterControl.isSelected = true;
			this.soundControlPanel.volumeSliderSystemSetFlag = true;
			this.soundControlPanel.volumeControl.setValue(this.soundControlPanel.masterControl.getVolume());
		} else {
			this.soundControlPanel.masterControl.setBackground(Color.white);
			this.soundControlPanel.masterControl.isSelected = false;
			this.soundControlPanel.volumeControl.setEnabled(false);
		}
	}

	private void selectSingleRow() {
		for (int count = 0; count < this.soundControlPanel.selectedCells.size(); count++) {
			this.soundControlPanel.selectedCells.elementAt(count).setBackground(
					Color.white);
			this.soundControlPanel.selectedCells.elementAt(count).isSelected = false;
		}
		this.soundControlPanel.selectedCells.removeAllElements();
		this.soundControlPanel.selectedCells.add(this);
		isSelected = true;
		this.soundControlPanel.rowsSelectedCount = 1;
		setBackground(Color.yellow);
		this.soundControlPanel.masterControl.isSelected = false;
		this.soundControlPanel.masterControl.setBackground(Color.white);
		this.soundControlPanel.volumeSliderSystemSetFlag = true;
		this.soundControlPanel.volumeControl.setValue(this.soundControlPanel.selectedCells.elementAt(0)
				.getVolume());
		this.soundControlPanel.volumeControl.setEnabled(true);
	}

	public void updateSelectedRowsDisplay(int clickCount) {
		int position;
		// System.out.println("Before Case =" + rowsSelectedCount+ " Vector
		// size="+selectedCells.size()+" Selected="+isSelected);
		switch (clickCount) {
		case 2:
			if (row > -1) { // this clears all selections, then makes this
				// row the only one highlighted
				for (int count = 0; count < this.soundControlPanel.selectedCells.size(); count++) {
					this.soundControlPanel.selectedCells.elementAt(count).setBackground(
							Color.white);
					this.soundControlPanel.selectedCells.elementAt(count).isSelected = false;
				}
				this.soundControlPanel.selectedCells.removeAllElements();
				this.soundControlPanel.selectedCells.add(this);
				isSelected = true;
				this.soundControlPanel.rowsSelectedCount = 1;
				setBackground(Color.yellow);
				this.soundControlPanel.masterControl.isSelected = false;
				this.soundControlPanel.masterControl.setBackground(Color.white);
				this.soundControlPanel.volumeSliderSystemSetFlag = true;
				this.soundControlPanel.volumeControl.setValue(this.soundControlPanel.selectedCells.elementAt(0).getVolume());
				return;
			}

		}
		if (row == -1) { // master control clicked
			selectMasterRow();
		} else {
			if (this.soundControlPanel.masterControl.isSelected == true) {
				this.soundControlPanel.masterControl.isSelected = false;
				this.soundControlPanel.masterControl.setBackground(Color.white);
				this.soundControlPanel.volumeControl.setEnabled(false);
			}

			switch (this.soundControlPanel.rowsSelectedCount) {
			case 0:
				this.soundControlPanel.rowsSelectedCount = 1;
				this.soundControlPanel.selectedCells.add(this);
				this.setBackground(Color.yellow);
				this.soundControlPanel.volumeSliderSystemSetFlag = true;
				this.soundControlPanel.volumeControl.setValue(this.getVolume());
				this.soundControlPanel.volumeControl.setEnabled(true);
				break;
			case 1:
				if (isSelected == true) {
					this.soundControlPanel.rowsSelectedCount = 0;
					this.soundControlPanel.selectedCells.removeAllElements();
					this.setBackground(Color.white);
					this.soundControlPanel.volumeControl.setEnabled(false);
				} else {
					this.soundControlPanel.rowsSelectedCount = 2;
					this.soundControlPanel.selectedCells.add(this);
					this.soundControlPanel.selectedCells.elementAt(0).setBackground(Color.green);
					this.soundControlPanel.selectedCells.elementAt(1).setBackground(Color.green);
					this.soundControlPanel.volumeSliderSystemSetFlag = true;
					this.soundControlPanel.volumeControl.setValue(50);

					this.soundControlPanel.volumeControl.setEnabled(true);

					this.soundControlPanel.clearSubMasterBleeds(this.soundControlPanel.selectedCells);
				}
				break;
			case 2:
				if (isSelected == true) {
					this.soundControlPanel.rowsSelectedCount = 1;
					this.setBackground(Color.white);
					position = this.soundControlPanel.selectedCells.indexOf(this);
					this.soundControlPanel.selectedCells.removeElementAt(position);
					this.soundControlPanel.selectedCells.elementAt(0).setBackground(Color.yellow);
					this.soundControlPanel.volumeControl.setValue(this.soundControlPanel.selectedCells.elementAt(0)
							.getVolume());
				} else {
					this.soundControlPanel.rowsSelectedCount = 3;
					this.setBackground(Color.green);
					this.soundControlPanel.selectedCells.add(this);
					this.soundControlPanel.volumeSliderSystemSetFlag = true;
					this.soundControlPanel.volumeControl.setValue(50);

					this.soundControlPanel.clearSubMasterBleeds(this.soundControlPanel.selectedCells);
				}
				break;
			default:
				if (isSelected == true) {
					this.soundControlPanel.rowsSelectedCount--;
					position = this.soundControlPanel.selectedCells.indexOf(this);
					this.soundControlPanel.selectedCells.removeElementAt(position);
					this.setBackground(Color.white);

				} else {
					this.setBackground(Color.green);
					this.soundControlPanel.selectedCells.add(this);
					this.soundControlPanel.rowsSelectedCount++;
					this.soundControlPanel.volumeSliderSystemSetFlag = true;
					this.soundControlPanel.volumeControl.setValue(50);

					this.soundControlPanel.clearSubMasterBleeds(this.soundControlPanel.selectedCells);

				}
				break;
			}
			isSelected = !isSelected;
		}
	}

	public RowControlSetX(SoundControlPanel soundControlPanel, int rowID) {
		this.soundControlPanel = soundControlPanel;
		row = rowID;
		initComponents();
	}
	
	public void setSizes(){
		int scpHeight = soundControlPanel.getHeight();
		int fontSize;
		if(scpHeight < 300){
			fontSize = 13;
		} else if (scpHeight < 325) {
			fontSize = 17;
		} else if (scpHeight < 350) {
			fontSize = 19;
		} else {
			fontSize = 23;
		}
		
		setFontSize(soundName,fontSize);
		
		int scpWidth = soundControlPanel.getWidth();
		int buttonSize;
		if(scpWidth < 600) {
			buttonSize = 20;
		} else if (scpWidth < 700) {
			buttonSize = 27;
		} else if (scpWidth < 800) {
			buttonSize = 34;
		} else {
			buttonSize = 41;
		}
		
		this.buttonSize = buttonSize;
		if(this.row == -1) {
			if(soundControlPanel.stateMap[SoundControlPanel.STATEMAPROWCOUNT][0]){
				Image img = SoundControlPanel.PREVIEWONICON.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_DEFAULT);
				setButtonSize(previewButton, buttonSize, new ImageIcon(img));
			} else {
				Image img = SoundControlPanel.PREVIEWOFFICON.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_DEFAULT);
				setButtonSize(previewButton, buttonSize, new ImageIcon(img));
			}
			
			if(soundControlPanel.stateMap[SoundControlPanel.STATEMAPROWCOUNT][1]){
				Image img = SoundControlPanel.SPEAKERONICON.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_DEFAULT);
				setButtonSize(playPauseButton, buttonSize, new ImageIcon(img));
			} else {
				Image img = SoundControlPanel.SPEAKEROFFICON.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_DEFAULT);
				setButtonSize(playPauseButton, buttonSize, new ImageIcon(img));
			}
		} else {
			if(soundControlPanel.stateMap[this.row][0]){
				Image img = SoundControlPanel.PREVIEWONICON.getImage().getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
				setButtonSize(previewButton, buttonSize, new ImageIcon(img));
			} else {
				Image img = SoundControlPanel.PREVIEWOFFICON.getImage().getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
				setButtonSize(previewButton, buttonSize, new ImageIcon(img));
			}
			
			if(soundControlPanel.stateMap[this.row][1]){
				Image img = SoundControlPanel.SPEAKERONICON.getImage().getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
				setButtonSize(playPauseButton, buttonSize, new ImageIcon(img));
			} else {
				Image img = SoundControlPanel.SPEAKEROFFICON.getImage().getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
				setButtonSize(playPauseButton, buttonSize, new ImageIcon(img));
			}
			
			int temp = this.soundControlPanel.soundscape.getSound(row).getPlaybackType();
			if (temp == SoundControlPanel.LOOPPLAY) {// 0
				Image img = SoundControlPanel.LOOPPLAYICON.getImage();
				Image scaledImg = img.getScaledInstance(buttonSize, buttonSize, Image.SCALE_DEFAULT);
				setButtonSize(playModeButton, buttonSize, new ImageIcon(scaledImg));
			} else if (temp == SoundControlPanel.PLAYONCE) {// 1
				Image img = SoundControlPanel.PLAYONCEICON.getImage();
				Image scaledImg = img.getScaledInstance(buttonSize, buttonSize, Image.SCALE_DEFAULT);
				setButtonSize(playModeButton, buttonSize, new ImageIcon(scaledImg));
			} else {
				Image img = SoundControlPanel.RANDOMPLAYICON.getImage();
				Image scaledImg = img.getScaledInstance(buttonSize, buttonSize, Image.SCALE_DEFAULT);
				setButtonSize(playModeButton, buttonSize, new ImageIcon(scaledImg));
			}
		}
		
		
	}
	
	public void setButtonSize(JButton button, int size) {
		button.setMinimumSize(new Dimension(size,size));
		button.setPreferredSize(new Dimension(size,size));
		button.setMaximumSize(new Dimension(size,size));
	}
	
	public void setButtonSize(JButton button, int size, ImageIcon icon) {
		Dimension dimension = new Dimension(size,size);
		button.setPreferredSize(dimension);
		button.setMinimumSize(dimension);
		button.setMaximumSize(dimension);
		button.setIcon(icon);
	}
	
	public void setFontSize(JComponent component, int size){
		Font font = component.getFont();
		component.setFont(font.deriveFont((float)size));
	}
//
//	public void setPlaybackType(int type) {
//		if (type == SoundControlPanel.LOOPPLAY) {// 0
//			this.soundControlPanel.chief.singleSoundModeChange(this.soundControlPanel.soundscape, row, SoundControlPanel.LOOPPLAY);
//			playModeButton.setIcon(SoundControlPanel.LOOPPLAYICON);
//		} else if (type == SoundControlPanel.PLAYONCE) {// 1
//			this.soundControlPanel.chief.singleSoundModeChange(this.soundControlPanel.soundscape, row, SoundControlPanel.PLAYONCE);
//			playModeButton.setIcon(SoundControlPanel.PLAYONCEICON);
//		} else {
//			this.soundControlPanel.chief.singleSoundModeChange(this.soundControlPanel.soundscape, row, SoundControlPanel.RANDOMPLAY);
//			playModeButton.setIcon(SoundControlPanel.RANDOMPLAYICON);
//		}
//	}

	public void setPlaybackGraphic(int type) {
		if (type == SoundControlPanel.LOOPPLAY) {// 0
			Image img = SoundControlPanel.LOOPPLAYICON.getImage().getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
			playModeButton.setIcon(new ImageIcon(img));
		} else if (type == SoundControlPanel.PLAYONCE) {// 1
			Image img = SoundControlPanel.PLAYONCEICON.getImage().getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
			playModeButton.setIcon(new ImageIcon(img));
		} else {
			Image img = SoundControlPanel.RANDOMPLAYICON.getImage().getScaledInstance(buttonSize-4, buttonSize-4, Image.SCALE_DEFAULT);
			playModeButton.setIcon(new ImageIcon(img));
		}
	}

	public void SetName(String newName) {
		soundName.setText(newName);
		name = newName;
	}

	public String GetName() {
		return name;
	}

	public void setTitleFont(Font titleFont) {
		soundName.setFont(titleFont);
	}

	public void setVolume(int newVolume) {
		volumeBar.setValue(newVolume);
		volume = newVolume;
	}

	public int getVolume() {
		return volume;
	}

	public void setSubMasterBaseVolume(int newVolume) {
		submasterBaseVolume = newVolume;
	}

	public int getSubMasterBaseVolume() {
		return submasterBaseVolume;
	}

	public void setRowBackground(Color color) {
		setBackground(color);
	}

	public boolean getSelectionStatus() {
		return isSelected;
	}

	public boolean getPlaybackEnabled() {
		if (row == -1)
			return consolePlaybackEnabled;
		else {

			return this.soundControlPanel.soundscape.getSound(row).getPlaybackEnabled();
		}
	}

	public void setPlaybackEnabled(boolean status) {
		if (row == -1)
			consolePlaybackEnabled = status;
		else
			this.soundControlPanel.soundscape.getSound(row).setPlaybackEnabled(status);
	}
	
	/**
	 * Sets the "Invalid" attribute on this sound and updates the Row's display.
	 * A sound is "Invalid" if the file does not exist or can not be parsed as
	 * a sound. The display indicates this by disabling the buttons to play/pause
	 * and changes the sound name to italic & strikethrough.
	 * @param invalid A value of true indicates an invalid file that should be greyed
	 * out and shown with a strikethrough of the name. False would be the default state.
	 */
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
		if(this.invalid) {
			Font font = soundName.getFont();
			font = font.deriveFont(Font.ITALIC);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			font = font.deriveFont(attributes);
			soundName.setFont(font);
			
			playPauseButton.setEnabled(false);
			playModeButton.setEnabled(false);
			previewButton.setEnabled(false);
		} else {
			Font font = soundName.getFont();
			font = font.deriveFont(Font.PLAIN);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.STRIKETHROUGH, null);
			font = font.deriveFont(attributes);
			soundName.setFont(font);
			
			playPauseButton.setEnabled(true);
			playModeButton.setEnabled(true);
			previewButton.setEnabled(true);
		}
	}

	/**
	 * This frame is invoked when right clicking on the play mode button, it
	 * provides a way to configure the settings for random play.
	 * @author CKidwell
	 *
	 */
	class RandomPlayFrame extends JFrame {
		private static final long serialVersionUID = -4676136420042830091L;
		private JLabel minPlayLabel = new JLabel("Minimum Repeats");
		private JLabel maxPlayLabel = new JLabel("Maximum Repeats");
		private JLabel minDelayLabel = new JLabel("Minimum Delay");
		private JLabel maxDelayLabel = new JLabel("Maximum Delay");
		private JButton saveButton = new JButton("Save Changes and Exit");
		private JButton discardButton = new JButton("Discard Changes");
		private JTextField minPlayField = new JTextField(4);
		private JTextField maxPlayField = new JTextField(4);
		private JTextField minDelayField = new JTextField(4);
		private JTextField maxDelayField = new JTextField(4);
		private int row;

		public RandomPlayFrame(int incRow) {
			this.row = incRow;

			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					saveButtonActionPerformed();
				}
			});

			discardButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					discardButtonActionPerformed();
				}
			});

			getContentPane().setLayout(new GridLayout(5, 2, 5, 5));

			getContentPane().add(minPlayLabel);
			getContentPane().add(minDelayLabel);

			getContentPane().add(minPlayField);
			getContentPane().add(minDelayField);
			minPlayField.setText(String.valueOf(soundControlPanel.soundscape.getSound(row)
					.getMinNumLoops()));
			minDelayField.setText(String.valueOf(soundControlPanel.soundscape.getSound(row)
					.getMinRepeatDelay()));

			getContentPane().add(maxPlayLabel);
			getContentPane().add(maxDelayLabel);

			getContentPane().add(maxPlayField);
			getContentPane().add(maxDelayField);
			maxPlayField.setText(String.valueOf(soundControlPanel.soundscape.getSound(row)
					.getMaxNumLoops()));
			maxDelayField.setText(String.valueOf(soundControlPanel.soundscape.getSound(row)
					.getMaxRepeatDelay()));

			getContentPane().add(saveButton);
			getContentPane().add(discardButton);

			this.pack();
		}

		public void saveButtonActionPerformed() {
			int minimumPlayTimes = Integer.parseInt(minPlayField.getText());
			int maximumPlayTimes = Integer.parseInt(maxPlayField.getText());
			int minimumDelay = Integer.parseInt(minDelayField.getText());
			int maximumDelay = Integer.parseInt(maxDelayField.getText());

			soundControlPanel.soundscape.getSound(row).setMinRepeats(minimumPlayTimes);
			soundControlPanel.soundscape.getSound(row).setMaxRepeats(maximumPlayTimes);
			soundControlPanel.soundscape.getSound(row).setMinRepeatDelay(minimumDelay);
			soundControlPanel.soundscape.getSound(row).setMaxRepeatDelay(maximumDelay);

			this.setVisible(false);
			this.dispose();
		}

		public void discardButtonActionPerformed() {
			setVisible(false);
			dispose();
		}

	}
}