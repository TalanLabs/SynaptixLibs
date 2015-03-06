package com.synaptix.widget.perimeter.view.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.StaticImage;

public abstract class AbstractListPerimeterWidget<E> extends AbstractPerimeterWidget {

	private static final long serialVersionUID = -1018312750879846662L;

	private static final Icon ADD_ICON = StaticImage.getImageScale(new ImageIcon(AbstractListPerimeterWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconAdd.png")), 10); //$NON-NLS-1$

	private static final Icon DELETE_ICON = StaticImage.getImageScale(new ImageIcon(AbstractListPerimeterWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconDelete.png")), 10); //$NON-NLS-1$

	private static final Icon CLEAR_ICON = StaticImage.getImageScale(new ImageIcon(AbstractListPerimeterWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconRemove.png")), 10); //$NON-NLS-1$

	protected Action addAction;

	protected Action editAction;

	protected Action deleteAction;

	protected Action clearAction;

	protected MyListModel listModel;

	protected JList list;

	protected JLabel nbLabel;

	protected String title;

	protected boolean editable;

	private JScrollPane scrollPane;

	public AbstractListPerimeterWidget(String title) {
		this(title, false);
	}

	public AbstractListPerimeterWidget(String title, boolean editable) {
		super();

		this.title = title;
		this.editable = editable;

		initActions();
		initComponents();
	}

	protected void initialize() {
		this.addContent(buildContents());
	}

	private void initActions() {
		addAction = new AddAction();
		editAction = new EditAction();
		editAction.setEnabled(false);
		deleteAction = new DeleteAction();
		deleteAction.setEnabled(false);
		clearAction = new ClearAction();
		clearAction.setEnabled(false);
	}

	private void createComponents() {
		listModel = new MyListModel();
		list = new JList(listModel);

		nbLabel = new JLabel("", JLabel.RIGHT); //$NON-NLS-1$
	}

	private void initComponents() {
		createComponents();

		nbLabel.setFont(new Font("Arial", Font.ITALIC, 10));

		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		list.getSelectionModel().addListSelectionListener(new MyListSelectionListener());
		list.setCellRenderer(new ObjectListCellRenderer());
		list.addMouseListener(new MyMouseListener());
		listModel.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						scrollPane.getViewport().setViewPosition(new Point(0, scrollPane.getViewport().getViewSize().height));
					}
				});
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
			}
		});

		updateView();
	}

	protected Action[] buildActions() {
		if (editable) {
			return new Action[] { addAction, editAction, null, deleteAction, clearAction };
		} else {
			return new Action[] { addAction, null, deleteAction, clearAction };
		}
	}

	private JComponent buildContents() {
		JComponent inputComponent = getInputComponent();
		FormLayout layout = null;
		if (inputComponent != null) {
			layout = new FormLayout("RIGHT:MAX(20DLU;DEFAULT):NONE,4DLU,FILL:51DLU:GROW(1.0)", //$NON-NLS-1$
					"CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,FILL:40DLU:GROW(1.0),CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		} else {
			layout = new FormLayout("RIGHT:MAX(20DLU;DEFAULT):NONE,4DLU,FILL:51DLU:GROW(1.0)", //$NON-NLS-1$
					"CENTER:DEFAULT:NONE,FILL:40DLU:GROW(1.0),CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		}
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int row = 1;
		if (inputComponent != null) {
			if (getDescription() != null) {
				builder.add(new JLabel(getDescription()), cc.xy(1, row));
				builder.add(inputComponent, cc.xy(3, row++));
			} else {
				builder.add(inputComponent, cc.xyw(1, row++, 3));
			}
		}
		builder.add(ToolBarFactory.buildToolBar(buildActions()), cc.xyw(1, row++, 3));
		scrollPane = new JScrollPane(list);
		builder.add(scrollPane, cc.xyw(1, row++, 3));
		builder.add(nbLabel, cc.xyw(1, row++, 3));
		return builder.getPanel();
	}

	/**
	 * Overwrite to define a description when used wuth an input component ({@link #getInputComponent()})
	 * 
	 * @return
	 */
	protected String getDescription() {
		return null;
	}

	/**
	 * Component which can help to fill in text
	 * 
	 * @return
	 */
	protected JComponent getInputComponent() {
		return null;
	}

	public void setShowLabel(boolean showLabel) {
		nbLabel.setVisible(showLabel);
	}

	public boolean isShowLabel() {
		return nbLabel.isVisible();
	}

	@Override
	public List<E> getValue() {
		List<E> e = listModel.getObjectList();
		return e != null && !e.isEmpty() ? new ArrayList<E>(e) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		if (value != null) {
			if (!Collection.class.isAssignableFrom(value.getClass())) {
				value = Arrays.<E> asList((E) value);
			}
		}
		setValue((List<E>) value);
	}

	public void setValue(List<E> values) {
		list.clearSelection();
		listModel.clear();

		addObjects(values);
	}

	@Override
	public String getTitle() {
		return title;
	}

	protected abstract String toObjectString(E o);

	protected abstract void add(IResultCallback<List<E>> resultCallback);

	private void updateView() {
		List<E> oList = listModel.getObjectList();
		int size = oList != null && !oList.isEmpty() ? oList.size() : 0;
		int s = list.getSelectedIndices().length;

		clearAction.setEnabled(size > 0);

		nbLabel.setText(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().selectedItems(size, s));

		fireTitleChanged();
	}

	protected void addObjects(List<E> value) {
		if (value != null && !value.isEmpty()) {
			list.clearSelection();
			for (E o : value) {
				if (o != null) {
					listModel.add(o);
				}
			}
		}
		updateView();

		fireValuesChanged();
	}

	private void edit(final int idx) {
		edit(listModel.getObjectList().get(idx), new IResultCallback<E>() {
			@Override
			public void setResult(E value) {
				if (value != null) {
					listModel.set(idx, value);
					updateView();

					fireValuesChanged();
				}
			}
		});
	}

	protected void edit(E value, IResultCallback<E> resultCallback) {
	}

	private final class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && list.getSelectedValue() != null) {
				if (editable) {
					edit(list.getSelectedIndex());
				} else {
					deleteAction.actionPerformed(null);
				}
			}
		}
	}

