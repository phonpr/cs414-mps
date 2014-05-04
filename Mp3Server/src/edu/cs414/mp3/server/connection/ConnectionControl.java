package edu.cs414.mp3.server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.cs414.mp3.common.ConnectionProtocol;
import edu.cs414.mp3.server.ServerResourceManager;
import edu.cs414.mp3.server.streamer.Streamer;

public abstract class ConnectionControl implements Runnable {

	private Socket clientSocket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	protected Streamer streamer;
	
	private boolean connectionControlRunning = false;
	
	public ConnectionControl(Streamer streamer) {
		this.streamer = streamer;
	}

	public void initialize(Socket clientSocket) {
		this.clientSocket = clientSocket;
		
		try {
			this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
			this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.out.println("[ConnectionControl] Failed to initialize socket i/o.");
			System.out.println(e);
		}
	}

	@Override
	public void run() {
		connectionControlRunning = true;
		System.out.println("[ConnectionControl] Connection control started...");
		
		try {
			String command = null;
			while ((command = reader.readLine()) != null && connectionControlRunning) {
				System.out.println("[ConnectionControl] Command Received : " + command);
				onReceiveCommand(command);
			}
		} catch (IOException e) {
			System.out.println("[ConnectionControl] Failed to read client command");
			System.out.println(e);
		}
	}

	private void onReceiveCommand(String command) {
		switch (command) {
		case ConnectionProtocol.CMD_PLAY:
			streamer.init(clientSocket.getInetAddress().getHostName());
			new Thread(streamer).start();
			
			// set the streamer to communicate with resource manager
			ServerResourceManager.setStreamer(streamer);
			sendResult(ConnectionProtocol.RESULT_SUCCESS);
			break;
		case ConnectionProtocol.CMD_PAUSE:
			sendResult(ConnectionProtocol.RESULT_SUCCESS);
			break;
		case ConnectionProtocol.CMD_RESUME:
			sendResult(ConnectionProtocol.RESULT_SUCCESS);
			break;
		case ConnectionProtocol.CMD_STOP:
			
			// reset the streamer
			ServerResourceManager.setStreamer(null);
			sendResult(ConnectionProtocol.RESULT_SUCCESS);
			break;
		default:
			if (command.startsWith(ConnectionProtocol.CMD_RESOURCE_CHANGED)) {
				String param = command.substring(ConnectionProtocol.CMD_RESOURCE_CHANGED.length());
				long newResource = Long.parseLong(param);
				sendResult(ConnectionProtocol.RESULT_SUCCESS);
				
				System.out.println("[ConnectionControl] Client resource changed : " + newResource);
				break;
			}
			
			onReceiveExtendedCommand(command);
			break;
		}
	}

	protected abstract void onReceiveExtendedCommand(String command);
	
	protected boolean sendResult(String result) {
		if (clientSocket == null || writer == null || reader == null) {
			return false;
		}
		
		writer.println(result);
		System.out.println("[ConnectionControl] Result sent to the client : " + result);
		
		return true;
	}
}