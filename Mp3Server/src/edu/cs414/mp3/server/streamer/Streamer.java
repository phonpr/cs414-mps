package edu.cs414.mp3.server.streamer;

import org.gstreamer.Pipeline;
import org.gstreamer.State;

public class Streamer implements Runnable {
	
	private static final int AUDIO_BANDWIDTH	= 8000;
	private static final int VIDEO_BANDWIDTH_L	= 61988;
	private static final int VIDEO_BANDWIDTH_S	= 17158;

	protected Pipeline pipe;
	
	@Override
	public void run() {
	}
	
	public void tearDown() {
		pipe.setState(State.READY);
		pipe.setState(State.NULL);
		pipe.dispose();
		pipe = null;
	}
	
	
	public void init(String host){}
	
	public void changeFramerate(int frames){}

	public void onServerResourceChanged(long currentResource) {
		int newFramerate = calcFramerate(currentResource, 0);
		changeFramerate(newFramerate);
	}

	public void onStop() {
		pipe.setState(State.NULL);
		pipe.dispose();
	};
	
	protected int calcFramerate(long band, int size) {
		int vidband = 0;
		if (size == 1)
			vidband = VIDEO_BANDWIDTH_L;
		else
			vidband = VIDEO_BANDWIDTH_S;
		
		// (availiable - audio) / video gives frames/sec
		int framerate = (int) ((band - AUDIO_BANDWIDTH) / vidband);
		if (framerate >= 25)
			return 25;
		else if (framerate <= 15)
			return 15;
		else
			return framerate;
	}
}
