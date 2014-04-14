package org.cs414.mp2.client.views;

import org.cs414.mp2.client.controllers.network.ResourceNegotiation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by bill on 4/12/14.
 */
public class DialogBandwidthOptions extends JDialog {

	private int maxBandwidth = 0;
	private boolean changed = false;

	private JPanel limitPanel;

	private JTextField bandwidthInput;

	private JButton btnSet = null;
	private JButton btnCancel = null;

	public DialogBandwidthOptions(JFrame frame) {
		super(frame, true);
	}

	public void initializeComponents() {
		limitPanel = new JPanel();

		setTitle("Set Bandwidth");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(limitPanel);

		bandwidthInput = new JTextField();
		bandwidthInput.setColumns(4);
		bandwidthInput.setText(Integer.toString(ResourceNegotiation.getAvailable()));
		btnSet = new JButton("SET");
		btnCancel = new JButton("CANCEL");

		limitPanel.add(new JLabel("Width : "));
		limitPanel.add(bandwidthInput);
		limitPanel.add(btnSet);
		limitPanel.add(btnCancel);
	}

	public int getMaxBandwidth() {
		return maxBandwidth;
	}

	public boolean isChanged() {
		return changed;
	}

	public void acknowledgeChanged() {
		changed = false;
	}

	public void initializeListener(ActionListener listener) {
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!"".equals(bandwidthInput.getText())) {
					maxBandwidth = Integer.valueOf(bandwidthInput.getText());
					changed = true;
					dispose();
				}
			}
		});

		btnCancel.addActionListener(new ActionListener() {
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
