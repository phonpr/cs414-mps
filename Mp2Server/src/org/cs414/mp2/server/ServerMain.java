package org.cs414.mp2.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.gstreamer.Gst;

public class ServerMain {
	
	private static final int SERVER_PORT = 7000;
	private static final int UDPSINK_PORT = 5001;
	
	private static boolean serverRunning = true;
	
	public static void main(String[] args) {
		Gst.init();
		
		System.out.println("Starting server...");

		int bandwidthLimit = 0;
		try {
			bandwidthLimit = Integer.parseInt(readFile("resource.txt"));
		}
		catch (Exception e) {}
		finally {
			System.out.println("Bandwidth Capacity : " + bandwidthLimit);
			ResourceManager.setBandwidthLimit(bandwidthLimit);
		}
		
		startServer();
	}

	private static void startServer() {
		ServerSocket listenerSocket = null;
		
		try {
			listenerSocket = new ServerSocket();
			listenerSocket.setReuseAddress(true);
			listenerSocket.bind(new InetSocketAddress(SERVER_PORT));
			
			while (serverRunning) {
				final Socket socket = listenerSocket.accept();
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						System.out.println("Client Connected from : " + socket.getInetAddress().getHostAddress());
						
						// control channel
						new Thread(new ControlChannel(socket)).start();
					}
				}).start();
			}
			
		} catch (IOException e) {
			System.out.println("[ERROR] Failed to initialize server socket.");
			System.out.println(e);
		}
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