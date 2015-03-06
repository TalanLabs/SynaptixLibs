package com.synaptix.client.utils;

import java.util.ArrayList;


public class OpenedList extends ArrayList<IOpened> implements IOpened {

	private static final long serialVersionUID = 6509431632252101080L;

	public void opened() {
		for (IOpened o : this) {
			o.opened();
		}
	}
}
