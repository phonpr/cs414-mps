package org.cs414.mp2.server;

public class ResourceManager {
	private static int currentBandwidth = 0;
	private static int bandwidthLimit = 0;
	
	public static int getCurrentBandwidth() {
		return currentBandwidth;
	}
	
	public static void addBandwidth(int bandwidth) {
		currentBandwidth += bandwidth;
		System.out.println("Current Bandwidth : " + currentBandwidth + " / " + bandwidthLimit);
	}
	
	public static void subtractBandwidth(int bandwidth) {
		currentBandwidth -= bandwidth;
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
}
