package org.cs414.mp1.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

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
	private VideoComponent videoComponent = null;
	
	public void initializeComponents() {
		videoComponent = new VideoComponent();
		
		// main frame
		setTitle(FRAME_TITLE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		add(videoComponent, BorderLayout.CENTER);
		
		updateSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
 		videoComponent.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
	
	public void updateSize(int width, int height) {
		// position and size
 		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
 		int frameX = (screen.width - width)/2;
 		int frameY = (screen.height - height)/2;
 		setBounds(frameX, frameY, width, height);
	}
	
	public VideoComponent getVideoComponent() {
		return videoComponent;
	}
}
