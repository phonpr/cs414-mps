package edu.cs414.mp3.common.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ResourceManager {
	private static int currentBandwidth = 0;
	private static int bandwidthLimit = 0;
	
	public static int getCurrentBandwidth() {
		return currentBandwidth;
	}
	
	public static void addBandwidth(int bandwidth) {
		if (currentBandwidth + bandwidth <= bandwidthLimit) {
			currentBandwidth += bandwidth;
			System.out.println("Current Bandwidth : " + currentBandwidth + " / " + bandwidthLimit);
		}
		else {
			System.out.println("Not enough server resource");
		}
	}
	
	public static void subtractBandwidth(int bandwidth) {
		if (currentBandwidth - bandwidth >= 0) {
			currentBandwidth -= bandwidth;
		}
		System.out.println("Current Bandwidth : " + currentBandwidth + " / " + bandwidthLimit);
	}
	
	public static int getBandwidthLimit() {
		return bandwidthLimit;
	}
	
	public static void setBandwidthLimit(int bandwidth) {
		bandwidthLimit = bandwidth;
	}
	
	public static boolean isBandwidthAvailable(int bandwidth) {
		System.out.println("Requested : " + (currentBandwidth + bandwidth) + " / " + bandwidthLimit);
		return currentBandwidth + bandwidth <= bandwidthLimit;
	}

	private static int getAvailableFromFile() {
		try {
			File resourceFile = new File("resource.txt");
			BufferedReader reader = new BufferedReader(new FileReader(resourceFile));
			String nextLine = reader.readLine();
			System.out.println(nextLine);
			bandwidthLimit = Integer.parseInt(nextLine);
		}
		catch(IOException e) {
		}
		return bandwidthLimit;
	}

	public static class ResourceChecker implements Runnable {
		boolean checkResources = true;

		public void run() {
			while(checkResources) {
				getAvailableFromFile();

				try {
					Thread.sleep(5000);
				}
				catch(Exception e) {
				}
			}
		}

		public void stopChecking() {
			checkResources = false;
		}
	}

}
