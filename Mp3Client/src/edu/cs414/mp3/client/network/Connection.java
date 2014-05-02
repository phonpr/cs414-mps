package edu.cs414.mp3.client.network;

public abstract class Connection {
	public abstract void onPlay();
	public abstract void onPause();
	public abstract void onResume();
	public abstract void onStop();
}
