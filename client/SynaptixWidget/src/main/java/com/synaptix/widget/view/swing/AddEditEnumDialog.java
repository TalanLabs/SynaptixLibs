package com.synaptix.widget.view.swing;

import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.dialog.AbstractSimpleDialog2;
import com.synaptix.widget.view.swing.helper.EnumViewHelper;

public class AddEditEnumDialog<E extends Enum<E>> extends AbstractSimpleDialog2 {

	private static final long serialVersionUID = -5968015818775564189L;

	private final E[] values;

	private final Map<String, String> map;

	private JComboBox enumComboBox;

	public AddEditEnumDialog(E[] values, Map<String, String> map) {
		super();

		this.values = values;
		this.map = map;

		initComponents();

		initialize();
	}

	private void initComponents() {
		enumComboBox = EnumViewHelper.createEnumComboBox(values, map, false);

		ValidationListener validationListener = new ValidationListener(this);
		enumComboBox.addActionListener(validationListener);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;DEFAULT):NONE,FILL:4DLU:NONE,FILL:200DLU:NONE", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.addLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().choose(), cc.xy(1, 1));
		builder.add(enumComboBox, cc.xy(3, 1));
		return builder.getPanel();
	}

	@Override
	protected void openDialog() {
		enumComboBox.setEnabled(!readOnly);
	}

	@SuppressWarnings("unchecked")
	public E getValue() {
		return (E) enumComboBox.getSelectedItem();
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		if (enumComboBox.getSelectedItem() == null) {
			result.add(new SimpleValidationMessage(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().mandatoryField(), Severity.ERROR, enumComboBox));
		}
	}
}