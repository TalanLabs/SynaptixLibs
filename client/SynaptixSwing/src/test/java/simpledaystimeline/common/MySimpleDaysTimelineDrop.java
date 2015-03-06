package simpledaystimeline.common;

import java.awt.datatransfer.Transferable;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineDrop;

public class MySimpleDaysTimelineDrop implements SimpleDaysTimelineDrop {

	public boolean canDrop(Transferable transferable) {
		if (transferable != null && transferable.isDataFlavorSupported(HoursTransferable.HOURS_FLAVOR)) {
			return true;
		}
		return false;
	}

	public SimpleDaysTask[] getTask(Transferable transferable, int resource, DayDate dayDate) {
		SimpleDaysTask[] tasks = null;
		Hours hours = null;
		try {
			hours = (Hours) transferable.getTransferData(HoursTransferable.HOURS_FLAVOR);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (hours != null) {
			SimpleDaysTask task1 = createTaskByHours(dayDate, hours);
			tasks = new SimpleDaysTask[] { task1 };
		}
		return tasks;
	}

	public void done(Transferable transferable, int resource, DayDate dayDate) {
		try {
			Hours hours = (Hours) transferable.getTransferData(HoursTransferable.HOURS_FLAVOR);
			System.out.println("Drop -> " + hours);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SimpleDaysTask createTaskByHours(DayDate dayDate, Hours hours) {
		DayDate dayDateMin = new DayDate(dayDate);
		dayDateMin.addMinute(hours.getMinuteStart());

		DayDate dayDateMax = new DayDate(dayDate);
		dayDateMax.addMinute(hours.getMinuteEnd());
		return new MyMissionSimpleDaysTask("gaby", dayDate.getDay(), dayDateMin, dayDateMax);
	}
}