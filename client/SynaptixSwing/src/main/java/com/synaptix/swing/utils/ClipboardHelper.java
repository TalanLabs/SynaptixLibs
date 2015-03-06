package com.synaptix.swing.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;

import org.apache.commons.lang.StringUtils;

public class ClipboardHelper {

	public static void installClipboardPasteListener(JComponent c) {
		String overridePaste = System.getProperty("synaptix.overridePaste", "true");
		if ("true".equals(overridePaste)) {
			Action action = c.getActionMap().get("paste-from-clipboard");
			c.getActionMap().put("paste-from-clipboard", new ProxyAction(action));
		}
	}

	private static class ProxyAction extends AbstractAction {

		private static final long serialVersionUID = 4776926434602115923L;

		private Action action;

		public ProxyAction(Action action) {
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable transf = clipboard.getContents(null); // argument is useless according to javaDoc

				if (transf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String data = (String) transf.getTransferData(DataFlavor.stringFlavor);
					StringSelection stringSelection = new StringSelection(StringUtils.trim(data));
					clipboard.setContents(stringSelection, stringSelection);
				}
			} catch (Exception ex) {
			}

			action.actionPerformed(e);
		}
	}
}
