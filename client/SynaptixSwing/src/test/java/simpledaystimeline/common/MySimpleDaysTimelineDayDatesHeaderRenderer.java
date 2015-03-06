package simpledaystimeline.common;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.simpledaystimeline.DefaultSimpleDaysTimelineDayDatesHeaderRenderer;

public class MySimpleDaysTimelineDayDatesHeaderRenderer extends DefaultSimpleDaysTimelineDayDatesHeaderRenderer {

	protected String toDayDateString(DayDate dayDate, int zoom) {
		StringBuilder sb = new StringBuilder();
		sb.append("Jour ");
		sb.append(dayDate.getDay() + 1);
		return sb.toString();
	}

	public String getToolTipText(DayDate dayDate) {
		StringBuilder sb = new StringBuilder();
		sb.append("MegaJour ");
		sb.append(dayDate.getDay() + 1);
		return sb.toString();
	}
}