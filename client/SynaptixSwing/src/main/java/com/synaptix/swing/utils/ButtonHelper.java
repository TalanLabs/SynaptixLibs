package com.synaptix.swing.utils;

import javax.swing.AbstractButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ButtonHelper {

	public static void installButtonChanger(final AbstractButton button) {
		button.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeButtonPainted(button);
			}
		});
		changeButtonPainted(button);
	}

	public static final void changeButtonPainted(AbstractButton button) {
		button.setFocusPainted(false);
		if (button.getModel().isRollover() && button.getModel().isEnabled()) {
			button.setBorderPainted(true);
			button.setContentAreaFilled(true);
		} else if (button.getModel().isSelected()) {
			button.setBorderPainted(true);
			button.setContentAreaFilled(true);
		} else {
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
		}
	}
}
