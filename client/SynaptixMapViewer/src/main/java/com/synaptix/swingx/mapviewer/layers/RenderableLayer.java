package com.synaptix.swingx.mapviewer.layers;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Collections;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.PickedObject;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;

import com.synaptix.swingx.mapviewer.layers.render.Renderable;

public class RenderableLayer extends AbstractLayer {

	private java.util.Collection<Renderable> renderables = new java.util.concurrent.ConcurrentLinkedQueue<Renderable>();

	public RenderableLayer() {
		super();

		this.setAntialiasing(true);
		this.setCacheable(false);
		this.setInterpolation(Interpolation.Bicubic);
	}

	public void addRenderable(Renderable renderable) {
		this.renderables.add(renderable);
	}

	public void addRenderables(Iterable<? extends Renderable> renderables) {
		for (Renderable renderable : renderables) {
			this.renderables.add(renderable);
		}
	}

	public void removeRenderable(Renderable renderable) {
		this.renderables.remove(renderable);
	}

	public void removeAllRenderables() {
		this.clearRenderables();
	}

	protected void clearRenderables() {

		this.renderables.clear();
	}

	public int getNumRenderables() {
		return this.renderables.size();
	}

	public Iterable<Renderable> getRenderables() {
		return Collections.unmodifiableCollection(this.renderables);
	}

	@Override
	protected void doPick(DrawContext dc, Point point) {
		for (Renderable renderable : renderables) {
			if (renderable.isPick(dc, point)) {
				PickedObject pickedObject = new PickedObject(point, renderable);
				pickedObject.setLayer(this);
				dc.addPickedObject(pickedObject);
			}
		}
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		for (Renderable renderable : renderables) {
			renderable.render(g, dc);

			if (isPickEnabled() && dc.isPickingMode()
					&& renderable.isPick(dc, dc.getPickPoint())) {
				PickedObject pickedObject = new PickedObject(dc.getPickPoint(),
						renderable);
				pickedObject.setLayer(this);
				dc.addPickedObject(pickedObject);
			}
		}
	}
}
