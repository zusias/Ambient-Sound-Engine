package ase_source;

/**
 * 
 * Just some information about the coders
 * 
 */
public class About extends javax.swing.JFrame {

	private static final long serialVersionUID = -6222365523255979354L;
	/** Creates new form About */
	public About() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		aboutPanel = new javax.swing.JPanel();
		aseLabel = new javax.swing.JLabel();
		createdLabel = new javax.swing.JLabel();
		lgNameLabel = new javax.swing.JLabel();
		dsNameLabel = new javax.swing.JLabel();
		ckNameLabel = new javax.swing.JLabel();
		thanksLabel = new javax.swing.JLabel();
		wirthNameLabel = new javax.swing.JLabel();
		windygaNameLabel = new javax.swing.JLabel();
		llewellynNameLabel = new javax.swing.JLabel();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Ambient Sound Engine v4.5");
		setFont(new java.awt.Font("Arial", 0, 12));
		setName("");
		setResizable(false);
		aseLabel.setFont(new java.awt.Font("Tahoma", 0, 18));
		aseLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		aseLabel.setText("Ambient Sound Engine v4.5");
		aseLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		createdLabel.setFont(new java.awt.Font("Arial", 3, 12));
		createdLabel.setText("Created By:");

		lgNameLabel.setFont(new java.awt.Font("Arial", 0, 12));
		lgNameLabel.setText("Eric \"Lance\" Gary (elancegary@hotmail.com)");

		dsNameLabel.setFont(new java.awt.Font("Arial", 0, 12));
		dsNameLabel.setText("David Shaw (david.mi.shaw@gmail.com)");

		ckNameLabel.setFont(new java.awt.Font("Arial", 0, 12));
		ckNameLabel.setText("Chris Kidwell (kidwell.chris.l@mail.com)");

		thanksLabel.setFont(new java.awt.Font("Arial", 3, 12));
		thanksLabel.setText("Special Thanks To:");

		wirthNameLabel.setFont(new java.awt.Font("Arial", 0, 12));
		wirthNameLabel.setText("Jeff Wirth");

		windygaNameLabel.setFont(new java.awt.Font("Arial", 0, 12));
		windygaNameLabel.setText("Dr. Piotr Windyga");

		llewellynNameLabel.setFont(new java.awt.Font("Arial", 0, 12));
		llewellynNameLabel.setText("Dr. Mark Llewellyn");

		org.jdesktop.layout.GroupLayout aboutPanelLayout = new org.jdesktop.layout.GroupLayout(
				aboutPanel);
		aboutPanel.setLayout(aboutPanelLayout);
		aboutPanelLayout
				.setHorizontalGroup(aboutPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								aboutPanelLayout
										.createSequentialGroup()
										.add(
												aboutPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																aboutPanelLayout
																		.createSequentialGroup()
																		.add(
																				31,
																				31,
																				31)
																		.add(
																				aseLabel))
														.add(
																aboutPanelLayout
																		.createSequentialGroup()
																		.add(
																				15,
																				15,
																				15)
																		.add(
																				aboutPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING)
																						.add(
																								thanksLabel)
																						.add(
																								aboutPanelLayout
																										.createSequentialGroup()
																										.add(
																												12,
																												12,
																												12)
																										.add(
																												wirthNameLabel))
																						.add(
																								aboutPanelLayout
																										.createSequentialGroup()
																										.add(
																												12,
																												12,
																												12)
																										.add(
																												windygaNameLabel))
																						.add(
																								aboutPanelLayout
																										.createSequentialGroup()
																										.add(
																												12,
																												12,
																												12)
																										.add(
																												llewellynNameLabel))
																						.add(
																								createdLabel)
																						.add(
																								aboutPanelLayout
																										.createSequentialGroup()
																										.add(
																												10,
																												10,
																												10)
																										.add(
																												aboutPanelLayout
																														.createParallelGroup(
																																org.jdesktop.layout.GroupLayout.LEADING)
																														.add(
																																dsNameLabel)
																														.add(
																																ckNameLabel)
																														.add(
																																lgNameLabel))))))
										.add(15, 15, 15)));
		aboutPanelLayout.setVerticalGroup(aboutPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				aboutPanelLayout.createSequentialGroup().add(15, 15, 15).add(
						aseLabel).add(15, 15, 15).add(createdLabel)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								lgNameLabel).add(3, 3, 3).add(dsNameLabel).add(
								3, 3, 3).add(ckNameLabel).addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								thanksLabel).add(3, 3, 3).add(wirthNameLabel)
						.add(3, 3, 3).add(windygaNameLabel).add(3, 3, 3).add(
								llewellynNameLabel).addContainerGap(
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		getContentPane().add(aboutPanel, new java.awt.GridBagConstraints());

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new About().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel aboutPanel;
	private javax.swing.JLabel aseLabel;
	private javax.swing.JLabel ckNameLabel;
	private javax.swing.JLabel createdLabel;
	private javax.swing.JLabel dsNameLabel;
	private javax.swing.JLabel lgNameLabel;
	private javax.swing.JLabel llewellynNameLabel;
	private javax.swing.JLabel thanksLabel;
	private javax.swing.JLabel windygaNameLabel;
	private javax.swing.JLabel wirthNameLabel;
	// End of variables declaration//GEN-END:variables

}
