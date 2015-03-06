package com.synaptix.widget.error.view.swing.builder;

import com.synaptix.widget.error.view.swing.AbstractErrorInfoBuilder;

public abstract class AbstractThrowableClassErrorInfoBuilder extends AbstractErrorInfoBuilder {

	private final Class<? extends Throwable> throwableClass;

	private final String message;

	public AbstractThrowableClassErrorInfoBuilder(Class<? extends Throwable> throwableClass) {
		this(throwableClass, null);
	}

	public AbstractThrowableClassErrorInfoBuilder(Class<? extends Throwable> throwableClass, String message) {
		super();

		this.throwableClass = throwableClass;
		this.message = message;
	}

	@Override
	public boolean acceptThrowable(Throwable t) {
		boolean res = throwableClass.equals(t.getClass());
		if (res && message != null) {
			res = message.equals(t.getMessage());
		}
		return res;
	}
}
