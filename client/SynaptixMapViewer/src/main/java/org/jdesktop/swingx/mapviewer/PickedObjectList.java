package org.jdesktop.swingx.mapviewer;

import java.util.ArrayList;

public class PickedObjectList extends ArrayList<PickedObject> {

	private static final long serialVersionUID = 2483060475462310156L;

	public PickedObjectList() {
		super();
	}

	public PickedObjectList(PickedObjectList pickedObjectList) {
		super(pickedObjectList);
	}

	public PickedObject getTopPickedObject() {
		return this.isEmpty() ? null : this.get(this.size() - 1);
	}

	public Object getTopObject() {
		PickedObject pickedObject = getTopPickedObject();
		return pickedObject != null ? pickedObject.getObject() : null;
	}

}
