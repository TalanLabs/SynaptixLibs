package simpledaystimeline.common;

import java.awt.datatransfer.Transferable;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineDrag;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineSelectionModel;

public class MySimpleDaysTimelineDrag implements SimpleDaysTimelineDrag {

	public Transferable createTransferable(JSimpleDaysTimeline simpleDaysTimeline) {
		SimpleDaysTimelineSelectionModel selectionModel = simpleDaysTimeline.getSelectionModel();
		if (selectionModel.getSelectionTaskCount() == 1) {
			SimpleDaysTask t = selectionModel.getSelectionTasks(selectionModel.getMinSelectionIndexResource())[0];
			if (t instanceof MyMissionSimpleDaysTask) {
				MyMissionSimpleDaysTask task = (MyMissionSimpleDaysTask) selectionModel.getSelectionTasks(selectionModel
						.getMinSelectionIndexResource())[0];
				System.out.println("createTransferable -> " + task);
				DayDate d1 = task.getDayDateMin();
				DayDate d2 = task.getDayDateMax();
				return new HoursTransferable(new Hours(task.getNum(), task.getIndiceMission(), d1.getHour() * 60 + d1.getMinute(),
						(d2.getDay() - d1.getDay()) * 24 * 60 + d2.getHour() * 60 + d2.getMinute()));
			}
		} else if (selectionModel.getSelectionTaskCount() > 1) {
			DayDate d1 = null;
			DayDate d2 = null;
			for (SimpleDaysTask t : selectionModel.getSelectionTasks(selectionModel.getMinSelectionIndexResource())) {
				if (t instanceof MyMissionSimpleDaysTask) {
					MyMissionSimpleDaysTask task = (MyMissionSimpleDaysTask) t;
					System.out.println("createTransferable -> " + task);
					if (d1 == null || task.getDayDateMin().before(d1)) {
						d1 = task.getDayDateMin();
					}
					if (d2 == null || task.getDayDateMax().after(d2)) {
						d2 = task.getDayDateMax();
					}
				}
			}
			return new HoursTransferable(new Hours(-1, null, d1.getHour() * 60 + d1.getMinute(), (d2.getDay() - d1.getDay()) * 24 * 60 + d2.getHour()
					* 60 + d2.getMinute()));
		}
		return null;
	}

	public void exportDone(JSimpleDaysTimeline simpleDaysTimeline, Transferable transferable) {
		System.out.println("exportDone");
	}
}