package org.cs414.mp2.client.controllers;

import java.io.File;

import javax.swing.SwingUtilities;

import org.cs414.mp2.client.views.FrameVideo;
import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.State;
import org.gstreamer.elements.AppSink;
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
	private int samplerate;
	
	private VideoType eVideoType;
	private AudioType eAudioType;
	
	private long compressTotal;
	private long lastTime;
	private int samplesSeen;
	
	private int rawSize;
	
	/**
	 * @param file
	 * @param width Width in pixels of video
	 * @param height Height in pixels of video
	 * @param frames Framerate in frames per second
	 * @param samples Samplerate in samples per second
	 * @param vidType This enum needs to get moved
	 * @param audType So does this one
	 */
	public RecordController(File file, int width, int height, int frames, int samples, VideoType vidType, AudioType audType ) {
		super(OperationType.RECORDING);
		
		this.width = width;
		this.height = height;
		framerate = frames;
		samplerate = samples;
		eVideoType = vidType;
		eAudioType = audType;
		compressTotal = 0;
		samplesSeen = 0;
		rawSize = 2 * width * height;
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
				
		        final Element videoSrc = ElementFactory.make(LINUX_WBCAM_SRC, "source");
		        final Element audioSrc = ElementFactory.make("pulsesrc", "soundsrc");
		        audioSrc.set("device", "alsa_input.pci-0000_00_1b.0.analog-stereo");
		        
		        final Element videoTee = ElementFactory.make("tee", "videoTee");
		        final Element dataTee = ElementFactory.make("tee", "dataTee");
		        
		        final Element colorspace = ElementFactory.make("ffmpegcolorspace", "colorspace");
		        final Element videofilter = ElementFactory.make("capsfilter", "flt"); 
		        videofilter.setCaps(Caps.fromString("video/x-raw-yuv,  width="+ width + ", height=" + height + ", framerate=" + framerate + "/1"));
		        final Element videoMux = ElementFactory.make("avimux", "avimux");
		        final Element audiofilter = ElementFactory.make("capsfilter", "aflt");
		        audiofilter.setCaps(Caps.fromString("audio/x-raw-int, channels=1, endianness=1234, width=16, depth=16, signed=true, rate=" + samplerate));
		        final Element audioConverter = ElementFactory.make("audioconvert", "audioconvert");
		        final Element audioMux = ElementFactory.make("oggmux", "oggmux");

		        final Element videoQFile = ElementFactory.make("queue", "q1");
		        final Element videoQMonitor = ElementFactory.make("queue", "q2");
		        final Element videoQData = ElementFactory.make("queue", "q4");
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
		        	audioEnc = null;
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
		        
		        final AppSink videoApp = (AppSink) ElementFactory.make("appsink", "datasink");
		        videoApp.set ("emit-signals", true);
		        videoApp.setSync(false);
		        videoApp.connect(new AppSink.NEW_BUFFER (){
					@Override
					public void newBuffer(AppSink arg0) {
						Buffer temp = arg0.getLastBuffer();
						long time = temp.getTimestamp().toMillis();	
						int size = temp.getSize();
						if (size <= 8)
							return;
						
						if (samplesSeen == 0) {
							lastTime = time;
							samplesSeen++;
						} else {	
							if (time != 0) {
								compressTotal += (time - lastTime);
								lastTime = time;
								samplesSeen++;
								getFrameVideo().updateCompressionTime((int) (compressTotal / samplesSeen));
							}
						}
						getFrameVideo().updateCompressedSize(size);
						getFrameVideo().updateCompressionRatio(rawSize / (size + 0.0) );
					}
		        });

		        Element videosink = videoComponent.getElement();
                getVideoPipe().addMany(videoSrc, videoTee, colorspace, videofilter, videoFile, videosink, videoQFile, videoQMonitor, videoMux, dataTee, videoQData, videoApp);
                getAudioPipe().addMany(audioSrc, audiofilter, audioConverter, audioQFile, audioMux, audioFile);
                
                videoSrc.link(videoTee);
                audioSrc.link(audioQFile, audiofilter, audioConverter);
                videoTee.link(videoQFile); videoQFile.link(colorspace);
                videoTee.link(videoQMonitor); videoQMonitor.link(videosink);
                colorspace.link(videofilter);
                
                switch (eVideoType) {
                case MPEG4:
                	getVideoPipe().add(videoEnc);
                	videofilter.link(videoEnc, dataTee, videoFile);
                	break;
                case MJPEG:
                	getVideoPipe().add(videoEnc);
                	videofilter.link(videoEnc,  videoMux, dataTee, videoFile);
                	break;
                default:
                	videofilter.link( videoMux, dataTee, videoFile);
                	break;
                }
                
                dataTee.link(videoQData, videoApp);
                
                switch (eAudioType) {
                case OGG:
                	getAudioPipe().add(audioEnc);
                	audioConverter.link(audioEnc, audioMux, audioFile);
                	break;
                default:
                	audioConverter.link( audioFile);
                	break;
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
