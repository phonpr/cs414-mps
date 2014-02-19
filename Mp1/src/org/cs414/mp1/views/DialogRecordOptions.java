package org.cs414.mp1.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.cs414.mp1.controllers.Controller;
import org.cs414.mp1.controllers.Controller.AudioType;
import org.cs414.mp1.controllers.Controller.VideoType;

public class DialogRecordOptions extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6349994745814921310L;
	
	// constants
	private static final String FRAME_TITLE = "Recording Options";
	
	// UI components
	private JPanel panelSize = null;
	private JPanel panelRate = null;
	private JPanel panelVideoType = null;
	private JPanel panelAudioType = null;
	private JPanel panelButtons = null;
	
	private JTextField textWidth = null;
	private JTextField textHeight = null;
	private JTextField textFrameRate = null;
	private JTextField textSamplingRate = null;
	
	private JRadioButton radioVideoRaw = null;
	private JRadioButton radioVideoMjpeg = null;
	private JRadioButton radioVideoMpeg4 = null;
	private JRadioButton radioAudioRaw = null;
	private JRadioButton radioAudioOgg = null;
	
	private JButton btnRecord = null;
	private JButton btnCancel = null;
	
	// logistics
	private boolean startRecording = false;
	private VideoType eVideoType = VideoType.RAW;
	private AudioType eAudioType = AudioType.RAW;
	
	public DialogRecordOptions(JFrame frame) {
		super(frame, true);
	}
	
	public int getVideoWidth() {
		return Integer.parseInt(textWidth.getText());
	}
	
	public int getVideoHeight() {
		return Integer.parseInt(textHeight.getText());
	}
	
	public int getFrameRate() {
		return Integer.parseInt(textFrameRate.getText());
	}
	
	public int getSamplingRate() {
		return Integer.parseInt(textSamplingRate.getText());
	}

	public boolean isStartRecording() {
		return startRecording;
	}
	
	public VideoType getVideoType() {
		return eVideoType;
	}
	
	public AudioType getAudioType() {
		return eAudioType;
	}
	
	public void initializeComponents() {
		panelSize = new JPanel();
		panelRate = new JPanel();
		panelVideoType = new JPanel();
		panelAudioType = new JPanel();
		panelButtons = new JPanel();
		
		// main frame
		setTitle(FRAME_TITLE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(panelSize);
		getContentPane().add(panelRate);
		getContentPane().add(panelVideoType);
		getContentPane().add(panelAudioType);
		getContentPane().add(panelButtons);
		
		// size panel
		textWidth = new JTextField();
		textWidth.setColumns(4);
		textWidth.setText(Integer.toString(Controller.VIDEO_DEFAULT_WIDTH));
		textHeight = new JTextField();
		textHeight.setColumns(4);
		textHeight.setText(Integer.toString(Controller.VIDEO_DEFAULT_HEIGHT));
		panelSize.add(new JLabel("Width : "));
		panelSize.add(textWidth);
		panelSize.add(new JLabel("Height : "));
		panelSize.add(textHeight);
		
		// rate panel
		textFrameRate = new JTextField();
		textFrameRate.setColumns(4);
		textFrameRate.setText(Integer.toString(Controller.VIDEO_DEFAULT_RATE));
		textSamplingRate = new JTextField();
		textSamplingRate.setColumns(6);
		textSamplingRate.setText(Integer.toString(Controller.AUDIO_DEFAULT_RATE));
		panelRate.add(new JLabel("Frame Rate : "));
		panelRate.add(textFrameRate);
		panelRate.add(new JLabel("Sampling Rate : "));
		panelRate.add(textSamplingRate);
		
		// video type panel
		panelVideoType.add(new JLabel("Video Type : "));
		radioVideoRaw = new JRadioButton("Raw");
		radioVideoRaw.setSelected(true);
		radioVideoMjpeg = new JRadioButton("MJPEG");
		radioVideoMpeg4 = new JRadioButton("MPEG4");
		panelVideoType.add(radioVideoRaw);
		panelVideoType.add(radioVideoMjpeg);
		panelVideoType.add(radioVideoMpeg4);
		
		// audio type panel
		radioAudioRaw = new JRadioButton("Raw");
		radioAudioRaw.setSelected(true);
		radioAudioOgg = new JRadioButton("OGG");
		panelAudioType.add(new JLabel("Audio Type : "));
		panelAudioType.add(radioAudioRaw);
		panelAudioType.add(radioAudioOgg);
		
		// button panel
		FlowLayout layoutButtons = (FlowLayout) panelButtons.getLayout();
		layoutButtons.setAlignment(FlowLayout.RIGHT);
		btnRecord = new JButton("Record");
		btnCancel = new JButton("Cancel");
		panelButtons.add(btnRecord);
		panelButtons.add(btnCancel);
	}
	
	public void initializeListener(ActionListener listener) {
		radioVideoRaw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eVideoType = VideoType.RAW;
				radioVideoMjpeg.setSelected(false);
				radioVideoMpeg4.setSelected(false);
			}
		});
		radioVideoMjpeg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eVideoType = VideoType.MJPEG;
				radioVideoRaw.setSelected(false);
				radioVideoMpeg4.setSelected(false);
			}
		});
		radioVideoMpeg4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eVideoType = VideoType.MPEG4;
				radioVideoRaw.setSelected(false);
				radioVideoMjpeg.setSelected(false);
			}
		});
		
		radioAudioRaw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eAudioType = AudioType.RAW;
				radioAudioOgg.setSelected(false);
			}
		});
		radioAudioOgg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eAudioType = AudioType.OGG;
				radioAudioRaw.setSelected(false);
			}
		});
		
		btnRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startRecording = true;
				setVisible(false);
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	public void showOpenDialog() {
		initializeComponents();
		initializeListener(null);
		pack();
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

}
