package edu.cs414.mp3.client;

public interface Controller {
	public void onPlay();
	public void onStop();
	public void onPause();
	public void onResume();
	public void onMute();
}
