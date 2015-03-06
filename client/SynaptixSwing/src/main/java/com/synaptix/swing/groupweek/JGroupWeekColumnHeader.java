package com.synaptix.swing.groupweek;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.synaptix.swing.JGroupWeek;
import com.synaptix.swing.plaf.GroupWeekColumnHeaderUI;

public class JGroupWeekColumnHeader extends JComponent {

	private static final long serialVersionUID = 608988628778077213L;

	private static final String uiClassID = "GroupWeekColumnHeaderUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicGroupWeekColumnHeaderUI"); //$NON-NLS-1$
	}

	private GroupWeekColumnHeaderCellRenderer defaultRenderer;

	private JGroupWeek groupWeek;

	public JGroupWeekColumnHeader() {
		super();

		initializeLocalVars();

		updateUI();
	}

	private void initializeLocalVars() {
		this.setOpaque(true);

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);
		defaultRenderer = new DefaultGroupWeekColumnHeaderCellRenderer();
	}

	public void setDefaultRenderer(
			GroupWeekColumnHeaderCellRenderer defaultRenderer) {
		if (defaultRenderer != null && this.defaultRenderer != defaultRenderer) {
			this.defaultRenderer = defaultRenderer;
			resizeAndRepaint();
		}
	}

	public GroupWeekColumnHeaderCellRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public Component prepareGroupWeekColumnHeaderCellRendererComponent(int day) {
		return defaultRenderer.getGroupWeekColumnHeaderCellRendererComponent(
				this, groupWeek.getSelectedDay() == day, false, day);
	}

	public void setGroupWeek(JGroupWeek groupWeek) {
		this.groupWeek = groupWeek;
	}

	public JGroupWeek getGroupWeek() {
		return groupWeek;
	}

	public int getDayWidth(int day) {
		return groupWeek.getDayWidth(day);
	}

	public int dayAtPoint(Point point) {
		return groupWeek.dayAtPoint(point);
	}

	public Rectangle getHeaderDayRect(int day) {
		Rectangle r = new Rectangle();
		r.height = getHeight();

		if (day < 0) {
			r.x = 0;
			r.width = 0;
		} else if (day >= 7) {
			r.x = groupWeek.getWidth();
			r.width = 0;
		} else {
			for (int i = 0; i < day; i++) {
				r.x += getDayWidth(i);
			}
			r.width = getDayWidth(day);
		}
		return r;
	}

	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();
		int day;

		if ((day = dayAtPoint(p)) != -1) {
			Component component = defaultRenderer
					.getGroupWeekColumnHeaderCellRendererComponent(this, false,
							false, day);
			if (component instanceof JComponent) {
				// Convert the event to the renderer's coordinate system
				MouseEvent newEvent;
				Rectangle cellRect = getHeaderDayRect(day);

				p.translate(-cellRect.x, -cellRect.y);
				newEvent = new MouseEvent(component, event.getID(), event
						.getWhen(), event.getModifiers(), p.x, p.y, event
						.getXOnScreen(), event.getYOnScreen(), event
						.getClickCount(), event.isPopupTrigger(),
						MouseEvent.NOBUTTON);

				tip = ((JComponent) component).getToolTipText(newEvent);
			}
		}

		if (tip == null) {
			tip = getToolTipText();
		}
		return tip;
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public GroupWeekColumnHeaderUI getUI() {
		return (GroupWeekColumnHeaderUI) ui;
	}

	public void setUI(GroupWeekColumnHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((GroupWeekColumnHeaderUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}
}
