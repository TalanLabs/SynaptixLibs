package simpledaystimeline.common;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class HoursTransferable implements Transferable {

	public final static DataFlavor HOURS_FLAVOR;

	private final static DataFlavor[] FLAVORS;

	private Hours hours;

	static {
		DataFlavor flavor = null;
		FLAVORS = new DataFlavor[1];
		try {
			flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Hours.class.getName());
			FLAVORS[0] = flavor;
		} catch (ClassNotFoundException e) {
			FLAVORS[0] = null;
		}
		HOURS_FLAVOR = flavor;
	}

	public HoursTransferable(Hours hours) {
		this.hours = hours;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!flavor.equals(HOURS_FLAVOR)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return hours;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(HOURS_FLAVOR);
	}
}