package org.cs414.mp1;

import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;

public class Controller {
	public static final String DEFAULT_CAPS_FILTER
		= "video/x-raw-yuv, width=640, height=480, framerate=10/1";
	
	public static final String WINDOWS_WEBCAM_SRC = "ksvideosrc";
	public static final String LINUX_WBCAM_SRC = "v4l2src";

	private Pipeline pipeline = null;
	private VideoComponent videoComponent = null;
	
	public Controller(Pipeline pipeline, VideoComponent videoComponent) {
		this.pipeline = pipeline;
		this.videoComponent = videoComponent;
	}
	
	public Pipeline getPipeline() {
		return pipeline;
	}
	
	public VideoComponent getVideoComponent() {
		return videoComponent;
	}
	
	public void setState(State state) {
		pipeline.setState(state);
	}
}
