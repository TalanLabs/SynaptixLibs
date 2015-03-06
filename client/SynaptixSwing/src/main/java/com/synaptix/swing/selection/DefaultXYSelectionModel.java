package com.synaptix.swing.selection;

import java.util.BitSet;

import javax.swing.event.EventListenerList;

public class DefaultXYSelectionModel implements XYSelectionModel {

	private static final int MIN = -1;

	private static final int MAX = Integer.MAX_VALUE;

	private int minX = MAX;

	private int minY = MAX;

	private int maxX = MIN;

	private int maxY = MIN;

	private int anchorX = -1;

	private int anchorY = -1;

	private int leadX = -1;

	private int leadY = -1;

	private int firstAdjustedX = MAX;

	private int firstAdjustedY = MAX;

	private int lastAdjustedX = MIN;

	private int lastAdjustedY = MIN;

	private boolean isAdjusting = false;

	private int firstChangedX = MAX;

	private int firstChangedY = MAX;

	private int lastChangedX = MIN;

	private int lastChangedY = MIN;

	private BitSet value = new BitSet(32);

	private boolean leadAnchorNotificationEnabled = true;

	private EventListenerList listenerList = new EventListenerList();

	private int selectionMode;

	public DefaultXYSelectionModel() {
		super();
		this.selectionMode = SINGLE_SELECTION;
	}

	@Override
	public int getSelectionMode() {
		return selectionMode;
	}

	@Override
	public void setSelectionMode(int selectionMode) {
		this.selectionMode = selectionMode;
	}

	@Override
	public void addXYSelectionModelListener(XYSelectionModelListener l) {
		listenerList.add(XYSelectionModelListener.class, l);
	}

	@Override
	public void removeXYSelectionModelListener(XYSelectionModelListener l) {
		listenerList.remove(XYSelectionModelListener.class, l);
	}

	@Override
	public int getSelectionCount() {
		return value.cardinality();
	}

	@Override
	public boolean isSelected(int X, int Y) {
		return ((X < minX) || (X > maxX) || (Y < minY) || (Y > maxY)) ? false : value.get((X << 16) + Y);
	}

	@Override
	public int getAnchorX() {
		return anchorX;
	}

	@Override
	public int getAnchorY() {
		return anchorY;
	}

	@Override
	public int getLeadX() {
		return leadX;
	}

	@Override
	public int getLeadY() {
		return leadY;
	}

	@Override
	public void setAnchor(int x, int y) {
		updateLeadAnchorIndices(x, y, this.leadX, this.leadY);
		fireSelectionChanged();
	}

	@Override
	public void setLead(int x, int y) {
		updateLeadAnchorIndices(this.anchorX, this.anchorY, x, y);
		fireSelectionChanged();
	}

	@Override
	public int getMinSelectionX() {
		return isSelectionEmpty() ? -1 : minX;
	}

	@Override
	public int getMinSelectionY() {
		return isSelectionEmpty() ? -1 : minY;
	}

	@Override
	public int getMaxSelectionX() {
		return maxX;
	}

	@Override
	public int getMaxSelectionY() {
		return maxY;
	}

	@Override
	public void clearSelection() {
		removeSelectionRangeImpl(minX, minY, maxX, maxY, false);
	}

	@Override
	public boolean isSelectionEmpty() {
		return minX > maxX;
	}

	@Override
	public void setSelectionRange(int x0, int y0, int x1, int y1) {
		if (x0 == -1 || y0 == -1 || x1 == -1 || y1 == -1) {
			return;
		}

		if (getSelectionMode() == SINGLE_SELECTION) {
			x0 = x1;
			y0 = y1;
		}

		updateLeadAnchorIndices(x0, y0, x1, y1);

		int clearMinX = minX;
		int clearMinY = minY;
		int clearMaxX = maxX;
		int clearMaxY = maxY;
		int setMinX = Math.min(x0, x1);
		int setMinY = Math.min(y0, y1);
		int setMaxX = Math.max(x0, x1);
		int setMaxY = Math.max(y0, y1);
		changeSelection(clearMinX, clearMinY, clearMaxX, clearMaxY, setMinX, setMinY, setMaxX, setMaxY);
	}

