package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.event.PlanningDataEvent;
import com.synaptix.swing.event.PlanningDataListener;
import com.synaptix.swing.utils.DateTimeUtils;

public class JPlanning extends JPanel implements PlanningDataListener {

	private static final long serialVersionUID = 8104079686631317447L;

	public enum Mode {
		WEEK, MONTH, YEAR
	}

	private static final Locale locale;

	private static final Color nameDayForegoundColor = new Color(0x152BD5);

	private static final Color daysBorderColor = new Color(0xC3D9FF);

	private static final Color dayBorderColor = new Color(0xCCDDEE);

	private static final Color numberDayBackgroundColor = new Color(0xE8EEF7);

	private static final Color numberDayNowBackgroundColor = new Color(0xBBCCDD);

	private static final Color numberDayForegroundColor = new Color(0x6A6A6B);

	private static final Color numberDayDiffMonthForegroundColor = new Color(
			0xBABDC4);

	private static final Color numberHourForegroundColor = new Color(0x446688);

	private static final Color hourBorderColor = new Color(0xDDDDDD);

	private static final Color daySelectedBackgroundColor = new Color(0xffe79c);

	private static final Color dayBackgroundColor = new Color(0xFFFFFF);

	private static final Color dayNowBackgroundColor = new Color(0xdfeaf4);

	// private static final Color activityBackgroundColor = new Color(0xD96666);

	// private static final Color activitySelectedBackgroundColor = new
	// Color(0x66D966);

	private static final Color activityForegroundColor = new Color(0xFFFFFF);

	private static final Color activitySelectedForegroundColor = new Color(
			0xFFFFFF);

	private static final Font allDaysMonthFont = new Font("arial", Font.PLAIN, //$NON-NLS-1$
			11);

	private static final Font dayMonthFont = new Font("arial", Font.PLAIN, 10); //$NON-NLS-1$

	private static final int nameWeekHeightMonth = 24;

	private static final int nameDayHeightWeek = 24;

	private static final int nameHourWidthWeek = 48;

	private static final int activityHeightMonth = 17;

	private static final String[] listMonthNames;

	// private static final String[] listShortMonthNames;

	private static final String[] listDayNames;

	private static final String[] listShortDayNames;

	private static final DateFormat weekDateFormat = new SimpleDateFormat(
			"dd MMM yyyy"); //$NON-NLS-1$

	private PlanningModel dataModel;

	private JInternalPlanningPanel internalPlanningPanel;

	private Action previousAction;

	private Action nextAction;

	private JLabel label;

	private Mode mode;

	private Action weekAction;

	private Action monthAction;

	private Action yearAction;

	private Calendar currentDate;

	private Calendar selectedDate;

	private Activity selectedActivity;

	static {
		locale = Locale.getDefault();

		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		listMonthNames = dateFormatSymbols.getMonths();
		listDayNames = dateFormatSymbols.getWeekdays();
		// listShortMonthNames = dateFormatSymbols.getShortMonths();
		listShortDayNames = dateFormatSymbols.getShortWeekdays();
	}

	public JPlanning(PlanningModel dataModel) {
		super(new BorderLayout());

		this.dataModel = dataModel;
		dataModel.addPlanningDataListener(this);

		currentDate = Calendar.getInstance();
		currentDate.setTime(new Date());

		selectedDate = null;
		selectedActivity = null;

		mode = Mode.MONTH;

		initActions();
		initComponents();

		updateLabel();

		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
		previousAction = new PreviousAction();
		nextAction = new NextAction();

		weekAction = new WeekAction();
		// weekAction.setEnabled(false);
		monthAction = new MonthAction();
		monthAction.putValue(Action.SELECTED_KEY, true);
		yearAction = new YearAction();
		yearAction.setEnabled(false);
	}

	private void createComponents() {
		label = new JLabel("", JLabel.CENTER); //$NON-NLS-1$
		internalPlanningPanel = new JInternalPlanningPanel();
	}

	private void initComponents() {
		createComponents();

		label.setFont(new Font("arial", Font.BOLD, 20)); //$NON-NLS-1$
		label.setBorder(BorderFactory.createEtchedBorder());

		internalPlanningPanel.setBorder(BorderFactory.createEtchedBorder());
	}

	public JComponent getInternalPlanningPanel() {
		return internalPlanningPanel;
	}

	private JComponent buildChooseModePanel() {
		ButtonGroup buttonGroup = new ButtonGroup();
		JToggleButton tb1 = new JToggleButton(weekAction);
		buttonGroup.add(tb1);
		JToggleButton tb2 = new JToggleButton(monthAction);
		buttonGroup.add(tb2);
		JToggleButton tb3 = new JToggleButton(yearAction);
		buttonGroup.add(tb3);

		FormLayout layout = new FormLayout(
				"FILL:50DLU:GROW(1.0),FILL:50DLU:GROW(1.0),FILL:50DLU:GROW(1.0)", //$NON-NLS-1$
				"FILL:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(tb1, cc.xy(1, 1));
		builder.add(tb2, cc.xy(2, 1));
		builder.add(tb3, cc.xy(3, 1));

		JPanel panel = builder.getPanel();
		panel.setBorder(BorderFactory.createEtchedBorder());
		return panel;
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:250DLU:GROW(1.0),FILL:DEFAULT:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:250DLU:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new JButton(previousAction), cc.xy(1, 2));
		builder.add(new JButton(nextAction), cc.xy(3, 2));
		builder.add(internalPlanningPanel, cc.xyw(1, 3, 3));
		builder.add(label, cc.xy(2, 2));
		builder.add(buildChooseModePanel(), cc.xywh(1, 1, 3, 1,
				CellConstraints.FILL, CellConstraints.DEFAULT));
		return builder.getPanel();
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;

		updateLabel();
		updateButtons();
	}

