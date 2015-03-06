import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.AbstractSimpleTask;
import com.synaptix.swing.AbstractSimpleTimelineModel;
import com.synaptix.swing.JSimpleTimeline;
import com.synaptix.swing.SimpleTask;
import com.synaptix.swing.simpletimeline.SimpleTimelineDrag;
import com.synaptix.swing.simpletimeline.SimpleTimelineDrop;
import com.synaptix.swing.simpletimeline.SimpleTimelineSelectionModel;
import com.synaptix.swing.simpletimeline.SimpleTimelineTaskRenderer;
import com.synaptix.swing.utils.DateTimeUtils;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SynaptixTransferHandler;

public class TestSimpleTimeline {

	private static Random random = new Random(0);

	private static class MySimpleTimelineModel extends
			AbstractSimpleTimelineModel {

		private Map<Integer, List<SimpleTask>> map;

		public MySimpleTimelineModel() {
			super();
			map = new HashMap<Integer, List<SimpleTask>>();

			Calendar cMin = Calendar.getInstance();
			cMin.add(Calendar.DAY_OF_YEAR, -2);
			Calendar cMax = Calendar.getInstance();
			cMax.add(Calendar.DAY_OF_YEAR, 3);

			for (int i = 0; i < 35; i++) {
				if (random.nextInt(2) == 1) {
					map.put(i, createRandomTasks(random.nextInt(5), cMin
							.getTime(), cMax.getTime()));
				}
			}
		}

		public int getResourceCount() {
			return 5;
		}

		public String getResourceName(int resource) {
			return "Gaby : " + resource;
		}

		public List<SimpleTask> getTasks(int resource, Date dateMin,
				Date dateMax) {
			return map.get(resource);
		}
	}

