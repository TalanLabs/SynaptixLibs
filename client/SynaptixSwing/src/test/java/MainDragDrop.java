import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.io.IOException;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MainDragDrop {

	private static final class DropPanel extends JLabel {

		private static final long serialVersionUID = -5225748790240477653L;

		private int heureDepart;

		private int heureArrivee;

		public DropPanel(String text) {
			super(text);

			this.heureDepart = -1;
			this.heureArrivee = -1;

			this.setOpaque(true);
			this.setTransferHandler(new MyTransferHandler());
			this.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));

			System.out.println(this.getDropTarget());
			try {
				this.getDropTarget().addDropTargetListener(new MyDropTargetListener());
			} catch (TooManyListenersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private final class MyDropTargetListener extends DropTargetAdapter {

			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
				System.out.println("ici dragEnter " + getText());
			}

			@Override
			public void dragExit(DropTargetEvent dte) {
				System.out.println("ici dragExit " + getText());
				clearSelection();
			}

			public void drop(DropTargetDropEvent dtde) {
				System.out.println("ici drop " + getText());
			}
		}

		public void setSelection(int heureDepart, int heureArrivee) {
			this.heureDepart = heureDepart;
			this.heureArrivee = heureArrivee;
			repaint();
		}

		public void clearSelection() {
			heureDepart = -1;
			heureArrivee = -1;
			repaint();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (heureDepart != -1 && heureArrivee != -1) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				int w = this.getWidth();
				int h = this.getHeight();

				int x1 = heureDepart * w / (24 * 60);
				int x2 = heureArrivee * w / (24 * 60);

				int w2 = x2 - x1 + 1;

				int h2 = h - h / 2;
				int y = h2 / 2;

				g2.setColor(Color.RED);
				g2.fillRect(x1, y, w2, h - h2);

				g2.setColor(Color.pink);
				g2.drawRect(x1, y, w2 - 1, h - h2 - 1);

				g2.dispose();
			}
		}

		private final class MyTransferHandler extends TransferHandler {

			private static final long serialVersionUID = 8513634368261935583L;

			public boolean canImport(TransferSupport support) {
				boolean res = false;
				if (support.isDrop()) {
					Transferable transferable = support.getTransferable();
					if (transferable != null) {
						if (transferable.isDataFlavorSupported(HoursTransferable.HOURS_FLAVOR)) {
							try {
								HoursTransferable.Hours hours = (HoursTransferable.Hours) transferable.getTransferData(HoursTransferable.HOURS_FLAVOR);

								setSelection(hours.minuteStart, hours.minuteEnd);

								res = true;
							} catch (Exception e) {
							}
						}
					}
				}
				return res;
			}
		}
	}

	private static final class HoursTransferable implements Transferable {

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

		public static final class Hours {

			private int minuteStart;

			private int minuteEnd;

			public Hours(int minuteStart, int minuteEnd) {
				super();
				this.minuteStart = minuteStart;
				this.minuteEnd = minuteEnd;
			}

			public int getMinuteStart() {
				return minuteStart;
			}

			public int getMinuteEnd() {
				return minuteEnd;
			}
		}
	}

	private static final class LabelTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 6560212511433794019L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			return new HoursTransferable(new HoursTransferable.Hours(0, 12 * 60));
		}
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTable table = new JTable(new Object[][] { { "drag me" } }, new Object[] { "Test1" });
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDragEnabled(true);
		table.setTransferHandler(new LabelTransferHandler());

		FormLayout layout = new FormLayout("FILL:600DLU:GROW(1.0)", "FILL:300PX:NONE,CENTER:3DLU:NONE,FILL:400PX:GROW(1.0),CENTER:3DLU:NONE,FILL:100DLU:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new DropPanel("Haut"), cc.xy(1, 1));
		builder.add(new DropPanel("Bas"), cc.xy(1, 3));
		builder.add(new JScrollPane(table), cc.xy(1, 5));

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}