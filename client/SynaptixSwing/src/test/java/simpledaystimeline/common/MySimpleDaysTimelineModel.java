package simpledaystimeline.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.swing.AbstractSimpleDaysTimelineModel;
import com.synaptix.swing.DayDate;
import com.synaptix.swing.SimpleDaysTask;

public class MySimpleDaysTimelineModel extends AbstractSimpleDaysTimelineModel {

	private Map<Integer, List<MyMissionSimpleDaysTask>> missionMap;

	private Map<Integer, List<MyPrevisionSimpleDaysTask>> previsionMap;

	public MySimpleDaysTimelineModel() {
		super();
		missionMap = new HashMap<Integer, List<MyMissionSimpleDaysTask>>();
		previsionMap = new HashMap<Integer, List<MyPrevisionSimpleDaysTask>>();

		List<MyMissionSimpleDaysTask> ml1 = new ArrayList<MyMissionSimpleDaysTask>();
		ml1.add(new MyMissionSimpleDaysTask("gaby", 0, new DayDate(0, 5, 0), new DayDate(0, 8, 0)));
		ml1.add(new MyMissionSimpleDaysTask("gaby", 0, new DayDate(0, 9, 0), new DayDate(0, 12, 0)));
		ml1.add(new MyMissionSimpleDaysTask("gaby", 0, new DayDate(0, 15, 0), new DayDate(0, 19, 0)));

		ml1.add(new MyMissionSimpleDaysTask("gaby", 1, new DayDate(1, 5, 0), new DayDate(1, 8, 0)));
		ml1.add(new MyMissionSimpleDaysTask("gaby", 1, new DayDate(1, 9, 0), new DayDate(1, 12, 0)));

		ml1.add(new MyMissionSimpleDaysTask("sandra", 0, new DayDate(0, 7, 0), new DayDate(0, 13, 0)));
		ml1.add(new MyMissionSimpleDaysTask("sandra", 0, new DayDate(0, 13, 0), new DayDate(0, 14, 0)));

		ml1.add(new MyMissionSimpleDaysTask("sandra", 1, new DayDate(1, 15, 0), new DayDate(1, 19, 0)));

		missionMap.put(0, ml1);

		List<MyPrevisionSimpleDaysTask> pl1 = new ArrayList<MyPrevisionSimpleDaysTask>();

		pl1.add(new MyPrevisionSimpleDaysTask("gaby", 0, new DayDate(0, 4, 0), new DayDate(0, 20, 0)));

		previsionMap.put(0, pl1);

		List<MyMissionSimpleDaysTask> ml2 = new ArrayList<MyMissionSimpleDaysTask>();
		ml2.add(new MyMissionSimpleDaysTask("gaby", 0, new DayDate(0, 12, 0), new DayDate(0, 19, 0)));

		ml2.add(new MyMissionSimpleDaysTask("gaby", 1, new DayDate(1, 5, 0), new DayDate(1, 8, 0)));

		ml2.add(new MyMissionSimpleDaysTask("sandra", 0, new DayDate(0, 13, 0), new DayDate(0, 14, 0)));
		ml2.add(new MyMissionSimpleDaysTask("sandra", 3, new DayDate(3, 15, 0), new DayDate(3, 19, 0)));

		missionMap.put(2, ml2);

		List<MyPrevisionSimpleDaysTask> pl3 = new ArrayList<MyPrevisionSimpleDaysTask>();

		pl3.add(new MyPrevisionSimpleDaysTask("sandra", 0, new DayDate(0, 12, 0), new DayDate(0, 14, 0)));

		previsionMap.put(2, pl3);
	}

	public int getResourceCount() {
		return 10;
	}

	public String getResourceName(int resource) {
		return "Gaby : " + resource;
	}

	public List<SimpleDaysTask> getTasks(int resource, DayDate dayDateMin, DayDate dayDateMax) {
		List<SimpleDaysTask> tasks = new ArrayList<SimpleDaysTask>();
		List<MyMissionSimpleDaysTask> ml = missionMap.get(resource);
		if (ml != null && !ml.isEmpty()) {
			for (MyMissionSimpleDaysTask st : ml) {
				if (!st.getDayDateMin().after(dayDateMax) && !st.getDayDateMax().before(dayDateMin)) {
					tasks.add(st);
				}
			}
		}
		List<MyPrevisionSimpleDaysTask> pl = previsionMap.get(resource);
		if (pl != null && !pl.isEmpty()) {
			for (MyPrevisionSimpleDaysTask st : pl) {
				if (!st.getDayDateMin().after(dayDateMax) && !st.getDayDateMax().before(dayDateMin)) {
					tasks.add(st);
				}
			}
		}
		return tasks;
	}

	public List<MyMissionSimpleDaysTask> getTasks(int resource) {
		return missionMap.get(resource);
	}
}