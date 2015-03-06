/*
 * Originaly developped for Opteam (all credit goes to Mr. e413915, whoever you are), this widget has been imported into Message Manager for the Error Analysis feature.
 * If it is generic and useful enough, we may consider refactoring it into a common project.
 */
package com.synaptix.widget.perimeter.view.swing;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import org.joda.time.LocalDate;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.model.Binome;
import com.synaptix.widget.joda.view.swing.JodaSwingUtils;
import com.synaptix.widget.util.StaticWidgetHelper;

/**
 * This widget allows the user to defines a filter upon a range of dates.<br />
 * <br />
 * In order to retrieve the filter value, the method {@link #getValue()} should be used.<br />
 * The <code>min</code> date shall be retrieve with {@link Binome#getValue1()}, and the <code>max</code> date with {@link Binome#getValue2()}. <br />
 * <br />
 * Of course, the values of this widget can be set with {@link LocalDateMinMaxPerimeterWidget#setValue(Object)}, where the <code>Object</code> parameter has a structure similar to the return of
 * getValue().
 * 
 * @version %I% %G%
 * 
 */
public class LocalDateMinMaxPerimeterWidget extends AbstractMinMaxPerimeterWidget<LocalDate> {

	/** Serialization ID */
	private static final long serialVersionUID = 414067748599078192L;

	/**
	 * Should we add one day for the maximum, so that comparisons with other dates works fine?
	 */
	private boolean addOneDay;

	/**
	 * 
	 * @param title
	 *            the title of this widget, sent to the super constructor.
	 * @param maxMinPerimetreContext
	 *            the perimeter context of this widget
	 * @see AbstractMinMaxPerimeterWidget#AbstractMinMaxPerimetreWidget(String)
	 */
	public LocalDateMinMaxPerimeterWidget(final String title) {
		this(title, false);
	}

	/**
	 * 
	 * @param title
	 *            the title of this widget, sent to the super constructor.
	 * @param maxMinPerimetreContext
	 *            the perimeter context of this widget
	 * @param addOneDay
	 *            for the maximum value, when not null, should we add one day so that comparisons with other dates works fine?
	 * @see AbstractMinMaxPerimeterWidget#AbstractMinMaxPerimetreWidget(String)
	 */
	public LocalDateMinMaxPerimeterWidget(final String title, final boolean addOneDay) {
		super(title);

		this.addOneDay = addOneDay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#buildContents()
	 */
	@Override
	protected JComponent buildContents() {
		final FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,FILL:8DLU:NONE,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE" /* ,FILL:3DLU:NONE,FILL:DEFAULT:NONE */);
		final PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		final CellConstraints cc = new CellConstraints();
		builder.add(new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().min()), cc.xy(1, 1));

		builder.add(JodaSwingUtils.decorateLocalDate(getMinComponent()), cc.xy(3, 1));
		builder.add(new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().max()), cc.xy(1, 3));
		builder.add(JodaSwingUtils.decorateLocalDate(getMaxComponent()), cc.xy(3, 3));
		// builder.add(createToolbar(), cc.xyw(1, 5, 3));

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
		return new LocalDate(super.getMinValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMaxValue()
	 */
	@Override
	protected Object getMaxValue() {
		return getMaxValue(addOneDay);
	}

	private LocalDate getMaxValue(boolean addOneDay) {
		if (addOneDay) {
			return new LocalDate(super.getMaxValue()).plusDays(1);
		}
		return new LocalDate(super.getMaxValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMin()
	 */
	@Override
	protected final LocalDate getMin() {
		final LocalDate v = null;
		if (hasMinComplete()) {
			return (LocalDate) getMinValue();
		}
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMax()
	 */
	@Override
	protected final LocalDate getMax() {
		return getMax(addOneDay);
	}

	private final LocalDate getMax(boolean addOneDay) {
		final LocalDate v = null;
		if (hasMaxComplete()) {
			return getMaxValue(addOneDay);
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
		if ((hasMinComplete()) && (hasMaxComplete()) && (getMin().isAfter(getMax(false)))) {
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
