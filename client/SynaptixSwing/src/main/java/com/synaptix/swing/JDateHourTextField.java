package com.synaptix.swing;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.formatter.RelativeDateFormatter;
import com.synaptix.swing.utils.formatter.MultiDateFormat;

public class JDateHourTextField extends JFormattedTextField {

	private static final long serialVersionUID = -9003389410306382227L;

	private static final ImageIcon iconCalendar = new ImageIcon(
			JDateHourTextField.class.getResource("/images/iconCalendar.png")); //$NON-NLS-1$

	private JLabel label;

	private JPopupMenu popupMenu;

	private JCalendarPanel calendarPanel;

	private boolean showCalendar;

	private Action acceptAction;

	private Action nullAction;

	private JSpinner hourSpinner;

	private JSpinner minuteSpinner;

	public JDateHourTextField() {
		this(true);
	}

	public JDateHourTextField(boolean enableNull) {
		this(enableNull, false, true);
	}

	public JDateHourTextField(boolean enableShortcuts,
			boolean commitsOnValidEdit) {
		this(true, enableShortcuts, commitsOnValidEdit);
	}

	public JDateHourTextField(boolean enableNull, boolean enableShortcuts,
			boolean commitsOnValidEdit) {
		super();

		initActions();
		initComponents();

		DateFormat normalDateHourFormat = new SimpleDateFormat("dd/MM/yy HH:mm"); //$NON-NLS-1$
		normalDateHourFormat.setLenient(false);

		DateFormat normalDateHourFormat2 = new SimpleDateFormat("dd/MM/yy HHmm"); //$NON-NLS-1$
		normalDateHourFormat.setLenient(false);

		DateFormat shortDateHourFormat = new SimpleDateFormat("ddMMyy HH:mm");
		normalDateHourFormat.setLenient(false);

		DateFormat ultraShortDateHourFormat = new SimpleDateFormat(
				"ddMMyy HHmm");
		normalDateHourFormat.setLenient(false);

		DateFormat shortFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		shortFormat.setLenient(false);

		DateFormat veryShortFormat = new SimpleDateFormat("ddMMyy");
		veryShortFormat.setLenient(false);

		DefaultFormatter defaultFormatter = new RelativeDateFormatter(
				new MultiDateFormat(normalDateHourFormat,
						normalDateHourFormat2, shortDateHourFormat,
						ultraShortDateHourFormat, shortFormat, veryShortFormat),
				false, false);
		defaultFormatter.setCommitsOnValidEdit(commitsOnValidEdit);

		JFormattedTextField.AbstractFormatter displayFormatter = new RelativeDateFormatter(
				normalDateHourFormat, enableShortcuts, true);

		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(
				defaultFormatter, displayFormatter);

		this.setFormatterFactory(formatterFactory);

		nullAction.setEnabled(enableNull);
		showCalendar = true;
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		nullAction = new NullAction();
	}

	private void createComponents() {
		label = new JLabel(iconCalendar);

		popupMenu = new JPopupMenu();
		calendarPanel = new JCalendarPanel();

		hourSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
		minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	}

	private void initComponents() {
		createComponents();

		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new LabelMouseListener());
		label.setToolTipText(SwingMessages.getString("JDateHourTextField.2")); //$NON-NLS-1$

		popupMenu.add(buildEditorPanel());
		popupMenu.pack();

		this.add(label);