	private final class AddAction extends AbstractAction {

		private static final long serialVersionUID = 8433301832170365910L;

		public AddAction() {
			super("", ADD_ICON); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().addEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			add(new IResultCallback<List<E>>() {
				@Override
				public void setResult(List<E> values) {
					if (values != null && !values.isEmpty()) {
						addObjects(values);
					}
				}
			});
		}
	}

	private final class EditAction extends AbstractEditAction {

		private static final long serialVersionUID = 6262346160868347650L;

		public EditAction() {
			super("");

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().editEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			edit(list.getSelectedIndex());
		}
	}

	private final class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = -1812514289272589074L;

		public DeleteAction() {
			super("", DELETE_ICON);

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().delete());
		}

		@Override
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			Object[] os = list.getSelectedValues();

			list.clearSelection();
			for (Object o : os) {
				listModel.delete((E) o);
			}
			updateView();

			fireValuesChanged();
		}
	}

	private final class ClearAction extends AbstractAction {

		private static final long serialVersionUID = 2116574410530992080L;

		public ClearAction() {
			super("", CLEAR_ICON); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().deleteAll());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			list.clearSelection();
			listModel.clear();
			updateView();

			fireValuesChanged();
		}
	}

	private final class MyListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				boolean selectionEmpty = list.getSelectionModel().isSelectionEmpty();
				if (!selectionEmpty) {
					deleteAction.setEnabled(true);
					editAction.setEnabled(list.getSelectionModel().getMinSelectionIndex() == list.getSelectionModel().getMaxSelectionIndex() && editable);
				} else {
					deleteAction.setEnabled(false);
					editAction.setEnabled(false);
				}
				updateView();
			}
		}
	}

	/**
	 * Return true if the given value can be found twice or more
	 * 
	 * @param value
	 * @return
	 */
	protected final boolean hasManyValue(E value) {
		return listModel.getObjectList().indexOf(value) != listModel.getObjectList().lastIndexOf(value);
	}

	/**
	 * Return true if the given value can be found at least once
	 * 
	 * @param value
	 * @return
	 */
	protected final boolean hasValue(E value) {
		return listModel.getObjectList().contains(value);
	}

	protected final class MyListModel extends AbstractListModel {

		private static final long serialVersionUID = 5528470324842374722L;

		private final List<E> objectList;

		public MyListModel() {
			objectList = new ArrayList<E>();
		}

		public void add(E o) {
			if (!objectList.contains(o)) {
				objectList.add(o);
				this.fireIntervalAdded(this, objectList.size() - 1, objectList.size());
			}
		}

		public void set(int index, E o) {
			objectList.set(index, o);
		}

		public void delete(E o) {
			int i = objectList.indexOf(o);
			objectList.remove(o);

			this.fireIntervalRemoved(this, i, i + 1);
		}

		public void clear() {
			int size = objectList.size();
			objectList.clear();

			this.fireIntervalRemoved(this, 0, size);
		}

		public List<E> getObjectList() {
			return objectList;
		}

		@Override
		public Object getElementAt(int index) {
			return objectList.get(index);
		}

		@Override
		public int getSize() {
			return objectList.size();
		}
	}

	private final class ObjectListCellRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 7388584552867300961L;

		public ObjectListCellRenderer() {
			super("", JLabel.LEFT);
			this.setOpaque(true);
		}

		@Override
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (value != null) {
				E o = (E) value;
				this.setText(toObjectString(o));
			} else {
				this.setText(null);
			}
			return this;
		}
	}
}
