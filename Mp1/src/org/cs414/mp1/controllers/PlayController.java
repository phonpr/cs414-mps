package org.cs414.mp1.controllers;

import org.cs414.mp1.controllers.Controller.OperationType;
import org.cs414.mp1.views.FrameVideo;
import org.gstreamer.*;
import org.gstreamer.elements.*;
import org.gstreamer.swing.VideoComponent;

import java.io.File;
import java.util.List;

import javax.swing.SwingUtilities;

public class PlayController extends Controller
{
	private long lastTime = 0L;

	public PlayController(File file) {
		super(file, OperationType.PLAYING);
	}

	public void startRunning() {
		final FrameVideo frameVideo = getFrameVideo();
		final VideoComponent videoComponent = frameVideo.getVideoComponent();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				//Detect whether or not it is a video file or an audio file
				String[] fileNameSplit = getFile().getName().split("\\.");
				String fileExtension = fileNameSplit[fileNameSplit.length - 1];

				boolean audio = false;
				if("pcm".equals(fileExtension) || "ogg".equals(fileExtension)) {
					audio = true;
				}


				getFrameVideo().setVisible(true);
				
				final FileSrc fileSrc = new FileSrc("fileSrc");
				fileSrc.setLocation(getFile());

				//Element decodeBin = ElementFactory.make("decodebin2", null);
				DecodeBin2 decodeBin = new DecodeBin2("bin");

				Element appSinkQ = ElementFactory.make("queue", "appSinkQ");
				AppSink appSink = (AppSink) ElementFactory.make("appsink", "appSink");
				final Element tee = ElementFactory.make("tee", "tee");
				Element videoQ = ElementFactory.make("queue", "videoQ");
				Element audioSink = ElementFactory.make("autoaudiosink", "audioSink");

				final Element videoElement = videoComponent.getElement();


				if(audio) {
					getVideoPipe().addMany(fileSrc, decodeBin, appSinkQ, appSink, tee, videoQ, audioSink);
					decodeBin.connect(new DecodeBin2.NEW_DECODED_PAD() {
						public void newDecodedPad(DecodeBin2 element, Pad pad, boolean bool) {
							element.link(tee);
						}
					});
					fileSrc.link(decodeBin);
					tee.link(appSinkQ, appSink);
					tee.link(videoQ, audioSink);
				}
				else {
					getVideoPipe().addMany(fileSrc, decodeBin, appSinkQ, appSink, tee, videoQ, videoElement);
					decodeBin.connect(new DecodeBin2.NEW_DECODED_PAD() {
						public void newDecodedPad(DecodeBin2 element, Pad pad, boolean bool) {
							element.link(tee);
						}
					});
					fileSrc.link(decodeBin);
					tee.link(appSinkQ, appSink);
					tee.link(videoQ, videoComponent.getElement());
				}
				//--gst-debug-level=7

				appSink.set("emit-signals", true);
				appSink.setSync(false);
				appSink.connect(new AppSink.NEW_BUFFER (){
					@Override
					public void newBuffer(AppSink arg0) {
						Buffer temp = arg0.getLastBuffer();
						long time = temp.getTimestamp().toMillis();

						if(lastTime != 0L) {
							System.out.println(time + " " + lastTime);
							System.out.println(time - lastTime);
							getFrameVideo().updateDecompressionTime((int)(time - lastTime));
						}
						lastTime = time;
					}
				});


				getVideoPipe().setState(State.PLAYING);


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
}
