package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.PickedObject;

import com.synaptix.swingx.mapviewer.layers.AirspacesLayer;

public abstract class AbstractShapeAirspace extends AbstractAirspace {

	private AirspaceAttributes airspaceAttributes;

	private AirspaceAttributes highlightAirspaceAttributes;

	public AbstractShapeAirspace() {
		super();

		this.airspaceAttributes = new DefaultAirspaceAttributes();
		this.highlightAirspaceAttributes = null;
	}

	public AirspaceAttributes getAirspaceAttributes() {
		return airspaceAttributes;
	}

	public void setAirspaceAttributes(AirspaceAttributes airspaceAttributes) {
		this.airspaceAttributes = airspaceAttributes;
	}

	public AirspaceAttributes getHighlightAirspaceAttributes() {
		return highlightAirspaceAttributes;
	}

	public void setHighlightAirspaceAttributes(
			AirspaceAttributes highlightAirspaceAttributes) {
		this.highlightAirspaceAttributes = highlightAirspaceAttributes;
	}

	@Override
	public void pick(DrawContext dc, Point point, AirspacesLayer airspacesLayer) {
		pick(dc, point, airspacesLayer, createShape(dc));
	}

	@Override
	public void paint(Graphics2D g, DrawContext dc,
			AirspacesLayer airspacesLayer) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(-dc.getViewportBounds().getX(), -dc.getViewportBounds()
				.getY());

		Shape shape = createShape(dc);

		AirspaceAttributes attr;
		if (isHighlighted() && getHighlightAirspaceAttributes() != null) {
			attr = getHighlightAirspaceAttributes();
		} else {
			attr = getAirspaceAttributes();
		}
		if (attr != null) {
			if (attr.isDrawInterior()) {
				Graphics2D g22 = (Graphics2D) g2.create();
				attr.applyInterior(g22);
				g22.fill(shape);
				g22.dispose();
			}

			if (attr.isDrawOutline()) {
				Graphics2D g22 = (Graphics2D) g2.create();
				attr.applyOutline(g22);
				g22.draw(shape);
				g22.dispose();
			}
		}

		pick(dc, dc.getPickPoint(), airspacesLayer, shape);

		if (isSelected()) {
			Shape rect = getSelectedShape(dc, shape);
			g2.setColor(selectedColor);
			g2.fill(rect);
			g2.setColor(borderSelectedColor);
			g2.draw(rect);
		}

		g2.dispose();
	}

	protected void pick(DrawContext dc, Point point,
			AirspacesLayer airspacesLayer, Shape shape) {
		if (dc.isPickingMode()) {
			if (shape.contains(point.x + dc.getViewportBounds().x,
					point.y + dc.getViewportBounds().y)) {
				PickedObject pickedObject = new PickedObject(dc.getPickPoint(),
						this);
				pickedObject.setLayer(airspacesLayer);
				dc.addPickedObject(pickedObject);
			}
		}
	}

	protected Shape getSelectedShape(DrawContext dc, Shape shape) {
		return shape.getBounds2D();
	}

	public abstract Shape createShape(DrawContext dc);

}
