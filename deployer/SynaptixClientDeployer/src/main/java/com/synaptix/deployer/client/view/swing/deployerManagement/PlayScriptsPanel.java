package com.synaptix.deployer.client.view.swing.deployerManagement;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.deployerManagement.PlayScriptsNode;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.swing.helper.DBHelper;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.swing.IconFeedbackComponentValidationResultPanel;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceListCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.IValidationView;
import com.synaptix.widget.view.swing.ValidationListener;

public class PlayScriptsPanel extends AbstractManagementPanel<PlayScriptsNode> implements IValidationView {

	private static final long serialVersionUID = -604848509763947228L;

	private static final Comparator<Integer> INTEGER_COMPARATOR = new Comparator<Integer>() {

		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
	};

	private JButton browseButton;

	private JButton removeSelectedButton;

	private JList list;

	private JButton nextStepButton;

	private JComboBox dbComboBox;

	private JPasswordField passwordField;

	private ValidationListener validationListener;

	private MyListModel listModel;

	public PlayScriptsPanel(PlayScriptsNode node, DeployerManagementController managementController) {
		super(node, managementController);
	}

	@Override
	protected void initComponents() {
		listModel = new MyListModel();
		list = new JList(listModel);
		list.setDragEnabled(true);
		list.setDropMode(DropMode.INSERT);

		list.setTransferHandler(new MyListDropHandler());

		new MyDragListener();

		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				removeSelectedButton.setEnabled(list.getSelectedIndex() > -1);
			}
		});
		list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setCellRenderer(new TypeGenericSubstanceListCellRenderer<File>(new GenericObjectToString<File>() {

			@Override
			public String getString(File t) {
				return t.getName();
			}
		}));

		browseButton = new JButton(StaticHelper.getDeployerManagementConstantsBundle().browse());
		browseButton.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = -8967887940773251714L;

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = getManagementController().browse();
				List<File> fileList = scanDirectoryForFiles(file);
				listModel.setFiles(fileList);
			}
		});
		removeSelectedButton = new JButton(StaticHelper.getDeployerManagementConstantsBundle().removeSelection());
		removeSelectedButton.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = -960409619017432560L;

			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.removeSelection(list.getSelectedIndices());
			}
		});

		nextStepButton = new JButton(StaticHelper.getDeployerManagementConstantsBundle().nextStep());
		nextStepButton.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = -6679798746079480745L;

			@Override
			public void actionPerformed(ActionEvent e) {
				getManagementController().selectScripts(getNode());
			}
		});
		updateNextButton();

		dbComboBox = DBHelper.buildDBComboBox(getManagementController().getSupportedDbsForEnvironment(getManagementController().getEnvironment()));

		passwordField = new JPasswordField();

		this.validationListener = new ValidationListener(this);

		dbComboBox.addActionListener(validationListener);
		passwordField.getDocument().addDocumentListener(validationListener);
		list.addListSelectionListener(validationListener);

		MyRefreshInfoListener refreshInfoListener = new MyRefreshInfoListener();
		dbComboBox.addActionListener(refreshInfoListener);
		passwordField.getDocument().addDocumentListener(refreshInfoListener);
		listModel.addListDataListener(refreshInfoListener);

		updateValidation();
	}

	protected List<File> scanDirectoryForFiles(File file) {
		List<File> fileList = new ArrayList<File>();
		if (file != null) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						fileList.addAll(scanDirectoryForFiles(f));
					} else {
						fileList.add(f);
					}
				}
			}
		}
		return fileList;
	}

	@Override
	protected JComponent buildContent() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:MAX(60DLU;DEFAULT):NONE,5DLU,FILL:DEFAULT:NONE,3DLU,FILL:MAX(60DLU;DEFAULT):NONE,FILL:DEFAULT:GROW(0.5),3DLU,FILL:DEFAULT:NONE");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		builder.append(browseButton);
		builder.nextColumn(7);
		builder.append(removeSelectedButton);
		builder.nextLine();
		builder.appendRelatedComponentsGapRow();
		builder.nextLine();
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().databaseSchema(), dbComboBox);
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().password(), passwordField);
		builder.appendRelatedComponentsGapRow();
		builder.nextLine();
		builder.appendRow("FILL:DEFAULT:GROW(1.0)");
		builder.nextLine();
		builder.append(new JSyScrollPane(list), 10);
		builder.nextLine();
		// builder.appendRelatedComponentsGapRow();
		builder.append(nextStepButton, 10);
		builder.appendRow("FILL:20DLU:NONE");

		return new IconFeedbackComponentValidationResultPanel(validationResultModel, builder.getPanel());
	}

	@Override
	public void updateValidation() {
		ValidationResult result = new ValidationResult();

		if (dbComboBox.getSelectedItem() == null) {
			result.add(new SimpleValidationMessage(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().mandatoryField(), Severity.ERROR, dbComboBox));
		}
		if (passwordField.getPassword().length == 0) {
			result.add(new SimpleValidationMessage(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().mandatoryField(), Severity.ERROR, passwordField));
		}
		if (listModel.getFileList() == null) {
			result.add(new SimpleValidationMessage(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().mandatoryField(), Severity.ERROR, list));
		}
		nextStepButton.setEnabled(!result.hasErrors());
		validationResultModel.setResult(result);
	}

	@Override
	public void setRunning(boolean running) {
		super.setRunning(running);

		nextStepButton.setEnabled(!running);
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);

		updateNextButton();
	}

	private void updateNextButton() {
		if ((getManagementController().getInstructions() != null) && (getManagementController().getInstructions().downloadInstanceSet.isEmpty())) {
			nextStepButton.setText(StaticHelper.getDeployerManagementConstantsBundle().run());
		} else {
			nextStepButton.setText(StaticHelper.getDeployerManagementConstantsBundle().nextStep());
		}
	}

	private final class MyListModel extends AbstractListModel {

		private static final long serialVersionUID = -2178624038879864050L;

		private final List<File> fileList;

		public MyListModel() {
			super();

			this.fileList = new ArrayList<File>();
		}

		public List<File> getFileList() {
			return fileList;
		}

		public void removeSelection(int[] selectedIndices) {
			if (selectedIndices != null) {
				List<Integer> indiceList = new ArrayList<Integer>(selectedIndices.length);
				for (Integer a : selectedIndices) {
					indiceList.add(a);
				}
				Collections.sort(indiceList, INTEGER_COMPARATOR);
				for (int i = indiceList.size() - 1; i >= 0; i--) {
					fileList.remove((int) indiceList.get(i));
				}
				fireContentsChanged(this, indiceList.get(0), indiceList.get(indiceList.size() - 1));
			}
			PlayScriptsPanel.this.list.clearSelection();
		}

		public void setFiles(List<File> fileList) {
			fireIntervalRemoved(list, 0, this.fileList.size());
			this.fileList.clear();
			if (fileList != null) {
				this.fileList.addAll(fileList);
			}
			fireContentsChanged(this, 0, this.fileList.size());
		}

		@Override
		public Object getElementAt(int index) {
			return fileList.get(index);
		}

		@Override
		public int getSize() {
			return fileList.size();
		}

		public void switchPositions(int idx1, int idx2) {
			File f1 = fileList.get(idx1);
			File f2 = fileList.get(idx2);
			fileList.set(idx1, f2);
			fileList.set(idx2, f1);
			fireContentsChanged(this, Math.min(idx1, idx2), Math.max(idx1, idx2));
		}
	}

	private final class MyDragListener implements DragSourceListener, DragGestureListener {

		DragSource ds = new DragSource();

		public MyDragListener() {
			ds.createDefaultDragGestureRecognizer(list, DnDConstants.ACTION_MOVE, this);
		}

		@Override
		public void dragGestureRecognized(DragGestureEvent dge) {
			StringSelection transferable = new StringSelection(Integer.toString(list.getSelectedIndex()));
			ds.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
		}

		@Override
		public void dragEnter(DragSourceDragEvent dsde) {
		}

		@Override
		public void dragExit(DragSourceEvent dse) {
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde) {
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
		}

		@Override
		public void dropActionChanged(DragSourceDragEvent dsde) {
		}
	}

	private final class MyListDropHandler extends TransferHandler {

		private static final long serialVersionUID = -3887386928058318196L;

		@Override
		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return false;
			}
			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
			if (dl.getIndex() == -1) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport support) {
			if (!canImport(support)) {
				return false;
			}

			Transferable transferable = support.getTransferable();
			String indexString;
			try {
				indexString = (String) transferable.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception e) {
				return false;
			}

			int index = Integer.parseInt(indexString);
			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
			int dropTargetIndex = dl.getIndex();

			listModel.switchPositions(index, index < dropTargetIndex ? dropTargetIndex - 1 : dropTargetIndex);
			return true;
		}
	}

	private final class MyRefreshInfoListener implements DocumentListener, ActionListener, ListDataListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshInfo();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			refreshInfo();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			refreshInfo();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			refreshInfo();
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			refreshInfo();
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			refreshInfo();
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			refreshInfo();
		}

		private void refreshInfo() {
			getManagementController().setScripts(passwordField.getPassword(), dbComboBox.getSelectedItem() != null ? (ISynaptixDatabaseSchema) dbComboBox.getSelectedItem() : null,
					listModel.getFileList());
		}
	}
}
