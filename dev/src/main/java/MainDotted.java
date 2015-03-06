import helper.MainHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.utils.GraphicsHelper;
import com.synaptix.widget.calendarday.AbstractCalendarDayModel;
import com.synaptix.widget.calendarday.CalendarDayCellRenderer;
import com.synaptix.widget.calendarday.JCalendarDay;

public class MainDotted {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				// JDotted point = new JDotted();
				// point.setDots(convert("111100111111111000000", 7, 3));
				// point.setDotSize(8);
				frame.getContentPane().add(createCalendar());
				frame.setSize(800, 600);
				frame.setVisible(true);
			}
		});
	}

	private static JCalendarDay createCalendar() {
		DottedCalendarDayModel model = new DottedCalendarDayModel();
		model.setDots(convert("111100111111111000000", 7, 3));
		JCalendarDay calendarDay = new JCalendarDay(model);
		calendarDay.setShowGrid(false);
		calendarDay.setColumnWidth(8);
		calendarDay.setRowHeight(8);
		calendarDay.setCalendarDayRowHeader(null);
		calendarDay.setCalendarDayColumnHeader(null);
		calendarDay.setCellRenderer(new DottedCalendarDayCellRenderer());
		return calendarDay;
	}

	private static class DottedCalendarDayCellRenderer extends JComponent implements CalendarDayCellRenderer {

		private static final Color dotColor = Color.DARK_GRAY;

		private static final Color notDotColor = Color.GRAY;

		private boolean use;

		public DottedCalendarDayCellRenderer() {
			super();
		}

		@Override
		public Component getCalendarDayCellRendererComponent(JCalendarDay calendarDay, Object value, int column, int row, boolean selected) {
			use = (Boolean) value;
			return this;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();
			GraphicsHelper.activeAntiAliasing(g2);

			if (use) {
				g2.setColor(dotColor);
				g2.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
			} else {
				g2.setColor(notDotColor);
				g2.drawRect(0, 0, this.getWidth() - 2, this.getHeight() - 2);
			}
			g2.dispose();
		}
	}

	private static class DottedCalendarDayModel extends AbstractCalendarDayModel {

		private boolean[][] dots;

		public void setDots(boolean[][] dots) {
			this.dots = dots != null ? dots.clone() : null;
			fireCalendarDayDataChanged();
		}

		public boolean[][] getDots() {
			return dots;
		}

		@Override
		public int getColumnCount() {
			return dots != null ? dots.length : 0;
		}

		@Override
		public String getColumnName(int column) {
			return null;
		}

		@Override
		public int getRowCount(int column) {
			return dots != null ? dots[column].length : 0;
		}

		@Override
		public Object getValue(int column, int row) {
			return dots != null ? dots[column][row] : null;
		}

	}

	public static boolean[][] convert(String slots, int days, int nb) {
		boolean[][] res = null;
		if (slots != null) {
			res = new boolean[days][nb];
			for (int i = 0; i < days; i++) {
				for (int j = 0; j < nb; j++) {
					int pos = i * nb + j;
					int v = Integer.parseInt(slots.substring(pos, pos + 1));
					if (v > 0) {
						res[i][j] = true;
					} else {
						res[i][j] = false;
					}
				}
			}
		}
		return res;
	}
}
