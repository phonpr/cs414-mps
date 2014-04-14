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

	private static final int smallVideoBitrate = 17158;

	private static final int smallVideoMax = 436950;

	private static final int largeVideoBitrate = 61988;

	private static final int largeVideoMax = 1557700;

	private static final int audioRequiredBandwidth = 8000;

	public static boolean doAdmission(byte video) {
		if(requiredFrames(available, video) >= MIN_FRAMERATE) {
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

	public static int requiredFrames(int available, byte video) {
		if(video == 0)
			return (available - 8000) / smallVideoBitrate;
		else if(video == 1)
			return (available - 8000) / largeVideoBitrate;
		return 0;
	}

	public static int getRequestedRate(byte video) {
		if(video == 0) {
			if (smallVideoMax < available) {
				return largeVideoMax;
			} else {
				return available;
			}
		}
		else if(video == 1) {
			if (largeVideoMax < available) {
				return smallVideoMax;
			} else {
				return available;
			}
		}

		return 0;
	}
}
