/*
 * SoundControlPanel.java
 *
 * Created on April 9, 2006, 11:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ase_source;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Sound Control Panels are the GUI element representing a holding 
 * container for soundscapes.
 * <br><br>History
 * <br>10/Oct/2015 - CKidwell - Moved the random play frame to the row 
 * control class.
 * 
 * @author Lance, CKidwell
 */
public class SoundControlPanel extends JPanel {
	private static final long serialVersionUID = 7680180836676768374L;
	static final int ROWCOUNT = 25;
	static final int ROWHEIGHT = 45;

	static final int PREVIEWBUTTON = 0;
	static final int DELETEBUTTON = 1;
	static final int ROWSELECTTOGGLE = 2;
	static final int PLAYMODEBUTTON = 3;
	static final int PLAYPAUSEBUTTON = 4;

	static final int ROWTOGGLESELECTED = 0;

	static final int LOOPPLAY = 0;
	static final int PLAYONCE = 1;
	static final int RANDOMPLAY = 2;

	static final boolean UNPAUSED = true;
	static final boolean PAUSED = false;

	static final boolean OFF = false;
	static final boolean ON = true;

	static final int STATEMAPROWCOUNT = 25;
	static final int STATEMAPCOLUMNCOUNT = 2;

	static final ImageIcon NEWSOUNDSCAPE = new ImageIcon("newSoundscape.gif");
	static final ImageIcon COPYICON = new ImageIcon("copy.gif");
	static final ImageIcon SAVEICON = new ImageIcon("save.gif");
	static final ImageIcon PREVIEWONICON = new ImageIcon("previewOn.gif");
	static final ImageIcon PREVIEWOFFICON = new ImageIcon("previewOff.gif");
	static final ImageIcon SPEAKERONICON = new ImageIcon("SpeakerOn.gif");
	static final ImageIcon SPEAKEROFFICON = new ImageIcon("SpeakerOff.gif");
	static final ImageIcon LOOPPLAYICON = new ImageIcon("LoopPlay.gif");
	static final ImageIcon PLAYONCEICON = new ImageIcon("PlayOnce.gif");
	static final ImageIcon RANDOMPLAYICON = new ImageIcon("RandomPlay.gif");

	private JScrollPane rowScroller = new JScrollPane();
	private JPanel masterPanel = new JPanel();
	RowControlSetX masterControl;
	private JPanel rowPanel = new JPanel();
	JSlider volumeControl = new JSlider();
	private JButton saveSoundscapeButton = new JButton();
	private JButton newSoundscapeButton = new JButton();
	private JButton copySoundscapeButton = new JButton();

	private RowControlSetX rows[] = new RowControlSetX[ROWCOUNT];
	Vector<RowControlSetX> selectedCells = new Vector<RowControlSetX>();
	int rowsSelectedCount;
	int prevRowsSelected;//Sloppy, but reason for this variable is documented below

	boolean volumeSliderSystemSetFlag = false;
	boolean soundscapeChanged = false;

	boolean[][] stateMap = new boolean[STATEMAPROWCOUNT + 1][STATEMAPCOLUMNCOUNT];

	int rowCount = 0;
	private int panelID = 0;
	OperationsManager chief;
	Soundscape soundscape;

