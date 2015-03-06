package com.synaptix.widget.grid;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GridCellTransferable implements Transferable {

	public final static DataFlavor GRID_CELL_FLAVOR;

	private final static DataFlavor[] FLAVORS;

	private final TransferData data;

	static {
		DataFlavor flavor = null;
		FLAVORS = new DataFlavor[1];
		try {
			flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + TransferData.class.getName());
			FLAVORS[0] = flavor;
		} catch (ClassNotFoundException e) {
			FLAVORS[0] = null;
		}
		GRID_CELL_FLAVOR = flavor;
	}

	public GridCellTransferable(Object value, Dimension dim, Point anchor) {
		this(value, dim, anchor, false);
	}

	public GridCellTransferable(Object value, Dimension dim, Point anchor, boolean local) {
		super();

		this.data = new TransferData(value, dim, anchor, local);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!flavor.equals(GRID_CELL_FLAVOR)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return data;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(GRID_CELL_FLAVOR);
	}

	public class TransferData {

		public final Object value;

		public final Dimension dimension;

		public final Point anchor;

		public final boolean local;

		public TransferData(Object value, Dimension dimension, Point anchor, boolean local) {
			super();
			this.value = value;
			this.dimension = dimension;
			this.anchor = anchor;
			this.local = local;
		}
	}
}