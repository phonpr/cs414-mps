package org.cs414.mp2.client.controllers.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by bill on 4/12/14.
 */
public class ResourceNegotiation {
	private static int available = getAvailableFromFile();

	private static final int MIN_FRAMERATE = 15;

	private static final int smallVideoBitrate = 0;

	private static final int largeVideoBitrate = 29002;

	private static final int largeVideoMax = 733050;

	private static final int audioRequiredBandwidth = 8000;

	public static boolean doAdmission() {
		if(requiredFrames(available) > MIN_FRAMERATE) {
			return true;
		}
		return false;
	}

	public static void setAvailable(int newMax) {
		available = newMax;
	}

	public static int getAvailable() {
		return available;
	}

	private static int getAvailableFromFile() {
		try {
			File resourceFile = new File("resource.txt");
			BufferedReader reader = new BufferedReader(new FileReader(resourceFile));
			String nextLine = reader.readLine();
			System.out.println(nextLine);
			available = Integer.parseInt(nextLine);
		}
		catch(IOException e) {
		}
		return available;
	}

	public static int requiredFrames(int available) {
		return (available - 8000) / 29002;
	}

	public static int getRequestedRate() {
		if (largeVideoMax < available) {
			return largeVideoMax;
		}
		else {
			return available;
		}
	}
}