	private static Date createRandomDate(Date dateMin, Date dateMax) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateMin);

		long mm = DateTimeUtils.getNumberOfMilliseconds(dateMin, dateMax);
		long mm2 = c.getTimeInMillis() + (long) (random.nextDouble() * mm);

		c.setTimeInMillis(mm2);

		return c.getTime();
	}

	private static List<SimpleTask> createRandomTasks(int nb, Date dateMin,
			Date dateMax) {
		List<SimpleTask> tasks = new ArrayList<SimpleTask>();
		for (int i = 0; i < nb; i++) {
			Date d1 = createRandomDate(dateMin, dateMax);
			Calendar c = Calendar.getInstance();
			c.setTime(d1);
			c.add(Calendar.DAY_OF_YEAR, 1);

			Date d2 = createRandomDate(d1, c.getTime());

			tasks.add(new MySimpleTask(d1, d2));
		}
		return tasks;
	}

	private static final class MySimpleTask extends AbstractSimpleTask {

		public Date min, max;

		public MySimpleTask(Date min, Date max) {
			this.min = min;
			this.max = max;
		}

		public Date getDateMax() {
			return max;
		}

		public Date getDateMin() {
			return min;
		}

		public SimpleTimelineTaskRenderer getTaskRenderer() {
			return new MyTaskRenderer();
		}
	}

	private static final class MyTaskRenderer implements
			SimpleTimelineTaskRenderer {

		public void paintTask(Graphics g, Rectangle rect,
				JSimpleTimeline simpleTimeline, SimpleTask task,
				boolean isSelected, boolean hasFocus, int ressource) {
			Graphics2D g2 = (Graphics2D) g.create();

			FontMetrics fm = g2.getFontMetrics();

			if (isSelected) {
				g2.setColor(Color.BLUE);
			} else {
				g2.setColor(Color.GREEN);
			}
			g2.fillRect(rect.x, rect.y + 5, rect.width, rect.height - 10);

			g2.setColor(Color.WHITE);
			g2.drawString("Rien...", rect.x + 2, rect.y + rect.height / 2
					+ fm.getDescent());

			g2.setColor(Color.BLACK);
			g2.drawRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

			// g2.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height);
			// g2.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width
			// - 1, rect.y + rect.height);

			g2.dispose();
		}
	}

	private static final class Hours {

		private int minuteStart;

		private int minuteEnd;

		public Hours(int minuteStart, int minuteEnd) {
			super();
			this.minuteStart = minuteStart;
			this.minuteEnd = minuteEnd;
		}

		public int getMinuteStart() {
			return minuteStart;
		}

		public int getMinuteEnd() {
			return minuteEnd;
		}
	}

	private static final class HoursTransferable implements Transferable {

		public final static DataFlavor HOURS_FLAVOR;

		private final static DataFlavor[] FLAVORS;

		private Hours hours;

		static {
			DataFlavor flavor = null;
			FLAVORS = new DataFlavor[1];
			try {
				flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
						+ ";class=" + Hours.class.getName());
				FLAVORS[0] = flavor;
			} catch (ClassNotFoundException e) {
				FLAVORS[0] = null;
			}
			HOURS_FLAVOR = flavor;
		}

		public HoursTransferable(Hours hours) {
			this.hours = hours;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!flavor.equals(HOURS_FLAVOR)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return hours;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return FLAVORS;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(HOURS_FLAVOR);
		}
	}

	private static final class DayTransferable implements Transferable {

		public final static DataFlavor DAY_FLAVOR;

		private final static DataFlavor[] FLAVORS;

		static {
			DataFlavor flavor = null;
			FLAVORS = new DataFlavor[1];
			try {
				flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
						+ ";class=gaby");
				FLAVORS[0] = flavor;
			} catch (ClassNotFoundException e) {
				FLAVORS[0] = null;
			}
			DAY_FLAVOR = flavor;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!flavor.equals(DAY_FLAVOR)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return 0;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return FLAVORS;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(DAY_FLAVOR);
		}
	}

	private static final class LabelTransferHandler extends
			SynaptixTransferHandler {

		private static final long serialVersionUID = 6560212511433794019L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			return new HoursTransferable(new Hours(0, 12 * 60));
		}
	}

	private static final class MySimpleTimelineDrop implements
			SimpleTimelineDrop {

		public boolean canDrop(Transferable transferable) {
			if (transferable != null
					&& transferable
							.isDataFlavorSupported(HoursTransferable.HOURS_FLAVOR)) {
				return true;
			}
			return false;
		}

		public SimpleTask[] getTask(Transferable transferable, int resource,
				Date date) {
			SimpleTask[] tasks = null;
			Hours hours = null;
			try {
				hours = (Hours) transferable
						.getTransferData(HoursTransferable.HOURS_FLAVOR);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (hours != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				SimpleTask task1 = createTaskByHours(c.getTime(), hours);

				// c.add(Calendar.DAY_OF_YEAR, 1);
				SimpleTask task2 = createTaskByHours(c.getTime(), new Hours(
						15 * 60 + 15, 17 * 60 + 30));

				tasks = new SimpleTask[] { task1, task2 };
			}
			return tasks;
		}

		public void done(Transferable transferable, int resource, Date date) {
		}

		private SimpleTask createTaskByHours(Date date, Hours hours) {
			Date dateMin = DateTimeUtils.createDateAndHour(date, hours
					.getMinuteStart());
			Date dateMax = DateTimeUtils.createDateAndHour(date, hours
					.getMinuteEnd());
			return new MySimpleTask(dateMin, dateMax);
		}
	}

	private static final class MySimpleTimelineDrag implements
			SimpleTimelineDrag {

		public Transferable createTransferable(JSimpleTimeline simpleTimeline) {
			SimpleTimelineSelectionModel selectionModel = simpleTimeline
					.getSelectionModel();
			if (selectionModel.getSelectionTaskCount() == 1) {
				SimpleTask task = selectionModel
						.getSelectionTasks(selectionModel
								.getMinSelectionIndexResource())[0];
				return new HoursTransferable(new Hours(5 * 60, 15 * 60));
			}
			return null;
		}

		@Override
		public void exportDone(JSimpleTimeline simpleTimeline,
				Transferable transferable) {
			System.out.println("exportDone");
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JTable table = new JTable(new Object[][] { { "drag me" } },
				new Object[] { "Test1" });
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDragEnabled(true);
		table.setTransferHandler(new LabelTransferHandler());

		final JSimpleTimeline simpleTimeline = new JSimpleTimeline(
				new MySimpleTimelineModel());
		simpleTimeline.setDropMode(JSimpleTimeline.DropMode.FILL_GHOST);
		simpleTimeline.setSimpleTimelineDrop(new MySimpleTimelineDrop());
		simpleTimeline.setSimpleTimelineDrag(new MySimpleTimelineDrag());
		simpleTimeline.getSelectionModel().setSelectionMode(
				SimpleTimelineSelectionModel.Mode.SINGLE_SELECTION);

		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(2, 0, 40,
				1));
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				simpleTimeline.setNbAfterDays((Integer) spinner.getValue());
			}
		});

		FormLayout layout = new FormLayout(
				"FILL:600DLU:GROW(1.0)",
				"FILL:DEFAULT:NONE,CENTER:3DLU:NONE,FILL:400DLU:GROW(1.0),CENTER:3DLU:NONE,FILL:100DLU:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(spinner, cc.xy(1, 1));
		builder.add(simpleTimeline, cc.xy(1, 3));
		builder.add(new JScrollPane(table), cc.xy(1, 5));

		frame.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

		frame.setTitle("SimpleTimeline");
		frame.pack();
		// frame.setSize(300, 400);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
				// timeline.centerAtCurrentDay();
			}
		});
	}
}
