package org.cs414.mp1;

import org.gstreamer.Gst;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.elements.*;
import org.gstreamer.*;
import org.gstreamer.elements.good.RTPBin;

import java.io.File;
import java.io.IOException;

public class Server {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Gst.init("testserver", args);
		FileSrc fileSrc = (FileSrc) ElementFactory.make("filesrc", "filesrc");
		fileSrc.setLocation(new File("video.avi") );
		
		Element demux = ElementFactory.make("avidemux", "demux");
		
		final DecodeBin2 viddecbin = new DecodeBin2("bin");
		
		final Element vidQ = ElementFactory.make("queue", "vidQ");
		final Element audQ = ElementFactory.make("queue", "audQ");
		
		Element colorspace = ElementFactory.make("ffmpegcolorspace", "colorspace");
		
		Element vidvariablerate = ElementFactory.make("videorate", "rate");
		
		Element videncode = ElementFactory.make("jpegenc", "enc");
		
		Element vidrtppay = ElementFactory.make("rtpjpegpay", "vidpay");
		Element audrtppay = ElementFactory.make("rtppcmapay", "audpay");
		
		RTPBin rtp = new RTPBin("rtp");
		
		Element vidUDPSink = ElementFactory.make("udpsink", "vidudpsink");
		Element vidRTCPSrc = ElementFactory.make("udpsrc", "vidrtcpsrc");
		Element vidRTCPSink = ElementFactory.make("udpsink", "vidrtcpsink");
		vidUDPSink.set("host", "127.0.0.1");
		vidUDPSink.set("port", 5000);
		vidRTCPSrc.set("port", 5005);
		vidRTCPSink.set("host", "127.0.0.1");
		vidRTCPSink.set("port", 5001);
		
		Element audUDPSink = ElementFactory.make("udpsink", "aududpsink");
		Element audRTCPSrc = ElementFactory.make("udpsrc", "audrtcpsrc");
		Element audRTCPSink = ElementFactory.make("udpsink", "audrtcpsink");
		audUDPSink.set("host", "127.0.0.1");
		audUDPSink.set("port", 5002);
		audRTCPSrc.set("port", 5007);
		audRTCPSink.set("host", "127.0.0.1");
		audRTCPSink.set("port", 5003);
		
		Pipeline pipe = new Pipeline();
		
		pipe.addMany(fileSrc, demux, viddecbin, vidQ, audQ, colorspace, vidvariablerate, videncode, vidrtppay, audrtppay, rtp, vidUDPSink, vidRTCPSink, vidRTCPSrc, audUDPSink, audRTCPSink, audRTCPSrc);
		fileSrc.link(demux);
		demux.connect(new Element.PAD_ADDED() {
            public void padAdded(Element element, Pad pad) {
                if (pad.getName().startsWith("audio_")) {
                        System.out.println("audio pad found");
                        Pad spad = audQ.getStaticPad("sink");
                }
                if (pad.getName().startsWith("video_")) {
                        System.out.println("video pad found");
                        Pad spad = viddecbin.getStaticPad("sink");
                    pad.link(spad);
                    
                }
            }
        }); 
		viddecbin.connect(new DecodeBin2.NEW_DECODED_PAD() {
			public void newDecodedPad(DecodeBin2 element, Pad pad, boolean bool) {
				element.link(vidQ);
			}
		});
		
		vidQ.link(colorspace, vidvariablerate, videncode, vidrtppay);
		audQ.link(audrtppay);
		
		vidrtppay.getStaticPad("src").link(rtp.getRequestPad("send_rtp_sink_0")); // Link vid to rtp
		rtp.getStaticPad("send_rtp_src_0").link(vidUDPSink.getStaticPad("sink")); // Link rtp to udp
		rtp.getRequestPad("send_rtcp_src_0").link(vidRTCPSink.getStaticPad("sink")); // Link rtcp to udp
		vidRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_0")); // Link udp to rtcp
		
		audrtppay.getStaticPad("src").link(rtp.getRequestPad("send_rtp_sink_1")); // Link vid to rtp
		rtp.getStaticPad("send_rtp_src_1").link(audUDPSink.getStaticPad("sink")); // Link rtp to udp
		rtp.getRequestPad("send_rtcp_src_1").link(audRTCPSink.getStaticPad("sink")); // Link rtcp to udp
		audRTCPSrc.getStaticPad("src").link(rtp.getRequestPad("recv_rtcp_sink_1")); // Link udp to rtcp
		
		System.out.println(rtp.getPads());
		
		pipe.setState(State.READY);
		pipe.setState(State.PLAYING);
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
