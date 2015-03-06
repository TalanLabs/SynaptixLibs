package com.synaptix.view.swing;

import com.synaptix.client.view.IViewWorker;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.WaitFullComponentSwingWorker;

public class WaitFullComponentSwingWaitWorker<E> extends WaitFullComponentSwingWorker<E> implements IViewWorker.PublisherListener<String> {

	private IViewWorker<E, String> viewWorker;

	public WaitFullComponentSwingWaitWorker(WaitComponentFeedbackPanel component, IViewWorker<E, String> viewWorker) {
		super(component);
		this.viewWorker = viewWorker;
	}

	protected E doInBackground() throws Exception {
		viewWorker.addPublisherListener(this);

		E res = null;
		try {
			res = viewWorker.doBackground();
		} finally {
			viewWorker.removePublisherListener(this);
		}

		return res;
	}

	protected void done() {
		try {
			E e = get();
			viewWorker.success(e);
		} catch (Exception e) {
			viewWorker.fail(e);
		}

	}

	public void publishMessage(String chunks) {
		publish(chunks);
	}

	public static final <E> IWaitWorker waitFullComponentSwingWaitWorker(WaitComponentFeedbackPanel component, IViewWorker<E, String> viewWorker) {
		return new WaitFullComponentSwingWaitWorker<E>(component, viewWorker).execute();
	}
}
