package com.synaptix.widget.perimeter.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.searchfield.context.ISearchFieldWidgetContext;
import com.synaptix.widget.searchfield.view.swing.DefaultSearchFieldWidget;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.StaticImage;

/**
 * A default perimeter which includes a suggest widget, a list of components and some buttons to clear
 * 
 * @author Nicolas P
 * 
 * @param <E>
 */
public class DefaultSuggestListPerimeterWidget<E extends IComponent> extends AbstractPerimeterWidget {

	private static final long serialVersionUID = -7517757966458102873L;

	private static final Icon CLEAR_ALL_ICON = StaticImage.getImageScale(
			new ImageIcon(AbstractStringPasteListPerimeterWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconRemove.png")), 10); //$NON-NLS-1$

	private static final Icon CLEAR_ICON = StaticImage.getImageScale(
			new ImageIcon(AbstractStringPasteListPerimeterWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconDelete.png")), 10); //$NON-NLS-1$

	private static final Icon ADD_MANY_ICON = StaticImage.getImageScale(new ImageIcon(AbstractStringPasteListPerimeterWidget.class.getResource("/images/plusIcon.png")), 10); //$NON-NLS-1$

	private final Class<E> componentClass;

	private final ISearchFieldWidgetContext<E> searchFieldWidgetContext;

	private final GenericObjectToString<E> objectToString;

	private final boolean editable;

	private final String promptText;

	private final String title;

	private final boolean forceUppercase;

	private DefaultSearchFieldWidget<E> searchFieldWidget;

	private Action clearAllAction;

	private Action addManyAction;

	private CustomList customTable;

	protected JLabel nbLabel;

	public DefaultSuggestListPerimeterWidget(String title, Class<E> componentClass, ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString, boolean editable,
			String promptText) {
		this(title, componentClass, searchFieldWidgetContext, objectToString, editable, promptText, false);
	}

	public DefaultSuggestListPerimeterWidget(String title, Class<E> componentClass, ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString, boolean editable,
			String promptText, boolean forceUppercase) {
		super();

		this.title = title;
		this.componentClass = componentClass;
		this.searchFieldWidgetContext = searchFieldWidgetContext;
		this.objectToString = objectToString;
		this.editable = editable;
		this.promptText = promptText;
		this.forceUppercase = forceUppercase;

		initComponents();

		this.addContent(buildContents());

		customTable.refresh();
	}

	@Override
	public final void addContent(Component content) {
		super.addContent(content); // final method, do not delete
	}

	private Component buildContents() {
		FormLayout layout = new FormLayout("RIGHT:MAX(20DLU;DEFAULT):NONE,4DLU,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:60DLU:NONE,FILL:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		if (getDescription() != null) {
			builder.add(new JLabel(getDescription()), cc.xy(1, 1));
			builder.add(searchFieldWidget, cc.xy(3, 1));
		} else {
			builder.add(searchFieldWidget, cc.xyw(1, 1, 3));
		}
		builder.add(ToolBarFactory.buildToolBar(buildActions()), cc.xyw(1, 2, 3));
		builder.add(new JScrollPane(customTable), cc.xyw(1, 3, 3));
		builder.add(nbLabel, cc.xyw(1, 4, 3));
		return builder.getPanel();
	}

	/**
	 * Overwrite to define a description
	 * 
	 * @return
	 */
	protected String getDescription() {
		return null;
	}

	private final void initComponents() {
		searchFieldWidget = new DefaultSearchFieldWidget<E>(searchFieldWidgetContext, objectToString, editable, promptText, forceUppercase) {

			private static final long serialVersionUID = -2053753470777876137L;

			@Override
			protected void search(String text) {
				searchFieldWidgetContext.searchMany(text != null && !text.isEmpty() ? text : null, new IResultCallback<List<E>>() {
					@Override
					public void setResult(List<E> e) {
						if (CollectionHelper.isNotEmpty(e)) {
							customTable.setValueIsAdjusting(true);
							for (E e2 : e) {
								customTable.addElement(e2);
							}
							customTable.setValueIsAdjusting(false);
						}
					}
				});
			}
		};
		searchFieldWidget.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (searchFieldWidget.getValue() != null) {
					addElement(searchFieldWidget.getValue());
					searchFieldWidget.setValue(null);
				}
			}
		});

		customTable = new CustomList();
		customTable.init();

		nbLabel = new JLabel("", JLabel.RIGHT); //$NON-NLS-1$
		nbLabel.setFont(new Font("Arial", Font.ITALIC, 10));
	}

	protected Action[] buildActions() {
		clearAllAction = new ClearAllAction();
		clearAllAction.setEnabled(false);

		addManyAction = new AddManyAction();
		addManyAction.setEnabled(true);

		return new Action[] { clearAllAction };
	}

	public void setShowLabel(boolean showLabel) {
		nbLabel.setVisible(showLabel);
	}

	public boolean isShowLabel() {
		return nbLabel.isVisible();
	}

	public final void addElement(E component) {
		customTable.addElement(component);

		fireValuesChanged();
	}

	@Override
	public List<E> getValue() {
		if (customTable.getComponentList().size() == 0) {
			return null;
		}
		return new ArrayList<E>(customTable.getComponentList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		customTable.setValueIsAdjusting(true);
		customTable.clear();
		if (value != null) {
			if (List.class.isAssignableFrom(value.getClass())) {
				List<?> list = (List<?>) value;
				for (Object obj : list) {
					if ((obj != null) && (componentClass.isAssignableFrom(obj.getClass()))) {
						customTable.addElement((E) obj);
					}
				}
			}
		}
		customTable.setValueIsAdjusting(false);
	}

	@Override
	public final String getTitle() {
		return title;
	}

	private final class CustomList extends JTable {

		private static final long serialVersionUID = -353867028159780525L;

		private final List<E> componentList;

		private boolean valueIsAdjusting;

		public CustomList() {
			super();

			this.componentList = new ArrayList<E>();
		}

		public final void init() {
			setModel(new MyTableModel());

			initTable();
		}

		public void setValueIsAdjusting(boolean valueIsAdjusting) {
			this.valueIsAdjusting = valueIsAdjusting;

			refresh();
		}

		private final void refresh() {
			if (!valueIsAdjusting) {
				getTableModel().fireTableDataChanged();

				nbLabel.setText(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().selectedItems(componentList.size(), customTable.getSelectedRowCount()));

				fireValuesChanged();
			}
		}

		@SuppressWarnings("unchecked")
		private MyTableModel getTableModel() {
			return (MyTableModel) getModel();
		}

		private final void initTable() {
			setShowGrid(false);
			setShowHorizontalLines(false);
			setShowVerticalLines(false);
			setCellSelectionEnabled(true);
			setColumnSelectionAllowed(false);
			setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			getColumnModel().getColumn(1).setMinWidth(20);
			getColumnModel().getColumn(1).setMaxWidth(20);
			getColumnModel().getColumn(1).setWidth(20);
			getTableHeader().setVisible(false);
			getTableHeader().setPreferredSize(new Dimension(0, 0));
			getColumnModel().getColumn(1).setCellEditor(new MyTableCellEditor());

			getColumnModel().getColumn(0).setCellRenderer(new TypeGenericSubstanceTableCellRenderer<E>(objectToString));

			setPreferredScrollableViewportSize(new Dimension(0, 0));

			getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						customTable.refresh();
					}
				}
			});
		}

		public final void addElement(E component) {
			int idx = componentList.indexOf(component);
			if (idx < 0) {
				componentList.add(component);
				clearAllAction.setEnabled(true);

				refresh();

				getSelectionModel().setAnchorSelectionIndex(componentList.size());
			} else {
				getSelectionModel().setAnchorSelectionIndex(idx);
			}
		}

		public final void removeElement(int elementIndex) {
			componentList.remove(elementIndex);

			refresh();

			if (elementIndex > 0) {
				getSelectionModel().setAnchorSelectionIndex(elementIndex - 1);
			}
			if (componentList.size() == 0) {
				clearAllAction.setEnabled(false);
			}
		}

		public final void clear() {
			componentList.clear();

			refresh();
		}

		public List<E> getComponentList() {
			return componentList;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 1) {
				return true;
			}
			return false;
		}
	}

	private final class MyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 7509786684358595570L;

		@Override
		public int getRowCount() {
			return customTable.getComponentList().size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				return String.class;
			}
			return Icon.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return customTable.getComponentList().get(rowIndex);
			}
			return CLEAR_ICON;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			super.setValueAt(aValue, rowIndex, columnIndex);
			if (columnIndex == 1) {
				customTable.removeElement(rowIndex);
				customTable.refresh();
			}
		}
	}

	private final class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

		private static final long serialVersionUID = -9212812179624540025L;

		private final JButton button;

		public MyTableCellEditor() {
			super();

			this.button = new JButton(CLEAR_ICON);
			this.button.setContentAreaFilled(false);
			this.button.setBorderPainted(false);
			this.button.addActionListener(new AbstractAction() {

				private static final long serialVersionUID = 6656531620288351768L;

				@Override
				public void actionPerformed(ActionEvent e) {
					stopCellEditing();
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			return button;
		}
	}

	private final class ClearAllAction extends AbstractAction {

		private static final long serialVersionUID = -8927709355448471161L;

		public ClearAllAction() {
			super("", CLEAR_ALL_ICON); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().clearAll());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			clearAllAction.setEnabled(false);
			customTable.clear();
		}
	}

	private final class AddManyAction extends AbstractAction {

		private static final long serialVersionUID = -8927709355448471161L;

		public AddManyAction() {
			super("", ADD_MANY_ICON); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().addMany());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			searchFieldWidgetContext.searchMany(searchFieldWidget.getText(), new IResultCallback<List<E>>() {

				@Override
				public void setResult(List<E> e) {
					if (CollectionHelper.isNotEmpty(e)) {
						customTable.setValueIsAdjusting(true);
						for (E entity : e) {
							customTable.addElement(entity);
						}
						customTable.setValueIsAdjusting(false);
					}
				}
			});
		}
	}
}
