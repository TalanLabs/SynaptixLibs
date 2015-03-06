/*
 * Originaly developped for Opteam (all credit goes to Mr. e413915, whoever you are), this widget has been imported into Message Manager for the Error Analysis feature.
 * If it is generic and useful enough, we may consider refactoring it into a common project.
 */
package com.synaptix.widget.perimeter.view.swing;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.model.Binome;
import com.synaptix.widget.joda.view.swing.JodaSwingUtils;
import com.synaptix.widget.util.StaticWidgetHelper;

/**
 * This widget allows the user to defines a filter upon a range of dates, and a time zone.<br />
 * <br />
 * In order to retrieve the filter value, the method {@link #getValue()} should be used. The result is a Map, of which keys are DatesMinMaxPerimetreWidget.{@link VALUE_KEY_TIMEZONE} and
 * DatesMinMaxPerimetreWidget.{@link #VALUE_KEY_TIMES}. <br />
 * Please note that the value associated to the former key is a String representing the chosen time zone, and the later is a {@link Binome} of dates.<br />
 * The <code>min</code> date shall be retrieve with {@link Binome#getValue1()}, and the <code>max</code> date with {@link Binome#getValue2()}. <br />
 * <br />
 * Of course, the values of this widget can be set with {@link LocalTimeMinMaxPerimeterWidget#setValue(Object)}, where the <code>Object</code> parameter has a structure similar to the return of
 * getValue().
 * 
 * @version %I% %G%
 * 
 */
public class LocalTimeMinMaxPerimeterWidget extends AbstractMinMaxPerimeterWidget<LocalTime> {

	/** Serialization ID */
	private static final long serialVersionUID = -7081530996734745572L;

	/** Combo box offering the choice of the time zone. */
	private JComboBox timezoneComboBox;

	/**
	 * 
	 * @param title
	 *            the title of this widget, sent to the super constructor.
	 * @see AbstractMinMaxPerimeterWidget#AbstractMinMaxPerimetreWidget(String)
	 */
	public LocalTimeMinMaxPerimeterWidget(final String title) {
		super(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#buildContents()
	 */
	@Override
	protected JComponent buildContents() {
		final List<String> timezones = new ArrayList<String>(DateTimeZone.getAvailableIDs());
		Collections.sort(timezones);
		this.timezoneComboBox = new JComboBox(timezones.toArray());
		this.timezoneComboBox.setSelectedItem(DateTimeZone.getDefault().getID());
		AutoCompleteDecorator.decorate(this.timezoneComboBox);
		this.timezoneComboBox.setPreferredSize(new Dimension(30, this.timezoneComboBox.getPreferredSize().height));

		final FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,FILL:8DLU:NONE,FILL:DEFAULT:GROW(1.0)",
				"FILL:DEFAULT:NONE,FILL:2DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE" /* ,FILL:3DLU:NONE,FILL:DEFAULT:NONE */);
		final PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		final CellConstraints cc = new CellConstraints();
		builder.add(new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().timezone()), cc.xyw(1, 1, 3));
		builder.add(timezoneComboBox, cc.xyw(1, 3, 3));
		builder.add(new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().min()), cc.xy(1, 5));

		builder.add(JodaSwingUtils.decorateLocalTime(getMinComponent()), cc.xy(3, 5));
		builder.add(new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().max()), cc.xy(1, 7));
		builder.add(JodaSwingUtils.decorateLocalTime(getMaxComponent()), cc.xy(3, 7));
		// builder.add(createToolbar(), cc.xyw(1, 9, 3));

		return builder.getPanel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractPerimetreWidget#getView()
	 */
	@Override
	public JComponent getView() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#createCombo()
	 */
	@Override
	protected JFormattedTextField createFieldComponent() {
		return new JFormattedTextField();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMinValue()
	 */
	@Override
	protected Object getMinValue() {
		return new LocalTime(super.getMinValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMaxValue()
	 */
	@Override
	protected Object getMaxValue() {
		return new LocalTime(super.getMaxValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMin()
	 */
	@Override
	protected final LocalTime getMin() {
		final LocalTime v = null;
		if (hasMinComplete()) {
			return (LocalTime) getMinValue();
		}
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMax()
	 */
	@Override
	protected final LocalTime getMax() {
		final LocalTime v = null;
		if (hasMaxComplete()) {
			return (LocalTime) getMaxValue();
		}
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#isCoherent()
	 */
	@Override
	protected boolean isCoherent() {
		if ((hasMinComplete()) && (hasMaxComplete()) && (getMin().isAfter(getMax()))) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#validateOnAction()
	 */
	@Override
	protected boolean validateOnAction() {
		return true;
	}
}
