package org.cs414.mp2.client.controllers.network;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by bill on 4/12/14.
 */
public class NetworkUtil {
	protected Socket socket;
	protected LinkedBlockingQueue<String> messageQueue;

	protected Writer writer;
	protected BufferedReader reader;

	private Runnable messageListener;

	private class ReadCommandsListener implements Runnable {
		private boolean exit = false;

		public void exit() {
			exit = true;
		}

		public void run() {
			while(exit = false) {
				try {
					if(reader.ready()) {
						messageQueue.offer(reader.readLine());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public NetworkUtil(String hostName, int portNum) {
		try {
			socket = new Socket(hostName, portNum);
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			messageQueue = new LinkedBlockingQueue();

			messageListener = new ReadCommandsListener();
			messageListener.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String command) {
		try {
			writer.write(command);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readMessage() {
		if(!messageQueue.isEmpty()) {
			try {
				return messageQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


}
