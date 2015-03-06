package com.synaptix.widget.error.view.swing;

import org.jdesktop.swingx.error.ErrorInfo;

public interface IErrorInfoBuilder {

	public boolean isActiveReport();

	public boolean acceptThrowable(Throwable t);

	public ErrorInfo buildErrorInfo(Throwable t);

}
