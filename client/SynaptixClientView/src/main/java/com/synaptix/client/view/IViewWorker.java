package com.synaptix.client.view;

public interface IViewWorker<E, F> {

	public abstract E doBackground() throws Exception;

	public abstract void success(E e);

	public abstract void fail(Throwable t);

	public abstract void addPublisherListener(PublisherListener<F> l);

	public abstract void removePublisherListener(PublisherListener<F> l);

	public interface PublisherListener<F> {

		public void publishMessage(F chunks);

	}
}
