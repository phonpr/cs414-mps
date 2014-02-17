package org.cs414.mp1.controllers;

import java.io.File;

import org.cs414.mp1.views.FrameVideo;
import org.gstreamer.Pipeline;
import org.gstreamer.State;

public class Controller {
	
	public enum VideoType {
		RAW		,
		MJPEG	,
		MPEG4
	}

	public enum AudioType {
		RAW		,
		OGG
	}

	// constants
	public static final String DEFAULT_CAPS_FILTER
		= "video/x-raw-yuv, width=640, height=480, framerate=10/1";
	
	public static final String WINDOWS_WEBCAM_SRC = "ksvideosrc";
	public static final String LINUX_WBCAM_SRC = "v4l2src";
	public static final String VIDEO_TEST_SRC = "videotestsrc";
	
	public static int VIDEO_DEFAULT_WIDTH = 640;
	public static int VIDEO_DEFAULT_HEIGHT = 480;
	public static int VIDEO_DEFAULT_RATE =10;

	// video components
	private FrameVideo frameVideo = null;
	private Pipeline videoPipe = null;
	private File file = null;
	
	// audio components
	private Pipeline audioPipe = null;
	
	public Controller(File file) {
		frameVideo = new FrameVideo();
		videoPipe = new Pipeline();
		audioPipe = new Pipeline();
		this.file = file;
		
		frameVideo.initializeComponents();
	}
	
	public Pipeline getVideoPipe() {
		return videoPipe;
	}
	
	public Pipeline getAudioPipe() {
		return audioPipe;
	}
	
	public FrameVideo getFrameVideo() {
		return frameVideo;
	}
	
	public File getFile() {
		return file;
	}

	public void setVideoState(State state) {
		videoPipe.setState(state);
	}
	
	public void setAudioState(State state) {
		audioPipe.setState(state);
	}
	
	public void startRunning() {
		setVideoState(State.PLAYING);
		setAudioState(State.PLAYING);
	}
	
	public void stopRunning() {
		setVideoState(State.NULL);
		setAudioState(State.NULL);
		frameVideo.dispose();
	}
	
	public boolean isRunning() {
		return (videoPipe.getState() == State.PLAYING ||
				audioPipe.getState() == State.PLAYING);
	}
}