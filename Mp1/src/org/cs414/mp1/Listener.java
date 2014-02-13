package org.cs414.mp1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.gstreamer.Pipeline;
import org.gstreamer.swing.VideoComponent;

public class Listener implements ActionListener {
	
	public static final String ACTION_RECORD = "record";
	public static final String ACTION_PLAY = "play";
	public static final String ACTION_FF = "ff";
	
	private RecordController recorder = null;
	private PlayController player = null;
	private Monitor monitor = null;
	
	public Listener(Pipeline pipeline, VideoComponent videoComponent) {
		recorder = new RecordController(pipeline, videoComponent);
		player = new PlayController(pipeline, videoComponent);
		monitor = new Monitor(pipeline, videoComponent);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action == ACTION_RECORD) {
			System.out.println("Record button pressed");
			recorder.startRecording();
		}
		else if (action == ACTION_PLAY) {
			System.out.println("Play button pressed");
			player.startPlaying();
		}
		else if (action == ACTION_FF) {
			System.out.println("FF button pressed");
			player.startFastForward();
		}
	}

}
