package org.cs414.mp2.server;


import org.gstreamer.Gst;

public class MediaThread implements Runnable {

	Server server;
	int framerate;
	int size;
	
	public MediaThread(int frames, int vidSize) {
		framerate = frames;
		size = vidSize;
	}

	public void stop() {

	}

	public void pause() {
		server.togglePause();
	}

	public void resume() {

	}

	public void rw() {
		server.toggleRW();
	}

	public void ff() {
		server.toggleFF();
	}

	public void activeMode() {
		
	}

	public void passiveMode() {
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		server = new Server();
		server.createPipeline(framerate, size);

		server.play();

	}

}