package org.cs414.mp1.controllers;

import java.io.File;

import org.cs414.mp1.frames.FrameVideo;
import org.gstreamer.Pipeline;
import org.gstreamer.State;

public class Controller {
	public static final String DEFAULT_CAPS_FILTER
		= "video/x-raw-yuv, width=640, height=480, framerate=10/1";
	
	public static final String WINDOWS_WEBCAM_SRC = "ksvideosrc";
	public static final String LINUX_WBCAM_SRC = "v4l2src";
	public static final String VIDEO_TEST_SRC = "videotestsrc";

	// video components
	private FrameVideo frameVideo = null;
	private Pipeline pipeline = null;
	private File file = null;
	
	public Controller(File file) {
		frameVideo = new FrameVideo();
		pipeline = new Pipeline();
		this.file = file;
		
		frameVideo.initializeComponents();
	}
	
	public Pipeline getPipeline() {
		return pipeline;
	}
	
	public FrameVideo getFrameVideo() {
		return frameVideo;
	}
	
	public File getFile() {
		return file;
	}

	public void setState(State state) {
		pipeline.setState(state);
	}
	
	public void startRunning() {
		setState(State.PLAYING);
	}
	
	public void pauseRunning() {
		setState(State.PAUSED);
	}
	
	public void stopRunning() {
		setState(State.NULL);
		frameVideo.dispose();
	}
	
	public boolean isRunning() {
		return (pipeline.getState() == State.PLAYING);
	}
}