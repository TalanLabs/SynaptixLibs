package com.synaptix.swing.foldertree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class FolderPathTransferable<E extends IFolder> implements Transferable {

	public final static DataFlavor FOLDER_FLAVOR;

	private final static DataFlavor[] FLAVORS;

	private E[] path;

	static {
		DataFlavor flavor = null;
		FLAVORS = new DataFlavor[1];
		try {
			flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=" + IFolder.class.getName());
			FLAVORS[0] = flavor;
		} catch (ClassNotFoundException e) {
			FLAVORS[0] = null;
		}
		FOLDER_FLAVOR = flavor;
	}

	public FolderPathTransferable(E[] path) {
		this.path = path;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (!flavor.equals(FOLDER_FLAVOR)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return path;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(FOLDER_FLAVOR);
	}
}
