package com.synaptix.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.DateTimeUtils;

public class JSelectionCalendar extends JPanel {

	private static final long serialVersionUID = -8527373451091146570L;

	private static final Color COLOR_BACKGROUND_DAYS;

	private static final Color COLOR_FORGROUND_DAYS;

	private static final Color COLOR_BACKGROUND_NAME_DAY;

	private static final Color COLOR_FORGROUND_NAME_DAY;

	private static final Color COLOR_GRID_DAYS;

	private static final Color COLOR_SELECTED_DAY;

	private static final Color COLOR_BACKGROUND_SELECT_DAY;

	private static final Color COLOR_ROLLOVER_DAY;

	private static final Color COLOR_BACKGROUND_DAY;

	private static final Color COLOR_OUT_OF_MONTH_DAY;

	private static final Color COLOR_IN_OF_MONTH_DAY;

	private static final Color COLOR_BACKGROUND_HOLIDAY;

	private static final Color COLOR_BACKGROUND_SATURDAYSUNDAY;

	private static final Color COLOR_PROHIBITE;

	private static final Locale locale;

	private static final String[] listMonthNames;

	private static final String[] listDayNames;

	private static final String[] listDayAbbreviations;

	private static final DateFormat dateFormatLong;

	private static final Font boldFont;

	private static final Font italicFont;

	private Action nextAction;

	private Action previousAction;

	private JLabel titleLabel;

	private Action menuSelectionAction;

	private Action monthYearChangeAction;

	private Action allSelectionAction;

	private Action noneSelectionAction;

	private JDayButton[] days;

	private JWeekLabel[] weeks;

	private Calendar calendar;

	private Calendar calendarMin;

	private Calendar calendarMax;

	private List<Date> prohibiteDates;

	private List<Date> holidayDates;

	private List<Date> selectedDates;

	private String expression;
	
	static {
		locale = Locale.getDefault();

		COLOR_BACKGROUND_DAYS = new Color(65, 177, 245);
		COLOR_FORGROUND_DAYS = new Color(255, 255, 255);
		COLOR_BACKGROUND_NAME_DAY = new Color(40, 125, 255);
		COLOR_FORGROUND_NAME_DAY = new Color(255, 255, 255);
		COLOR_GRID_DAYS = new Color(124, 180, 250);
		COLOR_BACKGROUND_SELECT_DAY = new Color(160, 230, 255);
		COLOR_SELECTED_DAY = new Color(58, 136, 251);
		COLOR_ROLLOVER_DAY = new Color(255, 128, 64);
		COLOR_BACKGROUND_DAY = new Color(255, 255, 255);
		COLOR_OUT_OF_MONTH_DAY = new Color(132, 191, 250);
		COLOR_IN_OF_MONTH_DAY = new Color(0, 0, 0);

		COLOR_BACKGROUND_HOLIDAY = new Color(232, 255, 232);
		COLOR_BACKGROUND_SATURDAYSUNDAY = new Color(232, 232, 232);

		COLOR_PROHIBITE = new Color(255, 64, 64);

		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);

		listMonthNames = dateFormatSymbols.getMonths();
		listDayNames = dateFormatSymbols.getWeekdays();
		listDayAbbreviations = dateFormatSymbols.getShortWeekdays();

		dateFormatLong = DateFormat.getDateInstance(DateFormat.FULL, locale);

