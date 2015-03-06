package com.synaptix.swing.utils;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class Manager {

	private static boolean autoSaveExportExcel;

	public static void setAutoSaveExportExcel(boolean autoSaveExportExcel) {
		Manager.autoSaveExportExcel = autoSaveExportExcel;
	}

	public static boolean isAutoSaveExportExcel() {
		return autoSaveExportExcel;
	}

	public static void installLookAndFeelWindows() {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static void installLookAndFeelNimbus() {
		boolean ok = false;
		UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
		int i = 0;
		while (i < looks.length && !ok) {
			UIManager.LookAndFeelInfo laf = looks[i];
			if ("Nimbus".equals(laf.getName())) {
				try {
					UIManager.setLookAndFeel(laf.getClassName());
					ok = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			i++;
		}
	}
}
