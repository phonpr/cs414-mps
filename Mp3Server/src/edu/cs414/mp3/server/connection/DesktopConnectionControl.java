package edu.cs414.mp3.server.connection;

import edu.cs414.mp3.common.ConnectionProtocol;

public class DesktopConnectionControl extends ConnectionControl {

	@Override
	protected void onReceiveExtendedCommand(String command) {
		switch (command) {
		default:
			sendResult(ConnectionProtocol.RESULT_UNKNOWN_COMMAND);
			break;
		}
	}

}
