package com.synaptix.swing;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import sun.awt.AppContext;

public class JHourField extends JTextField {

	private static final long serialVersionUID = 1L;

	private static final String TEXT_TOOLTIPS = SwingMessages.getString("JHourField.0"); //$NON-NLS-1$

	private static final Object FOCUSED_COMPONENT = new StringBuilder(
			"JTextComponent_FocusedComponent"); //$NON-NLS-1$

	private enum Selected {
		NONE, HOUR, MINUTE
	}

	private Calendar calendar;

	private boolean nulleHour;

	private boolean nulleMinute;

	private Selected isSelect;

	private boolean error;

	private List<ActionListener> listActionListener;

	private DateMouseListener dateMouseListener;

	private DateFocusListener dateFocusListener;

	private DateKeyListener dateKeyListener;

	private int position;

	private boolean enabled;

	public JHourField() {
		super();
		initialize();
	}

	public JHourField(Date d) {
		super();
		initialize();
		setDate(d);
	}

	public JHourField(int h, int m) {
		super();
		initialize();
		setHour(h);
		setMinute(m);
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		this.setEditable(b);
		this.setFocusable(b);
		enabled = b;
	}

	public void setDate(Date d) {
		if (d == null) {
			nulleHour = nulleMinute = true;
			calendar.setTime(new Date());
		} else {
			nulleHour = nulleMinute = false;
			calendar.setTime(d);
		}
		isSelect = Selected.NONE;
		position = 0;
		ActionEvent ae = new ActionEvent(this, 0, ""); //$NON-NLS-1$
		for (ActionListener cl : listActionListener) {
			cl.actionPerformed(ae);
		}
		repaint();
	}

	public Date getDate() {
		Date res = null;
		if (!nulleHour && !nulleMinute) {
			res = calendar.getTime();
		}
		return res;
	}

