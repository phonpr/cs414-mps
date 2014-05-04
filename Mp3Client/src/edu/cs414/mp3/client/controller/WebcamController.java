package edu.cs414.mp3.client.controller;

import edu.cs414.mp3.common.ConnectionConfig;

import org.gstreamer.*;

import edu.cs414.mp3.client.ButtonGroup;
import edu.cs414.mp3.client.VideoWindow;
import edu.cs414.mp3.client.connection.WebcamConnection;
import org.gstreamer.elements.AppSink;

import org.gstreamer.elements.good.RTPBin;

public class WebcamController implements Controller, Runnable {
	
	private ButtonGroup webcamButtonGroup;
	
	private Pipeline videoPipeline;
	private VideoWindow videoWindow;
	private WebcamConnection webcamConnection;

	private Element muter;
	private boolean passive;
	
	public WebcamController(ButtonGroup webcamButtonGroup) {
		this.webcamButtonGroup = webcamButtonGroup;
	}

	//All these things are for the session monitoring
	long totalVideoBuffersSeen = 0;
	int totalVideoBuffersSince = 0;
	long totalAudioBufferSeen = 0;
	long lastBandwidthTime = 0;
	int bandwidthSinceLast = 0;
	long lastAudioBuffer = 0;
	long lastVideoBuffer = 0;
	long totalJitter = 0;

