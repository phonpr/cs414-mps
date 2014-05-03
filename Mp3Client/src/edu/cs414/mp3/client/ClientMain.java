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

import edu.cs414.mp3.client.controller.DesktopController;
import edu.cs414.mp3.client.controller.WebcamController;

public class ClientMain {

	private JFrame frmMpClient;
	private JPanel panel;
	
	private JLabel lblWebcamServer;
	private JLabel lblDesktopServer;
	private JButton btnWebcamPlay;
	private JButton btnWebcamPause;
	private JButton btnWebcamResume;
	private JButton btnWebcamStop;
	private JCheckBox chckbxWebcamMute;
	
	private JButton btnDesktopPlay;
	private JButton btnDesktopPause;
	private JButton btnDesktopResume;
	private JButton btnDesktopStop;
	private JCheckBox chckbxDesktopMute;
	
	private ButtonGroup webcamButtonGroup;
	private WebcamController webcamController;
	
	private ButtonGroup desktopButtonGroup;
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
		initializeButtonGroups();
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
				webcamController = new WebcamController(webcamButtonGroup);
				webcamController.onPlay();
			}
		});
		
		btnWebcamPause = new JButton("Pause");
		btnWebcamPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (webcamController != null) {
					webcamController.onPause();
				}
			}
		});
		
		btnWebcamResume = new JButton("Resume");
		btnWebcamResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (webcamController != null) {
					webcamController.onResume();
				}
			}
		});
		
		btnWebcamStop = new JButton("Stop");
		btnWebcamStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (webcamController != null) {
					webcamController.onStop();
				}
			}
		});
		
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
				desktopController = new DesktopController(desktopButtonGroup);
				desktopController.onPlay();
			}
		});
		
		btnDesktopPause = new JButton("Pause");
		btnDesktopPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (desktopController != null) {
					desktopController.onPause();
				}
			}
		});
		
		btnDesktopResume = new JButton("Resume");
		btnDesktopResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (desktopController != null) {
					desktopController.onResume();
				}
			}
		});
		
		btnDesktopStop = new JButton("Stop");
		btnDesktopStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (desktopController != null) {
					desktopController.onStop();
				}
			}
		});
		
		chckbxDesktopMute = new JCheckBox("Mute");
		chckbxDesktopMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (desktopController != null) {
					desktopController.onMute();
				}
			}
		});

		panelDesktop.add(lblDesktopServer);
		panelDesktop.add(btnDesktopPlay);
		panelDesktop.add(btnDesktopPause);
		panelDesktop.add(btnDesktopResume);
		panelDesktop.add(btnDesktopStop);
		panelDesktop.add(chckbxDesktopMute);
	}
	
	public void initializeButtonGroups() {
		webcamButtonGroup = new ButtonGroup();
		webcamButtonGroup.setBtnPlay(btnWebcamPlay);
		webcamButtonGroup.setBtnPause(btnWebcamPause);
		webcamButtonGroup.setBtnResume(btnWebcamResume);
		webcamButtonGroup.setBtnStop(btnWebcamStop);
		webcamButtonGroup.setChkMute(chckbxWebcamMute);
		
		desktopButtonGroup = new ButtonGroup();
		desktopButtonGroup.setBtnPlay(btnDesktopPlay);
		desktopButtonGroup.setBtnPause(btnDesktopPause);
		desktopButtonGroup.setBtnResume(btnDesktopResume);
		desktopButtonGroup.setBtnStop(btnDesktopStop);
		desktopButtonGroup.setChkMute(chckbxDesktopMute);
		
		webcamButtonGroup.onReset();
		desktopButtonGroup.onReset();
	}
}
