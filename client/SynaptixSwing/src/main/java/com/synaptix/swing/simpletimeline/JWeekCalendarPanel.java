package com.synaptix.swing.simpletimeline;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.DateTimeUtils;

public class JWeekCalendarPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Color COLOR_BACKGROUND_DAYS;

	private static final Color COLOR_BACKGROUND_NAME_DAY;

	private static final Color COLOR_FORGROUND_NAME_DAY;

	private static final Color COLOR_GRID_DAYS;

	private static final Color COLOR_SELECTED_DAY;

	private static final Color COLOR_ROLLOVER_DAY;

	private static final Color COLOR_BACKGROUND_DAY;

	private static final Color COLOR_TODAY;

	private static final Color COLOR_OUT_OF_MONTH_DAY;

	private static final Color COLOR_IN_OF_MONTH_DAY;

	private static final DateFormat shortDateFormat;

	private static final DateFormat weekTooltipsDateFormat;

	private static final Locale locale;

	private static final Font normalFont;

	private static final Font boldFont;

	private static final Font italicFont;

	private static final Font labelBoldFont;

	private static final String[] listDayNames;

	private static final String[] listDayAbbreviations;

	private static final DateFormat dateFormatLong;

	private Date today;

	private Calendar calendar;

	private Calendar calendarSelected;

	private int nbBeforeDays;

	private int nbAfterDays;

	private JLabel label;

	private boolean none;

	private JDayButton[] days;

	private Action previousMonthAction;

	private Action nextMonthAction;

	private EventListenerList listenerList;

	static {
		locale = Locale.getDefault();

		COLOR_BACKGROUND_DAYS = new Color(0xFFFFFF);
		COLOR_BACKGROUND_NAME_DAY = new Color(0xC3D9FF);
		COLOR_FORGROUND_NAME_DAY = new Color(0x152BD5);
		COLOR_GRID_DAYS = new Color(0xCCDDEE);
		COLOR_SELECTED_DAY = new Color(0xffe79c);
		COLOR_ROLLOVER_DAY = new Color(0xFFFFFF);
		COLOR_BACKGROUND_DAY = new Color(0xFFFFFF);
		COLOR_TODAY = new Color(255, 0, 0);
		COLOR_OUT_OF_MONTH_DAY = new Color(0xBABDC4);
		COLOR_IN_OF_MONTH_DAY = new Color(0x6A6A6B);

		shortDateFormat = new SimpleDateFormat("MMM yy"); //$NON-NLS-1$
		weekTooltipsDateFormat = new SimpleDateFormat("dd MMMMM yyyy"); //$NON-NLS-1$

		normalFont = new Font("Tahoma", Font.PLAIN, 11); //$NON-NLS-1$
		boldFont = new Font("Tahoma", Font.BOLD, 11); //$NON-NLS-1$
		italicFont = new Font("Tahoma", Font.ITALIC, 11); //$NON-NLS-1$
		labelBoldFont = new Font("arial", Font.BOLD, 12); //$NON-NLS-1$

		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		listDayNames = dateFormatSymbols.getWeekdays();
		listDayAbbreviations = dateFormatSymbols.getShortWeekdays();

		dateFormatLong = DateFormat.getDateInstance(DateFormat.FULL, locale);
	}

	public JWeekCalendarPanel() {
		super(new BorderLayout());

		Calendar c = Calendar.getInstance(locale);
		today = c.getTime();

		calendar = Calendar.getInstance(locale);
		calendar.setTime(today);

		calendarSelected = (Calendar) calendar.clone();
		none = true;

		listenerList = new EventListenerList();

		initActions();
		initComponents();

		updateAll();

		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
		previousMonthAction = new PreviousMonthAction();
		nextMonthAction = new NextMonthAction();
	}

	private void createComponents() {
		label = new JLabel("", JLabel.CENTER); //$NON-NLS-1$

		days = new JDayButton[7];
	}

	private void initComponents() {
		createComponents();

		label.setFont(labelBoldFont);
		label.setBorder(BorderFactory.createEtchedBorder());

		DayActionListener dayActionListener = new DayActionListener();
		for (int i = 0; i < 7; i++) {
			days[i] = new JDayButton();
			days[i].addActionListener(dayActionListener);
			days[i].setFont(boldFont);
		}
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE", //$NON-NLS-1$
				"FILL:12DLU:NONE,FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new JButton(previousMonthAction), cc.xy(1, 1));
		builder.add(new JButton(nextMonthAction), cc.xy(3, 1));
		builder.add(label, cc.xy(2, 1));
		builder.add(buildDays(), cc.xyw(1, 2, 3));
		return builder.getPanel();
	}

	private JPanel buildDays() {
		FormLayout layout = new FormLayout(
				"FILL:DEFAULT:GROW(0.15), FILL:DEFAULT:GROW(0.14), FILL:DEFAULT:GROW(0.14), FILL:DEFAULT:GROW(0.14), FILL:DEFAULT:GROW(0.14), FILL:DEFAULT:GROW(0.14), FILL:DEFAULT:GROW(0.15)", //$NON-NLS-1$
				"FILL:DEFAULT:GROW(0.5),FILL:DEFAULT:GROW(0.5)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		for (int i = 0; i < 7; i++) {
			int firstDay = calendar.getFirstDayOfWeek() - 1;
			int k = (i + firstDay) % 7;

			JLabel label = new JDayLabel(listDayAbbreviations[k + 1]);
			label.setToolTipText(listDayNames[k + 1]);
			label.setFont(boldFont);

			builder.add(label, cc.xy(i + 1, 1));
		}

		for (int i = 0; i < 7; i++) {
			builder.add(days[i], cc.xy(i + 1, 2));
		}

		JPanel panel = builder.getPanel();
		panel.setBackground(COLOR_BACKGROUND_DAYS);
		// panel.setBorder(BorderFactory.createEtchedBorder());

		return builder.getPanel();
	}

	public void addChangeListener(ChangeListener changeListener) {
		listenerList.add(ChangeListener.class, changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		listenerList.remove(ChangeListener.class, changeListener);
	}

	protected void fireChangeListener(ChangeEvent e) {
		ChangeListener[] list = listenerList.getListeners(ChangeListener.class);
		for (ChangeListener cl : list)
			cl.stateChanged(e);
	}

	private void updateAll() {
		updateMonthYear();
		updateDays();

		repaint();
	}

	private void updateMonthYear() {
		label.setText(shortDateFormat.format(calendar.getTime()));

		// Previous month
		Calendar cPreviousMonth = (Calendar) calendar.clone();
		cPreviousMonth.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		cPreviousMonth.add(Calendar.WEEK_OF_YEAR, -1);
		previousMonthAction.putValue(Action.SHORT_DESCRIPTION,
				weekTooltipsDateFormat.format(cPreviousMonth.getTime()));

		// Next month
		Calendar cNextMonth = (Calendar) calendar.clone();
		cNextMonth.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		cNextMonth.add(Calendar.WEEK_OF_YEAR, 1);
		nextMonthAction.putValue(Action.SHORT_DESCRIPTION,
				weekTooltipsDateFormat.format(cNextMonth.getTime()));
	}

	private void updateDays() {
		deselectedAllDays();

		int m = calendar.get(Calendar.MONTH);
		Calendar c2 = (Calendar) calendar.clone();
		c2.setTime(today);

		Calendar c = (Calendar) calendar.clone();
		c.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

		int firstDay = calendar.getFirstDayOfWeek();

		int dw = (c.get(Calendar.DAY_OF_WEEK) + (7 - firstDay)) % 7;
		c.add(Calendar.DAY_OF_MONTH, -dw);

		int dayIndex = DateTimeUtils.getNumberOfDays(c.getTime(),
				calendarSelected.getTime()) - 1;

		for (int i = 0; i < 7; i++) {
			days[i].setBeforeAfter(false);
			days[i].setSelected(false);
			if (c.get(Calendar.MONTH) == m) {
				days[i].setOutOfMonth(false);
			} else {
				days[i].setOutOfMonth(true);
			}

			if (!none) {
				if (DateTimeUtils.sameDay(calendarSelected.getTime(), c
						.getTime())) {
					days[i].setSelected(true);
					dayIndex = i;
				}
			}

			days[i].setDate(c.getTime());
			days[i].setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
			days[i].setToolTipText(dateFormatLong.format(c.getTime()));
			c.add(Calendar.DAY_OF_MONTH, 1);
		}

		for (int i = dayIndex - nbBeforeDays; i < dayIndex + nbAfterDays + 1; i++) {
			if (i >= 0 && i < 7 && i != dayIndex) {
				days[i].setBeforeAfter(true);
			}
		}
	}

	public void setDate(Date date) {
		if (date == null) {
			none = true;
			calendar.setTime(today);
			calendarSelected.setTime(today);
		} else {
			none = false;
			calendar.setTime(date);
			calendarSelected.setTime(date);
		}
		updateAll();
	}

	public Date getDate() {
		Date res = null;
		if (!none) {
			res = calendarSelected.getTime();
		}
		return res;
	}

	public void setNbBeforeDays(int nbBeforeDays) {
		this.nbBeforeDays = nbBeforeDays;
		updateAll();
	}

	public int getNbBeforeDays() {
		return nbBeforeDays;
	}

	public void setNbAfterDays(int nbAfterDays) {
		this.nbAfterDays = nbAfterDays;
		updateAll();
	}

	public int getNbAfterDays() {
		return nbAfterDays;
	}

	private void clearButton(final JButton button) {
		ButtonModel model = button.getModel();
		model.setSelected(false);
		model.setArmed(false);
		model.setRollover(false);
	}

	private void deselectedAllDays() {
		for (int i = 0; i < 7; i++) {
			clearButton(days[i]);
		}
	}

	private class PreviousMonthAction extends AbstractAction {

		private static final long serialVersionUID = 3146164887141455575L;

		public PreviousMonthAction() {
			super("<"); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
			updateAll();
		}
	}

	private class NextMonthAction extends AbstractAction {

		private static final long serialVersionUID = 3146164887141455575L;

		public NextMonthAction() {
			super(">"); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
			updateAll();
		}
	}

	private class DayActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JButton) {
				JDayButton b = (JDayButton) e.getSource();
				Date date = b.getDate();
				calendar.setTime(date);
				calendarSelected.setTime(date);
				none = false;
				updateAll();
				ChangeEvent ce = new ChangeEvent(JWeekCalendarPanel.this);
				fireChangeListener(ce);
			}
		}
	}

	private class JDayButton extends JButton {

		private static final long serialVersionUID = 1L;

		private static final int WIDTH = 2;

		private static final int HALF_WIDTH = WIDTH / 2;

		private boolean beforeAfter;

		private Calendar calendar;

		private boolean outOfMonth;

		public JDayButton() {
			this(""); //$NON-NLS-1$
		}

		public JDayButton(final String text) {
			super(text);
			this.calendar = Calendar.getInstance();
			this.setOpaque(false);
			this.setContentAreaFilled(false);
			this.setRolloverEnabled(true);
			this.beforeAfter = false;
			this.outOfMonth = false;
			this.setBorder(null);
		}

		private void paintGrid(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			int w = this.getWidth();
			int h = this.getHeight();

			g2.setColor(COLOR_GRID_DAYS);
			g2.drawLine(w - 1, 0, w - 1, h - 1);
			g2.drawLine(0, h - 1, w - 1, h - 1);
		}

		private void paintBlock(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			int w = this.getWidth();
			int h = this.getHeight();

			ButtonModel model = this.getModel();
			if (model.isSelected() || model.isArmed() || beforeAfter) {
				g2.setColor(COLOR_SELECTED_DAY);
			} else if (model.isRollover()) {
				g2.setColor(COLOR_ROLLOVER_DAY);
			} else {
				g2.setColor(COLOR_BACKGROUND_DAY);

			}
			g2.fillRect(0, 0, w, h);
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

		protected void paintBorder(Graphics g) {
			super.paintBorder(g);
			ButtonModel model = this.getModel();
			if (model.isSelected()) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(COLOR_TODAY);
				g2.setStroke(new BasicStroke(2));
				g2.drawRoundRect(HALF_WIDTH, HALF_WIDTH, this.getWidth()
						- HALF_WIDTH - 1, this.getHeight() - HALF_WIDTH - 1,
						this.getWidth(), this.getHeight());
			}
		}

		public void setBeforeAfter(boolean beforeAfter) {
			this.beforeAfter = beforeAfter;
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
	}

	private class JDayLabel extends JLabel {

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
			this.setFont(normalFont);
		}
	}
}
