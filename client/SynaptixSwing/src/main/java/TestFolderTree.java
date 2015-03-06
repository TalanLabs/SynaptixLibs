import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.synaptix.swing.foldertree.FolderTreeSelectionListener;
import com.synaptix.swing.foldertree.IFolder;
import com.synaptix.swing.foldertree.IFolderActionCustomizer;
import com.synaptix.swing.foldertree.IFolderTreeContext;
import com.synaptix.swing.foldertree.IFolderTreeDrop;
import com.synaptix.swing.foldertree.JFolderTreePanel;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SynaptixTransferHandler;

public class TestFolderTree {

	private final static Random rand = new Random(0);

	private static long nextId = 0;

	private static synchronized BigDecimal nextId() {
		return new BigDecimal(nextId++);
	}

	private static final class MyFolder implements IFolder {

		private BigDecimal id;

		private BigDecimal idParent;

		private String name;

		private int childrenCount;

		private boolean readOnly;

		public MyFolder(String name, int childrenCount) {
			this(null, name, childrenCount);
		}

		public MyFolder(BigDecimal id, String name, int childrenCount) {
			this(id, name, childrenCount, false);
		}

		public MyFolder(BigDecimal id, String name, int childrenCount,
				boolean readOnly) {
			this.id = id;
			this.name = name;
			this.childrenCount = childrenCount;
			this.readOnly = readOnly;
		}

		public boolean isUpdateNameEnabled() {
			return !readOnly;
		}

		public boolean isLeaf() {
			return childrenCount == 0;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getChildrenCount() {
			return childrenCount;
		}

		public void setChildrenCount(int childrenCount) {
			this.childrenCount = childrenCount;
		}

		public void setId(BigDecimal id) {
			this.id = id;
		}

		public BigDecimal getId() {
			return id;
		}

		public void setIdParent(BigDecimal idParent) {
			this.idParent = idParent;
		}

		public BigDecimal getIdParent() {
			return idParent;
		}

		public boolean isDragEnabled() {
			return true;
		}

		public boolean isDropEnabled() {
			return true;
		}

		public boolean isAddChildrenEnabled() {
			return true;
		}

		public boolean isDeleteEnabled() {
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder("( MyFolder ->");
			sb.append(" id ");
			sb.append(getId());
			sb.append(" idParent ");
			sb.append(getIdParent());
			sb.append(" name ");
			sb.append(getName());
			sb.append(" childrenCount ");
			sb.append(getChildrenCount());
			sb.append(" isReadOnly ");
			sb.append(!isUpdateNameEnabled());
			sb.append(" )");
			return sb.toString();
		}
	}

	private static final class MyFolderContext implements
			IFolderTreeContext<MyFolder> {

		public MyFolder createEmptyFolder() {
			return new MyFolder("Nouveau folder", 0);
		}

		public void addFolder(MyFolder parent, MyFolder folder)
				throws Exception {
			folder.setId(nextId());
			folder.setIdParent(parent.getId());

			parent.setChildrenCount(parent.getChildrenCount() + 1);
		}

		public void deleteFolder(MyFolder parent, MyFolder folder)
				throws Exception {
		}

		public List<MyFolder> findFolderChildrensByFolder(MyFolder parent)
				throws Exception {
			List<MyFolder> res = new ArrayList<MyFolder>();
			for (int i = 0; i < parent.getChildrenCount(); i++) {
				MyFolder d = new MyFolder(nextId(), parent.getName() + "_"
						+ String.valueOf(i), rand.nextInt(10), parent
						.isUpdateNameEnabled()
						|| rand.nextInt(2) == 0 ? true : false);
				d.setIdParent(parent.getId());
				res.add(d);
			}

			return res;
		}

		public void moveFolder(MyFolder origine, MyFolder destination,
				MyFolder folder) throws Exception {
			folder.setIdParent(destination.getIdParent());

			origine.setChildrenCount(origine.getChildrenCount() - 1);
			destination.setChildrenCount(destination.getChildrenCount() + 1);
		}

		public void updateNameFolder(MyFolder folder, String name)
				throws Exception {
			folder.setName(name);

			System.out.println(folder);
		}
	}

	private static final class Fichier {

		private String name;

		public Fichier(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private static final class FichierTransferable implements Transferable {

		public final static DataFlavor FICHIER_FLAVOR;

		private final static DataFlavor[] FLAVORS;

		private Fichier fichier;

		static {
			DataFlavor flavor = null;
			FLAVORS = new DataFlavor[1];
			try {
				flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
						+ ";class=" + Fichier.class.getName());
				FLAVORS[0] = flavor;
			} catch (ClassNotFoundException e) {
				FLAVORS[0] = null;
			}
			FICHIER_FLAVOR = flavor;
		}

		public FichierTransferable(Fichier hours) {
			this.fichier = hours;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!flavor.equals(FICHIER_FLAVOR)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return fichier;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return FLAVORS;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(FICHIER_FLAVOR);
		}
	}

	private static final class LabelTransferHandler extends
			SynaptixTransferHandler {

		private static final long serialVersionUID = 6560212511433794019L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			return new FichierTransferable(new Fichier("Coucou"));
		}
	}

	private static final class MyFolderTreeDrop implements
			IFolderTreeDrop<MyFolder> {

		public boolean canDrop(Transferable transferable, MyFolder folder) {
			if (transferable != null
					&& transferable
							.isDataFlavorSupported(FichierTransferable.FICHIER_FLAVOR)) {
				return !folder.isUpdateNameEnabled();
			}
			return false;
		}

		public void done(Transferable transferable, MyFolder folder) {
			System.out.println("ici " + folder);
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());

		JTable table = new JTable(new Object[][] { { "drag me" } },
				new Object[] { "Test1" });
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDragEnabled(true);
		table.setTransferHandler(new LabelTransferHandler());

		final JFolderTreePanel<MyFolder> view = new JFolderTreePanel<MyFolder>(
				new MyFolderContext());
		view
				.addFolderTreeSelectionListener(new FolderTreeSelectionListener<MyFolder>() {

					public void selectedFolderChanged(MyFolder[] folderPath) {
						System.out.println(Arrays.toString(folderPath));
					}
				});
		view.setFolderTreeDrop(new MyFolderTreeDrop());

		view.setFolderActionCustomizer(new IFolderActionCustomizer<MyFolder>() {

			public boolean isContinueDeleteFolder(MyFolder folder) {
				return JOptionPane.showConfirmDialog(GUIWindow
						.getActiveWindow(), "Voulez-vous effacer le dossier "
						+ folder.getName() + " ?", "Effacer",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
			}

			public String getAddChildrenFolderName(MyFolder folder) {
				return "Ajouter un dossier à " + folder.getName();
			}

			public String getDeleteFolderName(MyFolder folder) {
				return "Effacer le dossier " + folder.getName();
			}

			public void visitFolderPopUp(JPopupMenu popupMenu, MyFolder folder) {
				popupMenu.add(new JMenuItem("Rien"));
			}
		});

		frame.getContentPane().add(view, BorderLayout.CENTER);
		frame.getContentPane().add(new JScrollPane(table), BorderLayout.EAST);

		frame.setTitle("TestFolderTree");
		frame.pack();
		frame.setSize(800, 600);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);

				view.setRoot(new MyFolder(nextId(), "Parents", 5));
			}
		});
	}
}
