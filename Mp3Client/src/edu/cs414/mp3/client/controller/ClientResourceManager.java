package edu.cs414.mp3.client.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.cs414.mp3.client.connection.DesktopConnection;
import edu.cs414.mp3.client.connection.WebcamConnection;

public class ClientResourceManager {
	private static long bandwidthLimit = 0;
	private static boolean running = false;
	
	private static DesktopConnection desktopConnection = null;
	private static WebcamConnection webcamConnection = null;
	
	private class ResourceManagerThread implements Runnable {
		@Override
		public void run() {
			boolean changed = false;
			
			while (running) {
				long newLimit = Long.parseLong(readFile("resource.txt"));
				
				if (bandwidthLimit != newLimit) {
					System.out.println("[ClientResourceManager] Bandwidth limit changed from " + bandwidthLimit + " => " + newLimit);
					bandwidthLimit = newLimit;
					changed = true;
				}
				else {
					changed = false;
				}
				
				if (desktopConnection != null && changed) {
					System.out.println("[ClientResourceManager] Update bandwidth limit to Desktop Server : " + bandwidthLimit);
					desktopConnection.onClientResourceChanged(bandwidthLimit);
				}
				
				if (webcamConnection != null && changed) {
					System.out.println("[ClientResourceManager] Update bandwidth limit to Webcam Server : " + bandwidthLimit);
					webcamConnection.onClientResourceChanged(bandwidthLimit);
				}
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private static ResourceManagerThread resourceRunnable;

	public static void start() {
		resourceRunnable = new ClientResourceManager().new ResourceManagerThread();
		running = true;
		new Thread(resourceRunnable).start();
	}

	public static void setDesktopConnection(DesktopConnection desktopConnection) {
		ClientResourceManager.desktopConnection = desktopConnection;
	}

	public static void setWebcamConnection(WebcamConnection webcamConnection) {
		ClientResourceManager.webcamConnection = webcamConnection;
	}
	
	private static String readFile(String file) {
		String value = "";
		
		try {
			FileInputStream fileStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				value += line;
			}
			
			fileStream.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return value;
	}
}
