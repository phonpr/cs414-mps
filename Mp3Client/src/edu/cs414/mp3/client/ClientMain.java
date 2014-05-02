package edu.cs414.mp3.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gstreamer.Gst;

public class ClientMain {

	private JFrame frmMpClient;
	private JPanel panel;
	private JLabel lblWebcamServer;
	private JLabel lblDesktopServer;
	private JButton btnWebcamPlay;
	private JButton btnWebcamStop;
	private JButton btnDesktopPlay;
	private JButton btnDesktopStop;
	private JButton btnWebcamPause;
	private JButton btnDesktopPause;
	private JButton btnWebcamResume;
	private JButton btnDesktopResume;
	private JCheckBox chckbxWebcamMute;
	private JCheckBox chckbxDesktopMute;
	
	private WebcamController webcamController;
	private DesktopController desktopController;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Gst.init("Client Main", args);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientMain window = new ClientMain();
					window.frmMpClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMpClient = new JFrame();
		frmMpClient.setTitle("Mp3 Client");
		frmMpClient.setBounds(100, 100, 491, 81);
		frmMpClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
		frmMpClient.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel panelWebcam = new JPanel();
		panel.add(panelWebcam);
		panelWebcam.setLayout(new BoxLayout(panelWebcam, BoxLayout.X_AXIS));
		
		lblWebcamServer = new JLabel("Webcam Server");
		
		btnWebcamPlay = new JButton("Play");
		btnWebcamPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				webcamController = new WebcamController();
				webcamController.onPlay();
				btnWebcamPlay.setEnabled(false);
				btnWebcamPause.setEnabled(true);
				btnWebcamStop.setEnabled(true);
			}
		});
		
		btnWebcamPause = new JButton("Pause");
		btnWebcamPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (webcamController != null) {
					webcamController.onPause();
					btnWebcamPause.setEnabled(false);
					btnWebcamResume.setEnabled(true);
				}
			}
		});
		btnWebcamPause.setEnabled(false);
		
		btnWebcamResume = new JButton("Resume");
		btnWebcamResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (webcamController != null) {
					webcamController.onResume();
					btnWebcamPause.setEnabled(true);
					btnWebcamResume.setEnabled(false);
				}
			}
		});
		btnWebcamResume.setEnabled(false);
		
		btnWebcamStop = new JButton("Stop");
		btnWebcamStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (webcamController != null) {
					webcamController.onStop();
					resetWebcamButtons();
				}
			}
		});
		btnWebcamStop.setEnabled(false);
		
		chckbxWebcamMute = new JCheckBox("Mute");
		
		panelWebcam.add(lblWebcamServer);
		panelWebcam.add(btnWebcamPlay);
		panelWebcam.add(btnWebcamPause);
		panelWebcam.add(btnWebcamResume);
		panelWebcam.add(btnWebcamStop);
		panelWebcam.add(chckbxWebcamMute);
		
		JPanel panelDesktop = new JPanel();
		panel.add(panelDesktop);
		panelDesktop.setLayout(new BoxLayout(panelDesktop, BoxLayout.X_AXIS));
		
		lblDesktopServer = new JLabel("Desktop Server");
		
		btnDesktopPlay = new JButton("Play");
		btnDesktopPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desktopController = new DesktopController();
				desktopController.onPlay();
				btnDesktopPlay.setEnabled(false);
				btnDesktopPause.setEnabled(true);
				btnDesktopStop.setEnabled(true);
			}
		});
		
		btnDesktopPause = new JButton("Pause");
		btnDesktopPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (desktopController != null) {
					desktopController.onPause();
					btnDesktopPause.setEnabled(false);
					btnDesktopResume.setEnabled(true);
				}
			}
		});
		
		btnDesktopResume = new JButton("Resume");
		btnDesktopResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (desktopController != null) {
					desktopController.onResume();
					btnDesktopPause.setEnabled(true);
					btnDesktopResume.setEnabled(false);
				}
			}
		});
		
		btnDesktopStop = new JButton("Stop");
		btnDesktopStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (desktopController != null) {
					desktopController.onStop();
					resetDesktopButtons();
				}
			}
		});
		
		chckbxDesktopMute = new JCheckBox("Mute");

		panelDesktop.add(lblDesktopServer);
		panelDesktop.add(btnDesktopPlay);
		panelDesktop.add(btnDesktopPause);
		panelDesktop.add(btnDesktopResume);
		panelDesktop.add(btnDesktopStop);
		panelDesktop.add(chckbxDesktopMute);
		
		resetDesktopButtons();
	}
	
	private void resetWebcamButtons() {
		btnWebcamPlay.setEnabled(true);
		btnWebcamPause.setEnabled(false);
		btnWebcamResume.setEnabled(false);
		btnWebcamStop.setEnabled(false);
	}

	private void resetDesktopButtons() {
		btnDesktopPlay.setEnabled(true);
		btnDesktopPause.setEnabled(false);
		btnDesktopResume.setEnabled(false);
		btnDesktopStop.setEnabled(false);
	}
}
