package org.cs414.mp2.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ControlChannel implements Runnable {
	
	private static final String START_CMD	= "START";
	private static final String STOP_CMD	= "STOP";
	private static final String RESUME_CMD	= "RESUME";
	private static final String PAUSE_CMD	= "PAUSE";
	private static final String FF_CMD		= "FF";
	private static final String RW_CMD		= "RW";
	private static final String ACTIVE_CMD	= "ACTIVE";
	private static final String PASSIVE_CMD	= "PASSIVE";
	
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
				
				if (inputLine.startsWith(START_CMD)) {
					String parameter = inputLine.substring(START_CMD.length());
					System.out.println("START parameter : " +  parameter);
					
					if (parameter.length() > 0) {
						bandwidth = Integer.parseInt(parameter);
						
						if (ResourceManager.isBandwidthAvailable(bandwidth)) {
							ResourceManager.addBandwidth(bandwidth);
							new Thread(mediaThread).start();
							writer.println("TRUE");
						}
						else {
							writer.println("FALSE");
						}
					}
				}
				else if (inputLine.startsWith(STOP_CMD)) {
					ResourceManager.subtractBandwidth(bandwidth);
					mediaThread.stop();
					break;
				}
				else if (inputLine.startsWith(PAUSE_CMD)) {
					mediaThread.pause();
				}
				else if (inputLine.startsWith(RESUME_CMD)) {
					mediaThread.resume();
				}
				else if (inputLine.startsWith(RW_CMD)) {
					mediaThread.rw();
				}
				else if (inputLine.startsWith(FF_CMD)) {
					mediaThread.ff();
				}
				else if (inputLine.startsWith(ACTIVE_CMD)) {
					mediaThread.activeMode();
				}
				else if (inputLine.startsWith(PASSIVE_CMD)) {
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
