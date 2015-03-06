package com.synaptix.widget.perimeter.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.common.model.Binome;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractClearAction;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;
import com.synaptix.widget.util.StaticWidgetHelper;

public abstract class AbstractMinMaxPerimeterWidget<E> extends AbstractPerimeterWidget implements IPerimeterWidget {

	private static final long serialVersionUID = -3060347771746600611L;

	private static final Log LOG = LogFactory.getLog(AbstractMinMaxPerimeterWidget.class);

	private JFormattedTextField min;
	private JFormattedTextField max;

	private String title;

	private Action checkAction;
	private Action editAction;
	private Action clearAction;

	private Action validateAction;

	private boolean checked;

	private boolean useToolbar; // temporary?

	public AbstractMinMaxPerimeterWidget(String title) {
		super();

		this.title = title;
		this.checked = false;
		this.useToolbar = false;

		initActions();
		initComponents();

		this.addContent(buildContents());
	}

	private void initActions() {
		checkAction = new MyAcceptAction();
		editAction = new MyEditAction();
		clearAction = new MyClearAction();

		validateAction = new AbstractAction() {

			private static final long serialVersionUID = 3562897436887835620L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((!checked) && (checkAction.isEnabled())) {
					if (hasMinText()) {
						try {
							min.commitEdit();
						} catch (ParseException e1) {
							LOG.error("Parse exception", e1);
						}
					}
					if (hasMaxText()) {
						try {
							max.commitEdit();
						} catch (ParseException e1) {
							LOG.error("Parse exception", e1);
						}
					}
					validateInput();
				}
			}
		};

		checkAction.setEnabled(false);
		editAction.setEnabled(false);
		clearAction.setEnabled(false);
	}

	private final class MyAcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = 3562897436887835620L;

		public MyAcceptAction() {
			super("");

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().validate());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			validateInput();
		}
	}

	private final class MyEditAction extends AbstractEditAction {

		private static final long serialVersionUID = -8145277580939341016L;

		public MyEditAction() {
			super("");

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().edit());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			checked = false;
			Object minValue = min.getValue();
			Object maxValue = max.getValue();
			setValue(null);
			min.setValue(minValue);
			max.setValue(maxValue);
			min.setEnabled(true);
			max.setEnabled(true);
			checkAction.setEnabled(true);
			editAction.setEnabled(checked);
			clearAction.setEnabled(true);
		}
	}

	private final class MyClearAction extends AbstractClearAction {

		private static final long serialVersionUID = 8875431757733992903L;

		public MyClearAction() {
			super("");

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().clear());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			min.setValue(null);
			max.setValue(null);
			validateInput();
		}
	}

	protected abstract JFormattedTextField createFieldComponent();

	private void initComponents() {
		min = createFieldComponent();
		max = createFieldComponent();

		MyActionListener listener = new MyActionListener();
		min.addActionListener(listener);
		max.addActionListener(listener);
		min.getDocument().addDocumentListener(listener);
		max.getDocument().addDocumentListener(listener);
		min.addMouseListener(listener);
		max.addMouseListener(listener);
		min.addPropertyChangeListener("value", listener);
		max.addPropertyChangeListener("value", listener);
		min.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		min.getActionMap().put("enter", validateAction);
		max.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		max.getActionMap().put("enter", validateAction);
	}

	protected abstract JComponent buildContents();

	protected final JFormattedTextField getMinComponent() {
		return min;
	}

	protected final JFormattedTextField getMaxComponent() {
		return max;
	}

	protected final JComponent createToolbar() {
		this.useToolbar = true;
		return ToolBarFactory.buildToolBar(checkAction, null, editAction, null, clearAction);
	}

	@Override
	public String getTitle() {
		return title;
	}

	private void validateInput() {
		if (useToolbar) {
			if ((hasMinComplete()) || (hasMaxComplete())) {
				if (isCoherent()) {
					checked = true;
					checkAction.setEnabled(false);
					editAction.setEnabled(checked);
					clearAction.setEnabled(true);
					min.setEnabled(false);
					max.setEnabled(false);
					fireValuesChanged();
				}
			} else {
				checked = false;
				checkAction.setEnabled(false);
				editAction.setEnabled(checked);
				clearAction.setEnabled(false);
				min.setEnabled(true);
				max.setEnabled(true);
				fireValuesChanged();
			}
		}
	}

	@Override
	public Object getValue() {
		if ((useToolbar) && (!checked)) {
			return null;
		}
		Binome<E, E> value = null;
		if (hasMinComplete()) {
			value = new Binome<E, E>();
			value.setValue1(getMin());
		}
		if (hasMaxComplete()) {
			if (value == null) {
				value = new Binome<E, E>();
			}
			value.setValue2(getMax());
		}
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value != null) {
			@SuppressWarnings("unchecked")
			Binome<Integer, Integer> v = (Binome<Integer, Integer>) value;
			min.setValue(v.getValue1());
			max.setValue(v.getValue2());
		} else {
			min.setValue(null);
			max.setValue(null);
		}
		validateInput();
		fireValuesChanged();
	}

	private final class MyActionListener extends MouseAdapter implements ActionListener, PropertyChangeListener, DocumentListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (validateOnAction()) {
				validateInput();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("value".equals(evt.getPropertyName())) {
				if (((hasMinComplete()) && (min.isEnabled())) || ((hasMaxComplete()) && (max.isEnabled()))) {
					if (isCoherent()) {
						checkAction.setEnabled(true);
					} else {
						checkAction.setEnabled(false);
					}
				} else {
					checkAction.setEnabled(false);
				}
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			checkText();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					checkText();
				}
			});
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
				if ((e.getSource() instanceof JFormattedTextField) && (((JFormattedTextField) e.getSource()).isEnabled())) {
					((JFormattedTextField) e.getSource()).setValue(null);
				}
			}
		}
	}

	private void checkText() {
		if (((hasMinText()) && (min.isEnabled())) || ((hasMaxText()) && (max.isEnabled()))) {
			if (isTextCoherent()) {
				checkAction.setEnabled(true);
			} else {
				checkAction.setEnabled(false);
			}
		} else {
			checkAction.setEnabled(false);
		}
		fireValuesChanged();
	}

	protected abstract boolean validateOnAction();

	protected abstract boolean isCoherent();

	protected boolean isTextCoherent() {
		boolean res = true;
		if (hasMinText()) {
			try {
				if (getMaxComponent().getFormatter().stringToValue(getMinText()) == null) {
					res = false;
				}
			} catch (Exception e) {
				res = false;
			}
		}
		if (hasMaxText()) {
			try {
				if (getMaxComponent().getFormatter().stringToValue(getMaxText()) == null) {
					res = false;
				}
			} catch (Exception e) {
				res = false;
			}
		}
		return res;
	}

	protected final boolean hasMinComplete() {
		if (min.getValue() != null) {
			return true;
		}
		return false;
	}

	protected final boolean hasMaxComplete() {
		if (max.getValue() != null) {
			return true;
		}
		return false;
	}

	protected Object getMinValue() {
		return min.getValue();
	}

	protected Object getMaxValue() {
		return max.getValue();
	}

	protected final boolean hasMinText() {
		if ((min.getText() != null) && (!min.getText().isEmpty())) {
			return true;
		}
		return false;
	}

	protected final boolean hasMaxText() {
		if ((max.getText() != null) && (!max.getText().isEmpty())) {
			return true;
		}
		return false;
	}

	protected String getMinText() {
		return min.getText();
	}

	protected String getMaxText() {
		return max.getText();
	}

	protected abstract E getMin();

	protected abstract E getMax();

}
