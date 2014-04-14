package org.cs414.mp2.client;

import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.cs414.mp2.client.controllers.Listener;
import org.cs414.mp2.client.views.FrameVRemote;
import org.gstreamer.Gst;

public class Main {
	// frame
	private static FrameVRemote frameVRemote = null;
	
	// listener
	private static ActionListener remoteListener = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Gst.init();
		
		String serverIp = "127.0.0.1";
		if (args.length > 0) {
			serverIp = args[0];
		}
		System.out.println("Server IP : " + serverIp);
		
		frameVRemote = new FrameVRemote();
		frameVRemote.initializeComponents();
		
		remoteListener = new Listener(frameVRemote, serverIp);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frameVRemote.setVisible(true);
				frameVRemote.initializeListener(remoteListener);
			}
		});
	}

}
