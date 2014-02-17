package org.cs414.mp1.controllers;

import java.io.File;

import javax.swing.SwingUtilities;

import org.cs414.mp1.frames.FrameVideo;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;

public class RecordController extends Controller {
	
	public RecordController(File file) {
		super(file);
	}

	public void startRunning() {
		final FrameVideo frameVideo = getFrameVideo();
		final VideoComponent videoComponent = frameVideo.getVideoComponent();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getFrameVideo().setVisible(true);
				
				Element videosrc = ElementFactory.make(VIDEO_TEST_SRC, "source");
				Element tee = ElementFactory.make("tee", "tee0");
				
				Element colorspace = ElementFactory.make("ffmpegcolorspace", "colorspace");
				Element videofilter = ElementFactory.make("capsfilter", "flt");
				
				videofilter.setCaps(Caps.fromString(DEFAULT_CAPS_FILTER));
				
				Element enc = ElementFactory.make("jpegenc", "Encoder");
				Element mux = ElementFactory.make("avimux", "muxer");
				Element fileOut = ElementFactory.make("filesink", "File sink");
				
				fileOut.set("location", getFile().getPath());
				
				Element videoSink = videoComponent.getElement();
				getPipeline().addMany(videosrc, tee, colorspace, videofilter, enc, mux, fileOut, videoSink);
				
				videosrc.link(tee);
				tee.link(colorspace);
				tee.link(videoSink);
				
				colorspace.link(videofilter, enc, mux, fileOut);
				
				setState(State.PLAYING);
			}
		});
	}
	
	public void stopRunning() {
		super.stopRunning();
	}
	
	public void togglePause() {
		//super.pauseRunning();
	}
}
