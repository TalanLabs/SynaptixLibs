package com.synaptix.swing;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import sun.awt.AppContext;

public class JDateField extends JTextField {

	private static final long serialVersionUID = 1L;

	private static final String TEXT_TOOLTIPS = SwingMessages.getString("JDateField.0"); //$NON-NLS-1$

	private static final Object FOCUSED_COMPONENT = new StringBuilder(
			"JTextComponent_FocusedComponent"); //$NON-NLS-1$

	private enum Selected {
		NONE, DAY, MONTH, YEAR
	}

	private boolean nulleDay;

	private int day;

	private boolean nulleMonth;

	private int month;

	private boolean nulleYear;

	private int year;

	private Selected isSelect;

	private boolean error;

	private EventListenerList listenerList;

	private DateMouseListener dateMouseListener;

	private DateFocusListener dateFocusListener;

	private DateKeyListener dateKeyListener;

	private int position;

	private boolean enabled;

	public JDateField() {
		super();
		initialize();
	}

	public JDateField(final Date d) {
		super();
		initialize();
		setDate(d);
	}

	private void initialize() {
		listenerList = new EventListenerList();

		enabled = true;
		dateMouseListener = new DateMouseListener();
		dateKeyListener = new DateKeyListener();
		dateFocusListener = new DateFocusListener();
		error = false;

		int l = this.getFontMetrics(this.getFont()).stringWidth(
				" 00 / 00 / 0000 ") //$NON-NLS-1$
				+ this.getInsets().left + this.getInsets().right;
		Dimension size = new Dimension(l, 20);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);

		this.addKeyListener(dateKeyListener);
		this.addMouseListener(dateMouseListener);
		this.addFocusListener(dateFocusListener);
		this.setCaretColor(this.getBackground());
		this.getCaret().setVisible(false);
		this.getCaret().setSelectionVisible(false);

		position = 0;

		nulleDay = nulleMonth = true;
		nulleYear = false;
		day = month = 1;
		year = getDefaultYear();

		isSelect = Selected.NONE;

