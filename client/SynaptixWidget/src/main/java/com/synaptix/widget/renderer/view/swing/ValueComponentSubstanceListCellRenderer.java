package com.synaptix.widget.renderer.view.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

public class ValueComponentSubstanceListCellRenderer<E> extends SubstanceDefaultListCellRenderer implements Icon {

	private static final long serialVersionUID = 3743922066155485212L;

	protected IValueComponent<E> valueComponent;

	private CellRendererPane cellRendererPane;

	public ValueComponentSubstanceListCellRenderer() {
		this(null);
	}

	public ValueComponentSubstanceListCellRenderer(IValueComponent<E> valueComponent) {
		super();

		this.valueComponent = valueComponent;
		this.cellRendererPane = new CellRendererPane();

		this.add(this.cellRendererPane);

		this.setIcon(this);

		if (valueComponent != null) {
			this.setPreferredSize(valueComponent.getComponent().getPreferredSize());
		}
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component res = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		this.setText("Gaby");
		setValue(value);
		this.setIcon(this);
		return res;
	}

	@SuppressWarnings("unchecked")
	protected void setValue(Object value) {
		this.valueComponent.setValue(this, (E) value);
	}

	protected Component getComponent() {
		return this.valueComponent.getComponent();
	}

	public void setValueComponent(IValueComponent<E> valueComponent) {
		this.valueComponent = valueComponent;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		getComponent().setSize(width, height);
	}

	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);
		getComponent().setSize((int) r.getWidth(), (int) r.getHeight());
	}

	@Override
	public int getIconWidth() {
		return getComponent().getWidth();
	}

	@Override
	public int getIconHeight() {
		return getComponent().getHeight();
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		cellRendererPane.paintComponent(g2, getComponent(), ValueComponentSubstanceListCellRenderer.this, 0, 0, getIconWidth(), getIconHeight(), true);
		cellRendererPane.removeAll();
	}
}