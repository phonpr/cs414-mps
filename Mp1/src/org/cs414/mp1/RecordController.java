package org.cs414.mp1;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;

public class RecordController extends Controller {
	
	public RecordController(Pipeline pipeline, VideoComponent videoComponent) {
		super(pipeline, videoComponent);
	}
	
	private class RecordThread implements Runnable {
		@Override
		public void run() {
			final Element videosrc = ElementFactory.make(WINDOWS_WEBCAM_SRC, "source");
			final Element tee = ElementFactory.make("tee", "tee0");
			
			final Element colorspace = ElementFactory.make("ffmpegcolorspace", "colorspace");
			final Element videofilter = ElementFactory.make("capsfilter", "flt");
			
			videofilter.setCaps(Caps.fromString(DEFAULT_CAPS_FILTER));
			
			final Element enc = ElementFactory.make("jpegenc", "Encoder");
			final Element mux = ElementFactory.make("avimux", "muxer");
			final Element fileOut = ElementFactory.make("filesink", "File sink");
			
			fileOut.set("location", "./test.avi");
			
			Element videoSink = getVideoComponent().getElement();
			getPipeline().addMany(videosrc, tee, colorspace, videofilter, enc, mux, fileOut, videoSink);
			
			videosrc.link(tee);
			tee.link(colorspace);
			tee.link(videoSink);
			
			colorspace.link(videofilter, enc, mux, fileOut);
			
			setState(State.PLAYING);
		}
	}

	public void startRecording() {
		new Thread(new RecordThread()).start();
	}

}
