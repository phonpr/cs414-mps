package edu.cs414.mp3.client.connection;

import edu.cs414.mp3.common.ConnectionConfig;


public class WebcamConnection extends Connection {
	
	public WebcamConnection() {
	}

	@Override
	public boolean onPlay() {
		return super.onPlay(
			ConnectionConfig.WEBCAM_SERVER_HOST,
			ConnectionConfig.WEBCAM_SERVER_PORT
		);
	}

}
