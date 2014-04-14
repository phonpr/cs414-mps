package org.cs414.mp2.server;

/**
* Created by bill on 4/13/14.
*/
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pad;
import org.gstreamer.PadDirection;
import org.gstreamer.elements.*;
import org.gstreamer.*;
import org.gstreamer.elements.good.RTPBin;

import java.io.File;
import java.util.List;

public class Server {

	private Pipeline pipe;

	private enum PlayType {
		NORMAL	,
		FF		,
		RW		,
	}

	private PlayType ePlayType = PlayType.NORMAL;

	public void createPipeline() {
		FileSrc fileSrc = (FileSrc) ElementFactory.make("filesrc", "filesrc");
		fileSrc.setLocation(new File("testvideo.avi") );

		Element demux = ElementFactory.make("avidemux", "demux");

		final Element vidQ = ElementFactory.make("queue", "vidQ");
		final Element audQ = ElementFactory.make("queue", "audQ");

		Element vidDec = ElementFactory.make("jpegdec", "stupiddec");
		Element vidRate = ElementFactory.make("videorate", "ratechange");
		Element vidEnc = ElementFactory.make("jpegenc", "stupidenc");
		
		vidRate.set("max-rate", 10);
				
		Element vidrtppay = ElementFactory.make("rtpjpegpay", "vidpay");
		Element audrtppay = ElementFactory.make("rtppcmapay", "audpay");

		RTPBin rtp = new RTPBin("rtp");

		Element vidUDPSink = ElementFactory.make("udpsink", "vidudpsink");
		Element vidRTCPSrc = ElementFactory.make("udpsrc", "vidrtcpsrc");
		Element vidRTCPSink = ElementFactory.make("udpsink", "vidrtcpsink");
		vidUDPSink.set("host", "172.16.206.138");
		vidUDPSink.set("port", 5000);
		vidRTCPSrc.set("port", 5005);
		vidRTCPSink.set("host", "172.16.206.138");
		vidRTCPSink.set("port", 5001);

		Element audUDPSink = ElementFactory.make("udpsink", "aududpsink");
		Element audRTCPSrc = ElementFactory.make("udpsrc", "audrtcpsrc");
		Element audRTCPSink = ElementFactory.make("udpsink", "audrtcpsink");
		audUDPSink.set("host", "172.16.206.138");
		audUDPSink.set("port", 5002);
		audRTCPSrc.set("port", 5007);
		audRTCPSink.set("host", "172.16.206.138");
		audRTCPSink.set("port", 5003);

		pipe = new Pipeline();

		pipe.addMany(vidDec, vidRate, vidEnc, fileSrc, demux, vidQ, audQ, vidrtppay, audrtppay, rtp, vidUDPSink, vidRTCPSink, vidRTCPSrc, audUDPSink, audRTCPSink, audRTCPSrc);
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

		vidQ.link(vidDec, vidRate, vidEnc, vidrtppay);
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
	}

	public void play() {
		pipe.setState(State.READY);
		pipe.setState(State.PLAYING);
	}

	public void togglePause() {
		if (pipe.getState() == State.PAUSED) {
			// when resuming pased, set to normal playing
			ePlayType = PlayType.NORMAL;
			setNewRate(1.0);
			pipe.setState(State.PLAYING);
		}
		else {
			pipe.setState(State.PAUSED);
		}
	}

	public void toggleFF()
	{
		if (ePlayType == PlayType.FF) {
			ePlayType = PlayType.NORMAL;
			setNewRate(1.0);
		}
		else {
			// case NORMAL or RW
			ePlayType = PlayType.FF;
			setNewRate(2.0);
		}
	}

	public void toggleRW() {
		if (ePlayType == PlayType.RW) {
			ePlayType = PlayType.NORMAL;
			setNewRate(1.0);
		}
		else {
			// case NORMAL or FF
			ePlayType = PlayType.RW;
			setNewRate(-1.0);
		}
	}

	/**
	 * Sets the rate of the playback to the specified rate
	 */
	private void setNewRate(double newRate) {

		System.out.println("SETTING NEW RATE: " + newRate);

		pipe.setState(State.PAUSED);
		long currentTime = pipe.queryPosition(Format.TIME);
		System.out.println(currentTime);
		pipe.setState(State.PLAYING);

		if(newRate >= 0.0) {
			pipe.seek(newRate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, currentTime, SeekType.NONE, -1);
		}
		else {
			pipe.seek(newRate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, 0, SeekType.SET, currentTime);
		}

	}
}

