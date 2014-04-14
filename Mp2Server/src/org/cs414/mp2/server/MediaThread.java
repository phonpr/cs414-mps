package org.cs414.mp2.server;


import org.gstreamer.Gst;

public class MediaThread implements Runnable {
	
	public void stop() {
		
	}

	public void pause() {
		
	}

	public void resume() {
		
	}

	public void rw() {
		
	}

	public void ff() {
		
	}

	public void activeMode() {
		
	}

	public void passiveMode() {
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Server server = new Server();
		server.createPipeline();

		server.play();

	}

}