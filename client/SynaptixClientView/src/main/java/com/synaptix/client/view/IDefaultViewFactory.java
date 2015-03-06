package com.synaptix.client.view;

public interface IDefaultViewFactory {

	/**
	 * Start a thread for compute.
	 * 
	 * Show a message glass pane, block all window UI
	 * 
	 * @param viewWorker
	 * @return
	 */
	public abstract <E> IWaitWorker waitGlassPaneViewWorker(IViewWorker<E, String> viewWorker);

	/**
	 * Start a thread for compute.
	 * 
	 * Show a little message, no block
	 * 
	 * @param view
	 * @param viewWorker
	 * @return
	 */
	public abstract <E> IWaitWorker waitComponentViewWorker(IView view, IViewWorker<E, String> viewWorker);

	/**
	 * Start a thread for compute.
	 * 
	 * Show a full component, block only view UI
	 * 
	 * @param view
	 * @param viewWorker
	 * @return
	 */
	public abstract <E> IWaitWorker waitFullComponentViewWorker(IView view, IViewWorker<E, String> viewWorker);

	/**
	 * Start a thread for compute.
	 * 
	 * No block UI
	 * 
	 * @param viewWorker
	 * @return
	 */
	public abstract <E> IWaitWorker waitSilentViewWorker(IViewWorker<E, String> viewWorker);

}
