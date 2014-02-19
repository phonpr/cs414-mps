package org.cs414.mp1.controllers;

import java.io.File;

import javax.swing.SwingUtilities;

import org.cs414.mp1.views.FrameVideo;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;

public class RecordController extends Controller {

	/* 2014. 02. 17. S.H. CHA - using superclass's pipe
	 * 
	private static Pipeline videoPipe;
	private static Pipeline audioPipe;
	*/
	
	private String vidExt;
	private String audExt;
	
	private int width;
	private int height;
	
	private int framerate;
	private VideoType eVideoType;
	private AudioType eAudioType;
	
	/**
	 * @param file
	 * @param width Width in pixels of video
	 * @param height Height in pixels of video
	 * @param frames Framerate in frames per second
	 * @param vidType This enum needs to get moved
	 * @param audType So does this one
	 */
	public RecordController(File file, int width, int height, int frames, VideoType vidType, AudioType audType ) {
		super(file);
		
		this.width = width;
		this.height = height;
		framerate = frames;
		eVideoType = vidType;
		eAudioType = audType;
	}
	
	public void startRunning() {
		final FrameVideo frameVideo = getFrameVideo();
		final VideoComponent videoComponent = frameVideo.getVideoComponent();
		
		frameVideo.updateSize(width, height);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String fileDir = getFile().getParent();
				String filename = getFile().getName();
				getFrameVideo().setVisible(true);
				
				/* 2014. 02. 17. S.H. CHA - using superclass's pipe
		        videoPipe = new Pipeline("VideoTest");
		        audioPipe = new Pipeline("AudioTest");
		        */
				
		        final Element videoSrc = ElementFactory.make(WINDOWS_WEBCAM_SRC, "source");
		        final Element audioSrc = ElementFactory.make(LINUX_AUDIO_SRC, "soundsrc");
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
		        
		        Element videoEnc = null;
		        Element audioEnc = null;
		        
		        switch (eVideoType) {
		        case RAW:
		        	videoEnc = null;
		        	vidExt = ".avi";
		        	break;
		        case MJPEG:
		        	videoEnc = ElementFactory.make("jpegenc", "Encoder");
		        	vidExt = ".avi";
		        	break;
		        case MPEG4:
		        	videoEnc = ElementFactory.make("ffenc_mpeg4", "Encoder");
		        	vidExt = ".mp4";
		        	break;
		        default: break; // unsupported type
		        }
		        
		        switch (eAudioType) {
		        case RAW:
		        	audioEnc = ElementFactory.make("mulawenc", "soundEncoder");
		        	audExt = ".pcm";
		        	break;
		        case OGG:
		        	audioEnc = ElementFactory.make("vorbisenc", "soundEncoder");
		        	audExt = ".ogg";
		        	break;
		        default: break; // unsupported type
		        }
		        
		        final Element videoFile = ElementFactory.make("filesink", "Video sink");
		        final Element audioFile = ElementFactory.make("filesink", "Audio sink");
		        videoFile.set("location", fileDir + "/" + filename + vidExt);
		        audioFile.set("location", fileDir + "/" + filename + audExt);

		        Element videosink = videoComponent.getElement();
                getVideoPipe().addMany(videoSrc, videoTee, colorspace, videofilter, videoEnc, videoFile, videosink, videoQFile, videoQMonitor, videoMux);
                getAudioPipe().addMany(audioSrc, audiofilter, audioQFile, audioEnc, audioMux, audioFile);
                
                videoSrc.link(videoTee);
                audioSrc.link(audioQFile, audiofilter);
                videoTee.link(videoQFile); videoQFile.link(colorspace);
                videoTee.link(videoQMonitor); videoQMonitor.link(videosink);
                colorspace.link(videofilter);
                
                switch (eVideoType) {
                case MPEG4:
                	videofilter.link(videoEnc, videoFile);
                case MJPEG:
                	videofilter.link(videoEnc, videoMux, videoFile);
                default:
                	videofilter.link(videoMux, videoFile);
                }
                
                switch (eAudioType) {
                case OGG:
                	audiofilter.link(audioEnc, audioMux, audioFile);
                default:
                	audiofilter.link(audioMux, audioFile);
                }
                
                // Start the pipeline processing
                setVideoState(State.PLAYING);
                setAudioState(State.PLAYING);
			}
		});
	}
	
	public void stopRunning() {
		super.stopRunning();
	}
}
