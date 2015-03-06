package com.synaptix.widget.view.swing.helper;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public final class ClipboardHelper {

	private ClipboardHelper() {
	}

	/**
	 * Permet d'effacer le clipboard
	 */
	public static void clearClipboard() {
		try {
			clipboard().setContents(new Transferable() {
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[0];
				}

				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return false;
				}

				public Object getTransferData(DataFlavor flavor)
						throws UnsupportedFlavorException {
					throw new UnsupportedFlavorException(flavor);
				}
			}, null);
		} catch (IllegalStateException e) {
		}
	}

	private static final Clipboard clipboard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
}
