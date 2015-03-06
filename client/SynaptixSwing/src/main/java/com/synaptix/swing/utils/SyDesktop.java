package com.synaptix.swing.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public final class SyDesktop {

	private static void openWithoutDesktop(File file) throws IOException {
		Runtime.getRuntime().exec(
				new String[] { "rundll32", "url.dll,FileProtocolHandler", //$NON-NLS-1$ //$NON-NLS-2$
						file.getAbsolutePath() });

	}

	public static void open(File file) throws Exception {
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(file);
		} else {
			openWithoutDesktop(file);
		}
	}

	public static void print(File file) throws Exception {
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().print(file);
		} else {
			throw new Exception("Desktop not supported"); //$NON-NLS-1$
		}
	}

	public static void browse(URI uri) throws Exception {
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(uri);
		} else {
			throw new Exception("Desktop not supported"); //$NON-NLS-1$
		}
	}
}
