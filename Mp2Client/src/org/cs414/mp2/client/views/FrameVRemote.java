package org.cs414.mp2.client.views;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cs414.mp2.client.controllers.Listener;

public class FrameVRemote extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1372538832916412583L;

	// constants
	private static final String FRAME_TITLE = "V-Remote";
	
	private static final int FRAME_X = 100;
	private static final int FRAME_Y = 100;
	private static final int FRAME_WIDTH = 400;
	private static final int FRAME_HEIGHT = 150;
	
	private static final String BUTTON_PLAY = "Play";
	private static final String BUTTON_RECORD = "Record";
	private static final String BUTTON_STOP = "Stop";
	private static final String BUTTON_PAUSE = "Pause";
	private static final String BUTTON_FF = "FF";
	private static final String BUTTON_RW = "RW";
	private static final String LABEL_FILE = "File";

	// UI components
	private JPanel panelPlayRecord = null;
	private JPanel panelControl = null;
	private JPanel panelFile = null;
	
	private JButton btnPlay = null;
	private JButton btnRecord = null;
	private JButton btnStop = null;
	private JButton btnPause = null;
	private JButton btnFF = null;
	private JButton btnRW = null;
	
	private JTextField textFilePath = null;

	/**
	 * Initialize the contents of the frame.
	 */
	public void initializeComponents() {
		panelPlayRecord = new JPanel();
		panelControl = new JPanel();
		panelFile = new JPanel();
		
		// main frame
		setTitle(FRAME_TITLE);
		setBounds(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(
			new BoxLayout(getContentPane(), BoxLayout.Y_AXIS)
		);
		setResizable(false);
		getContentPane().add(panelPlayRecord);
		getContentPane().add(panelControl);
		getContentPane().add(panelFile);
		
		// PlayRecord Panel
		btnPlay = new JButton(BUTTON_PLAY);
		btnRecord = new JButton(BUTTON_RECORD);
		btnStop = new JButton(BUTTON_STOP);
		panelPlayRecord.add(btnPlay);
		panelPlayRecord.add(btnRecord);
		panelPlayRecord.add(btnStop);
		
		// Control Panel
		btnPause = new JButton(BUTTON_PAUSE);
		btnFF = new JButton(BUTTON_FF);
		btnRW = new JButton(BUTTON_RW);
		panelControl.add(btnPause);
		panelControl.add(btnFF);
		panelControl.add(btnRW);
		
		// File Panel
		JLabel labelFile = new JLabel(LABEL_FILE);
		textFilePath = new JTextField();
		panelFile.add(labelFile);
		panelFile.add(textFilePath);
		textFilePath.setColumns(28);
		textFilePath.setEditable(false);
		
		// initial disables
		enableCommonControllers(false);
		enablePlayControllers(false);
	}
	
	public void initializeListener(ActionListener listener) {
		
		// PlayRecordPanel listener
		btnPlay.addActionListener(listener);
		btnPlay.setActionCommand(Listener.ACTION_PLAY);
		btnRecord.addActionListener(listener);
		btnRecord.setActionCommand(Listener.ACTION_RECORD);
		btnStop.addActionListener(listener);
		btnStop.setActionCommand(Listener.ACTION_STOP);
		
		// ControlPanel listener
		btnPause.addActionListener(listener);
		btnPause.setActionCommand(Listener.ACTION_PAUSE);
		btnFF.addActionListener(listener);
		btnFF.setActionCommand(Listener.ACTION_FF);
		btnRW.addActionListener(listener);
		btnRW.setActionCommand(Listener.ACTION_RW);
	}
	
	public void enablePlayRecord(boolean enable) {
		btnPlay.setEnabled(enable);
		btnRecord.setEnabled(enable);
	}
	
	public void enableCommonControllers(boolean enable) {
		btnStop.setEnabled(enable);
	}
	
	public void enablePlayControllers(boolean enable) {
		btnPause.setEnabled(enable);
		btnFF.setEnabled(enable);
		btnRW.setEnabled(enable);
	}

	public void setRecording(String path) {
		textFilePath.setText(path);
		enablePlayRecord(false);
		enableCommonControllers(true);
	}
	
	public void setPlaying(String path) {
		textFilePath.setText(path);
		enablePlayRecord(false);
		enableCommonControllers(true);
		enablePlayControllers(true);
	}

	public void resetComponents() {
		enablePlayRecord(true);
		enableCommonControllers(false);
		enablePlayControllers(false);
		textFilePath.setText(null);
		resetFFRW();
	}
	
	public void resetFFRW() {
		btnFF.setForeground(null);
		btnRW.setForeground(null);
	}
	
	public void togglePause() {
		if (btnPause.getForeground() == Color.RED) {
			btnPause.setForeground(null);
		}
		else {
			btnPause.setForeground(Color.RED);
			btnFF.setForeground(null);
			btnRW.setForeground(null);
		}
	}

	public void toggleFF() {
		if (btnFF.getForeground() == Color.RED) {
			btnFF.setForeground(null);
		}
		else {
			btnFF.setForeground(Color.RED);
			btnPause.setForeground(null);
			btnRW.setForeground(null);
		}
	}

	public void toggleRW() {
		if (btnRW.getForeground() == Color.RED) {
			btnRW.setForeground(null);
		}
		else {
			btnRW.setForeground(Color.RED);
			btnPause.setForeground(null);
			btnFF.setForeground(null);
		}
	}
}
