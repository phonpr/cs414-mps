package edu.cs414.mp3.client.controller;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;

import edu.cs414.mp3.client.ButtonGroup;
import edu.cs414.mp3.client.VideoWindow;
import edu.cs414.mp3.client.connection.WebcamConnection;

public class WebcamController implements Controller, Runnable {
	
	private ButtonGroup webcamButtonGroup;
	
	private Pipeline videoPipeline;
	private VideoWindow videoWindow;
	private WebcamConnection webcamConnection;
	
	public WebcamController(ButtonGroup webcamButtonGroup) {
		this.webcamButtonGroup = webcamButtonGroup;
	}

	@Override
	public void run() {
		// connect to server and start playing
		webcamConnection = new WebcamConnection();
		if (!webcamConnection.onPlay()) {
			return;
		}
		else {
			webcamButtonGroup.onPlay();
		}
		
		videoPipeline = new Pipeline("VideoTest");
        final Element videosrc = ElementFactory.make("videotestsrc", "source");
        final Element videofilter = ElementFactory.make("capsfilter", "filter");
        videofilter.setCaps(Caps.fromString("video/x-raw-yuv, width=720, height=576" + ", bpp=32, depth=32, framerate=25/1"));
        
        Element videosink = videoWindow.getVideoComponent().getElement();
        videoPipeline.addMany(videosrc, videofilter, videosink);
        Element.linkMany(videosrc, videofilter, videosink);
        
        // Start the pipeline processing
        videoPipeline.setState(State.PLAYING);
		videoWindow.setVisible(true);
	}

	@Override
	public void onPlay() {
		System.out.println("[WebcamController] onPlay()");
		
		videoWindow = new VideoWindow();
		videoWindow.initializeComponents();
		
		// this will start connection to the server, and play right away
		new Thread(this).start();
	}

	@Override
	public void onStop() {
		System.out.println("[WebcamController] onStop()");
		
		// reset and close has priority
		webcamButtonGroup.onReset();
		videoWindow.dispose();
		videoWindow = null;
		
		if (webcamConnection.onStop()) {
			videoPipeline.setState(State.NULL);
		}
	}

	@Override
	public void onPause() {
		System.out.println("[WebcamController] onPause()");
		
		if (webcamConnection.onPause()) {
			webcamButtonGroup.onPause();
			videoPipeline.setState(State.PAUSED);
		}
	}

	@Override
	public void onResume() {
		System.out.println("[WebcamController] onResume()");
		
		if (webcamConnection.onResume()) {
			webcamButtonGroup.onResume();
			videoPipeline.setState(State.PLAYING);
		}
	}

	@Override
	public void onMute() {
		System.out.println("[WebcamController] onMute()");
		
		// mute is controlled under client side
	}

	@Override
	public void onToggleHdMode() {
		System.out.println("[DesktopController] onToggleHdMode()");
		
		if (webcamConnection.isHdMode()) {
			if (webcamConnection.onSdMode()) {
				
			}
		}
		else {
			// currently SD mode
			if (webcamConnection.onHdMode()) {
				
			}
		}
	}

}