	public void buildPipeline() {
		/*
		failure rate:
		jitter:
		frame rate:
		synchronization skew:
		bandwidth:
		 */

		videoPipeline = new Pipeline("VideoTest");
		
		final Element vidrtpdepay = ElementFactory.make("rtpjpegdepay", "viddepay");
		final Element audrtpdepay = ElementFactory.make("rtppcmadepay", "auddepay");;

		Element vidQ = ElementFactory.make("queue", "vidQ");
		
		Element viddecode = ElementFactory.make("jpegdec", "viddec");
		Element colorspace = ElementFactory.make("ffmpegcolorspace", "colorspace");

		Element vidUDPSrc = ElementFactory.make("udpsrc", "vidUDPsrc");
		Element vidRTCPSink = ElementFactory.make("udpsink", "vidRTCPsink");
		Element vidRTCPSrc = ElementFactory.make("udpsrc", "vidRTCPsrc");

		vidUDPSrc.setCaps(Caps.fromString("application/x-rtp, media=(string)video, clock-rate=(int)90000, encoding-name=(string)JPEG, ssrc=(uint)2934514725, payload=(int)96, clock-base=(uint)2718573098, seqnum-base=(uint)11320"));
		vidUDPSrc.set("port", ConnectionConfig.DESKTOP_VIDEO_UDP_SINK + 1000);
		vidRTCPSrc.set("port", ConnectionConfig.DESKTOP_VIDEO_RTCP_SRC + 1000);
		vidRTCPSink.set("host", ConnectionConfig.DESKTOP_SERVER_HOST);
		vidRTCPSink.set("port", ConnectionConfig.DESKTOP_VIDEO_RTCP_SINK + 1000);

		final Element videoElement = videoWindow.getVideoComponent().getElement();

		final Element videoMonitorTee = ElementFactory.make("tee", "videoMonitorTee");
		final Element videoMonitorQ = ElementFactory.make("queue", "videoMonitorQ");
		final Element audioMonitorTee = ElementFactory.make("tee", "audioMonitorTee");
		final Element audioMonitorQ = ElementFactory.make("queue", "audioMonitorQ");


		final AppSink videoApp = (AppSink) ElementFactory.make("appsink", "videodatasink");
		videoApp.set ("emit-signals", true);
		videoApp.setSync(false);
		videoApp.connect(new AppSink.NEW_BUFFER (){
			@Override
			public void newBuffer(AppSink arg0) {
				Buffer temp = arg0.getLastBuffer();
				long time = temp.getTimestamp().toMillis();
				int size = temp.getSize();

				if(time - lastBandwidthTime > 1000) {
					lastBandwidthTime = time;
					//display the bandwidth
					videoWindow.updateBandwidth(bandwidthSinceLast / 1000);
					videoWindow.updateFramerate(totalVideoBuffersSince);
					bandwidthSinceLast = 0;
					totalVideoBuffersSince = 0;
				}
				else {
					bandwidthSinceLast += size;
					totalVideoBuffersSince++;
				}

				videoWindow.updateJitter((int) (time - lastVideoBuffer));

				totalVideoBuffersSeen++;
				totalJitter += time - lastVideoBuffer;
				lastVideoBuffer = time;

				if(!passive)
					videoWindow.updateSkew((int)(lastVideoBuffer - lastAudioBuffer));
			}
		});

		RTPBin rtp = new RTPBin("rtp");

		videoPipeline.add(rtp);
		videoPipeline.addMany(vidUDPSrc, vidRTCPSink, vidRTCPSrc, vidrtpdepay, viddecode, colorspace, vidQ, videoElement);
		videoPipeline.addMany(videoMonitorTee, videoMonitorQ, videoApp);

		rtp.connect(new Element.PAD_ADDED() {
			public void padAdded(Element element, Pad pad) {
				System.out.println("pad added");
				if (pad.getName().startsWith("recv_rtp_src_1")) {
					System.out.println("audio added");
					pad.link(audrtpdepay.getStaticPad("sink"));
				}
				if (pad.getName().startsWith("recv_rtp_src_0")) {
					System.out.println("video added");
					pad.link(vidrtpdepay.getStaticPad("sink"));
				}
			}
		});

		vidrtpdepay.link(viddecode, videoMonitorTee, vidQ, colorspace, videoElement);

		videoMonitorTee.link(videoMonitorQ);
		videoMonitorQ.link(videoApp);

		vidUDPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtp_sink_0"));
		vidRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_0"));
		rtp.getRequestPad("send_rtcp_src_0").link(vidRTCPSink.getStaticPad("sink"));
		
		vidRTCPSink.set("sync", false); vidRTCPSink.set("async", false);

		if (!passive) {
			Element audQ = ElementFactory.make("queue", "audQ");
			
			Element audDec = ElementFactory.make("alawdec", "auddec");
			Element audConvert = ElementFactory.make("audioconvert", "audconvert");
			Element audResample = ElementFactory.make("audioresample", "audresample");
			muter = ElementFactory.make("volume", "mutecontrol");
			Element audSink = ElementFactory.make("autoaudiosink", "audsink");
			
			Element audUDPSrc = ElementFactory.make("udpsrc", "audUDPsrc");
			Element audRTCPSink = ElementFactory.make("udpsink", "audRTCPsink");
			Element audRTCPSrc = ElementFactory.make("udpsrc", "audRTCPsrc");

			audUDPSrc.setCaps(Caps.fromString("application/x-rtp, media=(string)audio, clock-rate=(int)8000, encoding-name=(string)PCMA, ssrc=(uint)3824386182, payload=(int)8, clock-base=(uint)921092443, seqnum-base=(uint)8008"));
			audUDPSrc.set("port", ConnectionConfig.DESKTOP_AUDIO_UDP_SINK + 1000);
			audRTCPSrc.set("port", ConnectionConfig.DESKTOP_AUDIO_RTCP_SRC + 1000);
			audRTCPSink.set("host", ConnectionConfig.DESKTOP_SERVER_HOST);
			audRTCPSink.set("port", ConnectionConfig.DESKTOP_AUDIO_RTCP_SINK + 1000);

			final AppSink audioApp = (AppSink) ElementFactory.make("appsink", "audiodatasink");
			audioApp.set ("emit-signals", true);
			audioApp.setSync(false);
			audioApp.connect(new AppSink.NEW_BUFFER (){
				@Override
				public void newBuffer(AppSink arg0) {
					Buffer temp = arg0.getLastBuffer();
					long time = temp.getTimestamp().toMillis();
					int size = temp.getSize();

					if(time - lastBandwidthTime > 1000) {
						lastBandwidthTime = time;
						//display the bandwidth
						videoWindow.updateBandwidth(bandwidthSinceLast / 1000);
						bandwidthSinceLast = 0;

						videoWindow.updateFramerate(totalVideoBuffersSince);
						totalVideoBuffersSince = 0;
					}
					else {
						bandwidthSinceLast += size;
					}

					totalAudioBufferSeen++;
					lastAudioBuffer = time;
				}
			});

			videoPipeline.addMany(audUDPSrc, audRTCPSink, audRTCPSrc, audDec, audrtpdepay, audConvert, audResample, muter, audSink, audQ);
			videoPipeline.addMany(audioMonitorTee, audioMonitorQ, audioApp);


			audrtpdepay.link(audioMonitorTee, audQ, audDec, audConvert, audResample, muter, audSink);
			audioMonitorTee.link(audioMonitorQ);
			audioMonitorQ.link(audioApp);

			audUDPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtp_sink_1"));
			audRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_1"));
			rtp.getRequestPad("send_rtcp_src_1").link(audRTCPSink.getStaticPad("sink"));

			audRTCPSink.set("sync", false); audRTCPSink.set("async", false);
		}
	}
	
