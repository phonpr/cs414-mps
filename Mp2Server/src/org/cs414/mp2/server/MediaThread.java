package org.cs414.mp2.server;


import java.net.InetAddress;

public class MediaThread implements Runnable {

	private Server server;
	private int framerate;
	private String hostAddress;
	
	public MediaThread(int frames, String hostAddress) {
		this.framerate = frames;
		this.hostAddress = hostAddress;
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
		server = new Server(hostAddress);
		server.createPipeline(framerate);

		server.play();

	}

}