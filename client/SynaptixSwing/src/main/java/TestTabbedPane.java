import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.AbstractTimelineModel;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JTimeline;
import com.synaptix.swing.Task;
import com.synaptix.swing.docking.SyDockableContainerFactory;
import com.synaptix.swing.timeline.TimelineTaskRenderer;
import com.synaptix.swing.utils.Manager;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableContainerFactory;
import com.vlsolutions.swing.docking.DockingDesktop;

public class TestTabbedPane {

	private static class MyTimelineModel extends AbstractTimelineModel {

		private Map<Integer, List<Task>> map;

		public MyTimelineModel() {
			super();
			map = new HashMap<Integer, List<Task>>();

			List<Task> tasks = new ArrayList<Task>();
			MyTask t1 = new MyTask("A170", getDate(2007, 6, 1, 7), getDate(
					2007, 6, 2, 18));
			MyTask t2 = new MyTask("A180", getDate(2007, 6, 3, 5), getDate(
					2007, 6, 3, 15));

			t1.setLiaison(t2);
			t2.setLiaison(t1);

			tasks.add(t1);

			tasks.add(t2);
			tasks.add(new MyTask("A280", getDate(2007, 6, 5, 5), getDate(2007,
					6, 5, 12)));
			tasks.add(new MyTask("A380", getDate(2007, 8, 5, 5), getDate(2007,
					8, 9, 12)));

			map.put(0, tasks);
		}

		public int getRessourceCount() {
			return 35;
		}

		public String getRessourceName(int ressource) {
			return "Gaby : " + ressource;
		}

		public List<Task> getTasks(int ressource, Date dateMin, Date dateMax) {
			Calendar cMin = Calendar.getInstance();
			cMin.setTime(dateMin);
			Calendar cMax = Calendar.getInstance();
			cMax.setTime(dateMax);

			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();

			List<Task> tasks = map.get(ressource);
			List<Task> res = new ArrayList<Task>();
			if (tasks != null) {
				for (Task task : tasks) {
					c1.setTime(task.getDateMin());
					c2.setTime(task.getDateMax());
					if (((c1.after(cMin) || c1.equals(cMin)) && (c1
							.before(cMax) || c1.equals(cMax)))
							|| ((c2.after(cMin) || c2.equals(cMin)) && (c2
									.before(cMax) || c2.equals(cMax)))
							|| (c1.before(cMin) && c2.after(cMax))) {
						res.add(task);
					}
				}
			}
			return res;
		}

		public Date getDateMax() {
			return getDate(2007, 8, 10, 0);
		}

		public Date getDateMin() {
			return getDate(2007, 6, 1, 0);
		}

		private Date getDate(int y, int m, int d, int h) {
			Calendar c = Calendar.getInstance();
			c.clear();
			c.set(y, m, d, h, 0, 0);
			return c.getTime();
		}

		private class MyTask implements Task {

			public String title;

			public Date min, max;

			public Task liaison;

			public MyTask(String title, Date min, Date max) {
				this.min = min;
				this.max = max;
				this.title = title;
			}

			public Date getDateMax() {
				return max;
			}

			public Date getDateMin() {
				return min;
			}

			public String getTitle() {
				return title;
			}

			public Task getLiaison() {
				return liaison;
			}

			public void setLiaison(Task task) {
				this.liaison = task;
			}

			public TimelineTaskRenderer getTaskRenderer() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getPriority() {
				return 0;
			}
			
			public boolean isSelected() {
				return true;
			}
		}
	}

	private static class MyDock implements Dockable {

		private DockKey key = new DockKey("textEditor" + Math.random());

		private JTimeline timeline;

		private JSyScrollPane scrollPane;

		public MyDock(String name) {
			super();

			MyTimelineModel model = new MyTimelineModel();
			timeline = new JTimeline(model);
			scrollPane = new JSyScrollPane(timeline);

			key = new DockKey("textEditor" + Math.random());
			key.setName(name);
			//key.setFloatEnabled(true);
			key.setAutoHideEnabled(false);
		}

		public Component getComponent() {
			return scrollPane;
		}

		public DockKey getDockKey() {
			return key;
		}

	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		DockableContainerFactory.setFactory(new SyDockableContainerFactory());

		DockingDesktop desk = new DockingDesktop();
		
		frame.getContentPane().add(desk);

		Dockable dock1 = new MyDock("Gaby");
		Dockable dock2 = new MyDock("Juan");
		Dockable dock3 = new MyDock("Pascale");
		desk.addDockable(dock1);
		desk.createTab(dock1, dock3,0);
		desk.createTab(dock1, dock2, 0);
		// desk.setFloating(dock1, true);

		frame.setTitle("TestTileset");
		// frame.pack();
		frame.setSize(300, 400);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
