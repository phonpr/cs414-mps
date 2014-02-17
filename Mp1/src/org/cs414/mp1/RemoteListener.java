package org.cs414.mp1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

public class RemoteListener implements ActionListener {
	
	public static final String ACTION_PLAY = "play";
	public static final String ACTION_RECORD = "record";
	
	public static final String ACTION_STOP = "stop";
	public static final String ACTION_PAUSE = "pause";
	public static final String ACTION_FF = "ff";
	public static final String ACTION_RW = "rw";
	
	// frames
	private FrameVRemote frameRemote = null;
	
	// controllers
	private PlayController playController = null;
	private RecordController recordController = null;
	
	public RemoteListener(FrameVRemote frameRemote) {
		this.frameRemote = frameRemote;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action == ACTION_PLAY) {
			final JFileChooser fileChooser = new JFileChooser();
			int nReturn = fileChooser.showOpenDialog(frameRemote);
			if (nReturn == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				
				frameRemote.setPlaying(file.getPath());
				playController = new PlayController(file);
				playController.startRunning();
			}
		}
		else if (action == ACTION_RECORD) {
			final JFileChooser fileChooser = new JFileChooser();
			int nReturn = fileChooser.showSaveDialog(frameRemote);
			if (nReturn == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				
				frameRemote.setRecording(file.getPath());
				recordController = new RecordController(file);
				recordController.startRunning();
			}
		}
		else if (action == ACTION_STOP) {
			if (playController != null) {
				playController.stopRunning();
				playController = null;
			}
			else if (recordController != null) {
				recordController.stopRunning();
				recordController = null;
			}
			else ;
			frameRemote.resetComponents();
		}
		else if (action == ACTION_PAUSE) {
		}
		else if (action == ACTION_FF) {
		}
		else if (action == ACTION_RW) {
		}
		else ;
	}

}