	public Date getCurrentDate() {
		return currentDate.getTime();
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate.setTime(currentDate);

		updateLabel();
	}

	public void setSelectedDate(Date selectedDate) {
		if (selectedDate != null) {
			this.selectedDate = Calendar.getInstance();
			this.selectedDate.setTime(selectedDate);
		} else {
			this.selectedDate = null;
		}

		setCurrentDate(selectedDate);
	}

	public Date getSelectedDate() {
		if (selectedDate != null) {
			return selectedDate.getTime();
		}
		return null;
	}

	public void clearSelection() {
		this.selectedDate = null;
		this.selectedActivity = null;
		internalPlanningPanel.repaint();
	}

	public Activity getSelectedActivity() {
		return selectedActivity;
	}

	public void setSelectedActivity(Activity selectedActivity) {
		this.selectedActivity = selectedActivity;
		internalPlanningPanel.repaint();
	}

	public void contentsChanged(PlanningDataEvent e) {
		internalPlanningPanel.repaint();
	}

	private void updateLabel() {
		int m = currentDate.get(Calendar.MONTH);
		int y = currentDate.get(Calendar.YEAR);

		StringBuilder sb = new StringBuilder();

		switch (mode) {
		case WEEK:
			sb.append(weekDateFormat.format(getDateMin()));
			sb.append(" - "); //$NON-NLS-1$

			Calendar c = Calendar.getInstance();
			c.setTime(getDateMax());
			c.add(Calendar.MINUTE, -1);

			sb.append(weekDateFormat.format(c.getTime()));

			previousAction.putValue(Action.SHORT_DESCRIPTION, SwingMessages
					.getString("JPlanning.10")); //$NON-NLS-1$
			nextAction.putValue(Action.SHORT_DESCRIPTION, SwingMessages
					.getString("JPlanning.11")); //$NON-NLS-1$
			break;
		case MONTH:
			sb.append(listMonthNames[m].substring(0, 1).toUpperCase());
			sb.append(listMonthNames[m].substring(1));
			sb.append(" "); //$NON-NLS-1$
			sb.append(y);

			previousAction.putValue(Action.SHORT_DESCRIPTION, SwingMessages
					.getString("JPlanning.13")); //$NON-NLS-1$
			nextAction.putValue(Action.SHORT_DESCRIPTION, SwingMessages
					.getString("JPlanning.14")); //$NON-NLS-1$
			break;
		case YEAR:
			sb.append(y);

			previousAction.putValue(Action.SHORT_DESCRIPTION, SwingMessages
					.getString("JPlanning.15")); //$NON-NLS-1$
			nextAction.putValue(Action.SHORT_DESCRIPTION, SwingMessages
					.getString("JPlanning.16")); //$NON-NLS-1$
			break;
		}

		label.setText(sb.toString());

		internalPlanningPanel.repaint();
	}

	private void updateButtons() {
		switch (mode) {
		case WEEK:
			weekAction.putValue(Action.SELECTED_KEY, true);
			break;
		case MONTH:
			monthAction.putValue(Action.SELECTED_KEY, true);
			break;
		case YEAR:
			yearAction.putValue(Action.SELECTED_KEY, true);
			break;
		}
	}

	private Date getDateMinMonth() {
		Calendar c = (Calendar) currentDate.clone();
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		int firstDay = Calendar.getInstance().getFirstDayOfWeek();
		int dw = (c.get(Calendar.DAY_OF_WEEK) + (7 - firstDay)) % 7;
		c.add(Calendar.DAY_OF_MONTH, -dw);
		return c.getTime();
	}

