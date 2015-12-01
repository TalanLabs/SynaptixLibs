package com.synaptix.swing.utils;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.synaptix.swing.SwingMessages;

public final class ExcelHelper {

	private ExcelHelper() {
	}

	/**
	 * Permet de choisir un fichier de type Excel
	 * 
	 * @return
	 */
	public static final File chooseExcelFile() {
		File res = null;

		JFileChooser chooser = new JFileChooser();
		SaveXLSFileFilter filter = new SaveXLSFileFilter();
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(GUIWindow.getActiveWindow());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (getExtension(file) == null) {
				file = new File(file.getAbsolutePath() + ".xls"); //$NON-NLS-1$
			}
			if (!file.isDirectory()) {
				if (!file.exists()
						|| JOptionPane.showConfirmDialog(GUIWindow
								.getActiveWindow(), MessageFormat
								.format(SwingMessages
										.getString("TableFileWriter.1"), file //$NON-NLS-1$
										.getName()), SwingMessages
								.getString("TableFileWriter.2"), //$NON-NLS-1$
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					String extension = getExtension(file);
					if (extension.equals("xls")) { //$NON-NLS-1$
						res = file;
					}
				}
			}
		}

		return res;
	}

	private static final String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	private static class SaveXLSFileFilter extends FileFilter {

		public boolean accept(File pathname) {
			boolean res = false;
			if (pathname.isDirectory())
				res = true;
			else if (pathname.isFile()) {
				String extension = getExtension(pathname);
				if (extension != null) {
					if (extension.equals("xls")) { //$NON-NLS-1$ //$NON-NLS-2$
						res = true;
					}
				}
			}
			return res;
		}

		public String getDescription() {
			return "XLS table"; //$NON-NLS-1$
		}
	}
}
