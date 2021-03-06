package org.cs414.mp1;

import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.cs414.mp1.controllers.Listener;
import org.cs414.mp1.views.FrameVRemote;
import org.gstreamer.Gst;

public class Mp1 {
	
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