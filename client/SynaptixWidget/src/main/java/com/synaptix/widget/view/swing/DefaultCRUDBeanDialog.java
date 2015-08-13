package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.IView;
import com.synaptix.entity.IEntity;
import com.synaptix.widget.actions.view.swing.AbstractNextAction;
import com.synaptix.widget.actions.view.swing.AbstractPreviousAction;
import com.synaptix.widget.actions.view.swing.AbstractSaveAction;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;
import com.synaptix.widget.view.swing.helper.ToolBarActionsBuilder;

/**
 * A default CRUD bean dialog which allows the user to browse between the entities<br/>
 * It also has a toolbar on the top
 *
 * @author Nicolas P
 *
 * @param <E>
 */
public class DefaultCRUDBeanDialog<E extends IEntity> extends DefaultBeanDialog<E> implements ICRUDBeanDialogView<E> {

	private static final long serialVersionUID = 2124940929569388267L;

	private JButton previousButton;

	private JButton nextButton;

	private JButton saveButton;

	private JPanel toolbarPanel;

	private ICRUDDialogController<E> crudDialogContext;

	public DefaultCRUDBeanDialog(IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		this(true, beanExtensionDialogs);
	}

	public DefaultCRUDBeanDialog(boolean hideListIfAlone, IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		this(hideListIfAlone, false, null, null, null, beanExtensionDialogs);
	}

	public DefaultCRUDBeanDialog(boolean hideListIfAlone, boolean acceptActionEnabled, String acceptActionLabel, String cancelActionLabel, String closeActionLabel,
			IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		super(hideListIfAlone, acceptActionEnabled, acceptActionLabel, cancelActionLabel, closeActionLabel, beanExtensionDialogs);

		createActions();

		buildComponents();
	}

	private void createActions() {
		previousButton = new JButton(new AbstractPreviousAction() {

			private static final long serialVersionUID = -7370218300879933030L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (crudDialogContext != null) {
					closeDialog();
					crudDialogContext.showPrevious(bean);
				}
			}
		});
		nextButton = new JButton(new AbstractNextAction() {

			private static final long serialVersionUID = 6187188536969754544L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (crudDialogContext != null) {
					closeDialog();
					crudDialogContext.showNext(bean);
				}
			}
		});
		saveButton = new JButton(new AbstractSaveAction() {

			private static final long serialVersionUID = -1089345142735425536L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (crudDialogContext != null) {
					accept();
					crudDialogContext.saveBean();
				}
			}
		});
	}

	private void buildComponents() {
		toolbarPanel = new JPanel(new BorderLayout());
	}

	@Override
	protected JComponent buildAllPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		FormLayout fl = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:GROW(1.0)");
		PanelBuilder pb = new PanelBuilder(fl);
		pb.setDefaultDialogBorder();
		pb.add(toolbarPanel, cc.xy(1, 1));
		builder.add(pb.getPanel(), cc.xy(1, 1));
		builder.add(super.buildAllPanel(), cc.xy(1, 3));
		return builder.getPanel();
	}

	@Override
	protected Component buildSimplePanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(toolbarPanel, cc.xy(1, 1));
		builder.add(super.buildSimplePanel(), cc.xy(1, 3));
		return builder.getPanel();
	}

	private JComponent buildToolbar() {
		ToolBarActionsBuilder toolbarFactory = new ToolBarActionsBuilder();
		if (crudDialogContext != null) {
			toolbarFactory.addComponent(previousButton);
			toolbarFactory.addComponent(nextButton);
			toolbarFactory.addSeparator();
			toolbarFactory.addComponent(saveButton);
		}

		// if (getOthersActions().length != 0) {
		// for (Action a : getOthersActions()) {
		// toolbarFactory.addAction(a);
		// a.setEnabled(false);
		// }
		// }
		return toolbarFactory.build();
	}

	public void setReadOnly(boolean readOnly) {
		saveButton.setEnabled(!readOnly);
	}

	@Override
	public void setCRUDDialogContext(ICRUDDialogController<E> crudDialogContext) {
		this.crudDialogContext = crudDialogContext;
	}

	@Override
	protected boolean closeOnAccept() {
		return crudDialogContext == null || bean.getId() == null;
	}

	@Override
	protected boolean showCloseOnly() {
		return crudDialogContext != null && bean.getId() != null;
	}

	@Override
	public int showDialog(IView parent, String title, String subtitle, E bean, Map<String, Object> valueMap, boolean readOnly, boolean creation, boolean acceptAndReopenOption) {
		setReadOnly(readOnly);
		previousButton.setEnabled(crudDialogContext != null && crudDialogContext.hasPrevious(bean));
		nextButton.setEnabled(crudDialogContext != null && crudDialogContext.hasNext(bean));
		saveButton.setEnabled(crudDialogContext != null && crudDialogContext.hasAuthWrite());

		toolbarPanel.removeAll();
		JComponent toolbar = buildToolbar();
		if (toolbar != null) {
			toolbarPanel.add(toolbar, BorderLayout.CENTER);
		}

		getAcceptAction().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("enabled".equals(evt.getPropertyName())) { //$NON-NLS-1$
					saveButton.setEnabled(crudDialogContext != null && (Boolean) evt.getNewValue());
				}
			}
		});

		return super.showDialog(parent, title, subtitle, bean, valueMap, readOnly, creation, acceptAndReopenOption);
	}

	private void setSelectedTab(int selectedTabItem) {
		list.setSelectedIndex(selectedTabItem);
		Rectangle cellBounds = list.getCellBounds(selectedTabItem, selectedTabItem);
		listScrollPane.scrollToIfNecessary(cellBounds.y, cellBounds.height + 30);
	}

	@Override
	protected void fillListModel() {
		super.fillListModel();

		if (crudDialogContext != null) {
			setSelectedTab(crudDialogContext.getSelectedTabItem());
		}

		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (crudDialogContext != null) {
					crudDialogContext.setSelectedTabItem(list.getSelectedIndex());
				}
			}
		});
	}
}