		this.setToolTipText(TEXT_TOOLTIPS);
	}

	private int getDefaultYear() {
		Calendar c = Calendar.getInstance();
		int y = c.get(Calendar.YEAR);
		int m = c.get(Calendar.MONTH);
		int d = c.get(Calendar.DAY_OF_MONTH);
		if (m == Calendar.DECEMBER && d >= 15) {
			y++;
		}
		if (y > 9999)
			y = 9999;
		return y;
	}

	public void setDate(final Date date) {
		if (date == null) {
			nulleDay = nulleMonth = true;
			nulleYear = false;
			day = month = 1;
			year = getDefaultYear();
		} else {
			nulleDay = nulleMonth = nulleYear = false;
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			day = c.get(Calendar.DAY_OF_MONTH);
			month = c.get(Calendar.MONTH) + 1;
			year = c.get(Calendar.YEAR);
		}
		isSelect = Selected.NONE;
		position = 0;

		ActionEvent ae = new ActionEvent(JDateField.this, 0, ""); //$NON-NLS-1$
		fireActionListener(ae);

		repaint();
	}

	public Date getDate() {
		Date res = null;
		if (!nulleDay && !nulleMonth && !nulleYear) {
			Calendar c = Calendar.getInstance();
			c.set(year, month - 1, day, 0, 0);
			res = c.getTime();
		}
		return res;
	}

	private void verifyDate() {
		if (!nulleDay && !nulleMonth && !nulleYear) {
			Calendar c = Calendar.getInstance();
			c.set(year, month - 1, day);
			day = c.get(Calendar.DAY_OF_MONTH);
			month = c.get(Calendar.MONTH) + 1;
			year = c.get(Calendar.YEAR);
		}
	}

	public void setEnabled(final boolean b) {
		super.setEnabled(b);
		this.setEditable(b);
		this.setFocusable(b);
		enabled = b;
		// if (b) {
		// this.addKeyListener(dateKeyListener);
		// this.addMouseListener(dateMouseListener);
		// this.addFocusListener(dateFocusListener);
		// } else {
		// this.removeKeyListener(dateKeyListener);
		// this.removeMouseListener(dateMouseListener);
		// this.removeFocusListener(dateFocusListener);
		// }
	}

	public void addActionListener(final ActionListener al) {
		listenerList.add(ActionListener.class, al);
	}

	public void removeActionListener(final ActionListener al) {
		listenerList.remove(ActionListener.class, al);
	}

	protected void fireActionListener(final ActionEvent ae) {
		ActionListener[] als = listenerList.getListeners(ActionListener.class);
		for (ActionListener al : als) {
			al.actionPerformed(ae);
		}
	}

	public void setError(final boolean b) {
		error = b;
		repaint();
	}

	public void paint(Graphics g) {
		super.paint(g);
		this.setCaretColor(this.getBackground());
		this.getCaret().setVisible(false);
		this.getCaret().setSelectionVisible(false);
		if (error) {
			g.setColor(Color.red);
			g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		}
		switch (isSelect) {
		case NONE:
			break;
		case DAY:
			paintSelectedDay(g);
			break;
		case MONTH:
			paintSelectedMonth(g);
			break;
		case YEAR:
			paintSelectedYear(g);
			break;
		}
		paintDayMonthYear(g);
	}

	private void paintSelectedYear(Graphics g) {
		Dimension d = this.getSize();
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int posX = this.getInsets().left + fm.stringWidth(" 00 / 00 / "); //$NON-NLS-1$
		int posY = d.height - this.getInsets().bottom - fm.getHeight();

		g.setColor(this.getSelectionColor());
		g.fillRect(posX, posY, fm.stringWidth("0000"), fm.getHeight()); //$NON-NLS-1$
	}

	private void paintSelectedMonth(Graphics g) {
		Dimension d = this.getSize();
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int posX = this.getInsets().left + fm.stringWidth(" 00 / "); //$NON-NLS-1$
		int posY = d.height - this.getInsets().bottom - fm.getHeight();

		g.setColor(this.getSelectionColor());
		g.fillRect(posX, posY, fm.stringWidth("00"), fm.getHeight()); //$NON-NLS-1$
	}

	private void paintSelectedDay(Graphics g) {
		Dimension d = this.getSize();
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int posX = this.getInsets().left + fm.stringWidth(" "); //$NON-NLS-1$
		int posY = d.height - this.getInsets().bottom - fm.getHeight();

		g.setColor(this.getSelectionColor());
		g.fillRect(posX, posY, fm.stringWidth("00"), fm.getHeight()); //$NON-NLS-1$
	}

	private void paintDayMonthYear(Graphics g) {
		Dimension d = this.getSize();
		int posX = this.getInsets().left;
		int posY = d.height - this.getInsets().bottom
				- this.getFontMetrics(this.getFont()).getMaxDescent();

		g.setColor(this.getForeground());
		posX += this.getFontMetrics(this.getFont()).stringWidth(" "); //$NON-NLS-1$

		// Day
		StringBuffer sbDay = new StringBuffer();
		if (nulleDay) {
			sbDay.append("    "); //$NON-NLS-1$
		} else {
			if (day < 10)
				sbDay.append("0"); //$NON-NLS-1$
			sbDay.append(day);
		}
		g.setColor(this.getForeground());
		if (isSelect == Selected.DAY)
			g.setColor(this.getSelectedTextColor());
		g.drawString(sbDay.toString(), posX, posY);
		posX += this.getFontMetrics(this.getFont()).stringWidth(
				sbDay.toString());
		g.setColor(this.getForeground());
		g.drawString(" / ", posX, posY); //$NON-NLS-1$
		posX += this.getFontMetrics(this.getFont()).stringWidth(" / "); //$NON-NLS-1$

		// Month
		StringBuffer sbMonth = new StringBuffer();
		if (nulleMonth) {
			sbMonth.append("    "); //$NON-NLS-1$
		} else {
			if (month < 10)
				sbMonth.append("0"); //$NON-NLS-1$
			sbMonth.append(month);
		}
		g.setColor(this.getForeground());
		if (isSelect == Selected.MONTH)
			g.setColor(this.getSelectedTextColor());
		g.drawString(sbMonth.toString(), posX, posY);
		posX += this.getFontMetrics(this.getFont()).stringWidth(
				sbMonth.toString());
		g.setColor(this.getForeground());
		g.drawString(" / ", posX, posY); //$NON-NLS-1$
		posX += this.getFontMetrics(this.getFont()).stringWidth(" / "); //$NON-NLS-1$

		// Year
		StringBuffer sbYear = new StringBuffer();
		if (nulleYear) {
			sbYear.append("        "); //$NON-NLS-1$
		} else {
			if (year < 10)
				sbYear.append("000"); //$NON-NLS-1$
			else if (year < 100)
				sbYear.append("00"); //$NON-NLS-1$
			else if (year < 1000)
				sbYear.append("0"); //$NON-NLS-1$
			sbYear.append(year);
		}
		g.setColor(this.getForeground());
		if (isSelect == Selected.YEAR)
			g.setColor(this.getSelectedTextColor());
		g.drawString(sbYear.toString(), posX, posY);
	}

	private class DateKeyListener implements KeyListener {

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			if (enabled) {
				boolean isNum = false;
				int num = 0;
				if (isSelect != Selected.NONE) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_ENTER:
					case KeyEvent.VK_TAB:
						verifyDate();
						break;
					case KeyEvent.VK_UP:
						switch (isSelect) {
						case DAY:
							nulleDay = false;
							day++;
							if (day > 31)
								day = 1;
							break;
						case MONTH:
							nulleMonth = false;
							month++;
							if (month > 12)
								month = 1;
							break;
						case YEAR:
							nulleYear = false;
							year++;
							if (year > 9999)
								year = 0;
							break;
						}
						position = 0;
						break;
					case KeyEvent.VK_DOWN:
						switch (isSelect) {
						case DAY:
							nulleDay = false;
							day--;
							if (day < 1)
								day = 31;
							break;
						case MONTH:
							nulleMonth = false;
							month--;
							if (month < 1)
								month = 12;
							break;
						case YEAR:
							nulleYear = false;
							year--;
							if (year < 0)
								year = 9999;
							break;
						}
						position = 0;
						break;
					case KeyEvent.VK_LEFT:
						switch (isSelect) {
						case DAY:
							break;
						case MONTH:
							isSelect = Selected.DAY;
							break;
						case YEAR:
							isSelect = Selected.MONTH;
							break;
						}
						position = 0;
						break;
					case KeyEvent.VK_RIGHT:
						switch (isSelect) {
						case DAY:
							isSelect = Selected.MONTH;
							break;
						case MONTH:
							isSelect = Selected.YEAR;
							break;
						case YEAR:
							break;
						}
						position = 0;
						break;
					case KeyEvent.VK_SPACE:
						switch (isSelect) {
						case DAY:
							if (day < 1) {
								day = 1;
							}
							if (day > 31) {
								day = 31;
							}
							isSelect = Selected.MONTH;
							break;
						case MONTH:
							if (month < 1) {
								month = 1;
							}
							if (month > 12) {
								month = 12;
							}
							verifyDate();
							isSelect = Selected.YEAR;
							break;
						case YEAR:
							verifyDate();
							isSelect = Selected.DAY;
							break;
						}
						position = 0;
						break;
					case KeyEvent.VK_DELETE:
						switch (isSelect) {
						case DAY:
							nulleDay = true;
							day = 1;
							break;
						case MONTH:
							nulleMonth = true;
							month = 1;
							break;
						case YEAR:
							nulleYear = true;
							year = getDefaultYear();
							break;
						}
						position = 0;
						break;
					case KeyEvent.VK_BACK_SPACE:
						switch (isSelect) {
						case DAY:
							day = day / 10;
							if (day == 0)
								day = 1;
							break;
						case MONTH:
							month = month / 10;
							if (month == 0)
								month = 1;
							break;
						case YEAR:
							year = year / 10;
							if (year == 0)
								year = 1;
							break;
						}
						if (position > 0)
							position--;
						break;
					case KeyEvent.VK_0:
						if (e.isShiftDown()) {
							isNum = true;
							num = 0;
						}
						break;
					case KeyEvent.VK_NUMPAD0:
						isNum = true;
						num = 0;
						break;
					case KeyEvent.VK_1:
						if (e.isShiftDown()) {
							isNum = true;
							num = 1;
						}
						break;
					case KeyEvent.VK_NUMPAD1:
						isNum = true;
						num = 1;
						break;
					case KeyEvent.VK_2:
						if (e.isShiftDown()) {
							isNum = true;
							num = 2;
						}
						break;
					case KeyEvent.VK_NUMPAD2:
						isNum = true;
						num = 2;
						break;
					case KeyEvent.VK_3:
						if (e.isShiftDown()) {
							isNum = true;
							num = 3;
						}
						break;
					case KeyEvent.VK_NUMPAD3:
						isNum = true;
						num = 3;
						break;
					case KeyEvent.VK_4:
						if (e.isShiftDown()) {
							isNum = true;
							num = 4;
						}
						break;
					case KeyEvent.VK_NUMPAD4:
						isNum = true;
						num = 4;
						break;
					case KeyEvent.VK_5:
						if (e.isShiftDown()) {
							isNum = true;
							num = 5;
						}
						break;
					case KeyEvent.VK_NUMPAD5:
						isNum = true;
						num = 5;
						break;
					case KeyEvent.VK_6:
						if (e.isShiftDown()) {
							isNum = true;
							num = 6;
						}
						break;
					case KeyEvent.VK_NUMPAD6:
						isNum = true;
						num = 6;
						break;
					case KeyEvent.VK_7:
						if (e.isShiftDown()) {
							isNum = true;
							num = 7;
						}
						break;
					case KeyEvent.VK_NUMPAD7:
						isNum = true;
						num = 7;
						break;
					case KeyEvent.VK_8:
						if (e.isShiftDown()) {
							isNum = true;
							num = 8;
						}
						break;
					case KeyEvent.VK_NUMPAD8:
						isNum = true;
						num = 8;
						break;
					case KeyEvent.VK_9:
						if (e.isShiftDown()) {
							isNum = true;
							num = 9;
						}
						break;
					case KeyEvent.VK_NUMPAD9:
						isNum = true;
						num = 9;
						break;
					default:
						break;
					}
					if (isNum) {
						switch (isSelect) {
						case DAY:
							if (nulleDay) {
								day = num;
							} else {
								day = (day - (int) (day / 10) * 10) * 10 + num;
							}
							nulleDay = false;
							if (day < 0) {
								day = 0;
							}
							if (day > 99) {
								day = 99;
							}
							position++;
							break;
						case MONTH:
							if (nulleMonth) {
								month = num;
							} else {
								month = (month - (int) (month / 10) * 10) * 10
										+ num;
							}
							nulleMonth = false;
							if (month < 0) {
								month = 0;
							}
							if (month > 99) {
								month = 99;
							}
							position++;
							break;
						case YEAR:
							if (nulleYear) {
								year = num;
							} else {
								year = (year - (int) (year / 1000) * 1000) * 10
										+ num;
							}
							nulleYear = false;
							if (year < 0) {
								year = 0;
							}
							if (year > 9999) {
								year = 9999;
							}
							position++;
							break;
						}
						if (position >= 2 && isSelect != Selected.YEAR) {
							position = 0;
							switch (isSelect) {
							case DAY:
								if (day < 1) {
									day = 1;
								}
								if (day > 31) {
									day = 31;
								}
								isSelect = Selected.MONTH;
								break;
							case MONTH:
								if (month < 1) {
									month = 1;
								}
								if (month > 12) {
									month = 12;
								}
								verifyDate();
								isSelect = Selected.YEAR;
								break;
							case YEAR:
								verifyDate();
							}
						}
					}
					ActionEvent ae = new ActionEvent(JDateField.this, 0, ""); //$NON-NLS-1$
					fireActionListener(ae);

					repaint();
				}
			}
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	private class DateMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (enabled) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					FontMetrics fm = JDateField.this
							.getFontMetrics(JDateField.this.getFont());
					int deltaDayX = e.getX() - JDateField.this.getInsets().left
							- fm.stringWidth(" "); //$NON-NLS-1$
					int deltaMonthX = e.getX()
							- JDateField.this.getInsets().left
							- fm.stringWidth(" 00 / "); //$NON-NLS-1$
					int deltaYearX = e.getX()
							- JDateField.this.getInsets().left
							- fm.stringWidth(" 00 / 00 / "); //$NON-NLS-1$

					if (deltaDayX >= 0 && deltaDayX < fm.stringWidth("00")) //$NON-NLS-1$
						isSelect = Selected.DAY;
					else if (deltaMonthX >= 0
							&& deltaMonthX < fm.stringWidth("00")) //$NON-NLS-1$
						isSelect = Selected.MONTH;
					else if (deltaYearX >= 0
							&& deltaYearX < fm.stringWidth("0000")) //$NON-NLS-1$
						isSelect = Selected.YEAR;
					else
						isSelect = Selected.NONE;
					repaint();
					position = 0;
				}
			}

		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	private class DateFocusListener implements FocusListener {

		public void focusGained(FocusEvent fe) {
			if (enabled) {
				AppContext.getAppContext().put(FOCUSED_COMPONENT,
						fe.getSource());
				if (isSelect == Selected.NONE)
					isSelect = Selected.DAY;
				verifyDate();
				repaint();
			}
		}

		public void focusLost(FocusEvent fe) {
			if (enabled) {
				verifyDate();
				isSelect = Selected.NONE;
				repaint();
			}
		}
	}

	protected Document createDefaultModel() {
		return new DateDocument();
	}

	private class DateDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());

		JDateField c = new JDateField();
		c.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDateField d = (JDateField) e.getSource();
				System.out.println(d.getDate());
			}
		});

		frame.getContentPane().add(c, BorderLayout.CENTER);

		frame.pack();

		frame.setVisible(true);
	}
}

