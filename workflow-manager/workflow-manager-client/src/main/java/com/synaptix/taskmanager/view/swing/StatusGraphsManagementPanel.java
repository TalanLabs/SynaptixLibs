package com.synaptix.taskmanager.view.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.PopupMenuMouseListener;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.taskmanager.controller.StatusGraphsManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.StatusGraphFields;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.IStatusGraphsManagementView;
import com.synaptix.taskmanager.view.swing.statusGraph.JStatusGraphGraphComponent;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.actions.view.swing.AbstractCloneAction;
import com.synaptix.widget.actions.view.swing.AbstractDeleteAction;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;
import com.vlsolutions.swing.docking.DockKey;

public class StatusGraphsManagementPanel extends WaitComponentFeedbackPanel implements IStatusGraphsManagementView, IDockable, IDockingContextView, IRibbonContextView {

	private static final long serialVersionUID = -1211111972398859087L;

	public static final String ID_DOCKABLE = StatusGraphsManagementPanel.class.getName();

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(StatusGraphFields.currentStatus(), StatusGraphFields.nextStatus(), StatusGraphFields.taskType().dot()
			.code());

	private final StatusGraphsManagementController statusGraphsManagementController;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private JComboBox taskObjectClassBox;

	private DefaultComponentTableModel<IStatusGraph> statusGraphDefaultComponentTableModel;

	private JSyTable statusGraphTable;

	private JPopupMenu popupMenu;

	private Action addAction;

	private Action cloneAction;

	private Action editAction;

	private Action deleteAction;

	private JStatusGraphGraphComponent graphComponent;

	public StatusGraphsManagementPanel(StatusGraphsManagementController statusGraphsManagementController) {
		super();

		this.statusGraphsManagementController = statusGraphsManagementController;

		initialize();

		this.addContent(buildContents());
	}

