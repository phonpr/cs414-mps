package edu.cs414.mp3.client.controller;

import org.gstreamer.*;

import edu.cs414.mp3.client.ButtonGroup;
import edu.cs414.mp3.client.VideoWindow;
import edu.cs414.mp3.client.connection.WebcamConnection;
import org.gstreamer.elements.good.RTPBin;

public class WebcamController implements Controller, Runnable {
	
	private ButtonGroup webcamButtonGroup;
	
	private Pipeline videoPipeline;
	private VideoWindow videoWindow;
	private WebcamConnection webcamConnection;
	
	public WebcamController(ButtonGroup webcamButtonGroup) {
		this.webcamButtonGroup = webcamButtonGroup;
	}

	@Override
	public void run() {
		videoPipeline = new Pipeline();

		// connect to server and start playing
		webcamConnection = new WebcamConnection();
		if (!webcamConnection.onPlay()) {
			return;
		}
		else {
			webcamButtonGroup.onPlay();
		}

		videoWindow.setVisible(true);

		final Element vidrtpdepay = ElementFactory.make("rtpjpegdepay", "viddepay");
		final Element audrtpdepay = ElementFactory.make("rtppcmadepay", "auddepay");

		Element viddecode = ElementFactory.make("jpegdec", "viddec");
		Element colorspace = ElementFactory.make("ffmpegcolorspace", "colorspace");

		Element audDec = ElementFactory.make("alawdec", "auddec");
		Element audConvert = ElementFactory.make("audioconvert", "audconvert");
		Element audResample = ElementFactory.make("audioresample", "audresample");
		Element audSink = ElementFactory.make("alsasink", "audsink");

		Element vidQ = ElementFactory.make("queue", "vidQ");
		Element audQ = ElementFactory.make("queue", "audQ");

		Element vidUDPSrc = ElementFactory.make("udpsrc", "vidUDPsrc");
		Element vidRTCPSink = ElementFactory.make("udpsink", "vidRTCPsink");
		Element vidRTCPSrc = ElementFactory.make("udpsrc", "vidRTCPsrc");

		Element audUDPSrc = ElementFactory.make("udpsrc", "audUDPsrc");
		Element audRTCPSink = ElementFactory.make("udpsink", "audRTCPsink");
		Element audRTCPSrc = ElementFactory.make("udpsrc", "audRTCPsrc");

		vidUDPSrc.setCaps(Caps.fromString("application/x-rtp, media=(string)video, clock-rate=(int)90000, encoding-name=(string)JPEG, ssrc=(uint)2934514725, payload=(int)96, clock-base=(uint)2718573098, seqnum-base=(uint)11320"));
		vidUDPSrc.set("port", 6000);
		vidRTCPSrc.set("port", 6001);
		vidRTCPSink.set("host", "localhost");
		vidRTCPSink.set("port", 6005);

		audUDPSrc.setCaps(Caps.fromString("application/x-rtp, media=(string)audio, clock-rate=(int)8000, encoding-name=(string)PCMA, ssrc=(uint)3824386182, payload=(int)8, clock-base=(uint)921092443, seqnum-base=(uint)8008"));
		audUDPSrc.set("port", 6002);
		audRTCPSrc.set("port", 6003);
		audRTCPSink.set("host", "localhost");
		audRTCPSink.set("port", 5007);

		final Element videoElement = videoWindow.getVideoComponent().getElement();

		RTPBin rtp = new RTPBin("rtp");

		videoPipeline.addMany(vidUDPSrc, vidRTCPSink, vidRTCPSrc, audUDPSrc, audRTCPSink, audRTCPSrc, rtp, audDec, vidrtpdepay, viddecode, colorspace, vidQ, videoElement, audrtpdepay, audConvert, audResample, audSink, audQ);
		vidrtpdepay.link(viddecode, vidQ, colorspace, videoElement);
		audrtpdepay.link(audQ, audDec, audConvert, audResample, audSink);

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

		vidUDPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtp_sink_0"));
		vidRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_0"));
		rtp.getRequestPad("send_rtcp_src_0").link(vidRTCPSink.getStaticPad("sink"));

		audUDPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtp_sink_1"));
		audRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_1"));
		rtp.getRequestPad("send_rtcp_src_1").link(audRTCPSink.getStaticPad("sink"));

		audRTCPSink.set("sync", false); audRTCPSink.set("async", false);
		vidRTCPSink.set("sync", false); vidRTCPSink.set("async", false);

		System.out.println(rtp.getSources());
		System.out.println(rtp.getPads());

		videoPipeline.setState(State.READY);
		videoPipeline.setState(State.PLAYING);
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
		
		// mute is controlled under client side
	}

	@Override
	public void onToggleHdMode() {
		System.out.println("[DesktopController] onToggleHdMode()");
		
		if (webcamConnection.isHdMode()) {
			if (webcamConnection.onSdMode()) {
				
			}
		}
		else {
			// currently SD mode
			if (webcamConnection.onHdMode()) {
				
			}
		}
	}

}
