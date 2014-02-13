package org.cs414.mp1;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Mp1 extends JPanel {
	
	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -2398906894228626286L;
	
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 600;
	
	public static final int BUTTON_WIDTH = 140;
	public static final int BUTTON_HEIGHT = 40;
	public static final int MARGIN_SMALL = 10;
	
	private static ActionListener listener = null;
	
	private static JPanel panelMenu = null;
	
	private static JButton btnRecord = null;
	private static JButton btnPlay = null;
	private static JButton btnFastForward = null;
	
	private static void initialize() {
		listener = new Listener();
		panelMenu = new JPanel();
		btnRecord = new JButton("Record");
		btnPlay = new JButton("Play");
		btnFastForward = new JButton("FF");
		
		btnRecord.setActionCommand(Listener.ACTION_RECORD);
		btnRecord.addActionListener(listener);
		panelMenu.add(btnRecord);
		
		btnPlay.setActionCommand(Listener.ACTION_PLAY);
		btnPlay.addActionListener(listener);
		panelMenu.add(btnPlay);
		
		btnFastForward.setActionCommand(Listener.ACTION_FF);
		btnFastForward.addActionListener(listener);
		panelMenu.add(btnFastForward);
	}

	private static void createAndShowGUI() {
		// create and set up the window.
		JFrame frame = new JFrame("HelloWorldSwing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int frameX = (screen.width - FRAME_WIDTH)/2;
		int frameY = (screen.height - FRAME_HEIGHT)/2;
		frame.setLocation(frameX, frameY);
		
		// add menu panel
		frame.add(panelMenu);

		// display the window.
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		initialize();
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