	@Override
	public void addSelectionRange(int x0, int y0, int x1, int y1) {
		if (x0 == -1 || y0 == -1 || x1 == -1 || y1 == -1) {
			return;
		}

		if (getSelectionMode() == SINGLE_SELECTION) {
			setSelectionRange(x0, y0, x1, y1);
			return;
		}

		updateLeadAnchorIndices(x0, y0, x1, y1);

		int clearMinX = MAX;
		int clearMinY = MAX;
		int clearMaxX = MIN;
		int clearMaxY = MIN;
		int setMinX = Math.min(x0, x1);
		int setMinY = Math.min(y0, y1);
		int setMaxX = Math.max(x0, x1);
		int setMaxY = Math.max(y0, y1);

		changeSelection(clearMinX, clearMinY, clearMaxX, clearMaxY, setMinX, setMinY, setMaxX, setMaxY);
	}

	@Override
	public void removeSelectionRange(int x0, int y0, int x1, int y1) {
		removeSelectionRangeImpl(x0, y0, x1, y1, true);
	}

	@Override
	public void setValueIsAdjusting(boolean isAdjusting) {
		if (isAdjusting != this.isAdjusting) {
			this.isAdjusting = isAdjusting;

			fireSelectionChanged(isAdjusting);
		}
	}

	@Override
	public boolean getValueIsAdjusting() {
		return isAdjusting;
	}

	public void fireSelectionChanged(XYSelectionModelEvent e) {
		XYSelectionModelListener[] ls = listenerList.getListeners(XYSelectionModelListener.class);
		if (ls != null) {
			for (XYSelectionModelListener l : ls) {
				l.selectionChanged(e);
			}
		}
	}

	public void fireSelectionChanged(boolean adjusting) {
		int oldFirstChangedX = firstChangedX;
		int oldFirstChangedY = firstChangedY;
		int oldLastChangedX = lastChangedX;
		int oldLastChangedY = lastChangedY;
		firstChangedX = MAX;
		firstChangedY = MAX;
		lastChangedX = MIN;
		lastChangedY = MIN;

		fireSelectionChanged(oldFirstChangedX, oldFirstChangedY, oldLastChangedX, oldLastChangedY, adjusting);
	}

	private void fireSelectionChanged() {
		if (getValueIsAdjusting()) {
			firstChangedX = Math.min(firstChangedX, firstAdjustedX);
			firstChangedY = Math.min(firstChangedY, firstAdjustedY);
			lastChangedX = Math.max(lastChangedX, lastAdjustedX);
			lastChangedY = Math.max(lastChangedY, lastAdjustedY);
		}
		int oldFirstAdjustedX = firstAdjustedX;
		int oldFirstAdjustedY = firstAdjustedY;
		int oldLastAdjustedX = lastAdjustedX;
		int oldLastAdjustedY = lastAdjustedY;
		firstAdjustedX = MAX;
		firstAdjustedY = MAX;
		lastAdjustedX = MIN;
		lastAdjustedY = MIN;

		fireSelectionChanged(oldFirstAdjustedX, oldFirstAdjustedY, oldLastAdjustedX, oldLastAdjustedY);
	}

	public void fireSelectionChanged(int firstX, int firstY, int lastX, int lastY) {
		fireSelectionChanged(new XYSelectionModelEvent(this, firstX, firstY, lastX, lastY, getValueIsAdjusting()));
	}

	public void fireSelectionChanged(int firstX, int firstY, int lastX, int lastY, boolean adjusting) {
		fireSelectionChanged(new XYSelectionModelEvent(this, firstX, firstY, lastX, lastY, adjusting));
	}

	private void removeSelectionRangeImpl(int x0, int y0, int x1, int y1, boolean changeLeadAnchor) {
		if (x0 == -1 || x1 == -1 || y0 == -1 || y1 == -1) {
			return;
		}

		if (changeLeadAnchor) {
			updateLeadAnchorIndices(x0, y0, x1, y1);
		}

		int clearMinX = Math.min(x0, x1);
		int clearMinY = Math.min(y0, y1);
		int clearMaxX = Math.max(x0, x1);
		int clearMaxY = Math.max(y0, y1);
		int setMinX = MAX;
		int setMinY = MAX;
		int setMaxX = MIN;
		int setMaxY = MIN;

		changeSelection(clearMinX, clearMinY, clearMaxX, clearMaxY, setMinX, setMinY, setMaxX, setMaxY);
	}

	private boolean contains(int a0, int b0, int a1, int b1, int i, int j) {
		return (i >= a0) && (i <= a1) && (j >= b0) && (j <= b1);
	}

