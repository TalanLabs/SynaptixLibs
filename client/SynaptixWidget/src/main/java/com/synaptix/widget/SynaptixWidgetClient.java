package com.synaptix.widget;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.view.swing.error.ErrorViewManager;
import com.synaptix.widget.error.view.swing.ErrorInfoErrorViewManager;
import com.synaptix.widget.error.view.swing.IErrorInfoBuilder;

public class SynaptixWidgetClient {

	private static final Log LOG = LogFactory.getLog(SynaptixWidgetClient.class);

	@Inject
	private ErrorInfoErrorViewManager errorInfoErrorViewManager;

	@Inject(optional = true)
	private Set<IErrorInfoBuilder> errorInfoBuilders;

	public SynaptixWidgetClient() {
		super();
	}

	public void start() {
		LOG.info("Start SynaptixWidgetClient");

		ErrorViewManager.setInstance(errorInfoErrorViewManager);

		if (errorInfoBuilders != null && !errorInfoBuilders.isEmpty()) {
			for (IErrorInfoBuilder builder : errorInfoBuilders) {
				errorInfoErrorViewManager.addErrorInfoBuilder(builder);
			}
		}
	}

	public void stop() {
		LOG.info("Stop SynaptixWidgetClient");
	}
}
