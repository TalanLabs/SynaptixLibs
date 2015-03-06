package com.synaptix.widget.editorfield.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.swing.JRemoveLabel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;
import com.synaptix.widget.actions.view.swing.AbstractRemoveAction;
import com.synaptix.widget.editorfield.context.IEditorFieldWidgetContext;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.StaticImage;

public class DefaultEditorFieldWidget<E> extends WaitComponentFeedbackPanel implements IView {

	private static final long serialVersionUID = -5965080969005415936L;

	private final IEditorFieldWidgetContext<E> editorFieldWidgetContext;

	private final GenericObjectToString<E> genericObjectToString;

	private final boolean withRemoveLabel;

	private JTextField nameField;

	private Action editorButtonAction;

	private E value;

	private JButton button;

	private IResultCallback<E> editorCallback;

	private Action removeAction;

	private JRemoveLabel removeLabel;

	public DefaultEditorFieldWidget(IEditorFieldWidgetContext<E> editorFieldWidgetContext, GenericObjectToString<E> genericObjectToString) {
		super();

		this.editorFieldWidgetContext = editorFieldWidgetContext;
		this.genericObjectToString = genericObjectToString;
		this.withRemoveLabel = true;

		initComponents();

		this.addContent(buildContents());
	}

	private void initComponents() {
		editorButtonAction = new EditorButtonAction();

		nameField = new JTextField();
		nameField.setEditable(false);
		nameField.setFocusable(false);

		if (withRemoveLabel) {
			removeLabel = new JRemoveLabel();
			removeLabel.showErrorIcon(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().noneSelected());
			removeLabel.addActionListener(new RemoveActionListener());
		}

		button = new JButton(editorButtonAction);

		ImageIcon eraseIcon = StaticImage.getImageScale(new ImageIcon(AbstractRemoveAction.class.getResource("iconClearBig.png")), 16); //$NON-NLS-1$
		removeAction = new AbstractAction(null, eraseIcon) {

			private static final long serialVersionUID = 4102703144330237406L;

			@Override
			public void actionPerformed(ActionEvent e) {
				removeValue();
				fireChangeListener();
			}
		};
		removeAction.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().clear());
	}

	private JComponent buildContents() {
		FormLayout layout = null;
		if (withRemoveLabel) {
			layout = new FormLayout("FILL:80DLU:grow,FILL:2DLU:NONE,FILL:20DLU:NONE,FILL:26DLU:NONE", //$NON-NLS-1$
					"CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		} else {
			layout = new FormLayout("FILL:80DLU:grow,FILL:2DLU:NONE,FILL:20DLU:NONE", //$NON-NLS-1$
					"CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		}
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(nameField, cc.xy(1, 1));
		builder.add(button, cc.xy(3, 1));
		if (withRemoveLabel) {
			builder.add(removeLabel, cc.xy(4, 1));
		}
		return builder.getPanel();
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

	protected String valueToString(E value) {
		return genericObjectToString.getString(value);
	}

	public E getValue() {
		return value;
	}

	public void setValue(E value) {
		if (value != null) {
			this.value = value;

			nameField.setText(valueToString(value));
			nameField.setCaretPosition(0);

			updateRemoveLabel();
		} else {
			removeValue();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		removeLabel.setEnabled(enabled);
		button.setEnabled(enabled); // button or action?!

		super.setEnabled(enabled);

		updateRemoveLabel();
	}

	private void removeValue() {
		this.value = null;

		nameField.setText(null);

		updateRemoveLabel();
	}

	private void updateRemoveLabel() {
		if (withRemoveLabel) {
			if (value == null) {
				removeLabel.showErrorIcon(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().noneSelected());
				editorButtonAction.setEnabled(isEnabled());
			} else {
				removeLabel.showValidateIcon(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().doubleClickToClear());
				editorButtonAction.setEnabled(true); // button or action?! why?
			}
		} else {
			if (value == null || !isEnabled()) {
				button.setAction(editorButtonAction);
			} else {
				button.setAction(removeAction);
			}
		}
	}

	private final class RemoveActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			removeValue();
			fireChangeListener();
		}
	}

	protected IResultCallback<E> createEditorCallback() {
		return new IResultCallback<E>() {
			@Override
			public void setResult(E value) {
				if (editorCallback != null && editorCallback == this) {
					if (value != null) {
						setValue(value);
						fireChangeListener();
					}
				}
			}
		};
	}

	private void search() {
		editorCallback = createEditorCallback();
		editorFieldWidgetContext.editor(value, !isEnabled(), editorCallback);
	}

	private final class EditorButtonAction extends AbstractEditAction {

		private static final long serialVersionUID = -398767435122391168L;

		public EditorButtonAction() {
			super(null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			search();
		}
	}
}
