package org.cs414.mp1.controllers;

import org.cs414.mp1.views.FrameVideo;
import org.gstreamer.*;
import org.gstreamer.elements.FileSrc;
import org.gstreamer.event.SeekEvent;
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
	
	public PlayController(File file) {
		super(file);
	}

	private class Monitor implements Runnable {
		@Override
		public void run() {
			while(true) {
				System.out.println(getVideoPipe().getState(1000L));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					System.out.println("INTERRUPTED");
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}
		}
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

				getVideoPipe().addMany(fileSrc, filter, parser, decoder, colorSpace, videoComponent.getElement());
				Element.linkMany(fileSrc, filter, parser, decoder,  colorSpace, videoComponent.getElement());

				setVideoState(State.PLAYING);
				getVideoPipe().getBus().connect(new Bus.MESSAGE() {
					@Override
					public void busMessage(Bus bus, Message message) {
						System.out.println("GOT MESSAGE: " + message.toString());
					}
				});

				new Thread(new Monitor()).start();
			}
		});
	}
	
	public void togglePause() {
		if (getVideoPipe().getState() == State.PAUSED) {
			// when resuming pased, set to normal playing
			ePlayType = PlayType.NORMAL;
			setNewRate(1.0);
			setVideoState(State.PLAYING);
		}
		else {
			setVideoState(State.PAUSED);
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

		getVideoPipe().setState(State.PLAYING);
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
		getVideoPipe().setState(State.PLAYING);
	}

	/**
	 * Sets the rate of the playback to the specified rate
	 */
	private void setNewRate(double newRate) {

		System.out.println("SETTING NEW RATE: " + newRate);

		getVideoPipe().setState(State.PAUSED);
		long currentTime = getVideoPipe().queryPosition(Format.TIME);
		SeekEvent seekEvent = new SeekEvent(newRate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, currentTime, SeekType.NONE, -1);
		getVideoPipe().seek(newRate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, currentTime, SeekType.NONE, -1);
		//getVideoPipe().sendEvent(seekEvent);
		getVideoPipe().setState(State.PLAYING);

		//long currentTime = getVideoPipe().queryPosition(Format.TIME);
		//
	}

}
