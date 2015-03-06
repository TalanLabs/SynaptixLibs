package simpledaystimeline.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineGroupFactory;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineGroupTask;

public class MySimpleDaysTimelineGroupFactory implements SimpleDaysTimelineGroupFactory {

	public List<SimpleDaysTimelineGroupTask> buildGroupTaskList(int resIndex, List<SimpleDaysTask> taskList) {
		Map<String, List<SimpleDaysTask>> map = new HashMap<String, List<SimpleDaysTask>>();

		if (taskList != null && !taskList.isEmpty()) {
			for (SimpleDaysTask task : taskList) {
				String id = null;
				if (task instanceof MyMissionSimpleDaysTask) {
					MyMissionSimpleDaysTask mt = (MyMissionSimpleDaysTask) task;
					id = mt.getJour() + "-" + mt.getIndiceMission();

				} else if (task instanceof MyPrevisionSimpleDaysTask) {
					MyPrevisionSimpleDaysTask mt = (MyPrevisionSimpleDaysTask) task;
					id = mt.getJour() + "-" + mt.getIndiceMission();
				}
				if (id != null) {
					List<SimpleDaysTask> l = map.get(id);
					if (l == null) {
						l = new ArrayList<SimpleDaysTask>();
						map.put(id, l);
					}
					l.add(task);
				}
			}
		}

		List<SimpleDaysTimelineGroupTask> res = new ArrayList<SimpleDaysTimelineGroupTask>();
		if (map != null && !map.isEmpty()) {
			for (Entry<String, List<SimpleDaysTask>> e : map.entrySet()) {
				res.add(new MySimpleDaysTimelineGroupTask(e.getKey(), e.getValue()));
			}
		}

		return res;
	}
}
