package com.synaptix.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.synaptix.swing.event.GroupWeekModelEvent;
import com.synaptix.swing.event.GroupWeekModelListener;
import com.synaptix.swing.groupweek.DefaultGroupWeekCellRenderer;
import com.synaptix.swing.groupweek.DefaultGroupWeekSelectionModel;
import com.synaptix.swing.groupweek.GroupWeekCellRenderer;
import com.synaptix.swing.groupweek.GroupWeekSelectionListener;
import com.synaptix.swing.groupweek.GroupWeekSelectionModel;
import com.synaptix.swing.groupweek.JGroupWeekColumnHeader;
import com.synaptix.swing.groupweek.JGroupWeekRowHeader;
import com.synaptix.swing.plaf.GroupWeekUI;
import com.synaptix.swing.utils.DragDropComponent;

public class JGroupWeek extends JComponent implements GroupWeekModelListener,
		GroupWeekSelectionListener, DragDropComponent, Scrollable {

	private static final long serialVersionUID = 7460954729827857370L;

	private static final String uiClassID = "GroupWeekUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicGroupWeekUI"); //$NON-NLS-1$
	}

	private JGroupWeekColumnHeader groupWeekColumnHeader;

	private JGroupWeekRowHeader groupWeekRowHeader;

	private GroupWeekSelectionModel groupWeekSelectionModel;

	private Dimension preferredViewportSize;

	private GroupWeekModel model;

	private int rowDefaultHeight;

	private GroupWeekCellRenderer defaultRenderer;

	private int spaceGroupHeight;

	private Color spaceGroupColor;

	private Color selectionForeground;

	private Color selectionBackground;

	private boolean showGrid;

	private Color gridColor;

	private int rowMargin;

	private int dayMargin;

	public JGroupWeek(GroupWeekModel model) {
		super();

		setGroupWeekColumnHeader(new JGroupWeekColumnHeader());
		setGroupWeekRowHeader(new JGroupWeekRowHeader());
		setGroupWeekSelectionModel(new DefaultGroupWeekSelectionModel());
		setModel(model);

		initializeLocalVars();

		updateUI();
	}

	private void initializeLocalVars() {
		this.setOpaque(true);
		this.setFocusable(true);

		this.spaceGroupColor = Color.gray;
		this.selectionForeground = Color.white;
		this.selectionBackground = new Color(50, 64, 255, 128);
		this.spaceGroupHeight = 5;
		this.preferredViewportSize = new Dimension(400, 400);
		this.rowDefaultHeight = 50;
		this.showGrid = true;
		this.gridColor = Color.gray;
		this.dayMargin = 1;
		this.rowMargin = 1;

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);
		defaultRenderer = new DefaultGroupWeekCellRenderer();
	}

	public void setModel(GroupWeekModel model) {
		if (model == null) {
			throw new IllegalArgumentException("Cannot set a null TableModel");
		}
		if (this.model != model) {
			GroupWeekModel old = this.model;
			if (old != null) {
				old.removeGroupWeekModelListener(this);
			}
			this.model = model;
			model.addGroupWeekModelListener(this);

			firePropertyChange("model", old, model);
		}
	}

	public GroupWeekModel getModel() {
		return model;
	}

	public JGroupWeekColumnHeader getGroupWeekColumnHeader() {
		return groupWeekColumnHeader;
	}

	public void setGroupWeekColumnHeader(
			JGroupWeekColumnHeader groupWeekColumnHeader) {
		if (this.groupWeekColumnHeader != groupWeekColumnHeader) {
			JGroupWeekColumnHeader old = this.groupWeekColumnHeader;
			if (old != null) {
				old.setGroupWeek(null);
			}
			this.groupWeekColumnHeader = groupWeekColumnHeader;
			if (groupWeekColumnHeader != null) {
				groupWeekColumnHeader.setGroupWeek(this);
			}
			firePropertyChange("groupWeekHeader", old, groupWeekColumnHeader);
		}
	}

	public JGroupWeekRowHeader getGroupWeekRowHeader() {
		return groupWeekRowHeader;
	}

	public void setGroupWeekRowHeader(JGroupWeekRowHeader groupWeekRowHeader) {
		if (this.groupWeekRowHeader != groupWeekRowHeader) {
			JGroupWeekRowHeader old = this.groupWeekRowHeader;
			if (old != null) {
				old.setGroupWeek(null);
			}
			this.groupWeekRowHeader = groupWeekRowHeader;
			if (groupWeekRowHeader != null) {
				groupWeekRowHeader.setGroupWeek(this);
			}
			firePropertyChange("groupWeekRow", old, groupWeekRowHeader);
		}
	}

	public GroupWeekSelectionModel getGroupWeekSelectionModel() {
		return groupWeekSelectionModel;
	}

	public void setGroupWeekSelectionModel(
			GroupWeekSelectionModel groupWeekSelectionModel) {
		if (this.groupWeekSelectionModel != groupWeekSelectionModel) {
			GroupWeekSelectionModel old = this.groupWeekSelectionModel;
			if (old != null) {
				old.removeGroupWeekSelectionListener(this);
			}
			this.groupWeekSelectionModel = groupWeekSelectionModel;
			if (groupWeekSelectionModel != null) {
				groupWeekSelectionModel.addGroupWeekSelectionListener(this);
			}
			firePropertyChange("groupWeekSelectionModel", old,
					groupWeekSelectionModel);
		}
	}

	public void setDefaultRowHeight(int rowDefaultHeight) {
		this.rowDefaultHeight = rowDefaultHeight;
		resizeAndRepaint();
	}

	public int getDefaultRowHeight() {
		return rowDefaultHeight;
	}

	public int getRowHeight(int group, int row) {
		return rowDefaultHeight;
	}

	public int getSpaceGroupHeight() {
		return spaceGroupHeight;
	}

	public void setSpaceGroupHeight(int spaceGroupHeight) {
		this.spaceGroupHeight = spaceGroupHeight;
		resizeAndRepaint();
	}

	public Color getSpaceGroupColor() {
		return spaceGroupColor;
	}

	public void setSpaceGroupColor(Color spaceGroupColor) {
		if (spaceGroupColor != null && this.spaceGroupColor != spaceGroupColor) {
			this.spaceGroupColor = spaceGroupColor;
			resizeAndRepaint();
		}
	}

	public Color getSelectionForeground() {
		return selectionForeground;
	}

	public void setSelectionForeground(Color selectionForeground) {
		if (selectionForeground != null
				&& this.selectionForeground != selectionForeground) {
			this.selectionForeground = selectionForeground;
			resizeAndRepaint();
		}
	}

	public Color getSelectionBackground() {
		return selectionBackground;
	}

	public void setSelectionBackground(Color selectionBackground) {
		if (selectionBackground != null
				&& this.selectionBackground != selectionBackground) {
			this.selectionBackground = selectionBackground;
			resizeAndRepaint();
		}
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		if (this.showGrid != showGrid) {
			this.showGrid = showGrid;
			resizeAndRepaint();
		}
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		if (gridColor != null && this.gridColor != gridColor) {
			this.gridColor = gridColor;
			resizeAndRepaint();
		}
	}

	public int getRowMargin() {
		return rowMargin;
	}

	public void setRowMargin(int rowMargin) {
		this.rowMargin = rowMargin;
		resizeAndRepaint();
	}

	public int getDayMargin() {
		return dayMargin;
	}

	public void setDayMargin(int dayMargin) {
		this.dayMargin = dayMargin;
		resizeAndRepaint();
	}

	public GroupWeekCellRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public void setDefaultRenderer(GroupWeekCellRenderer defaultRenderer) {
		if (defaultRenderer != null && this.defaultRenderer != defaultRenderer) {
			this.defaultRenderer = defaultRenderer;
			resizeAndRepaint();
		}
	}

	public int getGroupHeight(int group) {
		int h = 0;
		for (int row = 0; row < model.getGroupRowCount(group); row++) {
			h += getRowHeight(group, row);
		}
		return h;
	}

	public Rectangle getGroupRect(int group) {
		Rectangle r = new Rectangle();
		r.x = 0;
		r.width = getWidth();
		r.height = getGroupHeight(group);
		r.y = 0;
		for (int g = 0; g < group; g++) {
			r.y += getGroupHeight(g);
		}
		r.y += group * getSpaceGroupHeight();
		return r;
	}

	public Rectangle getRowRect(int group, int row) {
		Rectangle res = new Rectangle();
		res.x = 0;
		res.width = getWidth();
		res.height = getRowHeight(group, row);
		res.y = 0;
		for (int r = 0; r < row; r++) {
			res.y += getRowHeight(group, row);
		}
		for (int g = 0; g < group; g++) {
			res.y += getGroupHeight(g);
		}
		res.y += group * getSpaceGroupHeight();
		return res;
	}

	public Rectangle getCellRect(int group, int row, int day) {
		Rectangle res = getRowRect(group, row);

		Rectangle header = groupWeekColumnHeader.getHeaderDayRect(day);
		res.x = header.x;
		res.width = header.width;

		return res;
	}

	public int getDayWidth(int day) {
		int w = this.getWidth();
		int dayWidth = 0;
		for (int i = 0; i <= day; i++) {
			dayWidth = w / (7 - i);
			w -= dayWidth;
		}
		return dayWidth;
	}

	public int dayAtPoint(Point point) {
		int x = point.x;
		int c = 0;
		for (int i = 0; i < 7; i++) {
			int w = getDayWidth(i);
			if (x >= c && x < c + w) {
				return i;
			}
			c += w;
		}
		return -1;
	}

	public int groupAtPoint(Point point) {
		int y = point.y;
		int g = 0;
		for (int group = 0; group < model.getGroupCount(); group++) {
			int h = getGroupHeight(group) + getSpaceGroupHeight();
			if (y >= g && y < g + h) {
				return group;
			}
			g += h;
		}
		return -1;
	}

	public int rowAtPoint(Point point) {
		int y = point.y;
		int g = 0;
		for (int group = 0; group < model.getGroupCount(); group++) {
			int h = getGroupHeight(group) + getSpaceGroupHeight();
			if (y >= g && y < g + h) {
				int r = g;
				for (int row = 0; row < model.getGroupRowCount(group); row++) {
					int h2 = getRowHeight(group, row);
					if (y >= r && y < r + h2) {
						return row;
					}
					r += h2;
				}
				return -1;
			}
			g += h;
		}
		return -1;
	}

	public int getSelectedGroup() {
		return groupWeekSelectionModel != null ? groupWeekSelectionModel
				.getSelectedGroup() : -1;
	}

	public int getSelectedRow() {
		return groupWeekSelectionModel != null ? groupWeekSelectionModel
				.getSelectedRow() : -1;
	}

	public int getSelectedDay() {
		return groupWeekSelectionModel != null ? groupWeekSelectionModel
				.getSelectedDay() : -1;
	}

	public void setSelected(int group, int row, int day) {
		if (groupWeekSelectionModel != null) {
			groupWeekSelectionModel.setSelected(group, row, day);
		}
	}

	public boolean isSelection(int group, int row, int day) {
		return groupWeekSelectionModel != null
				&& groupWeekSelectionModel.isSelection()
				&& groupWeekSelectionModel.getSelectedGroup() == group
				&& groupWeekSelectionModel.getSelectedRow() == row
				&& groupWeekSelectionModel.getSelectedDay() == day;
	}

	public boolean isSelection() {
		return groupWeekSelectionModel != null
				&& groupWeekSelectionModel.isSelection();
	}

	public void clearSelection() {
		if (groupWeekSelectionModel != null) {
			groupWeekSelectionModel.clearSelection();
		}
	}

	public GroupWeekUI getUI() {
		return (GroupWeekUI) ui;
	}

	public void setUI(GroupWeekUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((GroupWeekUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void addNotify() {
		super.addNotify();
		configureEnclosingScrollPane();
	}

	private void configureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.setColumnHeaderView(getGroupWeekColumnHeader());
				scrollPane.setRowHeaderView(getGroupWeekRowHeader());
			}
		}
	}

	public void removeNotify() {
		unconfigureEnclosingScrollPane();
		super.removeNotify();
	}

	protected void unconfigureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.setColumnHeaderView(null);
				scrollPane.setRowHeaderView(null);
			}
		}
	}

	public void resizeAndRepaint() {
		if (groupWeekColumnHeader != null) {
			groupWeekColumnHeader.resizeAndRepaint();
		}
		if (groupWeekRowHeader != null) {
			groupWeekRowHeader.resizeAndRepaint();
		}

		revalidate();
		repaint();
	}

	@Override
	public void clearDrop() {
		// TODO Auto-generated method stub

	}

	public void dataChanged(GroupWeekModelEvent e) {
		resizeAndRepaint();
	}

	public void selectionChanged() {
		resizeAndRepaint();
	}

	public void setPreferredScrollableViewportSize(
			Dimension preferredViewportSize) {
		this.preferredViewportSize = preferredViewportSize;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return preferredViewportSize;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return getScrollableUnitIncrement(visibleRect, orientation, direction);
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		int res = 0;
		if (SwingConstants.VERTICAL == orientation) {
			Point p = visibleRect.getLocation();
			int group = groupAtPoint(p);
			int row = rowAtPoint(p);

			if (direction > 0) {
				if (row == -1) {
					row = 0;
				}
				row++;
				if (row >= model.getGroupRowCount(group)) {
					group++;
					row = 0;
				}
			} else if (direction < 0) {
				if (group != -1) {
					if (row == -1) {
						row = model.getGroupRowCount(group) - 1;
					}
					if (getRowRect(group, row).y == p.y) {
						row--;
						if (row < 0) {
							group--;
							if (group >= 0) {
								row = model.getGroupRowCount(group) - 1;
							}
						}
					}
				}
			}
			if (group >= 0 && group < model.getGroupCount() && row >= 0
					&& row < model.getGroupRowCount(group)) {
				Rectangle r = getRowRect(group, row);
				res = Math.abs(r.y - p.y);
			}
		} else {
			res = 10;
		}
		return res;
	}

	public Component prepareCellRenderer(int group, int row, int day) {
		return defaultRenderer.getGroupWeekCellRendererComponent(this, model
				.getValue(group, row, day), isSelection(group, row, day),
				false, group, row, day);
	}
}
