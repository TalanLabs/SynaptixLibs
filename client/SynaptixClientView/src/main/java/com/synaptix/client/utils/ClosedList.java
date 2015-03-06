package com.synaptix.client.utils;

import java.util.ArrayList;

public class ClosedList extends ArrayList<IClosed> implements IClosed {

	private static final long serialVersionUID = 6509431632252101080L;

	public void closed() {
		for (IClosed o : this) {
			o.closed();
		}
	}
}
