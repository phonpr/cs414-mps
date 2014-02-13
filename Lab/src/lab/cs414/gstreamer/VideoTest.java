package lab.cs414.gstreamer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;

public class VideoTest {

	private static Pipeline pipe;
	
	public static void main(String[] args) {
		System.out.println("Start...");
		
		args = Gst.init("VideoTest", args);
		pipe = new Pipeline("VideoTest");
		
		final Element videoSrc = ElementFactory.make("videotestsrc", "source");
		final Element videoFilter = ElementFactory.make("capsfilter", "filter");
		
		videoFilter.setCaps(Caps.fromString("video/x-raw-yuv, width=720, height=576, bpp=32, depth=32, framerate=25/1"));
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				VideoComponent videoComponent = new VideoComponent();
				Element videoSink = videoComponent.getElement();
				
				pipe.addMany(videoSrc, videoFilter, videoSink);
				Element.linkMany(videoSrc, videoFilter, videoSink);
				
				// Now create a JFrame to display the video output
				JFrame frame = new JFrame("Swing Video Test");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.add(videoComponent, BorderLayout.CENTER);
				videoComponent.setPreferredSize(new Dimension(720, 576));
				frame.pack();
				frame.setVisible(true);
				
				// Start the pipeline processing
				pipe.setState(State.PLAYING);
			}
			
		});
	}

}
