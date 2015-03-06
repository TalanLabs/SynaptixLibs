package com.synaptix.widget.perimeter.view.swing;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.SwingComponentFactory;
import com.synaptix.widget.util.StaticWidgetHelper;

/**
 * Widget for subscribed to transport plan
 * 
 * @author Nicolas P
 * 
 */
public final class DefaultIntegerMinMaxPerimeterWidget extends AbstractMinMaxPerimeterWidget<Integer> implements IPerimeterWidget {

	private static final long serialVersionUID = -7081530996734745572L;

	public DefaultIntegerMinMaxPerimeterWidget(String title) {
		super(title);
	}

	@Override
	protected JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,FILL:8DLU:NONE,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE" /* ,FILL:3DLU:NONE,FILL:DEFAULT:NONE */);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();
		builder.add(new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().min()), cc.xy(1, 1));
		builder.add(getMinComponent(), cc.xy(3, 1));
		builder.add(new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().max()), cc.xy(1, 3));
		builder.add(getMaxComponent(), cc.xy(3, 3));
		// builder.add(createToolbar(), cc.xyw(1, 5, 3));
		return builder.getPanel();
	}

	@Override
	public JComponent getView() {
		return this;
	}

	@Override
	protected JFormattedTextField createFieldComponent() {
		return SwingComponentFactory.createIntegerField();
	}

	@Override
	protected final Integer getMin() {
		Integer v = null;
		if (hasMinComplete()) {
			v = (Integer) getMinValue();
		}
		return v;
	}

	@Override
	protected final Integer getMax() {
		Integer v = null;
		if (hasMaxComplete()) {
			v = (Integer) getMaxValue();
		}
		return v;
	}

	@Override
	protected boolean isCoherent() {
		if ((hasMinComplete()) && (hasMaxComplete()) && (getMin() > getMax())) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean validateOnAction() {
		return true;
	}
}
