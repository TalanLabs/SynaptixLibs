package org.jdesktop.swingx.mapviewer;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class DragSelectEvent extends SelectEvent {

	private static final long serialVersionUID = -8252169102322802341L;

	private final Point previousPickPoint;

	public DragSelectEvent(Object source, String eventAction,
			MouseEvent mouseEvent, PickedObjectList pickedObjects,
			Point previousPickPoint) {
		super(source, eventAction, mouseEvent, pickedObjects);
		this.previousPickPoint = previousPickPoint;
	}

	public Point getPreviousPickPoint() {
		return this.previousPickPoint;
	}
}
