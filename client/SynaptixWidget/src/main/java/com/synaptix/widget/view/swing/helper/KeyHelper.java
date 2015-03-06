package com.synaptix.widget.view.swing.helper;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Contains methods around keys & key stroke
 * @author Nicolas P
 *
 */
public class KeyHelper {


	/**
	 * Converts a key stroke into plain string
	 * Example: Ctrl+A
	 * @param accelerator
	 * @return String
	 */
	public static String getAcceleratorText(KeyStroke accelerator) {
		String acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
		if (acceleratorDelimiter == null) {
			acceleratorDelimiter = "+";
		}

		StringBuilder acceleratorText = new StringBuilder();
		if (accelerator != null) {
			int modifiers = accelerator.getModifiers();
			if (modifiers > 0) {
				acceleratorText.append(KeyEvent.getKeyModifiersText(modifiers));
				acceleratorText.append(acceleratorDelimiter);
			}

			int keyCode = accelerator.getKeyCode();
			if (keyCode != 0) {
				acceleratorText.append(KeyEvent.getKeyText(keyCode));
			} else {
				acceleratorText.append(accelerator.getKeyChar());
			}
		}
		return acceleratorText.toString();
	}
}
