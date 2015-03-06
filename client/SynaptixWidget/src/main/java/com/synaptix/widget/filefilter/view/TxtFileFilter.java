package com.synaptix.widget.filefilter.view;

import com.synaptix.widget.util.StaticWidgetHelper;

public class TxtFileFilter extends AbstractFileFilter {

	public static final String TXT_EXTENSION = ".txt";

	public TxtFileFilter() {
		super(TXT_EXTENSION);
	}

	public String getDescription() {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().txtFile();
	}
}
