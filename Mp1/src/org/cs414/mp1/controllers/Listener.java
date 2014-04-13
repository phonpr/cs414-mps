package org.cs414.mp1.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import org.cs414.mp1.controllers.Controller.AudioType;
import org.cs414.mp1.controllers.Controller.VideoType;
import org.cs414.mp1.views.DialogRecordOptions;
import org.cs414.mp1.views.FrameVRemote;

public class Listener implements ActionListener {
	
	public static final String ACTION_PLAY = "play";
	public static final String ACTION_RECORD = "record";
	
	public static final String ACTION_STOP = "stop";
	public static final String ACTION_PAUSE = "pause";
	public static final String ACTION_FF = "ff";
	public static final String ACTION_RW = "rw";
	
	// frames
	private FrameVRemote frameRemote = null;
	
	// dialog
	private DialogRecordOptions dialogRecordOptions = null;
	
	// controller
	private Controller controller = null;
	
	public Listener(FrameVRemote frameRemote) {
		this.frameRemote = frameRemote;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action == ACTION_PLAY) {
				frameRemote.setPlaying("temp");
				controller = new PlayController();
				controller.startRunning();
		}
		else if (action == ACTION_RECORD) {
			dialogRecordOptions = new DialogRecordOptions(frameRemote);
			dialogRecordOptions.showOpenDialog();
			if (dialogRecordOptions.isStartRecording()) {
				int width = dialogRecordOptions.getVideoWidth();
				int height = dialogRecordOptions.getVideoHeight();
				int frameRate = dialogRecordOptions.getFrameRate();
				int samplingRate = dialogRecordOptions.getSamplingRate();
				VideoType videoType = dialogRecordOptions.getVideoType();
				AudioType audioType = dialogRecordOptions.getAudioType();
				
				final JFileChooser fileChooser = new JFileChooser();
				int nReturn = fileChooser.showSaveDialog(frameRemote);
				if (nReturn == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					frameRemote.setRecording(file.getPath());
					controller = new RecordController(
						file, width, height, frameRate, samplingRate, videoType, audioType);
					controller.startRunning();
				}
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
			// this is activated only when controller is PlayController
			if (controller != null) {
				((PlayController) controller).togglePause();
				frameRemote.togglePause();
			}
		}
		else if (action == ACTION_FF) {
			// this is activated only when controller is PlayController
			if (controller != null) {
				((PlayController) controller).toggleFF();
				frameRemote.toggleFF();
			}
		}
		else if (action == ACTION_RW) {
			// this is activated only when controller is PlayController
			if (controller != null) {
				((PlayController) controller).toggleRW();
				frameRemote.toggleRW();
			}
		}
		else ;
	}

}
