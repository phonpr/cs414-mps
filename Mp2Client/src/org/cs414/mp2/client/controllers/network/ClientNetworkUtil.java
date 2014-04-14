package org.cs414.mp2.client.controllers.network;


/**
 * Created by bill on 4/12/14.
 */
public class ClientNetworkUtil{

	private static NetworkUtil clientNetworkUtil = null;

	private static final int SERVER_PORT = 7000;

	public static void initializeNetwork(String hostname) {
		clientNetworkUtil = new NetworkUtil(hostname, SERVER_PORT);
	}

	public static void sendStart(int requestedResource, int requestedVideo) {
		clientNetworkUtil.sendMessage(NetworkConstants.START_CMD + requestedResource + " " + requestedVideo);
	}

	public static String waitForGoMessage() {
		String nextMessage = null;

		while(nextMessage == null) {
			nextMessage = clientNetworkUtil.readMessage();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return nextMessage;
	}
	public static void sendStop() {
		clientNetworkUtil.sendMessage(NetworkConstants.STOP_CMD);
	}

	public static void sendPause() {
		clientNetworkUtil.sendMessage(NetworkConstants.PAUSE_CMD);
	}

	public static void sendResume() {
		clientNetworkUtil.sendMessage(NetworkConstants.RESUME_CMD);
	}

	public static void sendRewind() {
		clientNetworkUtil.sendMessage(NetworkConstants.RW_CMD);
	}

	public static void sendFF() {
		clientNetworkUtil.sendMessage(NetworkConstants.FF_CMD);
	}

	public static void sendActive() {
		clientNetworkUtil.sendMessage(NetworkConstants.ACTIVE );
	}

	public static void sendPassive() {
		clientNetworkUtil.sendMessage(NetworkConstants.PASSIVE);
	}

	public static void sendKill() {
		clientNetworkUtil.sendMessage(NetworkConstants.KILL);
	}

	public static void negotiateResource(int available) {
		clientNetworkUtil.sendMessage(String.valueOf(available));
	}
}
