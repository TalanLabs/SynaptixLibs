package com.synaptix.swingx.mapviewer.layers;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.SelectEvent;
import org.jdesktop.swingx.mapviewer.SelectListener;

public class HighlightSelectListener implements SelectListener {

	private JXMapViewer mapViewer;

	private Highlightable lastHighlightObject;

	public HighlightSelectListener(JXMapViewer mapViewer) {
		super();

		this.mapViewer = mapViewer;
	}

	public void selected(SelectEvent event) {
		if (event.isRollover()) {
			highlight(event.getTopObject());
		}
	}

	protected void highlight(Object o) {
		if (this.lastHighlightObject == o) {
			return;
		}

		boolean repaint = false;

		if (this.lastHighlightObject != null) {
			this.lastHighlightObject.setHighlighted(false);
			this.lastHighlightObject = null;
			repaint = true;
		}

		if (o instanceof Highlightable) {
			Highlightable h = (Highlightable) o;
			if (h.isHighlightable()) {
				this.lastHighlightObject = h;
				this.lastHighlightObject.setHighlighted(true);
				repaint = true;
			}
		}

		if (repaint) {
			mapViewer.repaint();
		}
	}

}
