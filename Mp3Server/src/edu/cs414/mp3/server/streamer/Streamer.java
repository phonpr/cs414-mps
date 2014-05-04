package edu.cs414.mp3.server.streamer;

public abstract class Streamer implements Runnable {

	@Override
	public void run() {

	}
	
	public abstract void init(String host);
	

}
