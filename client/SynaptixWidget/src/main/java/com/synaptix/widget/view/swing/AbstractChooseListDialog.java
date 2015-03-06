package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.JListToList;
import com.synaptix.swing.WaitGlassPaneSwingWorker;
import com.synaptix.view.swing.error.ErrorViewManager;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.dialog.IChooseListDialog;

public abstract class AbstractChooseListDialog<E> extends JPanel implements IChooseListDialog<E> {

	private static final long serialVersionUID = 8542499211229848574L;

	private JDialogModel dialog;

	private JListToList<E> listToList;

	private Action acceptAction;

	private Action cancelAction;

	private int returnValue;

	private List<E> valueList;

	private String title;

	public AbstractChooseListDialog(String title) {
		super(new BorderLayout());

		this.title = title;

		initActions();
		initComponents();

		dialog = null;
		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		acceptAction.setEnabled(false);
		cancelAction = new CancelAction();
	}

	private void createComponents() {
		listToList = new JListToList<E>();
	}

	private void initComponents() {
		createComponents();

		listToList.getAllList().setCellRenderer(newListCellRenderer());
		listToList.getSelectionList().setCellRenderer(newListCellRenderer());
		listToList.addChangeListener(new MyChangeListener());

		installInputMap();
	}

	protected abstract ListCellRenderer newListCellRenderer();

	private void installInputMap() {
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doAccept"); //$NON-NLS-1$
		this.getActionMap().put("doAccept", acceptAction); //$NON-NLS-1$

		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "doCancel"); //$NON-NLS-1$
		this.getActionMap().put("doCancel", cancelAction); //$NON-NLS-1$
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:250DLU:GROW(1.0)", //$NON-NLS-1$
				"p"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(listToList, cc.xy(1, 1));
		return builder.getPanel();
	}

	@Override
	public int showDialog(Component parent) {
		valueList = null;

		dialog = new JDialogModel(parent, title, null, this, new Action[] { acceptAction, cancelAction }, new OpenActionListener(), cancelAction);

		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	@Override
	public List<E> getValueList() {
		return valueList;
	}

	protected abstract List<E> loadAllValueList() throws Exception;

	private final class OpenActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			new WaitGlassPaneSwingWorker<LoadResult>(true) {

				@Override
				protected LoadResult doInBackground() throws Exception {
					publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().loading());

					LoadResult res = new LoadResult();
					res.valueList = loadAllValueList();

					publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().finished());
					return res;
				}

				@Override
				protected void done() {
					try {
						LoadResult res = get();

						if (res.valueList != null) {
							listToList.setAllValues(res.valueList.toArray());
						}
					} catch (Exception e) {
						ErrorViewManager.getInstance().showErrorDialog(null, e);
					}
				}
			}.execute();
		}

		private final class LoadResult {

			List<E> valueList;

		}
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = 9184100540105793420L;

		@Override
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;

			Object[] os = listToList.getSelectionValues();
			valueList = (List<E>) Arrays.asList(os);

			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = -1165636071113981103L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			dialog.closeDialog();
		}
	}

	private final class MyChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			Object[] os = listToList.getSelectionValues();
			acceptAction.setEnabled(os != null && os.length > 0);
		}
	}
}
