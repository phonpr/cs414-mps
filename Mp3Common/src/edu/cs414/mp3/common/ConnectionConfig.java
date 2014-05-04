package edu.cs414.mp3.common;

public class ConnectionConfig {
	
	public static final String WEBCAM_SERVER_HOST	= "127.0.0.1";
	public static final int WEBCAM_SERVER_PORT		= 9000;
	
	public static final String DESKTOP_SERVER_HOST	= "127.0.0.1";
	public static final int DESKTOP_SERVER_PORT		= 9001;
	
	public static final int DESKTOP_VIDEO_UDP_SINK	= 5000;
	public static final int DESKTOP_VIDEO_RTCP_SRC	= 5005;
	public static final int DESKTOP_VIDEO_RTCP_SINK	= 5001;
	
	public static final int DESKTOP_AUDIO_UDP_SINK	= 5002;
	public static final int DESKTOP_AUDIO_RTCP_SRC	= 5007;
	public static final int DESKTOP_AUDIO_RTCP_SINK	= 5003;
}
