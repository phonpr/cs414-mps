package org.cs414.mp2.client.controllers.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by bill on 4/12/14.
 */
public class ResourceNegotiation {
	public static boolean doAdmission() {
		try {
			if (getAvailable() >= getRequired()) {
				return true;
			}
		}
		catch(IOException e) {
			return false;
		}

		return false;
	}

	private static int getAvailable() throws IOException {
		File resourceFile = new File("resource.txt");
		BufferedReader reader = new BufferedReader(new FileReader(resourceFile));

		int available = Integer.parseInt(reader.readLine());

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
