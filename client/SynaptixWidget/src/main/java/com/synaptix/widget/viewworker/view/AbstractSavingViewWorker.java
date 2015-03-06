package com.synaptix.widget.viewworker.view;

import com.synaptix.client.view.AbstractViewWorker;
import com.synaptix.widget.util.StaticWidgetHelper;

public abstract class AbstractSavingViewWorker<E> extends AbstractViewWorker<E, String> {

	public final E doBackground() throws Exception {
		publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().saving());

		E res = doSaving();

		publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().done());
		return res;
	}

	protected abstract E doSaving() throws Exception;

}
