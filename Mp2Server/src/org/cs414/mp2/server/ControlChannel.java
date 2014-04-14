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

	private final int AUDIO_BANDWIDTH = 8000;
	private final int VIDEO_BANDWIDTH_L = 61988;
	private final int VIDEO_BANDWIDTH_S = 17158; ///TODO new number
	
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
			
			MediaThread mediaThread = null;
			
			String inputLine = null;
			while ((inputLine = reader.readLine()) != null) {
				
				System.out.println(inputLine);
				writer.println(inputLine);
				
				if (inputLine.startsWith(START_CMD)) {
					String parameters = inputLine.substring(START_CMD.length());
					System.out.println("START parameters : " +  parameters);
					
					if (parameters.length() > 0) {
						String[] paramSplit = parameters.split(" ");
						bandwidth = Integer.parseInt(paramSplit[0]);
						int size = Integer.parseInt(paramSplit[1]);
						
						bandwidth = Math.min(bandwidth, ResourceManager.getBandwidthLimit() - ResourceManager.getCurrentBandwidth());
						int framerate = calcFramerate(bandwidth, size);
						
						if (framerate == -1) {
							writer.println("FALSE");
						} else {
							ResourceManager.addBandwidth(bandwidth);

							mediaThread = new MediaThread(framerate, size, socket.getInetAddress().getHostAddress());

							writer.println("TRUE");
							new Thread(mediaThread).start();

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
	
	
	private int calcFramerate(int band, int size) {
		int vidband = 0;
		if (size == 1)
			vidband = VIDEO_BANDWIDTH_L;
		else
			vidband = VIDEO_BANDWIDTH_S;
		
		int framerate = (band - AUDIO_BANDWIDTH) / vidband; // (availiable - audio) / video gives frames/sec
		if (framerate >= 25)
			return 25;
		else if (framerate <= 15)
			return -1;
		else
			return framerate;
	}

}
