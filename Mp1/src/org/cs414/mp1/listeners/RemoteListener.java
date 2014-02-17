package org.cs414.mp1.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import org.cs414.mp1.controllers.Controller;
import org.cs414.mp1.controllers.PlayController;
import org.cs414.mp1.controllers.RecordController;
import org.cs414.mp1.frames.FrameVRemote;

public class RemoteListener implements ActionListener {
	
	public static final String ACTION_PLAY = "play";
	public static final String ACTION_RECORD = "record";
	
	public static final String ACTION_STOP = "stop";
	public static final String ACTION_PAUSE = "pause";
	public static final String ACTION_FF = "ff";
	public static final String ACTION_RW = "rw";
	
	// frames
	private FrameVRemote frameRemote = null;
	
	// controller
	private Controller controller = null;
	
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
				controller = new PlayController(file);
				controller.startRunning();
			}
		}
		else if (action == ACTION_RECORD) {
			final JFileChooser fileChooser = new JFileChooser();
			int nReturn = fileChooser.showSaveDialog(frameRemote);
			if (nReturn == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				
				frameRemote.setRecording(file.getPath());
				controller = new RecordController(file);
				controller.startRunning();
			}
		}
		else if (action == ACTION_STOP) {
			if (controller != null) {
				controller.stopRunning();
				controller = null;
			}
			frameRemote.resetComponents();
		}
		else if (action == ACTION_PAUSE) {
			if (controller != null) {
				controller.pauseRunning();
				controller = null;
			}
		}
		else if (action == ACTION_FF) {
			// this is activated only when controller is PlayController
			((PlayController) controller).toggleFastForward();
			frameRemote.toggleFF();
		}
		else if (action == ACTION_RW) {
			// this is activated only when controller is PlayController
			((PlayController) controller).toggleRewind();
			frameRemote.toggleRW();
		}
		else ;
	}

}
