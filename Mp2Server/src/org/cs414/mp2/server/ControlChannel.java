package org.cs414.mp2.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ControlChannel implements Runnable {
	
	private Socket socket = null;
	private PrintWriter writer = null;
	private BufferedReader reader = null;
	
	private int bandwidth = 0;
	
	public ControlChannel(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
        try {
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			MediaThread mediaThread = new MediaThread();
			
			String inputLine = null;
			while ((inputLine = reader.readLine()) != null) {
				
				System.out.println(inputLine);
				writer.println(inputLine);
				
				if (inputLine.startsWith("START")) {
					String parameter = inputLine.substring("START".length());
					if (parameter.length() > 0) {
						bandwidth = Integer.parseInt(parameter);
						
						if (ResourceManager.isBandwidthAvailable(bandwidth)) {
							ResourceManager.addBandwidth(bandwidth);
							new Thread(mediaThread).start();
						}
					}
				}
				else if (inputLine.startsWith("STOP")) {
					ResourceManager.subtractBandwidth(bandwidth);
					mediaThread.stop();
					break;
				}
				else if (inputLine.startsWith("PAUSE")) {
					mediaThread.pause();
				}
				else if (inputLine.startsWith("RESUME")) {
					mediaThread.resume();
				}
				else if (inputLine.startsWith("RW")) {
					mediaThread.rw();
				}
				else if (inputLine.startsWith("FF")) {
					mediaThread.ff();
				}
				else if (inputLine.startsWith("ACTIVE")) {
					mediaThread.activeMode();
				}
				else if (inputLine.startsWith("PASSIVE")) {
					mediaThread.passiveMode();
				}
				else {
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
