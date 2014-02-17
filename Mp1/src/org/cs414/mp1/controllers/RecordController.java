package org.cs414.mp1.controllers;

import java.io.File;

import javax.swing.SwingUtilities;

import org.cs414.mp1.frames.FrameVideo;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;

public class RecordController extends Controller {
	
	public RecordController(File file) {
		super(file);
	}
	
	// Vidtype and audtype should probably just be enums after we move them.
	public RecordController(File file, int width, int height, int frames, int vidType, int audType ) {
		super(file);
		
		this.width = width;
		this.height = height;
		framerate = frames;
		
	}
	
	public enum videoEnum { RAW, MJPEG, MPEG4 };
    public enum audioEnum { RAW, OGG };
    
    private static Pipeline videoPipe;
    private static Pipeline audioPipe;
    
    private String vidExt;
    private String audExt;
    private int width = 640;
    private int height = 480;
    private int framerate = 20;
    private final videoEnum vc = videoEnum.MJPEG;
    private final audioEnum ac = audioEnum.OGG;
    
    
	public void startRunning() {
		final FrameVideo frameVideo = getFrameVideo();
		final VideoComponent videoComponent = frameVideo.getVideoComponent();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String fileDir = getFile().getParent();
				String filename = getFile().getName();
				getFrameVideo().setVisible(true);

				
		   	
		        videoPipe = new Pipeline("VideoTest");
		        audioPipe = new Pipeline("AudioTest");
				
		        final Element videoSrc = ElementFactory.make(LINUX_WBCAM_SRC, "source");
		        final Element audioSrc = ElementFactory.make("pulsesrc", "soundsrc");
		        audioSrc.set("device", "alsa_input.pci-0000_00_1b.0.analog-stereo");
		        
		        final Element videoTee = ElementFactory.make("tee", "videoTee");
		        
		        final Element colorspace = ElementFactory.make("ffmpegcolorspace", "colorspace");
		        final Element videofilter = ElementFactory.make("capsfilter", "flt"); 
		        videofilter.setCaps(Caps.fromString("video/x-raw-yuv,  width="+ width + ", height=" + height + ", framerate=" + framerate + "/1"));
		        final Element videoMux = ElementFactory.make("avimux", "avimux");
		        final Element audiofilter = ElementFactory.make("audioconvert", "audioconvert");
		        final Element audioMux = ElementFactory.make("oggmux", "oggmux");

		        final Element videoQFile = ElementFactory.make("queue", "q1");
		        final Element videoQMonitor = ElementFactory.make("queue", "q2");
		        final Element audioQFile = ElementFactory.make("queue", "q3");		        
		        
		        final Element videoEnc;
		        final Element audioEnc;
		        
		        switch (vc) {
		        case MJPEG:
		        	videoEnc = ElementFactory.make("jpegenc", "Encoder");
		        	vidExt = ".avi";
		        	break;
		        case MPEG4:
		        	videoEnc = ElementFactory.make("ffenc_mpeg4", "Encoder");
		        	vidExt = ".mp4";
		        	break;
		        default:
		        	videoEnc = null;
		        	vidExt = ".avi";
		        	break;
		        }
		        
		        switch (ac) {
		        case OGG:
		        	audioEnc = ElementFactory.make("vorbisenc", "soundEncoder");
		        	audExt = ".ogg";
		        	break;
		        default:
		        	audioEnc = ElementFactory.make("mulawenc", "soundEncoder");
		        	audExt = ".pcm";
		        	break;
		        }
		        
		        final Element videoFile = ElementFactory.make("filesink", "Video sink");
		        final Element audioFile = ElementFactory.make("filesink", "Audio sink");
		        videoFile.set("location", fileDir + "/" + filename + vidExt);
		        audioFile.set("location", fileDir + "/" + filename + audExt);

		        Element videosink = videoComponent.getElement();
                videoPipe.addMany(videoSrc, videoTee, colorspace, videofilter, videoEnc, videoFile, videosink, videoQFile, videoQMonitor, videoMux);
                audioPipe.addMany(audioSrc, audiofilter, audioQFile, audioEnc, audioMux, audioFile);
                
                videoSrc.link(videoTee);
                audioSrc.link(audioQFile, audiofilter);
                videoTee.link(videoQFile); videoQFile.link(colorspace);
                videoTee.link(videoQMonitor); videoQMonitor.link(videosink);
                colorspace.link(videofilter);
                
                switch (vc) {
                case MPEG4:
                	videofilter.link(videoEnc, videoFile);
                case MJPEG:
                	videofilter.link(videoEnc, videoMux, videoFile);
                default:
                	videofilter.link(videoMux, videoFile);
                }
                
                switch (ac) {
                case OGG:
                	audiofilter.link(audioEnc, audioMux, audioFile);
                default:
                	audiofilter.link(audioMux, audioFile);
                }
                
                // Start the pipeline processing
                videoPipe.setState(State.PLAYING);
                audioPipe.setState(State.PLAYING);
			}
		});
	}
	
	public void stopRunning() {
		super.stopRunning();
	}
	
	public void togglePause() {
		//super.pauseRunning();
	}
}
