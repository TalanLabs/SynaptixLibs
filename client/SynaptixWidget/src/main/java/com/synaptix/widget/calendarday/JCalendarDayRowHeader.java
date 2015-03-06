package com.synaptix.widget.calendarday;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.synaptix.widget.calendarday.plaf.BasicCalendarDayRowHeaderUI;

public class JCalendarDayRowHeader extends JComponent implements CalendarDayModelListener {

	private static final long serialVersionUID = 7789628569244174704L;

	private static final String uiClassID = "CalendarDayRowHeaderUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicCalendarDayRowHeaderUI.class.getName());
	}

	private final JCalendarDay calendarDay;

	private CalendarDayRowHeaderRenderer rowHeaderRenderer;

	public JCalendarDayRowHeader(JCalendarDay calendarDay) {
		super();

		this.calendarDay = calendarDay;
		this.rowHeaderRenderer = createRowHeaderRenderer();

		if (calendarDay.getModel() != null) {
			calendarDay.getModel().addCalendarDayModelListener(this);
		}
		calendarDay.addPropertyChangeListener("model", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getOldValue() instanceof CalendarDayModel) {
					((CalendarDayModel) evt.getOldValue()).removeCalendarDayModelListener(JCalendarDayRowHeader.this);
				}
				if (evt.getNewValue() instanceof CalendarDayModel) {
					((CalendarDayModel) evt.getNewValue()).addCalendarDayModelListener(JCalendarDayRowHeader.this);
				}
			}
		});

		updateUI();
	}

	@Override
	public void updateUI() {
		super.updateUI();

		setUI(UIManager.getUI(this));
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public JCalendarDay getCalendarDay() {
		return calendarDay;
	}

	protected CalendarDayRowHeaderRenderer createRowHeaderRenderer() {
		return new DefaultCalendarDayRowHeaderRenderer();
	}

	public CalendarDayRowHeaderRenderer getRowHeaderRenderer() {
		return rowHeaderRenderer;
	}

	public void setRowHeaderRenderer(CalendarDayRowHeaderRenderer rowHeaderRenderer) {
		CalendarDayRowHeaderRenderer oldValue = this.rowHeaderRenderer;
		this.rowHeaderRenderer = rowHeaderRenderer;
		firePropertyChange("rowHeaderRenderer", oldValue, rowHeaderRenderer);
	}

	public int getRowCount() {
		return calendarDay.getRowCount();
	}

	public int getRowHeight() {
		return calendarDay.getRowHeight();
	}

	public int getRowHeaderWidth() {
		return calendarDay.getRowHeaderWidth();
	}

	public int rowAtPoint(Point point) {
		int columnHeaderHeight = calendarDay.isShowColumnHeaderInner() && calendarDay.getCalendarDayColumnHeader() != null ? calendarDay.getColumnHeaderHeight() : 0;
		return calendarDay.rowAtPoint(new Point(point.x, point.y + columnHeaderHeight));
	}

	protected void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	@Override
	public void calendarDayChanged(CalendarDayModelEvent e) {
		resizeAndRepaint();
	}
}
