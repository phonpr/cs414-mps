package edu.cs414.mp3.server.streamer;

import org.gstreamer.Pipeline;
import org.gstreamer.State;

public class Streamer implements Runnable {

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

	public void onServerResourceChanged(long currentResource) {
		
	};
	

}
