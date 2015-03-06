package com.synaptix.widget.joda.view.swing;

import java.util.Comparator;

import org.joda.time.base.BaseLocal;

public class LocalComparator implements Comparator<BaseLocal> {

	@Override
	public int compare(BaseLocal o1, BaseLocal o2) {
		return o1.compareTo(o2);
	}
}
