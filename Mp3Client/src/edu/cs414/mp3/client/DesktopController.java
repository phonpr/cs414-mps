package edu.cs414.mp3.client;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;

import edu.cs414.mp3.client.network.DesktopConnection;

public class DesktopController implements Controller, Runnable {
	
	private Pipeline videoPipeline;
	private VideoWindow videoWindow;
	private DesktopConnection desktopConnection;
	
	@Override
	public void run() {
		// connect to server and start playing
		desktopConnection = new DesktopConnection();
		desktopConnection.onPlay();
		
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
		System.out.println("[DesktopController] onPlay()");
		
		videoWindow = new VideoWindow();
		videoWindow.initializeComponents();
		
		// this will start connection to the server, and play right away
		new Thread(this).start();
	}

	@Override
	public void onStop() {
		System.out.println("[DesktopController] onStop()");
		
		videoPipeline.setState(State.NULL);
		
		videoWindow.dispose();
		videoWindow = null;
	}

	@Override
	public void onPause() {
		System.out.println("[DesktopController] onPause()");
		
		desktopConnection.onPause();
		videoPipeline.setState(State.PAUSED);
	}

	@Override
	public void onResume() {
		System.out.println("[DesktopController] onResume()");
		
		desktopConnection.onResume();
		videoPipeline.setState(State.PLAYING);
	}

	@Override
	public void onMute() {
		System.out.println("[DesktopController] onMute()");
		
		// mute is controlled under client side
	}

}
