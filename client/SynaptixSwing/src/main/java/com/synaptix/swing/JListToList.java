package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.list.Filters;
import com.synaptix.swing.widget.JInformationPanel;

public class JListToList<E> extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 5720747155152648422L;

	private DataFlavor OBJECT_FLAVOR;

	private DataFlavor[] FLAVORS;

	private Action addAction;

	private Action removeAction;

	private Action addAllAction;

	private Action removeAllAction;

	private MyListModel allListModel;

	private JList allList;

	private MyListModel selectedListModel;

	private JList selectedList;

	private Comparator<E> comparator;

	private Object[] allCopy;

	private String identifier;

	private Filters<E> filters;

	private JInformationPanel informationPanel;

	public JListToList() {
		this(null, null);
	}

	public JListToList(String identifier) {
		this(identifier, null);
	}

	public JListToList(Object[] list) {
		this(null, list);
	}

	public JListToList(String identifier, Object[] list) {
		super(new BorderLayout());

		this.identifier = identifier;

		FLAVORS = new DataFlavor[2];

		DataFlavor flavor1 = null;
		try {
			flavor1 = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ObjectTransferable.class.getName() + (identifier != null ? ";listToList=" + identifier : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			FLAVORS[0] = flavor1;
		} catch (ClassNotFoundException e) {
			FLAVORS[0] = null;
		}
		OBJECT_FLAVOR = flavor1;

		initActions();
		initComponents();

		setAllValues(list);

		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
		addAction = new AddAction();
		addAction.setEnabled(false);
		removeAction = new RemoveAction();
		removeAction.setEnabled(false);

		addAllAction = buildAddAllAction();
		addAllAction.setEnabled(false);

		removeAllAction = buildRemoveAllAction();
		removeAllAction.setEnabled(false);
	}

	private void createComponents() {
		allListModel = new MyListModel(true);
		allList = new JList(allListModel);
		selectedListModel = new MyListModel(false);
		selectedList = new JList(selectedListModel);
		informationPanel = new JInformationPanel();
	}

	private void initComponents() {
		createComponents();

		informationPanel.setPreferredSize(new Dimension(10, 100));
		// informationPanel.setVisible(false);

		allList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		allList.getSelectionModel().addListSelectionListener(new MyListSelectionModel());
		allList.addMouseListener(new AllListMouseListener());
		allList.setDragEnabled(true);
		allList.setDropMode(DropMode.INSERT);
		allList.setTransferHandler(new MyTransferHandler());
		allList.setName("allList");

		selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectedList.getSelectionModel().addListSelectionListener(new MyListSelectionModel());
		selectedList.addMouseListener(new SelectedListMouseListener());
		selectedList.setDragEnabled(true);
		selectedList.setDropMode(DropMode.INSERT);
		selectedList.setTransferHandler(new MyTransferHandler());
		selectedList.setName("selectedList");
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout(
				"FILL:100DLU:GROW(0.5),FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:100DLU:GROW(0.5)", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,FILL:20DLU:GROW(0.5),CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,FILL:20DLU:GROW(0.5)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new JButton(addAllAction), cc.xy(3, 5));
		builder.add(new JButton(addAction), cc.xy(3, 7));
		builder.add(new JButton(removeAction), cc.xy(3, 9));
		builder.add(new JButton(removeAllAction), cc.xy(3, 11));
		builder.add(buildAllEditorPanel(), cc.xywh(1, 3, 1, 11));
		builder.add(new JScrollPane(selectedList), cc.xywh(5, 3, 1, 11));
		builder.addLabel(SwingMessages.getString("JListToList.3"), cc.xywh(1, 1, 1, 1, CellConstraints.CENTER, //$NON-NLS-1$
				CellConstraints.CENTER));
		builder.addLabel(SwingMessages.getString("JListToList.4"), cc.xywh(5, 1, 1, 1, //$NON-NLS-1$
				CellConstraints.CENTER, CellConstraints.CENTER));
		return builder.getPanel();
	}

	private JComponent buildAllEditorPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(allList), BorderLayout.CENTER);
		panel.add(informationPanel, BorderLayout.WEST);
		return panel;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addAction.setEnabled(enabled);
		addAllAction.setEnabled(enabled);
		removeAction.setEnabled(enabled);
		removeAllAction.setEnabled(enabled);
		allList.setEnabled(enabled);
		selectedList.setEnabled(enabled);

		updateAllActions();
	}

	public JList getAllList() {
		return allList;
	}

	public JList getSelectionList() {
		return selectedList;
	}

	public void setComparator(Comparator<E> comparator) {
		this.comparator = comparator;
		selectedListModel.refreshList();
		allListModel.refreshList();
	}

	public Comparator<E> getComparator() {
		return comparator;
	}

	public void setFilters(Filters<E> filters) {
		Filters<E> oldValue = getFilters();
		this.filters = filters;

		if (oldValue != null) {
			oldValue.removeChangeListener(this);
		}
		filters.addChangeListener(this);

		updateFilters();
	}

	public Filters<E> getFilters() {
		return filters;
	}

	public void setAllValues(Object[] list) {
		this.allCopy = list;

		for (Object e : selectedListModel.getList()) {
			selectedListModel.removeObject(e);
		}

		allListModel.setList(list);

		updateAllActions();
	}

	public void setSelectionValues(Object[] list) {
		for (Object e : selectedListModel.getList()) {
			allListModel.addObject(e);
			selectedListModel.removeObject(e);
		}

		if (list != null) {
			for (Object e : list) {
				if (allListModel.contains(e)) {
					selectedListModel.addObject(e);
					allListModel.removeObject(e);
				}
			}
		}

		fireChangeListener();
		updateAllActions();
	}

	public Object[] getSelectionValues() {
		return selectedListModel.getList();
	}

	private void updateAllActions() {
		if (allList.isEnabled()) {
			boolean nonVide = allListModel.getList() != null && allListModel.getList().length > 0;

			addAllAction.setEnabled(nonVide);

			addAction.setEnabled(nonVide && !allList.getSelectionModel().isSelectionEmpty());
		}

		if (selectedList.isEnabled()) {
			boolean nonVide = selectedListModel.getList() != null && selectedListModel.getList().length > 0;

			removeAllAction.setEnabled(nonVide);

			removeAction.setEnabled(nonVide && !selectedList.getSelectionModel().isSelectionEmpty());
		}
	}

	private void addObjects(Object[] os) {
		addObjects(os, -1);
	}

	protected void addObjects(Object[] os, int index) {
		if (index == -1) {
			for (Object o : os) {
				selectedListModel.addObject(o);
				allListModel.removeObject(o);
			}
		} else {
			for (int i = os.length - 1; i >= 0; i--) {
				Object o = os[i];
				selectedListModel.addObject(index, o);
				allListModel.removeObject(o);
			}

			selectedList.clearSelection();
		}

		if (allListModel.getSize() == 0) {
			allList.clearSelection();
		}
		if (selectedListModel.getSize() == 0) {
			selectedList.clearSelection();
		}

		updateAllActions();
	}

	protected void removeObjects(Object[] os) {
		for (Object o : os) {
			allListModel.addObject(o);
			selectedListModel.removeObject(o);
		}

		if (allListModel.getSize() == 0) {
			allList.clearSelection();
		}
		if (selectedListModel.getSize() == 0) {
			selectedList.clearSelection();
		}

		updateAllActions();
	}

	private void moveObject(int index, Object o) {
		selectedListModel.moveObject(index, o);

		selectedList.clearSelection();
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireChangeListener() {
		ChangeListener[] ls = listenerList.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : ls) {
			l.stateChanged(e);
		}
	}

	private void updateFilters() {
		allListModel.refreshList();

		if (allListModel.isFilters()) {
			informationPanel.setText(SwingMessages.getString("JListToList.FILTRE")); //$NON-NLS-1$
			informationPanel.setVisible(true);
		} else {
			informationPanel.setVisible(false);
			informationPanel.setText(null);
		}

		revalidate();
		repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		updateFilters();
	}

	protected Action buildAddAllAction() {
		return new AddAllAction();
	}

	protected Action buildRemoveAllAction() {
		return new RemoveAllAction();
	}

	private final class AddAllAction extends AbstractAction {

		private static final long serialVersionUID = 1131008510650286896L;

		public AddAllAction() {
			super(">>"); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			allList.clearSelection();
			selectedList.clearSelection();

			setAllValues(allCopy);
			setSelectionValues(allCopy);

			fireChangeListener();
		}
	}

	private final class AddAction extends AbstractAction {

		private static final long serialVersionUID = 1131008510650286896L;

		public AddAction() {
			super(">"); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = selectedList.getSelectedIndex();

			Object[] os = allList.getSelectedValues();
			addObjects(os, index != -1 ? index + 1 : -1);

			fireChangeListener();
		}
	}

	private final class RemoveAction extends AbstractAction {

		private static final long serialVersionUID = -1589218945959331011L;

		public RemoveAction() {
			super("<"); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object[] os = selectedList.getSelectedValues();
			removeObjects(os);

			fireChangeListener();
		}
	}

	private final class RemoveAllAction extends AbstractAction {

		private static final long serialVersionUID = -1589218945959331011L;

		public RemoveAllAction() {
			super("<<"); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			allList.clearSelection();
			selectedList.clearSelection();

			setAllValues(allCopy);

			fireChangeListener();
		}
	}

	private final class MyListModel extends AbstractListModel {

		private static final long serialVersionUID = 7714342249839583409L;

		private final List<Object> list;

		private final List<Object> filterList;

		private final boolean useFilter;

		public MyListModel(boolean useFilter) {
			super();

			this.useFilter = useFilter;
			this.list = new ArrayList<Object>();
			this.filterList = new ArrayList<Object>();
		}

		public Object[] getList() {
			return list.toArray();
		}

		public void setList(Object[] list) {
			this.list.clear();
			if (list != null) {
				for (Object e : list) {
					this.list.add(e);
				}
			}
			refreshList();
		}

		@Override
		public Object getElementAt(int index) {
			return useFilter && filters != null ? filterList.get(index) : list.get(index);
		}

		@Override
		public int getSize() {
			return useFilter && filters != null ? filterList.size() : list.size();
		}

		public void addObject(Object e) {
			addObject(-1, e);
		}

		@SuppressWarnings("unchecked")
		public void refreshList() {
			if (comparator != null) {
				Collections.sort((List<E>) list, comparator);
			}
			if (useFilter && filters != null) {
				filterList.clear();
				for (Object o : list) {
					if (filters.isFilter((E) o)) {
						filterList.add(o);
					}
				}
				fireContentsChanged(this, 0, filterList.size());
			} else {
				fireContentsChanged(this, 0, list.size());
			}
		}

		public void addObject(int index, Object e) {
			if (index == -1) {
				list.add(e);
				index = list.size() - 1;
			} else {
				list.add(index, e);
			}

			refreshList();
		}

		public void removeObject(Object e) {
			list.remove(e);

			refreshList();
		}

		public void moveObject(int index, Object e) {
			int i = list.indexOf(e);
			list.add(index, e);
			if (i < index) {
				list.remove(i);
			} else {
				list.remove(i + 1);
			}

			refreshList();
		}

		public boolean contains(Object e) {
			return list.contains(e);
		}

		public int convertRowFilterToRowModel(int rowFilter) {
			int res = -1;
			if (useFilter) {
				Object o = filterList.get(rowFilter);
				res = list.indexOf(o);
			} else {
				res = rowFilter;
			}
			return res;
		}

		public boolean isFilters() {
			return useFilter ? filterList.size() != list.size() : false;
		}
	}

	private final class MyListSelectionModel implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				updateAllActions();
			}
		}
	}

	private final class AllListMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && !allList.isSelectionEmpty()) {
				Object element = allListModel.getElementAt(allList.getSelectedIndex());

				int index = selectedList.getSelectedIndex();

				addObjects(new Object[] { element }, index);

				fireChangeListener();
			}
		}
	}

	private final class SelectedListMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && !selectedList.isSelectionEmpty()) {
				Object element = selectedListModel.getElementAt(selectedList.getSelectedIndex());
				removeObjects(new Object[] { element });

				fireChangeListener();
			}
		}
	}

	private final class ObjectTransferable implements Transferable {

		private Object[] os;

		public ObjectTransferable(Object[] os) {
			this.os = os;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return os;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return FLAVORS;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			String i = flavor.getParameter("listToList");
			return flavor.equals(OBJECT_FLAVOR) && ((i == null && identifier == null) || (i != null && identifier != null && i.equals(identifier)));
		}
	}

	private final class MyTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 840082758308468379L;

		@Override
		protected Transferable createTransferable(JComponent c) {
			JList list = (JList) c;
			if (!list.getSelectionModel().isSelectionEmpty()) {
				return new ObjectTransferable(list.getSelectedValues());
			}
			return null;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		@Override
		public boolean canImport(TransferSupport support) {
			Transferable transferable = support.getTransferable();
			if (transferable != null) {
				if (transferable.isDataFlavorSupported(OBJECT_FLAVOR)) {
					try {
						Object[] os = (Object[]) transferable.getTransferData(OBJECT_FLAVOR);

						JList list = (JList) support.getComponent();
						MyListModel model = (MyListModel) list.getModel();

						boolean ok = false;
						int i = 0;
						while (i < os.length && !ok) {
							Object o = os[i];
							ok = model.contains(o);
							i++;
						}

						if ((comparator == null && allList != list && (ok && os.length == 1)) || !ok) {
							DropLocation dl = support.getDropLocation();
							if (dl != null) {
								JList.DropLocation dropLocation = (JList.DropLocation) dl;
								if (dropLocation.getIndex() != -1) {
									return true;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return false;
		}

		@Override
		public boolean importData(TransferSupport support) {
			if (canImport(support)) {
				try {
					Transferable transferable = support.getTransferable();

					Object[] os = (Object[]) transferable.getTransferData(OBJECT_FLAVOR);

					DropLocation dl = support.getDropLocation();
					JList.DropLocation dropLocation = (JList.DropLocation) dl;

					JList list = (JList) support.getComponent();
					MyListModel model = (MyListModel) list.getModel();

					boolean ok = false;
					int i = 0;
					while (i < os.length && !ok) {
						Object o = os[i];
						ok = model.contains(o);
						i++;
					}

					if (comparator != null) {
						if (list == allList) {
							removeObjects(os);
						} else {
							addObjects(os);
						}
					} else {
						if (ok) {
							int index = selectedListModel.convertRowFilterToRowModel(dropLocation.getIndex());
							moveObject(index, os[0]);
						} else {
							if (list == selectedList) {
								int index = selectedListModel.convertRowFilterToRowModel(dropLocation.getIndex());
								addObjects(os, index);
							} else {
								removeObjects(os);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}
}
