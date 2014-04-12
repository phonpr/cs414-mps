package org.cs414.mp2.client;

import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.cs414.mp2.client.controllers.Listener;
import org.cs414.mp2.client.views.FrameVRemote;
import org.gstreamer.Gst;

////Display RTSP streaming of video
//// (c) 2011 enthusiasticgeek
//// This code is distributed in the hope that it will be useful,
//// but WITHOUT ANY WARRANTY; without even the implied warranty of
//// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
//// Leave Credits intact
//
//package video2; //replace this with your package
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//
//import javax.swing.JFrame;
//import javax.swing.SwingUtilities;
//
////import org.gstreamer.Caps;
//import org.gstreamer.Element;
//import org.gstreamer.ElementFactory;
//import org.gstreamer.Gst;
//import org.gstreamer.Pad;
//import org.gstreamer.PadDirection;
//import org.gstreamer.Pipeline;
//import org.gstreamer.swing.VideoComponent;
//
///**
//* A Simple videotest example.
//*/
//public class Main {
// public Main() {
// }
// private static Pipeline pipe;
// public static void main(String[] args) {
// // Quartz is abysmally slow at scaling video for some reason, so turn it off.
// System.setProperty("apple.awt.graphics.UseQuartz", "false");
//
// args = Gst.init("SwingVideoTest", args);
//
// pipe = new Pipeline("pipeline");
// /*
// final Element videosrc = ElementFactory.make("videotestsrc", "source");
// final Element videofilter = ElementFactory.make("capsfilter", "flt");
// videofilter.setCaps(Caps.fromString("video/x-raw-yuv, width=720, height=576"
//         + ", bpp=32, depth=32, framerate=25/1"));
// */
//
//  pipe.getBus().connect(new Bus.ERROR() {
//     public void errorMessage(GstObject source, int code, String message) {
//         System.out.println("Error occurred: " + message);
//         Gst.quit();
//     }
// });
// pipe.getBus().connect(new Bus.STATE_CHANGED() {
//     public void stateChanged(GstObject source, State old, State current, State pending) {
//         if (source == pipe) {
//             System.out.println("Pipeline state changed from " + old + " to " + current);
//         }
//     }
// });
// pipe.getBus().connect(new Bus.EOS() {
//     public void endOfStream(GstObject source) {
//         System.out.println("Finished playing file");
//         Gst.quit();
//     }
// });        
//
//  pipe.getBus().connect(new Bus.TAG() {
//     public void tagsFound(GstObject source, TagList tagList) {
//         for (String tag : tagList.getTagNames()) {
//             System.out.println("Found tag " + tag + " = "
//                     + tagList.getValue(tag, 0));
//         }
//     }
// });
//
// final Element source = ElementFactory.make("rtspsrc", "Source");
// final Element demux = ElementFactory.make("rtpmp4vdepay", "Depay");
// final Element decoder=ElementFactory.make("ffdec_mpeg4", "Decoder");
// final Element colorspace = ElementFactory.make("ffmpegcolorspace",  "Colorspace");
// //final Element sink = ElementFactory.make ("autovideosink", "Output");
//
// SwingUtilities.invokeLater(new Runnable() {
//
//     public void run() {
//         // Create the video component and link it in
//         VideoComponent videoComponent = new VideoComponent();
//         Element videosink = videoComponent.getElement();
//
//        source.connect(new Element.PAD_ADDED() {
//        public void padAdded(Element element, Pad pad) {
//         pad.link(demux.getStaticPad("sink"));
//        }
//         });
//
//        Pad p = new Pad(null, PadDirection.SRC);
//        source.addPad(p);
//
//         source.set("location","rtsp://<user>:<pass>@<ip>/mpeg4/1/media.amp");  //replace this with your source
//
//         pipe.addMany(source, demux, decoder, colorspace, videosink);
//         Element.linkMany(demux, decoder, colorspace, videosink);
//
//         // Now create a JFrame to display the video output
//         JFrame frame = new JFrame("Swing Video Test");
//         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         frame.add(videoComponent, BorderLayout.CENTER);
//         videoComponent.setPreferredSize(new Dimension(720, 576));
//         frame.pack();
//         frame.setVisible(true);
//
//         // Start the pipeline processing
//         pipe.play();
//     }
// });
// }
//}

public class Main {

	
	// frame
	private static FrameVRemote frameVRemote = null;
	
	// listener
	private static ActionListener remoteListener = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		args = Gst.init("V-Remote", args);
		
		frameVRemote = new FrameVRemote();
		frameVRemote.initializeComponents();
		
		remoteListener = new Listener(frameVRemote);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frameVRemote.setVisible(true);
				frameVRemote.initializeListener(remoteListener);
			}
		});
	}

}
