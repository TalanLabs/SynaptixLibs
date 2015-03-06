import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.synaptix.swing.JSyList;

public class MainSyList {

	static JList list;

	private static final class MyTransferHandler extends TransferHandler {

		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		protected Transferable createTransferable(JComponent c) {
			return new StringTransferable((String) list.getSelectedValue());
		}

		public boolean canImport(TransferSupport support) {
			return true;
		}
	}

	private static final class StringTransferable implements Transferable {

		public static DataFlavor LIGNE_ROULEMENT_PARTAGE_FLAVOR;

		private static final DataFlavor[] FLAVORS;

		private String ligneRoulement;

		static {
			FLAVORS = new DataFlavor[1];
			try {
				LIGNE_ROULEMENT_PARTAGE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + String.class.getName()); //$NON-NLS-1$
				FLAVORS[0] = LIGNE_ROULEMENT_PARTAGE_FLAVOR;
			} catch (ClassNotFoundException e) {
				FLAVORS[0] = null;
			}
		}

		public StringTransferable(String ligneRoulement) {
			this.ligneRoulement = ligneRoulement;
		}

		public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return ligneRoulement;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return FLAVORS;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor != null && flavor.equals(LIGNE_ROULEMENT_PARTAGE_FLAVOR);
		}
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DefaultListModel listModel = new DefaultListModel();
		list = new JSyList(listModel);
		list.setFixedCellWidth(100);
		list.setFixedCellHeight(25);
		list.setDragEnabled(true);
		list.setDropMode(DropMode.INSERT);
		list.setTransferHandler(new MyTransferHandler());

		for (int i = 0; i < 10; i++) {
			listModel.addElement("Gaby_" + i);
		}

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(list, BorderLayout.CENTER);

		// frame.setSize(600, 600);
		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

}
