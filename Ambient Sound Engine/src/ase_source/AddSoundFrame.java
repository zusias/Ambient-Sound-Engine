package ase_source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Misc.BufferUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;

/**
 * A window used to load new sound files into the system
 * <br>
 * <br>
 * 11/26/2015 - CKidwell - Pretty much a full redesign based on requests
 * from Jeff, this leaves things like the keyword functionality at the bottom
 * unfinished.
 * @author Lance, CKidwell
 */
public class AddSoundFrame extends javax.swing.JFrame {
	private static final long serialVersionUID = -6955270383414547859L;
	private String fileName, directory, targetFile;
	private File selectedFile, outputFile;
	private static Channel channel;
	private static ImageIcon fileIcon = new ImageIcon("soundfiles.gif");
	private static ImageIcon previewIcon = new ImageIcon("previewoff.gif");
	private JTextField txtFilepath;
	private JTextField txtName;
	private JTextField txtSource;
	private JTextField txtImportedBy;
	private JTextField txtCreatedBy;
	private JTextField txtEditedBy;
	private JTextField txtSoundFile;
	private JTextField txtRelatedKeywordSearch;
	private JTextField txtKeywordSearch;
	private JScrollPane associatedKwScrollPane;
	private JList<String> associatedKwList;
	private JLabel lblLinkedKeywords1;
	private JLabel lblLinkedKeywords2;
	private JLabel lblSoundFile;
	private JLabel lblKeywordSearch;
	private JLabel lblKeywordsRelatedTo;
	private JTextArea txtAreaNotes;
	private JLabel lblNotes;
	private JLabel lblEditedBy;
	private JLabel lblCreatedBy;
	private JComboBox<String> copyrightComboBox;
	private JLabel lblCopyright;
	private JLabel lblImportedBy;
	private JLabel lblSource;
	private JLabel lblSoundName;
	private JButton btnImport;
	private JButton btnPreview;
	private JButton btnOpen;
	private JList<String> relatedKwList;
	private JList<String> searchKwList;
	private JLabel lblImport;
	private JLabel lblTitle;
	private JScrollPane relatedKwScrollPane;
	private JScrollPane searchKwScrollPane;
	private JButton btnAddRelatedKeywords;
	private JButton btnAddKeywords;
	private JButton btnAddKeywordToDB;
	private JLabel addKeywordsLbl1;
	private JLabel addKeywordsLbl2;
	private JLabel addRelatedKeywordsLbl1;
	private JLabel addRelatedKeywordsLbl2;
	private JScrollPane scrollPaneNotes;

