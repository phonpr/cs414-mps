package org.cs414.mp1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Listener implements ActionListener {
	
	public static final String ACTION_RECORD = "record";
	public static final String ACTION_PLAY = "play";
	public static final String ACTION_FF = "ff";

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action == ACTION_RECORD) {
			System.out.println("Record button pressed");
		}
		else if (action == ACTION_PLAY) {
			System.out.println("Play button pressed");
		}
		else if (action == ACTION_FF) {
			System.out.println("FF button pressed");
		}
	}

}
