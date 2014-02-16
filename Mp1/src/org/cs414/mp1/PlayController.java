package org.cs414.mp1;

import org.gstreamer.*;
import org.gstreamer.elements.FileSrc;
import org.gstreamer.event.SeekEvent;
import org.gstreamer.swing.VideoComponent;

import java.io.File;

public class PlayController extends Controller
{

	private File playFile;
	private Thread playThread;

	public PlayController(Pipeline pipeline, VideoComponent videoComponent)
	{
		super(pipeline, videoComponent);
	}

	private class PlayerThread implements Runnable
	{
		@Override
		public void run()
		{
			final FileSrc fileSrc = new FileSrc("fileSrc");
			fileSrc.setLocation(playFile);

			final Element filter = ElementFactory.make("capsfilter", "filter");
			filter.setCaps(Caps.fromString("image/jpeg, framerate=10/1"));

			final Element parser = ElementFactory.make("jpegparse", "parser");
			final Element decoder = ElementFactory.make("jpegdec", "decoder");

			final Element colorSpace = ElementFactory.make("ffmpegcolorspace", null);

			getPipeline().addMany(fileSrc, filter, parser, decoder, colorSpace, getVideoComponent().getElement());
			Element.linkMany(fileSrc, filter, parser, decoder,  colorSpace, getVideoComponent().getElement());

			getPipeline().setState(State.NULL);
		}
	}

	public void setFile(File newFile)
	{
		playFile = newFile;
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

}
