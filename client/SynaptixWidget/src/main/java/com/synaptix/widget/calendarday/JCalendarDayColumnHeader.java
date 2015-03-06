package com.synaptix.widget.calendarday;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.synaptix.widget.calendarday.plaf.BasicCalendarDayColumnHeaderUI;

public class JCalendarDayColumnHeader extends JComponent implements CalendarDayModelListener {

	private static final long serialVersionUID = 7789628569244174704L;

	private static final String uiClassID = "CalendarDayColumnHeaderUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicCalendarDayColumnHeaderUI.class.getName());
	}

	private final JCalendarDay calendarDay;

	private CalendarDayColumnHeaderRenderer columnHeaderRenderer;

	public JCalendarDayColumnHeader(JCalendarDay calendarDay) {
		super();

		this.calendarDay = calendarDay;
		this.columnHeaderRenderer = createColumnHeaderRenderer();

		if (calendarDay.getModel() != null) {
			calendarDay.getModel().addCalendarDayModelListener(this);
		}
		calendarDay.addPropertyChangeListener("model", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getOldValue() instanceof CalendarDayModel) {
					((CalendarDayModel) evt.getOldValue()).removeCalendarDayModelListener(JCalendarDayColumnHeader.this);
				}
				if (evt.getNewValue() instanceof CalendarDayModel) {
					((CalendarDayModel) evt.getNewValue()).addCalendarDayModelListener(JCalendarDayColumnHeader.this);
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

	protected CalendarDayColumnHeaderRenderer createColumnHeaderRenderer() {
		return new DefaultCalendarDayColumnHeaderRenderer();
	}

	public CalendarDayColumnHeaderRenderer getColumnHeaderRenderer() {
		return columnHeaderRenderer;
	}

	public void setColumnHeaderRenderer(CalendarDayColumnHeaderRenderer columnHeaderRenderer) {
		CalendarDayColumnHeaderRenderer oldValue = this.columnHeaderRenderer;
		this.columnHeaderRenderer = columnHeaderRenderer;
		firePropertyChange("columnHeaderRenderer", oldValue, columnHeaderRenderer);
	}

	public int getColumnCount() {
		return calendarDay.getColumnCount();
	}

	public int getColumnWidth() {
		return calendarDay.getColumnWidth();
	}

	public int getColumnHeaderHeight() {
		return calendarDay.getColumnHeaderHeight();
	}

	public int columnAtPoint(Point point) {
		int rowHeaderWidth = calendarDay.isShowRowHeaderInner() && calendarDay.getCalendarDayRowHeader() != null ? calendarDay.getRowHeaderWidth() : 0;
		return calendarDay.columnAtPoint(new Point(point.x + rowHeaderWidth, point.y));
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
