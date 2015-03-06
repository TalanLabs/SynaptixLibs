package com.synaptix.swing;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.formatter.RelativeDateFormatter;
import com.synaptix.swing.utils.formatter.MultiDateFormat;

public class JDateTextField extends JFormattedTextField {

	private static final long serialVersionUID = -9003389410306382227L;

	private static final ImageIcon iconCalendar = new ImageIcon(
			JDateTextField.class.getResource("/images/iconCalendar.png")); //$NON-NLS-1$

	private JLabel label;

	private JPopupMenu popupMenu;

	private JCalendarPanel calendarPanel;

	private boolean showCalendar;

	private Action nullAction;

	public JDateTextField() {
		this(true);
	}

	public JDateTextField(boolean enableNull) {
		this(enableNull, false, true);
	}

	public JDateTextField(boolean enableShortcuts, boolean commitsOnValidEdit) {
		this(true, enableShortcuts, commitsOnValidEdit);
	}

	public JDateTextField(boolean enableNull, boolean enableShortcuts,
			boolean commitsOnValidEdit) {
		super();

		initActions();
		initComponents();

		DateFormat shortFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		shortFormat.setLenient(false);

		DateFormat veryShortFormat = new SimpleDateFormat("ddMMyy");
		veryShortFormat.setLenient(false);

		DefaultFormatter defaultFormatter = new RelativeDateFormatter(
				new MultiDateFormat(shortFormat, veryShortFormat), false, false);
		defaultFormatter.setCommitsOnValidEdit(commitsOnValidEdit);

		JFormattedTextField.AbstractFormatter displayFormatter = new RelativeDateFormatter(
				shortFormat, enableShortcuts, true);

		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(
				defaultFormatter, displayFormatter, defaultFormatter);

		this.setFormatterFactory(formatterFactory);

		nullAction.setEnabled(enableNull);
		showCalendar = true;
	}

	private void initActions() {
		nullAction = new NullAction();
	}

	private void createComponents() {
		label = new JLabel(iconCalendar);
		popupMenu = new JPopupMenu();
		calendarPanel = new JCalendarPanel();
	}

	private void initComponents() {
		createComponents();

		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new LabelMouseListener());
		label.setToolTipText(SwingMessages.getString("JDateTextField.1")); //$NON-NLS-1$

		calendarPanel.addChangeListener(new CalendarChangeListener());

		popupMenu.add(buildEditorPanel());
		popupMenu.pack();

		this.add(label);

		this.addComponentListener(new DateTextFieldComponentListener());
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(calendarPanel, cc.xy(1, 1));
		builder.add(new JButton(nullAction), cc.xywh(1, 2, 1, 1,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));
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

	private final class CalendarChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			JDateTextField.this.setValue(calendarPanel.getDate());
			popupMenu.setVisible(false);

			JDateTextField.this.fireActionPerformed();
		}
	}

	public void showPopupCalendar() {
		if (popupMenu.isVisible()) {
			popupMenu.setVisible(false);
		} else {
			calendarPanel.setDate((Date) JDateTextField.this.getValue());

			JDateTextField.this.requestFocus();
			popupMenu.show(JDateTextField.this, 0, JDateTextField.this
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
			int x = JDateTextField.this.getWidth()
					- iconCalendar.getIconWidth()
					- JDateTextField.this.getInsets().right;
			label.setBounds(x, (JDateTextField.this.getHeight() - iconCalendar
					.getIconHeight()) / 2, iconCalendar.getIconWidth(),
					iconCalendar.getIconHeight());
		}

		public void componentShown(ComponentEvent e) {
		}
	}

	private final class NullAction extends AbstractAction {

		private static final long serialVersionUID = 4520353024501639917L;

		public NullAction() {
			super(SwingMessages.getString("JDateTextField.4")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JDateTextField.this.setValue(null);
			popupMenu.setVisible(false);

			JDateTextField.this.fireActionPerformed();
		}
	}

}
