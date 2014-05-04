package edu.cs414.mp3.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.gstreamer.Gst;

import edu.cs414.mp3.common.ConnectionConfig;
import edu.cs414.mp3.server.connection.ConnectionControl;
import edu.cs414.mp3.server.connection.DesktopConnectionControl;
import edu.cs414.mp3.server.connection.WebcamConnectionControl;

public class ServerMain {
	private static final String WEBCAM	= "webcam";
	private static final String DESKTOP = "desktop";
	
	private static boolean serverRunning = true;

	public static void main(String[] args) {
		Gst.init();
		String arg = WEBCAM;
//		String arg = DESKTOP;
		
		ServerResourceManager.start();
		
		switch (arg) {
		case WEBCAM:
			System.out.println("[ServerMain] Starting Webcam Streaming Server...");
			onStartWebcamServer();
			break;
		case DESKTOP:
			System.out.println("[ServerMain] Starting Desktop Streaming Server...");
			onStartDesktopServer();
			break;
		default:
			break;
		}
	}

	private static void onStartWebcamServer() {
		// any webcam server bootstrapping
		
		startServer(new WebcamConnectionControl(), ConnectionConfig.WEBCAM_SERVER_PORT);
	}
	
	private static void onStartDesktopServer() {
		// any desktop server bootstrapping
		
		startServer(new DesktopConnectionControl(), ConnectionConfig.DESKTOP_SERVER_PORT);
	}
	
	private static void startServer(ConnectionControl connectionControl, int listenerPort) {
		ServerSocket listenerSocket = null;
		
		try {
			listenerSocket = new ServerSocket();
			listenerSocket.setReuseAddress(true);
			listenerSocket.bind(new InetSocketAddress(listenerPort));
			
			while (serverRunning) {
				Socket clientSocket = listenerSocket.accept();
				System.out.println("[ServerMain] Client Connected from : " + clientSocket.getInetAddress().getHostAddress());
				
				connectionControl.initialize(clientSocket);
				new Thread(connectionControl).start();
			}
			
			listenerSocket.close();
			
		} catch (IOException e) {
			System.out.println("[ServerMain] Failed to initialize server socket.");
			System.out.println(e);
		}
	}
}
