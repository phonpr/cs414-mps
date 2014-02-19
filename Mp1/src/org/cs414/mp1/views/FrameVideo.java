package org.cs414.mp1.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gstreamer.swing.VideoComponent;

public class FrameVideo extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1704000236126848572L;
	
	// constants
	private static final String FRAME_TITLE = "Video";
	
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	
	// UI component
	private JPanel panelVideo = null;
	private JPanel panelMonitor = null;
	private VideoComponent videoComponent = null;
	private JTextField textCompressionTime = null;
	private JTextField textCompressionSize = null;
	
	public void initializeComponents() {
		panelVideo = new JPanel();
		panelMonitor = new JPanel();
		
		// main frame
		setTitle(FRAME_TITLE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(
			new BoxLayout(getContentPane(), BoxLayout.Y_AXIS)
		);
		getContentPane().add(panelVideo);
		getContentPane().add(panelMonitor);
		
		// video panel
		videoComponent = new VideoComponent();
		panelVideo.setLayout(new BorderLayout());
		panelVideo.add(videoComponent, BorderLayout.CENTER);
		
		// monitor panel
		textCompressionTime = new JTextField();
		textCompressionTime.setColumns(10);
		textCompressionTime.setEditable(false);
		textCompressionSize = new JTextField();
		textCompressionSize.setColumns(10);
		textCompressionSize.setEditable(false);
		panelMonitor.add(new JLabel("Compression Time :"));
		panelMonitor.add(textCompressionTime);
		panelMonitor.add(new JLabel("Compression Size :"));
		panelMonitor.add(textCompressionSize);
		
		updateSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
 		videoComponent.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
 		
 		// set initial compression monitor information to 0
 		updateCompressionTime(0);
 		updateCompressionSize(0);
	}
	
	public void updateSize(int width, int height) {
		// position and size
 		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
 		int frameX = (screen.width - width)/2;
 		int frameY = (screen.height - height)/2;
 		setBounds(frameX, frameY, width, height);
	}
	
	public void updateCompressionTime(int time) {
		textCompressionTime.setText(Integer.toString(time));
	}
	
	public void updateCompressionSize(int size) {
		textCompressionSize.setText(Integer.toString(size));
	}
	
	public VideoComponent getVideoComponent() {
		return videoComponent;
	}
}
