package edu.cs414.mp3.server.connection;

import edu.cs414.mp3.common.ConnectionProtocol;
import edu.cs414.mp3.server.streamer.DesktopStreamer;

public class DesktopConnectionControl extends ConnectionControl {

	public DesktopConnectionControl() {
		super(new DesktopStreamer());
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