	private void initialize() {
		addAction = new AddAction();
		addAction.setEnabled(false);

		cloneAction = new CloneAction();
		cloneAction.setEnabled(false);

		editAction = new EditAction();
		editAction.setEnabled(false);

		deleteAction = new DeleteAction();
		deleteAction.setEnabled(false);

		popupMenu = new JPopupMenu();
		popupMenu.add(cloneAction);
		popupMenu.add(editAction);
		popupMenu.addSeparator();
		popupMenu.add(deleteAction);

		taskObjectClassBox = TaskObjectClassViewHelper.createTaskObjectClassComboBox(true);
		taskObjectClassBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		statusGraphDefaultComponentTableModel = new DefaultComponentTableModel<IStatusGraph>(IStatusGraph.class, StaticHelper.getStatusGraphTableConstantsBundle(), TABLE_COLUMNS);
		statusGraphTable = new JSyTable(statusGraphDefaultComponentTableModel, StatusGraphsManagementPanel.class.getName());

		statusGraphTable.getYColumnModel().getColumn(statusGraphDefaultComponentTableModel.findColumnIndexById(StatusGraphFields.currentStatus().name()), true)
				.setCellRenderer(new MyStatusSubstanceDefaultTableCellRenderer());
		statusGraphTable.getYColumnModel().getColumn(statusGraphDefaultComponentTableModel.findColumnIndexById(StatusGraphFields.nextStatus().name()), true)
				.setCellRenderer(new MyStatusSubstanceDefaultTableCellRenderer());

		statusGraphTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (statusGraphTable.getSelectedRowCount() == 1) {
						cloneAction.setEnabled(statusGraphsManagementController.hasAuthWrite());
						editAction.setEnabled(statusGraphsManagementController.hasAuthWrite());
						deleteAction.setEnabled(statusGraphsManagementController.hasAuthWrite());
					} else {
						cloneAction.setEnabled(false);
						editAction.setEnabled(false);
						deleteAction.setEnabled(false);
					}
				}
			}
		});
		statusGraphTable.addMouseListener(new PopupMenuMouseListener(popupMenu) {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && statusGraphTable.getSelectedRowCount() == 1) {
					editAction.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ""));
				}
			}
		});

		graphComponent = new JStatusGraphGraphComponent();
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:PREF:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(buildHeader(), cc.xy(1, 1));
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildUpContents(), buildDownContents());
		splitPane.setResizeWeight(0.75);
		builder.add(splitPane, cc.xy(1, 3));
		return builder.getPanel();
	}

	private JComponent buildHeader() {
		FormLayout layout = new FormLayout("right:max(40dlu;default), 4dlu, 150dlu", //$NON-NLS-1$
				"FILL:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
		CellConstraints cc = new CellConstraints();
		builder.addLabel(StaticHelper.getStatusGraphTableConstantsBundle().objectType(), cc.xy(1, 1));
		builder.add(taskObjectClassBox, cc.xy(3, 1));
		return builder.getPanel();
	}

	private JComponent buildUpContents() {
		FormLayout layout = new FormLayout("FILL:default:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:100DLU:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
		CellConstraints cc = new CellConstraints();
		builder.add(ToolBarFactory.buildToolBar(addAction, cloneAction, null, editAction, deleteAction), cc.xy(1, 1));
		builder.add(new JSyScrollPane(statusGraphTable), cc.xy(1, 3));
		return builder.getPanel();
	}

	private JComponent buildDownContents() {
		return new JScrollPane(graphComponent);
	}

	@Override
	public DockKey getDockKey() {
		return dockKey;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void initializeRibbonContext(RibbonContext ribbonContext) {
		JCommandButton cb = new JCommandButton(StaticHelper.getTaskManagerConstantsBundle().statusGraphsManagement());
		cb.setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_ONLY);
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dockingContext.showDockable(ID_DOCKABLE);
			}
		});

		ribbonContext.addRibbonTask(StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskManager(), 2)
				.addRibbonBand(StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().configuration(), 1).addCommandeButton(cb, RibbonElementPriority.MEDIUM);
	}

	@Override
	public void initializeDockingContext(SyDockingContext dockingContext) {
		this.dockingContext = dockingContext;

		dockKey = new DockKey(ID_DOCKABLE, StaticHelper.getTaskManagerConstantsBundle().statusGraphsManagement());
		dockingContext.registerDockable(this);
	}

	@Override
	public String getCategory() {
		return "taskManager";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh() {
		Class<? extends ITaskObject<?>> taskObjectClass = (Class<? extends ITaskObject<?>>) taskObjectClassBox.getSelectedItem();
		addAction.setEnabled(taskObjectClass != null && statusGraphsManagementController.hasAuthWrite());
		statusGraphsManagementController.loadStatusGraphs(taskObjectClass);
	}

	@Override
	public void setStatusGraphs(List<IStatusGraph> statusGraphs) {
		statusGraphDefaultComponentTableModel.setComponents(statusGraphs);
		graphComponent.setStatusGraphs(statusGraphs);
	}

	private IStatusGraph getSelectedComponent() {
		if (statusGraphTable.getSelectedRowCount() == 1) {
			return statusGraphDefaultComponentTableModel.getComponent(statusGraphTable.convertRowIndexToModel(statusGraphTable.getSelectedRow()));
		}
		return null;
	}

	private final class MyStatusSubstanceDefaultTableCellRenderer extends SubstanceDefaultTableCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			IStatusGraph statusGraph = statusGraphDefaultComponentTableModel.getComponent(table.convertRowIndexToModel(row));
			if (statusGraph != null) {
				if (value != null && statusGraph.getObjectType() != null) {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(statusGraph.getObjectType());
					setText(objectType, (String) value);
				} else {
					setText(StaticCommonHelper.getCommonConstantsBundle().none());
				}
			}

			return res;
		}

		private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> void setText(
				IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
			if (objectType != null) {
				setText(objectType.getStatusMeaning(Enum.valueOf(objectType.getStatusClass(), value)));
			} else {
				setText(value);
			}
		}
	}

	private class AddAction extends AbstractAddAction {

		private static final long serialVersionUID = -2209334623415724937L;

		@Override
		public void actionPerformed(ActionEvent e) {
			statusGraphsManagementController.addEntity();
		}
	}

	private class CloneAction extends AbstractCloneAction {

		private static final long serialVersionUID = 892920133520692619L;

		public CloneAction() {
			super(StaticCommonHelper.getCommonConstantsBundle().cloneEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			statusGraphsManagementController.cloneEntity(getSelectedComponent());
		}
	}

	private class EditAction extends AbstractEditAction {

		private static final long serialVersionUID = 892920133520692619L;

		public EditAction() {
			super(StaticCommonHelper.getCommonConstantsBundle().editEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (statusGraphsManagementController.hasAuthWrite()) {
				statusGraphsManagementController.editEntity(getSelectedComponent());
			} else {
				statusGraphsManagementController.showEntity(getSelectedComponent());
			}
		}
	}

	private class DeleteAction extends AbstractDeleteAction {

		private static final long serialVersionUID = -4405083037235887225L;

		public DeleteAction() {
			super(StaticCommonHelper.getCommonConstantsBundle().deleteEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			statusGraphsManagementController.deleteEntity(getSelectedComponent());
		}
	}
}
