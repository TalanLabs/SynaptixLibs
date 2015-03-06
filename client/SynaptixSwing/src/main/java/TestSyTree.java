import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.synaptix.swing.JSyTree;
import com.synaptix.swing.tree.AbstractSpecialTreeModel;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.Manager;

public class TestSyTree extends JPanel {

	private static final long serialVersionUID = -8266372950629199649L;

	private MySyTreeModel treeModel;

	private JSyTree tree;

	private JPopupMenu dossierPopupMenu;

	private Action addDossierAction;

	private Action deleteDossierAction;

	public TestSyTree() {
		super(new BorderLayout());

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		addDossierAction = new AddDossierAction();
		deleteDossierAction = new DeleteDossierAction();
	}

	private void createComponents() {
		treeModel = new MySyTreeModel();
		tree = new JSyTree(treeModel);

		dossierPopupMenu = new JPopupMenu();
	}

	private void initComponents() {
		createComponents();

		tree.setEditable(true);
		tree.setCellEditor(new LibelleTreeCellEditor());
		tree.setCellRenderer(new MyTreeCellRenderer());
		tree.addMouseListener(new TreeMouseListener());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON);
		tree.setTransferHandler(new TreeTransferHandler());

		dossierPopupMenu.add(addDossierAction);
		dossierPopupMenu.addSeparator();
		dossierPopupMenu.add(deleteDossierAction);
		dossierPopupMenu.addPopupMenuListener(new DossierPopupMenuListener());
	}

	private JComponent buildContents() {
		return new JScrollPane(tree);
	}

	private final class TreeTransferHandler extends TransferHandler {

		private static final long serialVersionUID = -5969803900060694638L;

		protected Transferable createTransferable(JComponent c) {
			if (tree.getSelectionCount() == 1) {
				TreePath treePath = tree.getSelectionPath();
				Dossier dossier = (Dossier) treePath.getLastPathComponent();
				if (!dossier.isProteger()) {
					return new DossierTransferable(treePath);
				}
			}
			return null;
		}

		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		public boolean canImport(TransferSupport support) {
			Transferable t = support.getTransferable();
			if (t != null
					&& t
							.isDataFlavorSupported(DossierTransferable.DOSSIER_FLAVOR)) {
				try {
					TreePath inTreePath = (TreePath) t
							.getTransferData(DossierTransferable.DOSSIER_FLAVOR);
					JTree.DropLocation dl = (JTree.DropLocation) support
							.getDropLocation();
					if (dl != null) {
						TreePath outTreePath = dl.getPath();
						Dossier dossier = (Dossier) outTreePath
								.getLastPathComponent();
						if (!dossier.isProteger()) {
							Dossier d1 = (Dossier) inTreePath
									.getLastPathComponent();

							boolean trouve = false;
							int i = 0;
							while (i < outTreePath.getPathCount() && !trouve) {
								Object o = outTreePath.getPathComponent(i);
								if (o.equals(d1)) {
									trouve = true;
								}
								i++;
							}

							if (!trouve) {
								if (!inTreePath
										.getParentPath()
										.getLastPathComponent()
										.equals(
												outTreePath
														.getLastPathComponent())) {
									return true;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}

		public boolean importData(TransferSupport support) {
			if (canImport(support)) {
				Transferable t = support.getTransferable();
				try {
					TreePath inTreePath = (TreePath) t
							.getTransferData(DossierTransferable.DOSSIER_FLAVOR);
					JTree.DropLocation dl = (JTree.DropLocation) support
							.getDropLocation();

					if (dl != null) {
						TreePath outTreePath = dl.getPath();

						treeModel.moveDossier(inTreePath, outTreePath);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}

	private final class MySyTreeModel extends AbstractSpecialTreeModel {

		private Dossier root = null;

		public MySyTreeModel() {
			root = new Dossier("Parent", new Dossier[] {
					new Dossier("Sandra", new Dossier[] {
							new Dossier("Familles", new Dossier[] {
									new Dossier("Christiane", true),
									new Dossier("Michel") }),
							new Dossier("Amis") }),
					new Dossier("Gabriel", new Dossier[] {
							new Dossier("Familles", new Dossier[] {
									new Dossier("Béatrice"),
									new Dossier("Etienne") }),
							new Dossier("Amis",
									new Dossier[] { new Dossier("Julien"),
											new Dossier("Cyril"),
											new Dossier("Damien") }) }) });
		}

		public Object getChild(Object parent, int index) {
			Dossier c = (Dossier) parent;
			return c.getFilsList().get(index);
		}

		public int getChildCount(Object parent) {
			Dossier c = (Dossier) parent;
			return c.getFilsList() == null ? 0 : c.getFilsList().size();
		}

		public int getIndexOfChild(Object parent, Object child) {
			Dossier c = (Dossier) parent;
			return c.getFilsList().indexOf(child);
		}

		public Object getRoot() {
			return root;
		}

		public boolean isLeaf(Object node) {
			Dossier c = (Dossier) node;
			return c.getFilsList() == null || c.getFilsList().isEmpty();
		}

		public boolean isEditable(TreePath path) {
			Dossier l = (Dossier) path.getLastPathComponent();
			return !l.isProteger();
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
			Dossier c = (Dossier) path.getLastPathComponent();
			c.setName((String) newValue);

			fireTreeNodesChanged(this, path.getPath(), null, null);
		}

		public void addDossier(TreePath path, Dossier newDossier) {
			Dossier d = (Dossier) path.getLastPathComponent();

			d.getFilsList().add(newDossier);
			int index = d.getFilsList().indexOf(newDossier);

			fireTreeNodesInserted(this, path.getPath(), new int[] { index },
					new Object[] { newDossier });
			fireTreeNodesChanged(this, path.getPath(), null, null);
		}

		public void removeDossier(TreePath path) {
			Dossier d = (Dossier) path.getLastPathComponent();

			int count = path.getPathCount();
			if (count == 1) {
				root = null;

				fireTreeNodesRemoved(this, new Object[] {}, new int[] { 0 },
						new Object[] { root });
			} else {
				Dossier dp = (Dossier) path.getParentPath()
						.getLastPathComponent();
				int index = dp.getFilsList().indexOf(d);
				dp.getFilsList().remove(d);

				Object[] os = new Object[count - 1];
				for (int i = 0; i < count - 1; i++) {
					os[i] = path.getPathComponent(i);
				}

				fireTreeNodesRemoved(this, os, new int[] { index },
						new Object[] { d });
				fireTreeNodesChanged(this, os, null, null);
			}
		}

		public void moveDossier(TreePath inPath, TreePath outPath) {
			Dossier d1 = (Dossier) inPath.getLastPathComponent();

			removeDossier(inPath);
			addDossier(outPath, d1);
		}
	}

	private final class LibelleTreeCellEditor extends AbstractCellEditor
			implements TreeCellEditor {

		private static final long serialVersionUID = -6447741139006078789L;

		private JTextField textField;

		public LibelleTreeCellEditor() {
			super();

			textField = new JTextField();
			textField.setBorder(null);
			textField.addActionListener(new MyActionListener());
		}

		public Object getCellEditorValue() {
			return textField.getText();
		}

		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) {
				return ((MouseEvent) anEvent).getClickCount() >= 2;
			}
			return true;
		}

		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}

		public boolean stopCellEditing() {
			fireEditingStopped();
			return true;
		}

		public void cancelCellEditing() {
			fireEditingCanceled();
		}

		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
			Dossier c = (Dossier) value;
			textField.setText(c.getName());
			return textField;
		}

		private final class MyActionListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				stopCellEditing();
			}
		}
	}

	private final class MyTreeCellRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = -3722014389420849021L;

		public MyTreeCellRenderer() {
			super();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Component c = super.getTreeCellRendererComponent(tree, value,
					selected, expanded, leaf, row, hasFocus);
			JLabel label = (JLabel) c;
			if (value != null) {
				Dossier d = (Dossier) value;
				int n = d.getFilsList() != null ? d.getFilsList().size() : 0;
				label.setText(d.getName() + " (" + n + ")");
			} else {
				label.setText("");
			}
			return label;
		}
	}

	private final class TreeMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (tree.getSelectionCount() == 1) {
					dossierPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (tree.getSelectionCount() == 1) {
					dossierPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
	}

	private final class DossierPopupMenuListener implements PopupMenuListener {

		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			addDossierAction.setEnabled(false);
			deleteDossierAction.setEnabled(false);

			if (tree.getSelectionCount() == 1) {
				TreePath path = tree.getSelectionPath();
				Dossier d = (Dossier) path.getLastPathComponent();

				if (!d.isProteger()) {
					addDossierAction.setEnabled(true);
					deleteDossierAction.setEnabled(true);
				}
			}
		}
	}

	private final class AddDossierAction extends AbstractAction {

		private static final long serialVersionUID = 2658480720026121797L;

		public AddDossierAction() {
			super("Ajouter un dossier");
		}

		public void actionPerformed(ActionEvent e) {
			if (tree.getSelectionCount() == 1) {
				TreePath path = tree.getSelectionPath();

				Dossier dossier = new Dossier("Nouveau dossier");

				treeModel.addDossier(path, dossier);

				int count = path.getPathCount();
				Object[] os = new Object[count + 1];
				for (int i = 0; i < count; i++) {
					os[i] = path.getPathComponent(i);
				}
				os[count] = dossier;

				tree.startEditingAtPath(new TreePath(os));
			}
		}
	}

	private final class DeleteDossierAction extends AbstractAction {

		private static final long serialVersionUID = 2658480720026121797L;

		public DeleteDossierAction() {
			super("Effacer le dossier...");
		}

		public void actionPerformed(ActionEvent e) {
			if (tree.getSelectionCount() == 1) {
				TreePath path = tree.getSelectionPath();
				Dossier d = (Dossier) path.getLastPathComponent();

				if (JOptionPane.showConfirmDialog(GUIWindow.getActiveWindow(),
						"Voulez-vous effacer le dossier " + d.getName() + " ?",
						"Effacer", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					treeModel.removeDossier(path);
				}
			}
		}
	}

	public enum State {
		NO_LOAD, LOADING, LOAD
	};
	
	private class Dossier {

		private String name;

		private boolean proteger;

		private List<Dossier> filsList;
		
		private State state;

		public Dossier(String name) {
			this(name, null);
		}

		public Dossier(String name, boolean proteger) {
			this(name, proteger, null);
		}

		public Dossier(String name, Dossier[] fils) {
			this(name, false, fils);
		}

		public Dossier(String name, boolean proteger, Dossier[] fils) {
			this.state = State.NO_LOAD;
			this.name = name;
			this.proteger = proteger;

			this.filsList = new ArrayList<Dossier>();
			if (fils != null) {
				this.filsList.addAll(Arrays.asList(fils));
			}
		}

		public List<Dossier> getFilsList() {
			return filsList;
		}

		public void setFilsList(List<Dossier> filsList) {
			this.filsList = filsList;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public boolean isProteger() {
			return proteger;
		}

		public void setProteger(boolean proteger) {
			this.proteger = proteger;
		}

		public State getState() {
			return state;
		}
		
		public void setState(State state) {
			this.state = state;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder("( Dossier ");
			sb.append(hashCode());
			sb.append(" ->");
			sb.append(" name ");
			sb.append(getName());
			sb.append(" proteger ");
			sb.append(isProteger());
			sb.append(" )");
			return sb.toString();
		}
	}

	private static final class DossierTransferable implements Transferable {

		public final static DataFlavor DOSSIER_FLAVOR;

		private final static DataFlavor[] FLAVORS;

		private TreePath treePath;

		static {
			DataFlavor flavor = null;
			FLAVORS = new DataFlavor[1];
			try {
				flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
						+ ";class=" + TreePath.class.getName());
				FLAVORS[0] = flavor;
			} catch (ClassNotFoundException e) {
				FLAVORS[0] = null;
			}
			DOSSIER_FLAVOR = flavor;
		}

		public DossierTransferable(TreePath treePath) {
			this.treePath = treePath;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!flavor.equals(DOSSIER_FLAVOR)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return treePath;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return FLAVORS;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(DOSSIER_FLAVOR);
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add(new TestSyTree(), BorderLayout.CENTER);

		frame.setTitle("TestSyTree");
		frame.pack();
		frame.setSize(800, 600);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