		this.addComponentListener(new DateTextFieldComponentListener());
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(calendarPanel, cc.xy(1, 1));
		builder.add(buildSpinnersPanel(), cc.xywh(1, 2, 1, 1,
				CellConstraints.CENTER, CellConstraints.DEFAULT));
		builder.add(buildActionsPanel(), cc.xy(1, 3));
		return builder.getPanel();
	}

	private JComponent buildSpinnersPanel() {
		FormLayout layout = new FormLayout(
				"RIGHT:DEFAULT:NONE,FILL:4DLU:NONE, FILL:30DLU:NONE,FILL:4DLU:NONE, FILL:30DLU:NONE", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(hourSpinner, cc.xy(3, 1));
		builder.add(minuteSpinner, cc.xy(5, 1));
		builder.addLabel(
				SwingMessages.getString("JDateHourTextField.7"), cc.xy(1, 1)); //$NON-NLS-1$
		return builder.getPanel();
	}

	private JComponent buildActionsPanel() {
		FormLayout layout = new FormLayout(
				"LEFT:DEFAULT:NONE,CENTER:DEFAULT:GROW(1.0), RIGHT:DEFAULT:NONE", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new JButton(acceptAction), cc.xy(1, 1));
		builder.add(new JButton(nullAction), cc.xy(3, 1));
		return builder.getPanel();
	}

	public boolean isShowCalendar() {
		return showCalendar;
	}

	public void setShowCalendar(boolean showCalendar) {
		this.showCalendar = showCalendar;
		label.setVisible(showCalendar && label.isVisible());
	}

	public boolean isEnabledNull() {
		return nullAction.isEnabled();
	}

	public void setEnabledNull(boolean enabledNull) {
		nullAction.setEnabled(enabledNull);
	}

	public void setEditable(boolean b) {
		if (label != null) {
			label.setVisible(b && this.isEnabled());
		}

		super.setEditable(b);
	}

	public void setEnabled(boolean enabled) {
		if (label != null) {
			label.setVisible(enabled && this.isEditable());
		}

		super.setEnabled(enabled);
	}

	private Date getDatePopupMenu() {
		Date date = calendarPanel.getDate();
		if (date != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);

			c.set(Calendar.HOUR_OF_DAY, (Integer) hourSpinner.getValue());
			c.set(Calendar.MINUTE, (Integer) minuteSpinner.getValue());

			return c.getTime();
		} else {
			return null;
		}
	}

	public void showPopupCalendar() {
		if (popupMenu.isVisible()) {
			popupMenu.setVisible(false);
		} else {
			Date date = (Date) JDateHourTextField.this.getValue();

			calendarPanel.setDate(date);

			if (date != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				hourSpinner.setValue(c.get(Calendar.HOUR_OF_DAY));
				minuteSpinner.setValue(c.get(Calendar.MINUTE));
			} else {
				hourSpinner.setValue(0);
				minuteSpinner.setValue(0);
			}

			JDateHourTextField.this.requestFocus();
			popupMenu.show(JDateHourTextField.this, 0, JDateHourTextField.this
					.getHeight());
		}
	}

	private final class LabelMouseListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
				showPopupCalendar();
			}
		}
	}

	private final class DateTextFieldComponentListener implements
			ComponentListener {

		public void componentHidden(ComponentEvent e) {
		}

		public void componentMoved(ComponentEvent e) {
		}

		public void componentResized(ComponentEvent e) {
			int x = JDateHourTextField.this.getWidth()
					- iconCalendar.getIconWidth()
					- JDateHourTextField.this.getInsets().right;
			label.setBounds(x,
					(JDateHourTextField.this.getHeight() - iconCalendar
							.getIconHeight()) / 2, iconCalendar.getIconWidth(),
					iconCalendar.getIconHeight());
		}

		public void componentShown(ComponentEvent e) {
		}
	}

	private final class AcceptAction extends AbstractAction {

		private static final long serialVersionUID = 9171804641058298768L;

		public AcceptAction() {
			super(SwingMessages.getString("JDateHourTextField.10")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JDateHourTextField.this.setValue(getDatePopupMenu());
			popupMenu.setVisible(false);

			JDateHourTextField.this.fireActionPerformed();
		}
	}

	private final class NullAction extends AbstractAction {

		private static final long serialVersionUID = 4520353024501639917L;

		public NullAction() {
			super(SwingMessages.getString("JDateHourTextField.11")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JDateHourTextField.this.setValue(null);
			popupMenu.setVisible(false);

			JDateHourTextField.this.fireActionPerformed();
		}
	}
}
