package org.cs414.mp1;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Mp1 {

	private JFrame frmVRemote;
	private JTextField textFilePath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Mp1 window = new Mp1();
					window.frmVRemote.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Mp1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmVRemote = new JFrame();
		frmVRemote.setTitle("V-Remote");
		frmVRemote.setBounds(100, 100, 394, 149);
		frmVRemote.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmVRemote.getContentPane().setLayout(new BoxLayout(frmVRemote.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel panelPlayRecord = new JPanel();
		frmVRemote.getContentPane().add(panelPlayRecord);
		
		JButton btnPlay = new JButton("Play");
		panelPlayRecord.add(btnPlay);
		
		JButton btnRecord = new JButton("Record");
		panelPlayRecord.add(btnRecord);
		
		JPanel panelControl = new JPanel();
		FlowLayout fl_panelControl = (FlowLayout) panelControl.getLayout();
		frmVRemote.getContentPane().add(panelControl);
		
		JButton btnStop = new JButton("Stop");
		panelControl.add(btnStop);
		
		JButton btnPause = new JButton("Pause");
		panelControl.add(btnPause);
		
		JButton btnFF = new JButton("FF");
		panelControl.add(btnFF);
		
		JButton btnRW = new JButton("RW");
		panelControl.add(btnRW);
		
		JPanel panelFile = new JPanel();
		frmVRemote.getContentPane().add(panelFile);
		
		JLabel labelFile = new JLabel("File");
		panelFile.add(labelFile);
		
		textFilePath = new JTextField();
		panelFile.add(textFilePath);
		textFilePath.setColumns(28);
		btnRW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnFF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
	}

}


/*package org.cs414.mp1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.swing.VideoComponent;

public class Mp1 extends JPanel {
	
	*//**
	 * generated serial version UID
	 *//*
	private static final long serialVersionUID = -2398906894228626286L;
	
	// constants
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 680;
	
	// UI static components
	private static JFrame frameMain = null;
	
	private static JPanel panelMain = null;
	private static JPanel panelMenu = null;
	private static JPanel panelVideo = null;
	
	private static JButton btnRecord = null;
	private static JButton btnPlay = null;
	private static JButton btnFastForward = null;
	
	private static VideoComponent videoComponent = null;
	
	// GStreamer static components
	private static Pipeline pipeline = null;
	
	// Action listener
	private static ActionListener listener = null;
	
	private static void initialize() {
		panelMain = new JPanel();
		panelMenu = new JPanel();
		panelVideo = new JPanel();
		
		btnRecord = new JButton("Record");
		btnPlay = new JButton("Play");
		btnFastForward = new JButton("FF");
		
		videoComponent = new VideoComponent();
		pipeline = new Pipeline("Video");
		listener = new Listener(pipeline, videoComponent);
		
		panelMain.add(panelMenu);
		panelMain.add(panelVideo);
		
		btnRecord.setActionCommand(Listener.ACTION_RECORD);
		btnRecord.addActionListener(listener);
		panelMenu.add(btnRecord);
		
		btnPlay.setActionCommand(Listener.ACTION_PLAY);
		btnPlay.addActionListener(listener);
		panelMenu.add(btnPlay);
		
		btnFastForward.setActionCommand(Listener.ACTION_FF);
		btnFastForward.addActionListener(listener);
		panelMenu.add(btnFastForward);
		
		videoComponent.setPreferredSize(new Dimension(720, 576));
		panelVideo.add(videoComponent, BorderLayout.CENTER);
	}

	private static void createAndShowGUI() {
		// create and set up the window.
		frameMain = new JFrame("HelloWorldSwing");
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frameMain.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frameMain.setResizable(false);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int frameX = (screen.width - FRAME_WIDTH)/2;
		int frameY = (screen.height - FRAME_HEIGHT)/2;
		frameMain.setLocation(frameX, frameY);
		
		// add menu panel
		frameMain.add(panelMain);

		// display the window.
		frameMain.pack();
		frameMain.setVisible(true);
	}
	
	public static void main(String[] args) {
		args = Gst.init("VideoTest", args);
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initialize();
				createAndShowGUI();
			}
		});
	}

}
*/