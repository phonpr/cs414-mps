package org.cs414.mp2.client.controllers;

import org.cs414.mp2.client.views.FrameVideo;
import org.gstreamer.*;
import org.gstreamer.elements.good.RTPBin;
import org.gstreamer.swing.VideoComponent;

import javax.swing.SwingUtilities;

public class PlayController extends Controller
{

	private enum PlayType {
		NORMAL	,
		FF	,
		RW	,
	}

	private PlayType ePlayType = PlayType.NORMAL;

	private String hostname = "";

	public PlayController() {
		super(OperationType.PLAYING);
	}

	public void setHostname(String newHostName) {
		hostname = newHostName;
	}

	public String getHostname() {
		return hostname;
	}

	public void startRunning() {
		final FrameVideo frameVideo = getFrameVideo();
		final VideoComponent videoComponent = frameVideo.getVideoComponent();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {


				getFrameVideo().setVisible(true);

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
				vidUDPSrc.set("port", 5000);
				vidRTCPSrc.set("port", 5001);
				vidRTCPSink.set("host", hostname);
				vidRTCPSink.set("port", 5005);

				audUDPSrc.setCaps(Caps.fromString("application/x-rtp, media=(string)audio, clock-rate=(int)8000, encoding-name=(string)PCMA, ssrc=(uint)3824386182, payload=(int)8, clock-base=(uint)921092443, seqnum-base=(uint)8008"));
				audUDPSrc.set("port", 5002);
				audRTCPSrc.set("port", 5003);
				audRTCPSink.set("host", hostname);
				audRTCPSink.set("port", 5007);

				final Element videoElement = videoComponent.getElement();

				RTPBin rtp = new RTPBin("rtp");

				getVideoPipe().addMany(vidUDPSrc, vidRTCPSink, vidRTCPSrc, audUDPSrc, audRTCPSink, audRTCPSrc, rtp, audDec, vidrtpdepay, viddecode, colorspace, vidQ, videoElement, audrtpdepay, audConvert, audResample, audSink, audQ);
				vidrtpdepay.link(viddecode, vidQ, colorspace, videoElement);
				audrtpdepay.link(audDec, audConvert, audResample, audSink);

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

				getVideoPipe().setState(State.READY);
				getVideoPipe().setState(State.PLAYING);

			}
		});
	}

	public void togglePause() {
		if (getVideoPipe().getState() == State.PAUSED) {
// when resuming pased, set to normal playing
			ePlayType = PlayType.NORMAL;
			setNewRate(1.0);
			getVideoPipe().setState(State.PLAYING);
		}
		else {
			getVideoPipe().setState(State.PAUSED);
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

		getVideoPipe().setState(State.PAUSED);
		long currentTime = getVideoPipe().queryPosition(Format.TIME);
		System.out.println(currentTime);
		getVideoPipe().setState(State.PLAYING);

		if(newRate >= 0.0) {
			getVideoPipe().seek(newRate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, currentTime, SeekType.NONE, -1);
		}
		else {
			getVideoPipe().seek(newRate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, 0, SeekType.SET, currentTime);
		}

	}
}