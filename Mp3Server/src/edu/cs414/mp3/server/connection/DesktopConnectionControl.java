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
		case ConnectionProtocol.CMD_HD_MODE:
			streamer.tearDown();
			((DesktopStreamer) streamer).buildPipeline(1, 20);
			sendResult(ConnectionProtocol.RESULT_SUCCESS);
			break;
		case ConnectionProtocol.CMD_SD_MODE:
			streamer.tearDown();
			((DesktopStreamer) streamer).buildPipeline(0, 10);
			sendResult(ConnectionProtocol.RESULT_SUCCESS);
			break;
		default:
			sendResult(ConnectionProtocol.RESULT_UNKNOWN_COMMAND);
			break;
		}
	}

}
