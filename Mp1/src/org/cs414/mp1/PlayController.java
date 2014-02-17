package org.cs414.mp1;

import org.gstreamer.*;
import org.gstreamer.elements.FileSrc;
import org.gstreamer.event.SeekEvent;
import org.gstreamer.swing.VideoComponent;

import java.io.File;

import javax.swing.SwingUtilities;

public class PlayController extends Controller
{
	public PlayController(File file) {
		super(file);
	}

	public void startPlaying()
	{
		SeekEvent normalSpeed = new SeekEvent(1.0, Format.DEFAULT, SeekFlags.SKIP, SeekType.NONE, 0, SeekType.NONE, 0);
		getPipeline().sendEvent(normalSpeed);
	}

	public void startFastForward()
	{
		SeekEvent fastForward = new SeekEvent(2.0, Format.DEFAULT, SeekFlags.SKIP, SeekType.NONE, 0, SeekType.NONE, 0);
		getPipeline().sendEvent(fastForward);
	}

	public void startRunning() {
		final FrameVideo frameVideo = getFrameVideo();
		final VideoComponent videoComponent = frameVideo.getVideoComponent();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getFrameVideo().setVisible(true);
				
				final FileSrc fileSrc = new FileSrc("fileSrc");
				fileSrc.setLocation(getFile());

				final Element filter = ElementFactory.make("capsfilter", "filter");
				filter.setCaps(Caps.fromString("image/jpeg, framerate=10/1"));

				final Element parser = ElementFactory.make("jpegparse", "parser");
				final Element decoder = ElementFactory.make("jpegdec", "decoder");

				final Element colorSpace = ElementFactory.make("ffmpegcolorspace", null);

				getPipeline().addMany(fileSrc, filter, parser, decoder, colorSpace, videoComponent.getElement());
				Element.linkMany(fileSrc, filter, parser, decoder,  colorSpace, videoComponent.getElement());

				setState(State.PLAYING);
			}
		});
	}

	public void stopRunning() {
		super.stopRunning();
	}

}
