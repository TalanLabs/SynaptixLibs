package com.synaptix.swing;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.synaptix.swing.utils.SpringUtilities;

public class JDateHourField extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final String TEXT_BUTTON_TOOLTIPS = SwingMessages.getString("JDateHourField.0"); //$NON-NLS-1$

	private JDateField date;

	private JHourField hour;

	private JPopupMenu popupMenu;

	private JButton button;

	private JCalendar calendar;

	protected Vector<ChangeListener> listChangeListener;

	public JDateHourField() {
		this(true);
	}

	public JDateHourField(boolean calendar) {
		super();
		initialize(calendar);
	}

	public void addChangeListener(ChangeListener changeListener) {
		listChangeListener.add(changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		listChangeListener.remove(changeListener);
	}

	public void setDate(Date d) {
		date.setDate(d);
		hour.setDate(d);
		calendar.setDate(d);
	}

	public Date getDate() {
		Date res = null;
		if (date.getDate() != null) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.SECOND,0);
			c.set(Calendar.MILLISECOND,0);
			c.setTime(date.getDate());
			if (hour != null) {
				c.set(Calendar.HOUR_OF_DAY, hour.getHour());
				c.set(Calendar.MINUTE, hour.getMinute());
			} else {
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
			}
			res = c.getTime();
		}
		return res;
	}

	public void setBackground(Color color) {
		if (date != null)
			date.setBackground(color);
		if (hour != null)
			hour.setBackground(color);
		super.setBackground(color);
	}

	private void initialize(boolean cal) {
		listChangeListener = new Vector<ChangeListener>();
		this.setLayout(new SpringLayout());
		this.setBorder(null);

		DateHourActionListener dhal = new DateHourActionListener();

		// Date
		date = new JDateField();
		date.addActionListener(dhal);
		date.addKeyListener(new DateHourKeyListener());
		this.add(date);

		// Button
		button = new JButton();
		ImageIcon iconeCalendar = new ImageIcon(this.getClass().getResource(
				"/images/icone_calendrier.png")); //$NON-NLS-1$
		button.setIcon(iconeCalendar);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(new ButtonActionListener());
		button.setToolTipText(TEXT_BUTTON_TOOLTIPS);
		if (cal) {
			this.add(button);
		}
		popupMenu = new JPopupMenu();
		calendar = new JCalendar();
		calendar.addChangeListener(new CalendarChangeListener());
		popupMenu.add(calendar);
		popupMenu.pack();

		// Hour
		hour = new JHourField();
		hour.addActionListener(dhal);
		hour.addKeyListener(new DateHourKeyListener());
		this.add(hour);

		if (cal)
			SpringUtilities.makeCompactGrid(this, 1, 3, 0, 0, 0, 0);
		else
			SpringUtilities.makeCompactGrid(this, 1, 2, 0, 0, 0, 0);
	}

	private class DateHourActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ChangeEvent ce = new ChangeEvent(JDateHourField.this);
			for (ChangeListener cl : listChangeListener) {
				cl.stateChanged(ce);
			}
		}
	}

	private class ButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			popupMenu.show(button, 0, button.getHeight());
		}
	}

	private class CalendarChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			date.setDate(calendar.getDate());
			popupMenu.setVisible(false);
		}
	}

	private class DateHourKeyListener implements KeyListener {
		public void keyTyped(KeyEvent e) {
			for(KeyListener kl : JDateHourField.this.getKeyListeners())
				kl.keyTyped(e);
		}

		public void keyPressed(KeyEvent e) {
			for(KeyListener kl : JDateHourField.this.getKeyListeners())
				kl.keyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			for(KeyListener kl : JDateHourField.this.getKeyListeners())
				kl.keyReleased(e);
		}
	}

}

