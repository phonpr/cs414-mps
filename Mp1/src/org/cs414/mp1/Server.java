package org.cs414.mp1;

import org.gstreamer.Gst;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pad;
import org.gstreamer.PadDirection;
import org.gstreamer.elements.*;
import org.gstreamer.*;
import org.gstreamer.elements.good.RTPBin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Server {

	public static void main(String[] args) {
		Gst.init("testserver", args);
		FileSrc fileSrc = (FileSrc) ElementFactory.make("filesrc", "filesrc");
		fileSrc.setLocation(new File("testvideo3.avi") );
		
		Element demux = ElementFactory.make("avidemux", "demux");
		
		final Element vidQ = ElementFactory.make("queue", "vidQ");
		final Element audQ = ElementFactory.make("queue", "audQ");
		
		Element vidrtppay = ElementFactory.make("rtpjpegpay", "vidpay");
		Element audrtppay = ElementFactory.make("rtppcmapay", "audpay");
		
		RTPBin rtp = new RTPBin("rtp");
		
		Element vidUDPSink = ElementFactory.make("udpsink", "vidudpsink");
		Element vidRTCPSrc = ElementFactory.make("udpsrc", "vidrtcpsrc");
		Element vidRTCPSink = ElementFactory.make("udpsink", "vidrtcpsink");
		vidUDPSink.set("host", "localhost");
		vidUDPSink.set("port", 5000);
		vidRTCPSrc.set("port", 5005);
		vidRTCPSink.set("host", "localhost");
		vidRTCPSink.set("port", 5001);
		
		Element audUDPSink = ElementFactory.make("udpsink", "aududpsink");
		Element audRTCPSrc = ElementFactory.make("udpsrc", "audrtcpsrc");
		Element audRTCPSink = ElementFactory.make("udpsink", "audrtcpsink");
		audUDPSink.set("host", "localhost");
		audUDPSink.set("port", 5002);
		audRTCPSrc.set("port", 5007);
		audRTCPSink.set("host", "localhost");
		audRTCPSink.set("port", 5003);
		
		Pipeline pipe = new Pipeline();
		
		pipe.addMany(fileSrc, demux, vidQ, audQ, vidrtppay, audrtppay, rtp, vidUDPSink, vidRTCPSink, vidRTCPSrc, audUDPSink, audRTCPSink, audRTCPSrc);
		fileSrc.link(demux);
		demux.connect(new Element.PAD_ADDED() {
            public void padAdded(Element element, Pad pad) {
                if (pad.getName().startsWith("audio_")) {
                	System.out.println("audio pad found");
                	pad.link(audQ.getStaticPad("sink"));
                }
                if (pad.getName().startsWith("video_")) {
                	System.out.println("video pad found");
                	pad.link(vidQ.getStaticPad("sink"));
                }
            }
        }); 
		
		vidQ.link(vidrtppay);
		audQ.link(audrtppay);
		
		vidrtppay.getStaticPad("src").link(rtp.getRequestPad("send_rtp_sink_0")); // Link vid to rtp
		rtp.getStaticPad("send_rtp_src_0").link(vidUDPSink.getStaticPad("sink")); // Link rtp to udp
		rtp.getRequestPad("send_rtcp_src_0").link(vidRTCPSink.getStaticPad("sink")); // Link rtcp to udp
		vidRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_0")); // Link udp to rtcp
		
		audrtppay.getStaticPad("src").link(rtp.getRequestPad("send_rtp_sink_1")); // Link vid to rtp
		rtp.getStaticPad("send_rtp_src_1").link(audUDPSink.getStaticPad("sink")); // Link rtp to udp
		rtp.getRequestPad("send_rtcp_src_1").link(audRTCPSink.getStaticPad("sink")); // Link rtcp to udp
		audRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_1")); // Link udp to rtcp
		
		audRTCPSink.set("sync", false); audRTCPSink.set("async", false);
		vidRTCPSink.set("sync", false); vidRTCPSink.set("async", false);
		
		System.out.println(rtp.getPads());
		
		pipe.setState(State.READY);
		pipe.setState(State.PLAYING);
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		getCaps( audUDPSink);
	}
	
	public static String getCaps(Element elem) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Pad> pads = elem.getPads();

		for (Pad pad : pads) {

			if (pad.getDirection() == PadDirection.SINK) {
				Pad peer = pad.getPeer();
				Caps cap = peer.getNegotiatedCaps();
				String capString = cap.toString();
				System.out.println("sent " + capString);
				return capString;

			}
		}
		return null;
	}
	
	

}
