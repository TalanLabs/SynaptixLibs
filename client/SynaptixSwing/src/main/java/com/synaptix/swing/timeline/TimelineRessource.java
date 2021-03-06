package com.synaptix.swing.timeline;

import java.awt.Component;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.event.SwingPropertyChangeSupport;

import com.synaptix.swing.JTimeline;

public class TimelineRessource {

	public static final int DEFAULT_HEIGHT = 50;

	protected int modelIndex;

	protected Object identifier;

	protected int height;

	protected int minHeight;

	private int preferredHeight;

	protected int maxHeight;

	protected TimelineRessourceRenderer headerRenderer;

	protected Object headerValue;

	protected boolean isResizable;

	private SwingPropertyChangeSupport changeSupport;

	public TimelineRessource() {
		this(0);
	}

	public TimelineRessource(int modelIndex) {
		this(modelIndex, DEFAULT_HEIGHT);
	}

	public TimelineRessource(int modelIndex, int height) {
		super();
		this.modelIndex = modelIndex;
		preferredHeight = this.height = Math.max(height, 0);

		// Set other instance variables to default values.
		minHeight = Math.min(15, this.height);
		maxHeight = Integer.MAX_VALUE;
		isResizable = true;
		headerValue = null;
	}

	private void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		if (changeSupport != null) {
			changeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	private void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		if (oldValue != newValue) {
			firePropertyChange(propertyName, new Integer(oldValue),
					new Integer(newValue));
		}
	}

	private void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		if (oldValue != newValue) {
			firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean
					.valueOf(newValue));
		}
	}

	public void setModelIndex(int modelIndex) {
		int old = this.modelIndex;
		this.modelIndex = modelIndex;
		firePropertyChange("modelIndex", old, modelIndex); //$NON-NLS-1$
	}

	public int getModelIndex() {
		return modelIndex;
	}

	public void setIdentifier(Object identifier) {
		Object old = this.identifier;
		this.identifier = identifier;
		firePropertyChange("identifier", old, identifier); //$NON-NLS-1$
	}

	public Object getIdentifier() {
		return (identifier != null) ? identifier : getHeaderValue();

	}

	public void setHeaderValue(Object headerValue) {
		Object old = this.headerValue;
		this.headerValue = headerValue;
		firePropertyChange("headerValue", old, headerValue); //$NON-NLS-1$
	}

	public Object getHeaderValue() {
		return headerValue;
	}

	public void setHeaderRenderer(TimelineRessourceRenderer headerRenderer) {
		TimelineRessourceRenderer old = this.headerRenderer;
		this.headerRenderer = headerRenderer;
		firePropertyChange("headerRenderer", old, headerRenderer); //$NON-NLS-1$
	}

	public TimelineRessourceRenderer getHeaderRenderer() {
		return headerRenderer;
	}

	public void setHeight(int height) {
		int old = this.height;
		this.height = Math.min(Math.max(height, minHeight), maxHeight);
		firePropertyChange("height", old, this.height); //$NON-NLS-1$
	}

	public int getHeight() {
		return height;
	}

	public void setPreferredHeight(int preferredHeight) {
		int old = this.preferredHeight;
		this.preferredHeight = Math.min(Math.max(preferredHeight, minHeight),
				maxHeight);
		firePropertyChange("preferredHeight", old, this.preferredHeight); //$NON-NLS-1$
	}

	public int getPreferredHeight() {
		return preferredHeight;
	}

	public void setMinHeight(int minHeight) {
		int old = this.minHeight;
		this.minHeight = Math.max(Math.min(minHeight, maxHeight), 0);
		if (height < this.minHeight) {
			setHeight(this.minHeight);
		}
		if (preferredHeight < this.minHeight) {
			setPreferredHeight(this.minHeight);
		}
		firePropertyChange("minHeight", old, this.minHeight); //$NON-NLS-1$
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMaxHeight(int maxHeight) {
		int old = this.maxHeight;
		this.maxHeight = Math.max(minHeight, maxHeight);
		if (height > this.maxHeight) {
			setHeight(this.maxHeight);
		}
		if (preferredHeight > this.maxHeight) {
			setPreferredHeight(this.maxHeight);
		}
		firePropertyChange("maxHeight", old, this.maxHeight); //$NON-NLS-1$
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setResizable(boolean isResizable) {
		boolean old = this.isResizable;
		this.isResizable = isResizable;
		firePropertyChange("isResizable", old, this.isResizable); //$NON-NLS-1$
	}

	public boolean getResizable() {
		return isResizable;
	}

	public void sizeWidthToFit() {
		if (headerRenderer == null) {
			return;
		}
		Component c = headerRenderer.getTimelineRessourceRendererComponent(
				null, this, 0,false,false);

		setMinHeight(c.getMinimumSize().width);
		setMaxHeight(c.getMaximumSize().width);
		setPreferredHeight(c.getPreferredSize().width);

		setHeight(getPreferredHeight());
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if (changeSupport == null) {
			changeSupport = new SwingPropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if (changeSupport != null) {
			changeSupport.removePropertyChangeListener(listener);
		}
	}

	public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
		if (changeSupport == null) {
			return new PropertyChangeListener[0];
		}
		return changeSupport.getPropertyChangeListeners();
	}

	protected TimelineRessourceRenderer createDefaultHeaderRenderer() {
		DefaultTimelineRessourceRenderer label = new DefaultTimelineRessourceRenderer() {

			private static final long serialVersionUID = 7576421734696160721L;

			public Component getTimelineRessourceRendererComponent(
					JTimeline timeline, TimelineRessource ressource, int index,boolean isSelected,boolean hasFocus) {
				if (timeline != null) {
					JTimelineRessourceHeader header = timeline
							.getTimelineRessourceHeader();
					if (header != null) {
						setForeground(header.getForeground());
						setBackground(header.getBackground());
						setFont(header.getFont());
					}
				}

				setText((ressource.getHeaderValue() == null) ? "" : ressource //$NON-NLS-1$
						.getHeaderValue().toString());
				setBorder(UIManager.getBorder("TableHeader.cellBorder")); //$NON-NLS-1$
				return this;
			}
		};
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}
}
