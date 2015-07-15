package com.synaptix.widget.view.swing.helper;

import java.awt.Color;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.synaptix.swing.utils.FontAwesomeHelper;

public class IconHelper {

	private static boolean useFontAwesome = false;

	private static int size = 16;

	public static void setUseFontAwesome(boolean useFontAwesome) {
		IconHelper.useFontAwesome = useFontAwesome;
	}

	public static boolean isUseFontAwesome() {
		return useFontAwesome;
	}

	public static int getSize() {
		return size;
	}

	public static void setSize(int size) {
		IconHelper.size = size;
	}

	public enum Icons {

		CHECK("fa-check", "com/synaptix/widget/actions/view/swing/iconCheck.png"), ADD("fa-plus", "com/synaptix/widget/actions/view/swing/iconAdd.png"), ARROW_DOWN("fa-chevron-down",
				"com/synaptix/widget/actions/view/swing/iconArrowDown.png"), CANCEL("fa-remove", "com/synaptix/widget/actions/view/swing/iconCancel.png"), CLEAR("fa-ban",
				"com/synaptix/widget/actions/view/swing/iconRemove.png"), CLONE("fa-copy", "com/synaptix/widget/actions/view/swing/iconClone.png"), CLOSE("fa-close", null), COPY("fa-copy",
				"com/synaptix/widget/actions/view/swing/iconCopy.png"), CORBEILLE("fa-trash", "com/synaptix/widget/actions/view/swing/iconCorbeille.png"), CUT("fa-cut",
				"com/synaptix/widget/actions/view/swing/iconCut.png"), DELETE("fa-remove", "com/synaptix/widget/actions/view/swing/iconDelete.png"), DOWN("fa-arrow-down",
				"com/synaptix/widget/actions/view/swing/iconDown.png"), EDIT("fa-edit", "com/synaptix/widget/actions/view/swing/iconEdit.png"), EXPORT_EXCEL("fa-file-excel-o",
				"com/synaptix/widget/actions/view/swing/iconExcel.gif"), EXPORT_PDF("fa-file-pdf-o", "com/synaptix/widget/actions/view/swing/iconPdf.png"), EXPORT_TXT("fa-file-text-o",
				"com/synaptix/widget/actions/view/swing/iconTextDocument.png"), FINISH(null, null), HELP("fa-question", "com/synaptix/widget/actions/view/swing/iconHelp.png"), DOWNLOAD("fa-download",
				"com/synaptix/widget/actions/view/swing/iconDownload.png"), UPLOAD("fa-upload", "com/synaptix/widget/actions/view/swing/iconUpload.png"), MAP("fa-map-marker",
				"com/synaptix/widget/actions/view/swing/iconMap.png"), NEXT("fa-step-forward", "com/synaptix/widget/actions/view/swing/iconNext.png"), NO(null, null), OPTIONS("fa-gear",
				"com/synaptix/widget/actions/view/swing/iconOption.png"), PASTE("fa-paste", "com/synaptix/widget/actions/view/swing/iconPaste.png"), PREVIOUS("fa-step-backward",
				"com/synaptix/widget/actions/view/swing/iconPrevious.png"), PRINT("fa-print", "com/synaptix/widget/actions/view/swing/iconPrint.png"), REDO("fa-repeat",
				"com/synaptix/widget/actions/view/swing/iconRedo.png"), REFRESH("fa-refresh", "com/synaptix/widget/actions/view/swing/iconRefresh.png"), REMOVE("fa-remove",
				"com/synaptix/widget/actions/view/swing/iconRemove.png"), SAVE("fa-save", "com/synaptix/widget/actions/view/swing/iconSave.png"), SEARCH("fa-search",
				"com/synaptix/widget/actions/view/swing/iconSearch.png"), SEND("fa-upload", "com/synaptix/widget/actions/view/swing/iconSend.png"), SETTINGS("fa-gear",
				"com/synaptix/widget/actions/view/swing/iconSettings.png"), UNDO("fa-undo", "com/synaptix/widget/actions/view/swing/iconUndo.png"), UP("fa-arrow-up",
				"com/synaptix/widget/actions/view/swing/iconUp.png"), YES(null, null);

		private final String faName;

		private final String fileName;

		private final URL fileURL;

		private final Icon faIcon;

		private final Icon fileIcon;

		private Icons(String faName, String fileName) {
			this.faName = faName;
			this.fileName = fileName;

			this.faIcon = faName != null ? FontAwesomeHelper.getIcon(faName, size, Color.BLACK) : null;
			this.fileIcon = fileName != null ? StaticImage.getImageScale(new ImageIcon(IconHelper.class.getClassLoader().getResource(fileName)), size) : null;
			this.fileURL = fileName != null ? IconHelper.class.getClassLoader().getResource(fileName) : null;
		}

		public String getFaName() {
			return faName;
		}

		public String getFileName() {
			return fileName;
		}

		public URL getFileURL() {
			return fileURL;
		}

		public Icon getFaIcon() {
			return faIcon;
		}

		public Icon getFileIcon() {
			return fileIcon;
		}

		public Icon getIcon() {
			return useFontAwesome ? faIcon : fileIcon;
		}
	}
}
