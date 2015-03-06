import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.roulement.AbstractRoulementTask;
import com.synaptix.swing.roulement.AbstractRoulementTimelineModel;
import com.synaptix.swing.roulement.AbstractRoulementTimelineTaskRenderer;
import com.synaptix.swing.roulement.DefaultRoulementTimelineDayDatesHeaderRenderer;
import com.synaptix.swing.roulement.JRoulementTimeline;
import com.synaptix.swing.roulement.RoulementTask;
import com.synaptix.swing.roulement.RoulementTimelineDrag;
import com.synaptix.swing.roulement.RoulementTimelineDrop;
import com.synaptix.swing.roulement.RoulementTimelineGroupTask;
import com.synaptix.swing.roulement.RoulementTimelineSelectionModel;
import com.synaptix.swing.roulement.RoulementTimelineTaskRenderer;
import com.synaptix.swing.roulement.event.RoulementTimelineListener;
import com.synaptix.swing.utils.SynaptixTransferHandler;

public class MainRoulementTimeline {

	private static int nbDays = 3;

	private static Random random = new Random(0);

	private static class MyRoulementTimelineModel extends AbstractRoulementTimelineModel {

		private Map<Integer, List<MyRoulementTask>> map;

		public MyRoulementTimelineModel() {
			super();
			map = new HashMap<Integer, List<MyRoulementTask>>();

			DayDate cMin = new DayDate();
			DayDate cMax = new DayDate();
			cMax.addDay(nbDays);

			for (int i = 0; i < getResourceCount(); i++) {
				List<MyRoulementTask> res = new ArrayList<MyRoulementTask>();
				for (int ordre = 0; ordre < 5; ordre++) {
					res.addAll(createRandomTasks(random.nextInt(2), cMin, cMax, ordre));
				}
				map.put(i, res);
			}
		}

		public int getResourceCount() {
			return 10;
		}

		public String getResourceName(int resource) {
			return "Gaby : " + resource;
		}

		public List<RoulementTask> getTasks(int resource, DayDate dayDateMin, DayDate dayDateMax) {
			List<RoulementTask> tasks = new ArrayList<RoulementTask>();
			for (MyRoulementTask st : map.get(resource)) {
				if (!st.getDayDateMin().after(dayDateMax) && !st.getDayDateMax().before(dayDateMin)) {
					tasks.add(st);
				}
			}
			return tasks;
		}

		public List<MyRoulementTask> getTasks(int resource) {
			return map.get(resource);
		}

		public void update() {
			fireStructureChanged();
		}
	}

	private static DayDate createRandomdayDate(DayDate dayDateMin, DayDate dayDateMax) {
		DayDate c = new DayDate();
		c.setDayDate(dayDateMin);

		long mm = dayDateMax.getTimeInMinutes() - dayDateMin.getTimeInMinutes();
		long mm2 = c.getTimeInMinutes() + (long) (random.nextDouble() * mm);
		c.setTimeInMinutes(mm2);
		return c;
	}

	private static List<MyRoulementTask> createRandomTasks(int nb, DayDate dayDateMin, DayDate dayDateMax, int ordre) {
		List<MyRoulementTask> tasks = new ArrayList<MyRoulementTask>();
		for (int i = 0; i < nb; i++) {
			DayDate d1 = createRandomdayDate(dayDateMin, dayDateMax);
			DayDate c = new DayDate();
			c.setDayDate(d1);
			c.addDay(1);

			DayDate d2 = createRandomdayDate(d1, c);
			tasks.add(new MyRoulementTask(d1, d2, ordre));
		}
		return tasks;
	}

	static int id = 0;

	public static int nextId() {
		return id++;
	}

	private static final class MyRoulementTask extends AbstractRoulementTask {

		public DayDate min, max;

		private int ordre;

		private int num;

		private int group;

		public MyRoulementTask(DayDate min, DayDate max, int ordre) {
			this.min = min;
			this.max = max;
			this.ordre = ordre;

			num = nextId();
		}

		public DayDate getDayDateMax() {
			return max;
		}

		public DayDate getDayDateMin() {
			return min;
		}

		public RoulementTimelineTaskRenderer getTaskRenderer() {
			return new MyTaskRenderer();
		}

		public boolean isNoClipping() {
			return false;
		}

		public int getOrdre() {
			return ordre;
		}

		public boolean isSelectable() {
			return true;
		}

		public boolean equals(Object o) {
			if (o != null && o instanceof MyRoulementTask) {
				MyRoulementTask o2 = (MyRoulementTask) o;
				return num == o2.num;
			}
			return super.equals(o);
		}

		public int getNum() {
			return num;
		}

		public String toString() {
			return "Task " + num + " ordre " + ordre;
		}
	}

	private static final class MyTaskRenderer extends AbstractRoulementTimelineTaskRenderer {

		public String getToolTipText(Rectangle rect, JRoulementTimeline roulementTimeline, RoulementTask task, int resource, DayDate dayDate, Point point) {
			MyRoulementTask t = (MyRoulementTask) task;
			return t.getNum() + " " + dayDate + " " + point;
		}

