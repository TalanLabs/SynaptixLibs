package com.synaptix.widget.calendarday;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class DefaultMonthCalendarDayCellRenderer extends JPanel implements CalendarDayCellRenderer {

	private static final long serialVersionUID = 7388247768725804892L;

	private static final Color color1 = new Color(224, 224, 224);

	private static final Color selectedColor = new Color(50, 64, 255, 128);

	protected final JLabel numDayLabel;

	protected final JLabel cellLabel;

	private boolean selected;

	private Font normalFont;

	private Font italicFont;

	public DefaultMonthCalendarDayCellRenderer() {
		super();

		this.setLayout(new MyLayout());

		numDayLabel = new JLabel("1");
		numDayLabel.setHorizontalAlignment(JLabel.CENTER);
		numDayLabel.setOpaque(false);
		this.add(numDayLabel);

		cellLabel = new JLabel();
		cellLabel.setBackground(Color.WHITE);
		cellLabel.setOpaque(true);
		this.add(cellLabel);
	}

	@Override
	public Component getCalendarDayCellRendererComponent(JCalendarDay calendarDay, Object value, int column, int row, boolean selected) {
		this.selected = selected;

		DefaultMonthCalendarDayModel model = (DefaultMonthCalendarDayModel) calendarDay.getModel();

		LocalDate date = (LocalDate) value;

		numDayLabel.setText(String.valueOf(date.getDayOfMonth()));
		boolean currentMonth = date.getMonthOfYear() == model.getMonth();
		int dayOfWeek = date.getDayOfWeek();

		if (normalFont == null) {
			normalFont = this.getFont();
			italicFont = this.getFont().deriveFont(Font.ITALIC);
		}
		this.setFont(currentMonth ? normalFont : italicFont);
		if (dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY) {
			cellLabel.setBackground(currentMonth ? Color.LIGHT_GRAY : color1);
		} else {
			cellLabel.setBackground(currentMonth ? Color.WHITE : color1);
		}
		return this;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int width = this.getWidth();
		int height = this.getHeight();

		Graphics2D g2 = (Graphics2D) g.create();
		// g2.setColor(Color.GRAY);
		// g2.drawLine(height, 0, height, height);
		// g2.drawLine(height * 2, 0, height * 2, height);

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
			numDayLabel.setBounds(0, 0, 20, 20);
			cellLabel.setBounds(0, 0, width, height);
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
