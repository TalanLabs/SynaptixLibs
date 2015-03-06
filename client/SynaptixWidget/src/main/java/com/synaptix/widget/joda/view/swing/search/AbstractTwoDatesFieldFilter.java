package com.synaptix.widget.joda.view.swing.search;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.model.Binome;
import com.synaptix.common.model.DefaultTwoDates;
import com.synaptix.swing.search.AbstractFilter;
import com.synaptix.swing.utils.SwingComponentFactory;

public abstract class AbstractTwoDatesFieldFilter extends AbstractFilter {

	private String id;

	private String name;

	private JFormattedTextField date1Field;

	private JFormattedTextField date2Field;

	private JFormattedTextField defaultDate1Field;

	private JFormattedTextField defaultDate2Field;

	private JPanel panel;

	private JPanel defaultPanel;

	public AbstractTwoDatesFieldFilter(String name) {
		this(name, name);
	}

	public AbstractTwoDatesFieldFilter(String id, String name) {
		super();
		this.id = id;
		this.name = name;

		initComponents();

		panel = buildContents();

		initialize();
	}

	private void initComponents() {
		date1Field = createDateField();
		date1Field.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
					date1Field.setValue(null);
				}
			}
		});
		date2Field = SwingComponentFactory.createDateHourField(false, true);
		date2Field.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
					date2Field.setValue(null);
				}
			}
		});
	}

	protected abstract JFormattedTextField createDateField();

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("FILL:75DLU:NONE,FILL:4DLU:NONE,FILL:75DLU:NONE", "CENTER:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(date1Field, cc.xy(1, 1));
		builder.add(date2Field, cc.xy(3, 1));
		return builder.getPanel();
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Serializable getValue() {
		Date date1 = (Date) date1Field.getValue();
		Date date2 = (Date) date2Field.getValue();
		if ((date1 == null) && (date2 == null)) {
			return null;
		}
		return new DefaultTwoDates(date1, date2);
	}

	@Override
	public void setValue(Object o) {
		if (o != null) {
			@SuppressWarnings("unchecked")
			Binome<Date, Date> tdf = (Binome<Date, Date>) o;
			date1Field.setValue(tdf.getValue1());
			date2Field.setValue(tdf.getValue2());
		} else {
			date1Field.setValue(null);
			date2Field.setValue(null);
		}
	}

	@Override
	public void copyDefaultValue() {
		date1Field.setValue(null);
		date2Field.setValue(null);
	}

	@Override
	public JComponent getDefaultComponent() {
		return null;
	}
}
