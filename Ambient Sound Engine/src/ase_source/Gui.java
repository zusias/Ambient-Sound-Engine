/*
 * Gui.java
 *
 * Created on March 6, 2006, 11:13 AM
 */

package ase_source;

import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The main window of the application. The application should be initialized
 * through the main class.
 *         
 * @author David
 * @author CKidwell 
 */
public class Gui extends javax.swing.JFrame {
	private static final long serialVersionUID = -8348843710808859086L;
	static final int TOP = 1;
	static final int BOTTOM = 2;

	static boolean shift = false;

	boolean keyLock;
	boolean previewToggle = false;
	OperationsManager opsManager = new OperationsManager();

	/** Creates new form Gui */
	public Gui() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				onWindowClosing();
			}
		});
		setMinimumSize(new Dimension(815, 781));
		
		int xSize;
		int ySize;
		int xLoc;
		int yLoc;
		
		initComponents();

		String field = OperationsManager.db.getSetting("width");
		if (field != null) {
			try{
				xSize = Integer.valueOf(field);
				field = OperationsManager.db.getSetting("height");
				ySize = Integer.valueOf(field);
				field = OperationsManager.db.getSetting("xLocation");
				xLoc = Integer.valueOf(field);
				field = OperationsManager.db.getSetting("yLocation");
				yLoc = Integer.valueOf(field);
		
				this.setSize(xSize, ySize);
				this.setLocation(xLoc, yLoc);
			} catch (NumberFormatException e) {
				EnvVariables.logMessage("No size/position settings found.");
			}
		} 
		
		
		soundControlPanel1.setPanel(TOP);
		soundControlPanel2.setPanel(BOTTOM);

		ButtonGroup searchGroup = new ButtonGroup();
		searchGroup.add(soundScapeRadioButton);
		searchGroup.add(soundRadioButton);

		ButtonGroup historyGroup = new ButtonGroup();
		historyGroup.add(historyScapeRadio);
		historyGroup.add(historySFXRadio);

		ButtonGroup indexGroup = new ButtonGroup();
		indexGroup.add(indexScapeRadio);
		indexGroup.add(indexSFXRadio);

	}

	/*
	 * public void setButtonStates(boolean state) { toAButton.setEnabled(state);
	 * toBButton.setEnabled(state); previewButton.setEnabled(state); }
	 * //setButtonStates()
	 */

	public SoundControlPanel getSoundPanelOne() {
		return soundControlPanel1;
	}

	public SoundControlPanel getSoundPanelTwo() {
		return soundControlPanel2;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * Initializes and adds all the components for the main window.
	 */
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		tabPane = new javax.swing.JTabbedPane();
		searchPanel = new javax.swing.JPanel();
		soundScapeRadioButton = new javax.swing.JRadioButton();
		soundScapeRadioButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		soundRadioButton = new javax.swing.JRadioButton();
		soundRadioButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		keyWordLabel = new javax.swing.JLabel();
		keyWordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		searchField = new javax.swing.JTextField();
		searchField.setFont(new Font("Arial", Font.PLAIN, 12));
		matchLabel = new javax.swing.JLabel();
		matchScroll = new javax.swing.JScrollPane();
		matchList = new javax.swing.JList<String>();
		resultLabel = new javax.swing.JLabel();
		resultScroll = new javax.swing.JScrollPane();
		soundScapesList = new javax.swing.JList<String>();
		toAButton = new javax.swing.JButton();
		toBButton = new javax.swing.JButton();
		previewButton = new javax.swing.JButton();
		historyPanel = new javax.swing.JPanel();
		historyScapeRadio = new javax.swing.JRadioButton();
		historySFXRadio = new javax.swing.JRadioButton();
		historyListLabel = new javax.swing.JLabel();
		historyScroll = new javax.swing.JScrollPane();
		historyList = new javax.swing.JList<String>();
		historyPreviewButton = new javax.swing.JButton();
		indexPanel = new javax.swing.JPanel();
		indexSearchLabel = new javax.swing.JLabel();
		indexSearchField = new javax.swing.JTextField();
		indexScapeRadio = new javax.swing.JRadioButton();
		indexSFXRadio = new javax.swing.JRadioButton();
		indexListLabel = new javax.swing.JLabel();
		indexScroll = new javax.swing.JScrollPane();
		indexList = new javax.swing.JList<String>();
		indexPanel1Button = new javax.swing.JButton();
		indexPanel2Button = new javax.swing.JButton();
		indexPreviewButton = new javax.swing.JButton();
		Consoles = new javax.swing.JPanel();
		ConsoleOne = new javax.swing.JTabbedPane();
		soundControlPanel1 = new ase_source.SoundControlPanel();
		soundControlPanel1.setMinimumSize(new Dimension(457, 284));
		CenterConsole = new javax.swing.JTabbedPane();
		effectsPanel = new javax.swing.JPanel();
		jSlider1 = new javax.swing.JSlider();
		ConsoleTwo = new javax.swing.JTabbedPane();
		soundControlPanel2 = new ase_source.SoundControlPanel();
		soundControlPanel2.setMinimumSize(new Dimension(457, 284));
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		addMenuItem = new javax.swing.JMenuItem();
		manageMenuItem = new javax.swing.JMenuItem();
		changeMDItem = new javax.swing.JMenuItem();
		quitMenuItem = new javax.swing.JMenuItem();
		settingsMenu = new javax.swing.JMenu();
		preferencesMenuItem = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		helpMenuItem = new javax.swing.JMenuItem();
		aboutMenuItem = new javax.swing.JMenuItem();
		

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Ambient Sound Engine");
		setBackground(new java.awt.Color(255, 255, 255));
		setName("ASE");
		setResizable(true);
		tabPane.setFont(new Font("Arial", 0, 11));
		tabPane.setPreferredSize(new java.awt.Dimension(299, 722));
		GridBagLayout gbl_searchPanel = new GridBagLayout();
		gbl_searchPanel.columnWeights = new double[]{1.0, .5, 1.0};
		searchPanel.setLayout(gbl_searchPanel);
//		soundScapeRadioButton.setFont(new java.awt.Font("Arial", 0, 11));
		soundScapeRadioButton.setSelected(true);
		soundScapeRadioButton.setText("Soundscapes");
		soundScapeRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		soundScapeRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		soundScapeRadioButton.setPreferredSize(new java.awt.Dimension(100, 18));
		soundScapeRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				soundScapeRadioButtonFocusGained(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(0, 7, 7, 5);
		searchPanel.add(soundScapeRadioButton, gridBagConstraints);

//		soundRadioButton.setFont(new java.awt.Font("Arial", 0, 11));
		soundRadioButton.setText("Sound Files");
		soundRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		soundRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		soundRadioButton.setPreferredSize(new java.awt.Dimension(100, 18));
		soundRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				soundRadioButtonFocusGained(evt);
			}
		});

		gridBagConstraints_17 = new java.awt.GridBagConstraints();
		gridBagConstraints_17.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_17.gridx = 2;
		gridBagConstraints_17.gridy = 2;
		gridBagConstraints_17.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints_17.insets = new java.awt.Insets(0, 0, 7, 7);
		searchPanel.add(soundRadioButton, gridBagConstraints_17);

