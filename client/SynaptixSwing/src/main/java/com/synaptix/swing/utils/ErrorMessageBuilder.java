package com.synaptix.swing.utils;

public interface ErrorMessageBuilder {

	public abstract boolean isShowErrorMessage(Throwable throwable);

	public abstract String buildShortMessage(Throwable throwable);

	public abstract String buildLongMessage(Throwable throwable);

}
