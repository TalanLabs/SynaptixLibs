package com.synaptix.widget.filefilter.view;

import com.synaptix.widget.util.StaticWidgetHelper;

public class ZipFileFilter extends AbstractFileFilter {

	public static final String ZIP_EXTENSION = ".zip";

	public ZipFileFilter() {
		super(ZIP_EXTENSION);
	}

	@Override
	public String getDescription() {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().zipFile();
	}
}
