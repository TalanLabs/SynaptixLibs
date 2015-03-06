package com.synaptix.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class JCalendar extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Color COLOR_BACKGROUND_MONTH_YEAR;

	private static final Color COLOR_FORGROUND_MONTH_YEAR_ON;

	private static final Color COLOR_FORGROUND_MONTH_YEAR_ROLLOVER;

	private static final Color COLOR_BACKGROUND_TODAY;

	private static final Color COLOR_FORGROUND_NONE_ON;

	private static final Color COLOR_FORGROUND_NONE_ROLLOVER;

	private static final Color COLOR_BACKGROUND_DAYS;

	private static final Color COLOR_FORGROUND_DAYS;

	private static final Color COLOR_BACKGROUND_NAME_DAY;

	private static final Color COLOR_FORGROUND_NAME_DAY;

	private static final Color COLOR_GRID_DAYS;

	private static final Color COLOR_SELECTED_DAY;

	private static final Color COLOR_ROLLOVER_DAY;

	private static final Color COLOR_BACKGROUND_DAY;

	private static final Color COLOR_TODAY;

	private static final Color COLOR_OUT_OF_MONTH_DAY;

	private static final Color COLOR_IN_OF_MONTH_DAY;

	private static final String TEXT_TODAY = SwingMessages.getString("JCalendar.0"); //$NON-NLS-1$

	private static final String TEXT_NONE = SwingMessages.getString("JCalendar.1"); //$NON-NLS-1$

	private static final String TEXT_NONE_TOOLTIPS = SwingMessages.getString("JCalendar.2"); //$NON-NLS-1$

	private static final Locale locale;

	private String[] listMonthNames;

	private String[] listDayNames;

	private String[] listDayAbbreviations;

	private DateFormat dateFormatShort;

	private DateFormat dateFormatLong;

	private Date today;

	private Calendar calendar;

	private Calendar calendarSelected;

	private JLabel monthLabel;

	private JLabel yearLabel;

	private boolean none;

	private JDayButton[] days;

	private JWeekLabel[] weeks;

	private JNoneButton noneButton;

	private JPreviousNextButton previousMonthButton;

	private JPreviousNextButton previousYearButton;

	private JPreviousNextButton nextMonthButton;

	private JPreviousNextButton nextYearButton;

	private JTodayButton todayButton;

	private Font boldFont;

	private Font italicFont;

	private EventListenerList listenerList;

	static {
		locale = Locale.getDefault();

		COLOR_BACKGROUND_MONTH_YEAR = new Color(80, 80, 255);
		COLOR_FORGROUND_MONTH_YEAR_ON = new Color(255, 255, 255);
		COLOR_FORGROUND_MONTH_YEAR_ROLLOVER = new Color(130, 190, 255);

		COLOR_BACKGROUND_TODAY = new Color(80, 80, 255);

		COLOR_FORGROUND_NONE_ON = new Color(255, 255, 255);
		COLOR_FORGROUND_NONE_ROLLOVER = new Color(130, 190, 255);

		COLOR_BACKGROUND_DAYS = new Color(65, 177, 245);
		COLOR_FORGROUND_DAYS = new Color(255, 255, 255);
		COLOR_BACKGROUND_NAME_DAY = new Color(40, 125, 255);
		COLOR_FORGROUND_NAME_DAY = new Color(255, 255, 255);
		COLOR_GRID_DAYS = new Color(124, 180, 250);
		COLOR_SELECTED_DAY = new Color(58, 136, 251);
		COLOR_ROLLOVER_DAY = new Color(58, 136, 251);
		COLOR_BACKGROUND_DAY = new Color(255, 255, 255);
		COLOR_TODAY = new Color(255, 0, 0);
		COLOR_OUT_OF_MONTH_DAY = new Color(132, 191, 250);
		COLOR_IN_OF_MONTH_DAY = new Color(0, 0, 0);
	}

	public JCalendar() {
		super(new BorderLayout());

		Calendar c = Calendar.getInstance(locale);
		today = c.getTime();

		calendar = Calendar.getInstance(locale);
		calendar.setTime(today);

		calendarSelected = (Calendar) calendar.clone();
		none = true;

		this.setFont(this.getFont().deriveFont(11.0f));

		boldFont = this.getFont().deriveFont(Font.BOLD);
		italicFont = this.getFont().deriveFont(Font.ITALIC);

		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);

		listMonthNames = dateFormatSymbols.getMonths();
		listDayNames = dateFormatSymbols.getWeekdays();
		listDayAbbreviations = dateFormatSymbols.getShortWeekdays();

		dateFormatShort = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		dateFormatLong = DateFormat.getDateInstance(DateFormat.FULL, locale);

		listenerList = new EventListenerList();

		initComponents();

		update();
		
		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private JComponent buildEditorPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(buildMonthYearPanel(), BorderLayout.NORTH);
		panel.add(getDays(), BorderLayout.CENTER);
		this.add(getCurrentDay(), BorderLayout.SOUTH);
		return panel;
	}
	
	private void initComponents() {
		previousYearButton = new JPreviousNextButton("<<"); //$NON-NLS-1$
		previousYearButton.addActionListener(new PreviousYearActionListener());
		previousYearButton.setMargin(new Insets(0, 5, 0, 5));

		previousMonthButton = new JPreviousNextButton("<"); //$NON-NLS-1$
		previousMonthButton
				.addActionListener(new PreviousMonthActionListener());
		previousMonthButton.setMargin(new Insets(0, 5, 0, 5));

		monthLabel = new JLabel("Octobre"); //$NON-NLS-1$
		monthLabel.setFont(boldFont);
		monthLabel.setForeground(COLOR_FORGROUND_MONTH_YEAR_ON);

		yearLabel = new JLabel("9999"); //$NON-NLS-1$
		yearLabel.setFont(boldFont);
		yearLabel.setForeground(COLOR_FORGROUND_MONTH_YEAR_ON);

		nextMonthButton = new JPreviousNextButton(">"); //$NON-NLS-1$
		nextMonthButton.setMargin(new Insets(0, 5, 0, 5));
		nextMonthButton.addActionListener(new NextMonthActionListener());

		nextYearButton = new JPreviousNextButton(">>"); //$NON-NLS-1$
		nextYearButton.setMargin(new Insets(0, 5, 0, 5));
		nextYearButton.addActionListener(new NextYearActionListener());
		
		days = new JDayButton[6 * 7];
		weeks = new JWeekLabel[6];
		DayActionListener dayActionListener = new DayActionListener();
		int k = 0;
		for (int j = 0; j < 6; j++) {
			for (int i = 0; i < 8; i++) {
				if (i == 0) {
					weeks[j] = new JWeekLabel();
					weeks[j].setFont(boldFont);
				} else {
					days[k] = new JDayButton();
					days[k].addActionListener(dayActionListener);
					days[k].setFont(boldFont);
					k++;
				}
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append(TEXT_TODAY);
		sb.append(": "); //$NON-NLS-1$
		sb.append(dateFormatShort.format(today));
		todayButton = new JTodayButton(sb.toString());
		todayButton.addActionListener(new TodayActionListener());
		todayButton.setToolTipText(dateFormatLong.format(today));

		noneButton = new JNoneButton();
		noneButton.addActionListener(new NoneActionListener());
		noneButton.setEnabled(false);
		noneButton.setToolTipText(TEXT_NONE_TOOLTIPS);
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

	private void update() {
		updateMonthYear();
		updateDays();
		updateCurrentDay();

		repaint();
	}

	private void updateMonthYear() {
		int m = calendar.get(Calendar.MONTH);
		StringBuffer sbMonth = new StringBuffer();
		sbMonth.append(listMonthNames[m]);
		sbMonth.append(" ("); //$NON-NLS-1$
		sbMonth.append(m + 1);
		sbMonth.append(") "); //$NON-NLS-1$
		monthLabel.setText(sbMonth.toString());

		yearLabel.setText(String.valueOf(calendar.get(Calendar.YEAR)));

		// Previous year
		Calendar cPreviousYear = (Calendar) calendar.clone();
		cPreviousYear.add(Calendar.YEAR, -1);
		previousYearButton.setToolTipText(String.valueOf(cPreviousYear
				.get(Calendar.YEAR)));

		// Previous month
		Calendar cPreviousMonth = (Calendar) calendar.clone();
		cPreviousMonth.add(Calendar.MONTH, -1);
		int mP = cPreviousMonth.get(Calendar.MONTH);
		StringBuffer sbPreviousMonth = new StringBuffer();
		sbPreviousMonth.append(listMonthNames[mP]);
		sbPreviousMonth.append(" ("); //$NON-NLS-1$
		sbPreviousMonth.append(mP + 1);
		sbPreviousMonth.append(")"); //$NON-NLS-1$
		previousMonthButton.setToolTipText(sbPreviousMonth.toString());

		// Next month
		Calendar cNextMonth = (Calendar) calendar.clone();
		cNextMonth.add(Calendar.MONTH, 1);
		int mN = cNextMonth.get(Calendar.MONTH);
		StringBuffer sbNextMonth = new StringBuffer();
		sbNextMonth.append(listMonthNames[mN]);
		sbNextMonth.append(" ("); //$NON-NLS-1$
		sbNextMonth.append(mN + 1);
		sbNextMonth.append(")"); //$NON-NLS-1$
		nextMonthButton.setToolTipText(sbNextMonth.toString());

		// Next year
		Calendar cNextYear = (Calendar) calendar.clone();
		cNextYear.add(Calendar.YEAR, 1);
		nextYearButton.setToolTipText(String.valueOf(cNextYear
				.get(Calendar.YEAR)));
	}

	private void updateDays() {
		deselectedAllDays();

		int m = calendar.get(Calendar.MONTH);
		Calendar c2 = (Calendar) calendar.clone();
		c2.setTime(today);

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

				days[k].setRed(false);
				days[k].setSelected(false);
				if (c.get(Calendar.MONTH) == m) {
					days[k].setOutOfMonth(false);
					if (c2.get(Calendar.YEAR) == c.get(Calendar.YEAR)
							&& c2.get(Calendar.MONTH) == c.get(Calendar.MONTH)
							&& c2.get(Calendar.DAY_OF_MONTH) == c
									.get(Calendar.DAY_OF_MONTH)) {
						days[k].setRed(true);
					}
				} else {
					days[k].setOutOfMonth(true);
				}

				if (!none
						&& calendarSelected.get(Calendar.YEAR) == c
								.get(Calendar.YEAR)
						&& calendarSelected.get(Calendar.MONTH) == c
								.get(Calendar.MONTH)
						&& calendarSelected.get(Calendar.DAY_OF_MONTH) == c
								.get(Calendar.DAY_OF_MONTH)) {
					days[k].setSelected(true);
				} else {
					days[k].setSelected(false);
				}

				days[k].setDate(c.getTime());
				days[k].setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
				days[k].setToolTipText(dateFormatLong.format(c.getTime()));
				c.add(Calendar.DAY_OF_MONTH, 1);
				k++;
			}
		}
	}

	private void updateCurrentDay() {
		Calendar c = Calendar.getInstance(Locale.FRENCH);
		today = c.getTime();

		StringBuffer sb = new StringBuffer();
		sb.append(TEXT_TODAY);
		sb.append(": "); //$NON-NLS-1$
		sb.append(dateFormatShort.format(today));

		todayButton.setText(sb.toString());
		todayButton.setToolTipText(dateFormatLong.format(today));
	}

	private JPanel getCurrentDay() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(COLOR_BACKGROUND_TODAY);

		JPanel currentDayPanel = new JPanel();
		currentDayPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		currentDayPanel.setOpaque(false);

		JTodayLabel todayCircleLabel = new JTodayLabel();
		todayCircleLabel.setFont(boldFont);
		currentDayPanel.add(todayCircleLabel, BorderLayout.WEST);

		currentDayPanel.add(todayButton, BorderLayout.CENTER);
		panel.add(currentDayPanel, BorderLayout.WEST);

		panel.add(noneButton, BorderLayout.EAST);

		return panel;
	}

	private JPanel getDays() {
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

	private JPanel buildMonthYearPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(COLOR_BACKGROUND_MONTH_YEAR);

		JPanel previousPanel = new JPanel();
		previousPanel.setLayout(new BorderLayout());
		previousPanel.setOpaque(false);

		// Previous year
		previousPanel.add(previousYearButton, BorderLayout.WEST);

		// Previous month
		previousPanel.add(previousMonthButton, BorderLayout.EAST);

		panel.add(previousPanel, BorderLayout.WEST);

		JPanel monthYearPanel = new JPanel();
		monthYearPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		monthYearPanel.setOpaque(false);

		// Month
		StringBuffer sbMonth = new StringBuffer();
		sbMonth.append(getMonthMaxLength());
		sbMonth.append(" (99)"); //$NON-NLS-1$
		monthYearPanel.add(monthLabel);

		// Year
		monthYearPanel.add(yearLabel);

		panel.add(monthYearPanel, BorderLayout.CENTER);

		JPanel nextPanel = new JPanel();
		nextPanel.setLayout(new BorderLayout());
		nextPanel.setOpaque(false);

		// Next month
		nextPanel.add(nextMonthButton, BorderLayout.WEST);

		// Next year
		nextPanel.add(nextYearButton, BorderLayout.EAST);

		panel.add(nextPanel, BorderLayout.EAST);

		return panel;
	}

	private String getMonthMaxLength() {
		String res = listMonthNames[0];
		int max = listMonthNames[0].length();

		for (int i = 1; i < listMonthNames.length; i++) {
			int c = listMonthNames[i].length();
			if (c > max) {
				max = c;
				res = listMonthNames[i];
			}
		}

		return res;
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
		noneButton.setEnabled(!none);
		update();
	}

	public Date getDate() {
		Date res = null;
		if (!none) {
			res = calendarSelected.getTime();
		}
		return res;
	}

	private void clearButton(final JButton button) {
		ButtonModel model = button.getModel();
		model.setSelected(false);
		model.setArmed(false);
		model.setRollover(false);
	}

	private void deselectedAllDays() {
		clearButton(todayButton);
		clearButton(noneButton);

		for (int i = 0; i < 6 * 7; i++) {
			clearButton(days[i]);
		}
	}

	private class TodayActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			none = false;
			noneButton.setEnabled(true);
			calendar.setTime(today);
			calendarSelected.setTime(today);
			update();
			ChangeEvent ce = new ChangeEvent(JCalendar.this);
			fireChangeListener(ce);
		}
	}

	private class NoneActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			none = true;
			noneButton.setEnabled(false);
			update();
			ChangeEvent ce = new ChangeEvent(JCalendar.this);
			fireChangeListener(ce);
		}
	}

	private class PreviousYearActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			calendar.add(Calendar.YEAR, -1);
			update();
		}
	}

	private class PreviousMonthActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			calendar.add(Calendar.MONTH, -1);
			update();
		}
	}

	private class NextMonthActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			calendar.add(Calendar.MONTH, 1);
			update();
		}
	}

	private class NextYearActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			calendar.add(Calendar.YEAR, 1);
			update();
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
				noneButton.setEnabled(true);
				update();
				ChangeEvent ce = new ChangeEvent(JCalendar.this);
				fireChangeListener(ce);
			}
		}
	}

	private class JPreviousNextButton extends JButton {

		private static final long serialVersionUID = 1L;

		public JPreviousNextButton(final String text) {
			super(text);
			this.setContentAreaFilled(false);
			this.setRolloverEnabled(true);
			this.setFont(boldFont);
		}

		protected void paintComponent(Graphics g) {
			ButtonModel model = this.getModel();
			if (model.isSelected() || model.isArmed()) {
				this.setForeground(COLOR_FORGROUND_MONTH_YEAR_ROLLOVER);
			} else if (model.isRollover()) {
				this.setForeground(COLOR_FORGROUND_MONTH_YEAR_ROLLOVER);
			} else {
				this.setForeground(COLOR_FORGROUND_MONTH_YEAR_ON);
			}
			super.paintComponent(g);
		}

	}

	private class JTodayButton extends JButton {

		private static final long serialVersionUID = 1L;

		public JTodayButton(final String text) {
			super(text);
			this.setContentAreaFilled(false);
			this.setRolloverEnabled(true);
			this.setBorder(null);
			this.setFont(boldFont);
		}

		protected void paintComponent(Graphics g) {
			ButtonModel model = this.getModel();
			if (model.isSelected() || model.isArmed()) {
				this.setForeground(COLOR_FORGROUND_NONE_ROLLOVER);
			} else if (model.isRollover()) {
				this.setForeground(COLOR_FORGROUND_NONE_ROLLOVER);
			} else {
				this.setForeground(COLOR_FORGROUND_NONE_ON);
			}
			super.paintComponent(g);
		}

	}

	private class JNoneButton extends JButton {

		private static final long serialVersionUID = 1L;

		public JNoneButton() {
			super(TEXT_NONE);
			this.setContentAreaFilled(false);
			this.setRolloverEnabled(true);
			this.setBorder(null);
			this.setFont(boldFont);
		}

		protected void paintComponent(Graphics g) {
			ButtonModel model = this.getModel();
			if (model.isSelected() || model.isArmed()) {
				this.setForeground(COLOR_FORGROUND_NONE_ROLLOVER);
			} else if (model.isRollover()) {
				this.setForeground(COLOR_FORGROUND_NONE_ROLLOVER);
			} else {
				this.setForeground(COLOR_FORGROUND_NONE_ON);
			}
			super.paintComponent(g);
		}

	}

	private class JDayButton extends JButton {

		private static final long serialVersionUID = 1L;

		private static final int WIDTH = 2;

		private static final int HALF_WIDTH = WIDTH / 2;

		private boolean red;

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
			this.red = false;
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
			if (model.isSelected() || model.isArmed()) {
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
			if (red) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(COLOR_TODAY);
				g2.setStroke(new BasicStroke(2));
				g2.drawRoundRect(HALF_WIDTH, HALF_WIDTH, this.getWidth()
						- HALF_WIDTH - 1, this.getHeight() - HALF_WIDTH - 1,
						this.getWidth(), this.getHeight());
			}
		}

		public void setRed(final boolean red) {
			this.red = red;
			repaint();
		}

		public Date getDate() {
			return calendar.getTime();
		}

		public void setDate(final Date date) {
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
		}
	}

	private class JWeekLabel extends JLabel {

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

	private class JTodayLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		private static final int WIDTH = 2;

		private static final int HALF_WIDTH = WIDTH / 2;

		public JTodayLabel() {
			super("99", JLabel.CENTER); //$NON-NLS-1$
		}

		protected void paintBorder(Graphics g) {
			super.paintBorder(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(COLOR_TODAY);
			g2.setStroke(new BasicStroke(2));
			g2.drawRoundRect(HALF_WIDTH, HALF_WIDTH, this.getWidth()
					- HALF_WIDTH - 1, this.getHeight() - HALF_WIDTH - 1, this
					.getWidth(), this.getHeight());
		}

		protected void paintComponent(Graphics g) {
		}
	}
}
