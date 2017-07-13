package com.synaptix.swingx.mapviewer.layers;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.*;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;

import com.synaptix.swingx.mapviewer.layers.airspace.Airspace;

public class AirspacesLayer extends AbstractLayer {

	private List<Airspace> airspaces;

	public AirspacesLayer() {
		super();

		this.setAntialiasing(true);
		this.setCacheable(false);

		this.setPickEnabled(true);

		this.airspaces = new ArrayList<Airspace>();
	}

	public void addAirspace(Airspace airspace) {
		this.airspaces.add(airspace);
	}

	public void removeAirspace(Airspace airspace) {
		this.airspaces.remove(airspace);
	}

	public void removeAllAirspaces() {
		this.airspaces.clear();
	}

	public class AirspaceComparator implements Comparator<Airspace>{
		public int compare(Airspace airspace1, Airspace airspace2) {
			if(airspace1 instanceof Highlightable && airspace2 instanceof Highlightable){
				if(!((Highlightable)(airspace1)).isHighlighted() && ((Highlightable)(airspace2)).isHighlighted()){
					return -1;
				} else if(((Highlightable)(airspace1)).isHighlighted() && !((Highlightable)(airspace2)).isHighlighted()){
					return 1;
				}
			}
			return 0;
		}
	};

	@Override
	protected void doPick(DrawContext dc, Point point) {
		List<Airspace> sortedAirspaces  = new ArrayList<Airspace>();
		sortedAirspaces.addAll(airspaces);
		Collections.sort(sortedAirspaces, new AirspaceComparator());
		for (Airspace airspace : sortedAirspaces) {
			if (airspace.isVisible()) {
				airspace.pick(dc, point, this);
			}
		}
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		List<Airspace> sortedAirspaces  = new ArrayList<Airspace>();
		sortedAirspaces.addAll(airspaces);
		Collections.sort(sortedAirspaces, new AirspaceComparator());
		for (Airspace airspace : sortedAirspaces) {
			if (airspace.isVisible()) {
				airspace.paint(g, dc, this);
			}
		}
	}

}