	public AddSoundFrame() {
		setMinimumSize(new Dimension(900, 716));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeItems();
			}
		});
		initComponents();
		if(OperationsManager.db != null) {
			//Populates the keyword list
			searchKwList.setListData(OperationsManager.db.showTable("keyword","keyword"));
		} else {
			String[] data = {"No DB Connection found"};
			searchKwList.setListData(data);
			searchKwList.setEnabled(false);
			relatedKwList.setListData(data);
			relatedKwList.setEnabled(false);
			associatedKwList.setListData(data);
			associatedKwList.setEnabled(false);
		}
	}

	
	private void initComponents() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{	40, 320, 320, 320, 15};
		gridBagLayout.columnWeights = new double[]{0.1, 1.0, 0.3, 0.3};
		gridBagLayout.rowHeights = new int[]{	20, 20, 20, 20, 20, 
												20, 20, 20, 20, 20, 
												20, 20, 20, 20, 20, 
												20, 20, 20, 20, 20,
												20, 20, 20, 20, 20,
												30, 30, 30, 15};
		gridBagLayout.rowWeights = new double[]{0.1, 0.1, 0.1, 0.1, 0.1,  
												0.1, 0.1, 0.1, 0.1, 1.0, 
												0.1, 0.1, 0.1, 0.1, 0.1,
												0.1, 0.1, 0.1, 0.1, 0.1, 
												0.1, 0.1, 0.1, 0.1, 0.1,
												0.1, 1.0, 0.1, 0.0};
		getContentPane().setLayout(gridBagLayout);
		
		//Current grid row dynamically keeps track of which grid row a component is on.
		//The fact that it's dynamic helps quickly adjust spacing for each element
		int currentGridRow = 0;
		
		//Standard insets. There are other custom insets used for specific components
		Insets column1Inset = new Insets(0, 15, 5, 5); //for labels
		Insets column2_3Inset = new Insets(0, 0, 5, 5); //for text fields before notes
		
		lblTitle = new JLabel("Import Sound File");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.gridheight = 2;
		gbc_lblTitle.anchor = GridBagConstraints.WEST;
		gbc_lblTitle.insets = new Insets(15, 20, 15, 0); //custom inset for title
		gbc_lblTitle.gridwidth = 4;
		gbc_lblTitle.gridx = 0;
		gbc_lblTitle.gridy = currentGridRow;
		currentGridRow+=2;
		getContentPane().add(lblTitle, gbc_lblTitle);
		
		lblImport = new JLabel("Import");
		GridBagConstraints gbc_lblImport = new GridBagConstraints();
		gbc_lblImport.anchor = GridBagConstraints.EAST;
		gbc_lblImport.insets = column1Inset;
		gbc_lblImport.gridx = 0;
		gbc_lblImport.gridy = currentGridRow;
		getContentPane().add(lblImport, gbc_lblImport);
		
		txtFilepath = new JTextField();
		txtFilepath.setEnabled(false);
		GridBagConstraints gbc_txtFilepath = new GridBagConstraints();
		gbc_txtFilepath.gridwidth = 2;
		gbc_txtFilepath.insets = column2_3Inset;
		gbc_txtFilepath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFilepath.gridx = 1;
		gbc_txtFilepath.gridy = currentGridRow;
		getContentPane().add(txtFilepath, gbc_txtFilepath);
		txtFilepath.setColumns(10);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 3;
		gbc_panel.gridy = currentGridRow++; //last component in this row
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{40, 40, 80, 0};
		//gbl_panel.columnWidths = new int[]{80, 97, 0, 0};
		gbl_panel.rowHeights = new int[]{25, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		btnOpen = new JButton();
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFromButtonActionPerformed(e);
			}
		});
		btnOpen.setIcon(fileIcon);
		btnOpen.setToolTipText("Select File");
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.insets = new Insets(0, 0, 0, 5);
		gbc_btnOpen.gridx = 0;
		gbc_btnOpen.gridy = 0;
		panel.add(btnOpen, gbc_btnOpen);
		
		btnPreview = new JButton();
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				previewActionPerformed();
			}
		});
		btnPreview.setIcon(previewIcon);
		btnPreview.setToolTipText("Preview Sound");
		GridBagConstraints gbc_btnPreview = new GridBagConstraints();
		gbc_btnPreview.insets = new Insets(0, 0, 0, 5);
		gbc_btnPreview.gridx = 1;
		gbc_btnPreview.gridy = 0;
		panel.add(btnPreview, gbc_btnPreview);
		
		btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SaveActionPerformed();
			}
		});
		GridBagConstraints gbc_btnImport = new GridBagConstraints();
		gbc_btnImport.insets = new Insets(0, 0, 0, 5);
		gbc_btnImport.gridx = 2;
		gbc_btnImport.gridy = 0;
		panel.add(btnImport, gbc_btnImport);
		
		lblSoundName = new JLabel("Sound Name");
		GridBagConstraints gbc_lblSoundName = new GridBagConstraints();
		gbc_lblSoundName.anchor = GridBagConstraints.EAST;
		gbc_lblSoundName.insets = column1Inset;
		gbc_lblSoundName.gridx = 0;
		gbc_lblSoundName.gridy = currentGridRow;
		getContentPane().add(lblSoundName, gbc_lblSoundName);
		
		txtName = new JTextField();
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.weightx = 1.0;
		gbc_txtName.gridwidth = 2;
		gbc_txtName.insets = new Insets(0, 0, 5, 250); //give this element less space
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = currentGridRow++;
		getContentPane().add(txtName, gbc_txtName);
		
		lblSource = new JLabel("Source");
		GridBagConstraints gbc_lblSource = new GridBagConstraints();
		gbc_lblSource.anchor = GridBagConstraints.EAST;
		gbc_lblSource.insets = column1Inset;
		gbc_lblSource.gridx = 0;
		gbc_lblSource.gridy = currentGridRow;
		getContentPane().add(lblSource, gbc_lblSource);
		
		txtSource = new JTextField();
		GridBagConstraints gbc_txtSource = new GridBagConstraints();
		gbc_txtSource.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSource.gridwidth = 2;
		gbc_txtSource.insets = column2_3Inset;
		gbc_txtSource.gridx = 1;
		gbc_txtSource.gridy = currentGridRow++;
		getContentPane().add(txtSource, gbc_txtSource);
		
		lblImportedBy = new JLabel("Imported By");
		GridBagConstraints gbc_lblImportedBy = new GridBagConstraints();
		gbc_lblImportedBy.anchor = GridBagConstraints.EAST;
		gbc_lblImportedBy.insets = column1Inset;
		gbc_lblImportedBy.gridx = 0;
		gbc_lblImportedBy.gridy = currentGridRow;
		getContentPane().add(lblImportedBy, gbc_lblImportedBy);
		
		txtImportedBy = new JTextField();
		GridBagConstraints gbc_txtImportedBy = new GridBagConstraints();
		gbc_txtImportedBy.gridwidth = 2;
		gbc_txtImportedBy.insets = column2_3Inset;
		gbc_txtImportedBy.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtImportedBy.gridx = 1;
		gbc_txtImportedBy.gridy = currentGridRow++;
		getContentPane().add(txtImportedBy, gbc_txtImportedBy);
		txtImportedBy.setColumns(10);
		
		lblCopyright = new JLabel("Copyright");
		GridBagConstraints gbc_lblCopyright = new GridBagConstraints();
		gbc_lblCopyright.anchor = GridBagConstraints.EAST;
		gbc_lblCopyright.insets = column1Inset;
		gbc_lblCopyright.gridx = 0;
		gbc_lblCopyright.gridy = currentGridRow;
		getContentPane().add(lblCopyright, gbc_lblCopyright);
		
		copyrightComboBox = new JComboBox<String>();
		ComboBoxModel<String> model = new DefaultComboBoxModel<String>(SoundObject.comboBoxOptions);
		copyrightComboBox.setModel(model);
		GridBagConstraints gbc_copyrightComboBox = new GridBagConstraints();
		gbc_copyrightComboBox.gridwidth = 2;
		gbc_copyrightComboBox.insets = column2_3Inset;
		gbc_copyrightComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_copyrightComboBox.gridx = 1;
		gbc_copyrightComboBox.gridy = currentGridRow++;
		getContentPane().add(copyrightComboBox, gbc_copyrightComboBox);
		
		lblCreatedBy = new JLabel("Created By");
		GridBagConstraints gbc_lblCreatedBy = new GridBagConstraints();
		gbc_lblCreatedBy.anchor = GridBagConstraints.EAST;
		gbc_lblCreatedBy.insets = column1Inset;
		gbc_lblCreatedBy.gridx = 0;
		gbc_lblCreatedBy.gridy = currentGridRow;
		getContentPane().add(lblCreatedBy, gbc_lblCreatedBy);
		
		txtCreatedBy = new JTextField();
		GridBagConstraints gbc_txtCreatedBy = new GridBagConstraints();
		gbc_txtCreatedBy.gridwidth = 2;
		gbc_txtCreatedBy.insets = column2_3Inset;
		gbc_txtCreatedBy.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCreatedBy.gridx = 1;
		gbc_txtCreatedBy.gridy = currentGridRow++;
		getContentPane().add(txtCreatedBy, gbc_txtCreatedBy);
		txtCreatedBy.setColumns(10);
		
		lblEditedBy = new JLabel("Edited By");
		GridBagConstraints gbc_lblEditedBy = new GridBagConstraints();
		gbc_lblEditedBy.anchor = GridBagConstraints.EAST;
		gbc_lblEditedBy.insets = column1Inset;
		gbc_lblEditedBy.gridx = 0;
		gbc_lblEditedBy.gridy = currentGridRow;
		getContentPane().add(lblEditedBy, gbc_lblEditedBy);
		
		txtEditedBy = new JTextField();
		GridBagConstraints gbc_txtEditedBy = new GridBagConstraints();
		gbc_txtEditedBy.gridwidth = 2;
		gbc_txtEditedBy.insets = column2_3Inset;
		gbc_txtEditedBy.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEditedBy.gridx = 1;
		gbc_txtEditedBy.gridy = currentGridRow++;
		getContentPane().add(txtEditedBy, gbc_txtEditedBy);
		txtEditedBy.setColumns(10);
		
		lblNotes = new JLabel("Notes");
		GridBagConstraints gbc_lblNotes = new GridBagConstraints();
		gbc_lblNotes.gridheight = 2;
		gbc_lblNotes.anchor = GridBagConstraints.EAST;
		gbc_lblNotes.insets = column1Inset;
		gbc_lblNotes.gridx = 0;
		gbc_lblNotes.gridy = currentGridRow;
		getContentPane().add(lblNotes, gbc_lblNotes);
		
		scrollPaneNotes = new JScrollPane();
		GridBagConstraints gbc_scrollPaneNotes = new GridBagConstraints();
		gbc_scrollPaneNotes.gridheight = 4;
		gbc_scrollPaneNotes.gridwidth = 3;
		gbc_scrollPaneNotes.insets = column2_3Inset;
		gbc_scrollPaneNotes.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneNotes.gridx = 1;
		gbc_scrollPaneNotes.gridy = currentGridRow;
		currentGridRow+=6;
		getContentPane().add(scrollPaneNotes, gbc_scrollPaneNotes);
		
		txtAreaNotes = new JTextArea();
		txtAreaNotes.setLineWrap(true);
		txtAreaNotes.setWrapStyleWord(true);
		txtAreaNotes.setRows(1);
		scrollPaneNotes.setViewportView(txtAreaNotes);
		
		lblKeywordsRelatedTo = new JLabel("Keywords Related To");
		GridBagConstraints gbc_lblKeywordsRelatedTo = new GridBagConstraints();
		gbc_lblKeywordsRelatedTo.insets = new Insets(0, 0, 5, 5);
		gbc_lblKeywordsRelatedTo.gridx = 2;
		gbc_lblKeywordsRelatedTo.gridy = currentGridRow;
		getContentPane().add(lblKeywordsRelatedTo, gbc_lblKeywordsRelatedTo);
		
		lblKeywordSearch = new JLabel("Keyword Search");
		GridBagConstraints gbc_lblKeywordSearch = new GridBagConstraints();
		gbc_lblKeywordSearch.insets = new Insets(0, 0, 5, 0);
		gbc_lblKeywordSearch.gridx = 3;
		gbc_lblKeywordSearch.gridy = currentGridRow++;
		getContentPane().add(lblKeywordSearch, gbc_lblKeywordSearch);
		
		lblSoundFile = new JLabel("Sound File");
		GridBagConstraints gbc_lblSoundFile = new GridBagConstraints();
		gbc_lblSoundFile.anchor = GridBagConstraints.EAST;
		gbc_lblSoundFile.insets = column1Inset;
		gbc_lblSoundFile.gridx = 0;
		gbc_lblSoundFile.gridy = currentGridRow;
		getContentPane().add(lblSoundFile, gbc_lblSoundFile);
		
		txtSoundFile = new JTextField();
		txtSoundFile.setEnabled(false);
		GridBagConstraints gbc_txtSoundFile = new GridBagConstraints();
		gbc_txtSoundFile.insets = new Insets(0, 0, 5, 5);
		gbc_txtSoundFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSoundFile.gridx = 1;
		gbc_txtSoundFile.gridy = currentGridRow;
		getContentPane().add(txtSoundFile, gbc_txtSoundFile);
		txtSoundFile.setColumns(10);
		
		txtRelatedKeywordSearch = new JTextField();
		txtRelatedKeywordSearch.setEnabled(false);
		GridBagConstraints gbc_txtRelatedKeywordSearch = new GridBagConstraints();
		gbc_txtRelatedKeywordSearch.insets = new Insets(0, 0, 5, 5);
		gbc_txtRelatedKeywordSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtRelatedKeywordSearch.gridx = 2;
		gbc_txtRelatedKeywordSearch.gridy = currentGridRow;
		getContentPane().add(txtRelatedKeywordSearch, gbc_txtRelatedKeywordSearch);
		txtRelatedKeywordSearch.setColumns(10);
		
		//JPanel for keyword search and button
		JPanel keywordSearchPanel = new JPanel();
		GridBagConstraints gbc_kwSearchPanel = new GridBagConstraints();
		gbc_kwSearchPanel.insets = new Insets(0, 0, 5, 5);
		gbc_kwSearchPanel.fill = GridBagConstraints.BOTH;
		gbc_kwSearchPanel.gridx = 3;
		gbc_kwSearchPanel.gridy = currentGridRow++; //last in row
		getContentPane().add(keywordSearchPanel, gbc_kwSearchPanel);
		
		GridBagLayout gbl_kwSearchPanel = new GridBagLayout();
		gbl_kwSearchPanel.columnWidths = new int[]{280, 30};
		gbl_kwSearchPanel.rowHeights = new int[]{20};
		gbl_kwSearchPanel.columnWeights = new double[]{0.1, 0};
		gbl_kwSearchPanel.rowWeights = new double[]{0.1};
		keywordSearchPanel.setLayout(gbl_kwSearchPanel);
		
		//Keyword search textbox
		txtKeywordSearch = new JTextField();
		txtKeywordSearch.setEnabled(false);
		GridBagConstraints gbc_txtKeywordSearch = new GridBagConstraints();
		gbc_txtKeywordSearch.gridx = 0;
		gbc_txtKeywordSearch.gridy = 0;
		gbc_txtKeywordSearch.fill = GridBagConstraints.HORIZONTAL;
		keywordSearchPanel.add(txtKeywordSearch, gbc_txtKeywordSearch);
		txtKeywordSearch.setColumns(10);
		
		//Button to add keyword if not found
		btnAddKeywordToDB = new JButton("+");
		btnAddKeywordToDB.setToolTipText("Add Keyword to Database");
		btnAddKeywordToDB.setEnabled(false);
		GridBagConstraints gbc_btnAddKwToDB = new GridBagConstraints();
		gbc_btnAddKwToDB.gridx = 1;
		gbc_btnAddKwToDB.gridy = 0;
		gbc_btnAddKwToDB.anchor = GridBagConstraints.WEST;
		gbc_txtKeywordSearch.fill = GridBagConstraints.NONE;
		Dimension smallButtonSize = new Dimension(45, 40);
		btnAddKeywordToDB.setPreferredSize(smallButtonSize);
		btnAddKeywordToDB.setMaximumSize(smallButtonSize);
		btnAddKeywordToDB.setMinimumSize(smallButtonSize);
		keywordSearchPanel.add(btnAddKeywordToDB, gbc_btnAddKwToDB);
		
		lblLinkedKeywords1 = new JLabel("Linked");
		GridBagConstraints gbc_lblLinkedKeywords1 = new GridBagConstraints();
		gbc_lblLinkedKeywords1.insets = column1Inset;
		gbc_lblLinkedKeywords1.anchor = GridBagConstraints.SOUTHEAST;
		gbc_lblLinkedKeywords1.gridx = 0;
		gbc_lblLinkedKeywords1.gridy = currentGridRow+1;
		getContentPane().add(lblLinkedKeywords1, gbc_lblLinkedKeywords1);
		
		lblLinkedKeywords2 = new JLabel("Keywords");
		GridBagConstraints gbc_lblLinkedKeywords2 = new GridBagConstraints();
		gbc_lblLinkedKeywords2.insets = column1Inset;
		gbc_lblLinkedKeywords2.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblLinkedKeywords2.gridx = 0;
		gbc_lblLinkedKeywords2.gridy = currentGridRow+2;
		getContentPane().add(lblLinkedKeywords2, gbc_lblLinkedKeywords2);
		
		associatedKwScrollPane = new JScrollPane();
		GridBagConstraints gbc_associatedKwScrollPane = new GridBagConstraints();
		gbc_associatedKwScrollPane.weightx = 1.0;
		gbc_associatedKwScrollPane.gridheight = 9;
		gbc_associatedKwScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_associatedKwScrollPane.fill = GridBagConstraints.BOTH;
		gbc_associatedKwScrollPane.gridx = 1;
		gbc_associatedKwScrollPane.gridy = currentGridRow;
		getContentPane().add(associatedKwScrollPane, gbc_associatedKwScrollPane);
		
		associatedKwList = new JList<String>();
		associatedKwList.setEnabled(false);
		associatedKwScrollPane.setViewportView(associatedKwList);
		
		relatedKwScrollPane = new JScrollPane();
		GridBagConstraints gbc_relatedKwScrollPane = new GridBagConstraints();
		gbc_relatedKwScrollPane.weightx = 1.0;
		gbc_relatedKwScrollPane.gridheight = 9;
		gbc_relatedKwScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_relatedKwScrollPane.fill = GridBagConstraints.BOTH;
		gbc_relatedKwScrollPane.gridx = 2;
		gbc_relatedKwScrollPane.gridy = currentGridRow;
		getContentPane().add(relatedKwScrollPane, gbc_relatedKwScrollPane);
		
		relatedKwList = new JList<String>();
		relatedKwList.setEnabled(false);
		relatedKwScrollPane.setViewportView(relatedKwList);
		
		searchKwScrollPane = new JScrollPane();
		GridBagConstraints gbc_searchKwScrollPane = new GridBagConstraints();
		gbc_searchKwScrollPane.weightx = 1.0;
		gbc_searchKwScrollPane.gridheight = 9;
		gbc_searchKwScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_searchKwScrollPane.fill = GridBagConstraints.BOTH;
		gbc_searchKwScrollPane.gridx = 3;
		gbc_searchKwScrollPane.gridy = currentGridRow;
		currentGridRow+=9; // Rows it occupies
		getContentPane().add(searchKwScrollPane, gbc_searchKwScrollPane);
		
		searchKwList = new JList<String>();
		searchKwList.setEnabled(false);
		searchKwScrollPane.setViewportView(searchKwList);
		
		btnAddRelatedKeywords = new JButton();
		btnAddRelatedKeywords.setLayout(new BorderLayout());
		btnAddRelatedKeywords.setEnabled(false);
		addRelatedKeywordsLbl1 = new JLabel("Link Selected");
		addRelatedKeywordsLbl1.setHorizontalAlignment(JLabel.CENTER);
		addRelatedKeywordsLbl2 = new JLabel("Keyword(s) to Sound");
		addRelatedKeywordsLbl2.setHorizontalAlignment(JLabel.CENTER);
		btnAddRelatedKeywords.add(BorderLayout.NORTH, addRelatedKeywordsLbl1);
		btnAddRelatedKeywords.add(BorderLayout.SOUTH, addRelatedKeywordsLbl2);
		GridBagConstraints gbc_btnAddRelatedKw = new GridBagConstraints();
		gbc_btnAddRelatedKw.anchor = GridBagConstraints.NORTH;
		gbc_btnAddRelatedKw.gridheight = 2;
		gbc_btnAddRelatedKw.insets = new Insets(0, 0, 0, 5);
		gbc_btnAddRelatedKw.gridx = 2;
		gbc_btnAddRelatedKw.gridy = currentGridRow;
		getContentPane().add(btnAddRelatedKeywords, gbc_btnAddRelatedKw);
		
		btnAddKeywords = new JButton();
		btnAddKeywords.setLayout(new BorderLayout());
		btnAddKeywords.setEnabled(false);
		addKeywordsLbl1 = new JLabel("Link Selected");
		addKeywordsLbl1.setHorizontalAlignment(JLabel.CENTER);
		addKeywordsLbl2 = new JLabel("Keyword(s) to Sound");
		addKeywordsLbl2.setHorizontalAlignment(JLabel.CENTER);
		btnAddKeywords.add(BorderLayout.NORTH, addKeywordsLbl1);
		btnAddKeywords.add(BorderLayout.SOUTH, addKeywordsLbl2);
		GridBagConstraints gbc_btnAddKw = new GridBagConstraints();
		gbc_btnAddKw.anchor = GridBagConstraints.NORTH;
		gbc_btnAddKw.gridheight = 2;
		gbc_btnAddKw.gridx = 3;
		gbc_btnAddKw.gridy = currentGridRow;
		getContentPane().add(btnAddKeywords, gbc_btnAddKw);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		pack();
	}
	
	/**
	 * Plays the currently selected sound on the preview channel. If a sound is already playing
	 * on the preview channel, it stops the sound.
	 * @param evt
	 */
	private void previewActionPerformed() {
		if (channel != null) {
			ByteBuffer arg0 = BufferUtils.newByteBuffer(256);;
			FMOD_RESULT result = channel.isPlaying(arg0);
			if(result == FMOD_RESULT.FMOD_OK){
				channel.stop();
			} else {
				channel = OperationsManager.soundEngine.stage.phantom.previewSoundFile(txtFilepath.getText(), channel);
			}
		} else {
			channel = OperationsManager.soundEngine.stage.phantom.previewSoundFile(txtFilepath.getText());
		}
	}

	private void resizeItems(){
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

		//Window heading
		setFont(lblTitle, fontSize+3);
		//Combo Box
		setFont(copyrightComboBox, fontSize);
		//Labels
		setFonts(fontSize, lblImport, lblSoundName, lblSource, lblImportedBy,
				lblCopyright, lblCreatedBy, lblEditedBy, lblNotes, lblSoundFile,
				lblLinkedKeywords1, lblLinkedKeywords2, lblKeywordsRelatedTo, lblKeywordSearch);
		//Text Buttons
		setFonts(fontSize, addKeywordsLbl1, addKeywordsLbl2, addRelatedKeywordsLbl1, 
				addRelatedKeywordsLbl2, btnImport);
		//Text fields
		setFonts(fontSize, txtFilepath, txtName, txtSource, txtImportedBy,
				txtCreatedBy, txtEditedBy, txtAreaNotes, txtSoundFile,
				txtRelatedKeywordSearch, txtKeywordSearch);
		//JLists
		setFonts(fontSize, associatedKwList, relatedKwList, searchKwList);
		
		//Image Buttons
		int size;
		if(width < 700) {
			size = 20;
		} else if (width < 1000) {
			size = 27;
		} else if (width < 1300) {
			size = 32;
		} else {
			size = 35;
		}
		
		Image img = fileIcon.getImage();
		Image scaledImg = img.getScaledInstance(size-2, size-2, Image.SCALE_DEFAULT);
		setSquareButtonSize(btnOpen,size, new ImageIcon(scaledImg));
		img = previewIcon.getImage();
		scaledImg = img.getScaledInstance(size-2, size-2, Image.SCALE_DEFAULT);
		setSquareButtonSize(btnPreview,size, new ImageIcon(scaledImg));
	}
	
	/**
	 * Calls setFont on each item provided
	 * @param size
	 * @param components
	 */
	private void setFonts(int size, JComponent... components){
		for(JComponent component: components){
			setFont(component,size);
		}
	}
	
	private void setFont(JComponent component, int size){
		Font font = component.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), size);
		component.setFont(newFont);
	}
	
	public void setSquareButtonSize(JButton button, int size, ImageIcon icon) {
		Dimension dimension = new Dimension(size,size);
		button.setPreferredSize(dimension);
		button.setMinimumSize(dimension);
		button.setMaximumSize(dimension);
		button.setIcon(icon);
	}
	
	/**
	 * Checks that the user has a save location specified, then calls copy
	 * in order to actually create a copy of the sound file and then inserts
	 * the appropriate data into the database.
	 */
	private void SaveActionPerformed() {
		String saveTo = EnvVariables.saveTo;
		if(saveTo == null || saveTo.length() == 0) {
			SoundLocationDialog slDialog = new SoundLocationDialog();
			
			slDialog.setTitle("Set Sound Save Location");
			slDialog.setModal(true);
			slDialog.setVisible(true);
			
			saveTo = EnvVariables.saveTo;
			if(saveTo == null || saveTo.length() == 0){
				return;
			} else {
				saveTo = EnvVariables.saveTo;
			}
		}
		
		File saveDirectory = new File(saveTo);
		if(!saveDirectory.exists() || !saveDirectory.isDirectory()){
			//Can't find the directory, it was deleted or maybe this was copied from somewhere else
			EnvVariables.setSaveTo("");
			SoundLocationDialog slDialog = new SoundLocationDialog();
			
			slDialog.setTitle("Set Sound Save Location");
			slDialog.setModal(true);
			slDialog.setVisible(true);
			
			saveTo = EnvVariables.saveTo;
			if(saveTo == null || saveTo.length() == 0){
				return;
			} else {
				saveTo = EnvVariables.saveTo;
			}
		}
		
		targetFile = saveTo + fileName;
		System.out.println("targetFile= " + targetFile);
		outputFile = new File(targetFile);

		if (OperationsManager.soundEngine.stage.phantom.testSoundFile(txtFilepath.getText()) == true) {
			try {
				outputFile.createNewFile();
				String soundName = txtName.getText();
				String source = txtSource.getText();
				String importedBy = txtImportedBy.getText();
				String copyright = Integer.toString(copyrightComboBox.getSelectedIndex());
				String createdBy = txtCreatedBy.getText();
				String editedBy = txtEditedBy.getText();
				String notes = txtAreaNotes.getText();
				copy(selectedFile, outputFile, soundName, source, importedBy, copyright,
						createdBy, editedBy, notes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		txtRelatedKeywordSearch.setEnabled(true);
		txtKeywordSearch.setEnabled(true);
		relatedKwList.setEnabled(true);
		searchKwList.setEnabled(true);
		btnAddRelatedKeywords.setEnabled(true);
		btnAddKeywords.setEnabled(true);
	}

	/**
	 * The "Open From" button. Runs a file chooser and uses the returned value.
	 * @param evt
	 */
	private void openFromButtonActionPerformed(ActionEvent evt) {
		JFileChooser fileChooser;
		if (EnvVariables.loadFrom != "") {
			fileChooser = new JFileChooser(EnvVariables.loadFrom);
		} else {
			fileChooser = new JFileChooser(".");
		}
		int status = fileChooser.showOpenDialog(null);
		if (status == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();

			directory = selectedFile.getParent();
			fileName = selectedFile.getName();

			EnvVariables.setLoadFrom(directory);
		} else if (status == JFileChooser.CANCEL_OPTION) {
			directory = "";
			fileName = "";
		}
		txtFilepath.setText(directory + "\\" + fileName);
	}
	
	/**
	 * Copies an "inserted" sound to store it in a local directory so it won't 
	 * suffer if the original is moved or deleted and then stores a record for
	 * the file in the db.
	 * @param source Source file
	 * @param dest Destination file copy to
	 * @param soundName The name for the ASE to use for this sound
	 * @param sourceDesc Text description for the source of the sound
	 * @param importedBy Name of the person importing the sound
	 * @param copyright Copyright status of the sound
	 * @param createdBy Creator of the sound
	 * @param editedBy Editor of the sound
	 * @param notes Notes/general description
	 * @throws IOException Throws IOException if the input doesn't exist, if either
	 * are directories, or if opening either is blocked.
	 */
	private void copy(File source, File dest, String soundName, String sourceDesc, 
			String importedBy, String copyright, String createdBy, 
			String editedBy, String notes) throws IOException {
		FileChannel in = null, out = null;
		FileInputStream fis;
		FileOutputStream fos;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			in = fis.getChannel();
			out = fos.getChannel();

			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0,
					size);

			out.write(buf);

			insertIntoDb(dest.getName(), dest.getParent(), soundName, size,
					notes, importedBy, copyright, sourceDesc, createdBy, editedBy);

			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}
	
	/**
	 * Calls to the operations manager to insert a row for this new sound.
	 * @param fileName The filename including extension
	 * @param filePath Absolute or relative file path
	 * @param soundName The name for the ASE to refer to this sound by
	 * @param size File size, not really used much
	 * @param description Text description of the sound
	 * @param importedBy The person importing the sound
	 * @param copyright Copyright status for the sound
	 * @param sourceDesc The source of the sound
	 * @param createdBy Name of the creator of the sound file
	 * @param editedBy Name of the editor of the sound file
	 */
	private void insertIntoDb(String fileName, String filePath, String soundName,
			long size, String description, String importedBy, String copyright,
			String sourceDesc, String createdBy, String editedBy) {
		OperationsManager.db.addSoundFileIntoSystem(fileName, filePath + "\\",
				soundName, size, description, importedBy, copyright,
				sourceDesc, createdBy, editedBy);		
	}

	/**
	 * Run for viewing purposes, no db connection
	 */
	public static void main(String args[]) {
		EnvVariables.initVars();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new AddSoundFrame().setVisible(true);
			}
		});
	}
}
