package org.cs414.mp2.client.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import org.cs414.mp2.client.controllers.Controller.AudioType;
import org.cs414.mp2.client.controllers.Controller.VideoType;
import org.cs414.mp2.client.controllers.network.ClientNetworkUtil;
import org.cs414.mp2.client.controllers.network.ResourceNegotiation;
import org.cs414.mp2.client.views.DialogBandwidthOptions;
import org.cs414.mp2.client.views.DialogRecordOptions;
import org.cs414.mp2.client.views.FrameVRemote;

public class Listener implements ActionListener {

	public static final String ACTION_START = "start";
	public static final String ACTION_RESUME = "resume";
	public static final String ACTION_RECORD = "record";

	public static final String ACTION_SETBAND = "bandwidth";
	
	public static final String ACTION_STOP = "stop";
	public static final String ACTION_PAUSE = "pause";
	public static final String ACTION_FF = "ff";
	public static final String ACTION_RW = "rw";

	public static final String ACTION_SMALL = "small";
	public static final String ACTION_BIG = "big";

	public static final byte SMALL_VIDEO = 0;
	public static final byte BIG_VIDEO = 1;
	
	// frames
	private FrameVRemote frameRemote = null;
	
	// dialog
	private DialogRecordOptions dialogRecordOptions = null;
	private DialogBandwidthOptions dialogBandwidthOptions = null;

	// controller
	private PlayController controller = null;
	
	// networking
	private String hostname = "127.0.0.1";
	
	public Listener(FrameVRemote frameRemote, String serverIp) {
		this.frameRemote = frameRemote;
		this.hostname = serverIp;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action == ACTION_START) {
			if(ResourceNegotiation.doAdmission(frameRemote.getVideoSelection())) { //TODO UNCOMMENT

				controller = new PlayController();
				controller.setHostname(hostname);
				ClientNetworkUtil.initializeNetwork(hostname);
				ClientNetworkUtil.sendStart(ResourceNegotiation.getRequestedRate(frameRemote.getVideoSelection()), frameRemote.getVideoSelection());

				String goMessage = ClientNetworkUtil.waitForGoMessage();
				System.out.println("GOGOGO");
				if(goMessage.startsWith("TRUE")) {
					System.out.println("I GOT HERE");
					controller.startRunning();
				}
				else if(goMessage.startsWith("FALSE")) {
					JOptionPane.showMessageDialog(null, "Not enough resources on server");
				}
				else {
					System.out.println("Expected TRUE or FALSE response from server, got: " + goMessage);
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "Not enough resources on client");
			}


		}
		else if(action == ACTION_SETBAND) {
			dialogBandwidthOptions = new DialogBandwidthOptions(frameRemote);
			dialogBandwidthOptions.showOpenDialog();
			if(dialogBandwidthOptions.isChanged()) {
				dialogBandwidthOptions.acknowledgeChanged();
				ResourceNegotiation.setAvailable(dialogBandwidthOptions.getMaxBandwidth());

				if(controller != null) {
					if (ResourceNegotiation.doAdmission(frameRemote.getVideoSelection())) {
						ClientNetworkUtil.sendStop();
						ClientNetworkUtil.sendStart(ResourceNegotiation.getRequestedRate(frameRemote.getVideoSelection()), frameRemote.getVideoSelection());
						String goMessage = ClientNetworkUtil.waitForGoMessage();
						System.out.println(goMessage);
						if ("TRUE".equals(goMessage)) {
							controller.startRunning();
						} else if ("FALSE".equals(goMessage)) {
							JOptionPane.showMessageDialog(null, "Not enough resources on server");
						}
					} else
						JOptionPane.showMessageDialog(null, "Not enough resources on server");
				}
			}
		}
		else if(action == ACTION_RESUME) {
			ClientNetworkUtil.sendResume();
		}
		else if (action == ACTION_STOP) {
			if (controller != null) {
				controller.stopRunning();
				ClientNetworkUtil.sendStop();
				controller = null;
			}
		}
		else if (action == ACTION_PAUSE) {
			// this is activated only when controller is PlayController
			if (controller != null) {
				ClientNetworkUtil.sendPause();
			}
		}
		else if (action == ACTION_FF) {
			// this is activated only when controller is PlayController
			if (controller != null) {
				ClientNetworkUtil.sendFF();
			}
		}
		else if (action == ACTION_RW) {
			// this is activated only when controller is PlayController
			if (controller != null) {
				ClientNetworkUtil.sendRewind();
			}
		}
		else if(action == ACTION_SMALL) {
			frameRemote.setVideoSelection(SMALL_VIDEO);
		}
		else if(action == ACTION_BIG) {
			frameRemote.setVideoSelection(BIG_VIDEO);
		}
		else ;
	}

}
