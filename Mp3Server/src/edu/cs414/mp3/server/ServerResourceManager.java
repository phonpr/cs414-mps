package edu.cs414.mp3.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.cs414.mp3.server.streamer.Streamer;

public class ServerResourceManager {
	private static long currentBandwidth = 0;
	private static long bandwidthLimit = 0;
	private static boolean running = false;
	
	private static Streamer streamer = null;
	
	private class ResourceManagerThread implements Runnable {
		@Override
		public void run() {
			boolean changed = false;
			
			while (running) {
				long newResource = Long.parseLong(readFile("resource.txt"));
				
				if (bandwidthLimit != newResource) {
					System.out.println("[ServerResourceManager] Bandwidth limit changed from " + bandwidthLimit + " => " + newResource);
					bandwidthLimit = newResource;
					changed = true;
				}
				else {
					changed = false;
				}
				
				if (streamer != null && changed) {
					System.out.println("[ServerResourceManager] Update bandwidth limit to Streamer : " + bandwidthLimit);
					streamer.onServerResourceChanged(bandwidthLimit);
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
		resourceRunnable = new ServerResourceManager().new ResourceManagerThread();
		running = true;
		new Thread(resourceRunnable).start();
	}
	
	public static boolean addBandwidth(int bandwidth) {
		if (currentBandwidth + bandwidth <= bandwidthLimit) {
			currentBandwidth += bandwidth;
			System.out.println("[ServerResourceManager] Current bandwidth : " + currentBandwidth + " / " + bandwidthLimit);
			return true;
		}
		else {
			System.out.println("[ServerResourceManager] Not enough server resource.");
			return false;
		}
	}
	
	public static void subtractBandwidth(int bandwidth) {
		if (currentBandwidth - bandwidth >= 0) {
			currentBandwidth -= bandwidth;
		}
		System.out.println("[ServerResourceManager] Current bandwidth : " + currentBandwidth + " / " + bandwidthLimit);
	}
	
	public static boolean isBandwidthAvailable(int bandwidth) {
		return currentBandwidth + bandwidth <= bandwidthLimit;
	}
	
	public static long getBandwidthLimit() {
		return bandwidthLimit;
	}
	
	public static void setStreamer(Streamer streamer) {
		ServerResourceManager.streamer = streamer;
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