	public int getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public void setHour(int hour) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
	}

	public int getMinute() {
		return calendar.get(Calendar.MINUTE);
	}

	public void setMinute(int minute) {
		calendar.set(Calendar.MINUTE, minute);
	}

	private void initialize() {
		enabled = true;
		calendar = Calendar.getInstance();
		dateMouseListener = new DateMouseListener();
		dateKeyListener = new DateKeyListener();
		dateFocusListener = new DateFocusListener();
		listActionListener = new ArrayList<ActionListener>();
		error = false;

		int l = this.getFontMetrics(this.getFont()).stringWidth(" 00 : 00 ") //$NON-NLS-1$
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

		nulleHour = nulleMinute = true;
		position = 0;
		calendar.setTime(new Date());
		isSelect = Selected.NONE;

		this.setToolTipText(TEXT_TOOLTIPS);
	}

	public void addActionListener(ActionListener al) {
		listActionListener.add(al);
	}

	public void removeActionListener(ActionListener al) {
		listActionListener.remove(al);
	}

	public void setError(boolean b) {
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
		case HOUR:
			paintSelectedHour(g);
			break;
		case MINUTE:
			paintSelectedMinute(g);
			break;
		}
		paintHourMinute(g);
	}

	private void paintSelectedMinute(Graphics g) {
		Dimension d = this.getSize();
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int posX = this.getInsets().left + fm.stringWidth(" 00 : "); //$NON-NLS-1$
		int posY = d.height - this.getInsets().bottom - fm.getHeight();

		g.setColor(this.getSelectionColor());
		g.fillRect(posX, posY, fm.stringWidth("00"), fm.getHeight()); //$NON-NLS-1$
	}

	private void paintSelectedHour(Graphics g) {
		Dimension d = this.getSize();
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int posX = this.getInsets().left + fm.stringWidth(" "); //$NON-NLS-1$
		int posY = d.height - this.getInsets().bottom - fm.getHeight();

		g.setColor(this.getSelectionColor());
		g.fillRect(posX, posY, fm.stringWidth("00"), fm.getHeight()); //$NON-NLS-1$
	}

	private void paintHourMinute(Graphics g) {
		Dimension d = this.getSize();
		int posX = this.getInsets().left;
		int posY = d.height - this.getInsets().bottom
				- this.getFontMetrics(this.getFont()).getMaxDescent();

		g.setColor(this.getForeground());
		posX += this.getFontMetrics(this.getFont()).stringWidth(" "); //$NON-NLS-1$

		// Hour
		StringBuffer sbHour = new StringBuffer();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (nulleHour) {
			sbHour.append("    "); //$NON-NLS-1$
		} else {
			if (hour < 10)
				sbHour.append("0"); //$NON-NLS-1$
			sbHour.append(hour);
		}
		g.setColor(this.getForeground());
		if (isSelect == Selected.HOUR)
			g.setColor(this.getSelectedTextColor());
		g.drawString(sbHour.toString(), posX, posY);
		posX += this.getFontMetrics(this.getFont()).stringWidth(
				sbHour.toString());
		g.setColor(this.getForeground());
		g.drawString(" : ", posX, posY); //$NON-NLS-1$
		posX += this.getFontMetrics(this.getFont()).stringWidth(" : "); //$NON-NLS-1$

		// Minute
		StringBuffer sbMinute = new StringBuffer();
		int minute = calendar.get(Calendar.MINUTE);
		if (nulleMinute) {
			sbMinute.append("    "); //$NON-NLS-1$
		} else {
			if (minute < 10)
				sbMinute.append("0"); //$NON-NLS-1$
			sbMinute.append(minute);
		}
		g.setColor(this.getForeground());
		if (isSelect == Selected.MINUTE)
			g.setColor(this.getSelectedTextColor());
		g.drawString(sbMinute.toString(), posX, posY);
	}

	private class DateMouseListener implements MouseListener {

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (enabled) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					FontMetrics fm = JHourField.this
							.getFontMetrics(JHourField.this.getFont());
					int deltaHourX = e.getX()
							- JHourField.this.getInsets().left
							- fm.stringWidth(" "); //$NON-NLS-1$
					int deltaMinuteX = e.getX()
							- JHourField.this.getInsets().left
							- fm.stringWidth(" 00 : "); //$NON-NLS-1$

					if (deltaHourX >= 0 && deltaHourX < fm.stringWidth("00")) //$NON-NLS-1$
						isSelect = Selected.HOUR;
					else if (deltaMinuteX >= 0
							&& deltaMinuteX < fm.stringWidth("00")) //$NON-NLS-1$
						isSelect = Selected.MINUTE;
					else
						isSelect = Selected.NONE;
					position = 0;
					repaint();
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	private class DateKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (enabled) {
				boolean isNum = false;
				int num = 0;
				if (isSelect != Selected.NONE) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_UP:
						switch (isSelect) {
						case HOUR:
							nulleHour = false;
							calendar.roll(Calendar.HOUR_OF_DAY, true);
							break;
						case MINUTE:
							nulleMinute = false;
							calendar.roll(Calendar.MINUTE, true);
							break;
						}
						position = 0;
						break;
					case KeyEvent.VK_DOWN:
						switch (isSelect) {
						case HOUR:
							nulleHour = false;
							calendar.roll(Calendar.HOUR_OF_DAY, false);
							break;
						case MINUTE:
							nulleMinute = false;
							calendar.roll(Calendar.MINUTE, false);
							break;
						}
						position = 0;
						break;
					case KeyEvent.VK_LEFT:
						if (isSelect == Selected.MINUTE)
							isSelect = Selected.HOUR;
						position = 0;
						break;
					case KeyEvent.VK_RIGHT:
						if (isSelect == Selected.HOUR)
							isSelect = Selected.MINUTE;
						position = 0;
						break;
					case KeyEvent.VK_SPACE:
						if (isSelect == Selected.HOUR)
							isSelect = Selected.MINUTE;
						else
							isSelect = Selected.HOUR;
						position = 0;
						break;
					case KeyEvent.VK_DELETE:
						if (isSelect == Selected.HOUR)
							nulleHour = true;
						else
							nulleMinute = true;
						position = 0;
						break;
					case KeyEvent.VK_BACK_SPACE:
						if (isSelect == Selected.HOUR) {
							int hour = calendar.get(Calendar.HOUR_OF_DAY);
							calendar.set(Calendar.HOUR_OF_DAY, hour / 10);
						} else {
							int minute = calendar.get(Calendar.MINUTE);
							calendar.set(Calendar.MINUTE, minute / 10);
						}
						if (position > 0)
							position--;
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
						case HOUR:
							nulleHour = false;
							int hour = calendar.get(Calendar.HOUR_OF_DAY);
							int hour2 = (hour - (int) (hour / 10) * 10) * 10
									+ num;
							if (hour2 < 0)
								hour2 = 0;
							else if (hour2 > calendar
									.getActualMaximum(Calendar.HOUR_OF_DAY))
								hour2 = num;
							calendar.set(Calendar.HOUR_OF_DAY, hour2);
							position++;
							break;
						case MINUTE:
							nulleMinute = false;
							int minute = calendar.get(Calendar.MINUTE);
							int minute2 = (minute - (int) (minute / 10) * 10)
									* 10 + num;
							if (minute2 < 0)
								minute2 = 0;
							else if (minute2 > calendar
									.getActualMaximum(Calendar.MINUTE))
								minute2 = num;
							calendar.set(Calendar.MINUTE, minute2);
							position++;
							break;
						}
						if (position >= 2) {
							position = 0;
							if (isSelect == Selected.HOUR)
								isSelect = Selected.MINUTE;
						}
					}
					ActionEvent ae = new ActionEvent(this, 0, ""); //$NON-NLS-1$
					for (ActionListener cl : listActionListener) {
						cl.actionPerformed(ae);
					}
					repaint();
				}
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}

	private class DateFocusListener implements FocusListener {

		public void focusGained(FocusEvent fe) {
			if (enabled) {
				AppContext.getAppContext().put(FOCUSED_COMPONENT,
						fe.getSource());
				if (isSelect == Selected.NONE)
					isSelect = Selected.HOUR;
				position = 0;
				repaint();
			}
		}

		public void focusLost(FocusEvent fe) {
			if (enabled) {
				isSelect = Selected.NONE;
				position = 0;
				repaint();
			}
		}
	}

	protected Document createDefaultModel() {
		return new HourDocument();
	}

	private class HourDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {

		}
	}
}