//		keyWordLabel.setFont(new java.awt.Font("Arial", 0, 12));
		keyWordLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		keyWordLabel.setText("Keyword");
		keyWordLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		gridBagConstraints_4 = new java.awt.GridBagConstraints();
		gridBagConstraints_4.gridwidth = 3;
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_4.insets = new Insets(7, 7, 5, 0);
		searchPanel.add(keyWordLabel, gridBagConstraints_4);
		searchField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				searchFieldKeyReleased(evt);
			}
		});

		gridBagConstraints_3 = new java.awt.GridBagConstraints();
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridx = 0;
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridwidth = 3;
		gridBagConstraints_3.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_3.insets = new Insets(0, 7, 5, 7);
		searchPanel.add(searchField, gridBagConstraints_3);

		matchLabel.setFont(new java.awt.Font("Arial", 0, 12));
		matchLabel.setText("Matches");
		gridBagConstraints_5 = new java.awt.GridBagConstraints();
		gridBagConstraints_5.gridwidth = 3;
		gridBagConstraints_5.gridx = 0;
		gridBagConstraints_5.gridy = 3;
		gridBagConstraints_5.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_5.insets = new Insets(0, 7, 5, 0);
		searchPanel.add(matchLabel, gridBagConstraints_5);

		matchScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		matchScroll.setPreferredSize(new java.awt.Dimension(280, 375));
		matchList.setFont(new Font("Arial", Font.PLAIN, 12));
		matchList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		matchList.setPreferredSize(null);
		matchList.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				matchListKeyReleased(evt);
			}
		});
		matchList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						matchListValueChanged(evt);
					}
				});
		matchList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				matchListMouseClicked(evt);
			}
		});

		matchScroll.setViewportView(matchList);

		gridBagConstraints_7 = new java.awt.GridBagConstraints();
		gridBagConstraints_7.weighty = 0.5;
		gridBagConstraints_7.weightx = 1.0;
		gridBagConstraints_7.fill = GridBagConstraints.BOTH;
		gridBagConstraints_7.gridx = 0;
		gridBagConstraints_7.gridy = 4;
		gridBagConstraints_7.gridwidth = 3;
		gridBagConstraints_7.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_7.insets = new java.awt.Insets(0, 7, 7, 7);
		searchPanel.add(matchScroll, gridBagConstraints_7);

		resultLabel.setFont(new java.awt.Font("Arial", 0, 12));
		resultLabel.setText("Results");
		resultLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(0, 7, 5, 5);
		searchPanel.add(resultLabel, gridBagConstraints);

		resultScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		resultScroll.setFont(new java.awt.Font("Arial", 0, 11));
		resultScroll.setPreferredSize(new java.awt.Dimension(280, 150));
		soundScapesList.setFont(new Font("Arial", Font.PLAIN, 12));
		soundScapesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		soundScapesList.setAutoscrolls(false);
		soundScapesList.setPreferredSize(null);
		soundScapesList.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				soundScapesListKeyPressed(evt);
			}

			public void keyReleased(java.awt.event.KeyEvent evt) {
				soundScapesListKeyReleased(evt);
			}
		});
		soundScapesList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				soundScapesListMouseClicked(evt);
			}
		});

		resultScroll.setViewportView(soundScapesList);

		gridBagConstraints_6 = new java.awt.GridBagConstraints();
		gridBagConstraints_6.weightx = 1.0;
		gridBagConstraints_6.weighty = 0.5;
		gridBagConstraints_6.fill = GridBagConstraints.BOTH;
		gridBagConstraints_6.gridx = 0;
		gridBagConstraints_6.gridy = 6;
		gridBagConstraints_6.gridwidth = 3;
		gridBagConstraints_6.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_6.insets = new java.awt.Insets(0, 7, 7, 7);
		searchPanel.add(resultScroll, gridBagConstraints_6);

		toAButton.setFont(new java.awt.Font("Arial", 0, 11));
		toAButton.setText("To Console 1");
		toAButton.setPreferredSize(new java.awt.Dimension(125, 20));
		toAButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				toAButtonActionPerformed(evt);
			}
		});

		toAButtonConstraints = new java.awt.GridBagConstraints();
		toAButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
		toAButtonConstraints.weightx = 0.7;
		toAButtonConstraints.gridx = 2;
		toAButtonConstraints.gridy = 7;
		toAButtonConstraints.insets = new Insets(0, 20, 5, 7);
		searchPanel.add(toAButton, toAButtonConstraints);

		toBButton.setFont(new java.awt.Font("Arial", 0, 11));
		toBButton.setText("To Console 2");
		toBButton.setPreferredSize(new java.awt.Dimension(125, 20));
		toBButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				toBButtonActionPerformed(evt);
			}
		});

		gridBagConstraints_19 = new java.awt.GridBagConstraints();
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_19.weightx = 0.7;
		gridBagConstraints_19.gridx = 2;
		gridBagConstraints_19.gridy = 8;
		gridBagConstraints_19.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints_19.insets = new Insets(0, 20, 7, 7);
		searchPanel.add(toBButton, gridBagConstraints_19);

		previewButton.setFont(new java.awt.Font("Arial", 0, 11));
		previewButton.setText("Preview");
		previewButton.setPreferredSize(new java.awt.Dimension(125, 20));
		previewButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				previewButtonActionPerformed(evt);
			}
		});

		gridBagConstraints_20 = new java.awt.GridBagConstraints();
		gridBagConstraints_20.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_20.weightx = 0.7;
		gridBagConstraints_20.gridx = 0;
		gridBagConstraints_20.gridy = 7;
		gridBagConstraints_20.gridheight = 2;
		gridBagConstraints_20.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_20.insets = new Insets(0, 7, 7, 25);
		searchPanel.add(previewButton, gridBagConstraints_20);

		tabPane.addTab("Search", searchPanel);

		GridBagLayout gbl_historyPanel = new GridBagLayout();
		gbl_historyPanel.rowHeights = new int[]{39, 0, 0, 0, 0};
		gbl_historyPanel.columnWeights = new double[]{1.0, 1.0};
		gbl_historyPanel.columnWidths = new int[]{0, 0};
		historyPanel.setLayout(gbl_historyPanel);
		historyScapeRadio.setFont(new java.awt.Font("Arial", 0, 11));
		historyScapeRadio.setSelected(true);
		historyScapeRadio.setText("Soundscapes");
		historyScapeRadio
				.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		historyScapeRadio
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		historyScapeRadio.setPreferredSize(new java.awt.Dimension(100, 18));
		gridBagConstraints_26 = new java.awt.GridBagConstraints();
		gridBagConstraints_26.anchor = GridBagConstraints.SOUTHEAST;
		gridBagConstraints_26.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_26.gridx = 0;
		gridBagConstraints_26.gridy = 0;
		gridBagConstraints_26.insets = new java.awt.Insets(0, 0, 7, 0);
		historyPanel.add(historyScapeRadio, gridBagConstraints_26);

		historySFXRadio.setFont(new java.awt.Font("Arial", 0, 11));
		historySFXRadio.setText("Sound Files");
		historySFXRadio
				.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		historySFXRadio
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		historySFXRadio.setPreferredSize(new java.awt.Dimension(100, 18));
		gridBagConstraints_25 = new java.awt.GridBagConstraints();
		gridBagConstraints_25.anchor = GridBagConstraints.SOUTHEAST;
		gridBagConstraints_25.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_25.gridx = 1;
		gridBagConstraints_25.gridy = 0;
		gridBagConstraints_25.insets = new java.awt.Insets(0, 0, 7, 0);
		historyPanel.add(historySFXRadio, gridBagConstraints_25);

		historyListLabel.setFont(new java.awt.Font("Arial", 0, 12));
		historyListLabel.setText("History");
		gridBagConstraints_8 = new java.awt.GridBagConstraints();
		gridBagConstraints_8.gridwidth = 2;
		gridBagConstraints_8.gridx = 0;
		gridBagConstraints_8.gridy = 1;
		gridBagConstraints_8.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_8.insets = new java.awt.Insets(0, 7, 0, 0);
		historyPanel.add(historyListLabel, gridBagConstraints_8);

		historyScroll
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		historyScroll.setFont(new java.awt.Font("Arial", 0, 11));
		historyScroll.setPreferredSize(new java.awt.Dimension(280, 605));
		historyList.setFont(new java.awt.Font("Arial", 0, 12));
		historyList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		historyList.setAutoscrolls(false);
		historyList.setPreferredSize(null);
		historyScroll.setViewportView(historyList);

		gridBagConstraints_9 = new java.awt.GridBagConstraints();
		gridBagConstraints_9.weighty = 1.0;
		gridBagConstraints_9.weightx = 1.0;
		gridBagConstraints_9.fill = GridBagConstraints.BOTH;
		gridBagConstraints_9.gridx = 0;
		gridBagConstraints_9.gridy = 2;
		gridBagConstraints_9.gridwidth = 2;
		gridBagConstraints_9.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_9.insets = new java.awt.Insets(0, 7, 7, 7);
		historyPanel.add(historyScroll, gridBagConstraints_9);

		historyPreviewButton.setFont(new java.awt.Font("Arial", 0, 11));
		historyPreviewButton.setText("Preview");
		historyPreviewButton.setPreferredSize(new java.awt.Dimension(125, 20));
		gridBagConstraints_27 = new java.awt.GridBagConstraints();
		gridBagConstraints_27.insets = new Insets(0, 50, 0, 50);
		gridBagConstraints_27.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_27.weightx = 0.5;
		gridBagConstraints_27.gridx = 0;
		gridBagConstraints_27.gridy = 3;
		gridBagConstraints_27.gridwidth = 2;
		gridBagConstraints_27.gridheight = 2;
		historyPanel.add(historyPreviewButton, gridBagConstraints_27);

		tabPane.addTab("History", historyPanel);

		GridBagLayout gbl_indexPanel = new GridBagLayout();
		gbl_indexPanel.rowHeights = new int[]{0, 0, 33, 0, 0, 0, 0};
		gbl_indexPanel.columnWidths = new int[]{0, 0, 0};
		gbl_indexPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0};
		gbl_indexPanel.columnWeights = new double[]{1.0, 0.5, 1.0};
		indexPanel.setLayout(gbl_indexPanel);
		indexSearchLabel.setFont(new java.awt.Font("Arial", 0, 12));
		indexSearchLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		indexSearchLabel.setText("Search");
		indexSearchLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		gridBagConstraints_11 = new java.awt.GridBagConstraints();
		gridBagConstraints_11.weightx = 0.7;
		gridBagConstraints_11.gridwidth = 3;
		gridBagConstraints_11.gridx = 0;
		gridBagConstraints_11.gridy = 0;
		gridBagConstraints_11.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_11.insets = new Insets(7, 7, 5, 0);
		indexPanel.add(indexSearchLabel, gridBagConstraints_11);
		gridBagConstraints_10 = new java.awt.GridBagConstraints();
		gridBagConstraints_10.weightx = 1.0;
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.gridx = 0;
		gridBagConstraints_10.gridy = 1;
		gridBagConstraints_10.gridwidth = 3;
		gridBagConstraints_10.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_10.insets = new Insets(0, 7, 5, 7);
		indexPanel.add(indexSearchField, gridBagConstraints_10);

		indexScapeRadio.setFont(new java.awt.Font("Arial", 0, 11));
		indexScapeRadio.setSelected(true);
		indexScapeRadio.setText("Soundscapes");
		indexScapeRadio
				.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		indexScapeRadio
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		indexScapeRadio.setPreferredSize(new java.awt.Dimension(100, 18));
		indexScapeRadio.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				indexScapeRadioFocusGained(evt);
			}
		});

		gridBagConstraints_23 = new java.awt.GridBagConstraints();
		gridBagConstraints_23.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_23.weightx = 0.7;
		gridBagConstraints_23.gridx = 0;
		gridBagConstraints_23.gridy = 2;
		gridBagConstraints_23.insets = new Insets(0, 7, 7, 5);
		indexPanel.add(indexScapeRadio, gridBagConstraints_23);

		indexSFXRadio.setFont(new java.awt.Font("Arial", 0, 11));
		indexSFXRadio.setText("Sound Files");
		indexSFXRadio
				.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		indexSFXRadio
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		indexSFXRadio.setPreferredSize(new java.awt.Dimension(100, 18));
		indexSFXRadio.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				indexSFXRadioFocusGained(evt);
			}
		});

		gridBagConstraints_24 = new java.awt.GridBagConstraints();
		gridBagConstraints_24.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_24.weightx = 0.7;
		gridBagConstraints_24.gridx = 2;
		gridBagConstraints_24.gridy = 2;
		gridBagConstraints_24.insets = new java.awt.Insets(0, 0, 7, 7);
		indexPanel.add(indexSFXRadio, gridBagConstraints_24);

		indexListLabel.setFont(new java.awt.Font("Arial", 0, 12));
		indexListLabel.setText("Soundscapes");
		indexListLabel
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		gridBagConstraints_12 = new java.awt.GridBagConstraints();
		gridBagConstraints_12.gridwidth = 3;
		gridBagConstraints_12.gridx = 0;
		gridBagConstraints_12.gridy = 3;
		gridBagConstraints_12.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_12.insets = new Insets(0, 7, 5, 0);
		indexPanel.add(indexListLabel, gridBagConstraints_12);

		indexScroll
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		indexScroll.setFont(new java.awt.Font("Arial", 0, 11));
		indexScroll.setPreferredSize(new java.awt.Dimension(280, 555));
		indexList.setFont(new java.awt.Font("Arial", 0, 12));
		indexList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		indexList.setAutoscrolls(false);
		indexList.setPreferredSize(null);
		indexScroll.setViewportView(indexList);

		gridBagConstraints_13 = new java.awt.GridBagConstraints();
		gridBagConstraints_13.weightx = 1.0;
		gridBagConstraints_13.weighty = 1.0;
		gridBagConstraints_13.fill = GridBagConstraints.BOTH;
		gridBagConstraints_13.gridx = 0;
		gridBagConstraints_13.gridy = 4;
		gridBagConstraints_13.gridwidth = 3;
		gridBagConstraints_13.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints_13.insets = new java.awt.Insets(0, 7, 7, 7);
		indexPanel.add(indexScroll, gridBagConstraints_13);

		indexPanel1Button.setFont(new java.awt.Font("Arial", 0, 11));
		indexPanel1Button.setText("To Panel 1");
		indexPanel1Button.setPreferredSize(new java.awt.Dimension(125, 20));
		gridBagConstraints_21 = new java.awt.GridBagConstraints();
		gridBagConstraints_21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_21.weightx = 0.7;
		gridBagConstraints_21.gridx = 2;
		gridBagConstraints_21.gridy = 5;
		gridBagConstraints_21.insets = new Insets(0, 0, 5, 7);
		indexPanel.add(indexPanel1Button, gridBagConstraints_21);

		indexPanel2Button.setFont(new java.awt.Font("Arial", 0, 11));
		indexPanel2Button.setText("To Panel 2");
		indexPanel2Button.setPreferredSize(new java.awt.Dimension(125, 20));
		gridBagConstraints_18 = new java.awt.GridBagConstraints();
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.weightx = 0.7;
		gridBagConstraints_18.gridx = 2;
		gridBagConstraints_18.gridy = 6;
		gridBagConstraints_18.insets = new java.awt.Insets(0, 0, 0, 7);
		indexPanel.add(indexPanel2Button, gridBagConstraints_18);

		indexPreviewButton.setFont(new java.awt.Font("Arial", 0, 11));
		indexPreviewButton.setText("Preview");
		indexPreviewButton.setPreferredSize(new java.awt.Dimension(125, 20));
		gridBagConstraints_22 = new java.awt.GridBagConstraints();
		gridBagConstraints_22.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_22.weightx = 0.7;
		gridBagConstraints_22.gridx = 0;
		gridBagConstraints_22.gridy = 5;
		gridBagConstraints_22.gridheight = 2;
		gridBagConstraints_22.insets = new Insets(0, 7, 0, 5);
		indexPanel.add(indexPreviewButton, gridBagConstraints_22);

		tabPane.addTab("Index", indexPanel);

		gridBagConstraints_1 = new java.awt.GridBagConstraints();
		gridBagConstraints_1.weighty = 1.0;
		gridBagConstraints_1.weightx = 0.3;
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.anchor = java.awt.GridBagConstraints.NORTHWEST;
		getContentPane().add(tabPane, gridBagConstraints_1);

		GridBagLayout gbl_Consoles = new GridBagLayout();
		gbl_Consoles.columnWeights = new double[]{1.0};
		gbl_Consoles.rowWeights = new double[]{1.0, 0.0, 1.0};
		gbl_Consoles.rowHeights = new int[]{0, 0, 0};
		Consoles.setLayout(gbl_Consoles);

		Consoles.setMinimumSize(new java.awt.Dimension(500, 600));
		Consoles.setPreferredSize(new java.awt.Dimension(500, 720));
		ConsoleOne.setMinimumSize(new java.awt.Dimension(460, 200));
		ConsoleOne.setPreferredSize(new java.awt.Dimension(460, 260));
		ConsoleOne.addTab("tab1", soundControlPanel1);

		gridBagConstraints_16 = new java.awt.GridBagConstraints();
		gridBagConstraints_16.insets = new Insets(0, 15, 0, 15);
		gridBagConstraints_16.gridx = 0;
		gridBagConstraints_16.gridy = 0;
		gridBagConstraints_16.fill = GridBagConstraints.BOTH;
		gridBagConstraints_16.ipady = 50;
		Consoles.add(ConsoleOne, gridBagConstraints_16);

		effectsPanel.setLayout(new java.awt.GridBagLayout());

		effectsPanel.setMinimumSize(new java.awt.Dimension(414, 100));
		effectsPanel.setPreferredSize(new java.awt.Dimension(450, 15));
		jSlider1.setPreferredSize(new java.awt.Dimension(400, 18));
		effectsPanel.add(jSlider1, new java.awt.GridBagConstraints());

		CenterConsole.addTab("Effects", effectsPanel);

		gridBagConstraints_15 = new java.awt.GridBagConstraints();
		gridBagConstraints_15.insets = new Insets(0, 15, 0, 15);
		gridBagConstraints_15.gridx = 0;
		gridBagConstraints_15.gridy = 1;
		gridBagConstraints_15.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.ipady = 50;
		Consoles.add(CenterConsole, gridBagConstraints_15);

		ConsoleTwo.setMinimumSize(new java.awt.Dimension(460, 200));
		ConsoleTwo.setPreferredSize(new java.awt.Dimension(460, 260));
		ConsoleTwo.addTab("tab1", soundControlPanel2);

		gridBagConstraints_14 = new java.awt.GridBagConstraints();
		gridBagConstraints_14.insets = new Insets(0, 15, 0, 15);
		gridBagConstraints_14.gridx = 0;
		gridBagConstraints_14.gridy = 2;
		gridBagConstraints_14.fill = GridBagConstraints.BOTH;
		gridBagConstraints_14.ipady = 50;
		gridBagConstraints_14.anchor = java.awt.GridBagConstraints.SOUTH;
		Consoles.add(ConsoleTwo, gridBagConstraints_14);

		gridBagConstraints_2 = new java.awt.GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.gridx = 1;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.anchor = java.awt.GridBagConstraints.EAST;
		getContentPane().add(Consoles, gridBagConstraints_2);

		menuBar.setFont(new java.awt.Font("Arial", 0, 12));
		fileMenu.setText("File");
		fileMenu.setFont(new java.awt.Font("Arial", 0, 12));
		
		addMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
		addMenuItem.setText("Add a Sound File");
		addMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(addMenuItem);

		manageMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
		manageMenuItem.setText("Manage...");
		manageMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				manageMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(manageMenuItem);

		changeMDItem.setFont(new java.awt.Font("Arial", 0, 12));
		changeMDItem.setText("Change Metadata");
		changeMDItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				changeMDItemActionPerformed(evt);
			}
		});

		fileMenu.add(changeMDItem);

		quitMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
		quitMenuItem.setText("Quit");
		quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				quitMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(quitMenuItem);

		menuBar.add(fileMenu);
		
		settingsMenu.setText("Settings");
		settingsMenu.setFont(new java.awt.Font("Arial", 0, 12));
		
		preferencesMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
		preferencesMenuItem.setText("Preferences");
		preferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				preferencesMenuItemActionPerformed(evt);
			}
		});
		
		settingsMenu.add(preferencesMenuItem);
		
		menuBar.add(settingsMenu);

		helpMenu.setText("Help");
		helpMenu.setFont(new java.awt.Font("Arial", 0, 12));
		helpMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
		helpMenuItem.setText("Help");
		helpMenu.add(helpMenuItem);

		aboutMenuItem.setFont(new java.awt.Font("Arial", 0, 12));
		aboutMenuItem.setText("About...");
		aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				aboutMenuItemActionPerformed(evt);
			}
		});

		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setFonts();
			}
		});
		
		pack();
	}

	private void soundScapesListKeyReleased(java.awt.event.KeyEvent evt) {

		if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_SHIFT) {
			shift = false;
		}

	}

	private void soundScapesListKeyPressed(java.awt.event.KeyEvent evt) {

		try {

			if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_SHIFT) {
				shift = true;
			}

			int selectedRow = soundScapesList.getSelectedIndex();
			int target = opsManager.rowSelectToObjectID(selectedRow);

			if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {

				if (shift) {
					if (soundScapeRadioButton.isSelected()){
						opsManager.sendSoundscapeToPanel(soundControlPanel2,target, soundScapesList.getSelectedValue().toString());
					} else {
						opsManager.sendSoundToPanel(soundControlPanel2, target);
					}
				} else {
					if (soundScapeRadioButton.isSelected()) {
						opsManager.sendSoundscapeToPanel(soundControlPanel1, target, soundScapesList.getSelectedValue().toString());
					} else {
						opsManager.sendSoundToPanel(soundControlPanel1, target);
					}
				}

			}
		} catch (NullPointerException ex) {
		}

	}

	private void changeMDItemActionPerformed(java.awt.event.ActionEvent evt) {
		ChangeDB changeDatabaseWindow = new ChangeDB();
		changeDatabaseWindow.setTitle("Change Metadata");
		changeDatabaseWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		changeDatabaseWindow.setVisible(true);
	}
	
	private void setFonts(){
		int width = getWidth();
		int height = getHeight();
		
		if(width > height * 1.7){
			width = (int)(height * 1.7);
		}
		int fontSize;
		if (EnvVariables.debug) {
			System.out.println("GUI size:"+width);
		}
		if(width < 1000) {
			fontSize = 12;
		} else if (width < 1200) {
			fontSize = 15;
		} else if (width < 1400) {
			fontSize = 18;
		} else {
			fontSize = 21;
		}
		

		setFont(menuBar, fontSize);
		setFont(fileMenu, fontSize);
		setFont(addMenuItem, fontSize);
		setFont(manageMenuItem, fontSize);
		setFont(changeMDItem, fontSize);
		setFont(quitMenuItem, fontSize);
		setFont(settingsMenu, fontSize);
		setFont(preferencesMenuItem, fontSize);
		setFont(helpMenu, fontSize);
		setFont(helpMenuItem, fontSize);
		setFont(aboutMenuItem, fontSize);
		setFont(keyWordLabel, fontSize);
		setFont(searchField, fontSize);
		setFont(matchLabel, fontSize);
		setFont(matchList, fontSize);
		setFont(resultLabel, fontSize);
		setFont(soundScapesList, fontSize);
		setFont(soundScapeRadioButton, fontSize);
		setFont(soundRadioButton, fontSize);
		setFont(tabPane, fontSize);
		setFont(toAButton, fontSize);
		setFont(toBButton, fontSize);
		setFont(previewButton, fontSize);
		setFont(historyPreviewButton, fontSize);
		setFont(historyScapeRadio, fontSize);
		setFont(historySFXRadio, fontSize);
		setFont(historyListLabel, fontSize);
		setFont(historyList, fontSize);
		setFont(indexPanel1Button, fontSize);
		setFont(indexPanel2Button, fontSize);
		setFont(indexPreviewButton, fontSize);
		setFont(indexList, fontSize);
		setFont(indexListLabel, fontSize);
		setFont(indexSearchField, fontSize);
		setFont(indexScapeRadio, fontSize);
		setFont(indexSFXRadio, fontSize);
		setFont(indexSearchLabel, fontSize);
		setFont(ConsoleOne, fontSize);
		setFont(ConsoleTwo, fontSize);
		
		setButtonSize(toAButton, fontSize);
		setButtonSize(toBButton, fontSize);
		setButtonSize(previewButton, fontSize);
		setButtonSize(indexPanel1Button, fontSize);
		setButtonSize(indexPanel2Button, fontSize);
		setButtonSize(indexPreviewButton, fontSize);
		setButtonSize(historyPreviewButton, fontSize);
		
	}
	
	/**
	 * Used to set the font size inside a GUI component, will keep some font and style.
	 * @param component
	 * @param size
	 */
	private void setFont(JComponent component, int size){
		Font font = component.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), size);
		component.setFont(newFont);
	}
	
	private void setButtonSize(JButton button, int size){
		int width = button.getWidth();
		int height = (int)(size*1.7);
		Dimension dimension = new Dimension(width, height);
		button.setPreferredSize(dimension);
	}
	
	
	/**
	 * The menu item for the preferences window, currently only contains the default sound location item.
	 * @param evt
	 */
	private void preferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		PreferencesWindow preferencesWindow = new PreferencesWindow();
		preferencesWindow.setTitle("Set preferences");
		preferencesWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		preferencesWindow.setVisible(true);
	}

	private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			int selectedRow = soundScapesList.getSelectedIndex();
			int target = opsManager.rowSelectToObjectID(selectedRow);

			if (soundScapeRadioButton.isSelected()) {
				opsManager.stopPreviewingFromMainGuiPanel();
				if (previewToggle == false)
					opsManager.previewSoundscapeFromMainGuiPanel(target);

			}

			else {
				opsManager.stopPreviewingFromMainGuiPanel();
				if (previewToggle == false)
					opsManager.previewSoundFromMainGuiPanel(target);
			}
			previewToggle = !previewToggle;
		} catch (NullPointerException ex) {
		}

	}

	private void addMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		AddSoundFrame addSound = new AddSoundFrame();
		addSound.setTitle("Add a Sound File");
		addSound.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addSound.setVisible(true);
	}

	private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

		About aboutGui = new About();
		aboutGui.setVisible(true);

	}

	/**
	 * Sets the focus to the results list when the ENTER key is pressed
	 * 
	 * @param evt
	 */
	private void matchListKeyReleased(java.awt.event.KeyEvent evt) {
		int key = evt.getKeyCode();
		if (key == java.awt.event.KeyEvent.VK_ENTER) {
			soundScapesList.requestFocus();
			soundScapesList.setSelectedIndex(0);
		}
	}

	private void toBButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			int selectedRow = soundScapesList.getSelectedIndex();
			int target = opsManager.rowSelectToObjectID(selectedRow);

			if (soundScapeRadioButton.isSelected())
				opsManager.sendSoundscapeToPanel(soundControlPanel2, target,
						soundScapesList.getSelectedValue().toString());
			else
				opsManager.sendSoundToPanel(soundControlPanel2, target);
		} catch (NullPointerException ex) {
		}
	}

	private void toAButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			int selectedRow = soundScapesList.getSelectedIndex();
			int target = opsManager.rowSelectToObjectID(selectedRow);

			if (soundScapeRadioButton.isSelected()) {
				System.out.println("Sending soundscape " + target);
				opsManager.sendSoundscapeToPanel(soundControlPanel1, target,
						soundScapesList.getSelectedValue().toString());
			} else {
				System.out.println("Sending sound" + target);
				opsManager.sendSoundToPanel(soundControlPanel1, target);
			}
		} catch (NullPointerException ex) {
			System.out.println("error 13");
		}
	}

	private void matchListValueChanged(javax.swing.event.ListSelectionEvent evt) {

		Vector<ResultObject> results = new Vector<ResultObject>();

		try {
			if (soundScapeRadioButton.isSelected())
				results = opsManager.getSoundscapeSearchSet(matchList
						.getSelectedValue().toString());
			else
				results = opsManager.getSoundSearchSet(matchList
						.getSelectedValue().toString());
		} catch (NullPointerException ex) {
		}

		Vector<String> sList = new Vector<String>();

		int i;
		for (i = 0; i < results.size(); ++i) {
			sList.addElement(results.elementAt(i).primaryResultString);
		}

		soundScapesList.setListData(sList);
		results = new Vector<ResultObject>();
		soundScapesList.setSelectedIndex(0);

	}

	private void manageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		DataMan manager = new DataMan();
		manager.setVisible(true);
	}

	private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {
		Vector<ResultObject> results = new Vector<ResultObject>();

		if (searchField.getText() != "") {
			if (soundScapeRadioButton.isSelected()) {
				results = opsManager.getSoundscapeSubStringDbResults(searchField.getText());
			}
			else {
				results = opsManager.getSoundSubStringDbResults(searchField.getText());
			}
		}
		Vector<String> rList = new Vector<String>();

		int i;
		if (results.size() > 0) {
			for (i = 0; i < results.size(); ++i) {
				rList.addElement(results.elementAt(i).secondaryResultString);
			}
		}

		matchList.setListData(rList);

		int key = evt.getKeyCode();
		if (key == java.awt.event.KeyEvent.VK_ENTER) {
			matchList.requestFocus();
			matchList.setSelectedIndex(0);
		}

	}

	private void matchListMouseClicked(java.awt.event.MouseEvent evt) {
		Vector<ResultObject> results = new Vector<ResultObject>();
		try {
			if (soundScapeRadioButton.isSelected())
				results = opsManager.getSoundscapeSearchSet(matchList
						.getSelectedValue().toString());
			else
				results = opsManager.getSoundSearchSet(matchList
						.getSelectedValue().toString());
		} catch (NullPointerException ex) {
		}

		Vector<String> sList = new Vector<String>();

		int i;
		for (i = 0; i < results.size(); ++i) {
			sList.addElement(results.elementAt(i).primaryResultString);
		}

		soundScapesList.setListData(sList);
	}

	private void indexSFXRadioFocusGained(java.awt.event.FocusEvent evt) {
		indexListLabel.setText("Sound Files");

	}

	private void indexScapeRadioFocusGained(java.awt.event.FocusEvent evt) {
		indexListLabel.setText("Soundscapes");

	}

	private void soundRadioButtonFocusGained(java.awt.event.FocusEvent evt) {
		soundScapesList.setListData(new Vector<String>());
		matchList.setListData(new Vector<String>());
		searchField.setText("");
		searchField.requestFocus();
		opsManager.stopPreviewingFromMainGuiPanel();
		previewToggle = false;
	}

	private void soundScapeRadioButtonFocusGained(java.awt.event.FocusEvent evt) {
		soundScapesList.setListData(new Vector<String>());
		matchList.setListData(new Vector<String>());
		searchField.setText("");
		searchField.requestFocus();
		opsManager.stopPreviewingFromMainGuiPanel();
		previewToggle = false;
	}
	
	private void onWindowClosing() {
		soundControlPanel1.evaluateAndSave();
		soundControlPanel2.evaluateAndSave();
		
		int width = this.getWidth();
		int height = this.getHeight();
		Point location = this.getLocation();
		int xLocation = (int) location.getX();
		int yLocation = (int) location.getY();
		OperationsManager.db.setSetting("width", String.valueOf(width));
		OperationsManager.db.setSetting("height", String.valueOf(height));
		OperationsManager.db.setSetting("xLocation", String.valueOf(xLocation));
		OperationsManager.db.setSetting("yLocation", String.valueOf(yLocation));
	}

	private void soundScapesListMouseClicked(java.awt.event.MouseEvent evt) {
		try {
			soundScapesList.setSelectedIndex(soundScapesList
					.locationToIndex(evt.getPoint()));
			int selectedRow = soundScapesList.getSelectedIndex();
			int target = opsManager.rowSelectToObjectID(selectedRow);

			if (evt.getClickCount() == 2) {
				if (evt.getButton() == 1) {
					System.out.println("selected row=" + selectedRow);
					if (soundScapeRadioButton.isSelected()) {
						opsManager.sendSoundscapeToPanel(soundControlPanel1,target, soundScapesList.getSelectedValue().toString());
					} else {
						opsManager.sendSoundToPanel(soundControlPanel1, target);
					} 
				} 
				else {
					if (soundScapeRadioButton.isSelected()) {
						opsManager.sendSoundscapeToPanel(soundControlPanel2,target, soundScapesList.getSelectedValue().toString());
					} else {
						opsManager.sendSoundToPanel(soundControlPanel2, target);
					}
				}
			}
		} catch (NullPointerException ex) {
		}
	}
	
	private javax.swing.JTabbedPane CenterConsole;
	private javax.swing.JTabbedPane ConsoleOne;
	private javax.swing.JTabbedPane ConsoleTwo;
	private javax.swing.JPanel Consoles;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenuItem aboutMenuItem;
	private javax.swing.JMenuItem addMenuItem;
	private javax.swing.JMenuItem changeMDItem;
	private javax.swing.JMenu settingsMenu;
	private javax.swing.JMenuItem preferencesMenuItem;
	private javax.swing.JPanel effectsPanel;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenuItem helpMenuItem;
	private javax.swing.JList<String> historyList;
	private javax.swing.JLabel historyListLabel;
	private javax.swing.JPanel historyPanel;
	private javax.swing.JButton historyPreviewButton;
	private javax.swing.JRadioButton historySFXRadio;
	private javax.swing.JRadioButton historyScapeRadio;
	private javax.swing.JScrollPane historyScroll;
	private javax.swing.JList<String> indexList;
	private javax.swing.JLabel indexListLabel;
	private javax.swing.JPanel indexPanel;
	private javax.swing.JButton indexPanel1Button;
	private javax.swing.JButton indexPanel2Button;
	private javax.swing.JButton indexPreviewButton;
	private javax.swing.JRadioButton indexSFXRadio;
	private javax.swing.JRadioButton indexScapeRadio;
	private javax.swing.JScrollPane indexScroll;
	private javax.swing.JTextField indexSearchField;
	private javax.swing.JSlider jSlider1;
	private javax.swing.JLabel keyWordLabel;
	private javax.swing.JMenuItem manageMenuItem;
	private javax.swing.JLabel matchLabel;
	private javax.swing.JList<String> matchList;
	private javax.swing.JScrollPane matchScroll;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JButton previewButton;
	private javax.swing.JMenuItem quitMenuItem;
	private javax.swing.JLabel resultLabel;
	private javax.swing.JScrollPane resultScroll;
	private javax.swing.JTextField searchField;
	private javax.swing.JLabel indexSearchLabel;
	private javax.swing.JPanel searchPanel;
	private ase_source.SoundControlPanel soundControlPanel1;
	private ase_source.SoundControlPanel soundControlPanel2;
	private javax.swing.JRadioButton soundRadioButton;
	private javax.swing.JRadioButton soundScapeRadioButton;
	private javax.swing.JList<String> soundScapesList;
	private javax.swing.JTabbedPane tabPane;
	private javax.swing.JButton toAButton;
	private javax.swing.JButton toBButton;
	private GridBagConstraints gridBagConstraints_1;
	private GridBagConstraints gridBagConstraints_2;
	private GridBagConstraints gridBagConstraints_3;
	private GridBagConstraints gridBagConstraints_4;
	private GridBagConstraints gridBagConstraints_5;
	private GridBagConstraints gridBagConstraints_6;
	private GridBagConstraints gridBagConstraints_7;
	private GridBagConstraints gridBagConstraints_8;
	private GridBagConstraints gridBagConstraints_9;
	private GridBagConstraints gridBagConstraints_10;
	private GridBagConstraints gridBagConstraints_11;
	private GridBagConstraints gridBagConstraints_12;
	private GridBagConstraints gridBagConstraints_13;
	private GridBagConstraints gridBagConstraints_14;
	private GridBagConstraints gridBagConstraints_15;
	private GridBagConstraints gridBagConstraints_16;
	private GridBagConstraints gridBagConstraints_17;
	private GridBagConstraints toAButtonConstraints;
	private GridBagConstraints gridBagConstraints_19;
	private GridBagConstraints gridBagConstraints_20;
	private GridBagConstraints gridBagConstraints_18;
	private GridBagConstraints gridBagConstraints_21;
	private GridBagConstraints gridBagConstraints_22;
	private GridBagConstraints gridBagConstraints_23;
	private GridBagConstraints gridBagConstraints_24;
	private GridBagConstraints gridBagConstraints_25;
	private GridBagConstraints gridBagConstraints_26;
	private GridBagConstraints gridBagConstraints_27;
}
