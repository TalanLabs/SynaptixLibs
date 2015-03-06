package com.synaptix.widget.filefilter.view;

import com.synaptix.widget.util.StaticWidgetHelper;

public class PdfFileFilter extends AbstractFileFilter {

	public static final String PDF_EXTENSION = ".pdf";

	public PdfFileFilter() {
		super(PDF_EXTENSION);
	}

	public String getDescription() {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().pdfFile();
	}
}