	private Date getDateMinYear() {
		Calendar c = (Calendar) currentDate.clone();
		c.set(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	private Date getDateMinWeek() {
		Calendar c = (Calendar) currentDate.clone();
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public Date getDateMin() {
		Date dateMin = null;
		switch (mode) {
		case WEEK:
			dateMin = getDateMinWeek();
			break;
		case MONTH:
			dateMin = getDateMinMonth();
			break;
		case YEAR:
			dateMin = getDateMinYear();
			break;
		}
		return dateMin;
	}

	private Date getDateMaxMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(getDateMin());
		c.add(Calendar.DAY_OF_MONTH, 7 * 6);
		return c.getTime();
	}

	private Date getDateMaxYear() {
		Calendar c = Calendar.getInstance();
		c.setTime(getDateMin());
		c.add(Calendar.YEAR, 1);
		return c.getTime();
	}

	private Date getDateMaxWeek() {
		Calendar c = Calendar.getInstance();
		c.setTime(getDateMin());
		c.add(Calendar.DAY_OF_WEEK, 7);
		return c.getTime();
	}

	public Date getDateMax() {
		Date dateMin = null;
		switch (mode) {
		case WEEK:
			dateMin = getDateMaxWeek();
			break;
		case MONTH:
			dateMin = getDateMaxMonth();
			break;
		case YEAR:
			dateMin = getDateMaxYear();
			break;
		}
		return dateMin;
	}

	private class PreviousAction extends AbstractAction {

		private static final long serialVersionUID = -9057454762349142768L;

		public PreviousAction() {
			super("<"); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			switch (mode) {
			case WEEK:
				currentDate.add(Calendar.WEEK_OF_YEAR, -1);
				break;
			case MONTH:
				currentDate.add(Calendar.MONTH, -1);
				break;
			case YEAR:
				currentDate.add(Calendar.YEAR, -1);
				break;
			}
			updateLabel();
		}
	}

	private class NextAction extends AbstractAction {

		private static final long serialVersionUID = 1409637852042363429L;

		public NextAction() {
			super(">"); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			switch (mode) {
			case WEEK:
				currentDate.add(Calendar.WEEK_OF_YEAR, 1);
				break;
			case MONTH:
				currentDate.add(Calendar.MONTH, 1);
				break;
			case YEAR:
				currentDate.add(Calendar.YEAR, 1);
				break;
			}
			updateLabel();
		}
	}

	private class WeekAction extends AbstractAction {

		private static final long serialVersionUID = -9057454762349142768L;

		public WeekAction() {
			super(SwingMessages.getString("JPlanning.19")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			mode = Mode.WEEK;

			updateLabel();
		}
	}

	private class MonthAction extends AbstractAction {

		private static final long serialVersionUID = -9057454762349142768L;

		public MonthAction() {
			super(SwingMessages.getString("JPlanning.20")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			mode = Mode.MONTH;

			updateLabel();
		}
	}

	private class YearAction extends AbstractAction {

		private static final long serialVersionUID = -9057454762349142768L;

		public YearAction() {
			super(SwingMessages.getString("JPlanning.21")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			mode = Mode.YEAR;

			updateLabel();
		}
	}

	private class JInternalPlanningPanel extends JComponent {

		private static final long serialVersionUID = 3718678384544789926L;

		public JInternalPlanningPanel() {
			super();

			this.addMouseListener(new MyMouseListener());
			this.setPreferredSize(new Dimension(500, 500));
		}

		public SelectedResponse selectedPoint(Point point) {
			SelectedResponse res = null;
			switch (mode) {
			case WEEK:
				res = selectedPointWeek(point);
				break;
			case MONTH:
				res = selectedPointMonth(point);
				break;
			case YEAR:
				res = selectedPointYear(point);
				break;
			}
			return res;
		}

		private SelectedResponse selectedPointMonth(Point point) {
			Insets insets = this.getInsets();

			Rectangle rect = getWeeksMonthRectangle(insets, this.getWidth(),
					this.getHeight());

			Date selectedDate = null;
			Activity selectedActivity = null;
			boolean selectedPlus = false;
			List<Activity> plusActivities = null;

			Calendar current = Calendar.getInstance();
			current.setTime(getDateMin());

			Calendar today = Calendar.getInstance();
			today.setTime(new Date());

			List<Activity> activities = dataModel.getActivities(current
					.getTime(), getDateMax());

			int x = rect.x;
			int y = rect.y;
			for (int j = 0; j < 6; j++) {
				int dayHeight = rect.height / 6;

				if (j == 5) {
					dayHeight = rect.height - (dayHeight * 5);
				}

				for (int i = 0; i < 7; i++) {
					int dayWidth = rect.width / 7;

					if (i == 6) {
						dayWidth = rect.width - dayWidth * 6;
					}

					if (point.x >= x && point.x < x + dayWidth && point.y >= y
							&& point.y < y + dayHeight) {
						selectedDate = current.getTime();
					}

					if (activities != null && !activities.isEmpty()) {
						Calendar cMax = Calendar.getInstance();
						cMax.setTime(current.getTime());
						cMax.add(Calendar.DAY_OF_YEAR, 1);
						cMax.add(Calendar.MINUTE, -1);

						List<Activity> possibles = new ArrayList<Activity>();
						for (Activity activity : activities) {
							if (DateTimeUtils.includeDates(activity
									.getDateMin(), activity.getDateMax(),
									current.getTime(), cMax.getTime())) {
								possibles.add(activity);
							}
						}
						if (!possibles.isEmpty()) {
							Collections.sort(possibles,
									new ActivityComparator());

							Rectangle rect2 = new Rectangle(x + 1, y + 1 + 20,
									dayWidth - 2, dayHeight - 22);

							if (rect2.contains(point)) {
								int dy = point.y - rect2.y;

								int max = possibles.size();
								int a = rect2.height / activityHeightMonth;
								int b = dy / activityHeightMonth;
								if (a < possibles.size()) {
									max = a - 1;
								}

								if (b < max) {
									selectedActivity = possibles.get(b);
								} else if (max != possibles.size()) {
									selectedPlus = true;

									plusActivities = new ArrayList<Activity>();
									for (int index = max; index < possibles
											.size(); index++) {
										plusActivities
												.add(possibles.get(index));
									}
								}
							}
						}
					}

					current.add(Calendar.DAY_OF_MONTH, 1);

					x += dayWidth;
				}
				y += dayHeight;
				x = rect.x;
			}

			SelectedResponse res = new SelectedResponse();
			res.selectedDate = selectedDate;
			res.selectedActivity = selectedActivity;
			res.selectedPlus = selectedPlus;
			res.plusActivities = plusActivities;

			return res;
		}

		private SelectedResponse selectedPointYear(Point point) {
			Date date = null;
			Activity selActivity = null;

			int width = this.getWidth();
			int height = this.getHeight();

			int wDay = 24;
			int w = (width - wDay) / 12;
			int hMonth = 24;
			int h = (height - hMonth) / 31;

			Calendar c = Calendar.getInstance();
			c.setTime(getDateMin());

			List<Activity> activities = dataModel.getActivities(c.getTime(),
					getDateMax());

			// Days
			for (int i = 0; i < 12; i++) {
				int maxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				for (int j = 0; j < maxDays; j++) {
					int x = i * w + wDay;
					int y = j * h + hMonth;

					if (point.x >= x && point.x < x + w && point.y >= y
							&& point.y < y + h) {
						date = c.getTime();
					}

					int k = 0;
					int wActivity = (w - 8) / 5;
					for (Activity activity : activities) {
						Calendar cMin = Calendar.getInstance();
						cMin.setTime(activity.getDateMin());
						cMin.set(Calendar.HOUR_OF_DAY, 0);
						cMin.set(Calendar.MINUTE, 0);
						cMin.set(Calendar.SECOND, 0);
						cMin.set(Calendar.MILLISECOND, 0);

						Calendar cMax = Calendar.getInstance();
						cMax.setTime(activity.getDateMax());
						cMax.set(Calendar.HOUR_OF_DAY, 0);
						cMax.set(Calendar.MINUTE, 0);
						cMax.set(Calendar.SECOND, 0);
						cMax.set(Calendar.MILLISECOND, 0);

						if (c.getTimeInMillis() >= cMin.getTimeInMillis()
								&& c.getTimeInMillis() <= cMax
										.getTimeInMillis()) {
							int xA = x + k * wActivity + 2;
							int yA = y + 2;
							int wA = wActivity - 1;
							int hA = h - 4;

							if (point.x >= xA && point.x < xA + wA
									&& point.y >= yA && point.y < yA + hA) {
								selActivity = activity;
							}

							k++;
						}
					}

					c.add(Calendar.DAY_OF_YEAR, 1);
				}
			}

			SelectedResponse res = new SelectedResponse();
			res.selectedDate = date;
			res.selectedActivity = selActivity;
			res.selectedPlus = false;

			return res;
		}

		private SelectedResponse selectedPointWeek(Point point) {
			Insets insets = this.getInsets();

			Rectangle rect = getDaysWeekRectangle(insets, this.getWidth(), this
					.getHeight());

			Date selectedDate = null;
			Activity selectedActivity = null;

			Calendar c = Calendar.getInstance();
			c.setTime(getDateMin());

			List<Activity> activities = dataModel.getActivities(c.getTime(),
					getDateMax());

			int x = rect.x;
			int y = rect.y;
			for (int i = 0; i < 7; i++) {
				int dayWidth = rect.width / 7;

				if (i == 6) {
					dayWidth = rect.width - (x - rect.x);
				}

				for (int j = 0; j < 24; j++) {
					int hourHeight = rect.height / 24;

					if (j == 23) {
						hourHeight = rect.height - (y - rect.y);
					}

					if (point.x >= x && point.x < x + dayWidth && point.y >= y
							&& point.y < y + hourHeight) {
						selectedDate = c.getTime();
					}

					if (activities != null && !activities.isEmpty()) {
						Calendar cMax = Calendar.getInstance();
						cMax.setTime(c.getTime());
						cMax.add(Calendar.HOUR_OF_DAY, 1);

						List<Activity> possibles = new ArrayList<Activity>();
						for (Activity activity : activities) {
							if (DateTimeUtils.includeDates(activity
									.getDateMin(), activity.getDateMax(), c
									.getTime(), cMax.getTime())) {
								possibles.add(activity);
							}
						}
						if (!possibles.isEmpty()) {
							Collections.sort(possibles,
									new ActivityComparator());

							Rectangle rect2 = new Rectangle(x + 1, y + 1,
									dayWidth - 2, hourHeight - 2);
							if (rect2.contains(point)) {
								int dx = point.x - rect2.x;

								int a = rect2.width / possibles.size();
								int b = dx / a;
								selectedActivity = possibles.get(b);
							}
						}
					}

					c.add(Calendar.HOUR_OF_DAY, 1);
					y += hourHeight;
				}

				x += dayWidth;
				y = rect.y;
			}

			SelectedResponse res = new SelectedResponse();
			res.selectedDate = selectedDate;
			res.selectedActivity = selectedActivity;
			res.selectedPlus = false;

			return res;
		}

		private Rectangle getNameWeekMonthRectangle(Insets insets, int width,
				int height) {
			int w = width - (insets.left + insets.right);
			return new Rectangle(insets.left, insets.top, w,
					nameWeekHeightMonth);
		}

		private Rectangle getWeeksMonthRectangle(Insets insets, int width,
				int height) {
			int w = width - (insets.left + insets.right);
			int h = height - (insets.top + insets.bottom + nameWeekHeightMonth);
			return new Rectangle(insets.left, insets.top + nameWeekHeightMonth,
					w, h);
		}

		private Rectangle getNameDayWeekRectangle(Insets insets, int width,
				int height) {
			int w = width - (insets.left + insets.right);
			return new Rectangle(insets.left, insets.top, w, nameDayHeightWeek);
		}

		private Rectangle getNameHourWeekRectangle(Insets insets, int width,
				int height) {
			int h = height - (insets.top + insets.bottom + nameDayHeightWeek);
			return new Rectangle(insets.left, insets.top + nameDayHeightWeek,
					nameHourWidthWeek, h);
		}

		private Rectangle getDaysWeekRectangle(Insets insets, int width,
				int height) {
			int w = width - (insets.left + insets.right + nameHourWidthWeek);
			int h = height - (insets.top + insets.bottom + nameDayHeightWeek);
			return new Rectangle(insets.left + nameHourWidthWeek, insets.top
					+ nameDayHeightWeek, w, h);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			switch (mode) {
			case WEEK:
				paintWeek(g2);
				break;
			case MONTH:
				paintMonth(g2);
				break;
			case YEAR:
				paintYear(g2);
				break;
			}
		}

		private void paintNameWeekMonth(Graphics g, Rectangle rect) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setColor(daysBorderColor);
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);

			int firstDay = Calendar.getInstance().getFirstDayOfWeek();

			FontMetrics fm = g2.getFontMetrics();

			int width3 = rect.width / 7;

			g2.setColor(nameDayForegoundColor);
			// Week name
			for (int i = 0; i < 7; i++) {
				int x = i * width3 + rect.x;

				int k = (firstDay + i) % 8;
				if (k == 0)
					k = 1;

				String text = listDayNames[k];

				int l = fm.stringWidth(text);

				g2.drawString(text, x + (width3 - l) / 2, rect.y + rect.height
						/ 2 + fm.getDescent());
			}

			g2.dispose();
		}

		private void paintWeekMonth(Graphics g, Rectangle rect) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setColor(dayBackgroundColor);
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);

			Calendar current = Calendar.getInstance();
			current.setTime(getDateMin());

			int month = currentDate.get(Calendar.MONTH);

			Calendar today = Calendar.getInstance();
			today.setTime(new Date());

			List<Activity> activities = dataModel.getActivities(current
					.getTime(), getDateMax());

			FontMetrics fm = g2.getFontMetrics();

			int x = rect.x;
			int y = rect.y;
			for (int j = 0; j < 6; j++) {
				int dayHeight = rect.height / 6;

				if (j == 5) {
					dayHeight = rect.height - (y - rect.y);
				}

				for (int i = 0; i < 7; i++) {
					int dayWidth = rect.width / 7;

					if (i == 6) {
						dayWidth = rect.width - (x - rect.x);
					}

					boolean isToday = false;
					boolean isDiffMonth = false;
					boolean isSelected = false;
					if (current.get(Calendar.DAY_OF_YEAR) == today
							.get(Calendar.DAY_OF_YEAR)
							&& current.get(Calendar.YEAR) == today
									.get(Calendar.YEAR)) {
						isToday = true;
					}
					if (current.get(Calendar.MONTH) != month) {
						isDiffMonth = true;
					}
					if (selectedDate != null
							&& current.get(Calendar.YEAR) == selectedDate
									.get(Calendar.YEAR)
							&& current.get(Calendar.DAY_OF_YEAR) == selectedDate
									.get(Calendar.DAY_OF_YEAR)) {
						isSelected = true;
					}

					if (isSelected) {
						g2.setColor(daySelectedBackgroundColor);
						g2.fillRect(x, y, dayWidth, dayHeight);
					} else {
						if (isToday) {
							g2.setColor(numberDayNowBackgroundColor);
						} else {
							g2.setColor(numberDayBackgroundColor);
						}
						g2.fillRect(x, y, dayWidth, 20);
					}

					g2.setColor(dayBorderColor);
					g2.drawRect(x, y, dayWidth, dayHeight);

					int day = current.get(Calendar.DAY_OF_MONTH);

					String text = String.valueOf(day);
					if (isDiffMonth) {
						g2.setColor(numberDayDiffMonthForegroundColor);
					} else {
						g2.setColor(numberDayForegroundColor);
					}
					g2.drawString(text, x + 2, y + fm.getAscent() + 2);

					if (DateTimeUtils.isFete(current.getTime())) {
						int sw = fm.stringWidth(SwingMessages
								.getString("JPlanning.22")); //$NON-NLS-1$
						g2
								.drawString(
										SwingMessages.getString("JPlanning.22"), x + dayWidth - (sw + 2), y //$NON-NLS-1$
												+ fm.getAscent() + 2);
					}

					if (activities != null && !activities.isEmpty()) {
						Calendar cMax = Calendar.getInstance();
						cMax.setTime(current.getTime());
						cMax.add(Calendar.DAY_OF_YEAR, 1);
						cMax.add(Calendar.MINUTE, -1);

						List<Activity> possibles = new ArrayList<Activity>();
						for (Activity activity : activities) {
							if (DateTimeUtils.includeDates(activity
									.getDateMin(), activity.getDateMax(),
									current.getTime(), cMax.getTime())) {
								possibles.add(activity);
							}
						}
						if (!possibles.isEmpty()) {
							Rectangle rect2 = new Rectangle(x + 1, y + 1 + 20,
									dayWidth - 2, dayHeight - 22);
							paintActivitesDayMonth(g2, rect2, current,
									possibles);
						}
					}

					current.add(Calendar.DAY_OF_MONTH, 1);

					x += dayWidth;
				}
				y += dayHeight;
				x = rect.x;
			}

			g2.dispose();
		}

		private void paintActivitesDayMonth(Graphics g, Rectangle rect,
				Calendar current, List<Activity> activities) {
			Graphics2D g2 = (Graphics2D) g.create();

			Collections.sort(activities, new ActivityComparator());

			int y = rect.y;

			int max = activities.size();
			int a = rect.height / activityHeightMonth;
			if (a < activities.size()) {
				max = a - 1;
			}

			Calendar cMin = Calendar.getInstance();
			Calendar cMax = Calendar.getInstance();
			for (int i = 0; i < max; i++) {
				Activity activity = activities.get(i);

				Color activityBackgroundColor = activity.getColor();
				Color activitySelectedBackgroundColor = new Color(
						0xFFFFFF - activityBackgroundColor.getRGB());

				cMin.setTime(activity.getDateMin());
				cMax.setTime(activity.getDateMax());

				int h = cMin.get(Calendar.HOUR_OF_DAY);
				int m = cMin.get(Calendar.MINUTE);

				g2.setClip(rect.x, y, rect.width, activityHeightMonth - 2);

				boolean onDay = false;
				if (DateTimeUtils.sameDay(activity.getDateMin(), activity
						.getDateMax())
						&& (h != 0 || m != 0)) {
					onDay = true;
				}

				boolean selected = selectedActivity != null ? selectedActivity
						.equals(activity) : false;
				if (selected) {
					g2.setColor(activitySelectedBackgroundColor);
				} else {
					g2.setColor(activityBackgroundColor);
				}

				if (!onDay || selected) {
					g2.fillRoundRect(rect.x, y, rect.width,
							activityHeightMonth - 2, 2, 2);
				}

				if (selected) {
					g2.setColor(activitySelectedForegroundColor);
				} else {
					if (onDay) {
						g2.setColor(activityBackgroundColor);
					} else {
						g2.setColor(activityForegroundColor);
					}
				}

				FontMetrics fm = null;
				if (onDay) {
					g2.setFont(dayMonthFont);
					fm = g2.getFontMetrics(dayMonthFont);
				} else {
					g2.setFont(allDaysMonthFont);
					fm = g2.getFontMetrics(allDaysMonthFont);
				}

				StringBuilder sb = new StringBuilder();
				if (DateTimeUtils.sameDay(cMin.getTime(), current.getTime())
						&& (h != 0 || m != 0)) {
					if (!onDay) {
						sb.append("("); //$NON-NLS-1$
					}
					sb.append(DateTimeUtils.toHoursString(h * 60 + m));
					if (!onDay) {
						sb.append(")"); //$NON-NLS-1$
					}
					sb.append(" "); //$NON-NLS-1$
				}
				sb.append(activity.getTitle());
				g2.drawString(sb.toString(), rect.x + 2, y
						+ (activityHeightMonth - 2) / 2 + fm.getDescent());

				// g2.drawRoundRect(rect.x, y, rect.width,
				// activityHeightMonth - 2, 2, 2);
				y += activityHeightMonth;
			}

			if (max != activities.size()) {
				g2.setFont(allDaysMonthFont);
				FontMetrics fm = g2.getFontMetrics(allDaysMonthFont);

				int reste = activities.size() - max;
				g2.setColor(Color.BLUE);
				int l = fm
						.stringWidth(SwingMessages.getString("JPlanning.27") + String.valueOf(reste)); //$NON-NLS-1$
				g2
						.drawString(
								SwingMessages.getString("JPlanning.27") + String.valueOf(reste), rect.x //$NON-NLS-1$
										+ (rect.width - l) / 2, y
										+ (activityHeightMonth - 2) / 2
										+ fm.getMaxDescent());
			}

			g2.dispose();
		}

		private void paintMonth(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();

			Insets insets = this.getInsets();

			Rectangle nameWeekRectangle = getNameWeekMonthRectangle(insets,
					this.getWidth(), this.getHeight());
			paintNameWeekMonth(g2, nameWeekRectangle);

			Rectangle weeksRectangle = getWeeksMonthRectangle(insets, this
					.getWidth(), this.getHeight());
			paintWeekMonth(g2, weeksRectangle);

			g2.dispose();
		}

		private void paintYear(Graphics2D g2) {
			int width = this.getWidth();
			int height = this.getHeight();

			FontMetrics fm = g2.getFontMetrics();

			int wDay = 24;
			int w = (width - wDay) / 12;
			int hMonth = 24;
			int h = (height - hMonth) / 31;

			// Month name
			for (int i = 0; i < 12; i++) {
				int x = i * w + wDay;

				g2.setClip(x, 0, w, hMonth);

				g2.setColor(new Color(0xe7eeec));
				g2.fillRect(x, 0, w, hMonth);

				g2.setColor(new Color(0x3f7d91));
				g2.drawRect(x, 0, w - 1, hMonth - 1);

				String text = listMonthNames[i];

				int l = fm.stringWidth(text);

				g2.setColor(Color.BLACK);
				g2.drawString(text, x + (w - l) / 2, hMonth / 2
						+ fm.getDescent());
			}

			// Days name
			for (int j = 0; j < 31; j++) {
				int y = j * h + hMonth;

				g2.setClip(0, y, wDay, h);

				g2.setColor(new Color(0xe7eeec));
				g2.fillRect(0, y, wDay, h);

				g2.setColor(new Color(0x3f7d91));
				g2.drawRect(0, y, wDay - 1, h - 1);

				String text = String.valueOf(j + 1);

				int l = fm.stringWidth(text);

				g2.setColor(Color.BLACK);
				g2
						.drawString(text, (wDay - l) / 2, y + h / 2
								+ fm.getDescent());
			}

			Calendar c = Calendar.getInstance();
			c.setTime(getDateMin());

			List<Activity> activities = dataModel.getActivities(c.getTime(),
					getDateMax());

			// Days
			for (int i = 0; i < 12; i++) {
				int maxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				for (int j = 0; j < maxDays; j++) {
					int x = i * w + wDay;
					int y = j * h + hMonth;
					boolean isSelected = false;

					if (selectedDate != null) {
						if (c.get(Calendar.YEAR) == selectedDate
								.get(Calendar.YEAR)
								&& c.get(Calendar.DAY_OF_YEAR) == selectedDate
										.get(Calendar.DAY_OF_YEAR)) {
							isSelected = true;
						}
					}

					g2.setClip(x, y, w, h);

					Calendar today = Calendar.getInstance();
					today.setTime(new Date());

					if (isSelected) {
						g2.setColor(new Color(0xffe79c));
					} else if (c.get(Calendar.DAY_OF_YEAR) == today
							.get(Calendar.DAY_OF_YEAR)
							&& c.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
						g2.setColor(new Color(0xdfeaf4));
					} else {
						g2.setColor(Color.WHITE);
					}
					g2.fillRect(x, y, w, h);

					int k = 0;
					int wActivity = (w - 8) / 5;
					g2.setColor(Color.RED);
					if (activities != null) {
						for (Activity activity : activities) {
							Calendar cMin = Calendar.getInstance();
							cMin.setTime(activity.getDateMin());
							cMin.set(Calendar.HOUR_OF_DAY, 0);
							cMin.set(Calendar.MINUTE, 0);
							cMin.set(Calendar.SECOND, 0);
							cMin.set(Calendar.MILLISECOND, 0);

							Calendar cMax = Calendar.getInstance();
							cMax.setTime(activity.getDateMax());
							cMax.set(Calendar.HOUR_OF_DAY, 0);
							cMax.set(Calendar.MINUTE, 0);
							cMax.set(Calendar.SECOND, 0);
							cMax.set(Calendar.MILLISECOND, 0);

							if (c.getTimeInMillis() >= cMin.getTimeInMillis()
									&& c.getTimeInMillis() <= cMax
											.getTimeInMillis()) {
								int xA = x + k * wActivity + 2;
								int yA = y + 2;
								int wA = wActivity - 1;
								int hA = h - 4;
								g2.setClip(xA, yA, wA, hA);

								if (selectedActivity == activity) {
									g2.setColor(new Color(0xffe284));
								} else {
									g2.setColor(new Color(0xb7cde6));
								}
								g2.fillRect(xA, yA, wA, hA);

								g2.setColor(new Color(0x3f7d91));
								g2.drawRect(xA, yA, wA - 1, hA - 1);

								k++;
							}
						}
					}

					g2.setClip(x, y, w, h);

					if (isSelected) {
						g2.setColor(new Color(255, 0, 0));
					} else {
						g2.setColor(new Color(0x3f7d91));
					}

					g2.drawRect(x, y, w - 1, h - 1);

					c.add(Calendar.DAY_OF_YEAR, 1);
				}
			}
		}

		private void paintNameDayWeek(Graphics g, Rectangle rect) {
			Graphics2D g2 = (Graphics2D) g.create();

			Calendar c = Calendar.getInstance();
			c.setTime(getDateMin());

			g2.setColor(daysBorderColor);
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);

			int firstDay = Calendar.getInstance().getFirstDayOfWeek();

			FontMetrics fm = g2.getFontMetrics();

			g2.setColor(nameDayForegoundColor);
			// Day name
			int x = rect.x + nameHourWidthWeek;
			for (int i = 0; i < 7; i++) {
				int width3 = (rect.width - nameHourWidthWeek) / 7;

				if (i == 6) {
					width3 = (rect.width - nameHourWidthWeek)
							- (x - (rect.x + nameHourWidthWeek));
				}

				int k = (firstDay + i) % 8;
				if (k == 0)
					k = 1;

				int day = c.get(Calendar.DAY_OF_MONTH);
				int month = c.get(Calendar.MONTH);

				String text = listShortDayNames[k] + " " + String.valueOf(day) //$NON-NLS-1$
						+ "/" + String.valueOf(month + 1); //$NON-NLS-1$

				int l = fm.stringWidth(text);

				g2.drawString(text, x + (width3 - l) / 2, rect.y + rect.height
						/ 2 + fm.getDescent());

				c.add(Calendar.DAY_OF_WEEK, 1);
				x += width3;
			}

			g2.dispose();
		}

		private void paintNameHourWeek(Graphics g, Rectangle rect) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setColor(numberDayBackgroundColor);
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);

