package edu.cs414.mp3.client.connection;

import edu.cs414.mp3.common.ConnectionConfig;


public class DesktopConnection extends Connection {
	
	public DesktopConnection() {
	}

	@Override
	public boolean onPlay() {
		return super.onPlay(
			ConnectionConfig.DESKTOP_SERVER_HOST,
			ConnectionConfig.DESKTOP_SERVER_PORT
		);
	}

}
