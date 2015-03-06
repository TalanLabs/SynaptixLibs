package org.jdesktop.swingx.mapviewer;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class SelectEvent extends EventObject {

	private static final long serialVersionUID = -1307086862351109236L;

	public static final String LEFT_CLICK = "org.jdesktop.swingx.mapviewer.SelectEvent.LeftClick";

	/**
	 * The user double-clicked the left mouse button while the cursor was over
	 * picked object.
	 */
	public static final String LEFT_DOUBLE_CLICK = "org.jdesktop.swingx.mapviewer.SelectEvent.LeftDoubleClick";
	/**
	 * The user clicked the right mouse button while the cursor was over picked
	 * object.
	 */
	public static final String RIGHT_CLICK = "org.jdesktop.swingx.mapviewer.SelectEvent.RightClick";
	/**
	 * The user pressed the left mouse button while the cursor was over picked
	 * object.
	 */
	public static final String LEFT_PRESS = "org.jdesktop.swingx.mapviewer.SelectEvent.LeftPress";
	/**
	 * The user pressed the right mouse button while the cursor was over picked
	 * object.
	 */
	public static final String RIGHT_PRESS = "org.jdesktop.swingx.mapviewer.SelectEvent.RightPress";
	/**
	 * The cursor has moved over the picked object and become stationary, or has
	 * moved off the object of the most recent <code>HOVER</code> event. In the
	 * latter case, the picked object will be null.
	 */
	public static final String HOVER = "org.jdesktop.swingx.mapviewer.SelectEvent.Hover";
	/**
	 * The cursor has moved over the object or has moved off the object most
	 * recently rolled over. In the latter case the picked object will be null.
	 */
	public static final String ROLLOVER = "org.jdesktop.swingx.mapviewer.SelectEvent.Rollover";
	/** The user is attempting to drag the picked object. */
	public static final String DRAG = "org.jdesktop.swingx.mapviewer.SelectEvent.Drag";
	/** The user has stopped dragging the picked object. */
	public static final String DRAG_END = "org.jdesktop.swingx.mapviewer.SelectEvent.DragEnd";

	private final MouseEvent mouseEvent;

	private final PickedObjectList pickedObjects;

	private boolean consumed = false;

	private final Point pickPoint;

	private final String eventAction;

	public SelectEvent(Object source, String eventAction, Point pickPoint,
			PickedObjectList pickedObjectList) {
		super(source);

		this.eventAction = eventAction;
		this.mouseEvent = null;
		this.pickPoint = pickPoint;
		this.pickedObjects = pickedObjectList;
	}

	public SelectEvent(Object source, String eventAction,
			MouseEvent mouseEvent, PickedObjectList pickedObjectList) {
		super(source);

		this.eventAction = eventAction;
		this.mouseEvent = mouseEvent;
		this.pickPoint = mouseEvent != null ? mouseEvent.getPoint() : null;
		this.pickedObjects = pickedObjectList;
	}

	public String getEventAction() {
		return this.eventAction != null ? this.eventAction
				: "org.jdesktop.swingx.mapviewer.SelectEvent.UnknownEventAction";
	}

	public MouseEvent getMouseEvent() {
		return mouseEvent;
	}

	public Point getPickPoint() {
		return pickPoint;
	}

	public PickedObjectList getPickedObjectList() {
		return pickedObjects;
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void consume() {
		this.consumed = true;

		if (this.getMouseEvent() != null) {
			this.getMouseEvent().consume();
		}
	}

	public boolean hasObjects() {
		return this.pickedObjects != null && this.pickedObjects.size() > 0;
	}

	public PickedObject getTopPickedObject() {
		return this.hasObjects() ? this.pickedObjects.getTopPickedObject()
				: null;
	}

	public Object getTopObject() {
		PickedObject tpo = this.getTopPickedObject();
		return tpo != null ? tpo.getObject() : null;
	}

	public boolean isRollover() {
		return this.getEventAction() == ROLLOVER;
	}

	public boolean isHover() {
		return this.getEventAction() == HOVER;
	}

	public boolean isDragEnd() {
		return this.getEventAction() == DRAG_END;
	}

	public boolean isDrag() {
		return this.getEventAction() == DRAG;
	}

	public boolean isRightPress() {
		return this.getEventAction() == RIGHT_PRESS;
	}

	public boolean isRightClick() {
		return this.getEventAction() == RIGHT_CLICK;
	}

	public boolean isLeftDoubleClick() {
		return this.getEventAction() == LEFT_DOUBLE_CLICK;
	}

	public boolean isLeftClick() {
		return this.getEventAction() == LEFT_CLICK;
	}

	public boolean isLeftPress() {
		return this.getEventAction() == LEFT_PRESS;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getName() + " "
				+ (this.eventAction != null ? this.eventAction : null));
		if (this.pickedObjects != null
				&& this.pickedObjects.getTopObject() != null)
			sb.append(", ").append(
					this.pickedObjects.getTopObject().getClass().getName());

		return sb.toString();
	}
}
