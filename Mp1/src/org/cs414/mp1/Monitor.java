package org.cs414.mp1;

import org.gstreamer.Pipeline;
import org.gstreamer.swing.VideoComponent;

public class Monitor {
	
	private Pipeline pipeline = null;

	public Monitor(Pipeline pipeline, VideoComponent videoComponent) {
		this.pipeline = pipeline;
	}

}
