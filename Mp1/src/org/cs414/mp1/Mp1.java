package org.cs414.mp1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;

public class Mp1 extends JPanel {
	
	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -2398906894228626286L;
	
	// constants
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 680;
	
	// UI static components
	private static ActionListener listener = null;
	
	private static JFrame frameMain = null;
	
	private static JPanel panelMain = null;
	private static JPanel panelMenu = null;
	private static JPanel panelVideo = null;
	
	private static JButton btnRecord = null;
	private static JButton btnPlay = null;
	private static JButton btnFastForward = null;
	
	// GStreamer static components
	private static Pipeline pipe = null;
	
	private static void initializeUi() {
		listener = new Listener();
		
		panelMain = new JPanel();
		panelMenu = new JPanel();
		panelVideo = new JPanel();
		
		btnRecord = new JButton("Record");
		btnPlay = new JButton("Play");
		btnFastForward = new JButton("FF");
		
		panelMain.add(panelMenu);
		panelMain.add(panelVideo);
		
		btnRecord.setActionCommand(Listener.ACTION_RECORD);
		btnRecord.addActionListener(listener);
		panelMenu.add(btnRecord);
		
		btnPlay.setActionCommand(Listener.ACTION_PLAY);
		btnPlay.addActionListener(listener);
		panelMenu.add(btnPlay);
		
		btnFastForward.setActionCommand(Listener.ACTION_FF);
		btnFastForward.addActionListener(listener);
		panelMenu.add(btnFastForward);
	}

	private static void initializeVideo() {
		pipe = new Pipeline("VideoTest");
		
		final Element videoSrc = ElementFactory.make("videotestsrc", "source");
		final Element videoFilter = ElementFactory.make("capsfilter", "filter");
		
		videoFilter.setCaps(Caps.fromString("video/x-raw-yuv, width=720, height=576, bpp=32, depth=32, framerate=25/1"));
		VideoComponent videoComponent = new VideoComponent();
		Element videoSink = videoComponent.getElement();
		
		pipe.addMany(videoSrc, videoFilter, videoSink);
		Element.linkMany(videoSrc, videoFilter, videoSink);
		
		panelVideo.add(videoComponent, BorderLayout.CENTER);
		videoComponent.setPreferredSize(new Dimension(720, 576));
	}

	private static void createAndShowGUI() {
		// create and set up the window.
		frameMain = new JFrame("HelloWorldSwing");
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frameMain.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frameMain.setResizable(false);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int frameX = (screen.width - FRAME_WIDTH)/2;
		int frameY = (screen.height - FRAME_HEIGHT)/2;
		frameMain.setLocation(frameX, frameY);
		
		// add menu panel
		frameMain.add(panelMain);

		// display the window.
		frameMain.pack();
		frameMain.setVisible(true);
	}
	
	public static void main(String[] args) {
		args = Gst.init("VideoTest", args);
		
		initializeUi();
		initializeVideo();
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
