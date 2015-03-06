package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DefaultAirspaceAttributes implements AirspaceAttributes {

	private Color color = new Color(64, 255, 64, 128);

	private Color outlineColor = new Color(64, 64, 64, 192);

	private BasicStroke outlineStroke = new BasicStroke(5);

	private boolean drawInterior;

	private boolean drawOutline;

	public DefaultAirspaceAttributes() {
		super();

		this.drawInterior = true;
		this.drawOutline = true;
	}

	public boolean isDrawInterior() {
		return drawInterior;
	}

	public void setDrawInterior(boolean drawInterior) {
		this.drawInterior = drawInterior;
	}

	public boolean isDrawOutline() {
		return drawOutline;
	}

	public void setDrawOutline(boolean drawOutline) {
		this.drawOutline = drawOutline;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public Color getOutlineColor() {
		return outlineColor;
	}

	@Override
	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	@Override
	public void applyInterior(Graphics2D g) {
		g.setPaint(color);
	}

	@Override
	public void applyOutline(Graphics2D g) {
		g.setPaint(outlineColor);
		g.setStroke(outlineStroke);
	}

	@Override
	public BasicStroke getOutlineStroke() {
		return outlineStroke;
	}

	@Override
	public void setOutlineStroke(BasicStroke outlineStroke) {
		this.outlineStroke = outlineStroke;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static final class Builder {

		private DefaultAirspaceAttributes att;

		public Builder() {
			super();
			att = new DefaultAirspaceAttributes();
		}

		public Builder drawInterior(boolean drawInterior) {
			att.setDrawInterior(drawInterior);
			return this;
		}

		public Builder drawOutline(boolean drawOutline) {
			att.setDrawOutline(drawOutline);
			return this;
		}

		public Builder color(Color color) {
			att.setColor(color);
			return this;
		}

		public Builder outlineColor(Color outlineColor) {
			att.setOutlineColor(outlineColor);
			return this;
		}

		public Builder outlineStroke(BasicStroke outlineStroke) {
			att.setOutlineStroke(outlineStroke);
			return this;
		}

		public DefaultAirspaceAttributes build() {
			return att;
		}
	}
}
