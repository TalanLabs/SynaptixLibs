package com.synaptix.widget.view.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.view.swing.WaitFullComponentSwingWaitWorker;
import com.synaptix.view.swing.error.ErrorViewManager;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

public abstract class AbstractChooseDialog<E> extends WaitComponentFeedbackPanel {

	private static final long serialVersionUID = 8542499211229848574L;

	public static final int ACCEPT_OPTION = 0;

	public static final int CANCEL_OPTION = 1;

	private JDialogModel dialog;

	private JComboBox comboBox;

	private Action acceptAction;

	private Action cancelAction;

	private int returnValue;

	private E value;

	private String title;

	private boolean selectFirst;

	public AbstractChooseDialog(String title) {
		this(title, false);
	}

	public AbstractChooseDialog(String title, boolean selectFirst) {
		super();

		this.title = title;
		this.selectFirst = selectFirst;

		initActions();
		initComponents();

		dialog = null;
		this.addContent(buildContents());
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		acceptAction.setEnabled(false);
		cancelAction = new CancelAction();
	}

	private void createComponents() {
		comboBox = createComboBox();
	}

	private void initComponents() {
		createComponents();

		ListCellRenderer lcr = newListCellRenderer();
		if (lcr != null) {
			comboBox.setRenderer(lcr);
		}
		comboBox.addActionListener(new MyActionListener());

		installInputMap();
	}

	protected JComboBox createComboBox() {
		return new JComboBox();
	}

	protected abstract ListCellRenderer newListCellRenderer();

	private void installInputMap() {
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doAccept"); //$NON-NLS-1$
		this.getActionMap().put("doAccept", acceptAction); //$NON-NLS-1$

		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "doCancel"); //$NON-NLS-1$
		this.getActionMap().put("doCancel", cancelAction); //$NON-NLS-1$
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("right:max(40dlu;pref), 4dlu, FILL:150DLU:GROW(1.0)", //$NON-NLS-1$
				"p"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addLabel(title + "*", cc.xy(1, 1)); //$NON-NLS-1$
		builder.add(comboBox, cc.xy(3, 1));
		return builder.getPanel();
	}

	public int showDialog(Component parent) {
		return showDialog(parent, null);
	}

	public int showDialog(Component parent, E value) {
		this.value = value;

		dialog = new JDialogModel(parent, title, null, this, new Action[] { acceptAction, cancelAction }, new OpenActionListener(), cancelAction);

		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	public E getValue() {
		return value;
	}

	protected abstract List<E> loadAllValueList() throws Exception;

	private final class OpenActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			WaitFullComponentSwingWaitWorker.waitFullComponentSwingWaitWorker(AbstractChooseDialog.this, new AbstractLoadingViewWorker<LoadResult>() {

				protected LoadResult doLoading() throws Exception {
					LoadResult res = new LoadResult();
					res.valueList = loadAllValueList();
					return res;
				}

				public void success(LoadResult res) {
					comboBox.setModel(new DefaultComboBoxModel(res.valueList.toArray()));
					if (value != null && res.valueList.contains(value)) {
						comboBox.setSelectedItem(value);
					} else if (selectFirst && !res.valueList.isEmpty()) {
						comboBox.setSelectedIndex(0);
					} else {
						comboBox.setSelectedItem(null);
					}
				}

				public void fail(Throwable t) {
					ErrorViewManager.getInstance().showErrorDialog(null, t);
				}
			});
		}

		private final class LoadResult {

			List<E> valueList;

		}
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = 9184100540105793420L;

		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;

			value = (E) comboBox.getSelectedItem();

			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = -1165636071113981103L;

		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			dialog.closeDialog();
		}
	}

	private final class MyActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			acceptAction.setEnabled(comboBox.getSelectedItem() != null);
		}
	}
}
