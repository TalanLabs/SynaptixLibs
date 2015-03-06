package com.synaptix.widget.error.view.swing;

import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.synaptix.widget.util.StaticWidgetHelper;

public class DefaultErrorInfoBuilder extends AbstractErrorInfoBuilder {

	@Override
	public boolean isActiveReport() {
		return true;
	}

	@Override
	public boolean acceptThrowable(Throwable t) {
		return true;
	}

	@Override
	public ErrorInfo buildErrorInfo(Throwable t) {
		StringBuilder sb = new StringBuilder();
		sb.append(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().basicErrorMessage()).append("\n");
		sb.append(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().message()).append(" : ").append(t.getLocalizedMessage());
		return new ErrorInfo(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().basicErrorMessage(), null, null, t,
				ErrorLevel.SEVERE, null);
	}
}