	/** Creates a new instance of SoundControlPanel */
	public SoundControlPanel() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeButtons();
			}
		});
		masterControl = new RowControlSetX(this, -1);

		for (int count = 0; count < ROWCOUNT; count++)
			rows[count] = new RowControlSetX(this, count);

		Dimension masterPanelDimension = new Dimension(300, 40);
		Dimension rpSize = new Dimension(300, 0);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{305, 16, 16, 16};
		gridBagLayout.rowHeights = new int[]{35, 236};
		gridBagLayout.columnWeights = new double[]{0.9, 0.0, 0.05, 0.05};
		gridBagLayout.rowWeights = new double[]{0.0, 2.5};
		setLayout(gridBagLayout);
		copySoundscapeButton.setPreferredSize(new Dimension(16, 16));
		copySoundscapeButton.setMinimumSize(new Dimension(16, 16));
		copySoundscapeButton.setMaximumSize(new Dimension(16, 16));

		copySoundscapeButton.setIcon(COPYICON);
		copySoundscapeButton.setMargin(new Insets(0, 0, 0, 0));
		copySoundscapeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				copySoundscapeButton(evt);
			}
		});
		saveSoundscapeButton.setMinimumSize(new Dimension(16, 16));
		saveSoundscapeButton.setPreferredSize(new Dimension(16, 16));
		saveSoundscapeButton.setMaximumSize(new Dimension(16, 16));

		saveSoundscapeButton.setIcon(SAVEICON);
		saveSoundscapeButton.setMargin(new Insets(0, 0, 0, 0));
		saveSoundscapeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveSoundscapeButton(evt);
			}
		});
		setItemSize(masterPanel, masterPanelDimension);
		GridBagConstraints gbc_masterPanel = new GridBagConstraints();
		gbc_masterPanel.weighty = 0.1;
		gbc_masterPanel.weightx = 0.9;
		gbc_masterPanel.anchor = GridBagConstraints.WEST;
		gbc_masterPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_masterPanel.insets = new Insets(5, 5, 5, 10);
		gbc_masterPanel.gridx = 0;
		gbc_masterPanel.gridy = 0;
		add(masterPanel, gbc_masterPanel);
		masterPanel.setLayout(new GridLayout(0, 1));
		masterPanel.add(masterControl);
		newSoundscapeButton.setMaximumSize(new Dimension(16, 16));
		newSoundscapeButton.setMinimumSize(new Dimension(16, 16));
		newSoundscapeButton.setPreferredSize(new Dimension(16, 16));

		newSoundscapeButton.setIcon(NEWSOUNDSCAPE);
		newSoundscapeButton.setMargin(new Insets(0, 0, 0, 0));
		newSoundscapeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newSoundscapeButton(evt);
			}
		});
		GridBagConstraints gbc_newSoundscapeButton = new GridBagConstraints();
		gbc_newSoundscapeButton.insets = new Insets(5, 0, 5, 5);
		gbc_newSoundscapeButton.gridx = 1;
		gbc_newSoundscapeButton.gridy = 0;
		add(newSoundscapeButton, gbc_newSoundscapeButton);
		GridBagConstraints gbc_saveSoundscapeButton = new GridBagConstraints();
		gbc_saveSoundscapeButton.insets = new Insets(5, 0, 5, 5);
		gbc_saveSoundscapeButton.gridx = 2;
		gbc_saveSoundscapeButton.gridy = 0;
		add(saveSoundscapeButton, gbc_saveSoundscapeButton);
		GridBagConstraints gbc_copySoundscapeButton = new GridBagConstraints();
		gbc_copySoundscapeButton.insets = new Insets(5, 0, 5, 5);
		gbc_copySoundscapeButton.gridx = 3;
		gbc_copySoundscapeButton.gridy = 0;
		add(copySoundscapeButton, gbc_copySoundscapeButton);

		rowPanel.setLayout(new GridLayout(0, 1));
		setItemSize(rowPanel, rpSize);
		
		rowScroller.setViewportView(rowPanel);
		GridBagConstraints gbc_rowScroller = new GridBagConstraints();
		gbc_rowScroller.weightx = 0.9;
		gbc_rowScroller.fill = GridBagConstraints.BOTH;
		gbc_rowScroller.insets = new Insets(0, 5, 5, 10);
		gbc_rowScroller.gridx = 0;
		gbc_rowScroller.gridy = 1;
		add(rowScroller, gbc_rowScroller);
		setPanelName("***EMPTY***");

		setMinimumSize(new Dimension(450, 280));
		setMaximumSize(new Dimension(600, 390));
		setPreferredSize(new Dimension(457, 284));
		volumeControl.setMaximumSize(new Dimension(32767, 50));
		
		volumeControl.setOrientation(JSlider.VERTICAL);
		GridBagConstraints gbc_volumeControl = new GridBagConstraints();
		gbc_volumeControl.gridwidth = 3;
		gbc_volumeControl.fill = GridBagConstraints.VERTICAL;
		gbc_volumeControl.gridx = 1;
		gbc_volumeControl.gridy = 1;
		gbc_volumeControl.weightx = 0;
		add(volumeControl, gbc_volumeControl);
		volumeControl.setEnabled(false);
		
		volumeControl.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				volumeControlStateChanged(evt);
			}
		});

	}
	
	/**
	 * Checks if the current soundscape needs to be saved, (new or changed)
	 * if so, it will create a save dialogue, 
	 * @return True if the process should continue, false otherwise (equivalent of a cancel command)
	 */
	public boolean evaluateAndSave() {
		Object confirm = null;
		if (soundscape != null && (soundscapeChanged || (soundscape.getSoundscapeID() < 0))){
			String[] options = {"Save Changes","Save As","Discard Changes"};
			
			confirm = JOptionPane.showOptionDialog(null, "There are unsaved changes to soundscape " + soundscape.getSSName() + ".	", 
					"", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, null);
			if (confirm.equals(new Integer(-1))) {
				//Cancelled (X button used to close the window)
				return false;
			} else if (confirm.equals(new Integer(0))) {
				//Save
				chief.saveSoundscapeToDatabase(soundscape);
				return true;
			} else if (confirm.equals(new Integer(1))) {
				//Save As
				boolean done = false;
				while (!done) {
					String ssName = JOptionPane.showInputDialog(null,
							"Enter name of new Soundscape:");
					if (ssName == null)
						return false;
					if (ssName.length() > 0)
						done = true;
					soundscape.setSSname(ssName);
					setPanelName(ssName);
					int id = soundscape.getConsoleID();
					if (id > 0) {
						id*=-1;
					}
					soundscape.changeSSID(id);
					//Flip the ID to negative so that it saves it under a new name.
				}

				chief.saveSoundscapeToDatabase(soundscape);
			} else if (confirm.equals(new Integer(2))) {
				//Discard Changes
				return true;
			}
			
			return true;
		}
		return true;
	}

	private void newSoundscapeButton(ActionEvent evt) {
		String newSSName = "";
		
		if(!evaluateAndSave()) {
			 return;
		}
		
		boolean done = false;
		while (!done) {
			newSSName = JOptionPane.showInputDialog(null, "Enter new Soundscape name:");

			if (newSSName == null)
				return;
			if (newSSName.length() > 0)
				done = true;
		}
		Random generator = new Random();
		int randomNumber = generator.nextInt(100000);
		if (randomNumber > 0)
			randomNumber *= -1;
		int ssID = randomNumber;
		clearPanel();
		soundscape = new Soundscape(ssID, panelID);
		setPanelName(newSSName);
		soundscape.setSSname(newSSName);
		volumeControl.setValue(0);
		initializeStateMap();
		chief.loadBlankSoundscape(soundscape);
		prepareStateMapforLoadedSoundscape(soundscape);
		repaintButtons();
	}

	private void copySoundscapeButton(ActionEvent evt) {
		boolean done = false;
		if (soundscape == null) {
			String message = "A soundscape is not loaded. You cannot save a soundscape until you load or create one.";
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, message);
			return;
		}

		while (!done) {
			String ssName = JOptionPane.showInputDialog(null,
					"Enter name of new Soundscape:");
			if (ssName == null)
				return;
			if (ssName.length() > 0)
				done = true;
			soundscape.setSSname(ssName);
			setPanelName(ssName);
		}

		chief.saveSoundscapeToDatabase(soundscape);
		soundscapeChanged = false;

	}
	
	private void resizeButtons(){
		int width = getWidth();
		int height = getHeight();
		int size;
		
		if(EnvVariables.debug){
			System.out.println("Sound Panel size:"+width+"x"+height);
		}
		
		if(width < 600) {
			size = 20;
		} else if (width < 700) {
			size = 27;
		} else if (width < 800) {
			size = 34;
		} else {
			size = 41;
		}
		
		setButtonSize(saveSoundscapeButton,size,SAVEICON);
		setButtonSize(copySoundscapeButton,size,COPYICON);
		setButtonSize(newSoundscapeButton,size,NEWSOUNDSCAPE);
		
		if (size <= 34) {
			setItemSize(masterPanel, new Dimension(300,50));
		} else if (size == 41) {
			setItemSize(masterPanel, new Dimension(300,60));
		}
		
		if(size > 23){
			volumeControl.setMinimumSize(new Dimension(36,size));
			volumeControl.setPreferredSize(new Dimension(200, size));
			volumeControl.setMaximumSize(new Dimension(32767, size));
		}

		
		masterControl.setSizes();
		for(int i=0; i<rowCount; i++) {
			rows[i].setSizes();
		}
		
		if(EnvVariables.debug){
			System.out.println("Button size:"+saveSoundscapeButton.getWidth()+"x"+saveSoundscapeButton.getHeight());
		}
	}
	
	public void setButtonSize(JButton button, int size) {
		Dimension dimension = new Dimension(size,size);
		button.setPreferredSize(dimension);
		button.setMinimumSize(dimension);
		button.setMaximumSize(dimension);
	}
	
	public void setButtonSize(JButton button, int size, ImageIcon icon) {
		Dimension dimension = new Dimension(size,size);
		Image img = icon.getImage();
		Image scaledImg = img.getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
		button.setPreferredSize(dimension);
		button.setMinimumSize(dimension);
		button.setMaximumSize(dimension);
		button.setIcon(new ImageIcon(scaledImg));
	}

	private void saveSoundscapeButton(ActionEvent evt) {
		if (soundscape == null) {
			String message = "A soundscape is not loaded. You cannot save a soundscape until you load or create one.";
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, message);
			return;
		}

		chief.saveSoundscapeToDatabase(soundscape);
		soundscapeChanged = false;

	}

	private void volumeControlStateChanged(javax.swing.event.ChangeEvent evt) {
		System.out.println("Changed volume control, rows = "
				+ rowsSelectedCount);
		int currentVolume;
		if (volumeSliderSystemSetFlag == true) {

			volumeSliderSystemSetFlag = false;
			prevRowsSelected = -1;
		} else {
			if(prevRowsSelected == rowsSelectedCount) {
				//Attempting to use prevRowsSelected count to keep track of if the state change is because a row was clicked.
				//Or if it's because it was actually used to change a sound's volume.
				soundscapeChanged = true;	
			} else {
				prevRowsSelected = rowsSelectedCount;
			}
			currentVolume = volumeControl.getValue();

			switch (rowsSelectedCount) {
			case 0:
				chief.masterVolumeChanged(soundscape, currentVolume);
				masterControl.setVolume(currentVolume);

				break;
			case 1:
				chief.singleSoundVolumeChanged(soundscape, selectedCells
						.elementAt(0).row, currentVolume);
				selectedCells.elementAt(0).setVolume(currentVolume);
				break;
			default:
				int submasteredVolume;
				setSubMasterVolume(selectedCells, currentVolume);
				for (int count = 0; count < selectedCells.size(); count++) {
					submasteredVolume = selectedCells.elementAt(count)
							.getVolume();
					chief.singleSoundVolumeChanged(soundscape, selectedCells
							.elementAt(count).row, submasteredVolume);
				}
				break;
			}
		}
	}

	public void clearSubMasterBleeds(Vector<RowControlSetX> rowSet) {
		for (int count = 0; count < rowSet.size(); count++) {
			rowSet.elementAt(count).volumeBleed = 0;
			rowSet.elementAt(count).setSubMasterBaseVolume(
					rowSet.elementAt(count).getVolume());
		}
	}

	public void setSubMasterVolume(Vector<RowControlSetX> rowSet,
			int currentSliderPosition) {
		int sliderPositionChange = (currentSliderPosition - 50) * 2;
		int volumeBleed;

		for (int count = 0; count < rowSet.size(); count++) {
			volumeBleed = rowSet.elementAt(count).getSubMasterBaseVolume()
					+ sliderPositionChange;
			if (volumeBleed < 0) {
				rowSet.elementAt(count).setVolume(0);

			} else if (volumeBleed > 100) {
				rowSet.elementAt(count).setVolume(100);

			} else {
				rowSet.elementAt(count).setVolume(volumeBleed);

			}
		}
	}

	public void setPanel(int ID) {
		panelID = ID;
		chief = new OperationsManager(panelID);
	}

	public int getPanelID() {
		return panelID;
	}

	public void setItemSize(JPanel panel, Dimension newDimension) {
		panel.setMaximumSize(newDimension);
		panel.setMinimumSize(newDimension);
		panel.setPreferredSize(newDimension);
	}
	
	public boolean addRow(String name, int volume, int type, boolean invalid) {
		soundscapeChanged = true;
		System.out.println("adding row " + rowCount);
		if (rowCount == ROWCOUNT)
			return false;

		Dimension newDimension = new Dimension(250, rowCount * ROWHEIGHT);
		rows[rowCount].SetName(name);
		rows[rowCount].setVolume(volume);
		setItemSize(rowPanel, newDimension);

		rowPanel.add(rows[rowCount]);
		
		rows[rowCount].setInvalid(invalid);

		rows[rowCount].setPlaybackGraphic(type);
		
		rows[rowCount].setSizes();
		

		rowPanel.updateUI();

		rowCount++;

		return true;
	}

	public boolean addRow(String name, int volume, int type) {
		soundscapeChanged = true;
		System.out.println("adding row " + rowCount);
		if (rowCount == ROWCOUNT)
			return false;

		Dimension newDimension = new Dimension(250, rowCount * ROWHEIGHT);
		rows[rowCount].SetName(name);
		rows[rowCount].setVolume(volume);
		setItemSize(rowPanel, newDimension);

		rowPanel.add(rows[rowCount]);

		rows[rowCount].setPlaybackGraphic(type);
		
		rows[rowCount].setSizes();

		rowPanel.updateUI();

		rowCount++;

		return true;
	}

	public void deleteRow(int row) {
		String message = "This soundscape has changed. Do you wish to save it?";
		Object selectedValue = null;
		if (row == -1) { // master row - we should delete this soundscape
			if (soundscape == null) {
				message = "A soundscape is not loaded. There is nothing to delete.";
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame, message);
				return;
			}
			if (soundscapeChanged == true)
				selectedValue = JOptionPane.showConfirmDialog(null, message);
			System.out.println("value=" + selectedValue);

			chief.purgeSoundscapeInMemory(soundscape, panelID);
			clearPanel();
			setPanelName("***EMPTY***");
			soundscapeChanged = false;
			return;
		}
		soundscapeChanged = true;
		rowCount--;
		if (rows[row].isSelected == true)
			rows[row].updateSelectedRowsDisplay(1);
		Dimension newDimension = new Dimension(250, rowCount * ROWHEIGHT);
		setItemSize(rowPanel, newDimension);
		rows[row].SetName(rows[rowCount].GetName());
		rows[row].setVolume(rows[rowCount].getVolume());
		rows[row].setBackground(rows[rowCount].getBackground());
		rows[row].isSelected = rows[rowCount].isSelected;
		rows[row].setPlaybackEnabled(rows[rowCount].getPlaybackEnabled());
		rows[row].previewButton.setIcon(rows[rowCount].previewButton.getIcon());

		if (selectedCells.contains(rows[rowCount])) {
			selectedCells.remove(rows[rowCount]);
			selectedCells.add(rows[row]);
		}
		rowPanel.remove(rows[rowCount]);
		rowPanel.updateUI();
		chief.deleteSingleSoundfromSoundscape(soundscape, row);
	}

	public void clearPanel() {
		soundscape = null;
		for (int count = 0; count < rowCount; count++) {
			rowPanel.remove(rows[count]);
			rows[count].setBackground(Color.white);
			rows[count].isSelected = false;
		}
		rowPanel.updateUI();
		selectedCells.removeAllElements();
		rowCount = 0;
		rowsSelectedCount = 0;
		Dimension newDimension = new Dimension(200, 100);
		setItemSize(rowPanel, newDimension);
	}

	public void pushSoundscapeToPanel(Soundscape newSoundscape) {
		clearPanel();
		soundscape = newSoundscape;

		int masterVolume = soundscape.getMasterVolume();
		masterControl.setVolume(masterVolume);
		chief.masterVolumeChanged(soundscape, masterVolume);
		volumeSliderSystemSetFlag = true;
		volumeControl.setValue(masterVolume);

		for (int count = 0; count < soundscape.getSoundscapeSoundsCount(); count++) {
			String name = soundscape.getSoundName(count);
			int volume = soundscape.getSingleSoundVolume(count);
			int type = soundscape.getSingleSoundPlaybackType(count);
			boolean invalid = soundscape.getSound(count).isInvalid();
			addRow(name, volume, type, invalid);
		}
		System.out.println("Soundscape " + soundscape.getSSName()
				+ " successfully sent to panel " + panelID);
		prepareStateMapforLoadedSoundscape(soundscape);
		soundscapeChanged = false;
		repaintButtons();
	}

	public void setPanelName(String name) {
		System.out.println("Soundscape name=*" + name + "*");
		masterControl.SetName(name);
//		Font titleFont = new Font("Serif", Font.BOLD, 18);
//		masterControl.setTitleFont(titleFont);
	}

	public Soundscape getSoundscape() {
		return soundscape;
	}

	public void repaintButtons(boolean[][] updatedMap) {
		repaintButtons();
	}

	public void repaintButtons() {

		for (int stRow = 0; stRow < STATEMAPROWCOUNT; stRow++) {
			System.out.print("(");
			for (int stCol = 0; stCol < STATEMAPCOLUMNCOUNT; stCol++) {
				int size = rows[stRow].buttonSize;
				switch (stCol) {
				case 0:
					if (stateMap[stRow][stCol] == false) {
						Image img = PREVIEWOFFICON.getImage().getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
						rows[stRow].previewButton.setIcon(new ImageIcon(img));
					}
					else {
						Image img = PREVIEWONICON.getImage().getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
						rows[stRow].previewButton.setIcon(new ImageIcon(img));
					}
					break;
				case 1:
					if (stateMap[stRow][stCol] == false) {
						Image img = SPEAKEROFFICON.getImage().getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
						rows[stRow].playPauseButton.setIcon(new ImageIcon(img));
					}
					else {
						Image img = SPEAKERONICON.getImage().getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
						rows[stRow].playPauseButton.setIcon(new ImageIcon(img));
					}

				}
				if (stateMap[stRow][stCol] == true)
					System.out.print(" 1 ");
				else
					System.out.print(" 0");
			}
			System.out.print("), ");
			if (stRow % 13 == 12)
				System.out.println();
		}
		System.out.println();
		int size = masterControl.buttonSize;
		if (stateMap[STATEMAPROWCOUNT][1]) {
			Image img = SPEAKERONICON.getImage().getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
			masterControl.playPauseButton.setIcon(new ImageIcon(img));
		} else {
			Image img = SPEAKEROFFICON.getImage().getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
			masterControl.playPauseButton.setIcon(new ImageIcon(img));
		}
		
		if (stateMap[STATEMAPROWCOUNT][0]) {
			Image img = PREVIEWONICON.getImage().getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
			masterControl.previewButton.setIcon(new ImageIcon(img));
		} else {
			Image img = PREVIEWOFFICON.getImage().getScaledInstance(size-4, size-4, Image.SCALE_DEFAULT);
			masterControl.previewButton.setIcon(new ImageIcon(img));
		}
	}

	private void initializeStateMap() {
		for (int row = 0; row < STATEMAPROWCOUNT; row++) {
			for (int column = 0; column < STATEMAPCOLUMNCOUNT; column++) {
				stateMap[row][column] = OFF;
			}
		}
		stateMap[STATEMAPCOLUMNCOUNT][1] = OFF;
		stateMap[STATEMAPCOLUMNCOUNT][0] = OFF;
	}

	private void prepareStateMapforLoadedSoundscape(Soundscape soundscape) {
		for (int stRow = 0; stRow < soundscape.getSoundscapeSoundsCount(); stRow++) {
			stateMap[stRow][1] = soundscape.getSound(stRow)
					.getPlaybackEnabled();

		}
		stateMap[STATEMAPROWCOUNT][1] = OFF;
		stateMap[STATEMAPROWCOUNT][0] = OFF;
	}
}
