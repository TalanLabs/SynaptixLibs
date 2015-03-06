package com.synaptix.view.swing;

import com.synaptix.client.view.IViewWorker;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.swing.WaitGlassPaneSwingWorker;

public class WaitGlassPaneSwingViewWorker<E> extends WaitGlassPaneSwingWorker<E> implements IViewWorker.PublisherListener<String> {

	private IViewWorker<E, String> viewWorker;

	public WaitGlassPaneSwingViewWorker(boolean showGlassPane, IViewWorker<E, String> viewWorker) {
		super(showGlassPane);
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

	public static final <E> IWaitWorker waitGlassPaneSwingViewWorker(boolean showGlassPane, IViewWorker<E, String> viewWorker) {
		return new WaitGlassPaneSwingViewWorker<E>(showGlassPane, viewWorker).execute();
	}
}