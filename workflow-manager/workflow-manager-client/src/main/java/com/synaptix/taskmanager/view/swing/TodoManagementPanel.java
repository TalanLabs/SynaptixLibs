package com.synaptix.taskmanager.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.WrapLayout;
import org.joda.time.LocalDateTime;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;
import org.pushingpixels.substance.api.renderers.SubstanceRenderer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IId;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.ButtonHelper;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.taskmanager.controller.AbstractTodosManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.ITodo;
import com.synaptix.taskmanager.model.ITodoFolderListView;
import com.synaptix.taskmanager.model.domains.TodoStatus;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.widget.actions.view.swing.AbstractRefreshAction;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.renderer.view.swing.IValueComponent;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;

public class TodoManagementPanel extends WaitComponentFeedbackPanel implements com.synaptix.taskmanager.view.ITodoManagementView, IDockable, IDockingContextView, IRibbonContextView {

	public static final String ID_DOCKABLE = TodoManagementPanel.class.getName();

	private static final RibbonData RIBBON_DATA = new RibbonData(com.synaptix.taskmanager.util.StaticHelper.getTaskManagerConstantsBundle().todosManagement(), StaticTaskManagerHelper
			.getTaskManagerModuleConstantsBundle()
			.taskManager(), 1, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().todo(), 1, "taskManager", ImageWrapperResizableIcon.getIcon(
			TodoManagementPanel.class.getResource("/taskManager/images/todo.png"), new Dimension(30, 30)));
	private static final Icon CHECKED_BOX = ImageWrapperResizableIcon.getIcon(TodoManagementPanel.class.getResource("/taskManager/images/checked_checkbox.png"), new Dimension(16, 16));
	private static final Icon UNCHECKED_BOX = ImageWrapperResizableIcon.getIcon(TodoManagementPanel.class.getResource("/taskManager/images/unchecked_checkbox.png"), new Dimension(16, 16));
	private static final Icon CLOCK = ImageWrapperResizableIcon.getIcon(TodoManagementPanel.class.getResource("/taskManager/images/clock.png"), new Dimension(16, 16));

	private static final long serialVersionUID = 5878072650235240758L;
	private static Color normalPanelBackground = new Color(224, 224, 224);
	LinkedHashMap<Serializable, List<TodoListItem>> currentTodosMap;
	private DockKey dockKey;
	private SyDockingContext dockingContext;
	private JList foldersList;
	private JList todosJList;
	private String category;
	private AbstractTodosManagementController abstractTodosManagementController;
	private ComponentModel<ITodoFolderListView> foldersListModel;

	// private List<ITodo> todoList;
	private Action refreshAction;
	private JPanel detailPanel;
	private ITodoFolderListView selectedFolder;
	private TodoListModel todosListModel;
	private JCheckBox lastTodosFirstCheckBox;
	private JButton clearDoneTasksButton;
	private JXTextField searchField;
	private JButton searchButton;

	/**
	 * Todos as selected when the page is displayed for the first time, or reloaded. We need this list to know which todos are done and deleted from the database.
	 */
	private List<ITodo> currentTodoList;

	/**
	 * Contains all todos currently in the database, for the displayed objects. It does not contain todos which are already done (they are deleted from database).
	 */
	private List<ITodo> updatedTodoList;

	public TodoManagementPanel(AbstractTodosManagementController abstractTodosManagementController) {
		super();
		this.abstractTodosManagementController = abstractTodosManagementController;

		initComponents();

		this.addContent(buildContent());
	}

	private static String getDescriptionFromTodo(ITodo todo) {
		IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(todo.getObjectType());
		return objectType.getTodoMeaning(todo.getCode());
	}

