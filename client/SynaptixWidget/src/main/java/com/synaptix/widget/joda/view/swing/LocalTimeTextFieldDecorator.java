package com.synaptix.widget.joda.view.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLayeredPane;

public class LocalTimeTextFieldDecorator extends JLayeredPane {

	private static final long serialVersionUID = -7053713171500741045L;

	private static final int CONTENT_LAYER = 1;

	private static final int FEEDBACK_LAYER = 2;

	public static JComponent decorate(JFormattedTextField formattedTextField) {
		return new LocalTimeTextFieldDecorator(formattedTextField);
	}

	private JFormattedTextField formattedTextField;

	private LocalTimeTextFieldDecorator(JFormattedTextField formattedTextField) {
		super();

		this.setLayout(new SimpleLayout());

		this.formattedTextField = formattedTextField;

		initComponents();
	}

	private void initComponents() {
		this.add(formattedTextField, CONTENT_LAYER);
	}

	private final class SimpleLayout implements LayoutManager {

		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			if (formattedTextField != null) {
				return formattedTextField.getPreferredSize();
			}
			return new Dimension();
		}

		public Dimension minimumLayoutSize(Container parent) {
			if (formattedTextField != null) {
				return formattedTextField.getMinimumSize();
			}
			return new Dimension();
		}

		public void layoutContainer(Container parent) {
			if (formattedTextField != null) {
				Dimension size = parent.getSize();
				formattedTextField.setBounds(0, 0, size.width, size.height);
			}
		}
	}
}
