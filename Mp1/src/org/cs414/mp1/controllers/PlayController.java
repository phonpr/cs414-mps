package org.cs414.mp1.controllers;

import org.cs414.mp1.views.FrameVideo;
import org.gstreamer.*;
import org.gstreamer.elements.FileSrc;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.event.SeekEvent;
import org.gstreamer.message.ErrorMessage;
import org.gstreamer.message.GErrorMessage;
import org.gstreamer.message.InfoMessage;
import org.gstreamer.message.WarningMessage;
import org.gstreamer.swing.VideoComponent;

import java.io.File;

import javax.swing.SwingUtilities;

public class PlayController extends Controller
{
	
	private enum PlayType {
		NORMAL	,
		FF		,
		RW		,
	}
	
	private PlayType ePlayType = PlayType.NORMAL;

	private PlayBin2 bin;

	public PlayController(File file) {
		super(file);
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

				bin = new PlayBin2("bin");
				bin.setInputFile(getFile());
				bin.setVideoSink(videoComponent.getElement());
				bin.setState(State.PLAYING);
				/*
				//AVI playback
				if("avi".equals(fileExtension)) {
					final Element filter = ElementFactory.make("capsfilter", "filter");
					filter.setCaps(Caps.fromString("image/jpeg, framerate=10/1"));

					final Element parser = ElementFactory.make("jpegparse", "parser");
					final Element decoder = ElementFactory.make("jpegdec", "decoder");

					final Element colorSpace = ElementFactory.make("ffmpegcolorspace", null);

					videoSink = videoComponent.getElement();

					getVideoPipe().addMany(fileSrc, filter, parser, decoder, colorSpace, videoSink);
					Element.linkMany(fileSrc, filter, parser, decoder,  colorSpace, videoSink);
				}
				else if("mp4".equals(fileExtension)) {

					System.out.println("MP4");

					final Element filter = ElementFactory.make("capsfilter", "filter");
					filter.setCaps(Caps.fromString("video/mp4, framerate=10/1"));

					final Element parser = ElementFactory.make("jpegparse", "parser");
					final Element decoder = ElementFactory.make("ffdec_mpeg4", "decoder");

					final Element colorSpace = ElementFactory.make("ffmpegcolorspace", null);

					videoSink = videoComponent.getElement();

					getVideoPipe().addMany(fileSrc, filter, decoder, colorSpace, videoSink);
					Element.linkMany(fileSrc, filter, decoder, colorSpace, videoSink);
				}
				else if("pcm".equals(fileExtension)) {
				}
				else if("ogg".equals(fileExtension)) {
				}
				else {
				}
				*/
			}
		});
	}
	
	public void togglePause() {
		if (bin.getState() == State.PAUSED) {
			// when resuming pased, set to normal playing
			ePlayType = PlayType.NORMAL;
			setNewRate(1.0);
			bin.setState(State.PLAYING);
		}
		else {
			bin.setState(State.PAUSED);
		}
	}
	
	public void toggleFF()
	{
		if (ePlayType == PlayType.FF) {
			ePlayType = PlayType.NORMAL;
			setNewRate(1.0);
		}
		else {
			// case NORMAL or RW
			ePlayType = PlayType.FF;
			setNewRate(2.0);
		}
	}

	public void toggleRW() {
		if (ePlayType == PlayType.RW) {
			ePlayType = PlayType.NORMAL;
			setNewRate(1.0);
		}
		else {
			// case NORMAL or FF
			ePlayType = PlayType.RW;
			setNewRate(-1.0);
		}
	}

	/**
	 * Sets the rate of the playback to the specified rate
	 */
	private void setNewRate(double newRate) {

		System.out.println("SETTING NEW RATE: " + newRate);

		bin.setState(State.PAUSED);
		long currentTime = bin.queryPosition(Format.TIME);
		bin.setState(State.PLAYING);

		if(newRate >= 0.0) {
			bin.seek(newRate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, currentTime, SeekType.SET, -1);
		}
		else {
			bin.seek(newRate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, 0, SeekType.SET, currentTime);
		}

	}

}
