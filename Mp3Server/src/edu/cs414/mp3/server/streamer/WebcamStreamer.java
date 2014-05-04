package edu.cs414.mp3.server.streamer;

import org.gstreamer.*;
import org.gstreamer.elements.good.RTPBin;

public class WebcamStreamer extends Streamer {

	@Override
	public void run() {
		int isActiveMode = 0;
		int framerate = 10;

		createPipeline(framerate, isActiveMode);

		play();
	}
	
	public void init(String host) {
		hostAddress = host;
	}

	private Pipeline pipe = new Pipeline();
	private String hostAddress;

	public void createPipeline(int framerate, int isActiveMode) {
		/* Record pipeline:
		gst-launch-0.10 v4l2src ! videorate ! video/x-raw-yuv,width=640,height=480,framerate=20/1 !
		queue ! jpegenc ! queue ! mux. autoaudiosrc  ! audioconvert !
		audio/x-raw-int,rate=8000,channels=1 ! alawenc ! mux. avimux name=mux ! filesink location=testvideo.avi
		 */

		/*
		gst-launch-0.10 v4l2src ! videorate ! video/x-raw-yuv,width=640,height=480,framerate=20/1 ! ffenc_mpeg4 ! filesink location=testvideo.mp4
		 */

		/*
		failure rate:
		jitter:
		frame rate:
		synchronization skew:
		bandwidth:
		 */

		isActiveMode = 1;

		/* All of this stuff is from the RecordController from MP1 */
		final Element videoSrc = ElementFactory.make("v4l2src", "source");
		final Element audioSrc = ElementFactory.make("alsasrc", "soundsrc");
		final Element videoTee = ElementFactory.make("tee", "videoTee");
		final Element colorspace = ElementFactory.make("ffmpegcolorspace", "colorspace");

		final Element videofilter = ElementFactory.make("capsfilter", "flt");
		System.out.println(framerate);

		if(isActiveMode == 1) {
			videofilter.setCaps(Caps.fromString("video/x-raw-yuv,  width=" + 640 + ", height=" + 480 + ", framerate=" + 20 + "/1"));
		}
		else {
			videofilter.setCaps(Caps.fromString("video/x-raw-yuv,  width=" + 320 + ", height=" + 240 + ", framerate=" + 20 + "/1"));
		}

		final Element videoMux = ElementFactory.make("avimux", "avimux");

		final Element audiofilter = ElementFactory.make("capsfilter", "aflt");
		audiofilter.setCaps(Caps.fromString("audio/x-raw-int, channels=1, endianness=1234, width=16, depth=16, signed=true, rate=8000"));
		final Element audioConverter = ElementFactory.make("audioconvert", "audioconvert");
		Element videoEnc = ElementFactory.make("jpegenc", "Encoder");
		Element audioEnc = ElementFactory.make("alawenc", "soundEncoder");
		Element videosink = ElementFactory.make("xvimagesink", "videoSink");

		Element vidRate = ElementFactory.make("videorate", "ratechange");
		if(isActiveMode == 1) {
			vidRate.set("max-rate", framerate);
		}
		else {
			vidRate.set("max-rate", 10);
		}

		Element vidrtppay = ElementFactory.make("rtpjpegpay", "vidpay");

		Element audrtppay = ElementFactory.make("rtppcmapay", "audpay");
		RTPBin rtp = new RTPBin("rtp");
		Element vidUDPSink = ElementFactory.make("udpsink", "vidudpsink");
		Element vidRTCPSrc = ElementFactory.make("udpsrc", "vidrtcpsrc");

		Element vidRTCPSink = ElementFactory.make("udpsink", "vidrtcpsink");
		vidUDPSink.set("host", hostAddress);
		vidUDPSink.set("port", 6000);
		vidRTCPSrc.set("port", 6005);
		vidRTCPSink.set("host", hostAddress);
		vidRTCPSink.set("port", 6001);

		Element audUDPSink = ElementFactory.make("udpsink", "aududpsink");
		Element audRTCPSrc = ElementFactory.make("udpsrc", "audrtcpsrc");
		Element audRTCPSink = ElementFactory.make("udpsink", "audrtcpsink");
		audUDPSink.set("host", hostAddress);
		audUDPSink.set("port", 6002);
		audRTCPSrc.set("port", 6007);
		audRTCPSink.set("host", hostAddress);
		audRTCPSink.set("port", 6003);


		final Element videoQ = ElementFactory.make("queue", "videoQ");
		final Element audioQ = ElementFactory.make("queue", "audioQ");
		final Element testQ = ElementFactory.make("queue", "testQ");

		pipe.addMany(videoSrc, videoTee, colorspace, videofilter, videosink, videoEnc, videoMux, vidRate,	//video stuff
				rtp, vidrtppay, vidRTCPSrc, vidRTCPSink, vidUDPSink,	//network stuff
				videoQ, audioQ, testQ);	//queues

		if(isActiveMode == 1) {
			pipe.addMany(audioSrc, audioConverter, audiofilter, audioEnc,	//audio stuff
					audrtppay, audRTCPSrc, audRTCPSink, audUDPSink);	//network stuff
		}

		videoSrc.link(videoTee);

		//First branch of tee: to the network
		videoTee.link(videoQ);
		videoQ.link(colorspace);
		colorspace.link(videofilter);
		videofilter.link(vidRate);
		vidRate.link(videoEnc);
		videoEnc.link(vidrtppay);

		if(isActiveMode == 1) {
			audioSrc.link(audioQ);
			audioQ.link(audiofilter);
			audiofilter.link(audioEnc);
			audioEnc.link(audrtppay);
		}

		//Second (debug) branch: to xvimagesink
		videoTee.link(testQ);
		testQ.link(videosink);

		vidrtppay.getStaticPad("src").link(rtp.getRequestPad("send_rtp_sink_0")); // Link vid to rtp
		rtp.getStaticPad("send_rtp_src_0").link(vidUDPSink.getStaticPad("sink")); // Link rtp to udp
		rtp.getRequestPad("send_rtcp_src_0").link(vidRTCPSink.getStaticPad("sink")); // Link rtcp to udp
		vidRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_0")); // Link udp to rtcp

		vidRTCPSink.set("sync", false);
		vidRTCPSink.set("async", false);

		if(isActiveMode == 1) {
			audrtppay.getStaticPad("src").link(rtp.getRequestPad("send_rtp_sink_1")); // Link vid to rtp
			rtp.getStaticPad("send_rtp_src_1").link(audUDPSink.getStaticPad("sink")); // Link rtp to udp
			rtp.getRequestPad("send_rtcp_src_1").link(audRTCPSink.getStaticPad("sink")); // Link rtcp to udp
			audRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_1")); // Link udp to rtcp

			audRTCPSink.set("sync", false);
			audRTCPSink.set("async", false);
		}


		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean print = false;

				while (print) {
					System.out.println("TEST: " + testQ.get("current-level-time"));
					System.out.println("AUDIO: " + audioQ.get("current-level-time"));
					System.out.println("VIDEO: " + videoQ.get("current-level-time"));

					try {
						Thread.sleep(3000);
					}
					catch(Exception e) {
					}
				}
			}
		}).start();
	}

	public void play() {
		pipe.setState(State.READY);

		pipe.setState(State.PLAYING);
	}

	public void togglePause() {
		if (pipe.getState() == State.PAUSED) {
			// when resuming pased, set to normal playing
			pipe.setState(State.PLAYING);
		}
		else {
			pipe.setState(State.PAUSED);
		}
	}


}