	private void changeSelection(int clearMinX, int clearMinY, int clearMaxX, int clearMaxY, int setMinX, int setMinY, int setMaxX, int setMaxY, boolean clearFirst) {
		for (int i = Math.min(setMinX, clearMinX); i <= Math.max(setMaxX, clearMaxX); i++) {
			for (int j = Math.min(setMinY, clearMinY); j <= Math.max(setMaxY, clearMaxY); j++) {
				boolean shouldClear = contains(clearMinX, clearMinY, clearMaxX, clearMaxY, i, j);
				boolean shouldSet = contains(setMinX, setMinY, setMaxX, setMaxY, i, j);

				if (shouldSet && shouldClear) {
					if (clearFirst) {
						shouldClear = false;
					} else {
						shouldSet = false;
					}
				}

				if (shouldSet) {
					set(i, j);
				}
				if (shouldClear) {
					clear(i, j);
				}
			}
		}
		fireSelectionChanged();
	}

	private void changeSelection(int clearMinX, int clearMinY, int clearMaxX, int clearMaxY, int setMinX, int setMinY, int setMaxX, int setMaxY) {
		changeSelection(clearMinX, clearMinY, clearMaxX, clearMaxY, setMinX, setMinY, setMaxX, setMaxY, true);
	}

	private void markAsDirty(int x, int y) {
		firstAdjustedX = Math.min(firstAdjustedX, x);
		lastAdjustedX = Math.max(lastAdjustedX, x);

		firstAdjustedY = Math.min(firstAdjustedY, y);
		lastAdjustedY = Math.max(lastAdjustedY, y);
	}

	public void setLeadAnchorNotificationEnabled(boolean flag) {
		leadAnchorNotificationEnabled = flag;
	}

	public boolean isLeadAnchorNotificationEnabled() {
		return leadAnchorNotificationEnabled;
	}

	private void updateLeadAnchorIndices(int anchorX, int anchorY, int leadX, int leadY) {
		if (leadAnchorNotificationEnabled) {
			if (this.anchorX != anchorX || this.anchorY != anchorY) {
				if (this.anchorX != -1 && this.anchorY != -1) {
					markAsDirty(this.anchorX, this.anchorY);
				}
				markAsDirty(anchorX, anchorY);
			}

			if (this.leadX != leadX || this.leadY != leadY) {
				if (this.leadX != -1 && this.leadY != -1) {
					markAsDirty(this.leadX, this.leadY);
				}
				markAsDirty(leadX, leadY);
			}
		}
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.leadX = leadX;
		this.leadY = leadY;
	}

	private void set(int X, int Y) {
		if (value.get((X << 16) + Y)) {
			return;
		}
		value.set((X << 16) + Y);
		markAsDirty(X, Y);

		minX = Math.min(minX, X);
		minY = Math.min(minY, Y);
		maxX = Math.max(maxX, X);
		maxY = Math.max(maxY, Y);
	}

	private void clear(int X, int Y) {
		if (!value.get((X << 16) + Y)) {
			return;
		}
		value.clear((X << 16) + Y);
		markAsDirty(X, Y);

		// Update minimum and maximum indices
		if (X == minX) {
			boolean ok = false;
			int c;
			while (minX <= maxX && !ok) {
				c = minY;
				while (c <= maxY && !ok) {
					ok = value.get((minX << 16) + c);
					c++;
				}
				if (!ok) {
					minX++;
				}
			}
		}
		if (X == maxX) {
			boolean ok = false;
			int c;
			while (maxX >= minX && !ok) {
				c = minY;
				while (c <= maxY && !ok) {
					ok = value.get((maxX << 16) + c);
					c++;
				}
				if (!ok) {
					maxX--;
				}
			}
		}
		if (Y == minY) {
			boolean ok = false;
			int r;
			while (minY <= maxY && !ok) {
				r = minX;
				while (r <= maxX && !ok) {
					ok = value.get((r << 16) + minY);
					r++;
				}
				if (!ok) {
					minY++;
				}
			}
		}
		if (Y == maxY) {
			boolean ok = false;
			int r;
			while (maxY >= minY && !ok) {
				r = minX;
				while (r <= maxX && !ok) {
					ok = value.get((r << 16) + maxY);
					r++;
				}
				if (!ok) {
					maxY--;
				}
			}
		}

		if (isSelectionEmpty()) {
			minX = MAX;
			minY = MAX;
			maxX = MIN;
			maxY = MIN;
		}
	}
}
