package ase.views.components.consolepane;

import static ase.operations.OperationsManager.opsMgr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ase.operations.SoundModel;
import ase.operations.SoundscapeModel;
import ase.views.GuiSettings;
import ase.views.components.consolepane.events.RowClickedEvent;
import ase.views.events.SettingsEvent;

/**
 * Control Row element. Used for either the soundscape master control row,
 * or individual sound controllers. Most private members marked as
 * protected to allow child classes to manipulate as necessary
 * 
 * @author Kevin C. Gall
 *
 */
public abstract class ConsoleControlRow extends JPanel {
	private static final long serialVersionUID = -522609781179456194L;
	
	protected static final ImageIcon PREVIEW_ON_ICON = new ImageIcon("previewOn.gif");
	protected static final ImageIcon PREVIEW_OFF_ICON = new ImageIcon("previewOff.gif");
	protected static final ImageIcon SPEAKER_ON_ICON = new ImageIcon("SpeakerOn.gif");
	protected static final ImageIcon SPEAKER_OFF_ICON = new ImageIcon("SpeakerOff.gif");

	protected GuiSettings settings;
	
	protected final GridBagLayout layout = new GridBagLayout();
	
	protected int rowIndex;
	protected final EventBus tabEventBus;
	
	//Buttons
	protected final JButton previewButton = new JButton(PREVIEW_OFF_ICON);
	protected final GridBagConstraints previewButtonGbc = new GridBagConstraints();
	protected final JButton playButton = new JButton(SPEAKER_OFF_ICON);
	protected final GridBagConstraints playButtonGbc = new GridBagConstraints();
	
	//only for sound controls
	
	protected final JProgressBar volumeBar = new JProgressBar(0, 1000);
	protected final GridBagConstraints volumeBarGbc = new GridBagConstraints();
	protected final JLabel title = new JLabel();
	protected final GridBagConstraints titleGbc = new GridBagConstraints();
	
	public ConsoleControlRow(GuiSettings settings, int rowIndex,  EventBus tabEventBus) {
		this.settings = settings;
		this.tabEventBus = tabEventBus;
		this.rowIndex = rowIndex;
		
		setFocusable(true);
		setBorder(new EtchedBorder());
		
		//layout stuff
		layout.columnWidths = new int[]{25, 165, 25, 25};
		layout.rowHeights = new int[]{33};
		layout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		layout.rowWeights = new double[]{0.0};
		setLayout(layout);
		
		setupGridBagConstraints();
		
		previewButton.setToolTipText("Preview");
		add(previewButton, previewButtonGbc);
		
		add(title, titleGbc);
		
		//Volume bar settings:
		volumeBar.setMaximumSize(new Dimension(32767, 10));
		volumeBar.setMinimumSize(new Dimension(10, 10));
		volumeBar.setPreferredSize(new Dimension(146, 10));
		volumeBar.setForeground(new Color(0, 51, 255));
		volumeBar.setToolTipText("Volume");
		add(volumeBar, volumeBarGbc);
		
		playButton.setToolTipText("Play");
		add(playButton, playButtonGbc);
		
		initListeners();
		
		opsMgr.eventBus.register(this); //"Global" bus
		tabEventBus.register(this); //inter-tab communication
	}
	
	public abstract int getVolume();
	
	public void destroy(){
		opsMgr.eventBus.unregister(this);
		tabEventBus.unregister(this);
	}
	
	public void setPreviewOn(){
		this.previewButton.setIcon(SPEAKER_ON_ICON);
	}
	
	public void setPreviewOff() {
		this.previewButton.setIcon(SPEAKER_OFF_ICON);
	}
	
	protected void setupGridBagConstraints() {
		//Preview Button
		previewButtonGbc.insets = new Insets(0, 5, 0, 5);
		previewButtonGbc.gridx = 0;
		previewButtonGbc.gridy = 0;
		
		//Title
		titleGbc.anchor = GridBagConstraints.NORTH;
		titleGbc.fill = GridBagConstraints.BOTH;
		titleGbc.insets = new Insets(0, 5, 12, 5);
		titleGbc.gridx = 1;
		titleGbc.gridy = 0;
		
		//Volume Bar
		volumeBarGbc.fill = GridBagConstraints.HORIZONTAL;
		volumeBarGbc.weightx = 1.0;
		volumeBarGbc.anchor = GridBagConstraints.SOUTH;
		volumeBarGbc.insets = new Insets(0, 0, 3, 5);
		volumeBarGbc.gridx = 1;
		volumeBarGbc.gridy = 0;

		//Play Button
		playButtonGbc.gridx = 2;
		playButtonGbc.gridy = 0;
		playButtonGbc.insets = new Insets(0, 0, 0, 5);
	}
	
	private void initListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				handleMouseClicked(evt);
			}
		});
	}
	
	private void handleMouseClicked(MouseEvent evt) {
		tabEventBus.post(new RowClickedEvent(this, rowIndex, evt.getButton()));
	}
	
	//EventBus events
	@Subscribe public void applySettings(SettingsEvent e) {
		if (e.getNewSettings() != null) {
			settings = e.getNewSettings();
		}
		
		setFont(settings.largeFont);
		setBackground(settings.white);

		previewButton.setMaximumSize(settings.buttonSize);
		previewButton.setMinimumSize(settings.buttonSize);
		previewButton.setPreferredSize(settings.buttonSize);

		playButton.setMaximumSize(settings.buttonSize);
		playButton.setMinimumSize(settings.buttonSize);
		playButton.setPreferredSize(settings.buttonSize);
	}
}
