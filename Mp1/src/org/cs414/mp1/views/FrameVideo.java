package org.cs414.mp1.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cs414.mp1.controllers.Controller;
import org.cs414.mp1.controllers.Controller.OperationType;
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
	private JTextField textDecompressionTime = null;
	private JTextField textCompressionRatio = null;
	private JTextField textCompressedSize = null;
	
	// logics
	private OperationType eOperationType = null;
	
	public FrameVideo(OperationType operation) {
		eOperationType = operation;
	}
	
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
		// record / play
		if (eOperationType == OperationType.RECORDING) {
			textCompressionTime = new JTextField();
			textCompressionTime.setColumns(6);
			textCompressionTime.setEditable(false);
			panelMonitor.add(new JLabel("Compression Time :"));
			panelMonitor.add(textCompressionTime);
		}
		else if (eOperationType == OperationType.PLAYING) {
			textDecompressionTime = new JTextField();
			textDecompressionTime.setColumns(6);
			textDecompressionTime.setEditable(false);
			panelMonitor.add(new JLabel("Decompression Time :"));
			panelMonitor.add(textDecompressionTime);
		}
		else ;
		
		// common
		textCompressionRatio = new JTextField();
		textCompressionRatio.setColumns(6);
		textCompressionRatio.setEditable(false);
		textCompressedSize = new JTextField();
		textCompressedSize.setColumns(6);
		textCompressedSize.setEditable(false);
		
		panelMonitor.add(new JLabel("Compression Ratio :"));
		panelMonitor.add(textCompressionRatio);
		panelMonitor.add(new JLabel("Compressed Size :"));
		panelMonitor.add(textCompressedSize);
		
		updateSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
 		videoComponent.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
 		
 		// set initial compression monitor information to 0
 		updateCompressionTime(0);
 		updateDecompressionTime(0);
 		updateCompressionRatio(0);
 		updateCompressedSize(0);
	}
	
	public void updateSize(int width, int height) {
		// position and size
 		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
 		int frameX = (screen.width - width)/2;
 		int frameY = (screen.height - height)/2;
 		setBounds(frameX, frameY, width, height);
	}
	
	public void updateCompressionTime(int time) {
		if (eOperationType == OperationType.RECORDING) {
			textCompressionTime.setText(Integer.toString(time));
		}
	}
	
	public void updateDecompressionTime(int time) {
		if (eOperationType == OperationType.PLAYING) {
			textDecompressionTime.setText(Integer.toString(time));
		}
	}
	
	public void updateCompressionRatio(double ratio) {
		textCompressionRatio.setText(Double.toString(ratio));
	}
	
	public void updateCompressedSize(int size) {
		textCompressedSize.setText(Integer.toString(size));
	}
	
	public VideoComponent getVideoComponent() {
		return videoComponent;
	}
}
