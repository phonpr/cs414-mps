package edu.cs414.mp3.client;

import javax.swing.JButton;
import javax.swing.JCheckBox;

public class ButtonGroup {
	
	private JButton btnPlay;
	private JButton btnPause;
	private JButton btnResume;
	private JButton btnStop;
	private JCheckBox chkMute;
	
	public JButton getBtnPlay() {
		return btnPlay;
	}
	public void setBtnPlay(JButton btnPlay) {
		this.btnPlay = btnPlay;
	}
	
	public JButton getBtnPause() {
		return btnPause;
	}
	public void setBtnPause(JButton btnPause) {
		this.btnPause = btnPause;
	}
	
	public JButton getBtnResume() {
		return btnResume;
	}
	public void setBtnResume(JButton btnResume) {
		this.btnResume = btnResume;
	}
	
	public JButton getBtnStop() {
		return btnStop;
	}
	public void setBtnStop(JButton btnStop) {
		this.btnStop = btnStop;
	}
	
	public JCheckBox getChkMute() {
		return chkMute;
	}
	public void setChkMute(JCheckBox chkMute) {
		this.chkMute = chkMute;
	}
	
	public void onPlay() {
		btnPlay.setEnabled(false);
		btnPause.setEnabled(true);
		btnResume.setEnabled(false);
		btnStop.setEnabled(true);
		chkMute.setEnabled(true);
	}
	
	public void onPause() {
		btnPause.setEnabled(false);
		btnResume.setEnabled(true);
	}
	
	public void onResume() {
		btnPause.setEnabled(true);
		btnResume.setEnabled(false);
	}
	
	public void onReset() {
		btnPlay.setEnabled(true);
		btnPause.setEnabled(false);
		btnResume.setEnabled(false);
		btnStop.setEnabled(false);
		chkMute.setEnabled(false);
	}
}
