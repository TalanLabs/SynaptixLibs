package org.jdesktop.swingx.mapviewer.layers;

import java.awt.Graphics2D;
import java.awt.Point;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.painter.AbstractPainter;

public abstract class AbstractLayer extends AbstractPainter<JXMapViewer> implements Layer {

	private DrawContext dc;

	private boolean pickEnabled;

	private Double minAcceptZoom = null;

	private Double maxAcceptZoom = null;

	public AbstractLayer() {
		super();

		this.pickEnabled = true;
	}

	public void setMinAcceptZoom(Double minAcceptZoom) {
		this.minAcceptZoom = minAcceptZoom;
	}

	public Double getMinAcceptZoom() {
		return minAcceptZoom;
	}

	public void setMaxAcceptZoom(Double maxAcceptZoom) {
		this.maxAcceptZoom = maxAcceptZoom;
	}

	public Double getMaxAcceptZoom() {
		return maxAcceptZoom;
	}

	public boolean isPickEnabled() {
		return pickEnabled;
	}

	public void setPickEnabled(boolean pickEnabled) {
		this.pickEnabled = pickEnabled;
	}

	@Override
	public void pick(DrawContext dc, Point point) {
		if (!pickEnabled) {
			return;
		}
		if (!isAcceptZoom(dc)) {
			return;
		}
		doPick(dc, point);
		return;
	}

	protected abstract void doPick(DrawContext dc, Point point);

	@Override
	public void paint(Graphics2D g, DrawContext dc) {
		if (!isAcceptZoom(dc)) {
			return;
		}
		this.dc = dc;
		paint(g, null, dc.getDrawableWidth(), dc.getDrawableHeight());
	}

	@Override
	protected void doPaint(Graphics2D g, JXMapViewer mapViewer, int width, int height) {
		doPaint(g, dc);
	}

	protected abstract void doPaint(Graphics2D g, DrawContext dc);

	protected boolean isAcceptZoom(DrawContext dc) {
		return (minAcceptZoom == null || minAcceptZoom <= dc.getRealZoom()) && (maxAcceptZoom == null || maxAcceptZoom >= dc.getRealZoom());
	}
}