		public void paintTask(Graphics g, Rectangle rect, JRoulementTimeline roulementTimeline, RoulementTask task, boolean isSelected, boolean hasFocus, int ressource) {
			Graphics2D g2 = (Graphics2D) g.create();

			FontMetrics fm = g2.getFontMetrics();

			if (isSelected) {
				if (task.getOrdre() == 0) {
					g2.setColor(Color.BLUE);
				} else {
					g2.setColor(Color.YELLOW);
				}
			} else {
				if (task.getOrdre() == 0) {
					g2.setColor(new Color(0, 255, 0, 128));
				} else {
					g2.setColor(new Color(0, 128, 128, 128));
				}
			}
			g2.fillRect(rect.x, rect.y + 5, rect.width, rect.height - 10);

			g2.setColor(Color.WHITE);
			g2.drawString(task.toString(), rect.x + 2, rect.y + rect.height / 2 + fm.getDescent());

			g2.setColor(Color.BLACK);
			g2.drawRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

			// g2.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height);
			// g2.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width
			// - 1, rect.y + rect.height);

			g2.dispose();
		}
	}

	private static final class MyRoulementTimelineDayDatesHeaderRenderer extends DefaultRoulementTimelineDayDatesHeaderRenderer {

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
				flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Hours.class.getName());
				FLAVORS[0] = flavor;
			} catch (ClassNotFoundException e) {
				FLAVORS[0] = null;
			}
			HOURS_FLAVOR = flavor;
		}

		public HoursTransferable(Hours hours) {
			this.hours = hours;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
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
				flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=gaby");
				FLAVORS[0] = flavor;
			} catch (ClassNotFoundException e) {
				FLAVORS[0] = null;
			}
			DAY_FLAVOR = flavor;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
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

	private static final class LabelTransferHandler extends SynaptixTransferHandler {

		private static final long serialVersionUID = 6560212511433794019L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			return new HoursTransferable(new Hours(0, 12 * 60));
		}
	}

	private static final class MyRoulementTimelineDrop implements RoulementTimelineDrop {

		public boolean canDrop(Transferable transferable) {
			if (transferable != null && transferable.isDataFlavorSupported(HoursTransferable.HOURS_FLAVOR)) {
				return true;
			}
			return false;
		}

		public RoulementTask[] getTask(Transferable transferable, int resource, DayDate dayDate) {
			RoulementTask[] tasks = null;
			Hours hours = null;
			try {
				hours = (Hours) transferable.getTransferData(HoursTransferable.HOURS_FLAVOR);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (hours != null) {
				RoulementTask task1 = createTaskByHours(dayDate, hours);
				tasks = new RoulementTask[] { task1 };
			}
			return tasks;
		}

		public void done(Transferable transferable, int resource, DayDate dayDate) {
		}

		private RoulementTask createTaskByHours(DayDate dayDate, Hours hours) {
			DayDate dayDateMin = new DayDate(dayDate);
			dayDateMin.addMinute(hours.getMinuteStart());

			DayDate dayDateMax = new DayDate(dayDate);
			dayDateMax.addMinute(hours.getMinuteEnd());
			return new MyRoulementTask(dayDateMin, dayDateMax, 0);
		}
	}

	private static final class MyRoulementTimelineDrag implements RoulementTimelineDrag {

		public Transferable createTransferable(JRoulementTimeline roulementTimeline) {
			RoulementTimelineSelectionModel selectionModel = roulementTimeline.getSelectionModel();
			if (selectionModel.getSelectionTaskCount() == 1) {
				RoulementTask task = selectionModel.getSelectionTasks(selectionModel.getMinSelectionIndexResource())[0];
				return new HoursTransferable(new Hours(5 * 60, 15 * 60));
			}
			return null;
		}

		@Override
		public void exportDone(JRoulementTimeline roulementTimeline, Transferable transferable) {
			System.out.println("exportDone");
		}
	}

	private static final class MyGroupTask implements RoulementTimelineGroupTask {

		private DayDate dayDateMin;

		private DayDate dayDateMax;

		private List<RoulementTask> taskList;

		public MyGroupTask(List<RoulementTask> taskList) {
			this.taskList = taskList;
			dayDateMin = null;
			dayDateMax = null;
			for (RoulementTask task : taskList) {
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

		public List<RoulementTask> getRoulementTaskList() {
			return taskList;
		}
	}

	static RoulementTimelineListener l1;

	static RoulementTimelineListener l2;

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new JMenuItem("Gaby"));

		final MyRoulementTimelineModel model = new MyRoulementTimelineModel();

		final JRoulementTimeline roulementTimeline1 = new JRoulementTimeline(model);
		roulementTimeline1.setDropMode(JRoulementTimeline.DropMode.FILL_GHOST);
		roulementTimeline1.setRoulementTimelineDrop(new MyRoulementTimelineDrop());
		roulementTimeline1.setRoulementTimelineDrag(new MyRoulementTimelineDrag());
		roulementTimeline1.getSelectionModel().setSelectionMode(RoulementTimelineSelectionModel.Mode.MULTIPLE_SELECTION);
		roulementTimeline1.setHeaderRenderer(new MyRoulementTimelineDayDatesHeaderRenderer());
		roulementTimeline1.setShowIntersection(true);
		roulementTimeline1.setMultiLine(true);
		roulementTimeline1.setAutoHeight(true);

		roulementTimeline1.setNbDays(nbDays);
		roulementTimeline1.setCenterPopupMenu(popupMenu);
		roulementTimeline1.setName("1");
		roulementTimeline1.setMinDayWidth(100);
		roulementTimeline1.setMaxDayWidth(3000);
		roulementTimeline1.setDayCycle(7);
		roulementTimeline1.getLeftResourcesHeader().setReorderingAllowed(false);
		roulementTimeline1.setDefaultResourceHeight(100);

		roulementTimeline1.setDynamicDayDatesHeader(true);

		roulementTimeline1.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "doCopy");
		roulementTimeline1.getActionMap().put("doCopy", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Copie coller");
			}
		});

		frame.getContentPane().add(roulementTimeline1, BorderLayout.CENTER);

		frame.setTitle("RoulementTimeline");
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
