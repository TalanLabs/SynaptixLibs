package com.synaptix.swing.search.transferable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.synaptix.swing.search.SearchFilter;

public class SearchFilterTransferable implements Transferable {

	public static DataFlavor SEARCHFILTER_FLAVOR;

	private final static DataFlavor[] FLAVORS;
	
	private int index;
	
	static {
		FLAVORS = new DataFlavor[1];
		try {
			SEARCHFILTER_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=" + SearchFilter.class.getName());
			FLAVORS[0] = SEARCHFILTER_FLAVOR;
		} catch (ClassNotFoundException e) {
			FLAVORS[0] = null;
		}
	}
	public SearchFilterTransferable(int index) {
		this.index = index;
	}
	
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (!flavor.equals(SEARCHFILTER_FLAVOR)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return index;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(SEARCHFILTER_FLAVOR);
	}
}
