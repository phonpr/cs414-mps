package edu.cs414.mp3.client.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.cs414.mp3.common.ConnectionProtocol;

public abstract class Connection {
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	private boolean isHdMode = false;
	
	public boolean onPlay(String hostName, int port) {
		
		try {
			this.socket = new Socket(hostName, port);
			this.writer = new PrintWriter(socket.getOutputStream(), true);
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("[Connection] Failed to connect, unknown host : " + hostName + ":" + port);
		} catch (IOException e) {
			System.out.println("[Connection] Failed to connect to server : " + hostName + ":" + port);
			System.out.println(e);
		}
		
		return sendCommand(ConnectionProtocol.CMD_PLAY);
	}
	
	public abstract boolean onPlay();
	
	public boolean onPause() {
		return sendCommand(ConnectionProtocol.CMD_PAUSE);
	}
	
	public boolean onResume() {
		return sendCommand(ConnectionProtocol.CMD_RESUME);
	}
	
	public boolean onStop() {
		boolean success = false;
		success = sendCommand(ConnectionProtocol.CMD_STOP);
		
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("[Connection] Failed to close the socket.");
			success = false;
		}
		
		return success;
	}
	
	public boolean onSdMode() {
		if (sendCommand(ConnectionProtocol.CMD_SD_MODE)) {
			isHdMode = false;
			return true;
		}
		
		return false;
	}
	
	public boolean onHdMode() {
		if (sendCommand(ConnectionProtocol.CMD_HD_MODE)) {
			isHdMode = true;
			return true;
		}
		
		return false;
	}
	
	public boolean sendCommand(String command) {
		if (socket == null || writer == null || reader == null) {
			return false;
		}
		
		writer.println(command);
		System.out.println("[Connection] Command sent to the server : " + command);
		
		boolean success = true;
		try {
			if (ConnectionProtocol.RESULT_FAILED.equals(reader.readLine())) {
				System.out.println("[Connection] Server responded with failure.");
				success = false;
			}
		} catch (IOException e) {
			System.out.println("[Connection] Server not responding.");
			System.out.println(e);
			success = false;
		}
		
		return success;
	}
	
	public boolean isHdMode() {
		return isHdMode;
	}
}
