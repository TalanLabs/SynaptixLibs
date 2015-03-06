package com.synaptix.view.swing;

import java.util.List;

import javax.swing.SwingWorker;

import com.synaptix.client.view.IViewWorker;
import com.synaptix.client.view.IWaitWorker;

public class WaitSilentSwingViewWorker<E> implements IViewWorker.PublisherListener<String> {

	private IViewWorker<E, String> viewWorker;

	private MySwingWorker swingWorker;

	public WaitSilentSwingViewWorker(IViewWorker<E, String> viewWorker) {
		super();

		this.viewWorker = viewWorker;

		this.swingWorker = new MySwingWorker();
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

	@Override
	public void publishMessage(String chunks) {
		publish(chunks);
	}

	public final IWaitWorker execute() {
		swingWorker.execute();
		return swingWorker;
	}

	protected final E get() throws Exception {
		return swingWorker.get();
	}

	protected final void publish(String... chunks) {
		swingWorker.myPublish(chunks);
	}

	private final class MySwingWorker extends SwingWorker<E, String> implements IWaitWorker {

		public void myPublish(String... chunks) {
			publish(chunks);
		}

		@Override
		protected E doInBackground() throws Exception {
			return WaitSilentSwingViewWorker.this.doInBackground();
		}

		@Override
		protected void process(List<String> chunks) {
		}

		@Override
		protected void done() {
			WaitSilentSwingViewWorker.this.done();
		}
	}

	public static final <E> IWaitWorker waitSilentSwingViewWorker(IViewWorker<E, String> viewWorker) {
		return new WaitSilentSwingViewWorker<E>(viewWorker).execute();
	}
}