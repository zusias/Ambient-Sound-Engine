package ase_source_bak;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SoundLocationDialog extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6427140318025381072L;
	JLabel saveValueLabel;
	JLabel saveLabel;
	JButton saveButton;
	static int action;
	private JPanel bottomPanel;
	private JButton okButton;
	private JButton cancelButton;
	
	public SoundLocationDialog(){
		initComponents();
	}
	
	public void initComponents(){
		action = -1;
		this.getContentPane().setLayout(new java.awt.BorderLayout(10,10));
		
		saveButton = new JButton();
		saveButton.setIcon(new javax.swing.ImageIcon("soundfiles.gif"));
		saveButton.setMaximumSize(new java.awt.Dimension(41, 41));
		saveButton.setMinimumSize(new java.awt.Dimension(41, 41));
		saveButton.setPreferredSize(new java.awt.Dimension(41, 41));
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveToButtonActionPerformed(evt);
			}
		});
		saveButton.setPreferredSize(new Dimension(40,40));
		saveButton.setMaximumSize(new Dimension(40,40));
		
		saveValueLabel = new JLabel();
		saveValueLabel.setText(EnvVariables.saveTo);
		saveValueLabel.setPreferredSize(new Dimension(200,40));
		saveValueLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		
		saveLabel = new JLabel();
		saveLabel.setText("Save Sound Files to...");
		
		okButton = new JButton();
		cancelButton = new JButton();
		
		okButton.setText("Save");
		cancelButton.setText("Cancel");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
		
		bottomPanel = new JPanel();
		bottomPanel.add(okButton,BorderLayout.WEST);
		bottomPanel.add(cancelButton,BorderLayout.EAST);
		
		this.add(saveLabel, BorderLayout.WEST);
		this.add(saveValueLabel, BorderLayout.CENTER);
		this.add(saveButton, BorderLayout.EAST);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.pack();
	}
	
	private void saveToButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser fileChooser;
		File selectedDirectory;

		String runDirectory = System.getProperty("user.dir");
		if (EnvVariables.saveTo != "") {
			fileChooser = new JFileChooser(EnvVariables.saveTo);
		} else {
			fileChooser = new JFileChooser(".");
		}
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int status = fileChooser.showDialog(null, "Select Save Location");
		if (status == JFileChooser.APPROVE_OPTION) {
			selectedDirectory = fileChooser.getSelectedFile();
			String selectedDirectoryPath = selectedDirectory.getAbsolutePath();
			if(selectedDirectoryPath.startsWith(runDirectory)){
				selectedDirectoryPath = selectedDirectoryPath.replace(runDirectory, ".");
			}
			saveValueLabel.setText(selectedDirectoryPath+"\\");
		} else if (status == JFileChooser.CANCEL_OPTION) {
			
		}
	}
	
	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		action = 1;
		EnvVariables.setSaveTo(saveValueLabel.getText());
		this.dispose();
	}
	
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		action = 0;
		this.dispose();
	}
	
	public static void main(String[] args){
		EnvVariables.initVars();
		SoundLocationDialog slDialog = new SoundLocationDialog();
		slDialog.setTitle("Default Sound Save Location");
		slDialog.setModal(true);
		slDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		slDialog.setVisible(true);
	}
}
