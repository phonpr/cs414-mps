package edu.cs414.mp3.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gstreamer.swing.VideoComponent;

public class VideoWindow extends JFrame {

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
	private JTextField textSkew = null;
	private JTextField textJitter = null;
	private JTextField textBandwidth = null;
	private JTextField textFramerate = null;



	public VideoWindow() {
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
		
		textSkew = new JTextField();
		textSkew.setColumns(6);
		textSkew.setEditable(false);
		textJitter = new JTextField();
		textJitter.setColumns(6);
		textJitter.setEditable(false);
		textBandwidth = new JTextField();
		textBandwidth.setColumns(6);
		textBandwidth.setEditable(false);
		textFramerate = new JTextField();
		textFramerate.setColumns(6);
		textFramerate.setEditable(false);

		panelMonitor.add(new JLabel("Skew :"));
		panelMonitor.add(textSkew);
		panelMonitor.add(new JLabel("Jitter :"));
		panelMonitor.add(textJitter);
		panelMonitor.add(new JLabel("Bandwidth :"));
		panelMonitor.add(textBandwidth);
		panelMonitor.add(new JLabel("Framerate :"));
		panelMonitor.add(textFramerate);
		
		updateSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
 		videoComponent.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
 		
 		// set initial compression monitor information to 0
 		updateSkew(0);
 		updateJitter(0);
 		updateBandwidth(0);
 		updateFramerate(0);
	}
	
	public void updateSize(int width, int height) {
		// position and size
 		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
 		int frameX = (screen.width - width)/2;
 		int frameY = (screen.height - height)/2;
 		setBounds(frameX, frameY, width, height);
	}
	
	public void updateSkew(int skew) {
		textSkew.setText("" + skew);
	}
	
	public void updateJitter(int jitter) {
		textJitter.setText("" + jitter);
	}
	
	public void updateBandwidth(int bandwidth) {
		textBandwidth.setText("" + bandwidth);
	}
	
	public void updateFramerate(int framerate) {
		textFramerate.setText("" + framerate);
	}
	
	public VideoComponent getVideoComponent() {
		return videoComponent;
	}
}
