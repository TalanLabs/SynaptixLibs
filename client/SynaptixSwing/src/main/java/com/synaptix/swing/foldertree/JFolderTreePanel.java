package com.synaptix.swing.foldertree;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.synaptix.swing.JSyTree;
import com.synaptix.swing.tree.AbstractSpecialTreeModel;

public class JFolderTreePanel<E extends IFolder> extends JPanel {

	private static final long serialVersionUID = -8266372950629199649L;

	private IFolderTreeContext<E> context;

	private MySyTreeModel treeModel;

	private JSyTree tree;

	private Action addFolderAction;

	private Action deleteFolderAction;

	private ExecutorService executorService;

	private FolderTreeCellRenderer<E> treeCellRenderer;

	private boolean activeRename;

	private boolean activeDrag;

	private IFolderTreeDrop<E> folderTreeDrop;

	private IFolderActionCustomizer<E> folderActionCustomizer;

	public JFolderTreePanel(IFolderTreeContext<E> context) {
		super(new BorderLayout());

		this.context = context;
		this.activeRename = true;
		this.activeDrag = true;

		executorService = Executors.newFixedThreadPool(1);

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		addFolderAction = new AddFolderAction();
		deleteFolderAction = new DeleteFolderAction();
	}

	private void createComponents() {
		treeModel = new MySyTreeModel();
		tree = new JSyTree(treeModel);

		treeCellRenderer = new FolderTreeCellRenderer<E>();
	}

