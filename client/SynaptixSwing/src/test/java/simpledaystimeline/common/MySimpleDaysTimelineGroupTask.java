package simpledaystimeline.common;

import java.util.List;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineGroupTask;

public class MySimpleDaysTimelineGroupTask implements SimpleDaysTimelineGroupTask {

	private String id;

	private DayDate dayDateMin;

	private DayDate dayDateMax;

	private List<SimpleDaysTask> taskList;

	public MySimpleDaysTimelineGroupTask(String id, List<SimpleDaysTask> taskList) {
		this.id = id;
		this.taskList = taskList;
		dayDateMin = null;
		dayDateMax = null;
		for (SimpleDaysTask task : taskList) {
			if (dayDateMin == null || dayDateMin.after(task.getDayDateMin())) {
				dayDateMin = task.getDayDateMin();
			}
			if (dayDateMax == null || dayDateMax.before(task.getDayDateMax())) {
				dayDateMax = task.getDayDateMax();
			}
		}
	}

	public DayDate getDayDateMax() {
		return dayDateMax;
	}

	public DayDate getDayDateMin() {
		return dayDateMin;
	}

	public List<SimpleDaysTask> getSimpleDaysTaskList() {
		return taskList;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object o) {
		if (o != null && o instanceof MySimpleDaysTimelineGroupTask) {
			MySimpleDaysTimelineGroupTask o2 = (MySimpleDaysTimelineGroupTask) o;
			return id == o2.id;
		}
		return super.equals(o);
	}
}
