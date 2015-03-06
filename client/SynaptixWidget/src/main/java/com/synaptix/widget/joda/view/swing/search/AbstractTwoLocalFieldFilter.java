package com.synaptix.widget.joda.view.swing.search;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.joda.time.base.BaseLocal;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.model.Binome;
import com.synaptix.common.model.DefaultTwoLocal;
import com.synaptix.swing.search.AbstractFilter;

/*protected package*/abstract class AbstractTwoLocalFieldFilter<E extends BaseLocal> extends AbstractFilter {

	private String id;

	private String name;

	private JFormattedTextField minLocalField;

	private JFormattedTextField maxLocalField;

	private JPanel panel;

	public AbstractTwoLocalFieldFilter(String id, String name) {
		super();
		this.id = id;
		this.name = name;

		initComponents();

		panel = buildContents();

		initialize();
	}

	protected abstract JComponent decorate(JFormattedTextField localField);

	private void initComponents() {
		minLocalField = new JFormattedTextField();
		minLocalField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
					minLocalField.setValue(null);

				}
			}
		});
		maxLocalField = new JFormattedTextField();
		maxLocalField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
					maxLocalField.setValue(null);
				}
			}
		});
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("FILL:75DLU:NONE,FILL:4DLU:NONE,FILL:75DLU:NONE", "CENTER:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(decorate(minLocalField), cc.xy(1, 1));
		builder.add(decorate(maxLocalField), cc.xy(3, 1));
		return new MyPanel(builder.getPanel());
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
	@SuppressWarnings("unchecked")
	public Serializable getValue() {
		E min = (E) minLocalField.getValue();
		E max = (E) maxLocalField.getValue();
		return min == null && max == null ? null : new DefaultTwoLocal<E>(min, max);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object o) {
		if (o != null) {
			Binome<E, E> tdf = (Binome<E, E>) o;
			minLocalField.setValue(tdf.getValue1());
			maxLocalField.setValue(tdf.getValue2());
		} else {
			minLocalField.setValue(null);
			maxLocalField.setValue(null);
		}
	}

	@Override
	public void copyDefaultValue() {
		minLocalField.setValue(null);
		maxLocalField.setValue(null);
	}

	@Override
	public JComponent getDefaultComponent() {
		return null;
	}

	private class MyPanel extends JPanel {

		private static final long serialVersionUID = -5660613587390189541L;

		public MyPanel(JComponent component) {
			super(new BorderLayout());

			this.add(component, BorderLayout.CENTER);
		}

		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);

			minLocalField.setEnabled(enabled);
			maxLocalField.setEnabled(enabled);
		}
	}
}