	private void initComponents() {
		createComponents();

		tree.setEditable(true);
		tree.setCellEditor(new FolderIHMNameTreeCellEditor<E>());
		tree.setCellRenderer(treeCellRenderer);
		tree.addMouseListener(new TreeMouseListener());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON);
		tree.setTransferHandler(new TreeTransferHandler());
		tree.addTreeWillExpandListener(new MyTreeWillExpandListener());
		tree.addTreeSelectionListener(new MyTreeSelectionListener());
	}

	private JComponent buildContents() {
		return new JScrollPane(tree);
	}

	public void addFolderTreeSelectionListener(FolderTreeSelectionListener<E> l) {
		listenerList.add(FolderTreeSelectionListener.class, l);
	}

	public void removeFolderTreeSelectionListener(
			FolderTreeSelectionListener<E> l) {
		listenerList.remove(FolderTreeSelectionListener.class, l);
	}

	@SuppressWarnings("unchecked")
	private void fireSelectedFolderChanged(E[] folders) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == FolderTreeSelectionListener.class) {
				((FolderTreeSelectionListener<E>) listeners[i + 1])
						.selectedFolderChanged(folders);
			}
		}
	}

	public void setFolderTreeDrop(IFolderTreeDrop<E> folderTreeDrop) {
		this.folderTreeDrop = folderTreeDrop;
	}

	public IFolderTreeDrop<E> getFolderTreeDrop() {
		return folderTreeDrop;
	}

	public void setFolderActionCustomizer(
			IFolderActionCustomizer<E> folderActionCustomizer) {
		this.folderActionCustomizer = folderActionCustomizer;
	}

	public IFolderActionCustomizer<E> getFolderActionCustomizer() {
		return folderActionCustomizer;
	}

	public void setActiveRename(boolean activeRename) {
		this.activeRename = activeRename;
	}

	public boolean isActiveRename() {
		return activeRename;
	}

	public void setActiveDrag(boolean activeDrag) {
		this.activeDrag = activeDrag;
	}

	public boolean isActiveDrag() {
		return activeDrag;
	}

	public void setRoot(E value) {
		FolderIHM<E> d = new FolderIHM<E>(value);
		treeModel.setRoot(d);

		loadingFolderChildrens(d, new TreePath(d));
	}

	public void setFolderDecorate(IFolderTreeDecorate<E> folderDecorate) {
		treeCellRenderer.setFolderDecorate(folderDecorate);

		repaint();
	}

	public IFolderTreeDecorate<E> getFolderDecorate() {
		return treeCellRenderer.getFolderDecorate();
	}

	private void loadingFolderChildrens(FolderIHM<E> folder, TreePath path) {
		folder.setFolderTreeState(FolderTreeState.LOADING);
		treeModel.updateFolder(path, folder);

		executorService
				.execute(new LoadingFolderChildrensRunTree(folder, path));
	}

	public void refreshTree() {
		tree.treeDidChange();
	}

	@SuppressWarnings("unchecked")
	private E[] convertFolderIHMPathToFolderPath(Object[] os) {
		E[] res = null;
		if (os != null && os.length > 0) {
			FolderIHM<E> c = (FolderIHM<E>) os[0];
			res = (E[]) Array.newInstance(c.getFolder().getClass(), os.length);

			for (int i = 0; i < os.length; i++) {
				FolderIHM<E> folderIHM = (FolderIHM<E>) os[i];
				res[i] = folderIHM.getFolder();
			}
		}
		return res;
	}

	private FolderIHM<E> findFolderIHMByE(FolderIHM<E> parent, E e) {
		FolderIHM<E> res = null;
		if (parent.getFolder().equals(e)) {
			res = parent;
		} else {
			List<FolderIHM<E>> folderIHMList = parent.getFilsList();
			if (folderIHMList != null && !folderIHMList.isEmpty()) {
				int i = 0;
				while (i < folderIHMList.size() && res == null) {
					res = findFolderIHMByE(folderIHMList.get(i), e);
					i++;
				}
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private Object[] convertFolderPathToFolderIHMPath(E[] es) {
		Object[] res = null;
		if (es != null && es.length > 0) {
			FolderIHM<E> root = (FolderIHM<E>) treeModel.getRoot();

			res = new Object[es.length];
			for (int i = 0; i < es.length; i++) {
				E e = es[i];
				if (i == 0) {
					res[i] = findFolderIHMByE(root, e);
				} else {
					res[i] = findFolderIHMByE((FolderIHM<E>) res[i - 1], e);
				}
			}
		}
		return res;
	}

	private final class MyTreeWillExpandListener implements
			TreeWillExpandListener {

		public void treeWillCollapse(TreeExpansionEvent event)
				throws ExpandVetoException {
		}

		@SuppressWarnings("unchecked")
		public void treeWillExpand(TreeExpansionEvent event)
				throws ExpandVetoException {
			TreePath path = event.getPath();
			FolderIHM<E> c = (FolderIHM<E>) path.getLastPathComponent();
			if (c.getFolderTreeState().equals(FolderTreeState.NO_LOAD)) {
				loadingFolderChildrens(c, path);
			}
		}
	}

	private final class MySyTreeModel extends AbstractSpecialTreeModel {

		private FolderIHM<E> root = null;

		public MySyTreeModel() {
			root = null;
		}

		public void setRoot(FolderIHM<E> root) {
			this.root = root;

			fireTreeStructureChanged(this, new Object[] { root }, null, null);
		}

		@SuppressWarnings("unchecked")
		public Object getChild(Object parent, int index) {
			FolderIHM<E> c = (FolderIHM<E>) parent;
			return c.getFilsList().get(index);
		}

		@SuppressWarnings("unchecked")
		public int getChildCount(Object parent) {
			FolderIHM<E> c = (FolderIHM<E>) parent;
			if (c.getFolderTreeState().equals(FolderTreeState.LOAD)) {
				return c.getFilsList().size();
			}
			return 0;
		}

		@SuppressWarnings("unchecked")
		public int getIndexOfChild(Object parent, Object child) {
			FolderIHM<E> c = (FolderIHM<E>) parent;
			return c.getFilsList().indexOf(child);
		}

		public Object getRoot() {
			return root;
		}

		@SuppressWarnings("unchecked")
		public boolean isLeaf(Object node) {
			FolderIHM<E> c = (FolderIHM<E>) node;
			return c.getFolder().isLeaf();
		}

		@SuppressWarnings("unchecked")
		public boolean isEditable(TreePath path) {
			FolderIHM<E> folder = (FolderIHM<E>) path.getLastPathComponent();
			return activeRename && folder.getFolder().isUpdateNameEnabled();
		}

		@SuppressWarnings("unchecked")
		public void valueForPathChanged(TreePath path, Object newValue) {
			FolderIHM<E> folder = (FolderIHM<E>) path.getLastPathComponent();
			folder.setName((String) newValue);

			executorService.execute(new UpdateFolderRunTree(folder, path));
		}

		public void updateFolder(TreePath path, FolderIHM<E> folder) {
			fireTreeNodesChanged(this, path.getPath(), null, null);
		}

		public void updateFolderChildrens(TreePath path, FolderIHM<E> folder) {
			List<FolderIHM<E>> folderList = folder.getFilsList();
			int[] is = new int[folderList == null ? 0 : folderList.size()];
			Object[] os = new Object[is.length];
			for (int i = 0; i < is.length; i++) {
				is[i] = i;
				os[i] = folderList.get(i);
			}

			fireTreeNodesInserted(this, path.getPath(), is, os);
			fireTreeNodesChanged(this, path.getPath(), null, null);
		}

		@SuppressWarnings("unchecked")
		public void addFolder(TreePath path, FolderIHM<E> newFolder) {
			FolderIHM<E> parent = (FolderIHM<E>) path.getLastPathComponent();

			parent.getFilsList().add(newFolder);
			int index = parent.getFilsList().indexOf(newFolder);

			fireTreeNodesInserted(this, path.getPath(), new int[] { index },
					new Object[] { newFolder });
			fireTreeNodesChanged(this, path.getPath(), null, null);
		}

		@SuppressWarnings("unchecked")
		public void removeFolder(TreePath path) {
			FolderIHM<E> d = (FolderIHM<E>) path.getLastPathComponent();

			int count = path.getPathCount();
			if (count == 1) {
				root = null;

				fireTreeNodesRemoved(this, new Object[] {}, new int[] { 0 },
						new Object[] { root });
			} else {
				FolderIHM<E> dp = (FolderIHM<E>) path.getParentPath()
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

		@SuppressWarnings("unchecked")
		public void moveFolder(TreePath inPath, TreePath outPath) {
			FolderIHM<E> d1 = (FolderIHM<E>) inPath.getLastPathComponent();

			removeFolder(inPath);
			addFolder(outPath, d1);
		}
	}

	@SuppressWarnings("unchecked")
	private final class TreeTransferHandler extends TransferHandler {

		private static final long serialVersionUID = -5969803900060694638L;

		protected Transferable createTransferable(JComponent c) {
			if (activeDrag && tree.getSelectionCount() == 1) {
				TreePath treePath = tree.getSelectionPath();
				if (treePath != null) {
					FolderIHM<E> folder = (FolderIHM<E>) treePath
							.getLastPathComponent();
					if (folder.getFolder().isDragEnabled()) {
						return new FolderPathTransferable<E>(
								convertFolderIHMPathToFolderPath(treePath
										.getPath()));
					}
				}
			}
			return null;
		}

		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		public boolean canImport(TransferSupport support) {
			Transferable t = support.getTransferable();
			if (t != null) {
				JTree.DropLocation dl = (JTree.DropLocation) support
						.getDropLocation();
				if (dl != null) {
					TreePath outTreePath = dl.getPath();
					if (outTreePath != null) {
						FolderIHM<E> d2 = (FolderIHM<E>) outTreePath
								.getLastPathComponent();
						if (t
								.isDataFlavorSupported(FolderPathTransferable.FOLDER_FLAVOR)) {
							try {
								E[] es = (E[]) t
										.getTransferData(FolderPathTransferable.FOLDER_FLAVOR);
								TreePath inTreePath = new TreePath(
										convertFolderPathToFolderIHMPath(es));
								FolderIHM<E> d1 = (FolderIHM<E>) inTreePath
										.getLastPathComponent();

								if (d2.getFolder().isDropEnabled()) {
									boolean trouve = false;
									int i = 0;
									while (i < outTreePath.getPathCount()
											&& !trouve) {
										Object o = outTreePath
												.getPathComponent(i);
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
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (folderTreeDrop != null) {
							if (folderTreeDrop.canDrop(t, d2.getFolder())) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		public boolean importData(TransferSupport support) {
			if (canImport(support)) {
				Transferable t = support.getTransferable();

				JTree.DropLocation dl = (JTree.DropLocation) support
						.getDropLocation();
				TreePath outTreePath = dl.getPath();
				FolderIHM<E> folder = (FolderIHM<E>) outTreePath
						.getLastPathComponent();

				if (t
						.isDataFlavorSupported(FolderPathTransferable.FOLDER_FLAVOR)) {
					try {
						E[] es = (E[]) t
								.getTransferData(FolderPathTransferable.FOLDER_FLAVOR);
						TreePath inTreePath = new TreePath(
								convertFolderPathToFolderIHMPath(es));

						if (!folder.getFolderTreeState().equals(
								FolderTreeState.LOAD)) {
							loadingFolderChildrens(folder, outTreePath);
						}
						executorService.execute(new MoveFolderRunTree(
								inTreePath, outTreePath));

						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (folderTreeDrop != null) {
					folderTreeDrop.done(t, folder.getFolder());
					return true;
				}
			}
			return false;
		}
	}

	private final class TreeMouseListener extends MouseAdapter {

		@SuppressWarnings("unchecked")
		private void showPopupMenu(MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (tree.getSelectionCount() == 1) {
					TreePath path = tree.getSelectionPath();
					FolderIHM<E> folderIHM = (FolderIHM<E>) path
							.getLastPathComponent();
					E folder = folderIHM.getFolder();

					JPopupMenu folderPopupMenu = new JPopupMenu();
					if (folder.isAddChildrenEnabled()) {
						folderPopupMenu.add(addFolderAction);
						if (folderActionCustomizer != null) {
							String name = folderActionCustomizer
									.getAddChildrenFolderName(folder);
							if (name != null) {
								addFolderAction.putValue(Action.NAME, name);
							}
						}
					}
					if (folder.isDeleteEnabled()) {
						folderPopupMenu.add(deleteFolderAction);
						if (folderActionCustomizer != null) {
							String name = folderActionCustomizer
									.getDeleteFolderName(folder);
							if (name != null) {
								deleteFolderAction.putValue(Action.NAME, name);
							}
						}
					}

					if (folderActionCustomizer != null) {
						folderActionCustomizer.visitFolderPopUp(
								folderPopupMenu, folder);
					}

					if (folderPopupMenu.getComponentCount() > 0) {
						folderPopupMenu.show(e.getComponent(), e.getX(), e
								.getY());
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			showPopupMenu(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopupMenu(e);
		}
	}

	@SuppressWarnings("unchecked")
	private final class AddFolderAction extends AbstractAction {

		private static final long serialVersionUID = 2658480720026121797L;

		public AddFolderAction() {
			super("Ajouter un dossier");
		}

		public void actionPerformed(ActionEvent e) {
			if (tree.getSelectionCount() == 1) {
				TreePath path = tree.getSelectionPath();
				FolderIHM<E> parent = (FolderIHM<E>) path
						.getLastPathComponent();
				FolderIHM<E> folder = new FolderIHM<E>(context
						.createEmptyFolder());

				if (!parent.getFolderTreeState().equals(FolderTreeState.LOAD)) {
					loadingFolderChildrens(parent, path);
				}
				executorService.execute(new AddFolderRunTree(parent, folder,
						path));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private final class DeleteFolderAction extends AbstractAction {

		private static final long serialVersionUID = 2658480720026121797L;

		public DeleteFolderAction() {
			super("Effacer le dossier...");
		}

		public void actionPerformed(ActionEvent e) {
			if (tree.getSelectionCount() == 1) {
				TreePath path = tree.getSelectionPath();
				FolderIHM<E> parent = path.getParentPath() != null ? (FolderIHM<E>) path
						.getParentPath().getLastPathComponent()
						: null;
				FolderIHM<E> folder = (FolderIHM<E>) path
						.getLastPathComponent();

				if (folderActionCustomizer == null
						|| folderActionCustomizer.isContinueDeleteFolder(folder
								.getFolder())) {
					executorService.execute(new DeleteFolderRunTree(parent,
							folder, path));
				}
			}
		}
	}

	private final class LoadingFolderChildrensRunTree implements Runnable {

		private FolderIHM<E> folder;

		private TreePath path;

		public LoadingFolderChildrensRunTree(FolderIHM<E> folder, TreePath path) {
			this.folder = folder;
			this.path = path;
		}

		public void run() {
			try {
				List<E> list = context.findFolderChildrensByFolder(folder
						.getFolder());

				List<FolderIHM<E>> folderList = new ArrayList<FolderIHM<E>>();

				for (E value : list) {
					folderList.add(new FolderIHM<E>(value));
				}

				folder.getFilsList().addAll(folderList);

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						folder.setFolderTreeState(FolderTreeState.LOAD);
						treeModel.updateFolderChildrens(path, folder);

						tree.expandPath(path);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private final class AddFolderRunTree implements Runnable {

		private FolderIHM<E> parent;

		private FolderIHM<E> folder;

		private TreePath path;

		public AddFolderRunTree(FolderIHM<E> parent, FolderIHM<E> folder,
				TreePath path) {
			this.parent = parent;
			this.folder = folder;
			this.path = path;
		}

		public void run() {
			try {
				context.addFolder(parent.getFolder(), folder.getFolder());

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						treeModel.addFolder(path, folder);

						int count = path.getPathCount();
						Object[] os = new Object[count + 1];
						for (int i = 0; i < count; i++) {
							os[i] = path.getPathComponent(i);
						}
						os[count] = folder;

						tree.startEditingAtPath(new TreePath(os));
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private final class UpdateFolderRunTree implements Runnable {

		private FolderIHM<E> folder;

		private TreePath path;

		public UpdateFolderRunTree(FolderIHM<E> folder, TreePath path) {
			this.folder = folder;
			this.path = path;
		}

		public void run() {
			try {
				context.updateNameFolder(folder.getFolder(), folder.getName());

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						treeModel.updateFolder(path, folder);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private final class DeleteFolderRunTree implements Runnable {

		private FolderIHM<E> parent;

		private FolderIHM<E> folder;

		private TreePath path;

		public DeleteFolderRunTree(FolderIHM<E> parent, FolderIHM<E> folder,
				TreePath path) {
			this.parent = parent;
			this.folder = folder;
			this.path = path;
		}

		public void run() {
			try {
				context.deleteFolder(parent.getFolder(), folder.getFolder());

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						treeModel.removeFolder(path);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private final class MoveFolderRunTree implements Runnable {

		private TreePath inTreePath;

		private TreePath outTreePath;

		public MoveFolderRunTree(TreePath inTreePath, TreePath outTreePath) {
			this.inTreePath = inTreePath;
			this.outTreePath = outTreePath;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			FolderIHM<E> origine = (FolderIHM<E>) inTreePath.getParentPath()
					.getLastPathComponent();
			FolderIHM<E> destination = (FolderIHM<E>) outTreePath
					.getLastPathComponent();
			FolderIHM<E> folder = (FolderIHM<E>) inTreePath
					.getLastPathComponent();
			try {
				context.moveFolder(origine.getFolder(),
						destination.getFolder(), folder.getFolder());

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						treeModel.moveFolder(inTreePath, outTreePath);
						tree.expandPath(outTreePath);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private final class MyTreeSelectionListener implements
			TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent e) {
			TreePath path = e.getPath();
			if (path != null) {
				Object[] os = path.getPath();
				E[] es = convertFolderIHMPathToFolderPath(os);
				fireSelectedFolderChanged(es);
			} else {
				fireSelectedFolderChanged(null);
			}
		}
	}
}
