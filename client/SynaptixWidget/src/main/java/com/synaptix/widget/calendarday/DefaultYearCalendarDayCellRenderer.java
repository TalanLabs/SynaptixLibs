package com.synaptix.widget.calendarday;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import com.synaptix.widget.util.StaticWidgetHelper;

public class DefaultYearCalendarDayCellRenderer extends JPanel implements CalendarDayCellRenderer {

	private static final long serialVersionUID = 7388247768725804892L;

	private static final Color color1 = new Color(224, 224, 224);

	private static final Color selectedColor = new Color(50, 64, 255, 128);

	protected final JLabel numDayLabel;

	protected final JLabel dayOfWeekLabel;

	protected final JComponent cellLabel;

	private boolean selected;

	public DefaultYearCalendarDayCellRenderer() {
		super();

		this.setLayout(new MyLayout());

		this.numDayLabel = createNumDayLabel();
		this.add(numDayLabel);
		this.dayOfWeekLabel = createDayOfWeekLabel();
		this.add(dayOfWeekLabel);
		this.cellLabel = createCellLabel();
		this.add(this.cellLabel);
	}

	protected JLabel createNumDayLabel() {
		JLabel numDayLabel = new JLabel("1");
		numDayLabel.setHorizontalAlignment(JLabel.CENTER);
		numDayLabel.setOpaque(true);
		numDayLabel.setBackground(color1);
		return numDayLabel;
	}

	protected JLabel createDayOfWeekLabel() {
		JLabel dayOfWeekLabel = new JLabel("L");
		dayOfWeekLabel.setHorizontalAlignment(JLabel.CENTER);
		dayOfWeekLabel.setOpaque(true);
		dayOfWeekLabel.setBackground(color1);
		return dayOfWeekLabel;
	}

	protected JComponent createCellLabel() {
		JLabel cellLabel = new JLabel();
		cellLabel.setBackground(Color.WHITE);
		cellLabel.setOpaque(true);
		return cellLabel;
	}

	@Override
	public Component getCalendarDayCellRendererComponent(JCalendarDay calendarDay, Object value, int column, int row, boolean selected) {
		this.selected = selected;

		LocalDate date = (LocalDate) value;
		numDayLabel.setText(String.valueOf(date.getDayOfMonth()));

		int dayOfWeek = date.getDayOfWeek();
		dayOfWeekLabel.setText(StaticWidgetHelper.getSynaptixDateConstantsBundle().veryShortWeekDays()[dayOfWeek - 1]);
		if (dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY) {
			cellLabel.setBackground(Color.LIGHT_GRAY);
		} else {
			cellLabel.setBackground(Color.WHITE);
		}
		return this;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int width = this.getWidth();
		int height = this.getHeight();

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(Color.GRAY);
		g2.drawLine(height, 0, height, height);
		g2.drawLine(height * 2, 0, height * 2, height);

		if (selected) {
			g2.setColor(selectedColor);
			g2.fillRect(0, 0, width, height);
		}

		g2.dispose();
	}

	private class MyLayout implements LayoutManager {

		@Override
		public void layoutContainer(Container parent) {
			int width = parent.getWidth();
			int height = parent.getHeight();
			numDayLabel.setBounds(0, 0, height, height);
			dayOfWeekLabel.setBounds(height, 0, height, height);
			cellLabel.setBounds(height * 2, 0, width - height * 2, height);
		}

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return null;
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return null;
		}
	}
}
