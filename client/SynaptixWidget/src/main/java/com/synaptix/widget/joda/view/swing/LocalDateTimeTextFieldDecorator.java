package com.synaptix.widget.joda.view.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;

import org.jdesktop.swingx.JXMonthView;
import org.joda.time.LocalDateTime;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDateTextField;
import com.synaptix.swing.SwingMessages;

public class LocalDateTimeTextFieldDecorator extends JLayeredPane {

	private static final long serialVersionUID = -7053713171500741045L;

	private static final ImageIcon iconCalendar = new ImageIcon(JDateTextField.class.getResource("/images/iconCalendar.png")); //$NON-NLS-1$

	private static final int CONTENT_LAYER = 1;

	private static final int FEEDBACK_LAYER = 2;

	public static JComponent decorate(JFormattedTextField formattedTextField) {
		return new LocalDateTimeTextFieldDecorator(formattedTextField);
	}

	private JFormattedTextField formattedTextField;

	private JLabel label;

	private JPopupMenu popupMenu;

	private JXMonthView monthView;

	private Action nullAction;

	private LocalDateTimeTextFieldDecorator(JFormattedTextField formattedTextField) {
		super();

		this.setLayout(new SimpleLayout());

		this.formattedTextField = formattedTextField;

		initComponents();
	}

	private void initComponents() {
		nullAction = new NullAction();

		label = new JLabel(iconCalendar);
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
					showPopupCalendar();
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					formattedTextField.setValue(null);
				}
			}
		});
		label.setToolTipText(SwingMessages.getString("JDateTextField.1")); //$NON-NLS-1$
		label.setVisible(formattedTextField.isEnabled() && formattedTextField.isEditable());

		monthView = new JXMonthView();
		monthView.setTraversable(true);
		monthView.setShowingLeadingDays(true);
		monthView.setShowingTrailingDays(true);

		monthView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popupMenu.setVisible(false);
				Date date = monthView.getSelectionDate();
				if (date != null) {
					LocalDateTime oldValue = (LocalDateTime) formattedTextField.getValue();
					if (oldValue == null) {
						oldValue = new LocalDateTime();
					}
					Calendar c = Calendar.getInstance();
					c.setTime(date);
					LocalDateTime newValue = oldValue.withDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
					newValue = newValue.withTime(0, 0, 0, 0);
					formattedTextField.setValue(newValue);
					formattedTextField.firePropertyChange("calendar", false, true);
				} else {
					formattedTextField.setValue(null);
				}
			}
		});

		popupMenu = new JPopupMenu();
		popupMenu.add(buildEditorPanel());

		formattedTextField.addPropertyChangeListener("enabled", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				computeLabelVisible();
			}
		});
		formattedTextField.addPropertyChangeListener("editable", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				computeLabelVisible();
			}
		});

		this.add(formattedTextField, CONTENT_LAYER);
		this.add(label, Integer.valueOf(FEEDBACK_LAYER));

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				computeLabelPosition();
			}
		});
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(monthView, cc.xy(1, 1));
		builder.add(new JButton(nullAction), cc.xywh(1, 2, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		return builder.getPanel();
	}

	private void computeLabelVisible() {
		label.setVisible(formattedTextField.isEnabled() && formattedTextField.isEditable());
	}

	private void computeLabelPosition() {
		Insets insets = formattedTextField.getInsets();
		int x = this.getWidth() - iconCalendar.getIconWidth() - insets.right;
		label.setBounds(x, (this.getHeight() - iconCalendar.getIconHeight()) / 2, iconCalendar.getIconWidth(), iconCalendar.getIconHeight());
	}

	private void showPopupCalendar() {
		if (popupMenu.isVisible()) {
			popupMenu.setVisible(false);
		} else {
			LocalDateTime localDate = (LocalDateTime) formattedTextField.getValue();

			monthView.setSelectionDate(localDate != null ? localDate.toDate() : null);

			formattedTextField.requestFocus();

			popupMenu.pack();
			popupMenu.show(this, this.getWidth() - popupMenu.getPreferredSize().width, this.getHeight());
		}
	}

	private final class NullAction extends AbstractAction {

		private static final long serialVersionUID = 4520353024501639917L;

		public NullAction() {
			super(SwingMessages.getString("JDateTextField.4")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			popupMenu.setVisible(false);
			formattedTextField.setValue(null);
		}
	}

	private final class SimpleLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			if (formattedTextField != null) {
				return formattedTextField.getPreferredSize();
			}
			return new Dimension();
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			if (formattedTextField != null) {
				return formattedTextField.getMinimumSize();
			}
			return new Dimension();
		}

		@Override
		public void layoutContainer(Container parent) {
			if (formattedTextField != null) {
				Dimension size = parent.getSize();
				formattedTextField.setBounds(0, 0, size.width, size.height);
			}
		}
	}
}
