package org.cs414.mp2.client.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

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
	
	// frames
	private FrameVRemote frameRemote = null;
	
	// dialog
	private DialogRecordOptions dialogRecordOptions = null;

	private DialogBandwidthOptions dialogBandwidthOptions = null;

	// controller
	private PlayController controller = null;
	
	public Listener(FrameVRemote frameRemote) {
		this.frameRemote = frameRemote;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action == ACTION_START) {
			//controller = new PlayController(file);
			//controller.startRunning();
			if(/* ResourceNegotiation.doAdmission() */true) { //TODO UNCOMMENT
				controller = new PlayController();
				ClientNetworkUtil.sendStart(ResourceNegotiation.getAvailable());
				String goMessage = ClientNetworkUtil.waitForGoMessage();
				controller.startRunning();
			}


		}
		else if(action == ACTION_SETBAND) {
			dialogBandwidthOptions = new DialogBandwidthOptions(frameRemote);
			dialogBandwidthOptions.showOpenDialog();
			if(dialogBandwidthOptions.isChanged()) {
				dialogBandwidthOptions.acknowledgeChanged();
				ResourceNegotiation.setAvailable(dialogBandwidthOptions.getMaxBandwidth());
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
				((PlayController) controller).togglePause();
				ClientNetworkUtil.sendPause();
			}
		}
		else if (action == ACTION_FF) {
			// this is activated only when controller is PlayController
			if (controller != null) {
				((PlayController) controller).toggleFF();
				ClientNetworkUtil.sendFF();
			}
		}
		else if (action == ACTION_RW) {
			// this is activated only when controller is PlayController
			if (controller != null) {
				((PlayController) controller).toggleRW();
				ClientNetworkUtil.sendRewind();
			}
		}
		else ;
	}

}
