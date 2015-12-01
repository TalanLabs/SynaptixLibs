import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.selection.XYSelectionModel;
import com.synaptix.widget.calendarday.DefaultCalendarDayRowHeaderRenderer;
import com.synaptix.widget.calendarday.DefaultMonthCalendarDayCellRenderer;
import com.synaptix.widget.calendarday.DefaultMonthCalendarDayModel;
import com.synaptix.widget.calendarday.DefaultYearCalendarDayModel;
import com.synaptix.widget.calendarday.JCalendarDay;
import com.synaptix.widget.calendarday.JCalendarDayRowHeader;

import helper.MainHelper;

public class MainCalendarDay {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JCalendarDay calendarDay = createYearCalendar();

				frame.getContentPane().add(calendarDay);

				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	private static JCalendarDay createYearCalendar() {
		DefaultYearCalendarDayModel model = new DefaultYearCalendarDayModel(2014);
		JCalendarDay calendarDay = new JCalendarDay(model);
		calendarDay.setCalendarDayRowHeader(new JCalendarDayRowHeader(calendarDay));
		calendarDay.getCalendarDayRowHeader().setRowHeaderRenderer(new DefaultCalendarDayRowHeaderRenderer());
		calendarDay.getSelectionModel().setSelectionMode(XYSelectionModel.MULTI_SELECTION);
		// calendarDay.setSelectionMode(JCalendarDay.RECTANGLE_SELECTION_MODE);
		return calendarDay;
	}

	private static JCalendarDay createMonthCalendar() {
		DefaultMonthCalendarDayModel model = new DefaultMonthCalendarDayModel(2014, 7);
		JCalendarDay calendarDay = new JCalendarDay(model);
		calendarDay.setColumnWidth(75);
		calendarDay.setRowHeight(75);
		calendarDay.setCellRenderer(new DefaultMonthCalendarDayCellRenderer());
		calendarDay.getSelectionModel().setSelectionMode(XYSelectionModel.MULTI_SELECTION);
		calendarDay.setSelectionMode(JCalendarDay.HORIZONTAL_SELECTION_MODE);
		return calendarDay;
	}

}
