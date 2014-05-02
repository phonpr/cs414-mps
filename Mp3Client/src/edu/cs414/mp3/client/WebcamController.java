package edu.cs414.mp3.client;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;

import edu.cs414.mp3.client.network.WebcamConnection;

public class WebcamController implements Controller, Runnable {
	
	private Pipeline videoPipeline;
	private VideoWindow videoWindow;
	private WebcamConnection webcamConnection;
	
	@Override
	public void run() {
		// connect to server and start playing
		webcamConnection = new WebcamConnection();
		webcamConnection.onPlay();
		
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
		
		videoPipeline.setState(State.NULL);
		
		videoWindow.dispose();
		videoWindow = null;
	}

	@Override
	public void onPause() {
		System.out.println("[WebcamController] onPause()");
		
		webcamConnection.onPause();
		videoPipeline.setState(State.PAUSED);
	}

	@Override
	public void onResume() {
		System.out.println("[WebcamController] onResume()");
		
		webcamConnection.onResume();
		videoPipeline.setState(State.PLAYING);
	}

	@Override
	public void onMute() {
		System.out.println("[WebcamController] onMute()");
		
		// mute is controlled under client side
	}

}
