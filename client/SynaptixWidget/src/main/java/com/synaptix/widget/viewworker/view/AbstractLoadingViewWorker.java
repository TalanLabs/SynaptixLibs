package com.synaptix.widget.viewworker.view;

import com.synaptix.client.view.AbstractViewWorker;
import com.synaptix.widget.util.StaticWidgetHelper;

public abstract class AbstractLoadingViewWorker<E> extends AbstractViewWorker<E, String> {

	public final E doBackground() throws Exception {
		publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().loading());

		E res = doLoading();

		publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().done());
		return res;
	}

	protected abstract E doLoading() throws Exception;

}