		boldFont = new Font("Arial", Font.BOLD, 11); //$NON-NLS-1$
		italicFont = new Font("Arial", Font.ITALIC, 11); //$NON-NLS-1$
	}

	public JSelectionCalendar() {
		super(new BorderLayout());

		calendar = Calendar.getInstance(locale);
		calendar.setTime(DateTimeUtils.clearHourForDate(new Date()));

		calendarMin = (Calendar) calendar.clone();
		calendarMin.set(Calendar.DAY_OF_YEAR, calendarMin
				.getActualMinimum(Calendar.DAY_OF_YEAR));

		calendarMax = (Calendar) calendar.clone();
		calendarMax.set(Calendar.DAY_OF_YEAR, calendarMax
				.getActualMaximum(Calendar.DAY_OF_YEAR));

		prohibiteDates = new ArrayList<Date>();
		holidayDates = new ArrayList<Date>();
		selectedDates = new ArrayList<Date>();

		expression = ""; //$NON-NLS-1$
		
		initComponents();

		this.add(buildMonthYearEditor(), BorderLayout.NORTH);
		this.add(buildDaysEditor(), BorderLayout.CENTER);
		this.add(buildStatusEditor(), BorderLayout.SOUTH);

		update();
	}

	private void initComponents() {
		nextAction = new NextAction();
		previousAction = new PreviousAction();
		titleLabel = new JLabel("", JLabel.CENTER); //$NON-NLS-1$

		menuSelectionAction = new SelectionAction();
		monthYearChangeAction = new MonthYearChangeAction();
		allSelectionAction = new AllSelectionAction();
		noneSelectionAction = new NoneSelectionAction();

		DayActionListener dayActionListener = new DayActionListener();

		days = new JDayButton[6 * 7];
		weeks = new JWeekLabel[6];

		for (int j = 0; j < 6; j++) {
			weeks[j] = new JWeekLabel();
			weeks[j].setFont(boldFont);

			for (int i = 0; i < 7; i++) {
				int k = i + j * 7;
				days[k] = new JDayButton();
				days[k].addActionListener(dayActionListener);
				days[k].setFont(boldFont);
				k++;
			}
		}
	}

	private JPanel buildMonthYearEditor() {
		FormLayout layout = new FormLayout("p,2dlu,fill:default:grow,2dlu,p", //$NON-NLS-1$
				"default"); //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		JButton previousButton = new JButton(previousAction);
		previousButton.setFont(boldFont);
		builder.add(previousButton, cc.xy(1, 1));

		builder.add(titleLabel, cc.xy(3, 1));

		JButton nextButton = new JButton(nextAction);
		nextButton.setFont(boldFont);
		builder.add(nextButton, cc.xy(5, 1));

		return builder.getPanel();
	}

	private JPanel buildDaysEditor() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(7, 8));
		panel.setBackground(COLOR_BACKGROUND_DAYS);

		JLabel vide1 = new JDayLabel();
		panel.add(vide1);

		for (int i = 0; i < 7; i++) {
			int firstDay = calendar.getFirstDayOfWeek() - 1;
			int k = (i + firstDay) % 7;

			JLabel label = new JDayLabel(listDayAbbreviations[k + 1]);
			label.setToolTipText(listDayNames[k + 1]);
			label.setFont(boldFont);
			panel.add(label);
		}

		int k = 0;
		for (int j = 0; j < 6; j++) {
			for (int i = 0; i < 8; i++) {
				if (i == 0) {
					panel.add(weeks[j]);
				} else {
					panel.add(days[k]);
					k++;
				}
			}
		}

		return panel;
	}

	private JComponent buildStatusEditor() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(menuSelectionAction);
		toolBar.addSeparator();
		toolBar.add(allSelectionAction);
		toolBar.addSeparator();
		toolBar.add(noneSelectionAction);

		return toolBar;
	}

	private void update() {
		updateMonthYear();
		updateDays();
		// updateCurrentDay();

		repaint();
	}

	private void updateMonthYear() {
		int m = calendar.get(Calendar.MONTH);
		StringBuilder sbMonth = new StringBuilder();
		sbMonth.append(listMonthNames[m]);
		sbMonth.append(" ("); //$NON-NLS-1$
		sbMonth.append(m + 1);
		sbMonth.append(") "); //$NON-NLS-1$
		sbMonth.append(calendar.get(Calendar.YEAR));
		titleLabel.setText(sbMonth.toString());

		// Previous month
		Calendar cPrevious = (Calendar) calendar.clone();
		cPrevious.set(Calendar.DAY_OF_MONTH, cPrevious
				.getActualMinimum(Calendar.DAY_OF_MONTH));
		if (cPrevious.compareTo(calendarMin) > 0) {
			Calendar cPreviousMonth = (Calendar) calendar.clone();
			cPreviousMonth.add(Calendar.MONTH, -1);
			previousAction.putValue(Action.SHORT_DESCRIPTION,
					getNameMonth(cPreviousMonth.get(Calendar.MONTH)));
			previousAction.setEnabled(true);
		} else {
			previousAction.putValue(Action.SHORT_DESCRIPTION, null);
			previousAction.setEnabled(false);
		}

		// Next month
		Calendar cNext = (Calendar) calendar.clone();
		cNext.set(Calendar.DAY_OF_MONTH, cNext
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		if (cNext.compareTo(calendarMax) < 0) {
			Calendar cNextMonth = (Calendar) calendar.clone();
			cNextMonth.add(Calendar.MONTH, 1);
			nextAction.putValue(Action.SHORT_DESCRIPTION,
					getNameMonth(cNextMonth.get(Calendar.MONTH)));
			nextAction.setEnabled(true);
		} else {
			nextAction.putValue(Action.SHORT_DESCRIPTION, null);
			nextAction.setEnabled(false);
		}
	}

	private String getNameMonth(int month) {
		StringBuilder sb = new StringBuilder();
		sb.append(listMonthNames[month]);
		sb.append(" ("); //$NON-NLS-1$
		sb.append(month + 1);
		sb.append(")"); //$NON-NLS-1$
		return sb.toString();
	}

	private void updateDays() {
		//deselectedAllDays();

		int m = calendar.get(Calendar.MONTH);

		Calendar c = (Calendar) calendar.clone();
		c.set(Calendar.DAY_OF_MONTH, 1);

		int firstDay = calendar.getFirstDayOfWeek();

		int dw = (c.get(Calendar.DAY_OF_WEEK) + (7 - firstDay)) % 7;
		c.add(Calendar.DAY_OF_MONTH, -dw);

		int k = 0;
		for (int j = 0; j < 6; j++) {
			for (int i = 0; i < 7; i++) {
				if (i == 0) {
					int w = c.get(Calendar.WEEK_OF_YEAR);
					weeks[j].setText(String.valueOf(w));
				}

				days[k].setOutOfMonth(false);
				days[k].setHoliday(false);
				days[k].setSelectedDay(false);
				days[k].setSaturdaySunday(false);
				days[k].setProhibite(false);
				days[k].setEnabled(true);

				if (c.get(Calendar.MONTH) != m) {
					days[k].setOutOfMonth(true);
				}

				if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						|| c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					days[k].setSaturdaySunday(true);
				}

				if (c.compareTo(calendarMin) < 0
						|| c.compareTo(calendarMax) > 0) {
					days[k].setProhibite(true);
					days[k].setEnabled(false);
				}

				if (holidayDates.contains(c.getTime())) {
					days[k].setHoliday(true);
				}

				if (prohibiteDates.contains(c.getTime())) {
					days[k].setProhibite(true);
				}

				if (selectedDates.contains(c.getTime())) {
					days[k].setSelectedDay(true);
				}

				days[k].setDate(c.getTime());
				days[k].setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
				days[k].setToolTipText(dateFormatLong.format(c.getTime()));
				c.add(Calendar.DAY_OF_MONTH, 1);
				k++;
			}
		}
	}

	private void clearButton(final JButton button) {
		ButtonModel model = button.getModel();
		model.setSelected(false);
		model.setArmed(false);
		model.setRollover(false);
	}

	private void deselectedAllDays() {
		for (int i = 0; i < 6 * 7; i++) {
			clearButton(days[i]);
		}
	}

	public void setBetweenDate(Date dateMin, Date dateMax, Date dateCurrent) {
		calendarMin.setTime(DateTimeUtils.clearHourForDate(dateMin));
		calendarMax.setTime(DateTimeUtils.clearHourForDate(dateMax));
		calendar.setTime(DateTimeUtils.clearHourForDate(dateCurrent));

		update();
	}

	public Date getDateMax() {
		return calendarMax.getTime();
	}

	public Date getDateMin() {
		return calendarMin.getTime();
	}

	public Date getDateCurrent() {
		return calendar.getTime();
	}

	public void addHolidayDate(Date date) {
		holidayDates.add(DateTimeUtils.clearHourForDate(date));
		update();
	}

	public void addHolidayDates(Date[] dates) {
		for (Date date : dates) {
			holidayDates.add(DateTimeUtils.clearHourForDate(date));
		}
		update();
	}

	public void addProhibiteDate(Date date) {
		prohibiteDates.add(DateTimeUtils.clearHourForDate(date));
		update();
	}

	public void addProhibiteDates(Date[] dates) {
		for (Date date : dates) {
			prohibiteDates.add(DateTimeUtils.clearHourForDate(date));
		}
		update();
	}

	public String getTextSelectedDates() {
		StringBuilder sb = new StringBuilder();

		Calendar c = (Calendar) calendarMin.clone();
		while (c.compareTo(calendarMax) <= 0) {
			if (selectedDates.contains(c.getTime())) {
				sb.append("1"); //$NON-NLS-1$
			} else {
				sb.append("0"); //$NON-NLS-1$
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
		}

		return sb.toString();
	}

	public void setTextSelectedDates(String s) {
		selectedDates.clear();
		
		Calendar c = (Calendar) calendarMin.clone();
		for (int i = 0; i < s.length(); i++) {
			if (c.compareTo(calendarMax) <= 0 && s.charAt(i) == '1') {
				selectedDates.add(c.getTime());
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
		}

		update();
	}

	public void setExpression(String e) {
		selectedDates.clear();
		
		update();
	}
	
	public String getExpression() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(expression);
		
		return expression;
	}
	
	private final class NextAction extends AbstractAction {

		private static final long serialVersionUID = -3212187128241312706L;

		public NextAction() {
			super(">"); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Calendar c = (Calendar) calendar.clone();
			c.set(Calendar.DAY_OF_MONTH, c
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			if (c.compareTo(calendarMax) < 0) {
				calendar.add(Calendar.MONTH, 1);
				update();
			}
		}
	}

	private final class PreviousAction extends AbstractAction {

		private static final long serialVersionUID = 5047359664572990534L;

		public PreviousAction() {
			super("<"); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Calendar c = (Calendar) calendar.clone();
			c.set(Calendar.DAY_OF_MONTH, c
					.getActualMinimum(Calendar.DAY_OF_MONTH));
			if (c.compareTo(calendarMin) > 0) {
				calendar.add(Calendar.MONTH, -1);
				update();
			}
		}
	}

	private final class SelectionAction extends AbstractAction {

		private static final long serialVersionUID = 2223634812969477751L;

		public SelectionAction() {
			super(SwingMessages.getString("JSelectionCalendar.14")); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("JSelectionCalendar.15")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class MonthYearChangeAction extends AbstractAction {

		private static final long serialVersionUID = 2223634812969477751L;

		public MonthYearChangeAction() {
			super(SwingMessages.getString("JSelectionCalendar.16")); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class NoneSelectionAction extends AbstractAction {

		private static final long serialVersionUID = 2223634812969477751L;

		public NoneSelectionAction() {
			super(SwingMessages.getString("JSelectionCalendar.17")); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("JSelectionCalendar.18")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class AllSelectionAction extends AbstractAction {

		private static final long serialVersionUID = 2223634812969477751L;

		public AllSelectionAction() {
			super(SwingMessages.getString("JSelectionCalendar.0")); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("JSelectionCalendar.20")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class JDayButton extends JButton {

		private static final long serialVersionUID = 4301633920562728972L;

		private Stroke normalStroke;

		private Stroke prohibiteStroke;

		private Stroke selectedStroke;

		private boolean holiday;

		private boolean saturdaySunday;

		private Calendar calendar;

		private boolean outOfMonth;

		private boolean selectedDay;

		private boolean prohibite;

		public JDayButton() {
			this(""); //$NON-NLS-1$
		}

		public JDayButton(final String text) {
			super(text);

			normalStroke = new BasicStroke(1.0f);
			prohibiteStroke = new BasicStroke(2.0f);
			selectedStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL, 0, new float[] { 4, 4 }, 0);

			this.calendar = Calendar.getInstance();
			this.setOpaque(false);
			this.setContentAreaFilled(false);
			this.setRolloverEnabled(true);
			this.holiday = false;
			this.saturdaySunday = false;
			this.outOfMonth = false;
			this.selectedDay = false;
			this.prohibite = false;
			this.setBorder(null);
		}

		private void paintGrid(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			int w = this.getWidth();
			int h = this.getHeight();

			g2.setStroke(normalStroke);
			g2.setColor(COLOR_GRID_DAYS);
			g2.drawLine(w - 1, 0, w - 1, h - 1);
			g2.drawLine(0, h - 1, w - 1, h - 1);
		}

		private void paintBlock(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			int w = this.getWidth();
			int h = this.getHeight();

			ButtonModel model = this.getModel();
			if (model.isSelected() || model.isArmed()) {
				g2.setStroke(normalStroke);
				g2.setColor(COLOR_SELECTED_DAY);
				g2.fillRect(0, 0, w, h);
			} else {
				g2.setStroke(normalStroke);
				if (selectedDay) {
					g2.setColor(COLOR_BACKGROUND_SELECT_DAY);
				} else if (holiday) {
					g2.setColor(COLOR_BACKGROUND_HOLIDAY);
				} else if (saturdaySunday) {
					g2.setColor(COLOR_BACKGROUND_SATURDAYSUNDAY);
				} else {
					g2.setColor(COLOR_BACKGROUND_DAY);
				}
				g2.fillRect(0, 0, w, h);
			}

			if (prohibite) {
				g2.setStroke(prohibiteStroke);
				g2.setColor(COLOR_PROHIBITE);
				g2.drawLine(0, 0, w - 1, h - 1);
				g2.drawLine(w - 1, 0, 0, h - 1);
			}
			
			if (model.isRollover()) {
				g2.setStroke(selectedStroke);
				g2.setColor(COLOR_ROLLOVER_DAY);
				g2.drawRect(1, 1, w - 4, h - 4);
			}
		}

		protected void paintComponent(Graphics g) {
			paintBlock(g);
			paintGrid(g);

			if (outOfMonth) {
				this.setForeground(COLOR_OUT_OF_MONTH_DAY);
				this.setFont(italicFont);
			} else {
				this.setForeground(COLOR_IN_OF_MONTH_DAY);
				this.setFont(boldFont);
			}

			super.paintComponent(g);
		}

		public boolean isHoliday() {
			return holiday;
		}

		public void setHoliday(boolean holiday) {
			this.holiday = holiday;
			repaint();
		}

		public boolean isSaturdaySunday() {
			return saturdaySunday;
		}

		public void setSaturdaySunday(boolean saturdaySunday) {
			this.saturdaySunday = saturdaySunday;
			repaint();
		}

		public boolean isSelectedDay() {
			return selectedDay;
		}

		public void setSelectedDay(boolean selectedDay) {
			this.selectedDay = selectedDay;
			repaint();
		}

		public Date getDate() {
			return calendar.getTime();
		}

		public void setDate(Date date) {
			calendar.setTime(date);
		}

		public boolean isOutOfMonth() {
			return outOfMonth;
		}

		public void setOutOfMonth(boolean outOfMonth) {
			this.outOfMonth = outOfMonth;
			repaint();
		}

		public boolean isProhibite() {
			return prohibite;
		}

		public void setProhibite(boolean prohibite) {
			this.prohibite = prohibite;
			repaint();
		}
	}

	private final class JDayLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		public JDayLabel() {
			this(""); //$NON-NLS-1$
		}

		public JDayLabel(final String text) {
			super(text, JLabel.CENTER);
			this.setOpaque(true);
			this.setBackground(COLOR_BACKGROUND_NAME_DAY);
			this.setForeground(COLOR_FORGROUND_NAME_DAY);
			this.setBorder(null);
		}
	}

	private final class JWeekLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		public JWeekLabel() {
			this(""); //$NON-NLS-1$
		}

		public JWeekLabel(final String text) {
			super(text, JLabel.CENTER);
			this.setForeground(COLOR_FORGROUND_DAYS);
			this.setBorder(null);
		}
	}

	private final class DayActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JButton) {
				JDayButton b = (JDayButton) e.getSource();
				Date date = b.getDate();

				if (b.isSelectedDay()) {
					b.setSelectedDay(false);
					selectedDates.remove(date);
				} else {
					b.setSelectedDay(true);
					selectedDates.add(date);
				}
				update();
			}
		}
	}
}
