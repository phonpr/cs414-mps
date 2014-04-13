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

	public static boolean doAdmission() {
		if (getAvailable() >= getRequired()) {
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

	private static int getRequired() {
		//TODO: CALCULATIONS HERE
		return 0;
	}

	public static boolean doNegotiation() {

		return false;
	}
}