			FontMetrics fm = g2.getFontMetrics();

			g2.setColor(nameDayForegoundColor);

			// Hours name
			int y = rect.y;
			for (int j = 0; j < 24; j++) {
				int height3 = rect.height / 24;

				if (j == 23) {
					height3 = rect.height - (y - rect.y);
				}

				g2.setColor(hourBorderColor);
				g2.drawRect(rect.x, y, rect.width, height3);

				String text = DateTimeUtils.toHoursString(j * 60);

				int l = fm.stringWidth(text);

				g2.setColor(numberHourForegroundColor);
				g2.drawString(text, rect.x + (rect.width - l) / 2, y
						+ fm.getAscent());

				y += height3;
			}

			g2.dispose();
		}

		private void paintActivitesHourWeek(Graphics g, Rectangle rect,
				Calendar current, List<Activity> activities) {
			Graphics2D g2 = (Graphics2D) g.create();

			Collections.sort(activities, new ActivityComparator());

			int x = rect.x;
			int w = rect.width / activities.size();
			Calendar cMin = Calendar.getInstance();
			Calendar cMax = Calendar.getInstance();
			for (int i = 0; i < activities.size(); i++) {
				Activity activity = activities.get(i);

				Color activityBackgroundColor = activity.getColor();
				Color activitySelectedBackgroundColor = new Color(
						0xFFFFFF - activityBackgroundColor.getRGB());

				cMin.setTime(activity.getDateMin());
				cMax.setTime(activity.getDateMax());

				int h = cMin.get(Calendar.HOUR_OF_DAY);
				int m = cMin.get(Calendar.MINUTE);

				boolean onDay = false;
				if (DateTimeUtils.sameDay(activity.getDateMin(), activity
						.getDateMax())
						&& (h != 0 || m != 0)) {
					onDay = true;
				}

				boolean selected = selectedActivity != null ? selectedActivity
						.equals(activity) : false;
				if (selected) {
					g2.setColor(activitySelectedBackgroundColor);
				} else {
					g2.setColor(activityBackgroundColor);
				}
				if (!onDay || selected) {
					if (i < activities.size() - 1) {
						g2.fillRoundRect(x, rect.y, w - 1, rect.height, 2, 2);
					} else {
						g2.fillRoundRect(x, rect.y, w, rect.height, 2, 2);
					}
				}

				if (selected) {
					g2.setColor(activitySelectedForegroundColor);
				} else {
					if (onDay) {
						g2.setColor(activityBackgroundColor);
					} else {
						g2.setColor(activityForegroundColor);
					}
				}

				FontMetrics fm = null;
				if (onDay) {
					g2.setFont(dayMonthFont);
					fm = g2.getFontMetrics(dayMonthFont);
				} else {
					g2.setFont(allDaysMonthFont);
					fm = g2.getFontMetrics(allDaysMonthFont);
				}

				StringBuilder sb = new StringBuilder();
				// if (DateTimeUtils.equalDay(cMin.getTime(), current.getTime())
				// && (h != 0 || m != 0)) {
				// if (!onDay) {
				// sb.append("(");
				// }
				// sb.append(DateTimeUtils.toHoursString(h * 60 + m));
				// if (!onDay) {
				// sb.append(")");
				// }
				// sb.append(" ");
				// }
				sb.append(activity.getTitle());
				g2.drawString(sb.toString(), x + 2, rect.y + rect.height / 2
						+ fm.getDescent());
				x += w;
			}

			g2.dispose();
		}

		private void paintDaysWeek(Graphics g, Rectangle rect) {
			Graphics2D g2 = (Graphics2D) g.create();

			Calendar c = Calendar.getInstance();
			c.setTime(getDateMin());

			List<Activity> activities = dataModel.getActivities(c.getTime(),
					getDateMax());

			int x = rect.x;
			int y = rect.y;
			for (int i = 0; i < 7; i++) {
				int dayWidth = rect.width / 7;

				if (i == 6) {
					dayWidth = rect.width - (x - rect.x);
				}

				for (int j = 0; j < 24; j++) {
					int hourHeight = rect.height / 24;

					if (j == 23) {
						hourHeight = rect.height - (y - rect.y);
					}

					boolean isSelected = false;

					if (selectedDate != null) {
						if (c.get(Calendar.YEAR) == selectedDate
								.get(Calendar.YEAR)
								&& c.get(Calendar.DAY_OF_YEAR) == selectedDate
										.get(Calendar.DAY_OF_YEAR)) {
							isSelected = true;
						}
					}

					Calendar today = Calendar.getInstance();
					today.setTime(new Date());

					if (isSelected) {
						g2.setColor(daySelectedBackgroundColor);
					} else if (c.get(Calendar.DAY_OF_YEAR) == today
							.get(Calendar.DAY_OF_YEAR)
							&& c.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
						g2.setColor(dayNowBackgroundColor);
					} else {
						g2.setColor(Color.WHITE);
					}
					g2.fillRect(x, y, dayWidth, hourHeight);

					if (activities != null && !activities.isEmpty()) {
						Calendar cMax = Calendar.getInstance();
						cMax.setTime(c.getTime());
						cMax.add(Calendar.HOUR_OF_DAY, 1);

						List<Activity> possibles = new ArrayList<Activity>();
						for (Activity activity : activities) {
							if (DateTimeUtils.includeDates(activity
									.getDateMin(), activity.getDateMax(), c
									.getTime(), cMax.getTime())) {
								possibles.add(activity);
							}
						}
						if (!possibles.isEmpty()) {
							Rectangle rect2 = new Rectangle(x + 1, y + 1,
									dayWidth - 2, hourHeight - 2);
							paintActivitesHourWeek(g2, rect2, c, possibles);
						}
					}

					g2.setColor(hourBorderColor);
					g2.drawRect(x, y, dayWidth, hourHeight);

					c.add(Calendar.HOUR_OF_DAY, 1);
					y += hourHeight;
				}

				x += dayWidth;
				y = rect.y;
			}

			g2.dispose();
		}

		private void paintWeek(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();

			Insets insets = this.getInsets();

			Rectangle nameDayWeekRectangle = getNameDayWeekRectangle(insets,
					this.getWidth(), this.getHeight());
			paintNameDayWeek(g2, nameDayWeekRectangle);

			Rectangle nameHourWeekRectangle = getNameHourWeekRectangle(insets,
					this.getWidth(), this.getHeight());
			paintNameHourWeek(g2, nameHourWeekRectangle);

			Rectangle daysWeekRectangle = getDaysWeekRectangle(insets, this
					.getWidth(), this.getHeight());
			paintDaysWeek(g2, daysWeekRectangle);

			g2.dispose();
		}

		private final class MyMouseListener extends MouseAdapter {

			public void mousePressed(MouseEvent e) {
				final SelectedResponse res = selectedPoint(e.getPoint());
				if (res != null) {
					setSelectedDate(res.selectedDate);
					setSelectedActivity(res.selectedActivity);

					if (res.selectedPlus) {
						JPopupMenu popupMenu = new JPopupMenu();
						for (Activity activity : res.plusActivities) {
							popupMenu.add(new ActivityAction(activity));
						}

						popupMenu.show(e.getComponent(), e.getPoint().x, e
								.getPoint().y);
					}
				} else {
					setSelectedDate(null);
					setSelectedActivity(null);
				}
			}
		}

		private final class ActivityAction extends AbstractAction {

			private static final long serialVersionUID = -5980212810238648747L;

			private Activity activity;

			public ActivityAction(Activity activity) {
				super(activity.getTitle());
				this.activity = activity;
			}

			public void actionPerformed(ActionEvent e) {
				setSelectedActivity(activity);
			}

		}

		private final class SelectedResponse {

			Date selectedDate;

			Activity selectedActivity;

			boolean selectedPlus;

			List<Activity> plusActivities;

		}

		private final class ActivityComparator implements Comparator<Activity> {

			public int compare(Activity o1, Activity o2) {
				int res = 0;
				Calendar c1 = Calendar.getInstance();
				c1.setTime(o1.getDateMin());
				Calendar c2 = Calendar.getInstance();
				c2.setTime(o2.getDateMin());

				// boolean oneDay1 = DateTimeUtils.equalDay(o1.getDateMin(),
				// o1.getDateMax());
				// boolean oneDay2 = DateTimeUtils.equalDay(o2.getDateMin(),
				// o2.getDateMax());
				//				
				// if (oneDay1 && oneDay2) {
				//					
				// }
				//				
				if (c1.before(c2)) {
					res = -1;
				} else if (c2.before(c1)) {
					res = 1;
				}
				return res;
			}
		}
	}
}