	public void tearDown() {
		videoPipeline.setState(State.READY);
		videoPipeline.setState(State.NULL);
		videoPipeline.remove(videoWindow.getVideoComponent().getElement());
		videoPipeline.dispose();

		lastBandwidthTime = 0;

		videoPipeline = null;
	}

	@Override
	public void run() {
		// connect to server and start playing
		webcamConnection = new WebcamConnection();
		if (!webcamConnection.onPlay()) {
			return;
		}
		else {
			webcamButtonGroup.onPlay();
			ClientResourceManager.setWebcamConnection(webcamConnection);
		}

		passive = true;
		buildPipeline();
		
		videoPipeline.setState(State.READY);
		videoPipeline.setState(State.PLAYING);
		videoWindow.setVisible(true);
	}

	@Override
	public void onPlay() {
		System.out.println("[WebcamController] onPlay()");
		
		videoWindow = new VideoWindow();
		videoWindow.initializeComponents();
		
		// this will start connection to the server, and play right away
		new Thread(this).start();
	}

	@Override
	public void onStop() {
		System.out.println("[WebcamController] onStop()");
		
		// reset and close has priority
		webcamButtonGroup.onReset();
		videoWindow.dispose();
		videoWindow = null;
		
		if (webcamConnection.onStop()) {
			videoPipeline.setState(State.NULL);
			ClientResourceManager.setWebcamConnection(null);
		}
	}

	@Override
	public void onPause() {
		System.out.println("[WebcamController] onPause()");
		
		if (webcamConnection.onPause()) {
			webcamButtonGroup.onPause();
			videoPipeline.setState(State.PAUSED);
		}
	}

	@Override
	public void onResume() {
		System.out.println("[WebcamController] onResume()");
		
		if (webcamConnection.onResume()) {
			webcamButtonGroup.onResume();
			videoPipeline.setState(State.PLAYING);
		}
	}

	@Override
	public void onMute() {
		System.out.println("[WebcamController] onMute()");

		if (webcamConnection.isHdMode()) {
			if((Boolean) muter.get("mute")) {
				muter.set("mute", false);
			} else {
				muter.set("mute", true);
			}
		}	
	}

	@Override
	public void onToggleHdMode() {
		System.out.println("[DesktopController] onToggleHdMode()");

		System.out.println(webcamConnection.isHdMode());

		if (webcamConnection.isHdMode()) {
			if (webcamConnection.onSdMode()) {
				tearDown();
				passive = true;
				buildPipeline();
				
				videoPipeline.setState(State.READY);
				videoPipeline.setState(State.PLAYING);
			}
		}
		else {
			// currently SD mode
			if (webcamConnection.onHdMode()) {
				tearDown();
				passive = false;
				buildPipeline();
				
				videoPipeline.setState(State.READY);
				videoPipeline.setState(State.PLAYING);
			}
		}
	}

}
