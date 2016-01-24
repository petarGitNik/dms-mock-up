package system.manager.gui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class WorkflowFSM extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkflowFSM() {
		setModal(true);
		try {
			BufferedImage workflowPublishing = ImageIO.read(new File("./workflows/publishing/publishing_version_1.png"));
			JLabel picLabel = new JLabel(new ImageIcon(workflowPublishing));
			picLabel.setBounds(100, 100, 800, 600);
			getContentPane().setLayout(new BorderLayout(0, 0));
			setBounds(100, 100, 800, 600);
			setTitle("jDocument Management System - Publishing process");
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			add(picLabel);
			
			JPanel labelPanel = new JPanel();
			JLabel lblSubmission = new JLabel("S - Submission,");
			JLabel lblFirstRev = new JLabel("R-1 - First Review,");
			JLabel lblDesktop = new JLabel("D - Desktop Publishing,");
			JLabel lblFinalRev = new JLabel("R-F - Final Review,");
			JLabel lblProduction = new JLabel("P - Production,");
			JLabel lblClose = new JLabel("C - Close.");
			
			labelPanel.add(lblSubmission);
			labelPanel.add(lblFirstRev);
			labelPanel.add(lblDesktop);
			labelPanel.add(lblFinalRev);
			labelPanel.add(lblFinalRev);
			labelPanel.add(lblProduction);
			labelPanel.add(lblClose);
			
			add(labelPanel, BorderLayout.SOUTH);
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while drawing FSM: " + e);
			e.printStackTrace();
		}
	}
	
}
