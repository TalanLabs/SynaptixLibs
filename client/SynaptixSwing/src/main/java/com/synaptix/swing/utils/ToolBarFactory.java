package com.synaptix.swing.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public final class ToolBarFactory {

	private final static RowSpec[] ROW_SPECS = new RowSpec[] { RowSpec.decode("center:pref") };

	private static Color normalPanelBackground = new Color(224, 224, 224);

	private static Color separator1Color = new Color(128, 128, 128);

	private static Color separator2Color = new Color(245, 245, 245);

	private ToolBarFactory() {
	}

	public static void setNormalPanelBackground(Color normalPanelBackground) {
		ToolBarFactory.normalPanelBackground = normalPanelBackground;
	}

	public static Color getNormalPanelBackground() {
		return normalPanelBackground;
	}

	public static void setSeparator1Color(Color separator1Color) {
		ToolBarFactory.separator1Color = separator1Color;
	}

	public static Color getSeparator1Color() {
		return separator1Color;
	}

	public static void setSeparator2Color(Color separator2Color) {
		ToolBarFactory.separator2Color = separator2Color;
	}

	public static Color getSeparator2Color() {
		return separator2Color;
	}

	public static final JComponent buildToolBar(Action... actions) {
		List<ColumnSpec> columnSpecList = new ArrayList<ColumnSpec>();

		for (int i = 0; i < actions.length; i++) {
			Action a = actions[i];
			if (a != null) {
				columnSpecList.add(ColumnSpec.decode("FILL:D"));
			} else {
				columnSpecList.add(ColumnSpec.decode("FILL:4PX"));
			}
		}
		columnSpecList.add(ColumnSpec.decode("FILL:D:GROW(1.0)"));

		FormLayout layout = new FormLayout(columnSpecList.toArray(new ColumnSpec[columnSpecList.size()]), ROW_SPECS);
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int x = 1;
		for (int i = 0; i < actions.length; i++) {
			Action a = actions[i];
			if (a != null) {
				builder.add(decoreButton(new JButton(a)), cc.xy(x, 1));
			} else {
				builder.add(new MySeparator(), cc.xy(x, 1, CellConstraints.FILL, CellConstraints.FILL));
			}
			x++;
		}

		return decorePanel(builder.getPanel());
	}

	public static final JComponent buildToolBar(Component... components) {
		List<ColumnSpec> columnSpecList = new ArrayList<ColumnSpec>();

		for (int i = 0; i < components.length; i++) {
			Component b = components[i];
			if (b != null) {
				columnSpecList.add(ColumnSpec.decode("FILL:D"));
			} else {
				columnSpecList.add(ColumnSpec.decode("FILL:4PX"));
			}
		}
		columnSpecList.add(ColumnSpec.decode("FILL:D:GROW(1.0)"));

		FormLayout layout = new FormLayout(columnSpecList.toArray(new ColumnSpec[columnSpecList.size()]), ROW_SPECS);
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int x = 1;
		for (int i = 0; i < components.length; i++) {
			Component b = components[i];
			if (b != null) {
				builder.add(decoreButton(b), cc.xy(x, 1));
			} else {
				builder.add(new MySeparator(), cc.xy(x, 1, CellConstraints.FILL, CellConstraints.FILL));
			}
			x++;
		}
		return decorePanel(builder.getPanel());
	}

	private static final JComponent decorePanel(JPanel panel) {
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setBackground(normalPanelBackground);
		return panel;
	}

	private static final Component decoreButton(final Component component) {
		if (component instanceof AbstractButton) {
			final AbstractButton button = (AbstractButton) component;
			ButtonHelper.installButtonChanger(button);
			return button;// new MyButtonCompo(button);
		}
		return component;
	}

	private static final class MySeparator extends JComponent {

		private static final long serialVersionUID = -506703720452031562L;

		private Dimension dimension = new Dimension(2, 0);

		@Override
		public Dimension getPreferredSize() {
			return dimension;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();

			int h = this.getHeight();
			int w = this.getWidth();
			int m = w / 2;

			g2.setColor(separator1Color);
			g2.drawLine(m - 1, 1, m - 1, h - 2);

			g2.setColor(separator2Color);
			g2.drawLine(m, 2, m, h - 3);

			g2.dispose();
		}
	}
}
