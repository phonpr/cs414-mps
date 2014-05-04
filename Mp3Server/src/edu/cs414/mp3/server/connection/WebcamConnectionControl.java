package edu.cs414.mp3.server.connection;

import edu.cs414.mp3.common.ConnectionProtocol;
import edu.cs414.mp3.server.streamer.DesktopStreamer;
import edu.cs414.mp3.server.streamer.WebcamStreamer;

public class WebcamConnectionControl extends ConnectionControl {
	
	public WebcamConnectionControl() {
		super(new WebcamStreamer());
	}

	@Override
	protected void onReceiveExtendedCommand(String command) {
		switch (command) {
		case ConnectionProtocol.CMD_HD_MODE:
			streamer.tearDown();
			((WebcamStreamer) streamer).createPipeline(1, 20);
			((WebcamStreamer) streamer).play();
			sendResult(ConnectionProtocol.RESULT_SUCCESS);
			break;
		case ConnectionProtocol.CMD_SD_MODE:
			streamer.tearDown();
			((WebcamStreamer) streamer).createPipeline(0, 10);
			((WebcamStreamer) streamer).play();
			sendResult(ConnectionProtocol.RESULT_SUCCESS);
			break;
		default:
			sendResult(ConnectionProtocol.RESULT_UNKNOWN_COMMAND);
			break;
		}
	}

}
