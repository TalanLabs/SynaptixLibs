import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.AbstractPlanningModel;
import com.synaptix.swing.Activity;
import com.synaptix.swing.JPlanning;
import com.synaptix.swing.utils.Manager;

public class TestPlanning {

	private static class MyPlanningModel extends AbstractPlanningModel {

		private static final long serialVersionUID = -8860073159579745170L;

		private List<Activity> activities = new ArrayList<Activity>();

		public MyPlanningModel() {
			super();

			activities.add(new MyActivity("Gaby1", getDate(2008, 8, 2, 17),
					getDate(2008, 8, 2, 23)));
			activities.add(new MyActivity("Gaby2", getDate(2008, 8, 3, 8),
					getDate(2008, 8, 4, 10)));
			activities.add(new MyActivity("Gaby3", getDate(2008, 9, 22, 0),
					getDate(2008, 9, 22, 0)));
			// activities.add(new MyActivity("Sandra", getDate(2008, 8, 17, 17),
			// getDate(2008, 20, 3, 15)));
			// activities.add(new MyActivity("Tom", getDate(2008, 8, 7, 7),
			// getDate(2008, 8, 20, 17)));
			// activities.add(new MyActivity("Virginie", getDate(2008, 8, 15,
			// 0),
			// getDate(2008, 8, 15, 23)));
		}

		public List<Activity> getActivities(Date dateMin, Date dateMax) {
			Calendar cMin = Calendar.getInstance();
			cMin.setTime(dateMin);
			Calendar cMax = Calendar.getInstance();
			cMax.setTime(dateMax);

			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();

			List<Activity> res = new ArrayList<Activity>();
			if (activities != null) {
				for (Activity activity : activities) {
					c1.setTime(activity.getDateMin());
					c2.setTime(activity.getDateMax());
					if (((c1.after(cMin) || c1.equals(cMin)) && (c1
							.before(cMax) || c1.equals(cMax)))
							|| ((c2.after(cMin) || c2.equals(cMin)) && (c2
									.before(cMax) || c2.equals(cMax)))
							|| (c1.before(cMin) && c2.after(cMax))) {
						res.add(activity);
					}
				}
			}
			return activities;
		}

		private Date getDate(int y, int m, int d, int h) {
			Calendar c = Calendar.getInstance();
			c.set(y, m, d, h, 00);
			return c.getTime();
		}

		private Date getDate(int y, int m, int d, int h, int mm) {
			Calendar c = Calendar.getInstance();
			c.set(y, m, d, h, mm);
			return c.getTime();
		}

		private class MyActivity implements Activity {

			String title;

			Date min, max;

			public MyActivity(String title, Date min, Date max) {
				this.min = min;
				this.max = max;
				this.title = title;
			}

			public Date getDateMax() {
				Calendar c = Calendar.getInstance();
				c.set(2007, 6, 31, 17, 00);
				return max;
			}

			public Date getDateMin() {
				Calendar c = Calendar.getInstance();
				c.set(2007, 6, 27, 17, 00);
				return min;
			}

			public String getTitle() {
				return title;
			}

			public Color getColor() {
				return Color.RED;
			}
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JPlanning planning = new JPlanning(new MyPlanningModel());
		planning.setMode(JPlanning.Mode.MONTH);
		frame.getContentPane().add(planning, BorderLayout.CENTER);

		frame.setTitle("TestPlanning");
		frame.pack();
		// frame.setSize(300, 400);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
