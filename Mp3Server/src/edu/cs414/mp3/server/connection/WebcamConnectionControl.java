package edu.cs414.mp3.server.connection;

import edu.cs414.mp3.common.ConnectionProtocol;
import edu.cs414.mp3.server.streamer.WebcamStreamer;

public class WebcamConnectionControl extends ConnectionControl {
	
	public WebcamConnectionControl() {
		super(new WebcamStreamer());
	}

	@Override
	protected void onReceiveExtendedCommand(String command) {
		switch (command) {
		default:
			sendResult(ConnectionProtocol.RESULT_UNKNOWN_COMMAND);
			break;
		}
	}

}