	private void initComponents() {
		foldersListModel = new ComponentModel<ITodoFolderListView>();

		foldersList = new JList(foldersListModel);
		foldersList.setCellRenderer(new SubstanceDefaultListCellRenderer() {
			private static final long serialVersionUID = 4562225984802287430L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				JLabel label = (JLabel) component;
				ITodoFolderListView folder = (ITodoFolderListView) value;
				if (value != null) {
					String meaning = folder.getMeaning() != null ? folder.getMeaning() : com.synaptix.taskmanager.util.StaticHelper.getTodoFoldersTableConstantsBundle().withoutFolder();
					label.setText(meaning + " (" + folder.getTodoNumber() + ")");
				}
				return component;
			}
		});
		foldersList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (foldersList.getSelectedIndex() > -1) {
						ITodoFolderListView todoFolder = foldersListModel.getElementAt(foldersList.getSelectedIndex());
						setNewSelectedTodoFolder(todoFolder);
					}
				}
			}
		});
		foldersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		refreshAction = new AbstractRefreshAction() {

			private static final long serialVersionUID = -7567294953386083194L;

			@Override
			public void actionPerformed(ActionEvent e) {
				abstractTodosManagementController.loadTodoFoldersList();
			}
		};

		detailPanel = new JPanel();

		searchField = new JXTextField(com.synaptix.taskmanager.util.StaticHelper.getTodoTableConstantsBundle().search());
		searchField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					abstractTodosManagementController.reloadPage();
				}
			}
		});
		searchButton = new JButton(new AbstractAction(com.synaptix.taskmanager.util.StaticHelper.getTodosManagementConstantsBundle().filter()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				abstractTodosManagementController.reloadPage();
			}
		});
		lastTodosFirstCheckBox = new JCheckBox(new AbstractAction(com.synaptix.taskmanager.util.StaticHelper.getTodosManagementConstantsBundle().lastTodosFirst()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				abstractTodosManagementController.reloadPage();
			}
		});

		clearDoneTasksButton = new JButton(new AbstractAction(com.synaptix.taskmanager.util.StaticHelper.getTodosManagementConstantsBundle().clearDoneTask()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				abstractTodosManagementController.reloadPage();
			}
		});
		ButtonHelper.installButtonChanger(searchButton);
		ButtonHelper.installButtonChanger(lastTodosFirstCheckBox);
		ButtonHelper.installButtonChanger(clearDoneTasksButton);
		clearDoneTasksButton.setVisible(false);

		todosListModel = new TodoListModel();

		todosJList = new JList(todosListModel) {
			private static final long serialVersionUID = -6193854323864213741L;

			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
		};
		todosJList.setFixedCellHeight(-1);
		todosJList.setCellRenderer(new TodoRenderer());
		todosJList.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		todosJList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					fillDetailsPanel(getSelectedTodosListItems());
				}
			}
		});
		todosJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		todosJList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					int index = list.locationToIndex(evt.getPoint());
					ITodo todo = todosListModel.getElementAt(index).getTodo();
					List<com.synaptix.taskmanager.urimanager.uriaction.ITodoAction> actions = abstractTodosManagementController.getUriTodoActions(Collections.singletonList(todo));
					if (CollectionUtils.isNotEmpty(actions)) {
						actions.get(0).execute(Collections.singletonList(todo), TodoManagementPanel.this);
					}
				}
			}
		});

		final ListSelectionListener listSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] oldSelectedIndices = todosJList.getSelectedIndices();
				int[] selectedIndices = new int[0];
				for (int selectedIndice : oldSelectedIndices) {
					Object element = todosJList.getModel().getElementAt(selectedIndice);
					if (element instanceof TodoListItem) {
						TodoListItem todoListItem = (TodoListItem) element;
						if ((oldSelectedIndices.length == 1 && todoListItem.isTitle()) || (!todoListItem.isTitle() && todoListItem.getStatus() == EnumTodoElementStatus.TODO)) {
							selectedIndices = ArrayUtils.add(selectedIndices, selectedIndice);
						}
					}
				}

				if (oldSelectedIndices.length != selectedIndices.length) {
					todosJList.setSelectedIndices(selectedIndices);
				}
			}
		};
		todosJList.addListSelectionListener(listSelectionListener);
		ComponentListener l = new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				recomputeListItemHeight();
			}

		};

		todosJList.addComponentListener(l);
	}

	private void recomputeListItemHeight() {
		int h = -1;
		for (int i = 0; i < todosJList.getModel().getSize(); i++) {
			Object object = todosJList.getModel().getElementAt(i);
			Component c = todosJList.getCellRenderer().getListCellRendererComponent(todosJList, object, i, false, false);
			c.invalidate();
			c.repaint();
			h = Math.max(h, c.getMinimumSize().height);
		}
		// force cache invalidation by temporarily setting fixed height
		todosJList.setFixedCellHeight(-1);
		todosJList.setFixedCellHeight(h);
	}

	private void setNewSelectedTodoFolder(ITodoFolderListView todoFolder) {
		selectedFolder = todoFolder;

		currentTodoList = null;

		abstractTodosManagementController.setSelectedTodoFolder(todoFolder);
	}

	@Override
	public void updateDetailsPanel() {
		fillDetailsPanel(getSelectedTodosListItems());
	}

	private List<TodoListItem> getSelectedTodosListItems() {
		List<TodoListItem> todos = new ArrayList<TodoListItem>();
		for (Object object : todosJList.getSelectedValues()) {
			if (object != null) {
				if (object instanceof TodoListItem) {
					TodoListItem todoListItem = (TodoListItem) object;
					todos.add(todoListItem);
				}
			}
		}
		return todos;
	}

	private void fillDetailsPanel(List<TodoListItem> list) {
		detailPanel.removeAll();
		if (CollectionUtils.isNotEmpty(list)) {
			if (list.size() == 1 && list.get(0).isTitle()) {
				detailPanel.setLayout(new BorderLayout());
				JTextArea textArea = new JTextArea(list.get(0).getTodo().getDescription());
				textArea.setLineWrap(true);
				detailPanel.add(textArea);
			} else {
				buildDetailsContent(list);
			}
		}
		detailPanel.revalidate();
		detailPanel.repaint();
	}

	private List<Action> buildActions() {
		List<com.synaptix.taskmanager.urimanager.uriaction.ITodoAction> uriActions = abstractTodosManagementController.getUriTodoActions(getSelectedTodos());
		ArrayList<Action> actions = new ArrayList<Action>();
		for (final com.synaptix.taskmanager.urimanager.uriaction.ITodoAction uriAction : uriActions) {
			AbstractAction action = new AbstractAction(uriAction.getText()) {

				private static final long serialVersionUID = 6246795304254096238L;

				@Override
				public void actionPerformed(ActionEvent e) {
					uriAction.execute(getSelectedTodos(), TodoManagementPanel.this);
				}
			};
			actions.add(action);
		}

		return actions;
	}

	protected List<ITodo> getSelectedTodos() {
		List<ITodo> todos = new ArrayList<ITodo>();
		for (Object object : todosJList.getSelectedValues()) {
			if (object != null) {
				if (object instanceof TodoListItem) {
					TodoListItem todoListItem = (TodoListItem) object;
					if (!todoListItem.isTitle()) {
						todos.add(todoListItem.getTodo());
					}
				}
			}
		}
		return todos;
	}

	@Override
	public void updateTodos(List<ITodo> todoList, List<IId> idObjects) {
		if (todoList == null) {
			return;
		}

		// Fill updated todo list
		if (currentTodosMap == null || idObjects == null) {
			currentTodosMap = new LinkedHashMap<Serializable, List<TodoListItem>>();
		} else {
			for (ListIterator<ITodo> iterator = updatedTodoList.listIterator(); iterator.hasNext();) {
				ITodo todo = iterator.next();
				if (idObjects.contains(todo.getIdObject())) {
					iterator.remove();
				}
			}
			updatedTodoList.addAll(todoList);
		}

		// Change buttons display: if todo list has changed since last refresh, switch to edit mode.
		if (!updatedTodoList.equals(currentTodoList)) {
			setButtonsToEditMode(true);
		}

		// Fill map
		for (ITodo todo : todoList) {
			IId idObject = todo.getIdObject();
			List<TodoListItem> todosForObject;
			if (currentTodosMap.containsKey(idObject)) {
				todosForObject = currentTodosMap.get(idObject);
			} else {
				todosForObject = new ArrayList<TodoListItem>();
				currentTodosMap.put(idObject, todosForObject);
			}
			TodoListItem todoListItem = new TodoListItem(todo, false);
			if (todosForObject.contains(todoListItem)) {
				todosForObject.remove(todoListItem);
			}
			todosForObject.add(todoListItem);
		}

		// Change status of completed tasks
		for (List<TodoListItem> todoListItems : currentTodosMap.values()) {
			for (TodoListItem todoListItem : todoListItems) {
				if (!updatedTodoList.contains(todoListItem.getTodo())) {
					todoListItem.setStatus(EnumTodoElementStatus.DONE);
				}
			}
		}

		// Create items list (add a title item for each object)
		ArrayList<TodoListItem> newTodoList = new ArrayList<TodoListItem>();
		for (Serializable object : currentTodosMap.keySet()) {
			List<TodoListItem> todoListItems = currentTodosMap.get(object);

			sortTodoListItems(todoListItems);

			ITodo lastTodo = todoListItems.get(todoListItems.size() - 1).getTodo();
			newTodoList.add(new TodoListItem(lastTodo, true));

			for (TodoListItem todoListItem : todoListItems) {
				newTodoList.add(todoListItem);
			}
		}

		this.todosJList.getSelectionModel().clearSelection();
		this.todosListModel.setList(newTodoList);

		// Update selected todo
		updateSelectedTodo(idObjects);

		recomputeListItemHeight();
	}

	private void updateSelectedTodo(List<IId> idObjects) {
		if (idObjects != null) {
			IId idObject = idObjects.get(0);
			TodoListItem itemToSelect = null;

			List<Serializable> keys = new ArrayList<Serializable>(currentTodosMap.keySet());
			for (int i = keys.indexOf(idObject); i < keys.size(); i++) {
				List<TodoListItem> todoListItems = currentTodosMap.get(keys.get(i));
				for (TodoListItem todoListItem : todoListItems) {
					if (!todoListItem.isTitle() && todoListItem.getStatus() == EnumTodoElementStatus.TODO) {
						itemToSelect = todoListItem;
						break;
					}
				}
				if (itemToSelect != null) {
					break;
				}
			}
			if (itemToSelect == null) {
				for (int i = 0; i < keys.indexOf(idObject); i++) {
					List<TodoListItem> todoListItems = currentTodosMap.get(keys.get(i));
					for (TodoListItem todoListItem : todoListItems) {
						if (!todoListItem.isTitle() && todoListItem.getStatus() == EnumTodoElementStatus.TODO) {
							itemToSelect = todoListItem;
							break;
						}
					}
					if (itemToSelect != null) {
						break;
					}
				}
			}

			this.todosJList.setSelectedValue(itemToSelect, true);
		}
	}

	private void setButtonsToEditMode(boolean editMode) {
		searchButton.setVisible(!editMode);
		searchField.setVisible(!editMode);
		lastTodosFirstCheckBox.setVisible(!editMode);
		clearDoneTasksButton.setVisible(editMode);
	}

	private void sortTodoListItems(List<TodoListItem> todoListItems) {
		Collections.sort(todoListItems, new Comparator<TodoListItem>() {
			@Override
			public int compare(TodoListItem o1, TodoListItem o2) {
				if (o1.getStatus().getValue() < o2.getStatus().getValue()) {
					return -1;
				}
				if (o1.getStatus().getValue() > o2.getStatus().getValue()) {
					return 1;
				}
				if (o1.getTodo().getCreatedDate().after(o2.getTodo().getCreatedDate())) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	@Override
	public void setTodos(List<ITodo> todoList) {
		if (todoList == null) {
			return;
		}

		setButtonsToEditMode(false);

		// Fill map
		this.currentTodosMap = new LinkedHashMap<Serializable, List<TodoListItem>>();
		for (ITodo todo : todoList) {
			IId idObject = todo.getIdObject();
			List<TodoListItem> todosForObject;
			if (currentTodosMap.containsKey(idObject)) {
				todosForObject = currentTodosMap.get(idObject);
			} else {
				todosForObject = new ArrayList<TodoListItem>();
				currentTodosMap.put(idObject, todosForObject);
			}

			todosForObject.add(new TodoListItem(todo, false));
		}

		// Create items list (add a title item for each object)
		ArrayList<TodoListItem> newTodoList = new ArrayList<TodoListItem>();
		for (Serializable object : currentTodosMap.keySet()) {
			List<TodoListItem> todoListItems = currentTodosMap.get(object);
			sortTodoListItems(todoListItems);
			ITodo lastTodo = todoListItems.get(todoListItems.size() - 1).getTodo();
			newTodoList.add(new TodoListItem(lastTodo, true));

			for (TodoListItem todoListItem : todoListItems) {
				newTodoList.add(todoListItem);
			}
		}

		this.currentTodoList = new ArrayList<ITodo>(todoList);
		this.updatedTodoList = new ArrayList<ITodo>(todoList);
		this.todosJList.getSelectionModel().clearSelection();
		this.todosListModel.setList(newTodoList);

		recomputeListItemHeight();
	}

	public JComponent buildContent() {
		FormLayout layout = new FormLayout("FILL:120DLU:NONE,3DLU,fill:DEFAULT:GROW(1.0)", "fill:default:none,3dlu,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		builder.add(ToolBarFactory.buildToolBar(refreshAction), cc.xy(1, 1));

		builder.add(new JSyScrollPane(foldersList), cc.xy(1, 3));

		JSyScrollPane scrollPane = new JSyScrollPane(todosJList);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		JSyScrollPane detailScrollPane = new JSyScrollPane(detailPanel);
		detailScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		detailScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildPaginationScreen(scrollPane), detailScrollPane);
		splitPane.setResizeWeight(0.5);

		builder.add(splitPane, cc.xywh(3, 1, 1, 3));

		return builder.getPanel();
	}

	private Component buildPaginationScreen(JSyScrollPane scrollPane) {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder panelBuilder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		panelBuilder.add(buildPaginationBar(), cc.xy(1, 1));
		panelBuilder.add(scrollPane, cc.xy(1, 3));
		return panelBuilder.getPanel();
	}

	private Component buildPaginationBar() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:50DLU:NONE,3DLU,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(0.5),FILL:DEFAULT:NONE", "CENTER:PREF:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(searchField, cc.xy(3, 1));
		builder.add(searchButton, cc.xy(5, 1));
		builder.add(lastTodosFirstCheckBox, cc.xy(7, 1));
		builder.add(clearDoneTasksButton, cc.xy(1, 1));
		JPanel panel = builder.getPanel();
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setBackground(normalPanelBackground);
		return panel;
	}

	public void buildDetailsContent(List<TodoListItem> list) {
		if (list == null || list.isEmpty()) {
			return;
		}

		FormLayout layout = new FormLayout("left:default:NONE,3DLU,fill:default:grow");
		final DefaultFormBuilder builder = new DefaultFormBuilder(layout, detailPanel);
		builder.setDefaultDialogBorder();
		builder.appendSeparator("Actions");
		builder.nextLine();
		List<Action> actions = buildActions();
		Action[] arrayActions = new Action[actions.size()];
		actions.toArray(arrayActions);

		JComponent toolBar = ToolBarFactory.buildToolBar(arrayActions);
		toolBar.setLayout(new WrapLayout(FlowLayout.LEFT));
		builder.append(toolBar, 3);
		toolBar.revalidate();
		toolBar.repaint();

		builder.nextLine();
		builder.appendSeparator(com.synaptix.taskmanager.util.StaticHelper.getTodosManagementConstantsBundle().todoDetails());
		builder.nextLine();
		if (list.size() == 1) {
			// Build object details
			TodoListItem todoListItem = list.get(0);
			ITodo todo = todoListItem.getTodo();
			builder.append(com.synaptix.taskmanager.util.StaticHelper.getTodoTableConstantsBundle().objectType(), new JLabel(TaskObjectClassViewHelper.getObjectType(todo.getObjectType())));
			String descriptionMeaning = getDescriptionFromTodo(todo);
			builder.append(com.synaptix.taskmanager.util.StaticHelper.getTodoTableConstantsBundle().description(), new JLabel(descriptionMeaning));
			builder.append(com.synaptix.taskmanager.util.StaticHelper.getTodoTableConstantsBundle().createdDate(),
					new JLabel(new LocalDateTime(todo.getCreatedDate()).toString(StaticCommonHelper.getDateConstantsBundle().displayDateTimeFormat())));
			String meaning = todo.getTask().getTaskType().getMeaning();
			if (meaning == null) {
				meaning = todo.getTask().getTaskType().getServiceCode();
			}
			builder.append(com.synaptix.taskmanager.util.StaticHelper.getTodoTableConstantsBundle().task_taskType_meaning(), new JLabel(meaning));

			builder.appendSeparator(com.synaptix.taskmanager.util.StaticHelper.getTodosManagementConstantsBundle().objectDetails());

			abstractTodosManagementController.getUriDetailsPanel(todo, this, new IResultCallback<Component>() {
				@Override
				public void setResult(final Component panel) {
					builder.append(panel, 3);
				}
			});
		}
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
	public void initializeDockingContext(SyDockingContext context) {

		this.dockingContext = context;

		dockKey = new DockKey(ID_DOCKABLE, com.synaptix.taskmanager.util.StaticHelper.getTaskManagerConstantsBundle().todosManagement());
		dockKey.setFloatEnabled(true);
		dockKey.setAutoHideEnabled(false);

		context.registerDockable(this);

		context.addDockableStateChangeListener(this, new DockableStateChangeListener() {
			@Override
			public void dockableStateChanged(DockableStateChangeEvent event) {
				if (event.getPreviousState() == null || event.getPreviousState().isClosed()) {
					abstractTodosManagementController.loadTodoFoldersList();
				}
			}
		});
	}

	@Override
	public void initializeRibbonContext(RibbonContext ribbonContext) {
		JCommandButton cb = new JCommandButton(RIBBON_DATA.getTitle(), RIBBON_DATA.getIcon());
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dockingContext.showDockable(ID_DOCKABLE);
			}
		});

		String ribbonTaskTitle = RIBBON_DATA.getRibbonTaskTitle();
		String ribbonBandTitle = RIBBON_DATA.getRibbonBandTitle();
		ribbonContext.addRibbonTask(ribbonTaskTitle, RIBBON_DATA.getRibbonTaskPriority());

		ribbonContext.addRibbonTask(ribbonTaskTitle).addRibbonBand(ribbonBandTitle).addCommandeButton(cb, RIBBON_DATA.getPriority());

		category = RIBBON_DATA.getCategory();
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public void setTodoFoldersList(List<ITodoFolderListView> folderList, int total, boolean reloadSelectedFolder) {

		ArrayList<ITodoFolderListView> newFoldersList = new ArrayList<ITodoFolderListView>();

		ITodoFolderListView allTodosFolder = ComponentFactory.getInstance().createInstance(ITodoFolderListView.class);
		allTodosFolder.setMeaning(com.synaptix.taskmanager.util.StaticHelper.getTodoFoldersTableConstantsBundle().allTodos());
		allTodosFolder.setTotal(true);
		allTodosFolder.setTodoNumber(total);

		newFoldersList.add(allTodosFolder);
		newFoldersList.addAll(folderList);
		foldersListModel.setList(newFoldersList);

		if (selectedFolder != null) {
			foldersList.setSelectedValue(selectedFolder, true);
		}
		if (foldersList.getSelectedValue() == null) {
			ITodoFolderListView firstFolder = newFoldersList.get(0);
			if (firstFolder != null) {
				foldersList.setSelectedValue(firstFolder, true);
			}
		}
		if (reloadSelectedFolder) {
			setNewSelectedTodoFolder((ITodoFolderListView) foldersList.getSelectedValue());
		}
	}

	@Override
	public boolean isLastTodosFirst() {
		return lastTodosFirstCheckBox.isSelected();
	}

	@Override
	public String getSearchText() {
		return searchField.getText();
	}

	private enum EnumTodoElementStatus {
		TODO(1), PENDING(2), DONE(0);

		private int value;

		EnumTodoElementStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	@SubstanceRenderer
	private final class TodoRenderer extends DefaultListCellRenderer implements IValueComponent<TodoListItem> {

		private static final long serialVersionUID = -2474769735021864105L;

		private final Color selectedBackColor = new Color(210, 235, 252);
		private final Color deselectedBackColor = new Color(232, 237, 240);

		public TodoRenderer() {
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			if (value instanceof TodoListItem) {
				TodoListItem todoListItem = (TodoListItem) value;

				if (todoListItem.isTitle()) {
					String string = todoListItem.getTodo().getDescription();

					panel.add(new JLabel(string), BorderLayout.WEST);
					panel.setBackground(deselectedBackColor);
					panel.setEnabled(false);
				} else {
					ITodo todo = todoListItem.getTodo();
					String description = getDescriptionFromTodo(todo);

					String date = null;
					if (todo.getCreatedDate() != null) {
						date = new LocalDateTime(todo.getCreatedDate()).toString(StaticCommonHelper.getDateConstantsBundle().displayDateTimeFormat());
					}

					Icon icon = null;
					Font font = new JLabel().getFont();
					if (EnumTodoElementStatus.DONE == todoListItem.getStatus()) {
						icon = CHECKED_BOX;
						description = "<html><strike>" + description + "</strike></html>";
					} else if (EnumTodoElementStatus.PENDING == todoListItem.getStatus()) {
						icon = CLOCK;
						font = font.deriveFont(Font.ITALIC);
					} else if (EnumTodoElementStatus.TODO == todoListItem.getStatus()) {
						icon = UNCHECKED_BOX;
					}

					boolean enabled = EnumTodoElementStatus.TODO == todoListItem.getStatus();

					JLabel descriptionLabel = new JLabel(description);
					descriptionLabel.setEnabled(enabled);
					descriptionLabel.setIcon(icon);
					descriptionLabel.setFont(font);
					panel.add(descriptionLabel, BorderLayout.WEST);

					JLabel dateLabel = new JLabel(date);
					dateLabel.setEnabled(enabled);
					dateLabel.setFont(font);
					panel.add(dateLabel, BorderLayout.EAST);
				}
			}

			if (isSelected) {
				panel.setBackground(selectedBackColor);
			}

			return panel;
		}

		@Override
		public Component getComponent() {
			return this;
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension preferredSize = super.getPreferredSize();
			return new Dimension(preferredSize.width, Math.max(todosJList.getFixedCellHeight(), preferredSize.height));
		}

		@Override
		public void setValue(JLabel label, TodoListItem value) {
		}

	}

	private final class ComponentModel<T extends IComponent> extends AbstractListModel {
		private static final long serialVersionUID = 7030606250438181459L;

		private List<T> myComponentsList;

		@Override
		public int getSize() {
			return CollectionHelper.size(myComponentsList);
		}

		@Override
		public T getElementAt(int index) {
			if (index >= 0 && index < myComponentsList.size()) {
				return myComponentsList.get(index);
			}
			return null;
		}

		public void setList(List<T> componentsList) {
			this.myComponentsList = componentsList;
			fireContentsChanged(this, 0, CollectionHelper.size(this.myComponentsList));
		}
	}

	// TODO use component and @EqualsKey
	private final class TodoListItem {
		private ITodo todo;
		private EnumTodoElementStatus status;
		private boolean isTitle;

		public TodoListItem(ITodo todo, boolean isTitle) {
			this.todo = todo;
			this.isTitle = isTitle;

			if (TodoStatus.TODO == todo.getStatus()) {
				this.status = EnumTodoElementStatus.TODO;
			} else if (TodoStatus.PENDING == todo.getStatus()) {
				this.status = EnumTodoElementStatus.PENDING;
			}
		}

		public EnumTodoElementStatus getStatus() {
			return status;
		}

		public void setStatus(EnumTodoElementStatus status) {
			this.status = status;
		}

		public boolean isTitle() {
			return isTitle;
		}

		public ITodo getTodo() {
			return todo;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof TodoListItem) {
				TodoListItem todoListItem = (TodoListItem) obj;
				if (this.isTitle() != todoListItem.isTitle()) {
					return false;
				}

				if (this.getTodo() != null && todoListItem.getTodo() != null) {
					if (this.getTodo().getId() != null && this.getTodo().getId().equals(todoListItem.getTodo().getId())) {
						return true;
					}
				} else {
					return true;
				}
			}
			return false;
		}
	}

	private class TodoListModel extends AbstractListModel {
		private List<TodoListItem> todosList;

		@Override
		public int getSize() {
			return CollectionHelper.size(todosList);
		}

		@Override
		public TodoListItem getElementAt(int index) {
			if (index >= 0 && index < todosList.size()) {
				return todosList.get(index);
			}
			return null;
		}

		public void setList(List<TodoListItem> todosList) {
			this.todosList = todosList;
		}
	}
}
