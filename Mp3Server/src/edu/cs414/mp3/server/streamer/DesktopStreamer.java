package edu.cs414.mp3.server.streamer;

import java.io.IOException;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.elements.good.RTPBin;

import edu.cs414.mp3.common.ConnectionConfig;

public class DesktopStreamer implements Runnable {
	private String hostAddress;
	private boolean isPassive;
	private int framerate;
	
	public void init(String hostname) {
		hostAddress = hostname;
		isPassive = false;
		framerate = 10;
	}
	
	@Override
	public void run() {
		
		Element videoSrc = ElementFactory.make("ximagesrc", "vidsrc");
		
		if (isPassive) {
			videoSrc.set("endx", 319);
			videoSrc.set("endy", 239);	
		} else {
			videoSrc.set("endx", 639);
			videoSrc.set("endy", 479);	
		}
		
		Element vidQ = ElementFactory.make("queue", "vidQ");
		
		Element videofilter1 = ElementFactory.make("capsfilter", "flt"); 
        videofilter1.setCaps(Caps.fromString("video/x-raw-rgb, framerate=30/1"));
        Element videofilter2 = ElementFactory.make("capsfilter", "flt2");
        videofilter2.setCaps(Caps.fromString("video/x-raw-yuv"));

		Element vidRate = ElementFactory.make("videorate", "ratechange");
		Element vidColor = ElementFactory.make("ffmpegcolorspace", "colorspace");
		Element vidEnc = ElementFactory.make("jpegenc", "stupidenc");
		
		if (isPassive) {
			vidRate.set("max-rate", 10);
		} else {
			vidRate.set("max-rate", framerate);
		}
		
		Element vidrtppay = ElementFactory.make("rtpjpegpay", "vidpay");

		RTPBin rtp = new RTPBin("rtp");

		Element vidUDPSink = ElementFactory.make("udpsink", "vidudpsink");
		Element vidRTCPSrc = ElementFactory.make("udpsrc", "vidrtcpsrc");
		Element vidRTCPSink = ElementFactory.make("udpsink", "vidrtcpsink");
		vidUDPSink.set("host", hostAddress);
		vidUDPSink.set("port", ConnectionConfig.DESKTOP_VIDEO_UDP_SINK);
		vidRTCPSrc.set("port", ConnectionConfig.DESKTOP_VIDEO_RTCP_SRC);
		vidRTCPSink.set("host", hostAddress);
		vidRTCPSink.set("port", ConnectionConfig.DESKTOP_VIDEO_RTCP_SINK);
		Pipeline pipe = new Pipeline();

		pipe.add(rtp);
		pipe.addMany(videoSrc, vidColor, videofilter1, videofilter2, vidRate, vidEnc, vidQ, vidrtppay, vidUDPSink, vidRTCPSink, vidRTCPSrc);
		
		videoSrc.link(vidQ, videofilter1, vidColor, videofilter2, vidRate, vidEnc, vidrtppay);

		vidrtppay.getStaticPad("src").link(rtp.getRequestPad("send_rtp_sink_0")); // Link vid to rtp
		rtp.getStaticPad("send_rtp_src_0").link(vidUDPSink.getStaticPad("sink")); // Link rtp to udp
		rtp.getRequestPad("send_rtcp_src_0").link(vidRTCPSink.getStaticPad("sink")); // Link rtcp to udp
		vidRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_0")); // Link udp to rtcp

		vidRTCPSink.set("sync", false); vidRTCPSink.set("async", false);

		if (!isPassive) {

			Element audioSrc = ElementFactory.make("alsasrc", "soundsrc");

			Element audQ = ElementFactory.make("queue", "audQ");
			
	        Element audiofilter = ElementFactory.make("capsfilter", "aflt");
	        audiofilter.setCaps(Caps.fromString("audio/x-raw-int, channels=1, endianness=1234, width=16, depth=16, signed=true, rate=8000"));
			Element audrtppay = ElementFactory.make("rtppcmapay", "audpay");
			
			Element audEnc = ElementFactory.make("alawenc", "audenc");
			
			Element audUDPSink = ElementFactory.make("udpsink", "aududpsink");
			Element audRTCPSrc = ElementFactory.make("udpsrc", "audrtcpsrc");
			Element audRTCPSink = ElementFactory.make("udpsink", "audrtcpsink");
			audUDPSink.set("host", hostAddress);
			audUDPSink.set("port", ConnectionConfig.DESKTOP_AUDIO_UDP_SINK);
			audRTCPSrc.set("port", ConnectionConfig.DESKTOP_AUDIO_RTCP_SRC);
			audRTCPSink.set("host", hostAddress);
			audRTCPSink.set("port", ConnectionConfig.DESKTOP_AUDIO_RTCP_SINK);

			pipe.addMany(audioSrc, audiofilter, audQ, audEnc, audrtppay, audUDPSink, audRTCPSink, audRTCPSrc);
			audioSrc.link(audQ, audiofilter, audEnc, audrtppay);

			audrtppay.getStaticPad("src").link(rtp.getRequestPad("send_rtp_sink_1")); // Link vid to rtp
			rtp.getStaticPad("send_rtp_src_1").link(audUDPSink.getStaticPad("sink")); // Link rtp to udp
			rtp.getRequestPad("send_rtcp_src_1").link(audRTCPSink.getStaticPad("sink")); // Link rtcp to udp
			audRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_1")); // Link udp to rtcp
			
			audRTCPSink.set("sync", false); audRTCPSink.set("async", false);
		}
		
		System.out.println(rtp.getPads());
		
		pipe.setState(State.READY);
		pipe.setState(State.PLAYING);
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
