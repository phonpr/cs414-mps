package org.cs414.mp1;

import org.gstreamer.*;
import org.gstreamer.elements.DecodeBin;
import org.gstreamer.elements.FileSrc;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.swing.VideoComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Player {
	public static void main(String[] args) {
		args = Gst.init("VideoPlayer", args);
		File videoFile = new File("test.avi");
		System.out.println("Working Directory = " +
				System.getProperty("user.dir"));
		System.out.println(videoFile.exists());

		final Pipeline pipe = new Pipeline("SimplePipe");
		final FileSrc fileSrc = new FileSrc("fileSrc");
		fileSrc.setLocation(videoFile);

		final Element testSrc = ElementFactory.make("videotestsrc", null);
		final Element testFilter = ElementFactory.make("capsfilter", "filter");
		testFilter.setCaps(Caps.fromString("image/jpeg, framerate=10/1"));

		final Element parser = ElementFactory.make("jpegparse", "parser");
		final Element decoder = ElementFactory.make("jpegdec", "decoder");
		//decoder.setCaps(Caps.fromString("video/x-indeo, indeoversion=(int)4, framerate=(fraction)35/1, width=(int)256, height=(int)240"));

		final Element testMe = ElementFactory.make("ffmpegcolorspace", null);


		//final Element testMe2 = ElementFactory.make("", null);

		final Element videoRate = ElementFactory.make("videorate", null);
		videoRate.set("max-rate", 100);
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				VideoComponent videoComponent = new VideoComponent();

				Element videoSink = videoComponent.getElement();
				Element autoVideoSink = ElementFactory.make("autovideosink", null);
				Element queue = ElementFactory.make("queue", null);
				pipe.addMany(fileSrc, testFilter, parser, decoder,  testMe, videoSink);
				Element.linkMany(fileSrc, testFilter, parser, decoder,  testMe, videoSink);
				//pipe.addMany(testSrc, testFilter, videoSink);
				//Element.linkMany(testSrc, testFilter, videoSink);





				// Now create a JFrame to display the video output
				JFrame frame = new JFrame("Swing Video Test");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.add(videoComponent, BorderLayout.CENTER);
				videoComponent.setPreferredSize(new Dimension(720, 576));
				frame.pack();
				frame.setVisible(true);

				pipe.setState(State.PLAYING);
				}
		});
		Gst.main();
		pipe.setState(State.PLAYING);

	}
}
