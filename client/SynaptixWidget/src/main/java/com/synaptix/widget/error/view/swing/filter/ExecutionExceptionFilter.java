package com.synaptix.widget.error.view.swing.filter;

import java.util.concurrent.ExecutionException;

import com.synaptix.widget.error.view.swing.IThrowableFilter;

public class ExecutionExceptionFilter implements IThrowableFilter {

	@Override
	public Throwable filter(Throwable t) {
		if (t instanceof ExecutionException) {
			return t.getCause();
		}
		return t;
	}

}
