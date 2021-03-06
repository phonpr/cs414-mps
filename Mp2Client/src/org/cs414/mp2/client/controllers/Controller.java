package org.cs414.mp2.client.controllers;

import java.io.File;

import org.cs414.mp2.client.views.FrameVideo;
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
	
	public enum OperationType {
		RECORDING	,
		PLAYING		,
	}

	// constants
	public static final String DEFAULT_CAPS_FILTER
		= "video/x-raw-yuv, width=640, height=480, framerate=10/1";
	
	public static final String WINDOWS_WEBCAM_SRC = "ksvideosrc";
	public static final String LINUX_WBCAM_SRC = "v4l2src";
	public static final String VIDEO_TEST_SRC = "videotestsrc";
	public static final String LINUX_AUDIO_SRC = "pulsesrc";
	public static final String AUDIO_TEST_SRC = "audiotestsrc";
	
	public static int VIDEO_DEFAULT_WIDTH = 640;
	public static int VIDEO_DEFAULT_HEIGHT = 480;
	public static int VIDEO_DEFAULT_RATE = 10;
	public static int AUDIO_DEFAULT_RATE = 44100;

	// video components
	private FrameVideo frameVideo = null;
	private Pipeline videoPipe = null;
	private File file = null;
	
	// audio components
	private Pipeline audioPipe = null;
	
	public Controller(OperationType operation) {
		frameVideo = new FrameVideo(operation);
		videoPipe = new Pipeline();
		audioPipe = new Pipeline();

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