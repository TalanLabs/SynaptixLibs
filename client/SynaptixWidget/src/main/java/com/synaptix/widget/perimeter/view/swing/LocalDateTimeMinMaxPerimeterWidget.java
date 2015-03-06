/*
 * Originaly developped for Opteam (all credit goes to Mr. e413915, whoever you are), this widget has been imported into Message Manager for the Error Analysis feature.
 * If it is generic and useful enough, we may consider refactoring it into a common project.
 */
package com.synaptix.widget.perimeter.view.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import org.joda.time.LocalDateTime;

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
 * DatesMinMaxPerimetreWidget.{@link #VALUE_KEY_DATES}. <br />
 * Please note that the value associated to the former key is a String representing the chosen time zone, and the later is a {@link Binome} of dates.<br />
 * The <code>min</code> date shall be retrieve with {@link Binome#getValue1()}, and the <code>max</code> date with {@link Binome#getValue2()}. <br />
 * <br />
 * Of course, the values of this widget can be set with {@link LocalDateTimeMinMaxPerimeterWidget#setValue(Object)}, where the <code>Object</code> parameter has a structure similar to the return of
 * getValue().
 * 
 * @author psourisse
 * @version %I% %G%
 * 
 */
public class LocalDateTimeMinMaxPerimeterWidget extends AbstractMinMaxPerimeterWidget<LocalDateTime> {

	/** Serialization ID */
	private static final long serialVersionUID = -7081530996734745572L;

	/**
	 * 
	 * @param title
	 *            the title of this widget, sent to the super constructor.
	 * @param maxMinPerimetreContext
	 *            the perimeter context of this widget
	 * @see AbstractMinMaxPerimeterWidget#AbstractMinMaxPerimetreWidget(String)
	 */
	public LocalDateTimeMinMaxPerimeterWidget(final String title) {
		super(title);
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

		builder.add(JodaSwingUtils.decorateLocalDateTime(getMinComponent()), cc.xy(3, 1));
		builder.add(new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().max()), cc.xy(1, 3));
		builder.add(JodaSwingUtils.decorateLocalDateTime(getMaxComponent()), cc.xy(3, 3));
		// builder.add(createToolbar(), cc.xyw(1, 5, 3));

		getMinComponent().addPropertyChangeListener(new MinMaxFieldPropertyChangeListener(false, getMinComponent()));
		getMaxComponent().addPropertyChangeListener(new MinMaxFieldPropertyChangeListener(true, getMaxComponent()));

		return builder.getPanel();
	}

	/**
	 * This PropertyChangeListener should be used on each of the date fields components of the DatesMinMaxPerimetreWidget.<br />
	 * Its purpose is to get a date input from a calendar and put it into the date fileds. The calendar is issued from a graphical component allowing the user to select a date with a mouse action,
	 * instead of writing it down manually.
	 * 
	 * @author psourisse
	 * 
	 */
	private final class MinMaxFieldPropertyChangeListener implements PropertyChangeListener {

		/** Defines if this listener should listen to a max or a min date. */
		private final boolean isMax;

		/** The target text field, where the date will be written. */
		private final JFormattedTextField formattedTextField;

		/**
		 * Builds a new PropertyChangeListener for a DatesMinMaxPerimetreWidget.
		 * 
		 * @param isMax
		 *            <code>true</code> if this listener deals with a "max" date, <code>false</code> otherwise
		 * @param formattedTextField
		 *            the text field where will the text be written. In most cases, it is also the field on which this PropertyChangeListener is set.
		 */
		public MinMaxFieldPropertyChangeListener(boolean isMax, JFormattedTextField formattedTextField) {
			super();
			this.isMax = isMax;
			this.formattedTextField = formattedTextField;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if ("calendar".equals(evt.getPropertyName())) {
				final LocalDateTime dateTime = (LocalDateTime) this.formattedTextField.getValue();
				if (this.isMax) {
					this.formattedTextField.setValue(dateTime.withTime(23, 59, 59, 999));
				} else {
					this.formattedTextField.setValue(dateTime.withTime(0, 0, 0, 0));
				}
			} else if ("value".equals(evt.getPropertyName())) {
				fireValuesChanged();
			}
		}
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
		return new LocalDateTime(super.getMinValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMaxValue()
	 */
	@Override
	protected Object getMaxValue() {
		return new LocalDateTime(super.getMaxValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMin()
	 */
	@Override
	protected final LocalDateTime getMin() {
		final LocalDateTime v = null;
		if (hasMinComplete()) {
			return (LocalDateTime) getMinValue();
		}
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.widget.perimetre.view.swing.AbstractMinMaxPerimetreWidget#getMax()
	 */
	@Override
	protected final LocalDateTime getMax() {
		final LocalDateTime v = null;
		if (hasMaxComplete()) {
			return (LocalDateTime) getMaxValue();
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
